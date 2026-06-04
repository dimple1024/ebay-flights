package ebay.flights.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateBookingRequest(
        @NotBlank(message = "Flight ID is required")
        String flightId,

        @NotBlank(message = "Passenger name is required")
        String passengerName,

        @NotBlank(message = "Passenger email is required")
        @Email(message = "Invalid email format")
        String passengerEmail
) {}
