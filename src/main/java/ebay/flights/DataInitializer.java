package ebay.flights;

import ebay.flights.flight.Flight;
import ebay.flights.flight.FlightRepository;
import ebay.flights.flight.FlightStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final FlightRepository flightRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void seedSampleFlights() {
        LocalDateTime base = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);

        flightRepository.save(Flight.builder()
                .id(UUID.randomUUID().toString())
                .flightNumber("AA100")
                .origin("New York")
                .destination("Los Angeles")
                .departureTime(base.withHour(8))
                .arrivalTime(base.withHour(14))
                .totalSeats(150)
                .availableSeats(new AtomicInteger(150))
                .status(FlightStatus.SCHEDULED)
                .build());

        flightRepository.save(Flight.builder()
                .id(UUID.randomUUID().toString())
                .flightNumber("UA200")
                .origin("Chicago")
                .destination("Miami")
                .departureTime(base.withHour(10))
                .arrivalTime(base.withHour(14))
                .totalSeats(2)
                .availableSeats(new AtomicInteger(2))
                .status(FlightStatus.SCHEDULED)
                .build());

        log.info("Sample flights seeded.");
    }
}
