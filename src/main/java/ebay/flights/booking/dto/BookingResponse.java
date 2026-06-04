package ebay.flights.booking.dto;

import ebay.flights.booking.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponse(
        String id,
        String flightId,
        String passengerName,
        String passengerEmail,
        BookingStatus status,
        LocalDateTime bookedAt
) {}
