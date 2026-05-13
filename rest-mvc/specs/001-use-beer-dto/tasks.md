---
description: "Task list for: Use BeerDto Instead of Beer Entity at the Web/Service Boundary"
---

# Tasks: Use BeerDto Instead of Beer Entity at the Web/Service Boundary

- **Input**: Design documents from `specs/001-use-beer-dto/`
- **Prerequisites**: 
  - `plan.md` 
  - `spec.md` 
  - `research.md` 
  - `data-model.md` 
  - `contracts/beer-api.md` 
  - `quickstart.md`
- **Branch**: `001-use-beer-dto`

**Tests**: Existing `BeerControllerTest` is **adjusted** (FR-010, US4) 
- No new test files are created.

**Organization**: Tasks grouped by user story (US1 – US4) 
- so each delivers an independently verifiable slice.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Different file, no dependency on incomplete tasks — safe to parallelize
- **[Story]**: US1 / US2 / US3 / US4 — maps task to a spec user story
- All paths are **module-relative** (root: `rest-mvc/`)

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Wire MapStruct into the build so subsequent phases can use the generated mapper.

- [ ] T001 Add `org.mapstruct:mapstruct:1.6.3` runtime dependency and override `<annotationProcessorPaths>` in `rest-mvc/pom.xml` with this ordered list: lombok → `lombok-mapstruct-binding:0.2.0` → `hkj-processor-plugins` → `mapstruct-processor:1.6.3`; verify with `mvn -pl rest-mvc dependency:tree` showing `org.mapstruct:mapstruct:jar:1.6.3:compile`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: The DTO and its mapper are required by every user story (US1, US2, US3 via BootstrapData decoupling, US4 tests).

**⚠️ CRITICAL**: No user-story work can begin until T002–T004 complete.

- [ ] T002 [P] Create `BeerDto` record (components `id, version, beerName, beerStyle, upc, quantityOnHand, price, createdDate, updateDate` in that order; Lombok `@Builder`; `//: spring.boot.sfg7.rest.mvc.domain.beer.dto.BeerDto.java` header; NO Spring Data annotations) in `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/dto/BeerDto.java`
- [ ] T003 Create `BeerMapper` interface annotated `@Mapper(componentModel = "spring")` + `@NullMarked` with `BeerDto toDto(Beer beer)` and `Beer toEntity(BeerDto dto)`; declare 9 explicit `@Mapping(source="<comp>", target="<comp>")` annotations per method (one per component, both directions) in `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/dto/BeerMapper.java`
- [ ] T004 Run `mvn clean compile` and confirm `rest-mvc/target/generated-sources/annotations/spring/boot/sfg7/rest/mvc/domain/beer/dto/BeerMapperImpl.java` is produced (validates Lombok-before-MapStruct ordering)

**Checkpoint**: BeerDto + BeerMapper compile and generate. Story phases unblocked.

---

## Phase 3: User Story 1 — Decouple HTTP Contract From Persistence Mapping (Priority: P1) 🎯 MVP

**Goal**: `BeerController` and `BeerService` exchange `BeerDto`; no Spring Data type leaks into web/service layers. (FR-001, FR-003, FR-004, FR-011, SC-001)

**Independent Test**: After this phase, `grep -n "model.Beer" rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/web/controller/BeerController.java` and the same against `BeerService.java` return zero matches; existing `GET /sfg7/api/v1/beer` and `GET /sfg7/api/v1/beer/{id}` slice tests (once retyped in US4) still pass.

### Implementation for User Story 1

- [ ] T005 [US1] Swap `BeerService` interface signatures: `saveNewBeer(BeerDto) → BeerDto`, `findAllBeers() → List<BeerDto>`, `getBeerById(UUID) → BeerDto`, `updateBeerById(UUID, BeerDto)`, `patchBeerById(UUID, BeerDto)`; `deleteBeerById` unchanged; add `import …domain.beer.dto.BeerDto;` and remove `import …domain.beer.model.Beer;` from the interface section of `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/service/BeerService.java`
- [ ] T006 [US1] In `BeerServiceImpl` (same file): inject `BeerMapper` via constructor (`@RequiredArgsConstructor` field); implement `findAllBeers()` as `repository.findAll().stream().map(beerMapper::toDto).toList()`; implement `getBeerById(UUID id)` as `repository.findById(id).map(beerMapper::toDto).orElseThrow(() -> new NotFoundException(id))` in `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/service/BeerService.java`
- [ ] T007 [US1] Update `BeerController` GET endpoints: `List<BeerDto> findAllBeers()` and `BeerDto getBeerById(@PathVariable UUID beerId)`; add `import …domain.beer.dto.BeerDto;` and remove `import …domain.beer.model.Beer;` in `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/web/controller/BeerController.java`

**Checkpoint**: GET endpoints compile and serve identical wire JSON. Write endpoints fail to compile until Phase 4.

