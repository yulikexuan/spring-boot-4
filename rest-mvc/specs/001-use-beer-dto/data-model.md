# Data Model — Use BeerDto Instead of Beer Entity

Two types participate in the Beer flow after this feature lands. 

Their components are identical; only their **annotations** and **purpose** differ.

---

## `Beer` (entity) — UNCHANGED

Package: `spring.boot.sfg7.rest.mvc.domain.beer.model`

| Component        | Type        | Annotation                    | Notes                                                |
|------------------|-------------|-------------------------------|------------------------------------------------------|
| `id`             | `UUID`      | `@Id`                         | Defaulted at DB via `gen_random_uuid()`              |
| `version`        | `Integer`   | —                             | Reserved for optimistic locking (not enforced today) |
| `beerName`       | `String`    | `@Column("beer_name")`        |                                                      |
| `beerStyle`      | `BeerStyle` | `@Column("beer_style")`       | Enum stored as VARCHAR(50)                           |
| `upc`            | `String`    | —                             |                                                      |
| `quantityOnHand` | `Integer`   | `@Column("quantity_on_hand")` |                                                      |
| `price`          | `Integer`   | —                             | Stored as cents                                      |
| `createdDate`    | `Instant`   | `@Column("created_date")`     | Set on insert, preserved on update / patch           |
| `updateDate`     | `Instant`   | `@Column("update_date")`      | Refreshed on every update / patch                    |

**Type-level annotations**: `@Builder`, `@Table("beer")`.

**Methods (preserved by FR-007)**:
- `updateWith(UUID beerId, Beer other) → Beer` — full replacement; preserves `createdDate`, refreshes `updateDate`.
- `patchWith(UUID beerId, Beer other) → Beer` — partial merge; null/blank component on `other` keeps current value; `createdDate` preserved, `updateDate` refreshed.

**Usage after this feature**: referenced only by `BeerRepository`, `BootstrapData`, the merge methods themselves, and (internally) `BeerServiceImpl` for the transient build-side and the post-save read-side of every CRUD call.

---

## `BeerDto` (transport) — NEW

Package: `spring.boot.sfg7.rest.mvc.domain.beer.dto`

| Component        | Type        | Annotation   | Notes                                                                                                                                                              |
|------------------|-------------|--------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `id`             | `UUID`      | —            | Read-only response component. Populated when mapping from a persisted `Beer`. Ignored on inbound `POST` payloads (service builds fresh `Beer` from non-id fields). |
| `version`        | `Integer`   | —            |                                                                                                                                                                    |
| `beerName`       | `String`    | —            |                                                                                                                                                                    |
| `beerStyle`      | `BeerStyle` | —            | Same enum as entity                                                                                                                                                |
| `upc`            | `String`    | —            |                                                                                                                                                                    |
| `quantityOnHand` | `Integer`   | —            |                                                                                                                                                                    |
| `price`          | `Integer`   | —            |                                                                                                                                                                    |
| `createdDate`    | `Instant`   | —            | Set on create-response, preserved on update / patch responses                                                                                                      |
| `updateDate`     | `Instant`   | —            | Refreshed by entity merge methods, then mapped back                                                                                                                |

**Type-level annotations**: `@Builder` (Lombok). **Explicitly NO** `@Table`, `@Id`, `@Column` — FR-001.

**File header**: `//: spring.boot.sfg7.rest.mvc.domain.beer.dto.BeerDto.java`

**Usage**: parameter and return type of `BeerController` and `BeerService` for every endpoint that currently exchanges `Beer`. 
- Customer / Flashcard DTOs are out of scope.

---

## Mapping rules — `BeerMapper`

Package: `spring.boot.sfg7.rest.mvc.domain.beer.dto`

Interface: `BeerMapper`. Annotations: `@Mapper(componentModel = "spring")`, `@NullMarked` (jspecify).

| Method                       | Direction    | All 9 components mapped 1:1 by name   |
|------------------------------|--------------|---------------------------------------|
| `BeerDto toDto(Beer beer)`   | entity → dto | explicit `@Mapping` per component     |
| `Beer toEntity(BeerDto dto)` | dto → entity | explicit `@Mapping` per component     |

Both methods declare `@Mapping(source = "<comp>", target = "<comp>")` for every field, as the draft locks in (defends against silent field drift; see [research.md R-2](./research.md)).

---

## Lifecycle / state transitions

State transitions are unchanged by this feature — they remain owned by the `Beer` entity's `updateWith` / `patchWith`. The DTO is a stateless transport snapshot.

``` 
POST  BeerDto(no id) ──► BeerServiceImpl ──► toEntity ──► Beer (no id)
                                                 │
                                                 ▼
                                          repository.save → Beer (id, audit set)
                                                 │
                                                 ▼
                                              toDto → BeerDto (id, audit set) ──► 201 + Location

GET   /{id} ──► repository.findById → Beer ──► toDto → BeerDto ──► 200

PUT   BeerDto ──► toEntity → Beer (incoming) ──► existing.updateWith(id, incoming) → Beer (preserved createdDate, fresh updateDate) ──► save ──► 204
PATCH BeerDto ──► toEntity → Beer (incoming, partial) ──► existing.patchWith(id, incoming) → Beer ──► save ──► 204
DELETE id   ──► repository.deleteById ──► 204
404   thrown via NotFoundException(UUID) when findById is empty
```

---

## Validation

This feature does NOT introduce bean-validation annotations on `BeerDto`. 

Wire-format tolerance follows Jackson 3 defaults (spec Assumptions). A follow-up feature may add `@NotNull` / `@Size` etc.

---

## Persistence schema

`beer` table is unchanged. `schema.sql` continues to DROP+CREATE on every start. No migration needed.
