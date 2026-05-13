# Feature Specification: Use BeerDto Instead of Beer Entity at the Web/Service Boundary

- **Feature Branch**: `001-use-beer-dto`
- **Created**: 2026-05-12
- **Status**: Draft
- **Input**: User description: "Use Beer DTO instead of using Beer Entity: 
  - Create `BeerDto` based on `Beer` Entity. 
  - `BeerDto` is a java `record` with no Spring Data JDBC annotations. 
  - `BeerController` and `BeerService` should use `BeerDto`. 
  - `BeerRepository` should only use `Beer` entity. 
  - All existing tests must be adjusted."

--- 

## User Scenarios & Testing *(mandatory)*

### User Story 1 – Decouple HTTP Contract From Persistence Mapping (Priority: P1)

As a maintainer of the Beer REST API, I want the controller and service to exchange a transport object that carries no persistence annotations, so that changes to the database mapping (column names, ID strategy, audit columns) do not force changes to the public API contract or to controller/service code.

**Why this priority**: 

- This is the only user story that delivers the core value of the feature. 
- Without it, persistence concerns continue to leak into the HTTP layer and the rest of the work is undefined.

**Independent Test**: 

- Inspect the source: `BeerController` and `BeerService` reference only `BeerDto`; `BeerRepository` references only `Beer`. 
- Run the existing controller slice tests and service unit tests against the updated code and confirm they pass without re-introducing persistence types into the web/service layers.

**Acceptance Scenarios**:

1. **Given** the Beer API is reachable, **When** a client sends `GET /sfg7/api/v1/beer`, **Then** the response body lists Beer items using the DTO shape and the same JSON field names that exist today, with no observable change to HTTP status, headers, or payload structure.
2. **Given** the Beer API is reachable, **When** a client sends `GET /sfg7/api/v1/beer/{id}` for an existing id, **Then** a 200 response is returned with a DTO payload functionally equivalent to today's response.
3. **Given** the codebase after the change, **When** a reviewer searches the controller and service sources, **Then** no Spring Data JDBC type (`@Id`, `@Table`, `@Column`) appears in either; the only place those annotations remain is the `Beer` entity used by the repository.

### User Story 2 – Preserve Existing Write Semantics (POST / PUT / PATCH / DELETE) (Priority: P1)

As an API client, I want POST, PUT, PATCH, and DELETE on `/sfg7/api/v1/beer` 
to behave exactly as they do today (status codes, `Location` header on create, partial-merge rules on PATCH, full-replace rules on PUT) 
so that the DTO migration is invisible from outside the application.

**Why this priority**: 

- Write paths carry the most behavioral risk during the swap (merge logic, Location-header construction, optimistic-locking field handling). 
- Keeping their externally observable behavior unchanged is required for the change to ship safely.

**Independent Test**: 

- Run the existing `BeerControllerTest` suite (adjusted only to use the DTO type) 
- and verify all assertions on status codes, `Location` header, and request/response payloads still pass.

**Acceptance Scenarios**:

1. **Given** a valid new Beer payload, **When** a client `POST`s to `/sfg7/api/v1/beer`, **Then** the response is `201 Created` with a `Location` header of the form `.../sfg7/api/v1/beer/{newId}`, where `{newId}` is the id assigned by the database.
2. **Given** an existing beer, **When** a client `PUT`s a full replacement, **Then** the response is `204 No Content` and the stored row reflects the new values, with `createdDate` preserved and `updateDate` refreshed.
3. **Given** an existing beer, **When** a client `PATCH`es a partial payload (some fields null/blank), **Then** the response is `204 No Content` and only the supplied non-null/non-blank fields are updated; null/blank fields keep their prior values.
4. **Given** an existing beer, **When** a client `DELETE`s by id, **Then** the response is `204 No Content` and the row is removed.

### User Story 3 – Keep the Repository Layer on the Entity (Priority: P2)

As a developer working on persistence, I want `BeerRepository` to continue extending `ListCrudRepository<Beer, UUID>` and to use the `Beer` entity exclusively, so that Spring Data JDBC mapping, custom-query options, and aggregate-root semantics remain in one place.

**Why this priority**: This is structural housekeeping that protects the boundary in the other direction. It is P2 because it is largely passive — the repository simply must not gain any DTO awareness.

