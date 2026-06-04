package ebay.flights.common.exception;

public class OverbookingException extends RuntimeException {

    public OverbookingException(String flightId) {
        super(String.format("No seats available on flight: %s", flightId));
    }
}
