package ebay.flights.booking.repository;

import ebay.flights.booking.model.Booking;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
class InMemoryBookingRepository implements BookingRepository {

    private final Map<String, Booking> store = new ConcurrentHashMap<>();

    @Override
    public Booking save(Booking booking) {
        store.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Booking> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public List<Booking> findByFlightId(String flightId) {
        return store.values().stream()
                .filter(b -> b.getFlightId().equals(flightId))
                .toList();
    }
}
