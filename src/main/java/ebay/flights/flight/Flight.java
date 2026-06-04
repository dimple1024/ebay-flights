package ebay.flights.flight;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Builder
public class Flight {

    private final String id;
    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;
    private final int totalSeats;

    // AtomicInteger allows CAS-based seat management without blocking
    @Getter(AccessLevel.NONE)
    private final AtomicInteger availableSeats;

    private volatile FlightStatus status;

    public int getAvailableSeats() {
        return availableSeats.get();
    }

    /**
     * CAS loop: read current seats, verify > 0 and flight is bookable, then atomically
     * decrement. Retries on contention — no thread is ever blocked, starvation is
     * bounded because competing threads are also making progress.
     */
    public boolean tryReserveSeat() {
        int current;
        do {
            if (!isBookable()) return false;
            current = availableSeats.get();
            if (current <= 0) return false;
        } while (!availableSeats.compareAndSet(current, current - 1));
        return true;
    }

    public void releaseSeat() {
        int current;
        do {
            current = availableSeats.get();
            if (current >= totalSeats) return;
        } while (!availableSeats.compareAndSet(current, current + 1));
    }

    public boolean isBookable() {
        return status == FlightStatus.SCHEDULED || status == FlightStatus.BOARDING;
    }

    public void cancel() {
        // Zero seats first so in-flight CAS loops see no capacity before they see CANCELLED
        availableSeats.set(0);
        this.status = FlightStatus.CANCELLED;
    }
}
