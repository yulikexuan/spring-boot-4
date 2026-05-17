# Phase 1 — Data Model

The persistence-slice tests exercise the existing aggregates as-is. No new entities, no schema changes. This document pins what the tests must round-trip and the column/type contract they assert against.

## Beer

- Record: `spring.boot.sfg7.rest.mvc.domain.beer.model.Beer`
- Table: `beer` (DDL in `src/main/resources/schema_renew.sql`)
- Repository: `BeerRepository extends ListCrudRepository<Beer, UUID>`

| Field              | Java type        | Column               | SQL type          | Notes                                                  |
|--------------------|------------------|----------------------|-------------------|--------------------------------------------------------|
| `id`               | `UUID`           | `id`                 | `UUID PK`         | DB default `gen_random_uuid()` — assert default fires  |
| `version`          | `Integer`        | `version`            | `INTEGER`         | Reserved for future optimistic locking — not asserted  |
| `beerName`         | `String`         | `beer_name`          | `VARCHAR(255)`    | Round-trip                                             |
| `beerStyle`        | `BeerStyle`      | `beer_style`         | `VARCHAR(50)`     | Enum ↔ varchar — assert exact value preserved          |
| `upc`              | `String`         | `upc`                | `VARCHAR(50)`     | Round-trip                                             |
| `quantityOnHand`   | `Integer`        | `quantity_on_hand`   | `INTEGER`         | Round-trip                                             |
| `price`            | `Integer`        | `price`              | `INTEGER`         | Cents (per `rest-mvc/CLAUDE.md`)                       |
| `createdDate`      | `Instant`        | `created_date`       | `TIMESTAMP`       | Assert `Instant ↔ TIMESTAMP` preservation              |
| `updateDate`       | `Instant`        | `update_date`        | `TIMESTAMP`       | Same                                                   |

## Customer

- Record: `spring.boot.sfg7.rest.mvc.domain.customer.model.Customer`
- Table: `customer` (DDL in `src/main/resources/schema_renew.sql`)
- Repository: `CustomerRepository extends ListCrudRepository<Customer, UUID>`

| Field           | Java type   | Column           | SQL type         | Notes                                                  |
|-----------------|-------------|------------------|------------------|--------------------------------------------------------|
| `id`            | `UUID`      | `id`             | `UUID PK`        | DB default `gen_random_uuid()` — assert default fires  |
| `version`       | `Integer`   | `version`        | `INTEGER`        | Reserved for future optimistic locking — not asserted  |
| `name`          | `String`    | `name`           | `VARCHAR(255)`   | Round-trip                                             |
| `createdDate`   | `Instant`   | `created_date`   | `TIMESTAMP`      | Assert `Instant ↔ TIMESTAMP` preservation              |
| `updateDate`    | `Instant`   | `update_date`    | `TIMESTAMP`      | Same                                                   |

## Round-trip equality (both records)

- Construct a Beer / Customer via `record.builder().<all fields except id>().build()`, persist via `save`, read back via `findById(savedId)`, assert:
  - `savedId` is non-null UUID
  - read-back record is value-equal to the saved record *modulo* `id`
  - timestamps preserved to whatever precision Spring Data JDBC / PG TIMESTAMP guarantee (do not over-assert sub-microsecond precision — see edge cases in spec)

## Out of scope

- `BootstrapData` (does not run inside a `@DataJdbcTest` slice — slice excludes `CommandLineRunner` beans)
- `BeerMapper` / `CustomerMapper` (MapStruct) — exercised by the service-unit tests, not by this slice
- `Flashcard` — explicitly out of scope per spec
