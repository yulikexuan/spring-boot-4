# Flashcard Implementation Spec

> **Reference pattern**: every decision below mirrors the `Beer` domain.
> **Primary key**: `Long` / `BIGSERIAL` (not UUID).
> **`@GenerateLenses` + Spring Data JDBC**: confirmed compatible — keep both.

---

## Step 1 — SQL Schema (`schema_renew.sql`)

**File to modify**: `rest-mvc/src/main/resources/schema_renew.sql`

Append the following block **after** the existing `customer` table:

```sql
DROP TABLE IF EXISTS flashcard;

CREATE TABLE flashcard (
    id       BIGSERIAL PRIMARY KEY,
    question VARCHAR(500),
    answer   VARCHAR(1000),
    weight   INTEGER);
```

Add `DROP TABLE IF EXISTS flashcard;` at the top of the file alongside the
other `DROP` statements.

> **Rationale**: `Flashcard.id` is declared as `@Nullable Long`, so `BIGSERIAL`
> (auto-increment `BIGINT`) is the correct PostgreSQL type. No `version`,
> `created_date`, or `update_date` columns are needed — the record does not
> carry those fields.

---

## Step 2 — Make `Flashcard` a Spring Data JDBC Entity

**File to modify**:
`rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/ocp/model/Flashcard.java`

### Annotations to add

| Annotation            | Location              | Purpose                                  |
|-----------------------|-----------------------|------------------------------------------|
| `@Table("flashcard")` | class level           | maps the record to the `flashcard` table |
| `@Id`                 | on the `id` component | marks it as the primary key              |

Column names (`question`, `answer`, `weight`) already match field names, so no
`@Column` annotations are needed.

### Additional imports required

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
```

### `updateWith` / `patchWith` methods to add

Model after `Beer.updateWith` / `Beer.patchWith`, adapted to `Flashcard` fields
(no timestamps):

```java
// Full replacement — all fields come from the incoming flashcard
public Flashcard updateWith(@NonNull Long flashcardId, @NonNull Flashcard flashcard) {
    return Flashcard.builder()
            .id(flashcardId)
            .question(flashcard.question())
            .answer(flashcard.answer())
            .weight(flashcard.weight())
            .build();
}

// Partial update — keep existing value when incoming field is null / blank
public Flashcard patchWith(@NonNull Long flashcardId, @NonNull Flashcard flashcard) {
    return Flashcard.builder()
            .id(flashcardId)
            .question(StringUtils.hasText(flashcard.question()) ?
                    flashcard.question() : this.question())
            .answer(StringUtils.hasText(flashcard.answer()) ?
                    flashcard.answer() : this.answer())
            .weight(flashcard.weight() != null ?
                    flashcard.weight() : this.weight())
            .build();
}
```

Extra imports needed for these methods:

```java
import lombok.NonNull;
import org.springframework.util.StringUtils;
```

### Final record shape (for reference)

```java
//: spring.boot.sfg7.rest.mvc.domain.ocp.model.Flashcard.java

package spring.boot.sfg7.rest.mvc.domain.ocp.model;

import lombok.Builder;
import lombok.NonNull;
import org.higherkindedj.optics.annotations.GenerateLenses;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.StringUtils;

@Builder
@NullMarked
@GenerateLenses
@Table("flashcard")
public record Flashcard(
        @Id @Nullable Long id,
        String question,
        String answer,
        Integer weight) {

    public static Flashcard of(String question, String answer) {
        return new Flashcard(null, question, answer, 0);
    }

    public Flashcard updateWith(@NonNull Long flashcardId, @NonNull Flashcard flashcard) {
        return Flashcard.builder()
                .id(flashcardId)
                .question(flashcard.question())
                .answer(flashcard.answer())
                .weight(flashcard.weight())
                .build();
    }

    public Flashcard patchWith(@NonNull Long flashcardId, @NonNull Flashcard flashcard) {
        return Flashcard.builder()
                .id(flashcardId)
                .question(StringUtils.hasText(flashcard.question()) ?
                        flashcard.question() : this.question())
                .answer(StringUtils.hasText(flashcard.answer()) ?
                        flashcard.answer() : this.answer())
                .weight(flashcard.weight() != null ?
                        flashcard.weight() : this.weight())
                .build();
    }
}
```

---

## Step 3 — `FlashcardRepository`

**File to create**:
`rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/ocp/repository/FlashcardRepository.java`

```java
//: spring.boot.sfg7.rest.mvc.domain.ocp.repository.FlashcardRepository.java

