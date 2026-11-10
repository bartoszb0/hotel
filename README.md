# Hotel Reservation API

REST API for a small hotel: managing room types and rooms, searching for available rooms and booking stays.
Built with Spring Boot as a portfolio project, in stages – this repository currently contains **stage 1 (core booking)**.

The interesting part is not the CRUD, but the booking rules: no double bookings, prices frozen at booking time,
date rules that are testable regardless of when tests run, and a search that combines optional filters with an
availability check in a single SQL query.

## Tech stack

| Area | Technology |
|---|---|
| Language / framework | Java 21+, Spring Boot 4 (Web MVC, Validation, Data JPA) |
| Database | PostgreSQL 17, schema managed only by Flyway migrations |
| Mapping | MapStruct |
| Dynamic queries | Spring Data JPA Specifications (Criteria API) |
| API docs | springdoc-openapi (Swagger UI) |
| Tests | JUnit 5, Mockito, AssertJ, Testcontainers (real PostgreSQL) |
| Local environment | Docker Compose (started automatically by Spring Boot) |

## Running locally

Requirements: JDK 21+ and Docker running.

```bash
./mvnw spring-boot:run
```

Spring Boot's Docker Compose support starts PostgreSQL from `compose.yaml`, Flyway creates the schema, and the API is available at:

- API: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Endpoints

All paths are prefixed with `/api/v1`.

### Room types
| Method | Path | Description | Success |
|---|---|---|---|
| GET | `/room-types` | List room types | 200 |
| GET | `/room-types/{id}` | Get a room type | 200 |
| POST | `/room-types` | Create a room type (name, capacity, price per night) | 201 |
| PATCH | `/room-types/{id}` | Partially update a room type | 200 |
| DELETE | `/room-types/{id}` | Delete a room type (409 if rooms still use it) | 204 |

### Rooms
| Method | Path | Description | Success |
|---|---|---|---|
| GET | `/rooms` | List all rooms | 200 |
| GET | `/rooms/{id}` | Get a room | 200 |
| POST | `/rooms` | Create a room (number, room type) | 201 |
| PATCH | `/rooms/{id}` | Partially update a room | 200 |
| DELETE | `/rooms/{id}` | Delete a room | 204 |

### Availability search
| Method | Path | Description | Success |
|---|---|---|---|
| GET | `/rooms/available` | Search rooms; all filters optional: `roomTypeId`, `minCapacity`, `maxPrice`, `from` + `to` (ISO dates, both or none) | 200 |

### Reservations
| Method | Path | Description | Success |
|---|---|---|---|
| POST | `/reservations` | Book a room for a guest (guest identified by e-mail) | 201 |
| GET | `/reservations/{id}` | Get a reservation | 200 |
| GET | `/reservations?email=` | List a guest's reservations | 200 |
| POST | `/reservations/{id}/cancel` | Cancel a reservation | 200 |

## Business rules

1. Check-out must be after check-in, check-in cannot be in the past, a stay is at most 30 nights.
2. A room cannot have two **confirmed** reservations with overlapping dates. The check-out day of one stay may be the
   check-in day of the next one.
3. The number of guests cannot exceed the room type's capacity.
4. The total price (nights × price per night) is calculated and **stored at booking time**. Later price changes do
   not affect existing reservations.
5. A guest is identified by e-mail (trimmed, case-insensitive). The same e-mail reuses the existing guest; the
   existing guest's name is not overwritten.
6. A reservation can be cancelled only before the check-in day and only once. Cancelling changes the status instead
   of deleting the row, and frees the room.

## Error handling

Errors are returned as RFC 9457 `application/problem+json` (`ProblemDetail`).

| Situation | Status |
|---|---|
| Invalid request body, malformed JSON, invalid UUID/date, missing required parameter | 400 (validation errors include an `errors` map per field) |
| Business rule broken by the request itself (dates, capacity, cancelling too late) | 400 |
| Resource not found | 404 |
| Conflict with the current state (room already booked, duplicate room number / type name, deleting a type in use) | 409 |

400 vs 409: a 400 request can never succeed as sent; a 409 request is valid but conflicts with current data and
could succeed later (e.g. after another reservation is cancelled).