---

## Phase 4: User Story 2 — Preserve Existing Write Semantics (Priority: P1)

**Goal**: POST returns 201 + `Location`; PUT/PATCH/DELETE return 204; `createdDate` preserved on update/patch; `updateDate` refreshed; merge semantics from `Beer.updateWith` / `Beer.patchWith` unchanged (FR-006, FR-007, FR-008, FR-009, SC-004).

**Independent Test**: After this phase, the codebase compiles cleanly; once US4 retypes `BeerControllerTest`, every existing POST/PUT/PATCH/DELETE scenario (status code, `Location` header, `ArgumentCaptor` payload, `jsonPath` body assertions) passes.

### Implementation for User Story 2

- [ ] T008 [US2] Implement `BeerServiceImpl.saveNewBeer(BeerDto dto)`: build a transient `Beer` via `beerMapper.toEntity(dto)`, force `id` to `null` so DB assigns it, set `createdDate` and `updateDate` to `Instant.now()`, call `repository.save(...)`, return `beerMapper.toDto(saved)` in `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/service/BeerService.java`
- [ ] T009 [US2] Implement `BeerServiceImpl.updateBeerById(UUID id, BeerDto dto)`: `repository.findById(id).orElseThrow(() -> new NotFoundException(id))`; build incoming `Beer` via `beerMapper.toEntity(dto)`; call `existing.updateWith(id, incoming)`; `repository.save(updated)`; method returns `void` in `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/service/BeerService.java`
- [ ] T010 [US2] Implement `BeerServiceImpl.patchBeerById(UUID id, BeerDto dto)`: mirror T009 but call `existing.patchWith(id, incoming)` so null/blank components keep their prior values; `updateDate` always refreshed by `patchWith` in `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/service/BeerService.java`
- [ ] T011 [US2] Update `BeerController` write endpoints to `BeerDto`: `ResponseEntity<Void> saveNewBeer(@RequestBody BeerDto beerDto)` using `WebUtils.buildLocation("/{beerId}", savedDto::id)`; `updateBeerById(@PathVariable UUID beerId, @RequestBody BeerDto beerDto)`; `patchBeerById(@PathVariable UUID beerId, @RequestBody BeerDto beerDto)`; `deleteBeerById` signature unchanged in `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/web/controller/BeerController.java`

**Checkpoint**: Module compiles; all five CRUD endpoints exchange `BeerDto`.

---

## Phase 5: User Story 3 — Keep the Repository Layer on the Entity (Priority: P2)

**Goal**: `BeerRepository` references only `Beer`; `BootstrapData` continues inserting `Beer` entities by going around the now-DTO-typed service (FR-005, SC-002, research R-7, R-8).

**Independent Test**: `grep -n "BeerDto" rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/repository/BeerRepository.java` returns zero matches; `mvn -pl rest-mvc spring-boot:run` (with PostgreSQL up) logs `>>> 3 different Beers saved.`

### Implementation for User Story 3

- [ ] T012 [US3] Replace `private final BeerService beerService;` with `private final BeerRepository beerRepository;`; swap all three `beerN = beerService.saveNewBeer(beerN);` call sites for `beerN = beerRepository.save(beerN);` (Customer + Flashcard sections untouched) in `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/bootstrap/BootstrapData.java`
- [ ] T013 [P] [US3] Source-inspect `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/repository/BeerRepository.java` and confirm it still declares `extends ListCrudRepository<Beer, UUID>` with zero `BeerDto` imports or references (no code change expected — verification only)

**Checkpoint**: Repository unchanged; BootstrapData decoupled from DTO-typed service.

---

## Phase 6: User Story 4 — Adjust All Existing Tests to the New Boundary (Priority: P1)

**Goal**: `BeerControllerTest` exchanges `BeerDto` wherever production code now does; wire-format assertions (`jsonPath`, status codes, `Location` header) untouched (FR-010, SC-003).

**Independent Test**: `mvn clean test -Dtest=BeerControllerTest` is green; `mvn clean test` shows zero regressions in `CustomerControllerTest` / `FlashcardControllerTest`.

### Implementation for User Story 4

- [ ] T014 [US4] Retype `BeerControllerTest`: change `ArgumentCaptor<Beer>` → `ArgumentCaptor<BeerDto>`; replace every `Beer.builder()` with `BeerDto.builder()` in `testBeer`, list-assertion bodies, `requestBeer`, `savedBeer`, and the put-test `beer` local; change `given(beerService.saveNewBeer(any(Beer.class)))` → `any(BeerDto.class)`; keep all `jsonPath(...)` and `status()` matchers unchanged; remove `import …domain.beer.model.Beer;` and add `import …domain.beer.dto.BeerDto;` in `rest-mvc/src/test/java/spring/boot/sfg7/rest/mvc/web/controller/BeerControllerTest.java`
- [ ] T015 [US4] Run `mvn clean test -Dtest=BeerControllerTest` and confirm every scenario (GET list, GET by id, POST, PUT, PATCH, DELETE, 404) passes
- [ ] T016 [US4] Run `mvn clean test` and confirm `CustomerControllerTest` + `FlashcardControllerTest` were not modified by this feature and remain green (SC-005)

