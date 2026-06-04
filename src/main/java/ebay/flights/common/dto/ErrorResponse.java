package ebay.flights.common.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        Instant timestamp,
        String path,
        List<String> fieldErrors
) {
    public static ErrorResponse of(String code, String message, String path) {
        return new ErrorResponse(code, message, Instant.now(), path, List.of());
    }

    public static ErrorResponse ofValidation(String path, List<String> fieldErrors) {
        return new ErrorResponse("VALIDATION_ERROR", "Validation failed", Instant.now(), path, fieldErrors);
    }
}
