# Guidelines for Using Junie

This document outlines the coding standards and best practices for Junie when working on this project, which is built on **Spring Boot 4.0.3** and **Java 25**.

## Project Overview

A multi-module Maven lab project exploring **Spring Boot 4** (Spring Framework 7) with **Java 25**. Each module is a self-contained learning experiment.

| Module         | Description                                                                       |
|----------------|-----------------------------------------------------------------------------------|
| `di`           | Spring DI fundamentals + JDK 25 language features + optics patterns (hand-rolled) |
| `data-optics`  | Functional optics using the `hkj-spring-boot-starter` annotation processor        |
| `data-jdbc`    | Spring Data JDBC with PostgreSQL (school: Student/Course/Enrollment)              |
| `rest-mvc`     | REST API with Spring MVC + Spring Data JDBC (beer domain)                         |
| `retry`        | Spring Core retry (`org.springframework.core.retry`) + `@EnableResilientMethods`  |
| `web-app-demo` | Thymeleaf web app + Spring Data JDBC (book/author/publisher domain)               |

---

## Spring Boot 4.0.3

# Spring Boot Guidelines

## 1. Prefer Constructor Injection over Field/Setter Injection
* Declare all the mandatory dependencies as `final` fields and inject them through the constructor.
* Spring will auto-detect if there is only one constructor, no need to add `@Autowired` on the constructor.
* Avoid field/setter injection in production code.

## 2. Prefer package-private over public for Spring components
* Declare Controllers, their request-handling methods, `@Configuration` classes and `@Bean` methods with default (package-private) visibility whenever possible. There's no obligation to make everything `public`.

## 3. Organize Configuration with Typed Properties
* Group application-specific configuration properties with a common prefix in `application.properties` or `.yml`.
* Bind them to `@ConfigurationProperties` classes with validation annotations so that the application will fail fast if the configuration is invalid.
* Prefer environment variables instead of profiles for passing different configuration properties for different environments.

## 4. Define Clear Transaction Boundaries
* Define each Service-layer method as a transactional unit.
* Annotate query-only methods with `@Transactional(readOnly = true)`.
* Annotate data-modifying methods with `@Transactional`.
* Limit the code inside each transaction to the smallest necessary scope.


## 5. Disable Open Session in View Pattern
* While using Spring Data JPA, disable the Open Session in View filter by setting ` spring.jpa.open-in-view=false` in `application.properties/yml.`

## 6. Separate Web Layer from Persistence Layer
* Don't expose entities directly as responses in controllers.
* Define explicit request and response record (DTO) classes instead.
* Apply Jakarta Validation annotations on your request records to enforce input rules.

## 7. Follow REST API Design Principles
* **Versioned, resource-oriented URLs:** Structure your endpoints as `/api/v{version}/resources` (e.g. `/api/v1/orders`).
* **Consistent patterns for collections and sub-resources:** Keep URL conventions uniform (for example, `/posts` for posts collection and `/posts/{slug}/comments` for comments of a specific post).
* **Explicit HTTP status codes via ResponseEntity:** Use `ResponseEntity<T>` to return the correct status (e.g. 200 OK, 201 Created, 404 Not Found) along with the response body.
* Use pagination for collection resources that may contain an unbounded number of items.
* The JSON payload must use a JSON object as a top-level data structure to allow for future extension.
* Use snake_case or camelCase for JSON property names consistently.

## 8. Use Command Objects for Business Operations
* Create purpose-built command records (e.g., `CreateOrderCommand`) to wrap input data.
* Accept these commands in your service methods to drive creation or update workflows.

## 9. Centralize Exception Handling
* Define a global handler class annotated with `@ControllerAdvice` (or `@RestControllerAdvice` for REST APIs) using `@ExceptionHandler` methods to handle specific exceptions.
* Return consistent error responses. Consider using the ProblemDetails response format ([RFC 9457](https://www.rfc-editor.org/rfc/rfc9457)).

## 10. Actuator
* Expose only essential actuator endpoints (such as `/health`, `/info`, `/metrics`) without requiring authentication. All the other actuator endpoints must be secured.

## 11. Internationalization with ResourceBundles
* Externalize all user-facing text such as labels, prompts, and messages into ResourceBundles rather than embedding them in code.

## 12. Use Testcontainers for integration tests
* Spin up real services (databases, message brokers, etc.) in your integration tests to mirror production environments.

## 13. Use random port for integration tests
* When writing integration tests, start the application on a random available port to avoid port conflicts by annotating the test class with:

    ```java
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    ```

## 14. Logging
* **Use a proper logging framework.**  
  Never use `System.out.println()` for application logging. Rely on SLF4J (or a compatible abstraction) and your chosen backend (Logback, Log4j2, etc.).

* **Protect sensitive data.**  
  Ensure that no credentials, personal information, or other confidential details ever appear in log output.

* **Guard expensive log calls.**  
  When building verbose messages at `DEBUG` or `TRACE` level, especially those involving method calls or complex string concatenations, wrap them in a level check or use suppliers:

``` 
if (logger.isDebugEnabled()) {
    logger.debug("Detailed state: {}", computeExpensiveDetails());
}

// using Supplier/Lambda expression
logger.atDebug()
	.setMessage("Detailed state: {}")
	.addArgument(() -> computeExpensiveDetails())
    .log();
```

## 15. Unit Test Tools to Use
* **JUnit 5:** The most popular testing framework for Java.
* **Mockito (BDDMockito):** Mocking framework for unit tests, we prefer to use `BDDMockito` in this project.
* **AssertJ:** Assertions library for unit tests.
* **BDDMockito:** Mocking framework for BDD tests.

---

## Code Style for Junie

- **Path Comments:** Every file begins with a path comment: `//: package.ClassName.java`
- **Null-Safety:** Use `@NullMarked` (jspecify) on classes that enforce null-safety.
- **Lombok:** Use Lombok (`@RequiredArgsConstructor`, `@Slf4j`, `@Builder`) where appropriate to reduce boilerplate.
- **Records:** Prefer Java **Records** over classes for domain models and DTOs.
- **Java 25 Features:** Take advantage of the latest Java 25 features like advanced pattern matching, string templates (if available/stabilized), and improved collections.
- **Testing:** 
  - `@DisplayName` + `@DisplayNameGeneration(ReplaceUnderscores.class)` on every test class.
  - `@WebMvcTest` for controller slice tests (Spring Boot 4: `spring-boot-starter-webmvc-test`).
  - `@MockitoBean` (not `@MockBean`) for injecting mocks in Spring Boot 4 slice tests.
  - AssertJ (`assertThat`) for assertions; BDDMockito (`given`) for stubbing.
