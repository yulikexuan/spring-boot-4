# New DI Features of Spring Boot 4

Spring Framework 7 and Spring Boot 4 (both released in November 2025) introduce several enhancements to dependency injection, primarily focused on null safety, modularity, and integration with modern Java standards.

## Programmatic Bean Registration 

A new `BeanRegistrar` contract allows for more flexible and dynamic bean registration 
beyond the traditional `@Bean` methods, enabling registration based on runtime logic. 

- [Programmatic Bean Registration Mechanism With BeanRegistrar in Spring](https://www.baeldung.com/spring-beanregistrar-registration)


## Modular Auto-Configuration 

- Spring Boot 4 refactored its massive monolithic `auto-configuration` JAR into smaller, focused modules. 
- This makes DI leaner by ensuring only the necessary configuration logic for your specific dependencies is loaded into the application context.

## Composite Task Decorators 

- For task scheduling, Spring Boot 4 now automatically creates a `CompositeTaskDecorator` that chains multiple `TaskDecorator` beans based on the `@Order` annotation, removing the need for manual chaining code.

## Dynamic Dependency Resolution (DDR) 

Spring Boot 4 introduces a new Dynamic Dependency Resolution system to help manage complex dependency trees at runtime, aiming to reduce version conflicts in microservice or plugin-based architectures.

## Improved `@Nested` Test Dependency Injection 

The Spring Extension for JUnit Jupiter now offers more consistent dependency injection support within `@Nested` test class hierarchies, using the same `ApplicationContext` across lifecycle and test methods.

## Jakarta DI Migration

- Support for `javax.inject (e.g., @Inject, @Named)` has been removed.
  - You must now use the Jakarta EE 11 equivalents from the `jakarta.inject` package.


## Enhanced EntityManager Injection

In JPA 3.2, you can now inject EntityManagerFactory and shared `EntityManager`
references directly using `@Autowired` or `@Inject` with qualifier support,
eliminating the need for `SharedEntityManagerBean`.


## `JSpecify` Null Safety

- The framework has moved from org.springframework.lang annotations to JSpecify (`@Nullable`, `@NonNull`) as the new standard.
- This provides compile-time enforcement of null-safety, which is particularly beneficial for Kotlin interoperability.

