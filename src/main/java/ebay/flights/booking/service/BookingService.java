package ebay.flights.booking.service;

import ebay.flights.booking.dto.BookingResponse;
import ebay.flights.booking.dto.BookingRequest;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
}
