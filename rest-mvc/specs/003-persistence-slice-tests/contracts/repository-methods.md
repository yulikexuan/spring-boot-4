# Contract — Repository Methods Under Test

The persistence slice has no public API of its own; 
- the "contract" is the subset of `ListCrudRepository` methods invoked by the service layer today. 
- This file is the inventory the tests must satisfy.

## Source: service-layer call sites

- `BeerServiceImpl` (`src/main/java/.../domain/beer/service/BeerService.java`)
- `CustomerServiceImpl` (`src/main/java/.../domain/customer/service/CustomerService.java`)

Both impls call exactly four methods through their respective repositories.

## Methods in scope (per domain)

| Method                                    | Service call sites (Beer)                         | Service call sites (Customer)                                | Positive-path test   | Negative-path test                                                         |
|-------------------------------------------|---------------------------------------------------|--------------------------------------------------------------|----------------------|----------------------------------------------------------------------------|
| `save(T entity) → T`                      | `saveNewBeer`, `updateBeerById`, `patchBeerById`  | `saveNewCustomer`, `updateCustomerById`, `patchCustomerById` | required             | n/a                                                                        |
| `findAll() → List<T>`                     | `findAllBeers`                                    | `findAllCustomers`                                           | required             | n/a (empty-list edge case asserted inside positive path)                   |
| `findById(ID id) → Optional<T>`           | `getBeerById`, `updateBeerById`, `patchBeerById`  | `getCustomerById`, `updateCustomerById`, `patchCustomerById` | required             | required                                                                   |
| `deleteById(ID id) → void`                | `deleteBeerById`                                  | `deleteCustomerById`                                         | required             | required (must not throw on unknown id, per `ListCrudRepository` contract) |

## Methods explicitly out of scope (FR-010)

`count`, `existsById`, `findAllById`, `saveAll`, `deleteAll`, `delete(T)`, `deleteAllById(Iterable<ID>)` — not invoked by any service today.

## Per-domain test enumeration (minimum)

Seven tests per domain, fourteen total:

1. `save_new_<domain>_assigns_uuid_and_round_trips` — covers `save` (+) on the "DB-assigned id" branch, DB UUID default, full-field round-trip incl. `Instant ↔ TIMESTAMP`.
2. `find_all_returns_persisted_rows` — covers `findAll` (+); also asserts empty-list path implicitly when run on a fresh schema.
3. `find_by_id_returns_existing` — covers `findById` (+).
4. `find_by_id_returns_empty_for_unknown_uuid` — covers `findById` (−).
5. `save_existing_id_updates_in_place_without_duplicating` — covers `save` (+) update branch; asserts `findAll().size()` unchanged.
6. `save_with_explicit_unknown_uuid_pins_repository_contract` — pins spec edge case 1: persist a `<domain>` whose `id` is a freshly generated, never-persisted UUID; assert the actual Spring Data JDBC contract (row count, returned `id`, raised exception type — whichever is observed). Prevents silent regressions to a "phantom row of the wrong shape" or to an unexpected runtime error type.
7. `delete_by_id_removes_and_subsequent_find_returns_empty` — covers `deleteById` (+) and `findById` (−) after delete; an additional one-liner asserts `deleteById(unknownUuid)` does not throw, covering `deleteById` (−).

## What the contract is **not**

This is not an external HTTP / message / SDK contract — the persistence slice is internal. 

The `contracts/` directory exists per `/speckit-plan` Phase 1 conventions; the artifact here is documentary only.
