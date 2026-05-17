---
description: "Task list — Fast Persistence-Slice Tests for Beer and Customer"
---

# Tasks: Fast Persistence-Slice Tests for Beer and Customer

**Input**: `specs/003-persistence-slice-tests/{spec.md, plan.md, research.md, data-model.md, contracts/, quickstart.md}`
**Tests**: This feature *is* tests. The "implementation" tasks below produce test files; there is no separate test-of-tests layer.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Different file, no incomplete dependencies
- **[Story]**: `US1` (P1) or `US2` (P2). Setup / Foundational / Polish have no story label.
- All paths absolute or repo-relative to `rest-mvc/`.

---

## Phase 1: Setup

**Purpose**: Add the one test-scope dependency and the test-classpath property override the slice needs (`spring.sql.init.mode: never`); the existing `banner-mode: off` is already in place.

No production changes.

- [X] T001 Add `io.zonky.test:embedded-database-spring-test:2.8.0` (test scope) to `rest-mvc/pom.xml`
- [X] T002 Append `spring.sql.init.mode: never` to the existing `rest-mvc/src/test/resources/application.yml` (file already exists with `spring.main.banner-mode: off` from commit 5dcdb4c). The `init.mode: never` prevents Boot from running the production `schema.sql` TRUNCATE/VACUUM against the empty Zonky instance — see research.md D-3.

**Checkpoint**: `mvn test -pl rest-mvc` still green; no new tests yet.

---

## Phase 2: Foundational

**Purpose**: Confirm prerequisites the slice will rely on. Read-only — no edits.

- [X] T003 Verify `rest-mvc/src/main/resources/schema_renew.sql` is the DROP+CREATE DDL (not TRUNCATE/VACUUM); confirm Beer / Customer columns match `data-model.md` (no schema change needed if so — flag a research deviation if not)

**Checkpoint**: DDL source-of-truth confirmed; tests can `@Sql("classpath:schema_renew.sql")` without redefining columns.

---

## Phase 3: User Story 1 — Verify service-called repository methods work against real PostgreSQL (Priority: P1) 🎯 MVP

**Goal**: `BeerRepository` and `CustomerRepository` are covered for every `ListCrudRepository` method the service layer calls today (`save`, `findAll`, `findById`, `deleteById`) on a real PostgreSQL engine, with no full Spring context boot.

**Independent Test**: `mvn test -pl rest-mvc -Dtest='BeerRepositoryTest,CustomerRepositoryTest'` — 14 tests green on a clean checkout with no PostgreSQL installed, no `DATASOURCE_PASSWORD` set.

### Implementation for User Story 1

- [X] T004 [P] [US1] Create `rest-mvc/src/test/java/spring/boot/sfg7/rest/mvc/domain/beer/repository/BeerRepositoryTest.java` — `@DataJdbcTest` + `@AutoConfigureEmbeddedDatabase(provider = EMBEDDED)` + `@Sql(scripts = "classpath:schema_renew.sql", executionPhase = BEFORE_TEST_METHOD)` + `@DisplayName` + `@DisplayNameGeneration(ReplaceUnderscores.class)`; inject `BeerRepository`; add 7 tests per `contracts/repository-methods.md`: `save_new_beer_assigns_uuid_and_round_trips`, `find_all_returns_persisted_rows`, `find_by_id_returns_existing`, `find_by_id_returns_empty_for_unknown_uuid`, `save_existing_id_updates_in_place_without_duplicating`, `save_with_explicit_unknown_uuid_pins_repository_contract` (pins spec edge-case 1: persist a Beer whose `id` is a freshly generated, never-persisted UUID — assert the actual Spring Data JDBC behavior: row count, returned `id`, and whether an exception is raised), `delete_by_id_removes_and_subsequent_find_returns_empty` (the last also one-liner-asserts `deleteById(unknownUuid)` does not throw)
- [X] T005 [P] [US1] Create `rest-mvc/src/test/java/spring/boot/sfg7/rest/mvc/domain/customer/repository/CustomerRepositoryTest.java` — same shape as T004; 7 tests `save_new_customer_assigns_uuid_and_round_trips`, `find_all_returns_persisted_rows`, `find_by_id_returns_existing`, `find_by_id_returns_empty_for_unknown_uuid`, `save_existing_id_updates_in_place_without_duplicating`, `save_with_explicit_unknown_uuid_pins_repository_contract`, `delete_by_id_removes_and_subsequent_find_returns_empty`
- [X] T006 [US1] Run `mvn test -pl rest-mvc -Dtest='BeerRepositoryTest,CustomerRepositoryTest'`; confirm all 14 tests pass on cold start (Zonky binary extraction allowed once) and on a warm re-run

**Checkpoint**: US1 fully functional. SC-001 / SC-003 (per matrix) / SC-005 met.

---

## Phase 4: User Story 2 — Schema-driven defaults and constraints are honored (Priority: P2)

**Goal**: The PostgreSQL-side behavior baked into `schema_renew.sql` (UUID default via `gen_random_uuid()`, `VARCHAR` caps, `TIMESTAMP` precision for `Instant` round-trip) is pinned by assertions, per research D-7's "augment positive-path tests" approach.