**Independent Test**: Source inspection: `BeerRepository.java` references only `Beer` (and Spring Data types). No `BeerDto` import appears anywhere under `domain/beer/repository/`.

**Acceptance Scenarios**:

1. **Given** the codebase after the change, **When** a reviewer opens `BeerRepository`, **Then** the type parameter and any method signatures use only `Beer`, never `BeerDto`.
2. **Given** the repository, **When** the service calls `findAll`, `findById`, `save`, `deleteById`, **Then** these calls return / accept `Beer` entities, and the service performs DTO ↔ entity conversion itself.

### User Story 4 – Adjust All Existing Tests to the New Boundary (Priority: P1)

As a developer running the existing test suite, I want every test that currently uses `Beer` at the controller or service boundary to be updated to use `BeerDto` instead, 
so that the suite still validates the same behavior after the migration and no test references stale types.

**Why this priority**: 
- Tests are the executable specification of current behavior. 
- They must move with the boundary or the migration cannot be validated.

**Independent Test**: 

- Run `mvn test -pl rest-mvc` (and any module-level command that exercises Beer tests). 
- The pre-existing tests for Beer compile and pass without regressions; 
  - no test references `Beer` where the production code now uses `BeerDto`.

**Acceptance Scenarios**:

1. **Given** the test sources after the change, **When** a reviewer searches Beer controller and service tests, **Then** stubbing, request bodies, response assertions, and `ArgumentCaptor` types use `BeerDto` wherever the production code under test now exchanges `BeerDto`.
2. **Given** the suite, **When** the developer runs the Beer-related unit and slice tests, **Then** all of them pass.

### Edge Cases

- **A PATCH payload in which every mutable field is null/blank:** 
  - behavior must match today — the existing row's mutable fields are all preserved; 
  - `updateDate` is refreshed; `createdDate` is preserved.
- A POST whose payload supplies `id`, `createdDate`, or `updateDate`: 
- any DTO-supplied values for these server-managed fields must be ignored on creation, 
  - with the database / service assigning them. 
  - `Location` is built from the server-assigned id.
- **A GET on an unknown id:** continues to surface a 404 via the existing `NotFoundException(UUID)` path. 
  - The DTO migration must not alter this behavior.
- **A request whose JSON contains an unknown field:** 
  - behavior is whatever Jackson 3 defaults dictate today; 
  - this feature does not change deserialization tolerance.

