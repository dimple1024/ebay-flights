package ebay.flights.common.exception;

public class FlightNotAvailableException extends RuntimeException {

    public FlightNotAvailableException(String flightId) {
        super(String.format("Flight %s is not available for booking", flightId));
    }
}