package spring.boot.sfg7.rest.mvc.domain.ocp.repository;

import org.springframework.data.repository.ListCrudRepository;
import spring.boot.sfg7.rest.mvc.domain.ocp.model.Flashcard;

public interface FlashcardRepository extends ListCrudRepository<Flashcard, Long> {
}
```

> `Long` matches `Flashcard.id` type. No custom query methods needed.

---

## Step 4 — `FlashcardService` Interface + `FlashcardServiceImpl`

**File to create**:
`rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/ocp/service/FlashcardService.java`

Both the public interface and the package-private implementation live in the
**same file**, exactly as `BeerService.java` does.

### Interface

```java
public interface FlashcardService {

    Flashcard saveNewFlashcard(Flashcard flashcard);

    List<Flashcard> findAllFlashcards();

    Flashcard getFlashcardById(Long id);

    void updateFlashcardById(Long flashcardId, Flashcard flashcard);

    void deleteFlashcardById(Long flashcardId);

    void patchFlashcardById(Long flashcardId, Flashcard flashcard);
}
```

### Implementation

Class-level annotations: `@Service`, `@NullMarked`, `@RequiredArgsConstructor`.
Visibility: package-private (no `public` keyword).

Key notes:
- `saveNewFlashcard`: build a new `Flashcard` **without** `id` (let BIGSERIAL
  generate it), then delegate to `flashcardRepository.save(data)`.
- `getFlashcardById`: use `flashcardRepository.findById(id).orElseThrow()`.
- `updateFlashcardById` / `patchFlashcardById`: fetch existing entity first,
  then call `existingFlashcard.updateWith(...)` / `existingFlashcard.patchWith(...)`,
  then save.

### Full file (for reference)

```java
//: spring.boot.sfg7.rest.mvc.domain.ocp.service.FlashcardService.java

package spring.boot.sfg7.rest.mvc.domain.ocp.service;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import spring.boot.sfg7.rest.mvc.domain.ocp.model.Flashcard;
import spring.boot.sfg7.rest.mvc.domain.ocp.repository.FlashcardRepository;


public interface FlashcardService {

    Flashcard saveNewFlashcard(Flashcard flashcard);

    List<Flashcard> findAllFlashcards();

    Flashcard getFlashcardById(Long id);

    void updateFlashcardById(Long flashcardId, Flashcard flashcard);

    void deleteFlashcardById(Long flashcardId);

    void patchFlashcardById(Long flashcardId, Flashcard flashcard);
}


