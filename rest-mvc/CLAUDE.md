//: rest-mvc/CLAUDE.md

# rest-mvc Module

> For shared standards see [root CLAUDE.md](../CLAUDE.md), [java-guidelines](../.junie/java-guidelines.md), and [spring-boot-guidelines](../.junie/spring-boot-guidelines.md).

## Overview

A Spring MVC REST API backed by Spring Data JDBC (PostgreSQL). Three sub-domains share an identical CRUD pattern:

| Sub-domain | Base path              | ID type | Notes                     |
|------------|------------------------|---------|---------------------------|
| Beer       | `/sfg7/api/v1/beer`     | UUID    | `BeerStyle` enum          |
| Customer   | `/sfg7/api/v1/customer` | UUID    |                           |
| Flashcard  | `/sfg7/api/v1/flashcard`| Long    | OCP exam-prep; auto-incr  |

Layered: **Controller → Service (interface + impl) → Repository**.

## Build & Run

```bash
# Unit tests only (no DB)
mvn test -pl rest-mvc

# Full verify including integration tests (requires PostgreSQL on localhost:54316)
mvn verify -pl rest-mvc

# Single test class
mvn test -pl rest-mvc -Dtest=BeerControllerTest
```

## API & Domain Conventions

- All `POST` responses return **201 Created** with a `Location` header built by `WebUtils.buildLocation()`.
- `PUT` / `PATCH` / `DELETE` return **204 No Content**.
- `NotFoundException` carries `@ResponseStatus(HttpStatus.NOT_FOUND)` — no `@ControllerAdvice` needed.
- Every domain record provides:
  - `updateWith(T other)` — full replacement (used by PUT handler)
  - `patchWith(T other)` — partial update (null fields are preserved; used by PATCH handler)

## Test Conventions

- **Controller slice tests** use `@WebMvcTest(XController.class)` + `@MockitoBean` for the service.
- Inject `MockMvc` via `@Autowired`; never spin up the full context for controller tests.
- Use `ArgumentCaptor` to assert what the service receives, not just the HTTP response code.
- **Service unit tests** use `@ExtendWith(MockitoExtension.class)` + `@Mock` repository + `@InjectMocks` impl.
- Use `BDDMockito.given(…)` for stubbing and `then(mock).should(…)` for verification.
- The `ObjectMapper` for JSON serialization should be set up in `@BeforeEach` (or injected in slice tests).

## Database

Schema: [`src/main/resources/schema_renew.sql`](src/main/resources/schema_renew.sql) — recreated on every start (DROP + CREATE).

| Table       | PK type    | Key columns                                                                 |
|-------------|------------|-----------------------------------------------------------------------------|
| `beer`      | UUID       | `beer_name`, `beer_style` (VARCHAR 50), `upc`, `quantity_on_hand`, `price` (integer cents), `created_date`, `update_date` |
| `customer`  | UUID       | `name`, `created_date`, `update_date`                                       |
| `flashcard` | BIGSERIAL  | `question` (VARCHAR 500), `answer` (VARCHAR 1000), `weight`                |

- All UUIDs default to `gen_random_uuid()` — no application-side ID generation.
- Tables are **independent** — no foreign keys between them; each is its own aggregate root.
- `version` column on `beer` and `customer` is reserved for optimistic locking.

## Spring Data JDBC Persistence

- All repositories extend `ListCrudRepository<Entity, ID>` — prefer this over `CrudRepository` when you need `List` return types.
- Domain models are **records** annotated with `@Table("table_name")`.
- Map snake_case columns explicitly with `@Column("column_name")` on the record component.
- Mark the primary key with `@Id`; use `UUID` for application-assigned IDs, `Long` for DB auto-increment.
- Each record is its own **aggregate root** — no nested one-to-many collections are used here.
- Avoid custom `@Query` unless the built-in `ListCrudRepository` methods are insufficient.
- Bootstrap data (3 rows per table) is loaded by `BootstrapData` (`CommandLineRunner`) on every start; keep it idempotent or guard with a row-count check.
