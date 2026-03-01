  ---
Generated Prompt: Flashcard Implementation Plan

Please inspect the `rest-mvc` module and create a detailed, step-by-step
implementation plan for the Flashcard feature described in
`.prompt/flashcard_requirement.md`.

## Context to explore before planning

1. Read the existing `Flashcard` record in:
   `rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/ocp/model/Flashcard.java`

2. Use the `Beer` domain as the reference pattern for all layers:
    - Model:      `domain/beer/model/Beer.java`
    - Repository: `domain/beer/repository/BeerRepository.java`
    - Service:    `domain/beer/service/BeerService.java`
    - Controller: `web/controller/BeerController.java`
    - Test:       `src/test/java/.../web/controller/BeerControllerTest.java`

3. Read the current schema file to understand what already exists:
   `rest-mvc/src/main/resources/schema_renew.sql`

4. Read the guidelines at `.junie/guidelines.md` for code style rules.

## What the plan must cover (in order)

### Step 1 — SQL schema
- Decide the PostgreSQL table name and column types for all four fields
  of `Flashcard` (`id`, `question`, `answer`, `weight`).
- Specify the exact `DROP TABLE IF EXISTS` + `CREATE TABLE` SQL block
  to append to `schema_renew.sql`, compatible with the existing Beer/Customer tables.
- Choose the appropriate primary key type (UUID like Beer, or BIGSERIAL —
  justify your choice based on the `@Nullable Long id` field on the record).

### Step 2 — Make `Flashcard` a Spring Data JDBC entity
- List every annotation that needs to be added to the `Flashcard` record
  (`@Table`, `@Id`, `@Column` if column names differ from field names, etc.).
- Note any import changes required.
- Decide whether to keep `@GenerateLenses` — confirm it does not conflict
  with Spring Data JDBC annotations.

### Step 3 — `FlashcardRepository`
- Specify the full file path and package.
- State the exact interface signature (which `ListCrudRepository` type
  parameters to use given the `id` type chosen in Step 1).

### Step 4 — `FlashcardService` interface + `FlashcardServiceImpl`
- List all CRUD methods the interface must expose, mirroring the
  BeerService pattern (save, findAll, findById, update, delete).
- Note the class-level annotations for the implementation
  (`@Service`, `@NullMarked`, `@RequiredArgsConstructor`).
- Specify the exact file path for both interface and implementation
  (same file, package-private impl, as per Beer pattern).

### Step 5 — `FlashcardController`
- Specify the base `@RequestMapping` path (e.g., `/sfg7/api/v1/flashcard`).
- List all endpoints: GET list, GET by id, POST, PUT, DELETE, PATCH.
- Confirm return types and HTTP status codes matching the Beer controller
  convention (`ResponseEntity.created(location)`, `ResponseEntity.noContent()`).
- Note the `WebUtils.buildLocation(...)` usage for POST.

### Step 6 — Unit tests
- Identify which classes need unit tests and what scenarios to cover
  (service method logic, edge cases like missing entity).

### Step 7 — `@WebMvcTest` integration test (`FlashcardControllerTest`)
- Specify file path: `src/test/java/.../web/controller/FlashcardControllerTest.java`
- List test cases to implement, following the BeerControllerTest pattern:
    - GET by id — happy path
    - GET all — returns list
    - POST — returns 201 with Location header
    - PUT — returns 204
    - DELETE — returns 204
    - PATCH — returns 204
- Note required annotations: `@WebMvcTest(FlashcardController.class)`,
  `@MockitoBean FlashcardService`, `@DisplayName`,
  `@DisplayNameGeneration(ReplaceUnderscores.class)`.

## Output format

Present the plan as a numbered, hierarchical list with:
- Exact file paths for every file to create or modify
- Specific code decisions justified by the existing patterns
- Any gotchas or deviations from the Beer pattern (e.g., Long id vs UUID)

Do NOT write any code yet — only the plan.

  ---
Key decisions the plan prompt forces Claude to resolve up-front:

┌──────────────────────────────────────────────────┬─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                     Decision                     │                                                                 Why it matters                                                                  │
├──────────────────────────────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ Long vs UUID primary key                         │ Flashcard.id is @Nullable Long, but the Beer table uses UUID DEFAULT gen_random_uuid() — these require different SQL and different @Id handling │
├──────────────────────────────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ @GenerateLenses + Spring Data JDBC compatibility │ Both use annotation processors; the plan should confirm they coexist                                                                            │
├──────────────────────────────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ Service method signatures                        │ Flashcard has no updateWith/patchWith yet — the plan needs to address this                                                                      │
├──────────────────────────────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ Controller base path                             │ Needs to be decided consistently with existing /sfg7/api/v1/... convention                                                                      │
└──────────────────────────────────────────────────┴─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
