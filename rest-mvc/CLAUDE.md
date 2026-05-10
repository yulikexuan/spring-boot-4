//: rest-mvc/CLAUDE.md

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

> Shared standards: [root CLAUDE.md](../CLAUDE.md), [java-guidelines](../.junie/java-guidelines.md), [spring-boot-guidelines](../.junie/spring-boot-guidelines.md).

## Overview

Spring MVC REST API on Spring Boot 4 / Spring Data JDBC / PostgreSQL. 

Three independent subdomains share an identical CRUD pattern:

| Sub-domain   | Base path                  | ID type   | Notes                    |
|--------------|----------------------------|-----------|--------------------------|
| Beer         | `/sfg7/api/v1/beer`        | UUID      | `BeerStyle` enum         |
| Customer     | `/sfg7/api/v1/customer`    | UUID      |                          |
| Flashcard    | `/sfg7/api/v1/flashcard`   | Long      | OCP exam-prep; BIGSERIAL |

Layered: **Controller → Service (interface + impl) → Repository**. 

The service interface and its package-private `*Impl` live in the **same `.java` file** (e.g. `BeerService.java` declares both `BeerService` and `BeerServiceImpl`).

## Build & Run

```bash
# Unit tests only (no DB required)
mvn test -pl rest-mvc

# Verify (includes IT suite, but RestMvcApplicationIT is @Disabled today)
mvn verify -pl rest-mvc

# Single test class
mvn test -pl rest-mvc -Dtest=BeerControllerTest

# Run the app (needs PostgreSQL on localhost:54316; DATASOURCE_PASSWORD env var)
mvn spring-boot:run -pl rest-mvc
```

## API & Domain Conventions

- `POST` → **201 Created** with `Location` header built by `WebUtils.buildLocation(path, idSupplier)`.
- `PUT` / `PATCH` / `DELETE` → **204 No Content**.
- `NotFoundException` is annotated `@ResponseStatus(HttpStatus.NOT_FOUND)` — no `@ControllerAdvice` is wired (the `ExceptionHandler` class is intentionally commented-out scaffolding). It accepts only `UUID` constructors and is used by Beer/Customer; **`FlashcardServiceImpl` uses `.orElseThrow()`** (raw `NoSuchElementException`) — extend `NotFoundException` to support `Long` if you need consistent 404s for Flashcard.
- Every domain record exposes:
  - `updateWith(ID id, T other)` — full replacement; preserves `createdDate` / refreshes `updateDate` (used by PUT).
  - `patchWith(ID id, T other)` — partial; existing field is kept when the incoming field is null/blank (used by PATCH).
- `Customer` and `Flashcard` records are annotated `@GenerateLenses` (hkj optics processor); `Beer` is not. Lenses are generated under `target/generated-sources/`.

## Test Conventions

- **Controller slice** — `@WebMvcTest(XController.class)` + `@MockitoBean` for the service. Inject `MockMvc` via `@Autowired`. Import `WebMvcTest` from `org.springframework.boot.webmvc.test.autoconfigure` (Spring Boot 4 path), not the legacy autoconfigure package.
- **Service unit** — `@ExtendWith(MockitoExtension.class)` + `@Mock` repository + `@InjectMocks` impl; `BDDMockito.given(…)` / `then(mock).should(…)`.
- **JSON** — Spring Boot 4 ships **Jackson 3**: import `tools.jackson.databind.ObjectMapper` (not `com.fasterxml.jackson…`). Build the `ObjectMapper` in `@BeforeEach` for unit-style tests.
- Use `ArgumentCaptor` to assert what the service receives, not just the HTTP status.
- `@DisplayName` + `@DisplayNameGeneration(ReplaceUnderscores.class)` on every test class.

## Database

Two SQL files in `src/main/resources/`:

- `schema.sql` — **DROP + CREATE** of all three tables. Auto-loaded on every start via `spring.sql.init.mode: always`. This is what makes `BootstrapData` non-idempotent inserts safe — every restart begins on empty tables.
- `schema_renew.sql` — manual `TRUNCATE … RESTART IDENTITY CASCADE` + `VACUUM FULL`. Not auto-loaded; run by hand against a live DB to reset without restarting.

| Table         | PK type     | Key columns                                                                                                                      |
|---------------|-------------|----------------------------------------------------------------------------------------------------------------------------------|
| `beer`        | UUID        | `version`, `beer_name`, `beer_style` (VARCHAR 50), `upc`, `quantity_on_hand`, `price` (int cents), `created_date`, `update_date` |
| `customer`    | UUID        | `version`, `name`, `created_date`, `update_date`                                                                                 |
| `flashcard`   | BIGSERIAL   | `question` (VARCHAR 500), `answer` (VARCHAR 1000), `weight`                                                                      |

- UUIDs default to `gen_random_uuid()` — no application-side ID generation.
- Tables are independent (no FKs); each is its own aggregate root.
- `version` columns on `beer` / `customer` are reserved for optimistic locking (not enforced yet).

## Spring Data JDBC Persistence

- Repositories extend `ListCrudRepository<Entity, ID>` (prefer over `CrudRepository` for `List` returns).
- Domain models are records: `@Table("…")`, `@Column("snake_case")` on each component, `@Id` on the PK. `UUID` for app-assigned IDs, `Long` for DB auto-increment.
- No nested one-to-many — every record is its own aggregate root.
- Avoid custom `@Query` unless `ListCrudRepository` is insufficient.
- `BootstrapData` (`CommandLineRunner`) inserts 3 rows per table on every start. **It is not idempotent** — correctness depends on `schema.sql` running DROP+CREATE first. Don't add a row-count guard without also disabling `schema.sql` execution.

<!-- SPECKIT START -->
For additional context about technologies to be used, project structure,
shell commands, and other important information, read the current plan
<!-- SPECKIT END -->
