package ebay.flights;

import ebay.flights.flight.model.Flight;
import ebay.flights.flight.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    // Fixed IDs — stable across restarts; used in README curl examples and tests
    public static final String AA100_ID = "00000000-0000-0000-0000-000000000001";
    public static final String UA200_ID = "00000000-0000-0000-0000-000000000002";

    private final FlightRepository flightRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void seedSampleFlights() {
        LocalDateTime base = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);

        flightRepository.save(Flight.builder()
                .id(AA100_ID)
                .flightNumber("AA100")
                .origin("New York")
                .destination("Los Angeles")
                .departureTime(base.withHour(8))
                .arrivalTime(base.withHour(14))
                .totalSeats(150)
                .availableSeats(new AtomicInteger(150))
                .build());

        flightRepository.save(Flight.builder()
                .id(UA200_ID)
                .flightNumber("UA200")
                .origin("Chicago")
                .destination("Miami")
                .departureTime(base.withHour(10))
                .arrivalTime(base.withHour(14))
                .totalSeats(2)
                .availableSeats(new AtomicInteger(2))
                .build());

        log.info("Sample flights seeded: AA100 id={}, UA200 id={}", AA100_ID, UA200_ID);
    }
}
