# Quickstart — Running the Persistence Slice

## Prerequisites

- JDK 25 on `PATH` (project Java baseline)
- Maven 3.9+
- **No PostgreSQL install required.** 
- **No `DATASOURCE_PASSWORD` required.**
- First run downloads / extracts the Zonky binary (~50–100 MB cached under `~/.embedpostgresql/`); subsequent runs reuse the cache.

## Run

```bash
# Both classes
mvn test -Dtest='BeerRepositoryTest,CustomerRepositoryTest'

# Just one
mvn test -Dtest=BeerRepositoryTest
```

Or from IntelliJ: right-click → Run, same as any JUnit class.

## Expected outcome

- Both classes green, **12 tests minimum** (6 per domain — see `contracts/repository-methods.md`).
- Wall-clock target (SC-002): 
  - well under the time `@SpringBootTest` needs to print its banner. 
  - Cold start may include a one-off Zonky binary launch (~3–5 s on a developer laptop); 
  - warm runs amortize that across all `@DataJdbcTest` classes in **the Same JVM.**

## Troubleshooting

- **`relation "beer" does not exist`** during a test — `src/test/resources/application.yml` is missing the `spring.sql.init.mode: never` override; 
  - Boot is trying to apply the real `src/main/resources/schema.sql` (which is TRUNCATE/VACUUM, not DDL) against the empty Zonky instance.
- **Port-in-use / "could not bind"** — Zonky picks an ephemeral port; 
  - if you see binding failures, kill stray `postgres` child processes from prior crashed runs 
  - (Windows: `taskkill /F /IM postgres.exe`; Linux/macOS: `pkill -f postgres`).
- **Slow first run** — expected (binary extraction). 
  - Speed up CI by mounting / caching `~/.embedpostgresql/`.
- **IT suite suddenly trying to run** 
  - these tests are `*Test.java`, not `*IT.java`, so Failsafe ignores them. 
  - `mvn verify` still kicks off the (disabled) `RestMvcApplicationIT`; 
  - that is pre-existing behavior unaffected by this feature.

## Where the tests live

```
src/test/java/spring/boot/sfg7/rest/mvc/domain/
├── beer/repository/BeerRepositoryTest.java
└── customer/repository/CustomerRepositoryTest.java
```

## Where the rest of the suite lives (untouched by this feature)

```
src/test/java/spring/boot/sfg7/rest/mvc/
├── RestMvcApplicationIT.java                            # @Disabled, pre-existing
├── domain/ocp/service/FlashcardServiceImplTest.java
└── web/controller/{BeerControllerTest, CustomerControllerTest, FlashcardControllerTest}.java
```
