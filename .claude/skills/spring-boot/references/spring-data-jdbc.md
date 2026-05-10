# Spring Data JDBC

- [Key principles](#key-principles)
- [Configure Spring Data JDBC properties](#configure-spring-data-jdbc-properties)
- [Identity generation](#identity-generation)
- [Value Object for Primary Key](#value-object-for-primary-key)
- [JDBC Auditing](#use-jdbc-auditing-support)
- [AssertUtil](#assertutil-class-to-validate-input-parameters)
- [Example aggregate root](#example-aggregate-root)
- [Example aggregate with one-to-many](#example-aggregate-with-one-to-many)
- [Example repository](#example-beerrepository)
- [Cross-aggregate references](#cross-aggregate-references)
- [Schema management](#schema-management)

## Key principles

Follow these principles when using Spring Data JDBC:

- Model aggregate roots as **Java `record`s** annotated with
  `@Table("table_name")` from `org.springframework.data.relational.core.mapping`
- Mark the primary key with `@Id` from `org.springframework.data.annotation`
  (NOT `jakarta.persistence.Id`)
- Use `UUID` or a TSID-backed value object for application-assigned IDs;
  use `Long` for DB-generated identities (`BIGSERIAL` / `IDENTITY`)
- Map snake_case columns explicitly with
  `@Column("column_name")` from `org.springframework.data.relational.core.mapping`
- Use **enum types** for enum fields — Spring Data JDBC stores them as `VARCHAR`
  by name by default
- Treat each `@Table`-annotated record as its **own aggregate root**;
  cross-aggregate links go through `AggregateReference<Target, ID>`,
  not nested object graphs
- Use `@MappedCollection(idColumn = "...")` to model one-to-many relations
  **inside the same aggregate**
- Use **optimistic locking** with `@Version` from
  `org.springframework.data.annotation`
- Repositories extend `ListCrudRepository<T, ID>` (preferred — returns `List`)
  or `CrudRepository<T, ID>` / `PagingAndSortingRepository<T, ID>`
- Create repositories **only for aggregate roots**
- Use `@Query` only when derived query methods are insufficient;
  custom SQL goes in the repository interface
- Prefer **meaningful method names** on repositories over long Spring Data
  finder method names
- Use **domain methods** on the record (e.g. `updateWith`, `patchWith`)
  to keep mutation logic with the data
- Use **DTO projections** (interface or record) for read-only views

## Configure Spring Data JDBC properties

```properties
spring.datasource.hikari.auto-commit=false
spring.sql.init.mode=always
# Disable Spring Boot's auto-detected schema init when you control DDL via Flyway/Liquibase
# spring.sql.init.mode=never
```

> Spring Data JDBC has **no session, no lazy loading, no dirty tracking**.
> Every `save()` issues SQL immediately. There is no `spring.jpa.*` property —
> if you see one in a JDBC module, it is unused.

Auto-configuration is activated by adding the starter:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jdbc</artifactId>
</dependency>
```

## Identity generation

Three strategies, pick one per aggregate:

### 1. Database-generated (`BIGSERIAL` / `IDENTITY`)

Leave `id` as `null` on insert; Spring Data JDBC reads back the generated value.

```java
@Table(name = "student")
public record Student(@Id Long id, String name, String email) {}
```

### 2. Application-assigned UUID

Pre-populate the id and let Postgres' `gen_random_uuid()` default fill any
unset values, OR generate it in a factory method.

```java
@Table("beer")
public record Beer(@Id UUID id, /* ... */) {}
```

### 3. TSID via `BeforeConvertCallback`

Add the dependency:

```xml
<dependency>
    <groupId>io.hypersistence</groupId>
    <artifactId>hypersistence-tsid</artifactId>
    <version>2.1.4</version>
</dependency>
```

```java
public final class IdGenerator {
    private IdGenerator() {}
    public static String generateString() {
        return TSID.Factory.getTsid().toString();
    }
}
```

Register a callback to assign IDs before insert:

```java
@Configuration
class IdAssigningConfig {

    @Bean
    BeforeConvertCallback<Beer> assignBeerId() {
        return beer -> beer.id() == null
                ? Beer.builder().id(UUID.randomUUID()).build().updateWith(beer)
                : beer;
    }
}
```

## Value Object for Primary Key

Spring Data JDBC has no `@EmbeddedId`. Wrap the raw id in a record and let
the JDBC converter unwrap it via a registered `Converter`:

```java
public record UserId(String id) {
    public UserId {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("User id cannot be null or empty");
    }
    public static UserId of(String id)   { return new UserId(id); }
    public static UserId generate()      { return new UserId(IdGenerator.generateString()); }
}
```

Register read/write converters so JDBC treats `UserId` as a `String` column:

```java
@Configuration
class JdbcConverterConfig extends AbstractJdbcConfiguration {
    @Override
    protected List<?> userConverters() {
        return List.of(
            (Converter<String, UserId>) UserId::of,
            (Converter<UserId, String>) UserId::id
        );
    }
}
```

## Use JDBC Auditing Support

Add `@CreatedDate` / `@LastModifiedDate` (from
`org.springframework.data.annotation`) directly on record components, and
enable auditing with `@EnableJdbcAuditing` (NOT `@EnableJpaAuditing`).

> Records cannot extend a `BaseEntity` `@MappedSuperclass` like JPA. Either
> repeat the four audit fields per aggregate, or compose an `Auditable`
> sealed interface for behavior — JDBC will only persist record components.

```java
@Table("beer")
public record Beer(
        @Id UUID id,
        @Version Integer version,
        @Column("beer_name") String beerName,
        @CreatedDate  @Column("created_date") Instant createdDate,
        @LastModifiedDate @Column("update_date") Instant updateDate) {
}
```

**Enable JDBC Auditing** in your application configuration:

```java
@Configuration
@EnableJdbcAuditing
public class JdbcConfig {
}
```

### AssertUtil class to validate input parameters
Create an `AssertUtil` class with static methods to validate input parameters.

```java
public final class AssertUtil {
    private AssertUtil() {}

    public static <T> T requireNotNull(T obj, String message) {
        if (obj == null)
            throw new IllegalArgumentException(message);
        return obj;
    }
}
```

## Example aggregate root

A flat record-based aggregate with `@Builder`-driven update / patch methods —
the canonical pattern used in the `rest-mvc` module:

```java
@Builder
@Table("beer")
public record Beer(
        @Id UUID id,
        @Version Integer version,
        @Column("beer_name") String beerName,
        @Column("beer_style") BeerStyle beerStyle,
        String upc,
        @Column("quantity_on_hand") Integer quantityOnHand,
        Integer price,
        @Column("created_date") Instant createdDate,
        @Column("update_date") Instant updateDate) {

    public Beer updateWith(@NonNull UUID beerId, @NonNull Beer other) {
        return Beer.builder()
                .id(beerId)
                .version(other.version())
                .beerName(other.beerName())
                .beerStyle(other.beerStyle())
                .upc(other.upc())
                .quantityOnHand(other.quantityOnHand())
                .price(other.price())
                .createdDate(this.createdDate)
                .updateDate(Instant.now())
                .build();
    }
}
```

## Example aggregate with one-to-many

Use `@MappedCollection` to model a child collection that lives **inside** the
aggregate. The child has no `@Id` of its own when keyed only by the parent FK:

```java
@Table(name = "course")
public record Course(
        @Id Long id,
        @NonNull String name,
        String description,
        @MappedCollection(idColumn = "course_id") Set<Enrollment> enrollments) {
}

@Table(name = "enrollment")
public record Enrollment(
        @NonNull @Column("student_id") Long studentId,
        @Column("enrolled_at") Instant enrolledAt) {
}
```

When the `Course` aggregate is saved, Spring Data JDBC inserts/updates
`enrollment` rows automatically — no separate `EnrollmentRepository`.

## Example: BeerRepository

**File:** `domain/beer/repository/BeerRepository.java`

```java
public interface BeerRepository extends ListCrudRepository<Beer, UUID> {

    Optional<Beer> findByUpc(String upc);

    @Query("SELECT * FROM beer WHERE beer_style = :style")
    List<Beer> findByStyle(@Param("style") BeerStyle style);

    default Beer getByUpc(String upc) {
        return findByUpc(upc)
                .orElseThrow(() -> new ResourceNotFoundException("Beer not found with upc: " + upc));
    }
}
```

## Cross-aggregate references

Never embed another aggregate root by reference — use `AggregateReference`:

```java
@Table("order_line")
public record OrderLine(
        @Id Long id,
        @Column("beer_id") AggregateReference<Beer, UUID> beer,
        Integer quantity) {
}
```

The column stores only the foreign id; loading the `Beer` is the caller's
responsibility (via `BeerRepository`). This keeps each aggregate independently
loadable and consistent.

## Schema management

Spring Data JDBC does **not** generate DDL — you own the schema.

- For lab modules: a single `schema.sql` (or `schema_renew.sql`) on the
  classpath, picked up by `spring.sql.init.mode=always`.
- For production: Flyway or Liquibase; set `spring.sql.init.mode=never` so
  Spring Boot's basic init does not interfere.
- Tables are independent unless modeled with `@MappedCollection` —
  enforce FKs in DDL only when they map to in-aggregate relationships.
