package ebay.flights.booking.repository;

import ebay.flights.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(String id);
    List<Booking> findAll();
    List<Booking> findByFlightId(String flightId);
}
