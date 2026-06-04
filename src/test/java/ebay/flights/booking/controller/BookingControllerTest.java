package ebay.flights.booking.controller;

import tools.jackson.databind.ObjectMapper;
import ebay.flights.booking.dto.BookingRequest;
import ebay.flights.booking.dto.BookingResponse;
import ebay.flights.booking.model.BookingStatus;
import ebay.flights.booking.service.BookingService;
import ebay.flights.common.exception.OverbookingException;
import ebay.flights.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    private static final String FLIGHT_ID = "00000000-0000-0000-0000-000000000001";
    private static final String URL = "/api/v1/bookings";

    @Test
    void createBooking_validRequest_returns201WithLocationHeader() throws Exception {
        BookingRequest request = new BookingRequest(FLIGHT_ID, "Jane Doe", "jane@example.com");
        BookingResponse response = new BookingResponse(
                "booking-abc", FLIGHT_ID, "Jane Doe", "jane@example.com",
                BookingStatus.CONFIRMED, LocalDateTime.now());

        when(bookingService.createBooking(any())).thenReturn(response);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/bookings/booking-abc"))
                .andExpect(jsonPath("$.id").value("booking-abc"))
                .andExpect(jsonPath("$.flightId").value(FLIGHT_ID))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void createBooking_blankFlightId_returns400WithValidationError() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flightId":"","passengerName":"Jane","passengerEmail":"jane@example.com"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void createBooking_invalidEmail_returns400WithValidationError() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flightId":"some-id","passengerName":"Jane","passengerEmail":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void createBooking_missingBody_returns400() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_unknownFlight_returns404() throws Exception {
        BookingRequest request = new BookingRequest("unknown-id", "Jane", "jane@example.com");
        when(bookingService.createBooking(any()))
                .thenThrow(new ResourceNotFoundException("Flight", "unknown-id"));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Flight not found with id: unknown-id"));
    }

    @Test
    void createBooking_flightFull_returns409() throws Exception {
        BookingRequest request = new BookingRequest(FLIGHT_ID, "Jane", "jane@example.com");
        when(bookingService.createBooking(any()))
                .thenThrow(new OverbookingException(FLIGHT_ID));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("OVERBOOKING"));
    }
}
