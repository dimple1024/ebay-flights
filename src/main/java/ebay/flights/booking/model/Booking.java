package ebay.flights.booking.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Booking {

    private final String id;
    private final String flightId;
    private final String passengerName;
    private final String passengerEmail;
    private BookingStatus status;
    private final LocalDateTime bookedAt;

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }

    public boolean isCancelled() {
        return status == BookingStatus.CANCELLED;
    }
}
