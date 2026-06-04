package ebay.flights.flight;

import ebay.flights.flight.dto.CreateFlightRequest;
import ebay.flights.flight.dto.FlightResponse;

import java.util.List;

public interface FlightService {
    FlightResponse createFlight(CreateFlightRequest request);
    FlightResponse getFlightById(String id);
    List<FlightResponse> getAllFlights();
    FlightResponse cancelFlight(String id);
}
