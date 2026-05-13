# Implementation Plan: Use BeerDto Instead of Beer Entity at the Web/Service Boundary

- **Branch**: `001-use-beer-dto` | **Date**: 2026-05-13 | **Spec**: [./spec.md](./spec.md)
- **Input**: Feature specification from `/specs/001-use-beer-dto/spec.md`

## Summary

Introduce `BeerDto` (Java record, Lombok `@Builder`, zero Spring Data annotations) as the parameter and return type for `BeerController` and `BeerService`. 

`BeerRepository` stays typed on `Beer` only. 

DTO ↔ Entity conversion is performed inside `BeerServiceImpl` through a MapStruct-generated mapper (`BeerMapper`, `componentModel = "spring"`, package `domain.beer.dto`). 

`Beer.updateWith` / `Beer.patchWith` stay on the entity unchanged. 

`BootstrapData` switches from `BeerService` to `BeerRepository` so it can keep building `Beer` entities (the spec-level "untouched" goal is preserved in spirit: BootstrapData continues to insert `Beer` entities, just via the repository instead of the now-DTO-typed service). 

Wire format is unchanged; Customer / Flashcard are untouched.

## Technical Context

- **Language/Version**: Java 25 (records, `var`, pattern matching, sealed types) — `<java.version>25</java.version>` in root pom
- **Primary Dependencies**: Spring Boot 4.0.6 (Spring Framework 7), Spring Data JDBC, Jackson 3 (`tools.jackson.databind.*`), Lombok, **MapStruct 1.6.3** (new), **lombok-mapstruct-binding 0.2.0** (new)
- **Storage**: PostgreSQL on `localhost:54316` (unchanged); `schema.sql` DROP+CREATE on every start (unchanged); `beer` table unchanged
- **Testing**: JUnit 5 + Mockito + AssertJ + BDDMockito; `@WebMvcTest` from `org.springframework.boot.webmvc.test.autoconfigure`; `@MockitoBean` for service mocks; Mockito agent via `argLine` in root pom
- **Target Platform**: JVM 25 server
- **Project Type**: Spring Boot module within a multi-module Maven project (`rest-mvc`)
- **Performance Goals**: N/A (tutorial module; no perf delta expected — one extra mapper invocation per CRUD call)
- **Constraints**: No `org.springframework.data.*` imports under `web/controller/` or `domain/beer/service/`; wire format byte-identical to current (same JSON field names, types, presence); Customer + Flashcard sub-domains untouched; `BeerRepository` stays `ListCrudRepository<Beer, UUID>`
- **Scale/Scope**: ~7 production files edited/added, 1 test file edited; one new package (`domain.beer.dto`)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle                                        | Status   | Notes                                                                                                                                                                                                                                                                                                                                                           |
|--------------------------------------------------|----------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **I. Simplicity Over Features (NON-NEGOTIABLE)** | PASS     | MapStruct chosen over hand-rolled mapper to avoid 9-field × 2-direction boilerplate that would be error-prone and obscure the boundary. No new patterns introduced beyond the DTO ↔ Entity convention the spec mandates. No speculative knobs.                                                                                                                  |
| **II. Unit-Test Discipline (NON-NEGOTIABLE)**    | PASS     | `BeerControllerTest` retains its scope; `ArgumentCaptor<Beer>` → `ArgumentCaptor<BeerDto>`. Service-level mapping behavior is covered through the existing slice. No new test omissions. `Beer.updateWith` / `patchWith` retain their behavioral signatures, so existing assertions stay valid.                                                                 |
| **III. Modern Java Practice**                    | PASS     | `BeerDto` is a record; uses Lombok `@Builder` only (allowed list); `//: package.ClassName.java` header on each new file; `@NullMarked` on the new mapper interface.                                                                                                                                                                                             |
| **IV. Spring Boot Idiomatic Practice**           | PASS     | HTTP semantics preserved (201+Location, 204, 404 via `NotFoundException`); repository stays `ListCrudRepository<Beer, UUID>`; service interface + package-private impl in same `.java` file (existing pattern preserved); Jackson 3 imports unchanged (`tools.jackson.databind.ObjectMapper`); `@WebMvcTest` slice path unchanged; `@MockitoBean` already used. |

