<!--
SYNC IMPACT REPORT
==================
Version change: (template) → 1.0.0
Bump rationale: Initial ratification — populating placeholder template with concrete principles
                for the rest-mvc tutorial module.

Modified principles:
  - [PRINCIPLE_1_NAME]        → I. Simplicity Over Features (NON-NEGOTIABLE)
  - [PRINCIPLE_2_NAME]        → II. Unit-Test Discipline (NON-NEGOTIABLE)
  - [PRINCIPLE_3_NAME]        → III. Modern Java Practice
  - [PRINCIPLE_4_NAME]        → IV. Spring Boot Idiomatic Practice
  - [PRINCIPLE_5_NAME]        → REMOVED (4 principles match the user-stated themes; no fifth needed)

Added sections:
  - Tutorial Module Constraints (replaces [SECTION_2_NAME])
  - Development Workflow & Quality Gates (replaces [SECTION_3_NAME])
  - Governance (concrete content)

Removed sections:
  - 5th principle slot (intentional — user input scoped 4 themes)

Templates requiring updates:
  - ✅ .specify/memory/constitution.md            (this file)
  - ⚠ .specify/templates/plan-template.md         (Constitution Check section is a stub —
                                                  /speckit-plan must populate gates from this
                                                  constitution; no edits required to the template
                                                  itself, but plan output must enforce P-I..P-IV)
  - ⚠ .specify/templates/spec-template.md         (no constitution-driven mandatory sections changed)
  - ⚠ .specify/templates/tasks-template.md        (note: P-II makes unit tests REQUIRED for the
                                                  rest-mvc module; the template's "Tests are OPTIONAL"
                                                  guidance must be overridden by /speckit-tasks for
                                                  this module — no template edit needed)
  - ⚠ rest-mvc/CLAUDE.md                          (already documents test conventions — aligned)
  - ⚠ ../.junie/java-guidelines.md                (canonical reference for P-III — verify present)
  - ⚠ ../.junie/spring-boot-guidelines.md         (canonical reference for P-IV — verify present)

Deferred / TODOs:
  - None.
-->

# rest-mvc Tutorial Module Constitution

## Core Principles

### I. Simplicity Over Features (NON-NEGOTIABLE)

The rest-mvc module is a teaching artifact. Pedagogical clarity outranks feature completeness.

- New code MUST justify itself against the simplest working alternative; if a flat function or
  inline call works, prefer it over a new abstraction, interface, framework, or pattern.
- Speculative generality is forbidden: do NOT add hooks, strategy patterns, configuration knobs,
  or extension points for hypothetical future requirements.
- Cross-cutting concerns (caching, async, retries, security filters, custom advice) MUST stay
  out unless a user story demands them. Three similar lines beat a premature abstraction.
- Every PR introducing a new abstraction MUST list, in the description, the simpler alternative
  considered and the concrete reason it is insufficient.

**Rationale**: Learners reading this module must trace the request → controller → service →
repository path with no detours. Every layer of indirection is a tax on comprehension.

### II. Unit-Test Discipline (NON-NEGOTIABLE)

Three layers MUST have unit tests; integration tests are additive, not substitutive.

