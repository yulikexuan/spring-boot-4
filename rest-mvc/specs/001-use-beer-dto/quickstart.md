# Quickstart — Use BeerDto Instead of Beer Entity

How to land this feature end-to-end. 

Steps are ordered so each one compiles or fails cleanly on its own.

---

## Prereqs

- JDK 25, Maven 3.9+
- Module: `rest-mvc`
- PostgreSQL on `localhost:54316` with `DATASOURCE_PASSWORD` set is needed only for `mvn verify` / `mvn spring-boot:run`. The unit-test verification (`mvn test`) does NOT need a database.

---

## Step 1 — Wire MapStruct into the build

Edit `rest-mvc/pom.xml`:

1. Add `org.mapstruct:mapstruct:1.6.3` to `<dependencies>` (runtime, default `compile` scope).
2. Add a module-local `<plugin>` block for `maven-compiler-plugin` that overrides the parent's `<annotationProcessorPaths>` with this ordered list (parent has Lombok + HKJ; rest-mvc needs MapStruct + binding too):
   1. `org.projectlombok:lombok:${lombok.version}`
   2. `org.projectlombok:lombok-mapstruct-binding:0.2.0`
   3. `io.github.higher-kinded-j:hkj-processor-plugins:${hkj-spring-boot-starter.version}`
   4. `org.mapstruct:mapstruct-processor:1.6.3`

**Verify**: `mvn dependency:tree | grep mapstruct` shows `org.mapstruct:mapstruct:jar:1.6.3:compile`.

---

## Step 2 — Add `BeerDto`

Create `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/dto/BeerDto.java`.

- File header: `//: spring.boot.sfg7.rest.mvc.domain.beer.dto.BeerDto.java`
- Record components, in this order: `id, version, beerName, beerStyle, upc, quantityOnHand, price, createdDate, updateDate`
- Types match `Beer` 1:1 (see [data-model.md](./data-model.md))
- Annotate the type with Lombok `@Builder` only — **no** Spring Data annotations

**Verify**: `mvn clean compile` passes.

---

## Step 3 — Add `BeerMapper`

Create `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/dto/BeerMapper.java`.

- File header `//: …BeerMapper.java`
- `@Mapper(componentModel = "spring") @NullMarked public interface BeerMapper`
- Two methods, each with **9 explicit `@Mapping(source = "<comp>", target = "<comp>")`** annotations (one per component):
  - `BeerDto toDto(Beer beer)`
  - `Beer toEntity(BeerDto dto)`

**Verify**: `mvn clean compile` succeeds **and** `rest-mvc/target/generated-sources/annotations/spring/boot/sfg7/rest/mvc/domain/beer/dto/BeerMapperImpl.java` exists.

---

## Step 4 — Migrate `BeerService` to DTO

Edit `BeerService.java` in place. Replace `Beer` with `BeerDto` in:

- Interface methods: `saveNewBeer(BeerDto) → BeerDto`, `findAllBeers() → List<BeerDto>`, `getBeerById(UUID) → BeerDto`, `updateBeerById(UUID, BeerDto)`, `patchBeerById(UUID, BeerDto)`.
- `BeerServiceImpl`: inject `BeerMapper` alongside `BeerRepository`. Inside each method:
  - `saveNewBeer`: build a fresh `Beer` from the inbound DTO via `beerMapper.toEntity(dto)`, override `id` to `null`, set `createdDate` / `updateDate` to `Instant.now()`, save, return `beerMapper.toDto(saved)`.
  - `findAllBeers`: `repository.findAll().stream().map(beerMapper::toDto).toList()`.
  - `getBeerById`: same `orElseThrow(NotFoundException::new)` path; map the returned entity to DTO.
  - `updateBeerById`: load entity via `getBeerByIdEntity(id)` helper (or inline); build incoming `Beer` via `toEntity`; call `existing.updateWith(id, incoming)`; save. Method returns `void` — no DTO mapping needed on the way out.
  - `patchBeerById`: mirror `updateBeerById`, but call `existing.patchWith(...)`.
  - `deleteBeerById`: unchanged.

