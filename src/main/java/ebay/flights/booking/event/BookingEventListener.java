package ebay.flights.booking.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookingEventListener {

    @EventListener
    public void onBookingConfirmed(BookingConfirmedEvent event) {
        log.info("Booking confirmed: id={}, flightId={}, passenger={}",
                event.booking().getId(),
                event.booking().getFlightId(),
                event.booking().getPassengerEmail());
    }

    @EventListener
    public void onBookingCancelled(BookingCancelledEvent event) {
        log.info("Booking cancelled: id={}, flightId={}, passenger={}",
                event.booking().getId(),
                event.booking().getFlightId(),
                event.booking().getPassengerEmail());
    }
}
