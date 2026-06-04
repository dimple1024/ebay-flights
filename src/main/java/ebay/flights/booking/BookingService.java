package ebay.flights.booking;

import ebay.flights.booking.dto.BookingResponse;
import ebay.flights.booking.dto.CreateBookingRequest;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(CreateBookingRequest request);
    BookingResponse getBookingById(String id);
    List<BookingResponse> getAllBookings();
    List<BookingResponse> getBookingsByFlightId(String flightId);
    void cancelBooking(String id);
}