**Verify**: `mvn clean compile` passes (controller will momentarily fail to compile until Step 5).

---

## Step 5 — Migrate `BeerController` to DTO

Edit `BeerController.java`:

- Replace `Beer` with `BeerDto` in every method signature (`@RequestBody`, return types, `List<Beer>` → `List<BeerDto>`).
- `WebUtils.buildLocation("/{beerId}", newBeer::id)` stays — `newBeer` is now `BeerDto` and its `id()` accessor is identical.
- Remove `import spring.boot.sfg7.rest.mvc.domain.beer.model.Beer;`.

**Verify**: `mvn clean compile` passes module-wide.

---

## Step 6 — Update `BootstrapData`

Edit `BootstrapData.java`:

- Replace `private final BeerService beerService;` with `private final BeerRepository beerRepository;`.
- Replace each `beerN = beerService.saveNewBeer(beerN);` (3 sites) with `beerN = beerRepository.save(beerN);`.
- Customer + Flashcard sections unchanged.

**Verify**: `mvn clean compile` passes; `mvn -pl rest-mvc spring-boot:run` (with PG up) prints `>>> 3 different Beers saved.`

---

## Step 7 — Update `BeerControllerTest`

Edit `src/test/java/.../web/controller/BeerControllerTest.java`:

- `ArgumentCaptor<Beer> beerArgumentCaptor` → `ArgumentCaptor<BeerDto> beerArgumentCaptor`
- Replace `Beer.builder()` with `BeerDto.builder()` in: `testBeer` (get-by-id), the list assertion bodies, `requestBeer` and `savedBeer` (create), `beer` (put), `payload` map for patch is unchanged
- `given(beerService.saveNewBeer(any(Beer.class)))` → `any(BeerDto.class)`
- `given(beerService.getBeerById(...)).willReturn(testBeer)` and `then(beerService).should().updateBeerById(beerId, beer)` — `testBeer` / `beer` are now `BeerDto`-typed locals
- All `jsonPath(...)` and `status()` matchers stay
- Remove `import spring.boot.sfg7.rest.mvc.domain.beer.model.Beer;`

**Verify**: `mvn clean test -Dtest=BeerControllerTest` green.

---

## Step 8 — Full module verification

``` 
mvn clean test  
```

Expectation:

- All Beer test cases pass.
- `CustomerControllerTest` and `FlashcardControllerTest` are untouched and still pass (SC-005).
- No `import org.springframework.data.*` appears in any file under `web/controller/` or `domain/beer/service/`.

Manual cross-check commands:

```powershell
# Should print nothing
Select-String -Path src/main/java/spring/boot/sfg7/rest/mvc/web/controller/BeerController.java -Pattern "org.springframework.data"
Select-String -Path src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/service/BeerService.java -Pattern "org.springframework.data"

# Should print nothing
Select-String -Path src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/repository/BeerRepository.java -Pattern "BeerDto"

# Should print exactly one match line (the import) in each
Select-String -Path src/main/java/spring/boot/sfg7/rest/mvc/web/controller/BeerController.java -Pattern "BeerDto"
Select-String -Path src/main/java/spring/boot/sfg7/rest/mvc/domain/beer/service/BeerService.java -Pattern "BeerDto"
```

(SC-001, SC-002.)

---

## Optional — Manual replay (SC-004)

If PostgreSQL is up:

``` 
mvn spring-boot:run 
```

Then replay any prior `curl` against the running app — JSON body and `Location` header MUST be byte-identical to the pre-feature output.

---

## Rollback

The feature is local to the Beer subdomain and to `BootstrapData`. 

Reverting the 7 edited files (and deleting the new `domain/beer/dto/` package) restores prior behavior; no schema / data changes.
