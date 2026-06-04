package ebay.flights.flight.repository;

import ebay.flights.flight.model.Flight;

import java.util.List;
import java.util.Optional;

public interface FlightRepository {
    Flight save(Flight flight);
    Optional<Flight> findById(String id);
}
