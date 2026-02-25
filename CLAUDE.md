# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository. 
For comprehensive development guidelines, please refer to [guidelines.md](guidelines.md).

## Project Overview

A multi-module Maven lab project exploring **Spring Boot 4** (Spring Framework 7) with **Java 25**. Each module is a self-contained learning experiment.

| Module | Description |
|---|---|
| `di` | Spring DI fundamentals + JDK 25 language features + optics patterns (hand-rolled) |
| `data-optics` | Functional optics using the `hkj-spring-boot-starter` annotation processor |
| `data-jdbc` | Spring Data JDBC with PostgreSQL (school: Student/Course/Enrollment) |
| `rest-mvc` | REST API with Spring MVC + Spring Data JDBC (beer domain) |
| `retry` | Spring Core retry (`org.springframework.core.retry`) + `@EnableResilientMethods` |
| `web-app-demo` | Thymeleaf web app + Spring Data JDBC (book/author/publisher domain) |

## Build & Test Commands

```bash
# Build all modules (skip tests)
./mvnw clean package -DskipTests

# Build a single module
./mvnw clean package -DskipTests -pl di

# Run all unit tests for a module
./mvnw test -pl data-optics

# Run a single test class
./mvnw test -pl di -Dtest=CompanyTest

# Run integration tests (suffix *IT)
./mvnw verify -pl rest-mvc

# Run all tests across all modules
./mvnw verify
```

> **Local Maven repository**: The project uses a custom local repo at `C:/Users/yul/.m2/spring/repository` configured via `.mvn/maven.config`. IntelliJ requires the matching `settings_spring.xml` — see `Local_MVN_Repository.md`.

## Architecture & Key Patterns

### Optics (functional lenses/prisms/traversals)

Two approaches are used in parallel:

1. **Hand-rolled** (in `di` module, `hkj/optics` package): `Lens`, `Prism`, and `Traversal` are plain Java records with `andThen` composition. Use these to understand the mechanics.

2. **Annotation-processor generated** (in `data-optics` module): Records annotated with `@GenerateLenses` (from `hkj-spring-boot-starter`) have lens accessors generated at compile time. The `@EnableResilientMethods` annotation activates the Spring resilience AOP proxy in the `retry` module.

### Module system

The `di` module uses JPMS (`module-info.java`). When adding dependencies to `di`, the `module-info.java` must be updated accordingly.

### Surefire / Mockito configuration

Tests require a Mockito agent on the JVM arg line. This is already configured in the root `pom.xml`:
```xml
<argLine>-Xshare:off -javaagent:${settings.localRepository}/org/mockito/mockito-core/${mockito.version}/mockito-core-${mockito.version}.jar</argLine>
```

### Integration tests

Files matching `**/*IT.java` are integration tests run by `maven-failsafe-plugin` during the `verify` phase. The PostgreSQL modules (`rest-mvc`, `retry`, `data-jdbc`, `web-app-demo`) require a running PostgreSQL instance on `localhost:54316` with password set via the environment variable `DATASOURCE_PASSWORD`.

### Test conventions

- `@DisplayName` + `@DisplayNameGeneration(ReplaceUnderscores.class)` on every test class
- `@WebMvcTest` for controller slice tests (Spring Boot 4: `spring-boot-starter-webmvc-test`)
- `@MockitoBean` (not `@MockBean`) for injecting mocks in Spring Boot 4 slice tests
- AssertJ (`assertThat`) for assertions; BDDMockito (`given`) for stubbing

### Code style

- Every file begins with a path comment: `//: package.ClassName.java`
- `@NullMarked` (jspecify) on classes that enforce null-safety
- Lombok (`@RequiredArgsConstructor`, `@Slf4j`, `@Builder`) used throughout
- Records preferred over classes for domain models

--- 

## Spring Boot Guidelines

Refer to [guidelines.md](./.junie/guidelines.md) for the full list of Spring Boot and Java development standards.