- **Domain logic** (records' `updateWith` / `patchWith`, enum mappings, value-object invariants):
  pure JUnit, no Spring context, no mocks.
- **Services** (`*ServiceImpl`): `@ExtendWith(MockitoExtension.class)` + `@Mock` repository +
  `@InjectMocks` impl; assert behavior via `BDDMockito.given(...)` / `then(mock).should(...)` and
  use `ArgumentCaptor` to verify what the service hands the repository.
- **REST controllers**: `@WebMvcTest(XController.class)` + `@MockitoBean` for the service; assert
  status, headers (e.g. `Location` on 201), and JSON shape via `MockMvc`. Import `@WebMvcTest`
  from `org.springframework.boot.webmvc.test.autoconfigure` (Spring Boot 4 path).
- A controller, service, or non-trivial domain method MUST NOT merge without a unit test
  covering its happy path AND at least one error/edge case (404, validation failure, null/blank
  patch field, etc.).
- A task is not "done" until its tests run green locally via `mvn test -pl rest-mvc`.

**Rationale**: Each layer is independently teachable only if it is independently verifiable.
Skipping a layer's unit tests collapses the layer boundary the module is trying to demonstrate.

### III. Modern Java Practice

Code MUST track the Java 25 / JDK feature baseline established by the parent project's
[`java-guidelines.md`](../../../.junie/java-guidelines.md).

- Records over classes for domain models, DTOs, and value objects.
- `var` for local inference where the right-hand side makes the type obvious; explicit types at
  API boundaries.
- Sealed types and pattern matching (instanceof, switch) where they replace visitor or
  type-tag boilerplate.
- `Optional` only as a return type for absence; never as a parameter or field.
- `@NullMarked` (jspecify) on every `package-info` or class enforcing null-safety; do not
  re-introduce nullable-by-default APIs.
- Lombok is permitted for `@RequiredArgsConstructor`, `@Slf4j`, `@Builder` only.
- File header comment `//: package.ClassName.java` MUST appear as line 1 of every Java file.
- Comments MUST be sparing — present only when the *why* is non-derivable from the code; never
  narrate *what* the code does.

**Rationale**: The module exists partly to showcase modern JDK ergonomics; using older idioms
defeats that purpose.

### IV. Spring Boot Idiomatic Practice

Code MUST follow the parent project's
[`spring-boot-guidelines.md`](../../../.junie/spring-boot-guidelines.md) and the conventions
already documented in `rest-mvc/CLAUDE.md`.

- HTTP semantics: `POST` → 201 + `Location` header (built via `WebUtils.buildLocation(...)`);
  `PUT` / `PATCH` / `DELETE` → 204 No Content.
- Persistence: Spring Data JDBC; repositories extend `ListCrudRepository<Entity, ID>`;
  domain records use `@Table`, `@Column("snake_case")`, `@Id`. No nested aggregates; every record
  is its own aggregate root. No custom `@Query` unless `ListCrudRepository` is insufficient.
- IDs: `UUID` defaulted at the database (`gen_random_uuid()`) for application-assigned domains;
  `BIGSERIAL`/`Long` for auto-increment domains. Never generate IDs in application code for the
  UUID domains.
- Service layer: interface + package-private `*Impl` colocated in the same `.java` file
  (e.g. `BeerService.java` declares both `BeerService` and `BeerServiceImpl`).
- Schema management: `schema.sql` runs DROP+CREATE on every start (`spring.sql.init.mode: always`);
  `BootstrapData` non-idempotent inserts depend on this — do NOT add row-count guards.
- JSON: Spring Boot 4 ships Jackson 3 — import `tools.jackson.databind.ObjectMapper`,
  not the Jackson 2 (`com.fasterxml.jackson...`) package, except inside the donut-shop
  cross-version compatibility demo (out of scope here).
- Testing imports: `@WebMvcTest` from the Boot 4 webmvc-test autoconfigure package;
  `@MockitoBean` (not the deprecated `@MockBean`).
- Documentation lookups for Spring/Jackson/Spring Data MUST use the JavaDoc Central MCP
  (`https://www.javadocs.dev/mcp`) before relying on training-data recall.

**Rationale**: Boot 4 changed the Jackson namespace, the test slice package paths, and the
mocking annotation; cargo-culting Boot 3 idioms produces silently broken examples.

## Tutorial Module Constraints

- **Scope discipline**: The three sub-domains (Beer, Customer, Flashcard) are intentionally
  independent and intentionally identical in shape. New features MUST be added to all three
  (or to none) unless the feature exists *to demonstrate* divergence.
- **No premature exception handling infrastructure**: `@ControllerAdvice` is intentionally
  absent. `NotFoundException` (`@ResponseStatus(HttpStatus.NOT_FOUND)`) handles the 404 case
  for UUID-keyed domains. Adding global advice requires a documented learning goal in the spec.
- **Database state**: PostgreSQL on `localhost:54316`, password via `DATASOURCE_PASSWORD`.
  `schema.sql` is the source of truth; `schema_renew.sql` is a manual reset script and is NOT
  auto-loaded.
- **Dead code is a defect**: Endpoints, constants, methods, and fields kept "because tests
  reference them" MUST be deleted along with their tests. Commented-out scaffolding is
  permitted only when the file's CLAUDE.md explicitly identifies it as intentional (e.g., the
  exception-handler scaffold).

