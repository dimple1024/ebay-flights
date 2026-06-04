package ebay.flights.booking;

import ebay.flights.booking.dto.BookingResponse;
import ebay.flights.booking.dto.CreateBookingRequest;
import ebay.flights.booking.event.BookingCancelledEvent;
import ebay.flights.booking.event.BookingConfirmedEvent;
import ebay.flights.common.exception.FlightNotAvailableException;
import ebay.flights.common.exception.OverbookingException;
import ebay.flights.common.exception.ResourceNotFoundException;
import ebay.flights.flight.Flight;
import ebay.flights.flight.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final BookingMapper bookingMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public BookingResponse createBooking(CreateBookingRequest request) {
        Flight flight = flightRepository.findById(request.flightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight", request.flightId()));

        if (!flight.isBookable()) {
            throw new FlightNotAvailableException(request.flightId());
        }

        // tryReserveSeat is synchronized on the flight instance — provides per-flight locking
        // to prevent overbooking under concurrent requests without blocking unrelated flights
        if (!flight.tryReserveSeat()) {
            throw new OverbookingException(request.flightId());
        }

        Booking booking = bookingMapper.toEntity(request);
        Booking saved = bookingRepository.save(booking);
        eventPublisher.publishEvent(new BookingConfirmedEvent(saved));
        return bookingMapper.toResponse(saved);
    }

    @Override
    public BookingResponse getBookingById(String id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
    }

    @Override
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Override
    public List<BookingResponse> getBookingsByFlightId(String flightId) {
        if (!flightRepository.existsById(flightId)) {
            throw new ResourceNotFoundException("Flight", flightId);
        }
        return bookingRepository.findByFlightId(flightId).stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Override
    public void cancelBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));

        if (booking.isCancelled()) {
            return;
        }

        booking.cancel();
        bookingRepository.save(booking);

        flightRepository.findById(booking.getFlightId())
                .ifPresent(Flight::releaseSeat);

        eventPublisher.publishEvent(new BookingCancelledEvent(booking));
        log.info("Booking cancelled: id={}", id);
    }
}
