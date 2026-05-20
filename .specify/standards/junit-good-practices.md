//: .specify/standards/junit-good-practices.md

# JUnit (Jupiter) — Good Practices

> Canonical elaboration of **Principle II — Unit-Test Discipline** of `.specify/templates/constitution-template.md`.
>
> **Target stack**: Spring Boot 4.x on Java 25. Jupiter version is managed by the Boot BOM — currently `6.0.3` (`spring-boot-dependencies-4.0.6.pom` → `junit-jupiter.version`, imports `org.junit:junit-bom`). No override in the root `pom.xml` is required.
>
> **Audience**: contributors writing or reviewing tests in any module of this repo.

---

## 1. Method-parameter injection

Prefer parameter injection over static helpers, instance fields, or `@BeforeEach`-only setup.

- **Built-in resolvers**:
  - `TestInfo` — display name, tags, test class/method metadata.
  - `TestReporter` — publish key-value entries to the test report.
  - `RepetitionInfo` — only inside `@RepeatedTest` bodies.
  - `@TempDir Path` / `@TempDir File` — auto-created, auto-cleaned scratch directory.
- **Spring**: `SpringExtension` (transitively activated by `@WebMvcTest`, `@DataJdbcTest`, `@SpringBootTest`) injects beans straight into `@Test`-method parameters — prefer this over field `@Autowired` for one-off collaborators.
- **Custom `ParameterResolver`**: write one only when it removes setup duplication across **many** tests; a single ad-hoc fixture stays a local variable.

```java
@Test
void writes_invoice(@TempDir Path dir, TestInfo info) {
    // 'dir' is unique per test method; cleaned up after the test
    // 'info' carries the display name for log lines
}
```

---

## 2. Parameterized tests

Prefer `@ParameterizedTest` over hand-rolled loops, `@RepeatedTest`, or copy-paste cases.

- **Sources**: `@ValueSource` (single arg), `@CsvSource` / `@CsvFileSource`, `@EnumSource`, `@FieldSource`, `@MethodSource`.
- **Named cases**: use `Arguments.argumentSet("label", a, b)` (or wrap individual args in `Named.of("label", value)`) so failure output reads `[1] rejects empty UPC` instead of `[1] "", BeerStyle.IPA`.
- **`@ParameterizedClass`** (new in JUnit 6 / backported to 5.13+): parameterize an entire test class when many methods share the same inputs — eliminates per-method repetition.
- **Non-static `@MethodSource`** is legal **only** under `@TestInstance(Lifecycle.PER_CLASS)`. Default `PER_METHOD` requires static factories.

```java
@ParameterizedTest(name = "{0}")
@MethodSource
void rejects_invalid_upc(String label, String upc) {
    assertThatThrownBy(() -> Beer.of(upc)).isInstanceOf(IllegalArgumentException.class);
}

static Stream<Arguments> rejects_invalid_upc() {
    return Stream.of(
        argumentSet("empty",        ""),
        argumentSet("blank",        "   "),
        argumentSet("non-numeric",  "abc"));
}
```

---

## 3. `@Nested` for scenario grouping

Use `@Nested` inner classes to group tests that share **fixture** or **scenario context** — not to mirror the SUT's package structure.

- Group by precondition: `WhenBeerExists`, `WhenBeerMissing`, `GivenInvalidPayload`.
- Apply `@DisplayName` per nest so the report reads like prose.
- **Hard cap: depth ≤ 2**. Deeper nesting hides setup and slows comprehension.
- Inner class is non-static; it sees the outer class's `@BeforeEach` fixture.

```java
@DisplayName("BeerService")
class BeerServiceTest {

    @Nested
    @DisplayName("when the beer exists")
    class WhenBeerExists {
        // shared given() / setup in outer @BeforeEach
        @Test void returns_the_beer() { ... }
        @Test void increments_version_on_update() { ... }
    }

    @Nested
    @DisplayName("when the beer is missing")
    class WhenBeerMissing {
        @Test void throws_NotFoundException() { ... }
    }
}
```

---

## 4. Lifecycle — `@TestInstance`

