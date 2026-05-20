<!--
SYNC IMPACT REPORT
==================
Version change: (template) → <MAJOR.MINOR.PATCH>
Bump rationale: <fill in on ratification — e.g. "Initial ratification — populating
                placeholder template with concrete principles for <PROJECT_NAME>.">

Modified principles:
  - <list any principles added / renamed / removed vs the previous version>

Added sections:
  - <list>

Removed sections:
  - <list>

Templates requiring updates:
  - ⚠ .specify/templates/plan-template.md          (verify Constitution Check gates align)
  - ⚠ .specify/templates/spec-template.md          (verify no constitution-driven sections changed)
  - ⚠ .specify/templates/tasks-template.md         (verify test-discipline overrides intact)
  - ⚠ .specify/standards/junit-good-practices.md  (elaboration of Principle II — keep idioms current)
  - ⚠ CLAUDE.md (root and per-module)              (align MUST rules)
  - ⚠ .claude/guidelines/java-guidelines.md        (cross-reference target for Modern Java)
  - ⚠ .claude/guidelines/spring-boot-guidelines.md (cross-reference target for Boot idioms)
  - ⚠ .claude/guidelines/java-test-guidelines.md   (cross-reference target for Java testing idioms)

Deferred / TODOs:
  - <list, or "None">
-->

# <PROJECT_NAME> Constitution

## Project Identity
- **Name**: <PROJECT_NAME>
- **Purpose**: <one sentence — what this service does and why it exists>
- **Primary Users**: <who calls this API — internal services, mobile clients, partner systems>
- **Business Domain**: <domain — e.g. ordering, billing, catalog>
- **Dual Purpose**: 
  - This project is BOTH a production-quality backend AND a learning artifact for modern Java, Spring Boot 4 / Spring Framework 7, Spring Data JDBC, and the wider JVM ecosystem. 
  - Pedagogical clarity and idiomatic use of new features are first-class concerns, not afterthoughts.

---

## Core Principles

### I. Learning-First, Production-Quality (NON-NEGOTIABLE)

The codebase is also a teaching artifact. Every commit must be readable by a learner.

- Prefer the **newest idiomatic API** over the older one when both work 
  - records over POJOs, virtual threads over thread pools, `RestClient` over `RestTemplate`, `Stream.toList()` over `Collectors.toList()`. 
  - The point of the project is partly to **demonstrate** the modern idiom.
- When a feature has a "classic" and a "modern" form, the modern form MUST be chosen unless the modern form is genuinely worse for the task — in which case the PR description states why.
- A non-trivial new feature (sealed type, virtual thread, structured concurrency, scoped value, gatherer, pattern-matching switch, Spring Modulith module, Spring AI integration, etc.) MUST be accompanied by either (a) a unit test that exercises the feature, or (b) a note in the module's `CLAUDE.md` describing what is being demonstrated.
- Speculative generality is forbidden: a learning artifact is most teachable when each layer of indirection is justified. Three similar lines beat a premature abstraction.

**Rationale**: A learner reading this project must be able to trace request → controller → service → aggregate → DB with no detours, while also seeing how modern Java/Boot/Framework idioms are wired in. Both goals fail if either is neglected.

### II. Unit-Test Discipline (NON-NEGOTIABLE)

Three layers MUST have unit tests; integration tests are additive, not substitutive.

