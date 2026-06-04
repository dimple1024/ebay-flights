package ebay.flights.flight;

import java.util.List;
import java.util.Optional;

public interface FlightRepository {
    Flight save(Flight flight);
    Optional<Flight> findById(String id);
    List<Flight> findAll();
    boolean existsById(String id);
}
