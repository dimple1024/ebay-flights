package ebay.flights.booking.mapper;

import ebay.flights.booking.dto.BookingRequest;
import ebay.flights.booking.dto.BookingResponse;
import ebay.flights.booking.model.Booking;
import ebay.flights.booking.model.BookingStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getFlightId(),
                booking.getPassengerName(),
                booking.getPassengerEmail(),
                booking.getStatus(),
                booking.getBookedAt()
        );
    }

    public Booking toEntity(BookingRequest request) {
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