@Service
@NullMarked
@RequiredArgsConstructor
class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardRepository flashcardRepository;

    @Override
    public Flashcard saveNewFlashcard(@NonNull Flashcard flashcard) {
        var data = Flashcard.builder()
                .question(flashcard.question())
                .answer(flashcard.answer())
                .weight(flashcard.weight())
                .build();
        return flashcardRepository.save(data);
    }

    @Override
    public List<Flashcard> findAllFlashcards() {
        return flashcardRepository.findAll();
    }

    @Override
    public Flashcard getFlashcardById(@NonNull Long id) {
        return flashcardRepository.findById(id).orElseThrow();
    }

    @Override
    public void updateFlashcardById(Long flashcardId, Flashcard flashcard) {
        var existing = getFlashcardById(flashcardId);
        var updated = existing.updateWith(flashcardId, flashcard);
        flashcardRepository.save(updated);
    }

    @Override
    public void patchFlashcardById(Long flashcardId, Flashcard flashcard) {
        var existing = getFlashcardById(flashcardId);
        var patched = existing.patchWith(flashcardId, flashcard);
        flashcardRepository.save(patched);
    }

    @Override
    public void deleteFlashcardById(Long flashcardId) {
        flashcardRepository.deleteById(flashcardId);
    }
}
```

---

## Step 5 — `FlashcardController`

**File to create**:
`rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/web/controller/FlashcardController.java`

| Detail             | Value                                                                                                |
|--------------------|------------------------------------------------------------------------------------------------------|
| Class visibility   | package-private                                                                                      |
| Class annotations  | `@Slf4j`, `@RestController`, `@RequestMapping("/sfg7/api/v1/flashcard")`, `@RequiredArgsConstructor` |
| Path variable type | `Long flashcardId`                                                                                   |

### Endpoints

| Method | Path                                   | Handler                                                       | Returns                              |
|--------|----------------------------------------|---------------------------------------------------------------|--------------------------------------|
| GET    | `/sfg7/api/v1/flashcard`               | `listFlashcards()`                                            | `List<Flashcard>` + 200              |
| GET    | `/sfg7/api/v1/flashcard/{flashcardId}` | `getFlashcardById(@PathVariable Long)`                        | `Flashcard` + 200                    |
| POST   | `/sfg7/api/v1/flashcard`               | `saveNewFlashcard(@RequestBody Flashcard)`                    | `ResponseEntity<URI>` 201 + Location |
| PUT    | `/sfg7/api/v1/flashcard/{flashcardId}` | `updateFlashcard(@PathVariable Long, @RequestBody Flashcard)` | `ResponseEntity<Void>` 204           |
| DELETE | `/sfg7/api/v1/flashcard/{flashcardId}` | `deleteFlashcardById(@PathVariable Long)`                     | `ResponseEntity<Void>` 204           |
| PATCH  | `/sfg7/api/v1/flashcard/{flashcardId}` | `patchFlashcard(@PathVariable Long, @RequestBody Flashcard)`  | `ResponseEntity<Void>` 204           |

POST location header: `WebUtils.buildLocation("/{flashcardId}", newFlashcard::id)`

### Full file (for reference)

```java
//: spring.boot.sfg7.rest.mvc.web.controller.FlashcardController.java

package spring.boot.sfg7.rest.mvc.web.controller;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.boot.sfg7.rest.mvc.domain.ocp.model.Flashcard;
import spring.boot.sfg7.rest.mvc.domain.ocp.service.FlashcardService;
import spring.boot.sfg7.rest.mvc.web.util.WebUtils;


@Slf4j
@RestController
@RequestMapping("/sfg7/api/v1/flashcard")
@RequiredArgsConstructor
class FlashcardController {

    private final FlashcardService flashcardService;

    @GetMapping
    public List<Flashcard> listFlashcards() {
        return flashcardService.findAllFlashcards();
    }

    @GetMapping("{flashcardId}")
    public Flashcard getFlashcardById(@PathVariable Long flashcardId) {
        log.debug("Get Flashcard by Id - in controller");
        return flashcardService.getFlashcardById(flashcardId);
    }

    @PostMapping
    public ResponseEntity<URI> saveNewFlashcard(@RequestBody Flashcard flashcard) {
        var newFlashcard = flashcardService.saveNewFlashcard(flashcard);
        URI location = WebUtils.buildLocation("/{flashcardId}", newFlashcard::id);
        return ResponseEntity.created(location).build();
    }

