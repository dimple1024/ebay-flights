package ebay.flights.booking;

import ebay.flights.booking.dto.BookingResponse;
import ebay.flights.booking.dto.CreateBookingRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
class BookingMapper {

    BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getFlightId(),
                booking.getPassengerName(),
                booking.getPassengerEmail(),
                booking.getStatus(),
                booking.getBookedAt()
        );
    }

    Booking toEntity(CreateBookingRequest request) {
        return Booking.builder()
                .id(UUID.randomUUID().toString())
                .flightId(request.flightId())
                .passengerName(request.passengerName())
                .passengerEmail(request.passengerEmail())
                .status(BookingStatus.CONFIRMED)
                .bookedAt(LocalDateTime.now())
                .build();
    }
}
