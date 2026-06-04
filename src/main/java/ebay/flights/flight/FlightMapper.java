package ebay.flights.flight;

import ebay.flights.flight.dto.CreateFlightRequest;
import ebay.flights.flight.dto.FlightResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class FlightMapper {

    FlightResponse toResponse(Flight flight) {
        return new FlightResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getTotalSeats(),
                flight.getAvailableSeats(),
                flight.getStatus()
        );
    }

    Flight toEntity(CreateFlightRequest request) {
        return Flight.builder()
                .id(UUID.randomUUID().toString())
                .flightNumber(request.flightNumber())
                .origin(request.origin())
                .destination(request.destination())
                .departureTime(request.departureTime())
                .arrivalTime(request.arrivalTime())
                .totalSeats(request.totalSeats())
                .availableSeats(request.totalSeats())
                .status(FlightStatus.SCHEDULED)
                .build();
    }
}