**Result**: All four core principles pass. No `Complexity Tracking` entries needed.

## Project Structure

### Documentation (this feature)

``` 
specs/001-use-beer-dto/
├── plan.md                     # This file
├── plan-prompt.md              # Locked-in technical choices (input)
├── spec.md                     # Feature specification
├── research.md                 # Phase 0 output
├── data-model.md               # Phase 1 output
├── quickstart.md               # Phase 1 output
├── contracts/
│   └── beer-api.md             # Phase 1 output — wire-format contract
└── tasks.md                    # Phase 2 output (created by /speckit-tasks)
```

### Source Code (repository root)

``` 
rest-mvc/
├── pom.xml                                                          # MODIFIED — add MapStruct deps + processor path
└── src/
    ├── main/java/spring/boot/sfg7/rest/mvc/
    │   ├── bootstrap/
    │   │   └── BootstrapData.java                                  # MODIFIED — inject BeerRepository for Beer inserts
    │   ├── domain/beer/
    │   │   ├── dto/                                                # NEW PACKAGE
    │   │   │   ├── BeerDto.java                                    # NEW — record + @Builder, no Spring Data annotations
    │   │   │   └── BeerMapper.java                                 # NEW — MapStruct @Mapper(componentModel="spring")
    │   │   ├── model/
    │   │   │   ├── Beer.java                                       # UNCHANGED — updateWith / patchWith stay here
    │   │   │   └── BeerStyle.java                                  # UNCHANGED
    │   │   ├── repository/
    │   │   │   └── BeerRepository.java                             # UNCHANGED — ListCrudRepository<Beer, UUID>
    │   │   └── service/
    │   │       └── BeerService.java                                # MODIFIED — interface & impl swap Beer → BeerDto
    │   └── web/controller/
    │       └── BeerController.java                                 # MODIFIED — Beer → BeerDto in signatures
    └── test/java/spring/boot/sfg7/rest/mvc/web/controller/
        └── BeerControllerTest.java                                 # MODIFIED — Beer → BeerDto in stubs/captors/requests
```

**Structure Decision**: 
- Use the existing rest-mvc layered layout. 
- Add one new package `spring.boot.sfg7.rest.mvc.domain.beer.dto` containing the DTO and the MapStruct mapper, colocated per the draft. 
- No other directories were created. 
- Customer / Flashcard / config / web util packages are untouched.

### File-by-file change list

