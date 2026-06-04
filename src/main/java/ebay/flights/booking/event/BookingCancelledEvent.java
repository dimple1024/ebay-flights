package ebay.flights.booking.event;

import ebay.flights.booking.Booking;

public record BookingCancelledEvent(Booking booking) {}
