# Feature Specification - Fast Persistence-Slice Tests for Beer and Customer 

- **Feature Branch**: `003-persistence-slice-tests`
- **Created**: 2026-05-16
- **Status**: Draft
- **Input**: **User description:** 
  - Test only the persistence layer in isolation for domain `beer` and `customer`. 
  - Loading the whole spring context is not necessary; 
    - tests should run very fast; 
    - test data is throwable; 
    - PostgreSQL dialect must be respected; 
    - existing data schema must be respected; 
    - all repository methods referenced in the service layer of Beer and Customer must be covered 

## Clarifications

### Session 2026-05-16

- Q: Which engine should the persistence-slice tests 
  - Run against (`H2` in `PG-mode` vs. `embedded PostgreSQL` vs. `both`)? 

- → Answer: Embedded PostgreSQL via Zonky's Spring auto-config wrapper (`io.zonky.test:embedded-database-spring-test`, which manages an `embedded-postgres` instance behind `@AutoConfigureEmbeddedDatabase`)
  - Zonky's default ephemeral data directory is acceptable; the SC-002 speed target is met by Zonky's binary cache and per-class refresh, not by a tmpfs/RAM backing 
  - H2-in-PostgreSQL-mode is rejected because Spring Data JDBC would resolve `H2Dialect` instead of `PostgresDialect`, 
    - losing PG-native SQL fidelity 

## User Scenarios & Testing *(mandatory)*

---

### User Story 1 — Verify service-called repository methods work against a real PostgreSQL engine (Priority: P1)

A backend developer changes a domain record, a column mapping, or a query and wants instant feedback that `BeerRepository` and `CustomerRepository` still behave correctly for every operation the service layer invokes — without paying the cost of booting the whole application.

**Why this Priority**: 
- Without this safety net, regressions in the persistence layer surface only when the full integration suite or the running app exercises them. 
- Fast, focused tests catch column-mapping, type-conversion, and dialect issues at the earliest possible moment.

**Independent Test**: 
- A developer runs the new test class(es) from the command line or IDE. 
- The suite provisions its own throwaway PostgreSQL, applies the existing schema, exercises every service-called repository method on round-trip data, and reports green 
  - all without starting controllers, services, or the rest of the application context.

**Acceptance Scenarios**:

1. **Given** an empty database 
   - **When** a new Beer is persisted with all required fields 
   - **Then** the row is written, an `id` UUID is assigned by the database, and a subsequent read returns a record value-equal to what was sent (modulo the database-assigned `id`).
2. **Given** a previously persisted Beer 
   - **When** the test calls `findById` with its `id` 
   - **Then** the returned `Optional` is non-empty and contains every field unchanged.
3. **Given** a previously persisted Beer 
   - **When** the test calls `findById` with an unknown UUID 
   - **Then** an empty `Optional` is returned.
4. **Given** several persisted Beers, 
   - **When** `findAll` is called 
   - **Then** the returned list contains exactly the persisted rows.
5. **Given** a previously persisted Beer 
   - **When** the test saves a modified copy with the same `id` 
   - **Then** the row is updated in place (no duplicate rows; `findAll().size()` is unchanged).
6. **Given** a previously persisted Beer 
   - **When** `deleteById` is called with its `id` 
   - **Then** a subsequent `findById` returns empty and `findAll` no longer contains it.
7. The same six scenarios apply identically to Customer.

---

### User Story 2 — Schema-driven defaults and constraints are honored (Priority: P2)

The developer needs confidence that PostgreSQL-Side behavior baked into `schema_renew.sql` (UUID auto-generation, NOT-NULL absence, `VARCHAR` length caps, `TIMESTAMP` precision) survives any future change to mappings, dialects, or driver versions.

**Why this priority**: 
- These behaviors are not visible in Java code — they live only in SQL. 
- A passing unit test against a mocked repository would not catch their loss. 
- Tests must run against a real PostgreSQL engine to detect drift.

**Independent Test**: 
- A developer runs the test class and sees assertions confirming database-assigned defaults (e.g., `gen_random_uuid()` populates `id` when not supplied) and constraint behavior (e.g., over-length strings are rejected per column definitions).

**Acceptance Scenarios**:

1. **Given** a Beer or Customer saved without an explicit `id` 
   - **When** the row is read back 
   - **Then** the `id` is a valid UUID assigned by the database default.
2. **Given** a Beer saved with `beer_style` and `upc` strings 
   - **When** the row is read back 
   - **Then** the stored values match (within their declared `VARCHAR` caps) 
     - confirming no truncation surprise during a round-trip.
3. **Given** `created_date` and `update_date` written as `Instant` values 
   - **When** the row is read back 
   - **Then** the values round-trip correctly through the PostgreSQL `TIMESTAMP` columns.

---

### Edge Cases

- An update that targets a non-existent `id` does not silently insert a phantom row of the wrong shape and does not raise an unexpected runtime error type — the test pins the actual repository contract.
- `deleteById` against an unknown `id` completes without throwing (matches `ListCrudRepository` contract).
- An empty repository returns an empty list from `findAll` (not `null`).
- Concurrent invocations are explicitly out of scope (the `version` columns are reserved but not enforced today — no optimistic-locking assertions in this feature).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The test suite MUST exercise every repository method called by `BeerServiceImpl` and `CustomerServiceImpl` today, namely: `save`, `findAll`, `findById`, `deleteById` — on both repositories.
- **FR-002**: The test suite MUST run against a real PostgreSQL engine — actual PostgreSQL server binaries, started and managed by the test infrastructure itself — matching the production dialect, including `gen_random_uuid()` and `TIMESTAMP` semantics. 
  - Substitutes such as H2 or HSQLDB in "PostgreSQL-compatibility mode" are not acceptable because they cause Spring Data JDBC to resolve a non-PostgreSQL dialect at runtime, eroding the very fidelity these tests exist to guarantee.
