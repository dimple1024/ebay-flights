package ebay.flights.flight;

import ebay.flights.common.exception.BusinessException;
import ebay.flights.common.exception.ResourceNotFoundException;
import ebay.flights.flight.dto.CreateFlightRequest;
import ebay.flights.flight.dto.FlightResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    @Override
    public FlightResponse createFlight(CreateFlightRequest request) {
        if (!request.departureTime().isBefore(request.arrivalTime())) {
            throw new BusinessException("INVALID_SCHEDULE", "Departure time must be before arrival time");
        }
        Flight flight = flightMapper.toEntity(request);
        Flight saved = flightRepository.save(flight);
        log.info("Flight created: id={}, flightNumber={}", saved.getId(), saved.getFlightNumber());
        return flightMapper.toResponse(saved);
    }

    @Override
    public FlightResponse getFlightById(String id) {
        return flightRepository.findById(id)
                .map(flightMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", id));
    }

    @Override
    public List<FlightResponse> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(flightMapper::toResponse)
                .toList();
    }

    @Override
    public FlightResponse cancelFlight(String id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", id));
        flight.cancel();
        flightRepository.save(flight);
        log.info("Flight cancelled: id={}", id);
        return flightMapper.toResponse(flight);
    }
}
