package ebay.flights.booking.service;

import ebay.flights.booking.dto.BookingRequest;
import ebay.flights.booking.mapper.BookingMapper;
import ebay.flights.booking.repository.BookingRepository;
import ebay.flights.booking.dto.BookingResponse;
import ebay.flights.booking.model.Booking;
import ebay.flights.common.exception.FlightNotAvailableException;
import ebay.flights.common.exception.OverbookingException;
import ebay.flights.common.exception.ResourceNotFoundException;
import ebay.flights.flight.Flight;
import ebay.flights.flight.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        Flight flight = flightRepository.findById(request.flightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight", request.flightId()));

        if (!flight.isBookable()) {
            throw new FlightNotAvailableException(request.flightId());
        }

        // tryReserveSeat is synchronized on the flight instance — per-flight locking
        // prevents overbooking under concurrent requests without blocking other flights
        if (!flight.tryReserveSeat()) {
            throw new OverbookingException(request.flightId());
        }

        Booking booking = bookingMapper.toEntity(request);
        Booking saved = bookingRepository.save(booking);
        log.info("Booking confirmed: id={}, flightId={}, passenger={}",
                saved.getId(), saved.getFlightId(), saved.getPassengerEmail());
        return bookingMapper.toResponse(saved);
    }
}