- **FR-003**: The test suite MUST be self-provisioning: 
  - a developer running it on a clean checkout MUST NOT need to install, start, or configure any external database service beforehand.
- **FR-004**: The test suite MUST NOT load the full Spring application context. 
  - Only the persistence-layer collaborators required by the repositories may be initialized.
- **FR-005**: The test suite MUST use the existing schema definition (the same DDL that defines `beer` and `customer` in production) as the source of truth. 
  - Tests MUST NOT redefine columns, types, or constraints inline.
- **FR-006**: Each test MUST start from a known, predictable state and MUST NOT depend on the order of execution or on residue from any other test. 
  - Test data is treated as disposable.
- **FR-007**: The test suite MUST verify that the database-assigned `id` default (`gen_random_uuid()`) is applied when a row is persisted without an explicit `id`.
- **FR-008**: The test suite MUST verify that `Instant` ↔ `TIMESTAMP` round-tripping for `created_date` and `update_date` preserves the values written.
- **FR-009**: The test suite MUST be runnable independently of the existing integration-test (`*IT`) suite and MUST NOT require the `DATASOURCE_PASSWORD` environment variable or the local PostgreSQL instance on port `54316`.
- **FR-010**: Repository methods inherited from `ListCrudRepository` that are NOT invoked by either service today (e.g., `count`, `existsById`, `findAllById`, `saveAll`, `deleteAll`) are explicitly out of scope for this feature.

### Key Entities

- **Beer**: aggregate root persisted in the `beer` table. 
  - Identified by a UUID (database-assigned by default). 
  - Holds name, style, UPC, quantity, price, version, and creation/update timestamps.
- **Customer**: aggregate root persisted in the `customer` table. 
  - Identified by a UUID (database-assigned by default). 
  - Holds name, version, and creation/update timestamps.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: A developer can run the new persistence-slice test class for either domain on a freshly cloned repository — with only the project's standard toolchain installed — and see all tests pass, without performing any database installation, configuration, or environment-variable setup.
- **SC-002**: Wall-clock time for the full persistence-slice suite (Beer + Customer combined) is materially shorter than the time required to start a full Spring context-loaded test for the same module. 
  - The qualitative target is "feels instant on a developer's machine": the entire suite finishes well under the time a `@SpringBootTest` takes to print its first banner.
- **SC-003**: Every repository method invoked by `BeerServiceImpl` and `CustomerServiceImpl` is exercised by tests, per the matrix below. 
  - "Positive-path" = the target row exists / the call succeeds. 
  - "Negative-path" = the target row is absent. 
  - `save` and `findAll` have no meaningful "row absent" branch, so a negative-path test is not required for them. 
  - SC-003 is met when every required cell is green for both domains (Beer and Customer) — minimum **12 tests** (4 required cells × 2 domains, of which 2 are positive + 1 negative per domain across `findById` / `deleteById`, plus 1 positive each for `save` / `findAll`):

  | Repository method | Positive-path test | Negative-path test |
  |-------------------|--------------------|--------------------|
  | `save`            | required           | not applicable     |
  | `findAll`         | required           | not applicable     |
  | `findById`        | required           | required           |
  | `deleteById`      | required           | required           |
- **SC-004**: A regression that breaks PostgreSQL dialect compatibility — for example, a column-type change incompatible with the existing DDL, or removal of the UUID default — is caught by this suite before it reaches the integration-test layer.
- **SC-005**: The suite is fully repeatable: running it N times in a row produces N identical green results with no manual cleanup step in between.

## Assumptions

- The existing DDL that defines `beer` and `customer` (currently `src/main/resources/schema_renew.sql`) is the authoritative schema. 
  - Any in-test bootstrap mechanism reuses this DDL rather than redefining tables.
- "In-memory" in the user's speed target is a qualitative goal, not a storage-backing mandate. The SC-002 ceiling is met by (a) Zonky's cached PostgreSQL binary (one extraction per developer machine, ~3–5 s), (b) per-class refresh (one PG launch per test class, not per method), and (c) the small data volume the slice tests produce. 
  - The phrase does NOT refer to a non-PostgreSQL in-memory engine; 
  - H2/HSQLDB remain ruled out by the dialect requirement.
  - If on a future CI host the wall-clock target slips, the first lever is mounting `~/.embedpostgresql/` (binary cache); a tmpfs-backed Zonky data directory is a second lever and would be introduced as a follow-up, not a baseline requirement.
- The `flashcard` table and its repository are out of scope for this feature.
- The `version` columns are reserved for future optimistic-locking support and are not asserted on by these tests.
- Existing controller-slice and service-unit tests remain unchanged; 
  - this feature adds a new, complementary test layer.
- The project's Maven configuration can be extended (test-scope dependencies, Surefire/Failsafe inclusions) 
  - without breaking other modules.
