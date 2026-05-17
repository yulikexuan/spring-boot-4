# Phase 0 — Research

All decisions confirmed during planning (no `NEEDS CLARIFICATION` carryovers).

## D-1. Embedded Engine — Zonky Variant

- **Decision**: `io.zonky.test:embedded-database-spring-test` 
  - (Spring auto-config integration), used via `@AutoConfigureEmbeddedDatabase(provider = EMBEDDED)` (the constant is `DatabaseProvider.EMBEDDED`; the older `ZONKY` constant was renamed and is now `@Deprecated` — same backend, just a rename)
- **Rationale**: Zero-glue DataSource replacement on a `@DataJdbcTest` slice; refresh mode defaults to `BEFORE_CLASS` so every test class gets a clean PG instance. 
  - Binary is cached after the first launch.
- **Alternatives Rejected**:
  - Raw `io.zonky.test:embedded-postgres` — forces hand-rolled JUnit extension or `@BeforeAll` lifecycle + manual DataSource wiring. 
    - More code, no benefit on a slice test.
  - Testcontainers `PostgreSQLContainer` — requires Docker on every dev machine; 
    - violates FR-003 (no external service to install / start). Also, slower cold-start.
  - H2 / HSQLDB in PG-compat mode — rejected by spec (FR-002): 
    - Spring Data JDBC would resolve `H2Dialect` instead of `PostgresDialect`, 
    - losing the fidelity the suite is meant to guarantee.

**How the engine is selected (mechanism)**:

- The library does not *infer* PostgreSQL — the engine is chosen explicitly via the `provider` attribute of `@AutoConfigureEmbeddedDatabase`.
- Supported providers (all PostgreSQL — the library ships no H2/HSQLDB option):

  | `provider`   | Backing implementation                                    | Notes                                                         |
  |--------------|-----------------------------------------------------------|---------------------------------------------------------------|
  | `EMBEDDED`   | Zonky `embedded-postgres`                                 | What we use; in-process real PG (renamed from `ZONKY`)        |
  | `DOCKER`     | Testcontainers `PostgreSQLContainer` (library default)    | Requires Docker — rejected by FR-003                          |
  | `OPENTABLE`  | OpenTable `otj-pg-embedded`                               | Older lineage of Zonky's binary                               |
  | `ZONKY`      | (alias of `EMBEDDED`)                                     | `@Deprecated`, scheduled for removal next major — do not use  |
  | `YANDEX`     | Yandex `embedded-postgres-binaries`                       | `@Deprecated`, scheduled for removal next major               |

- At test-context refresh, `@AutoConfigureEmbeddedDatabase` registers a `BeanFactoryPostProcessor` that **replaces** Spring Boot's auto-configured `DataSource` with one backed by the chosen provider. From Spring Data JDBC's view the `DataSource` simply *is* PostgreSQL, so `DialectResolver` resolves `PostgresDialect` automatically — no extra wiring needed.
- The H2-in-PG-compat-mode path (the rejected alternative) is mutually exclusive with this library — to take that path you would not use `embedded-database-spring-test` at all; you would let Boot's own `DataSource` auto-config kick in with `org.h2:h2` on the test classpath.

**Version Pin**: 
- Use the latest `2.x` release of `embedded-database-spring-test` known to support Spring Framework 7 / Spring Boot 4. 
- *Verify on Javadoc Central before adding to `pom.xml`* 
- if no 4.x-compatible release exists yet, fall back to `io.zonky.test:embedded-postgres` (raw) behind a small `@RegisterExtension` and revisit Decision D-1 in the task phase. 
  - (Surface this as a research checkpoint, not a blocker.)

## D-2. DDL bootstrap mechanism

