package ebay.flights.flight;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
class InMemoryFlightRepository implements FlightRepository {

    private final Map<String, Flight> store = new ConcurrentHashMap<>();

    @Override
    public Flight save(Flight flight) {
        store.put(flight.getId(), flight);
        return flight;
    }

    @Override
    public Optional<Flight> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Flight> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public boolean existsById(String id) {
        return store.containsKey(id);
    }
}