- **Default `PER_METHOD`** is right almost always. Switch to `@TestInstance(Lifecycle.PER_CLASS)` **only** when you need:
  - Non-static `@BeforeAll` / `@AfterAll`.
  - Non-static `@MethodSource` / `@FieldSource` factories (closure over instance state).
- Prefer `@BeforeEach` + local variables over shared mutable fields — fewer flaky-order surprises.
- Never mutate shared static state from a test; if you must, restore it in `@AfterEach`.

---

## 5. Assertions

This project standardizes on **AssertJ** for assertion style; the items below cover JUnit's own helpers where they add value.

- **`assertAll(...)`**: surface every failure of one logical outcome in a single run — useful when verifying multiple fields of one returned object. Do not use it as a catch-all wrapper around an entire test method.
- **`assertThrows(...)`** returns the thrown exception. Assert the message / cause via AssertJ for a richer diff:
  ```java
  IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> Beer.of(""));
  assertThat(ex).hasMessageContaining("upc must not be blank");
  ```
- Prefer AssertJ `assertThat` for everything else; reserve raw `assertEquals` for trivial primitive checks.

---

## 6. Mockito `ArgumentCaptor` (cross-reference)

Captor practice is anchored in **Principle II** of the constitution. This doc only adds:

- Prefer `@Captor` field over inline `ArgumentCaptor.forClass(...)` — generics are cleaner, less casting.
- Pair with BDDMockito (the project convention): `then(repo).should().save(captor.capture()); assertThat(captor.getValue())…`.
- Don't over-capture: when `eq(...)` suffices to express the expected argument, use it instead of capturing-then-asserting.

---

## 7. Display names — once, consistently

Project convention (enforced by `Code Standards` in the constitution):

- `@DisplayName` + `@DisplayNameGeneration(ReplaceUnderscores.class)` on every test class.
- Method names use snake-style underscores; the generator converts them to readable display names automatically.
- Do **not** mix free-form per-method `@DisplayName` annotations inside such classes unless the underscored form is genuinely ambiguous (rare).

---

## 8. Other annotations worth reaching for

| Annotation                                       | Use it for                                                                                                  |
|--------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| `@TempDir`                                       | File I/O tests — auto-cleanup beats manual `tearDown`.                                                      |
| `@TestFactory`                                   | Runtime-generated dynamic tests when parameterized sources can't express the cases.                         |
| `@EnabledIfEnvironmentVariable` / `@EnabledOnOs` | Environment-driven skips — preferred over `Assumptions.assumeTrue`.                                         |
| `@Disabled`                                      | Allowed only with a linked ticket / reason in the annotation `value`. No orphan disables.                   |
| `@Timeout`                                       | Guard against tests that block forever; one second is generally too tight, ten seconds is generally enough. |

---

## 9. What's new in JUnit 6 vs JUnit 5

| Item                                    | Note                                                                                                                                                                            |
|-----------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Java runtime baseline                   | Java 17+ (this repo runs on Java 25, comfortably above).                                                                                                                        |
| `@ParameterizedClass`                   | GA in 6; also backported to 5.13.                                                                                                                                               |
| Kotlin support                          | Polished — assertion helpers, default-argument-friendly parameterized sources.                                                                                                  |
| JSpecify annotations                    | Public API is JSpecify-annotated, so `@NullMarked` packages see correct nullability.                                                                                            |
| Extension model                         | **No breaking changes**. Existing `@ExtendWith(MockitoExtension.class)` and Spring Boot 4 slice annotations (`@WebMvcTest`, `@DataJdbcTest`, `@SpringBootTest`) work unchanged. |

---

## 10. References

- Constitution: [`../templates/constitution-template.md`](../templates/constitution-template.md) — Principle II (Unit-Test Discipline).
- Spring Boot testing idioms: [`../../.claude/guidelines/spring-boot-guidelines.md`](../../.claude/guidelines/spring-boot-guidelines.md) — Testing section.
- Java testing standards: [`../../.claude/guidelines/java-test-guidelines.md`](../../.claude/guidelines/java-test-guidelines.md).
- Per-module conventions: [`../../rest-mvc/CLAUDE.md`](../../rest-mvc/CLAUDE.md) — controller-slice / service-unit / `@DataJdbcTest` patterns.