- **Decision**: `@Sql(scripts = "classpath:schema_renew.sql", executionPhase = BEFORE_TEST_METHOD)` on each new test class.
- **Rationale**: Spec FR-005 demands the production DDL be the source of truth. `@Sql` is the idiomatic Spring Test mechanism. `BEFORE_TEST_METHOD` ensures each test starts on a freshly DROP-and-CREATE'd table set, satisfying FR-006 (predictable state) without per-test boilerplate.
- **Alternatives rejected**:
  - `spring.sql.init.schema-locations` override — global to the test classpath; affects every future slice test, including ones that may want their own DDL. Implicit, not declarative.
  - JUnit extension that runs `schema_renew.sql` via `JdbcTemplate` in `@BeforeEach` — duplicates `@Sql`.

## D-3. Spring Boot auto SQL init (existing `schema.sql` / `data.sql`)

- **Decision**: Set `spring.sql.init.mode: never` in `src/test/resources/application.yml`.
- **Rationale**: The on-disk `src/main/resources/schema.sql` actually contains `TRUNCATE … RESTART IDENTITY CASCADE; VACUUM FULL …` (not the DDL — the file naming is inverted relative to the existing `rest-mvc/CLAUDE.md` description; the canonical DDL lives in `schema_renew.sql`). 
  - Running TRUNCATE against an empty Zonky instance fails with "relation does not exist". 
  - Disabling Boot's auto SQL init across the test classpath sidesteps this without touching production.
- **Out of scope for this feature, but worth flagging**: 
  - `rest-mvc/CLAUDE.md` describes the two SQL files in the opposite order from their actual content. 
  - Recommend updating that documentation in a separate change.

## D-4. Maven phase and naming

- **Decision**: Surefire phase, file naming `*RepositoryTest.java`.
- **Rationale**: Spec FR-009 demands independence from the existing `*IT` suite and the `DATASOURCE_PASSWORD` env var. 
  - SC-002 demands "instant"-feeling tests. 
  - Surefire keeps the slice runnable via plain `mvn test`. No changes to Surefire/Failsafe `<includes>` are needed — Surefire's default already picks up `*Test.java`.
- **Alternatives rejected**:
  - Failsafe (`*IT.java`) — pushes the slice into the same phase as the (disabled) `RestMvcApplicationIT`, blurring the "fast feedback" boundary.
  - Dual-phase via JUnit `@Tag` — extra config for no measurable benefit at two test classes.

## D-5. JUnit baseline

- **Decision**: JUnit Jupiter 6 only (the version shipped by `spring-boot-starter-test` in Boot 4.0.6).
- **Rationale**: All imports stay `org.junit.jupiter.api.*`; no vintage engine, no parallel APIs. 
  - Matches every existing test in the module.

## D-6. Refresh mode (Zonky)

- **Decision**: Default (`refresh = BEFORE_CLASS`).
- **Rationale**: One PG launch per test class is enough — `@Sql` re-runs the DDL between methods, so per-method DB refresh would be wasted I/O. 
  - Per-method refresh is available (`BEFORE_EACH_TEST_METHOD`) but not warranted at this scope.

## D-7. Coverage matrix (anchors to spec SC-003)

| Domain   | `save` (+) | `findAll` (+) | `findById` (+) | `findById` (−) | `deleteById` (+) | `deleteById` (−) |
|----------|------------|---------------|----------------|----------------|------------------|------------------|
| Beer     | required   | required      | required       | required       | required         | required         |
| Customer | required   | required      | required       | required       | required         | required         |

Augmenting cells (round-trip equality, UUID-default check, `Instant ↔ TIMESTAMP` preservation) live inside the relevant positive-path tests rather than dedicated extra cases — keeps the per-domain count at 6 minimums.

## D-8. What is explicitly **not** done

- No assertion on `version` columns (per spec assumption).
- No `count` / `existsById` / `findAllById` / `saveAll` / `deleteAll` tests (per FR-010).
- No Flashcard repository test.
- No changes to `BootstrapData`, controllers, services, mappers, or DTOs.
- No update to `rest-mvc/CLAUDE.md`'s schema description in this feature (flagged in D-3 as follow-up).
