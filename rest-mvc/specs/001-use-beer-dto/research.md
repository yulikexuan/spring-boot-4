# Phase 0 Research — Use BeerDto Instead of Beer Entity

All technical choices were pre-locked in `plan-draft.md` and confirmed during the planning clarification round. 

This document records the rationale and the alternatives evaluated so future readers do not have to re-derive them.

---

## R-1: Conversion mechanism — MapStruct vs. hand-rolled vs. inline

**Decision**: MapStruct 1.6.3, `componentModel = "spring"`, mapper interface `BeerMapper` colocated with `BeerDto` in package `domain.beer.dto`.

**Rationale**:
- `BeerDto` and `Beer` share 9 components. A hand-rolled converter has 2 × 9 = 18 field accesses to keep in sync, all silent on mismatch.
- MapStruct generates the impl at compile time → no reflection, no runtime cost, errors surface as compile errors.
- `componentModel = "spring"` matches the rest of the module (everything is a Spring bean) and lets `BeerServiceImpl` inject the mapper without manual `Mappers.getMapper(...)`.

**Alternatives considered**:
- **Static `from` / `toEntity` methods on `BeerDto`** (mentioned in spec assumptions). Rejected: pulls the mapping logic into the DTO type, which then has to import the entity — the opposite of the boundary the feature is trying to establish.
- **Inline mapping in `BeerServiceImpl`**. Rejected: 18 field assignments inside the service obscure the actual business logic (`updateWith` / `patchWith` call, repository call).
- **Spring `Converter<S,T>`** beans. Rejected: still hand-rolled body; same boilerplate as a plain mapper class.

---

## R-2: MapStruct + records — explicit `@Mapping` per component

**Decision**: Declare `@Mapping(source = "<comp>", target = "<comp>")` for every component on **both** directions, even when source and target component names match.

**Rationale**:
- `BeerDto` and `Beer` are both Java records — MapStruct must use canonical record constructors to instantiate them. Per MapStruct ≥ 1.5 record support, the processor will pick up record components automatically when both names match, but the draft locks in explicit mappings to keep mapper behavior obvious in code review and immune to a future field rename on either side.
- Explicit mappings also produce a clear compile error if either side gains or loses a component without the mapper being updated — turning "silent data loss" into "build break."

**Alternatives considered**:
- **Implicit mapping by name**. Works for the current shape but is invisible at the source level. Rejected per the draft's preference for verbose, audit-friendly mappers.

---

## R-3: Annotation-processor ordering — Lombok before MapStruct

**Decision**: Add to `rest-mvc/pom.xml` `maven-compiler-plugin` `<annotationProcessorPaths>`, in this order:
1. `org.projectlombok:lombok:${lombok.version}` (so Lombok-generated `@Builder` builders exist by the time MapStruct runs)
2. `org.projectlombok:lombok-mapstruct-binding:0.2.0` (bridge that exposes Lombok-generated accessors to MapStruct)
3. `io.github.higher-kinded-j:hkj-processor-plugins:${hkj-spring-boot-starter.version}` (preserved from the parent pom — unused on `BeerDto` but kept for module consistency)
4. `org.mapstruct:mapstruct-processor:1.6.3`

**Rationale**: Without `lombok-mapstruct-binding`, MapStruct may run before Lombok and fail to see Lombok-generated getters / builders on classes that use them. The binding is the officially documented MapStruct + Lombok bridge.

**Alternatives considered**:
- **Skip the binding**. Works today because `Beer` and `BeerDto` are records (record accessors are real methods, not Lombok-generated) — but `@Builder` static inner classes are Lombok-generated. The binding is cheap insurance and matches the draft.

---

## R-4: MapStruct runtime artifact — both `mapstruct` and `mapstruct-processor`

**Decision**: Add **both** `org.mapstruct:mapstruct:1.6.3` (compile/runtime) and `org.mapstruct:mapstruct-processor:1.6.3` (annotation-processor path only).

**Rationale**: `mapstruct` provides the `@Mapper` / `@Mapping` annotations and supporting types that the generated impl references at runtime. `mapstruct-processor` is the annotation processor itself and should not appear on the application classpath — it goes only in `annotationProcessorPaths`.

**Alternatives considered**:
- **Processor only**. Rejected: some setups appear to work because IDE classpaths leak the processor jar, but it is not portable to a clean `mvn` build.

---

## R-5: `BeerDto.id` — keep, not drop

**Decision**: `BeerDto` carries an `id` UUID component. Its value is populated when mapping from a persisted `Beer`. It is ignored on inbound `POST` payloads (the controller does not read it; the service builds a fresh `Beer` from non-id fields before save).

