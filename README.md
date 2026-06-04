# Flight Booking API

A Spring Boot REST API for booking flights, with CAS-based overbooking prevention and in-memory storage.

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 17+ |
| Maven | 3.9+ (or use the included `mvnw` wrapper) |

---

## Build & Run

```bash
# Build
./mvnw clean package -q

# Run
./mvnw spring-boot:run
```

The server starts on **http://localhost:8080**.

On startup, two sample flights are seeded automatically with **fixed IDs** (same every run):

| Flight | Route | Seats | ID |
|--------|-------|-------|----|
| AA100 | New York → Los Angeles | 150 | `00000000-0000-0000-0000-000000000001` |
| UA200 | Chicago → Miami | **2** | `00000000-0000-0000-0000-000000000002` |

> UA200 has only 2 seats — useful for testing overbooking rejection.

---

## API

### `POST /api/v1/bookings` — Create a booking

**Request body**

```json
{
  "flightId": "<flight-id>",
  "passengerName": "Jane Doe",
  "passengerEmail": "jane@example.com"
}
```

**Responses**

| Status | Meaning |
|--------|---------|
| `201 Created` | Booking confirmed; `Location` header points to the booking |
| `400 Bad Request` | Validation failure (blank fields, invalid email) |
| `404 Not Found` | Flight ID does not exist |
| `409 Conflict` | No seats remaining on the flight |

---

## Example curl commands

### Book a seat on AA100 (150 seats) (seed sampled flight info into flight repository)

```bash
curl -s -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "flightId": "00000000-0000-0000-0000-000000000001",
    "passengerName": "Jane Doe",
    "passengerEmail": "jane@example.com"
  }' | jq
```

Expected response (`201 Created`):

```json
{
  "id": "...",
  "flightId": "00000000-0000-0000-0000-000000000001",
  "passengerName": "Jane Doe",
  "passengerEmail": "jane@example.com",
  "status": "CONFIRMED",
  "bookedAt": "2026-06-05T10:00:00"
}
```

---

### Trigger overbooking on UA200 (2 seats only) (seed sampled flight info into flight repository)

Run this three times in quick succession — the third request will be rejected:

```bash
for i in 1 2 3; do
  curl -s -o /dev/null -w "Request $i → HTTP %{http_code}\n" \
    -X POST http://localhost:8080/api/v1/bookings \
    -H "Content-Type: application/json" \
    -d "{\"flightId\":\"00000000-0000-0000-0000-000000000002\",\"passengerName\":\"Passenger $i\",\"passengerEmail\":\"p$i@example.com\"}"
done
```

Expected output:
```
Request 1 → HTTP 201
Request 2 → HTTP 201
Request 3 → HTTP 409
```

---

### Validation error example

```bash
curl -s -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{"flightId":"","passengerName":"","passengerEmail":"bad-email"}' | jq
```

Expected response (`400 Bad Request`):

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Validation failed",
  "timestamp": "...",
  "path": "/api/v1/bookings",
  "fieldErrors": [
    "flightId: Flight ID is required",
    "passengerEmail: Invalid email format",
    "passengerName: Passenger name is required"
  ]
}
```

---

## Running tests

```bash
# All tests (unit + concurrency)
./mvnw test

# Just the controller unit tests
./mvnw test -Dtest=BookingControllerTest

# Just the concurrency test
./mvnw test -Dtest=BookingConcurrencyTest
```

### What the concurrency test does

`BookingConcurrencyTest` fires **10 simultaneous threads** at UA200 (2 seats) using a `CyclicBarrier` to ensure all threads start at exactly the same instant. It asserts:

- Exactly **2** requests receive `201 Created`
- Exactly **8** requests receive `409 Conflict`

This proves the CAS loop in `Flight.tryReserveSeat()` prevents overbooking without locks.

---

## Architecture

```
ebay.flights/
├── DataInitializer          # Seeds sample flights on startup
├── flight/
│   ├── model/Flight         # Rich domain model — owns seat reservation via CAS
│   └── repository/          # FlightRepository interface + InMemory impl
└── booking/
    ├── controller/          # POST /api/v1/bookings
    ├── service/             # BookingService interface + impl
    ├── repository/          # BookingRepository interface + InMemory impl
    ├── mapper/              # BookingRequest ↔ Booking ↔ BookingResponse
    ├── model/               # Booking, BookingStatus
    └── dto/                 # BookingRequest, BookingResponse
```

### Overbooking prevention

`Flight.tryReserveSeat()` uses a **Compare-And-Swap (CAS) loop** on an `AtomicInteger`:

```
read current seats
  → if 0, return false (overbooking rejected)
  → compareAndSet(current, current - 1)
      → success: seat reserved
      → failure: another thread changed it, retry from read
```

No thread is ever blocked. Contention causes a brief spin that resolves in O(n) retries for n concurrent threads, each making progress.


### Things that I would if had more time:
- Logically I think the code works all fine, allows you to book flight (only the ones which is seeded from DataInitialiser)
- I would have done more things like:
  - Javadocs
  - Review pom.xml for unncessary / redundant dependencies
  - Reviewed Datainialiser for appropriate way of seeding data into flightRepository
  - Reviewed tests/ added more to gain more confidence into solution
  - More aggressive review on the overall code review, keeping only what's needed.