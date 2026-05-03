# Jackson JsonView with Spring Boot 4.0

A comprehensive, hands-on tutorial exploring Jackson JSON processing in Spring Boot 4.0. 

Learn how to control
JSON serialization and deserialization, leverage Jackson 3's new features, configure JsonMapper,
and implement real-world filtering patterns with `@JsonView` and the new `hint()` method.

If you want to learn more about these changes, you can read [this article](https://spring.io/blog/2025/10/07/introducing-jackson-3-support-in-spring)
by Sébastien Deleuze.

## What you will learn

- **Jackson 2 & 3 Compatibility** 
  - Understand why this project uses Jackson 3.1.x (databind) with jackson-annotations 2.20, 
  - How Spring Boot 4.0 bridges both versions for seamless ecosystem migration.
- **Jackson 3 Key Changes** - Explore the major improvements: 
  - New `tools.jackson` packages 
  - Immutable builder-based configuration 
  - ISO-8601 date serialization by default 
  - Unchecked exceptions that work seamlessly with lambdas and streams 
- **JsonMapper: Reading and Writing JSON** 
  - Learn how Spring Boot Auto-Configures JsonMapper beans 
  - Use the TypeReference pattern for reading JSON from files 
  - Serialize objects with configured settings 
  - Optionally customize with builder patterns
- **Application Properties Deep Dive** - Master Jackson config properties like 
  - `use-jackson2-defaults` for migration compatibility 
  - `indent-output` for pretty-printing 
  - Understand why `sort-properties-alphabetically` doesn't work with records 
  - Discover other common serialization/deserialization features
- **JSON Views** 
  - Eliminate DTO proliferation by creating multiple JSON representations from a single model using `@JsonView` 
  - Learn 
    - hierarchical view design 
    - server-side response filtering 
    - client-side request filtering with the new `hint()` method 
    - why it's superior to the old `MappingJacksonValue` wrapper approach 

## Jackson 2 & 3 Compatibility

This project uses **Jackson 3.1.x** (databind) with **jackson-annotations 2.20** 
- and yes, that version mismatch is intentional!

### Why Different Versions?

The Jackson team solved a critical compatibility problem: 
- How to support both Jackson 2 and Jackson 3 simultaneously during the ecosystem's transition period 

**The Solution:**
- **Jackson Core 3.1.x**: New `tools.jackson` packages, improved APIs, thread-safe builders
- **jackson-annotations 2.20**: Shared annotation library that works with BOTH Jackson 2 and 3

This means:
- Your `@JsonView`, `@JsonFormat`, and other annotations work identically across Jackson 2 and 3
- Organizations can gradually migrate from Jackson 2 to 3 without breaking shared domain models
- Spring Boot 4.0 uses Jackson 3's processing engine while maintaining full annotation compatibility

**Dependency Tree:**

``` 
spring-boot-starter-jackson (4.0.5)
├── tools.jackson.core:jackson-databind:3.1.0  ← Jackson 3 engine
└── com.fasterxml.jackson.core:jackson-annotations:2.20  ← Shared annotations
```

This is the permanent solution, not a transitional state. 

It ensures maximum compatibility across the Java ecosystem.

---

## Jackson 3 Key Changes

If you're coming from Jackson 2, here are the important changes:

### Package Renaming

``` 
// Jackson 2
import com.fasterxml.jackson.databind.ObjectMapper;

// Jackson 3
import tools.jackson.databind.json.JsonMapper;
```

### Builder-Based Configuration

Jackson 3 requires immutable, thread-safe configuration:

``` 
// Jackson 2 - Mutable (not recommended)
ObjectMapper mapper = new ObjectMapper();
mapper.enable(SerializationFeature.INDENT_OUTPUT);  // Mutable state!

// Jackson 3 - Immutable Builder Pattern
JsonMapper mapper = JsonMapper.builder()
    .enable(SerializationFeature.INDENT_OUTPUT)
    .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    .build();  // Thread-safe and immutable
```

### Date Serialization Default Changed

``` 
// Jackson 2 default (WRITE_DATES_AS_TIMESTAMPS = true)
{"bakedAt": 1699257000000}

// Jackson 3 default (WRITE_DATES_AS_TIMESTAMPS = false)
{"bakedAt": "2025-11-06T05:30:00"}
```

Jackson 3 Defaults to ISO-8601 Strings 
- More human-readable and easier for frontend frameworks to parse 

### Unchecked Exceptions

Jackson 3 switches from checked to unchecked exceptions 
- A significant improvement for modern Java development.

``` 
// Jackson 2 - Checked exceptions (forced handling)
try {
    objectMapper.readValue(json, MyClass.class);
} catch (JsonProcessingException e) {  // Checked - must catch
} catch (IOException e) {              // Checked - must catch
}

// Jackson 3 - Unchecked exceptions (optional handling)
try {
    jsonMapper.readValue(json, MyClass.class);
} catch (JacksonException e) {  // Unchecked RuntimeException - catch if needed
    // Single exception hierarchy
}
```

**Why the Change?**

Jackson 2's checked exceptions were inherited from `IOException` 
- Reflecting the historical view that I/O operations should force error handling. 
- However, this created problems in modern Java:
  - **Lambda compatibility issues**: Checked exceptions can't be thrown from lambda expressions
  - **Stream API friction**: Requires awkward wrapping in `try-catch` or helper methods
  - **Unnecessary boilerplate**: Most developers just rethrow or wrap them anyway
  - **Runtime Nature**: JSON parsing errors are typically unrecoverable runtime failures, not business logic that should be handled

**Benefits of Unchecked Exceptions:**

``` 
// Jackson 2 - Doesn't compile! Checked exceptions in lambdas
donuts.stream()
    .map(d -> jsonMapper.writeValueAsString(d))  // ❌ Compile error
    .toList();

// Jackson 3 - Works seamlessly
donuts.stream()
    .map(d -> jsonMapper.writeValueAsString(d))  // ✅ No problem
    .toList();
```

- **Works in Lambdas and Streams**: No need for wrapper methods or sneaky throws
- **Cleaner Code**: Only catch exceptions where you can actually handle them
- **Single Exception Hierarchy**: All Jackson exceptions extend `JacksonException`
- **Still Catchable**: You can still catch and handle when needed
- **Modern Java Alignment**: Follows contemporary exception handling patterns

**Spring Boot 4.0 Transition**
- Use `spring.jackson.use-jackson2-defaults: true` in `application.yaml` 
  - to maintain Jackson 2 behavior during migration 

## JsonMapper: Reading and Writing JSON

Spring Boot 4.0 auto-configures a `JsonMapper` bean that you can inject and use throughout your application for reading and writing JSON.

### Auto-Configuration

Spring Boot automatically creates a `JsonMapper` bean configured from your `application.yaml` properties 
- No explicit `@Bean` configuration needed!

``` 
@Component
public class DataLoader implements ApplicationRunner {

    private final JsonMapper jsonMapper;  // Auto-configured by Spring Boot
    private final ResourceLoader resourceLoader;

    public DataLoader(JsonMapper jsonMapper, ResourceLoader resourceLoader) {
        this.jsonMapper = jsonMapper;
        this.resourceLoader = resourceLoader;
    }
    // ... use the jsonMapper
}
```

### Reading JSON from Files

The `DataLoader` class demonstrates reading JSON data from a file (`src/main/resources/data/donuts-menu.json`):

``` 
@Override
public void run(ApplicationArguments args) throws Exception {

    Resource resource = resourceLoader.getResource("classpath:data/donuts-menu.json");

    // Read JSON into List<Donut> using TypeReference
    this.donuts = jsonMapper.readValue(
        resource.getInputStream(),
        new TypeReference<List<Donut>>() {}  // Preserves generic type info
    );

    log.info("Loaded {} donuts from JSON file", donuts.size());
}
```

**Why TypeReference?**
- Java's type erasure means `List<Donut>` becomes just `List` at runtime
- `TypeReference` captures the full generic type `List<Donut>`
- Jackson 3 uses the diamond operator `<>` 
  - type is inferred from the variable

### Writing JSON (Serialization)

You can serialize objects back to JSON strings:

``` 
// Pretty-print a single donut (uses indent-output: true from application.yaml)
String json = jsonMapper.writeValueAsString(donuts.getFirst());
log.info("Serialized donut:\n{}", json);
```

**Output:**
``` 
{
  "type" : "Classic Glazed",
  "glaze" : "VANILLA",
  "toppings" : [ ],
  "price" : "2.50",
  "isVegan" : false,
  "calories" : 260,
  "bakedAt" : "2025-11-06T05:30:00"
}
```

### Custom Configuration (Optional)

If you need custom behavior beyond `application.yaml` properties
- You can define your own bean:

``` 
@Configuration
public class JacksonConfig {
    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
    }
}
```

> **Note:** This project uses Spring Boot's auto-configuration to keep the configuration in `application.yaml` for simplicity.

---

## Application Properties Deep Dive

This project's `application.yaml` demonstrates key Jackson configuration options:

``` 
spring:
  application:
    name: donuts
  jackson:
    use-jackson2-defaults: true  # Backward compatibility
    serialization:
      indent-output: true         # Pretty-print JSON
    # mapper:
    #   sort-properties-alphabetically: false  # Commented - see below
```

### use-jackson2-defaults: true

In Spring Boot 4.0.5, the property `spring.jackson.use-jackson2-defaults` 
is a transitional configuration used to ease the migration from Jackson 2 to Jackson 3, 
which is the new default in Spring Boot 4.

When set to true, this property instructs the auto-configured JsonMapper to use 
default settings that align as closely as possible with the Jackson 2 defaults used in Spring Boot 3.

#### Key Effects of Enabling this Property

- __Restores Legacy Behavior__ Reverts several default behaviors that changed in Jackson 3, 
    - Allowing applications to maintain compatibility with older JSON payloads 
      without immediate code changes
- __Date Serialization__ - One major change in Jackson 3 is writing dates as ISO-8601 strings by default. 
    - Setting this property to true can help revert to legacy timestamp-based formats if they were previously used. 
- __Property Ordering__ - Jackson 3 may sort properties alphabetically by default; 
    - this property helps maintain the prior ordering expectations from Jackson 2. 
- __Configuration Alignment__ 
  - It configures the new JsonMapper with internal settings that match the older ObjectMapper defaults.

#### Migration Context 

- __Jackson 3 is Preferred__ 
  - While this property exists for compatibility, 
    Spring recommends migrating fully to Jackson 3 settings over time 
- __Deprecation__ 
  - Support for Jackson 2 is officially deprecated in Spring Boot 4 and is intended to be removed in a future 4.x release
- __Implementation__ 
  - This property specifically affects the `JsonMapper.Builder` provided by the Spring Boot auto-configuration.

> **Purpose:** Maintains Jackson 2 behavior during the transition to Jackson 3.

**Why needed:**
- Jackson 3 changed some defaults (like date serialization)
- This setting ensures consistent behavior if you're migrating from Jackson 2
- Recommended for Spring Boot 4.0 during the transition period

**When to remove:** Once you've validated all Jackson 3 changes work for your use case.

### spring.http.converters.preferred-json-mapper=jackson2

n Spring Boot 4.0.5, the property `spring.http.converters.preferred-json-mapper=jackson2` 
is used to force the application to use Jackson 2 for JSON serialization 
and deserialization instead of the new default, Jackson 3.

This configuration is primarily a migration tool to help developers upgrade to Spring Boot 4 
while they transition their codebase and dependencies to support Jackson 3.

#### Key Functions

- __Reverts the Default Mapper__ 
  - In Spring Boot 4, Jackson 3 is the preferred and default library for JSON processing. 
    Setting this property to jackson2 tells the `HttpMessageConvertersAutoConfiguration` 
    to use the Jackson 2-based converter for all HTTP requests and responses.
- __Enables Compatibility__ 
  - It allows legacy code that relies on Jackson 2-specific features—such 
    as the `com.fasterxml.jackson` package (which changed to tools.jackson in Jackson 3) 
    or mutable ObjectMapper behavior - to continue functioning without immediate refactoring.
- __Global Impact__ 
  - This property affects any component using standard HTTP message converters, 
    including `RestTemplate`, `RestClient`, and Spring MVC `@RestController` endpoints

#### Context and Deprecation
- __Deprecation__ 
  - Support for Jackson 2 in Spring Boot 4 is considered deprecated 
    and is expected to be removed in a future 4.x release
- __Requirement__ 
  - To use this setting, you must ensure the Jackson 2 modules 
    (e.g., jackson-module-parameter-names, jackson-datatype-jsr310) 
    are still present on your project's classpath
- __Property Renaming__ 
  - In Spring Boot 4, this property was renamed from 
    `spring.mvc.converters.preferred-json-mapper` to `spring.http.converters.preferred-json-mapper` 
    to better reflect its impact across both MVC and non-MVC clients.

### Jackson's Locale serialization format changed

#### Format Switch (Underscore to Hyphen) 

By default, Jackson 3 now uses the IETF BCP 47 Language Tag format rather than 
the traditional Java underscore format.

___Example___ 

- `Locale.CHINA` previously serialized as `zh_CN` in Jackson 2, 
but now serializes as `zh-CN` in Jackson 3

### indent-output: true

> **Purpose:** Pretty-prints JSON output with proper indentation.

**Maps to:** `SerializationFeature.INDENT_OUTPUT`

**Effect:**
``` 
// With indent-output: true
{
  "type" : "Maple Bar",
  "price" : "3.99"
}

// With indent-output: false
{"type":"Maple Bar","price":"3.99"}
```

> **Best for:** Development, debugging, human-readable APIs. 
> Disable in production for smaller payloads.

### sort-properties-alphabetically: false (Commented)

**Why comment out?** This property has a known limitation in Jackson 3:

**The Issue:**
- `SORT_PROPERTIES_ALPHABETICALLY` doesn't work with Java records or classes with parameterized constructors
- Jackson 3 prioritizes constructor parameter order over alphabetical sorting
- Since this project uses records (`public record Donut(...)`), alphabetical sorting doesn't work

**Example:**
``` 
public record Donut(
    String type,      // Order: 1
    Glaze glaze,      // Order: 2
    // ... follows constructor parameter order
) {}
```

__Output follows constructor order, not alphabetical order.__

**Workaround:** 
- Use `@JsonPropertyOrder(alphabetic = true)` on regular classes (not records) 
  - if you need alphabetical ordering

### Other Common Properties

While not used in this project, these are frequently useful:

**Serialization:**

``` 
spring:
  jackson:
    serialization:
      fail-on-empty-beans: false        # Allow serializing objects with no properties
      write-dates-as-timestamps: false  # ISO-8601 strings (Jackson 3 default)
```

**Deserialization:**

```yaml
spring:
  jackson:
    deserialization:
      fail-on-unknown-properties: false      # Ignore unknown JSON fields
      accept-empty-string-as-null-object: true  # Treat "" as null
```

**Date Handling in Jackson 3:**
- Jackson 2 default: `"bakedAt": 1699257000000` (numeric timestamp)
- Jackson 3 default: `"bakedAt": "2025-11-06T05:30:00"` (ISO-8601 string)
- More human-readable and easier for JavaScript/frontend frameworks

## JSON Views

Imagine you're building a Donut API that needs to serve different clients 
with different data requirements:

- **Mobile App** (bandwidth-constrained): Just `type` and `price`
- **Public Website**: Add `glaze`, `toppings`, and `isVegan`
- **Internal Dashboard**: Everything, including `calories` and `bakedAt`
- **Admin Panel**: All fields plus system metadata

**Traditional Approach Problems:**

``` 
// ❌ Multiple DTOs for every endpoint
public class DonutSummaryDTO { ... }
public class DonutPublicDTO { ... }
public class DonutInternalDTO { ... }
public class DonutAdminDTO { ... }

// ❌ Multiple controllers or tons of mapping code
@GetMapping("/summary")
public List<DonutSummaryDTO> getSummary() {
    return donuts.stream()
        .map(d -> new DonutSummaryDTO(d.getType(), d.getPrice()))
        .toList();
}

@GetMapping("/public")
public List<DonutPublicDTO> getPublic() {
    return donuts.stream()
        .map(d -> new DonutPublicDTO(d.getType(), d.getGlaze(), /* ... */))
        .toList();
}
// ... multiply by every API endpoint!
```

This creates:
- DTO class proliferation
- Repetitive mapping code
- Maintenance nightmares
- Easy to make mistakes and expose wrong data

---

### The Solution: Jackson @JsonView

**One model, multiple views:**

``` 
public record Donut(

    @JsonView(Views.Summary.class)  // Mobile: type and price
    String type,

    @JsonView(Views.Public.class)    // Web: + glaze, toppings, isVegan
    Glaze glaze,

    @JsonView(Views.Public.class)
    List<String> toppings,

    @JsonView(Views.Summary.class)
    BigDecimal price,

    @JsonView(Views.Public.class)
    boolean isVegan,

    @JsonView(Views.Internal.class)  // Internal: + calories, bakedAt
    Integer calories,

    @JsonView(Views.Internal.class)
    LocalDateTime bakedAt
) { }
```

**View hierarchy:**

``` 
public class Views {
    public interface Summary {}                        // type, price
    public interface Public extends Summary {}         // + glaze, toppings, isVegan
    public interface Internal extends Public {}        // + calories, bakedAt
    public interface Admin extends Internal {}         // all fields
}
```

**One controller, multiple endpoints:**

``` 
@GetMapping("/api/donuts/summary")
@JsonView(Views.Summary.class)
public List<Donut> getSummary() {
    return dataLoader.getDonuts();  // Same data, different view!
}

@GetMapping("/api/donuts/public")
@JsonView(Views.Public.class)
public List<Donut> getPublic() {
    return dataLoader.getDonuts();  // Same data, different view!
}
```

**Result:** Clean, maintainable, and difficult to accidentally expose sensitive data.

---

### The Innovation: hint() Method

#### The Old Way (Spring Boot 3.x and earlier)

Before Spring Boot 4.0, if you wanted to **send** a request with filtered fields, 
you needed this awkward wrapper:

``` 
// ❌ OLD: Required mutable wrapper object
var user = new User("Marcel", "Martin", LocalDate.of(1971, 7, 12),
    "m@m.fr", "1234 rue Gambetta", 69002, "Lyon", "France");

var jacksonValue = new MappingJacksonValue(user);  // Wrapper!
jacksonValue.setSerializationView(Summary.class);   // Mutable!

var response = this.restTemplate.postForObject(
    "http://localhost:8080/create",
    jacksonValue,  // Have to send wrapper, not the actual object
    String.class
);
```

**Problems:**
- Extra wrapper object (`MappingJacksonValue`)
- Mutable state (not thread-safe)
- Breaks fluent API style
- Not intuitive

### The New Way (Spring Boot 4.0+)

With Spring Boot 4.0 and Jackson 3, you can use the new `hint()` method:

```java
// ✅ NEW: Clean, immutable, fluent API
var user = new User("Marcel", "Martin", LocalDate.of(1971, 7, 12),
    "m@m.fr", "1234 rue Gambetta", 69002, "Lyon", "France");

var response = this.restClient.post()
    .uri("http://localhost:8080/create")
    .hint(JsonView.class.getName(), Summary.class)  // Clean hint!
    .body(user)  // Send the actual object
    .retrieve()
    .body(String.class);
```

**Benefits:**
- No wrapper objects
- Immutable and thread-safe
- Fluent API maintained
- Works with Jackson 3's `SmartHttpMessageConverter`

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.6+
- Spring Boot 4.0.0-RC1

### Running the Demo

**Terminal 1 - Start the Server:**

```bash
./mvnw spring-boot:run
```

The server starts on `http://localhost:8080` with these endpoints:

- `GET /api/donuts/summary` - Summary view (type, price)
- `GET /api/donuts/public` - Public view (+ glaze, toppings, isVegan)
- `GET /api/donuts/internal` - Internal view (+ calories, bakedAt)
- `GET /api/donuts/admin` - Admin view (all fields)
- `POST /api/donuts` - Create donut (accepts only Summary fields)

**Terminal 2 - Run the Client:**

```bash
./mvnw spring-boot:run -Dstart-class=dev.danvega.donuts.ClientApp
```

The client demonstrates:
1. Fetching donuts with GET
2. Creating a donut with POST using `hint()` to filter what gets sent

## Additional Resources

- [Jackson @JsonView Documentation](https://github.com/FasterXML/jackson-docs/wiki/JacksonJsonViews)
- [Spring Boot 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)
- [Jackson 3.0 Migration Guide](https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md)