## Architecture

Packages are organised **by feature**, not by layer:

```
com.bd.hotelapp
├── common        shared exceptions, exception handler, configuration (Clock), API paths
├── roomtype      room types
├── room          rooms
├── guest         guests (no public API – created through reservations)
├── reservation   booking, cancelling, overlap check
└── availability  room search with filters
```

Dependencies only go one way, there are no cycles:

```
availability ──► room ──► roomtype
     │
     └────────► reservation ──► room
                     └────────► guest
```

Each feature talks to other features through their **services**, never through their repositories.

Inside a feature: `controller` → `service` → `repository`, with separate request DTOs, service commands, response
DTOs and JPA entities.

## Key design decisions

- **Overlap check in the database.** A booking conflicts when
  `new.checkOut > existing.checkIn AND new.checkIn < existing.checkOut`. The check is a JPQL `exists` query, so the
  database answers true/false instead of loading a room's whole reservation history into the application.
- **Search with Specifications.** Each filter is a separate `Specification<Room>`; the service adds only the filters
  that were sent and combines them with `AND`. Availability is a `NOT EXISTS` subquery with the same overlap
  condition as above. Everything runs as one SQL query.
- **Separate `availability` feature.** Availability needs both rooms and reservations, and reservations already
  depend on rooms. Putting the search inside `room` would create a dependency cycle, so the search lives in its own
  package and has its own endpoint (`/rooms/available`). It also separates the public, guest-facing search from room
  management, which becomes staff-only once authentication is added.
- **Injected `Clock`.** "Today" always comes from a `Clock` bean (`Europe/Warsaw`), never from `LocalDate.now()`.
  Tests use a fixed clock, so date rules behave the same no matter when the tests run.
- **Validation in three layers.** Request DTOs check single fields (required, format, length, ranges); services
  check rules that need context (other fields, current date, database state); the database enforces `NOT NULL`,
  `CHECK`, `UNIQUE` and foreign keys as the last line of defence.
- **Client sends only what a guest would fill in.** IDs, prices, statuses and timestamps are set by the server.
  Changing a reservation's status is only possible through the `cancel` action, not a generic update.
- **Lazy relations + `open-in-view=false`.** All relations are `LAZY`; queries that need related data load it
  explicitly with `@EntityGraph`. Disabling open-in-view makes missing fetches fail loudly instead of silently
  causing N+1 queries.
- **Schema only via Flyway.** Hibernate runs with `ddl-auto=validate`; migrations are the single source of truth.
- **Unknown JSON fields are ignored** (Spring Boot default), so the API is tolerant to extra fields from clients.

## Tests

```bash
./mvnw test
```

Docker must be running – integration tests start a real PostgreSQL container with Testcontainers.

- **Unit tests** (Mockito, fixed `Clock`) for every service with business logic, including boundary cases
  (check-in today, exactly 30 nights, guests equal to capacity).
- **Integration tests** against real PostgreSQL and real Flyway migrations for the overlap query and the search
  specifications (before / after / inside / covering, back-to-back stays, other rooms, cancelled reservations,
  combined filters).
- **Postman collection** in `postman/` with end-to-end smoke tests of the booking flow:
  `npx newman run postman/hotel-reservations.postman_collection.json` (with the application running).

## Known limitations (stage 1)

- **No authentication.** Anyone who knows a reservation ID or a guest's e-mail can read those reservations. There is
  deliberately no "list all reservations" endpoint until staff accounts exist.
- **Concurrent bookings.** Two requests booking the same room at the same moment could both pass the overlap check.
  The planned fix is a PostgreSQL exclusion constraint on `(room_id, daterange(check_in, check_out))` for confirmed
  reservations, so the database itself rejects the second booking.

## Roadmap

1. ✅ Core booking API
2. Guest and staff accounts (Spring Security, JWT, roles, "my reservations")
3. Automatic e-mails (confirmation, cancellation) sent after commit, asynchronously
4. Payments (Stripe test mode, webhooks, unpaid reservations expiring)
5. Deployment (Docker image, GitHub Actions CI, AWS)
6. Frontend in TypeScript with types generated from the OpenAPI spec
