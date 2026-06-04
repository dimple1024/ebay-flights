package ebay.flights.flight.repository;

import ebay.flights.flight.model.Flight;
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
}