- **Domain logic** (records' invariants, `updateWith` / `patchWith`, enum mappings, value-object equality): pure JUnit, no Spring context, no mocks.
- **Application services** (`*ServiceImpl`): `@ExtendWith(MockitoExtension.class)` + `@Mock` repository + `@InjectMocks` impl; assert behavior via `BDDMockito.given(...)` / `then(mock).should(...)`; use `ArgumentCaptor` to verify what the service hands the repository.
- **REST controllers**: `@WebMvcTest(XController.class)` + `@MockitoBean` for the service; assert status, headers (e.g. `Location` on 201), and JSON shape via `MockMvc`. 
  - Import `@WebMvcTest` from `org.springframework.boot.webmvc.test.autoconfigure` (Spring Boot 4 path), **not** the legacy autoconfigure package.
- **Repository slices**: `@DataJdbcTest` + Zonky `embedded-database-spring-test` for slice tests; Testcontainers (`postgres:16`) for end-to-end ITs.
- A controller, service, or non-trivial domain method MUST NOT merge without a unit test covering its happy path AND at least one error/edge case.
- A task is not "done" until its tests run green locally via `mvn test -pl <module>` (and `mvn verify -pl <module>` for IT-touching changes).
- Prefer `@ParameterizedTest` + `@MethodSource` / `@CsvSource` / `@EnumSource` / `@FieldSource` over hand-rolled loops or `@RepeatedTest`. See [JUnit 6 Good Practices](../standards/junit-good-practices.md) for the full set of recommended Jupiter idioms.

**Elaboration**: [`.specify/standards/junit-good-practices.md`](../standards/junit-good-practices.md) — concrete Jupiter idioms (parameter injection, `@Nested`, named parameterized cases, `@TestInstance`, `@TempDir`, `@TestFactory`, conditional execution).

**Rationale**: 
- Each layer is independently teachable only if it is independently verifiable. 
- Skipping a layer's unit tests collapses the layer boundary the project is trying to demonstrate.

### III. Modern Java Practice (JDK 25-First)

See [`Modern Java Practice`](#modern-java-practice-jdk-25-first) below for the full table. 
- As a principle: JDK 8 idioms are retired except where the JVM library itself still exposes them; preview features are opt-in per module and never on by default in `main`.

### IV. Spring Boot 4 Idiomatic Practice

- HTTP semantics: `POST` → 201 + `Location` header; `PUT` / `PATCH` / `DELETE` → 204 No Content; errors → RFC 7807 `ProblemDetail`.
- Persistence: **Spring Data JDBC** (NOT Spring JDBC — they are different) against PostgreSQL 16; repositories extend `ListCrudRepository<E, ID>`; aggregate-root-per-table, no nested one-to-many that crosses aggregate boundaries; no custom `@Query` unless `ListCrudRepository` is insufficient.
- IDs: `UUID` defaulted at the DB (`gen_random_uuid()`) for app-keyed domains; `BIGSERIAL` / `Long` for auto-increment domains. Never generate UUIDs in application code for UUID domains.
- Service layer: interface + package-private `*Impl` colocated in the same `.java` file.
- JSON: Spring Boot 4 ships **Jackson 3** — import `tools.jackson.databind.*`; the Jackson 2 (`com.fasterxml.jackson.*`) namespace is forbidden except in explicit cross-version compatibility demos.
- Mocking: `@MockitoBean` (not the deprecated `@MockBean`).
- HTTP outbound: `RestClient` or `HttpInterface` — never `RestTemplate` in new code.
- Concurrency: `spring.threads.virtual.enabled=true`; request threads are virtual.
- Documentation lookups for Spring / Jackson / Spring Data MUST use the JavaDoc Central MCP (`https://www.javadocs.dev/mcp`) before relying on training-data recall.

**Rationale**: Boot 4 changed the Jackson namespace, the test slice package paths, the mocking annotation, and the recommended HTTP client. Cargo-culting Boot 3 idioms produces silently broken or out-of-date examples.

---

## Technical Stack — Non-Negotiable
- **Language**: Java 25 (LTS) — records, sealed types, pattern matching, virtual threads, structured concurrency, scoped values, stream gatherers all available by default.
- **Framework / Platform**: Spring Boot 4.x on Spring Framework 7.
- **Build Tool**: Maven 3.9.x (latest); Maven Wrapper committed; single `pom.xml` per module; root `pom.xml` aligned to the Spring Boot 4 BOM.
- **Frontend**: N/A — backend-only HTTP service. 
  - Clients are downstream services, CLIs, or OpenAPI-generated SDKs. 
  - If a UI is later added, it lives in a separate module.
- **Web Layer**: 
  - Spring MVC (servlet stack) on embedded Tomcat 11; 
  - virtual-thread executor (`spring.threads.virtual.enabled=true`); 
  - JSON via Jackson 3 (`tools.jackson.databind.*`).
- **Persistence Layer**: 
  - Spring Data JDBC against PostgreSQL 16; `ListCrudRepository<E, ID>`; 
  - aggregate-root-per-table; schema managed by Flyway (`db/migration/Vyyyymmdd__*.sql`); 
  - `spring.sql.init.mode=never` in non-dev profiles.
- **Database**: PostgreSQL 16 (UUID via `gen_random_uuid()` for app-keyed domains, `BIGSERIAL` for auto-increment); single logical DB per service.
- **Security**: Spring Security 7 — OAuth2 Resource Server (JWT) on every non-public endpoint; CSRF disabled only for stateless APIs; `BCryptPasswordEncoder` (cost ≥ 12); secrets from environment variables or a secret manager — never from `application.yml`.
- **Observability**: Spring Boot Actuator + Micrometer; metrics → Prometheus; traces → OpenTelemetry exporter; structured JSON logs via Logback `JsonEncoder` with `traceId` / `spanId` MDC keys.
- **Testing**: 
  - JUnit 6 (Jupiter — version managed by the Spring Boot 4.x BOM, currently `6.0.3` via `spring-boot-dependencies`), 
  - Mockito with BDD style (`given(...)`, `then(...).should(...)`), 
  - AssertJ for assertions; 
  - Spring Boot 4 slice tests (`@WebMvcTest`, `@DataJdbcTest`); 
  - Zonky `embedded-database-spring-test` for repository slices; 
  - Testcontainers (`postgres:16`) for full IT suite (`*IT.java`, Failsafe phase). 
  - Concrete Jupiter idioms live in [`.specify/standards/junit-good-practices.md`](../standards/junit-good-practices.md).

--- 

## Modern Java Practice (JDK 25-first)

Code MUST use idioms from the post-JDK-8 era. JDK 8 patterns are retired except where the JVM library itself still exposes them.

### Use (preferred / required)

| Feature                                                                           | Since     | Use for                                                                                                      |
|-----------------------------------------------------------------------------------|-----------|--------------------------------------------------------------------------------------------------------------|
| `var` local-variable inference                                                    | JDK 10    | Locals where the RHS makes the type obvious; never at API boundaries.                                        |
| Records                                                                           | JDK 16    | DTOs, value objects, domain models, `@ConfigurationProperties`, events.                                      |
| Sealed classes / interfaces                                                       | JDK 17    | Closed type hierarchies (command/result, state machines, ADTs).                                              |
| Pattern matching for `instanceof`                                                 | JDK 16    | Replace cast-after-check chains.                                                                             |
| Pattern matching for `switch` + records (deconstruction)                          | JDK 21    | Exhaustive dispatch over sealed hierarchies; record deconstruction.                                          |
| Text blocks (`"""..."""`)                                                         | JDK 15    | Multi-line SQL, JSON literals in tests, HTML fragments.                                                      |
| `List.of` / `Map.of` / `Set.of`                                                   | JDK 9     | Small immutable collections — never `Arrays.asList(...)` or double-brace init.                               |
| `Stream.toList()` (immutable)                                                     | JDK 16    | Default terminal op; `Collectors.toUnmodifiableList()` only when an API requires a `Collector`.              |
| `Optional.isEmpty()`, `Optional.or`, `Optional.stream`                            | JDK 11+   | Replace `!opt.isPresent()` and nested `flatMap` chains.                                                      |
| `HttpClient` (`java.net.http`)                                                    | JDK 11    | Outbound HTTP in non-Spring utility code; in Spring code, use `RestClient` / `HttpInterface`.                |
| `Files.readString` / `Files.writeString`                                          | JDK 11    | Small file I/O — never `BufferedReader` boilerplate for the simple case.                                     |
| Virtual threads                                                                   | JDK 21    | Request-handling and I/O fan-out. Boot 4 enables them via `spring.threads.virtual.enabled=true`.             |
| Structured concurrency (`StructuredTaskScope`)                                    | JDK 25    | Fan-out + join with cancellation propagation; replaces `CompletableFuture.allOf` ceremony.                   |
| Scoped values (`ScopedValue`)                                                     | JDK 25    | Per-request immutable context (correlation IDs, principals) — replaces `ThreadLocal` in virtual-thread code. |
| Stream gatherers (`Stream.gather`, `Gatherers.*`)                                 | JDK 24    | Custom stream operations (windowing, fold, scan) — replaces hand-rolled reducers.                            |
| Sequenced collections (`SequencedCollection`, `getFirst`/`getLast`, `reversed()`) | JDK 21    | Use over `list.get(list.size() - 1)` and friends.                                                            |
| `Optional.orElseThrow()` overloads                                                | JDK 10    | Replace `.orElseThrow(() -> new X())` when a no-arg form works.                                              |

### *Retire (forbidden in new code)*

| Legacy idiom                                                   | Replace with                                                                                                                                     |
|----------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| Anonymous inner class as a `Runnable` / listener               | Lambda or method reference.                                                                                                                      |
| `for (int i = 0; i < list.size(); i++)` over a `List`          | Enhanced `for`, stream, or `forEach` — index loops only when the index is actually used.                                                         |
| `new ArrayList<String>()` (raw or diamondless)                 | `new ArrayList<>()` or `List.of(...)`.                                                                                                           |
| `Date`, `Calendar`, `SimpleDateFormat`                         | `java.time.*` (`Instant`, `LocalDate`, `OffsetDateTime`, `DateTimeFormatter`); persist `Instant` / `OffsetDateTime` to PostgreSQL `TIMESTAMPTZ`. |
| Bean classes with getters/setters as domain models             | Records (immutable) + behavior methods.                                                                                                          |
| `null` to signal absence                                       | `Optional` return; jspecify `@Nullable` only where genuinely nullable.                                                                           |
| Checked-exception laundering (`throw new RuntimeException(e)`) | Domain exception with cause, or `UncheckedIOException` when wrapping `IOException` is genuinely intended.                                        |
| `ThreadLocal` for request context                              | `ScopedValue` (JDK 25) in virtual-thread code.                                                                                                   |
| `synchronized` on long-held monitors in request threads        | Virtual threads + bounded `Semaphore` or `ReentrantLock` with `tryLock(timeout)`.                                                                |
| Reflection-based copy / equality / toString                    | Records (auto-generated) or Lombok `@Builder`.                                                                                                   |
| `Stream.collect(Collectors.toList())`                          | `Stream.toList()` (immutable, JDK 16+).                                                                                                          |
| `URL` / `URLConnection` for HTTP                               | `java.net.http.HttpClient` or Spring's `RestClient`.                                                                                             |
| `RestTemplate`                                                 | `RestClient` (Boot 4) or `HttpInterface` (declarative client).                                                                                   |
| Field injection (`@Autowired` on a field)                      | Constructor injection (record-style services or `@RequiredArgsConstructor`).                                                                     |
| `@MockBean`                                                    | `@MockitoBean` (Boot 4).                                                                                                                         |
| `com.fasterxml.jackson.*` imports                              | `tools.jackson.databind.*` (Jackson 3 ships with Boot 4).                                                                                        |

### Enforcement

- **Compiler flags**: `-Xlint:all -Werror` in CI; preview features (`--enable-preview`) are opt-in per module and never on by default in `main`.
- **Static checks**: ErrorProne + Refaster rules pin the retirements above; violations fail the build.
- **Code review**: a reviewer who sees a retired idiom in a diff MUST request a change — "the file already used it" is not a justification.

--- 

## Quality Requirements

- **Test Coverage**: 
  - ≥ 80 % line coverage on new / changed code (JaCoCo); 
  - 100 % coverage of `*ServiceImpl` happy + edge paths is the target, not the ceiling. 
  - Generated code (MapStruct, Lombok, optics processors) is excluded.
- **Test Pyramid**: 
  - ≥ 70 % unit, ≤ 25 % slice, ≤ 5 % full-stack IT. 
  - A PR that adds only an IT to cover a unit-testable concern is rejected.
- **Performance**:
  - P95 API response time < 200 ms for read endpoints, < 400 ms for write endpoints (measured on the staging baseline workload).
  - P99 < 800 ms; sustained throughput ≥ 200 RPS per pod on the reference instance.
  - Cold start (JVM up → first request served) < 10 s; CDS / AOT enabled where supported.
- **Reliability**: No unbounded I/O — every external call has a timeout (connect 2 s, read 5 s default) and a retry policy via `spring-retry` or `org.springframework.core.retry`. Idempotency keys required on every mutating endpoint that may be re-driven.
- **Security**:
  - All endpoints authenticated by default; `permitAll()` requires a one-line justification in the security config.
  - Input validation via Jakarta Bean Validation (`@Valid` on every `@RequestBody` / `@PathVariable` / `@RequestParam` boundary); failures → 400 with an RFC 7807 `ProblemDetail` body.
  - Dependency scan on every CI run (OWASP Dependency-Check or Trivy); HIGH / CRITICAL CVEs block merge.
  - OWASP Top 10 checklist enforced in code review (SQLi guarded by parameterized queries, SSRF blocked by outbound allowlists, deserialization restricted to known types).
  - PII never logged at INFO or below; at DEBUG only with explicit masking.

---

## Code Standards

- **File header**: every `.java` file's line 1 is `//: <fully.qualified.ClassName>.java`.
- **Records over classes** for DTOs, value objects, domain models, configuration properties (`@ConfigurationProperties`).
- **`var`** for local inference where the RHS makes the type obvious; explicit types at public API boundaries.
- **Null-safety**: `@NullMarked` (jspecify) at the package level; `Optional` only as a return type for absence — never as a parameter, field, or collection element.
- **Lombok** is restricted to `@RequiredArgsConstructor`, `@Slf4j`, `@Builder`; no `@Data`, no `@AllArgsConstructor` on records, no `@SneakyThrows`.
- **Comments are sparing**: present only when the *why* is non-derivable from the code; never narrate *what* the code does. No `// TODO` without an owner and a ticket reference.
- **Naming**: classes `PascalCase`, methods/fields `camelCase`, constants `UPPER_SNAKE`, packages `lowercase.singular.noun`. Test classes end in `Test` (unit / slice) or `IT` (integration).
- **HTTP semantics**: `POST` → 201 + `Location` header; `PUT` / `PATCH` / `DELETE` → 204 No Content; errors → RFC 7807 `ProblemDetail`.
- **Service layer pattern**: interface + package-private `*Impl` colocated in the same `.java` file.
- **Spring config**: constructor injection only (no `@Autowired` field injection); `@ConfigurationProperties` records over `@Value`; profiles named `dev`, `test`, `staging`, `prod` — no others.
- **Imports**: no wildcard imports; reformat via IDE before commit (the JetBrains MCP `reformat_file` tool optimizes imports automatically).
- **Static analysis**: Spotless (Palantir Java Format), Checkstyle, ErrorProne all run in CI on the `verify` phase; warnings-as-errors.
- **`@DisplayName` + `@DisplayNameGeneration(ReplaceUnderscores.class)`** on every test class.

---

## Project Structure — Domain-Driven Design

The codebase is organized by **bounded context**, not by technical layer at the top level. Each bounded context is a self-contained module; framework concerns live inside it, not above it.

### Top-level layout

```
src/main/java/<base.package>/
├── <ProjectName>Application.java           # @SpringBootApplication only — no business code
├── shared/                                 # shared kernel: cross-context VOs, common types,
│   │                                       # base classes — kept deliberately small
│   ├── domain/                             # e.g. Money, EmailAddress, Identifier<T>
│   └── infrastructure/                     # cross-cutting tech (config, error handling,
│                                           # security filters, ProblemDetail mapper)
└── <context-1>/                            # one package per bounded context (ordering,
    │                                       # billing, catalog, …). Package-private by default.
    ├── package-info.java                   # @NullMarked + @ApplicationModule (Spring Modulith)
    ├── web/                                # inbound adapter: REST controllers, request/response
    │                                       # DTOs, exception handlers scoped to this context
    ├── application/                        # use-case services (the public API of the context);
    │                                       # orchestrates domain + infrastructure; thin
    ├── domain/                             # the model — pure, framework-free where possible
    │   ├── model/                          # aggregate roots, entities, value objects (records)
    │   ├── event/                          # domain events (records published via
    │   │                                   # ApplicationEventPublisher)
    │   └── <ContextName>Repository.java    # repository interface — owned by the domain,
    │                                       # extends ListCrudRepository<Aggregate, ID>
    └── infrastructure/                     # outbound adapters: JDBC repository impls only when
                                            # a query exceeds ListCrudRepository, external
                                            # clients (RestClient), messaging
```

### Bounded-context rules

- **One context per top-level package**; 
  - a context owns its DB tables, its REST paths (`/api/v1/<context>/…`), and its domain events. 
  - No table is shared across contexts.
- **Public API of a context = its `application/` services + published events**. 
  - Everything else (`domain/`, `infrastructure/`, `web/`) is package-private unless Spring requires otherwise. 
  - Enforce via Spring Modulith (`@ApplicationModule`) + ArchUnit tests.
- **No cross-context imports of `domain/` or `infrastructure/`**. Contexts collaborate via:
  1. The other context's `application/` service (synchronous), or
  2. Domain events (asynchronous, preferred for write-write coupling).
- **Anti-corruption layer**: 
  - when consuming another context's model, translate at the boundary into this context's vocabulary 
    - never leak a foreign aggregate inward.

### DDD building blocks → Spring Data JDBC mapping

| DDD Concept                      | Implementation in this Stack                                                                                                                         |
|----------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Aggregate Root**               | Record annotated `@Table`, `@Id` on the identifier; one row per aggregate root.                                                                      |
| **Aggregate**                    | The root + its value objects (`@Embedded`) or a set of `@MappedCollection` children — always loaded / saved as a unit through the root's repository. |
| **Entity** (non-root)            | Reachable only through its aggregate root; never has a top-level repository.                                                                         |
| **Value Object**                 | Immutable record; no `@Id`; equality by value. Persisted via `@Embedded` or as a column.                                                             |
| **Repository**                   | Interface in `domain/`, extends `ListCrudRepository<Aggregate, ID>`. One repository per aggregate root.                                              |
| **Domain Service**               | Stateless class in `domain/` for logic that doesn't naturally fit on an aggregate.                                                                   |
| **Application Service**          | Class in `application/`; `@Transactional` lives here, not on the controller; orchestrates aggregate + repository + events.                           |
| **Domain Event**                 | Record in `domain/event/`; published via `ApplicationEventPublisher` or registered on the aggregate (Spring Data JDBC `@DomainEvents`).              |
| **Factory**                      | Static factory method on the aggregate (`Order.place(...)`) — preferred over Factory classes for records.                                            |
| **Specification / Query Object** | Custom `@Query` method on the repository, named by intent (`findOpenOrdersFor(customerId)`), not by SQL shape.                                       |

### Hard Constraints

- **No anemic domain models**: 
  - behavior lives on the aggregate root 
  - application services orchestrate, they don't compute 
  - An aggregate with only getters and setters is a defect 
- **Aggregates enforce their own invariants**: every mutating method on an aggregate root must leave the aggregate in a valid state or throw a domain exception — no half-built state.
- **Identifiers are typed**: use a record-wrapper (`record OrderId(UUID value) {}`) rather than raw `UUID` / `Long` at API and method boundaries; raw IDs are permitted only at the DB column and JSON serialization edges.
- **Transactions span one aggregate**: an application service writes to a single aggregate per transaction. Multi-aggregate coordination uses domain events + eventual consistency.
- **No JPA-style lazy loading** — Spring Data JDBC loads aggregates eagerly and completely; if an aggregate is too big to load, it's too big and must be split.
- **ArchUnit tests** verify the package rules above and fail the build on violation. 
  - The tests live in `src/test/java/<base.package>/architecture/`.

---

## Boundaries

### Always Do
- Run `mvn verify` locally before committing (unit + slice + IT against Testcontainers).
- Add a Flyway migration for **any** schema change; never edit a committed migration.
- Log errors with full context (correlation ID, principal, request URI) to the monitoring sink.
- Update the OpenAPI spec (`springdoc-openapi`) when adding, removing, or changing an endpoint — contract tests assert it matches the previous published version.
- Use parameterized queries / JDBC templates — never string-concatenate SQL.
- Validate every external input at the controller boundary with Jakarta Bean Validation.
- Add a unit test for every new `*ServiceImpl` method (happy path + one error / edge case).
- Use `@MockitoBean` (not the deprecated `@MockBean`) and the Spring Boot 4 webmvc-test autoconfigure package (`org.springframework.boot.webmvc.test.autoconfigure`).
- Pin third-party container images by digest in Testcontainers and `docker-compose.yml`.
- Bump the constitution version (semver) when amending principles; record the Sync Impact Report at the top of the file.
- Cite the JDK / Boot / Framework version that introduced any non-trivial feature used (in `CLAUDE.md` or a code comment), so the learning-artifact value is preserved.

### Ask First
- Adding a new runtime dependency (license review + CVE check + bundle-size impact).
- Changing the database schema in a way that requires backfill or a multi-step migration.
- Modifying the authentication / authorization flow or token shape.
- Introducing async / messaging (Kafka, RabbitMQ, JMS) — a service that was synchronous becoming eventually consistent is an architectural decision.
- Adding a new bounded context or aggregate root.
- Disabling, ignoring, or marking `@Disabled` an existing test.
- Adding a `@ControllerAdvice` or custom exception handler that affects more than one controller.
- Adopting a new Spring sub-project (Spring Cloud, Spring Batch, Spring Integration, Spring AI, Spring Modulith, etc.).
- Adding a feature flag — the gate, removal plan, and owner must be documented.
- Exposing a public-facing (unauthenticated) endpoint.
- Enabling JDK preview features (`--enable-preview`) on a module.

### Never Do
- Commit secrets, API keys, JWT signing keys, DB passwords, or `.env` files. Pre-commit secret scanning is mandatory.
- Remove, `@Disabled`, or skip an existing test to make CI green without an approved follow-up ticket.
- Push to `main` directly — every change goes through a reviewed PR.
- Force-push to `main` or any release branch.
- Use `System.out` / `System.err` / `e.printStackTrace()` for logging.
- Catch `Throwable` or swallow exceptions without re-throwing or logging at ERROR.
- Use raw `RestTemplate` for new code — `RestClient` (Boot 4) or `HttpInterface` only.
- Import from `com.fasterxml.jackson.*` (Jackson 2) — Boot 4 ships Jackson 3 (`tools.jackson.databind.*`).
- Generate UUIDs in application code for DB-keyed UUID columns (the DB does it via `gen_random_uuid()`).
- Add `@Transactional` on controller methods; transactions belong on the application-service layer.
- Block on a `CompletableFuture` from a request thread (use `DeferredResult`, virtual threads, or `StructuredTaskScope`).
- Add nullable parameters / fields without `@Nullable`; default is non-null.
- Add `// commented-out code` — delete it. Git remembers.
- Use Spring JDBC's raw `JdbcTemplate` for repository code when Spring Data JDBC's `ListCrudRepository` suffices. 
  - The two are different — the project uses **Spring Data JDBC**.

---

## Architecture Principles

- **SOLID**: 
  - SRP per class; 
  - OCP via composition + interfaces; 
  - LSP enforced by the type system + jspecify; 
  - ISP — split interfaces when callers use a subset; 
  - DIP — depend on abstractions injected via constructors.
- **Object-Oriented**: 
  - encapsulate invariants in the type that owns them; expose behavior, not state; 
  - favor immutability (records, `List.copyOf`).
- **Data-Oriented**: 
  - prefer records + pattern-matching switches for transformations over visitor hierarchies; 
  - keep data and behavior separable when the data is the point.
- **Composition over inheritance**: 
  - no class hierarchy more than two levels deep without justification; 
  - sealed types only when the closed set is the design intent.
- **Component size**: 
  - production classes ≤ 300 lines; 
  - methods ≤ 40 lines; 
  - cyclomatic complexity ≤ 10 per method (enforced in CI). 
  - Larger means split.
- **Dependency injection**: 
  - through constructors only; 
  - no service locators, 
  - no static singletons, 
  - no `ApplicationContext` lookups in business code.
- **Hexagonal-lite layering**: 
  - `web` → `application` → `domain`; 
  - `infrastructure` implements ports declared in `domain`. 
  - The web layer never imports `infrastructure`; 
  - the domain layer never imports `web` or `infrastructure`.
- **Aggregate-root-per-table**: 
  - every `@Table`-annotated record is its own aggregate root; 
  - cross-aggregate references hold IDs, not nested objects.
- **Pure functions for domain logic**: 
  - side-effecting code is isolated in adapters; 
  - domain transformations (`updateWith`, `patchWith`, enum mappings) are pure and unit-tested without Spring.
- **Fail fast at boundaries**: 
  - validate inputs and throw at the edge; 
  - never propagate `null` deeper.
- **Idempotency by default** for mutating endpoints; 
  - replay-safe handlers everywhere a message queue could re-drive a command.
- **Versioned APIs**: 
  - every public path is prefixed `/api/v<N>/`; 
  - breaking changes bump the major version; 
  - deprecations carry a sunset date in the OpenAPI spec.
- **Configuration is code**: 
  - every tunable in `application.yml` has a `@ConfigurationProperties` record with validation; 
  - magic strings in `@Value` are banned.
- **Observability is a feature**, not an afterthought: 
  - every new endpoint emits a request timer, an error counter, and a trace span automatically via Boot Actuator + Micrometer + OpenTelemetry.

---

## Development Workflow & Quality Gates

- **Plan-First for non-trivial work**: 
  - any task spanning 3+ steps or introducing an architectural choice MUST produce a plan under `docs/dev/YYYY-MM-DD-hh-mm_plan-*.md` before implementation. 
  - Move the plan to `docs/dev/finished/` on completion.
- **Pre-merge gates** (every PR):
  1. `mvn test -pl <module>` passes.
  2. `mvn verify -pl <module>` passes (unit + slice + IT, given a running PostgreSQL / Testcontainers).
  3. New / changed controllers, services, and domain methods carry unit tests per Principle II.
  4. No remaining `@MockBean` (must be `@MockitoBean`); no `com.fasterxml.jackson...` imports; no `RestTemplate` in new code.
  5. ArchUnit suite is green.
  6. PR description names the principle each non-trivial decision is justified by, or the `Complexity Tracking` table entry justifying a deviation.
- **Refactoring tools**: 
  - use JetBrains MCP tools (`reformat_file`, `rename_refactoring`, `search_symbol`, `get_symbol_info`) for cross-file refactors and import optimization.
- **Documentation lookups**: 
  - JavaDoc Central MCP (`https://www.javadocs.dev/mcp`) for Spring / Jackson / Maven Central JVM artifacts; 
  - Context7 MCP for non-JVM (HTMX, Tailwind, Alpine.js).
- **Verification before "done"**: 
  - a task is complete only when its evidence (test output, log, or demonstrable behavior) is in hand. "Should work" is not "done."

---

## Governance

- This constitution supersedes ad-hoc style decisions across the project. 
  - Where it conflicts with `CLAUDE.md` (root or per-module) or the `.claude/guidelines/*.md` files, 
  - the constitution wins and the conflicting doc MUST be updated.
- **Amendment procedure**:
  1. Open a PR editing `.specify/memory/constitution.md` with a Sync Impact Report block at the top describing the version bump, modified principles, and dependent templates affected.
  2. Update every dependent template flagged in the report (or document the deferral).
  3. Update CLAUDE.md and guideline docs if any MUST rule changes.
- **Versioning** (semver):
  - **MAJOR**: removing a principle, redefining one backward-incompatibly, restructuring governance.
  - **MINOR**: adding a principle or materially expanding MUST / SHOULD rules.
  - **PATCH**: wording clarifications, typo fixes, non-semantic refinements.
- **Compliance review**: every PR reviewer MUST confirm the core principles are upheld. 
  - A "complexity-justified" deviation MUST appear in the PR's `Complexity Tracking` table (per the plan template) with the simpler alternative explicitly rejected.
- **Runtime guidance**: consult `CLAUDE.md`, `.claude/guidelines/java-guidelines.md`, `.claude/guidelines/spring-boot-guidelines.md`, `.claude/guidelines/java-test-guidelines.md`. The constitution is the authority; those are the elaboration.

**Version**: <MAJOR.MINOR.PATCH> | **Ratified**: <YYYY-MM-DD> | **Last Amended**: <YYYY-MM-DD>