## Development Workflow & Quality Gates

- **Plan-first for non-trivial work**: Any task spanning 3+ steps or introducing an
  architectural choice MUST produce a plan under `docs/dev/YYYY-MM-DD-hh-mm_plan-*.md` before
  implementation. Move the plan to `docs/dev/finished/` on completion.
- **Pre-merge gates** (every PR):
  1. `mvn test -pl rest-mvc` passes.
  2. `mvn verify -pl rest-mvc` passes (unit + IT, given a running PostgreSQL — IT may be
     `@Disabled` if the suite is not yet wired, in which case the disable MUST be documented).
  3. New/changed controllers, services, and domain methods carry unit tests per Principle II.
  4. No remaining `@MockBean` (must be `@MockitoBean`); no `com.fasterxml.jackson...` imports.
  5. PR description names the principle each non-trivial decision is justified by, or the
     `Complexity Tracking` table entry justifying a deviation.
- **Refactoring tools**: Use JetBrains MCP tools (`reformat_file`, `rename_refactoring`,
  `search_symbol`, `get_symbol_info`) for cross-file refactors and import optimization, per
  the user's global instructions.
- **Documentation lookups**: JavaDoc Central MCP for Spring / Jackson / Maven Central JVM
  artifacts; Context7 MCP for non-JVM (HTMX, Tailwind, Alpine.js, etc.).
- **Verification before "done"**: A task is complete only when its evidence (test output, log,
  or demonstrable behavior) is in hand. "Should work" is not "done."

## Governance

- This constitution supersedes ad-hoc style decisions within the rest-mvc module. Where it
  conflicts with `rest-mvc/CLAUDE.md` or the root `CLAUDE.md`, the constitution wins and the
  CLAUDE.md MUST be updated to match.
- **Amendment procedure**:
  1. Open a PR that edits `.specify/memory/constitution.md` with a Sync Impact Report block at
     the top describing the version bump, modified principles, and templates affected.
  2. Update every dependent template flagged in the report (or justify the deferral).
  3. Update `rest-mvc/CLAUDE.md` if any principle's "MUST" rules change.
- **Versioning policy** (semantic):
  - **MAJOR**: Removing a principle, redefining one in a backward-incompatible way, or
    restructuring governance.
  - **MINOR**: Adding a principle or materially expanding a principle's MUST/SHOULD rules.
  - **PATCH**: Wording clarifications, typo fixes, non-semantic refinements.
- **Compliance review**: Every PR reviewer MUST confirm the four core principles are upheld.
  A "complexity-justified" deviation MUST appear in the PR's `Complexity Tracking` table
  (per the plan template) with the simpler alternative explicitly rejected.
- **Runtime guidance**: For day-to-day development, consult
  [`rest-mvc/CLAUDE.md`](../../CLAUDE.md), [`java-guidelines`](../../../.junie/java-guidelines.md),
  and [`spring-boot-guidelines`](../../../.junie/spring-boot-guidelines.md). The constitution
  is the authority; the guidelines are the elaboration.

**Version**: 1.0.0 | **Ratified**: 2026-05-09 | **Last Amended**: 2026-05-09