    @PutMapping("{flashcardId}")
    public ResponseEntity<Void> updateFlashcard(
            @PathVariable Long flashcardId, @RequestBody Flashcard flashcard) {
        flashcardService.updateFlashcardById(flashcardId, flashcard);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{flashcardId}")
    public ResponseEntity<Void> deleteFlashcardById(@PathVariable Long flashcardId) {
        flashcardService.deleteFlashcardById(flashcardId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{flashcardId}")
    public ResponseEntity<Void> patchFlashcard(
            @PathVariable Long flashcardId, @RequestBody Flashcard flashcard) {
        flashcardService.patchFlashcardById(flashcardId, flashcard);
        return ResponseEntity.noContent().build();
    }
} /// :~
```

---

## Step 6 — Unit Tests

### `FlashcardServiceImplTest`

**File to create**:
`rest-mvc/src/test/java/spring/boot/sfg7/rest/mvc/domain/ocp/service/FlashcardServiceImplTest.java`

Annotations: `@ExtendWith(MockitoExtension.class)`, `@DisplayName`, `@DisplayNameGeneration(ReplaceUnderscores.class)`.

| Test case                          | Scenario                                                           |
|------------------------------------|--------------------------------------------------------------------|
| `saves_New_Flashcard_Successfully` | `saveNewFlashcard` builds without id, returns saved entity         |
| `finds_All_Flashcards`             | delegates to repository `findAll()`                                |
| `gets_Flashcard_By_Id`             | happy path — entity found                                          |
| `throws_When_Flashcard_Not_Found`  | `getFlashcardById` with unknown id throws `NoSuchElementException` |
| `updates_Flashcard_By_Id`          | calls `updateWith`, saves result                                   |
| `patches_Flashcard_By_Id`          | calls `patchWith`, saves only changed fields                       |
| `deletes_Flashcard_By_Id`          | delegates to `repository.deleteById`                               |

Use `@Mock FlashcardRepository`, `@InjectMocks FlashcardServiceImpl`.
Use `BDDMockito.given(...)` for stubbing, AssertJ `assertThat(...)` for assertions.

---

## Step 7 — `@WebMvcTest` Integration Test (`FlashcardControllerTest`)

**File to create**:
`rest-mvc/src/test/java/spring/boot/sfg7/rest/mvc/web/controller/FlashcardControllerTest.java`

### Class-level setup

```java
@WebMvcTest(FlashcardController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("Test FlashcardController - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class FlashcardControllerTest {

    static final String FLASHCARD_URI   = "/sfg7/api/v1/flashcard";
    static final String FLASHCARD_ID_PATH = "/{flashcardId}";

    @MockitoBean
    private FlashcardService flashcardService;

    @Autowired
    private MockMvc mockMvc;
}
```

### Test cases

| Test method                          | HTTP call                    | Stub                                                     | Expected                               |
|--------------------------------------|------------------------------|----------------------------------------------------------|----------------------------------------|
| `able_To_Get_Flashcard_By_Id`        | GET `/{id}`                  | `given(service.getFlashcardById(1L)).willReturn(card)`   | 200, JSON fields match                 |
| `able_To_Get_All_Flashcards_In_List` | GET `/`                      | `given(service.findAllFlashcards()).willReturn(list)`    | 200, JSON array, correct length        |
| `able_To_Save_New_Flashcard`         | POST `/` with JSON body      | `given(service.saveNewFlashcard(...)).willReturn(saved)` | 201, `Location` header contains new id |
| `able_To_Update_Flashcard`           | PUT `/{id}` with JSON body   | `willDoNothing` on `updateFlashcardById`                 | 204                                    |
| `able_To_Delete_Flashcard`           | DELETE `/{id}`               | `willDoNothing` on `deleteFlashcardById`                 | 204                                    |
| `able_To_Patch_Flashcard`            | PATCH `/{id}` with JSON body | `willDoNothing` on `patchFlashcardById`                  | 204                                    |

### Patterns to replicate from `BeerControllerTest`

- Build URIs with `ServletUriComponentsBuilder.fromUriString(FLASHCARD_URI)`
- Use `MockMvcRequestBuilders.get/post/put/delete/patch(uri).accept(APPLICATION_JSON).contentType(APPLICATION_JSON)`
- `jsonPath("$.id")`, `jsonPath("$.question")`, `jsonPath("$.answer")`, `jsonPath("$.weight")`
- Use `ObjectMapper` (auto-wired via `@WebMvcTest`) to serialize request bodies

> **Note**: For POST, the Location header value can be verified with
> `.andExpect(header().string("Location", containsString(String.valueOf(savedId))))`.

---

## Files Summary

| Action | File path                                                                     |
|--------|-------------------------------------------------------------------------------|
| Modify | `rest-mvc/src/main/resources/schema_renew.sql`                                |
| Modify | `rest-mvc/src/main/java/.../domain/ocp/model/Flashcard.java`                  |
| Create | `rest-mvc/src/main/java/.../domain/ocp/repository/FlashcardRepository.java`   |
| Create | `rest-mvc/src/main/java/.../domain/ocp/service/FlashcardService.java`         |
| Create | `rest-mvc/src/main/java/.../web/controller/FlashcardController.java`          |
| Create | `rest-mvc/src/test/java/.../domain/ocp/service/FlashcardServiceImplTest.java` |
| Create | `rest-mvc/src/test/java/.../web/controller/FlashcardControllerTest.java`      |