**Rationale**: The spec explicitly relaxes the original "no id field" instruction (`spec.md` Assumptions, FR-002). Dropping `id` would force `WebUtils.buildLocation(...)` to take its UUID from somewhere other than the returned DTO — a more invasive change with no offsetting benefit.

**Alternatives considered**:
- **Two DTOs**: `BeerCreateDto` (no id) + `BeerResponseDto` (with id). Rejected for now: doubles the surface area and the test churn for a tutorial module whose point is the boundary, not request/response asymmetry. Can be split later as a follow-up.

---

## R-6: Merge methods stay on `Beer` entity

**Decision**: `Beer.updateWith(UUID, Beer)` and `Beer.patchWith(UUID, Beer)` keep their current location and signatures (FR-007). `BeerServiceImpl` calls them on the `Beer` instance returned by `BeerRepository.findById(...)`, after mapping the inbound `BeerDto` to a transient `Beer`.

**Rationale**: Moving merge logic to `BeerDto` would force the DTO to know how to refresh `updateDate` and preserve `createdDate` — both audit fields the DTO is supposed to be ignorant of on inbound. Keeping merge on the entity preserves the "DTO is dumb, entity owns invariants" boundary.

---

## R-7: `BootstrapData` — switch to `BeerRepository`

**Decision**: `BootstrapData` is modified to depend on `BeerRepository` (instead of `BeerService`) for Beer inserts. The three existing `beer1/2/3` builders continue to build `Beer` entities and call `beerRepository.save(beer)` directly.

**Rationale**:
- After the feature, `BeerService.saveNewBeer` accepts `BeerDto`, so `BootstrapData` cannot literally stay untouched (the draft phrasing was aspirational).
- Switching to the repository preserves the draft's intent (BootstrapData continues to insert `Beer` entities, not DTOs) and avoids forcing a `BeerDto` builder into the bootstrap layer.
- Customer and Flashcard inserts remain via their respective services because those services still exchange entities.

**Alternatives considered**:
- **Build `BeerDto` and keep calling `BeerService`**. Rejected (clarification round): the draft explicitly preserves "BootstrapData uses `Beer` entities."
- **Build `Beer`, map to DTO inline, then call service**. Rejected: pure ceremony — bootstrap does not need round-trip serialization semantics.

---

## R-8: `BeerRepository` stays unchanged

**Decision**: `BeerRepository extends ListCrudRepository<Beer, UUID>` — unchanged, no `BeerDto` reference. SC-002 / FR-005.

**Rationale**: Repository is the persistence-layer boundary by definition; injecting DTO awareness here would break Principle IV (Spring Data JDBC idioms) and the spec's hard constraint.

---

## R-9: Test surface — captor retype + builder swap; JSON assertions unchanged

**Decision**: In `BeerControllerTest.java`:
- `@Captor ArgumentCaptor<Beer> beerArgumentCaptor` → `@Captor ArgumentCaptor<BeerDto> beerArgumentCaptor`
- `Beer.builder()` → `BeerDto.builder()` in request-body construction, stubbed `given(...)` return values, `then(...).should().…(beerDto)` arguments
- `given(beerService.saveNewBeer(any(Beer.class)))` → `any(BeerDto.class)`
- All `jsonPath(...)` assertions are unchanged — `BeerDto` has the same component names as `Beer`, so the serialized wire shape is byte-identical (FR-012, SC-004)
- Remove the now-stale `import …model.Beer;` from the test file

**Rationale**: The test slice exists to verify HTTP behavior; the only thing the boundary swap changes is the **Java type** the controller exchanges with its mock service. JSON shape, status codes, headers, and `NotFoundException` 404 path are all unchanged.

---

## R-10: Customer / Flashcard untouched

**Decision**: No edits under `domain/customer/`, `domain/ocp/` (flashcard), or their controllers / tests. SC-005.

**Rationale**: Feature scope is explicitly Beer-only. The two other sub-domains may adopt the same DTO pattern in a later iteration.

---

## Open questions resolved during clarification

| Question | Answer | Source |
|---|---|---|
| How does `BootstrapData` adapt? | Inject `BeerRepository` directly; keep building `Beer` entities. | clarification round |
| MapStruct runtime artifact strategy? | Add both `mapstruct` and `mapstruct-processor` 1.6.3. | clarification round |
| Where does `lombok-mapstruct-binding` version live? | Pinned to `0.2.0` inside `rest-mvc/pom.xml` only. | clarification round |

All `NEEDS CLARIFICATION` markers from Technical Context are resolved.
