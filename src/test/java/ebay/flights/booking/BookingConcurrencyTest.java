package ebay.flights.booking;

import ebay.flights.DataInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class BookingConcurrencyTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * UA200 has exactly 2 seats. 10 threads all start simultaneously via CyclicBarrier.
     * CAS in Flight.tryReserveSeat() must allow exactly 2 through and reject the rest.
     */
    @Test
    void overbookingIsPrevented_underConcurrentLoad() throws Exception {
        int totalRequests = 10;
        int availableSeats = 2;

        ExecutorService executor = Executors.newFixedThreadPool(totalRequests);
        // CyclicBarrier forces all threads to reach the barrier before any proceeds,
        // maximising the chance of a real race condition on the seat counter
        CyclicBarrier barrier = new CyclicBarrier(totalRequests);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < totalRequests; i++) {
            final int idx = i;
            futures.add(executor.submit(() -> {
                barrier.await();
                String body = """
                        {"flightId":"%s","passengerName":"Passenger %d","passengerEmail":"p%d@test.com"}
                        """.formatted(DataInitializer.UA200_ID, idx, idx);
                MvcResult result = mockMvc.perform(post("/api/v1/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                        .andReturn();
                return result.getResponse().getStatus();
            }));
        }

        executor.shutdown();
        executor.awaitTermination(15, TimeUnit.SECONDS);

        AtomicInteger created = new AtomicInteger();
        AtomicInteger conflict = new AtomicInteger();
        for (Future<Integer> future : futures) {
            int status = future.get();
            if (status == 201) created.incrementAndGet();
            else if (status == 409) conflict.incrementAndGet();
        }

        assertThat(created.get())
                .as("Exactly %d bookings should succeed — one per available seat", availableSeats)
                .isEqualTo(availableSeats);
        assertThat(conflict.get())
                .as("Remaining %d requests should be rejected with 409 Conflict", totalRequests - availableSeats)
                .isEqualTo(totalRequests - availableSeats);
    }
}