**Independent Test**: Same command as US1; with these augmentations in place, removing any of `gen_random_uuid()`, the `VARCHAR(50)` width of `beer_style`/`upc`, or the `TIMESTAMP` column types from the DDL makes the suite fail.

### Implementation for User Story 2

- [X] T007 [US2] Augment the `save_new_beer_assigns_uuid_and_round_trips` test in `rest-mvc/src/test/java/spring/boot/sfg7/rest/mvc/domain/beer/repository/BeerRepositoryTest.java` — persist `Beer` with `id == null`; assert returned `id` is non-null UUID (DB default fired); assert `beerStyle` enum value preserved verbatim; assert `upc` preserved; assert `createdDate` / `updateDate` `Instant` values round-trip through `TIMESTAMP` (use AssertJ `isCloseTo(..., within(Duration.ofMillis(1)))` to tolerate PG `TIMESTAMP` microsecond precision; do not over-assert)
- [X] T008 [US2] Augment the `save_new_customer_assigns_uuid_and_round_trips` test in `rest-mvc/src/test/java/spring/boot/sfg7/rest/mvc/domain/customer/repository/CustomerRepositoryTest.java` — persist `Customer` with `id == null`; assert DB-assigned UUID and `Instant ↔ TIMESTAMP` preservation for `createdDate` / `updateDate`
- [X] T009 [US2] Re-run `mvn test -pl rest-mvc -Dtest='BeerRepositoryTest,CustomerRepositoryTest'`; confirm augmented assertions pass

**Checkpoint**: US2 satisfied. SC-004 met (dialect / DDL regressions caught at this layer).

---

## Phase 5: Polish & Cross-Cutting Concerns

- [X] T010 [P] Run `mvn verify -pl rest-mvc` — confirm the new `*RepositoryTest` classes run in the Surefire phase, the (disabled) `RestMvcApplicationIT` is untouched, and no existing controller-slice / service-unit test regressed
- [X] T011 [P] Walk through `specs/003-persistence-slice-tests/quickstart.md` end-to-end on a warm cache (no external Postgres, no `DATASOURCE_PASSWORD`); confirm SC-002 ("feels instant" — well under `@SpringBootTest` banner time) holds
- [X] T012 [P] Open `rest-mvc/CLAUDE.md` and verify the existing reference to `specs/003-persistence-slice-tests/plan.md` is still accurate; no doc edit required by this feature beyond what plan.md already promised

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start immediately
- **Foundational (Phase 2)**: Depends on Setup; read-only verification
- **User Story 1 (Phase 3)**: Depends on Foundational — this is the MVP
- **User Story 2 (Phase 4)**: Depends on US1 (US2 augments US1's positive-path tests in-place — same files)
- **Polish (Phase 5)**: Depends on US2

### User Story Dependencies

- **US1 (P1)**: After Foundational. Self-contained — green by itself.
- **US2 (P2)**: After US1. Hard dependency: US2 edits the same two test files US1 created (per research D-7, US2 lives inside US1's positive-path tests, not new test methods). US2 cannot run in parallel with US1.

### Within Each User Story

- US1: T004 and T005 are different files → can run in parallel. T006 runs after both.
- US2: T007 and T008 are different files → can run in parallel. T009 runs after both.

### Parallel Opportunities

- T004 ∥ T005 (different test classes)
- T007 ∥ T008 (different test classes)
- T010 ∥ T011 ∥ T012 (Polish — independent reads / runs)

---

## Parallel Example: User Story 1

```bash
# Author both test classes concurrently (different files, no shared state):
Task: "Create BeerRepositoryTest with 6 tests covering save / findAll / findById±/ save-update / deleteById±"
Task: "Create CustomerRepositoryTest with 6 tests covering save / findAll / findById±/ save-update / deleteById±"
```

---

## Implementation Strategy

### MVP First (US1 only)

1. Phase 1 Setup (T001, T002)
2. Phase 2 Foundational (T003)
3. Phase 3 US1 (T004 ∥ T005 → T006)
4. **STOP and VALIDATE** — 12 tests green; this satisfies SC-001 / SC-003 / SC-005 and is shippable

### Incremental Delivery

1. Setup + Foundational → ready
2. US1 → demo: "fast slice tests for the four service-called methods" (MVP)
3. US2 → demo: "DDL defaults / round-trip fidelity now pinned"
4. Polish → close out

### Parallel Team Strategy

- One developer can absorb all of US1 in a single session (two files, ~12 tests).
- US2 cannot be parallelized across developers — it edits the same files US1 created.

---

## Notes

- US2 augments existing US1 test bodies in-place, per research.md D-7. There are no new test methods for US2.
- `[P]` honored only across distinct files. T007 / T008 cannot run in parallel with T004 / T005 because they edit the same files (US2 builds on US1).
- All new tests live under `rest-mvc/src/test/java/spring/boot/sfg7/rest/mvc/domain/{beer,customer}/repository/`, mirroring the production package.
- File-header `//: …` comment, `@NullMarked`, `@DisplayName` + `ReplaceUnderscores`, AssertJ, BDDMockito — per `rest-mvc/CLAUDE.md` conventions.
- No production code changes anywhere in this feature.