| #   | File                                                       | Change                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | Reason                                                                                                        |
|-----|------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| 1   | `rest-mvc/pom.xml`                                         | Add `org.mapstruct:mapstruct:1.6.3` runtime dep; add `org.mapstruct:mapstruct-processor:1.6.3` and `org.projectlombok:lombok-mapstruct-binding:0.2.0` to `annotationProcessorPaths` (after Lombok). Override the parent's `maven-compiler-plugin` `<annotationProcessorPaths>` block locally because the root pom only declares Lombok + HKJ.                                                                                                                                                                                  | Wires MapStruct compile-time generation; binding ensures Lombok-generated accessors are visible to MapStruct. |
| 2   | `src/main/java/.../domain/beer/dto/BeerDto.java`           | NEW. Record with components `id, version, beerName, beerStyle, upc, quantityOnHand, price, createdDate, updateDate`. Annotated `@Builder`. No Spring Data annotations. `//: ...` header.                                                                                                                                                                                                                                                                                                                                       | DTO required by FR-001 / FR-002.                                                                              |
| 3   | `src/main/java/.../domain/beer/dto/BeerMapper.java`        | NEW. `@Mapper(componentModel = "spring") @NullMarked` interface with `BeerDto toDto(Beer beer)` and `Beer toEntity(BeerDto dto)`. Because both types are records, declare explicit `@Mapping(source="…", target="…")` for every component on each direction (one `@Mapping` per field, both ways).                                                                                                                                                                                                                             | MapStruct cannot infer record-component accessors as setters; explicit mappings are required.                 |
| 4   | `src/main/java/.../domain/beer/service/BeerService.java`   | MODIFIED. Replace `Beer` with `BeerDto` in interface signatures: `saveNewBeer(BeerDto) → BeerDto`, `findAllBeers() → List<BeerDto>`, `getBeerById(UUID) → BeerDto`, `updateBeerById(UUID, BeerDto)`, `patchBeerById(UUID, BeerDto)`. `BeerServiceImpl` injects `BeerRepository` + `BeerMapper`. Map inbound `BeerDto → Beer`, call existing `updateWith` / `patchWith` on `Beer`, save, map back `Beer → BeerDto`.                                                                                                             | FR-004, FR-006.                                                                                               |
| 5   | `src/main/java/.../web/controller/BeerController.java`     | MODIFIED. Replace every `Beer` with `BeerDto` in method signatures (request bodies, return types, `@PathVariable` UUID unchanged). `WebUtils.buildLocation("/{beerId}", newBeer::id)` stays — `id()` now lives on `BeerDto`. Remove the `import …model.Beer;`.                                                                                                                                                                                                                                                                 | FR-003, FR-008, SC-001.                                                                                       |
| 6   | `src/main/java/.../bootstrap/BootstrapData.java`           | MODIFIED. Replace `private final BeerService beerService;` with `private final BeerRepository beerRepository;`. Replace three `beerService.saveNewBeer(beerN)` calls with `beerRepository.save(beerN)`. Customer + Flashcard sections untouched.                                                                                                                                                                                                                                                                               | Decision from clarification: BootstrapData keeps `Beer` entities by going around the now-DTO-typed service.   |
| 7   | `src/test/java/.../web/controller/BeerControllerTest.java` | MODIFIED. Retype `ArgumentCaptor<Beer>` → `ArgumentCaptor<BeerDto>`. Replace `Beer.builder()` with `BeerDto.builder()` in request payload construction, stubbed return values, and `.willReturn(...)` arguments. Replace `given(beerService.saveNewBeer(any(Beer.class)))` with `any(BeerDto.class)`. Replace `then(beerService).should().updateBeerById(beerId, beer)` with the `BeerDto`-typed variable. JSON assertions (`jsonPath("$.beerName")` …) unchanged — wire shape is identical. Remove the `import …model.Beer;`. | FR-010, SC-003.                                                                                               |

### Implementation order

1. **pom.xml**: add MapStruct deps + processor path. Verify `mvn -pl rest-mvc dependency:tree` shows `org.mapstruct:mapstruct:1.6.3`.
2. **BeerDto.java**: new record. Compile-only check (no generated mapper yet).
3. **BeerMapper.java**: new interface with explicit `@Mapping` per field, both directions. Trigger annotation processor: `mvn -pl rest-mvc compile` and confirm `target/generated-sources/annotations/.../BeerMapperImpl.java` is produced.
4. **BeerService.java**: change interface signatures; rewrite `BeerServiceImpl` to use mapper + repository; existing `Beer.updateWith` / `patchWith` calls are preserved on the entity instance returned by `findById`.
5. **BeerController.java**: replace `Beer` with `BeerDto` in signatures; remove `Beer` import.
6. **BootstrapData.java**: swap `BeerService` for `BeerRepository`; update three `saveNewBeer(...)` call sites.
7. **BeerControllerTest.java**: retype captor + builders; keep JSON assertions; remove `Beer` import from the test.
8. **Verify**: `mvn test rest-mvc` — all `BeerControllerTest` cases pass green; Customer + Flashcard test files unchanged and still pass.

### Verification command

``` 
mvn test rest-mvc
```

Optional follow-up (manual, requires PostgreSQL on `localhost:54316` + `DATASOURCE_PASSWORD`): `mvn spring-boot:run -pl rest-mvc` and replay `GET /sfg7/api/v1/beer` + `POST /sfg7/api/v1/beer` to confirm wire format byte-identical and `Location` header unchanged (SC-004).

## Complexity Tracking

> No constitution violations. Table left empty by design.

| Violation   | Why Needed   | Simpler Alternative Rejected Because   |
|-------------|--------------|----------------------------------------|
| _(none)_    | —            | —                                      |

<!-- SPECKIT START -->
Plan: [./plan.md](./plan.md) — Spec: [./spec.md](./spec.md) — Research: [./research.md](./research.md) — Data model: [./data-model.md](./data-model.md) — Contracts: [./contracts/beer-api.md](./contracts/beer-api.md) — Quickstart: [./quickstart.md](./quickstart.md)
<!-- SPECKIT END -->
