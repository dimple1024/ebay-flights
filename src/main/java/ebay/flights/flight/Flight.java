package ebay.flights.flight;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
    private int availableSeats;
    private FlightStatus status;

    /**
     * Atomically reserves a seat. Returns false if no seats remain or flight is not bookable.
     * synchronized on this instance provides per-flight locking — concurrent bookings
     * on different flights don't block each other.
     */
    public synchronized boolean tryReserveSeat() {
        if (availableSeats <= 0 || !isBookable()) {
            return false;
        }
        availableSeats--;
        return true;
    }

    public synchronized void releaseSeat() {
        availableSeats = Math.min(availableSeats + 1, totalSeats);
    }

    public boolean isBookable() {
        return status == FlightStatus.SCHEDULED || status == FlightStatus.BOARDING;
    }

    public synchronized void cancel() {
        this.status = FlightStatus.CANCELLED;
    }
}