**Checkpoint**: Full module test suite green; behavior demonstrably preserved.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final spec-level verification (SC-001, SC-002, SC-004, SC-005).

- [ ] T017 [P] Grep `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/web/controller/BeerController.java` for `org.springframework.data` — expect zero matches (SC-001)
- [ ] T018 [P] Grep `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/service/BeerService.java` for `org.springframework.data` — expect zero matches (SC-001)
- [ ] T019 [P] Grep `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/repository/BeerRepository.java` for `BeerDto` — expect zero matches (SC-002)
- [ ] T020 Run the four PowerShell `Select-String` commands listed in `specs/001-use-beer-dto/quickstart.md` Step 8 and confirm expected output (cross-checks SC-001 + SC-002 + Beer-import absence in controller/service)
- [ ] T021 Optional — with PostgreSQL on `localhost:54316` and `DATASOURCE_PASSWORD` set, run `mvn -pl rest-mvc spring-boot:run`, replay a captured prior `curl` against `GET /sfg7/api/v1/beer` and `POST /sfg7/api/v1/beer`, and confirm JSON body + `Location` header are byte-identical (SC-004)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup, T001)** → no upstream dependencies.
- **Phase 2 (Foundational, T002–T004)** → depends on T001 (MapStruct must be wired before `BeerMapper` compiles).
- **Phase 3 (US1, T005–T007)** → depends on Phase 2.
- **Phase 4 (US2, T008–T011)** → depends on Phase 3 (interface swap in T005 must land first; without writes, controller in T007 does not compile module-wide).
- **Phase 5 (US3, T012–T013)** → depends on Phase 3 (BootstrapData edit needs the DTO-typed service to be in place to motivate the switch; T013 is independent verification).
- **Phase 6 (US4, T014–T016)** → depends on Phase 4 (test retyping needs all DTO-typed signatures present).
- **Phase 7 (Polish, T017–T021)** → depends on Phase 6.

### User Story Dependencies

- US1 (P1) is the foundation slice — must complete before US2.
- US2 (P1) requires US1 (shared file `BeerService.java`, `BeerController.java`).
- US3 (P2) requires US1 — BootstrapData edit is forced by the service-signature swap in T005.
- US4 (P1) requires US1 + US2 — tests assert against final DTO-typed surface.

### Within Each User Story

- Models/types before mappers (T002 → T003).
- Interface swap before impl bodies (T005 → T006, T008–T010).
- Service before controller for read-path (T006 → T007); same for write-path (T008–T010 → T011).
- Production code before tests (T011 → T014).

### Parallel Opportunities

- T002 is the only Phase-2 task marked [P]; T003 depends on T002 by import.
- T013 (US3 source inspection) is [P] — independent of T012.
- T017, T018, T019 (polish greps) are [P] — three different files.
- Cross-story: nothing else can be parallelized because US1/US2/US4 share files.

---

## Parallel Example: Polish Phase

``` 
# Run the three SC-001 / SC-002 greps concurrently:
Task: T017 — grep BeerController.java for org.springframework.data
Task: T018 — grep BeerService.java for org.springframework.data
Task: T019 — grep BeerRepository.java for BeerDto
```

---

## Implementation Strategy

### MVP First (US1 only)

1. T001 → T002 → T003 → T004 (foundation).
2. T005 → T006 → T007 (US1).
3. **Stop here only if write paths are not in scope** — the module will not compile because Phase-4 write impls are needed once T005 swaps the full interface. In practice, US1 and US2 ship together.

### Recommended Incremental Order

1. **Compile-clean foundation**: T001 → T002 → T003 → T004 → T005 → T006 → T008 → T009 → T010 → T007 → T011 → T012 → T013. After this, `mvn -pl rest-mvc compile` is green; production code is on `BeerDto`.
2. **Test correctness**: T014 → T015 → T016. Suite is green.
3. **Spec-level validation**: T017 → T018 → T019 → T020. SC-001/SC-002 demonstrated.
4. **Optional manual replay**: T021. SC-004 demonstrated.

### Parallel Team Strategy

Not applicable — feature touches 7 files, 4 of which are shared between US1 and US2. 
- **A single developer sequence is fastest.** 
- T013 + T017/T018/T019 can be batched.

---

## Notes

- [P] = different files, no incomplete dependencies.
- [Story] traces each task to a spec user story.
- T020 / T021 are verification, not implementation — they have no edits.
- T021 requires PostgreSQL; skip if unavailable and rely on `BeerControllerTest` for behavior verification.
- After T016 the suite is the executable proof of SC-003.
