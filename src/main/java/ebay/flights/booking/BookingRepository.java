package ebay.flights.booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(String id);
    List<Booking> findAll();
    List<Booking> findByFlightId(String flightId);
}