--- 

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: A new record type `BeerDto` MUST exist alongside `Beer`, carrying the same logical fields (id, version, beerName, beerStyle, upc, quantityOnHand, price, createdDate, updateDate) **but no Spring Data JDBC annotations** (no `@Table`, no `@Id`, no `@Column`).
- **FR-002**: `BeerDto` MUST keep an `id` field as a read-only response field — populated when mapping from a persisted `Beer`, ignored on inbound create requests. (Resolution of the original "no id field" instruction; see Assumptions.)
- **FR-003**: `BeerController` MUST accept and return `BeerDto` for every endpoint it currently uses `Beer` for: `GET` list, `GET` by id, `POST`, `PUT`, `PATCH`, `DELETE`.
- **FR-004**: `BeerService` (interface and `BeerServiceImpl`) MUST expose `BeerDto` as its parameter and return types in place of `Beer` for every method that currently exchanges `Beer`. Methods returning `void` keep their signature.
- **FR-005**: `BeerRepository` MUST remain typed on `Beer` only. No `BeerDto` reference appears in the repository package.
- **FR-006**: `BeerServiceImpl` MUST perform DTO ↔ Entity conversion internally: incoming `BeerDto` is mapped to a transient `Beer` before calling `existingEntity.updateWith(...)` / `existingEntity.patchWith(...)` (which remain on the `Beer` entity); persisted `Beer` results are mapped back to `BeerDto` before returning.
- **FR-007**: The merge methods `Beer.updateWith(UUID, Beer)` and `Beer.patchWith(UUID, Beer)` MUST remain on the `Beer` entity and retain their current semantics (full replace vs. partial merge; preserve `createdDate`; refresh `updateDate`).
- **FR-008**: `POST /sfg7/api/v1/beer` MUST return `201 Created` with a `Location` header pointing at the new resource. The id used to build that header MUST come from the persisted entity (via the DTO's `id` field after mapping back from the saved `Beer`).
- **FR-009**: `PUT`, `PATCH`, `DELETE` MUST continue to return `204 No Content`. `GET` endpoints MUST continue to return `200 OK` with the DTO payload, or `404 Not Found` via the existing `NotFoundException` path when an id does not exist.
- **FR-010**: All existing tests under `BeerControllerTest` and any Beer-service tests MUST be adjusted so that stubbing, request bodies, response assertions, `ObjectMapper` (de)serialization, and `ArgumentCaptor` types use `BeerDto` wherever production code now exchanges `BeerDto`. No test may reference `Beer` where production code under test now uses `BeerDto`.
- **FR-011**: After this change, no class under `web/controller/` or under `domain/beer/service/` may import `org.springframework.data.*` types as part of a Beer code path.
- **FR-012**: The JSON wire format observed by clients MUST be functionally equivalent to today's (same field names, types, and presence rules) so that no client change is required.

### Key Entities *(include if feature involves data)*

- **Beer** (entity): persistence-mapped aggregate root for the `beer` table. Owns `@Table`, `@Id`, `@Column` annotations and the `updateWith` / `patchWith` merge methods. Used exclusively by `BeerRepository` and (internally) by `BeerServiceImpl` during conversion.
- **BeerDto** (transport): plain Java `record` with the same logical fields as `Beer` and zero Spring Data JDBC annotations. Used as the parameter and return type of `BeerController` and `BeerService`. Carries `id` as a read-only response field.

--- 

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: After the change, a grep of `BeerController.java` and `BeerService.java` shows zero references to the `Beer` type and zero imports from `org.springframework.data.*`; `BeerDto` is the only Beer-domain type referenced there.
- **SC-002**: A grep of `BeerRepository.java` shows zero references to `BeerDto`.
- **SC-003**: The Beer-related unit and controller-slice tests (`BeerControllerTest` and any `BeerService*Test`) all pass on `mvn test -pl rest-mvc`, with the same scenario coverage as before the change (no scenarios deleted; all renamed/retyped to `BeerDto`).
- **SC-004**: The publicly observable HTTP behavior of every Beer endpoint is unchanged: identical status codes, identical `Location`-header format on POST, identical JSON field names and types — verifiable by replaying any prior request/response pair against the new code.
- **SC-005**: The change is local to the Beer subdomain: Customer and Flashcard code paths and tests remain untouched. A diff of `domain/customer/`, `domain/flashcard/`, and their tests shows no modifications attributable to this feature.

--- 

## Assumptions

- **DTO field set**: `BeerDto` mirrors all of `Beer`'s logical fields (including `version`, `createdDate`, `updateDate`) per explicit user choice, not just the client-mutable subset. A future feature may further trim the DTO if a stricter client-facing contract is desired.
- **`id` on `BeerDto`**: The original instruction said "`BeerDto` should not have an `id` field." During clarification the user chose to **relax this constraint** and keep `id` on the DTO as a read-only response field, because removing it would force a more invasive change to how `POST` builds its `Location` header. The spec follows the clarified intent, not the literal original wording.
- **Merge logic location**: `updateWith` / `patchWith` remain on the `Beer` entity (current location). The service is responsible for `BeerDto → Beer` mapping before invoking them, and `Beer → BeerDto` mapping after `save`.
- **Conversion mechanism**: The shape of the converter (static `from` / `toEntity` methods on `BeerDto`, a dedicated mapper class, MapStruct, or inline service code) is a *plan-phase* decision, not a spec-phase one. The spec only mandates that conversion happens inside the service layer.
- **No `@ControllerAdvice`** is introduced by this feature. The current `NotFoundException(@ResponseStatus 404)` path continues to be the 404 mechanism for Beer.
- **Persistence schema** (`beer` table, `schema.sql`, `BootstrapData`) is untouched.
- **Other subdomains** (`Customer`, `Flashcard`) are out of scope for this feature; they may follow the same pattern in a later iteration.
- **JSON tolerance / validation**: This feature does not introduce or remove bean-validation annotations on `BeerDto`. Wire-format behavior follows Jackson 3 defaults already configured in the module.
- **Lombok `@Builder`**: `BeerDto` follows the codebase convention and uses Lombok's `@Builder` (and any other Lombok annotations consistent with sibling records).
