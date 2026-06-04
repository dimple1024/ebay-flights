package ebay.flights;

import ebay.flights.flight.FlightService;
import ebay.flights.flight.dto.CreateFlightRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final FlightService flightService;

    @EventListener(ApplicationReadyEvent.class)
    public void seedSampleFlights() {
        LocalDateTime base = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);

        flightService.createFlight(new CreateFlightRequest(
                "AA100", "New York", "Los Angeles",
                base.withHour(8), base.withHour(14), 150));

        flightService.createFlight(new CreateFlightRequest(
                "UA200", "Chicago", "Miami",
                base.withHour(10), base.withHour(14), 2));

        flightService.createFlight(new CreateFlightRequest(
                "DL300", "Boston", "San Francisco",
                base.withHour(12), base.withHour(18), 180));

        log.info("Sample flights seeded.");
    }
}
