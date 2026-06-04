package ebay.flights.flight.dto;

import ebay.flights.flight.FlightStatus;

import java.time.LocalDateTime;

public record FlightResponse(
        String id,
        String flightNumber,
        String origin,
        String destination,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        int totalSeats,
        int availableSeats,
        FlightStatus status
) {}
