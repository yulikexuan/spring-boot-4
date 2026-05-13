```
Goal: It's a prompt for running `/speckit.plan` 
```
**Spec:** @./specs/001-use-beer-dto/spec.md

**Tech context:** 
Spring Boot 4.0.6, Spring Data JDBC, Jackson 3, Java 25,
Lombok, JUnit 5 + Mockito + AssertJ + BDDMockito. Module: rest-mvc.

**Lock in these technical choices:**
- `BeerDto` placement: in package `domain.beer.dto`
- Conversion: Use MapStruct. `BeerMapper` interface lives in `domain.beer.dto`
  (same package as `BeerDto`), annotated `@Mapper(componentModel = "spring")`.
  Because `BeerDto` is a Java record (no setters), declare an explicit
  `@Mapping` for every component on both directions.
- Build wiring: add `org.mapstruct:mapstruct-processor:1.6.3` to
  `rest-mvc/pom.xml` and update the annotation-processor path so Lombok runs
  before MapStruct (add `org.projectlombok:lombok-mapstruct-binding`).
- All DTO<->Entity mapping inside `BeerServiceImpl`; controller sees DTO only
- `BeerDto`: Java record + Lombok @Builder, no Spring Data annotations
- Location header: keep WebUtils.buildLocation(path, () -> savedDto.id())
- updateWith / patchWith stay on `Beer` entity
- Tests: retype `ArgumentCaptor<Beer>` -> `ArgumentCaptor<BeerDto>`;
  request-body builders in tests construct `BeerDto` instead of `Beer`; the
  serialized JSON shape is unchanged.

Constraints to preserve:
- No `org.springframework.data.*` under web/controller/ or domain/beer/service/
- `BeerRepository` stays `ListCrudRepository<Beer, UUID>`
- Wire format unchanged; Customer/Flashcard untouched
- `BootstrapData` is untouched; it continues to insert via `BeerRepository`
  using `Beer` entities.

Deliverables: file-by-file change list, implementation order, and the
verification command (mvn test -pl rest-mvc).