# Beer REST API Contract — Wire Format (Unchanged by this Feature)

Base path: `/sfg7/api/v1/beer`

The wire format below is what clients observe **today** and what they MUST observe **after** the DTO migration. 

SC-004 asserts byte-equivalence; FR-012 mandates the same JSON field names, types, and presence rules.

The only thing that changes is the **Java type** the controller and service exchange (`Beer` → `BeerDto`). 

The on-the-wire JSON is identical because both types declare the same 9 components in the same order.

---

## JSON Shape (single Beer)

``` 
{
  "id": "f1a2b3c4-d5e6-7890-abcd-ef1234567890",
  "version": 1,
  "beerName": "Galaxy Cat",
  "beerStyle": "PALE_ALE",
  "upc": "12356",
  "quantityOnHand": 122,
  "price": 1299,
  "createdDate": "2026-05-13T14:23:00.123Z",
  "updateDate":  "2026-05-13T14:23:00.123Z"
}
```

| Field            | JSON type                 | Source                         | Inbound behavior                                                                    |
|------------------|---------------------------|--------------------------------|-------------------------------------------------------------------------------------|
| `id`             | string (UUID)             | server-assigned                | ignored on `POST`; required on `PUT`/`PATCH` path but **path** value wins over body |
| `version`        | integer                   | client or server               | client may supply; not enforced for optimistic locking today                        |
| `beerName`       | string                    | client                         | required logically; no bean-validation today                                        |
| `beerStyle`      | string (enum name)        | client                         | one of the `BeerStyle` enum values                                                  |
| `upc`            | string                    | client                         |                                                                                     |
| `quantityOnHand` | integer                   | client                         |                                                                                     |
| `price`          | integer                   | client                         | in cents                                                                            |
| `createdDate`    | string (ISO-8601 Instant) | server-assigned on insert      | ignored on inbound `POST`                                                           |
| `updateDate`     | string (ISO-8601 Instant) | server-refreshed on every save | ignored on inbound `POST`                                                           |

Jackson 3 unknown-field tolerance is whatever the module's defaults dictate — this feature does not change it.

---

## Endpoints

### `GET /sfg7/api/v1/beer`

- **200 OK** with `Content-Type: application/json`
- Body: JSON array of Beer objects (shape above)

### `GET /sfg7/api/v1/beer/{beerId}`

- **200 OK** with a single Beer object body when `beerId` exists
- **404 Not Found** via `NotFoundException(UUID)` (annotated `@ResponseStatus(HttpStatus.NOT_FOUND)`) when no row with that id

### `POST /sfg7/api/v1/beer`

- Request body: JSON Beer object. `id`, `createdDate`, `updateDate` are server-managed and **ignored** if present in inbound body.
- **201 Created**
- Response header `Location: …/sfg7/api/v1/beer/{newId}` where `{newId}` is the id assigned by the database (`gen_random_uuid()`), surfaced through the DTO returned from `BeerServiceImpl.saveNewBeer(...)`
- Empty response body

### `PUT /sfg7/api/v1/beer/{beerId}`

- Full replacement semantics via `Beer.updateWith(beerId, incoming)` (internally on the entity).
- Preserves `createdDate`; refreshes `updateDate`.
- **204 No Content** on success; **404** when `beerId` not found (via `NotFoundException`).

### `PATCH /sfg7/api/v1/beer/{beerId}`

- Partial-merge semantics via `Beer.patchWith(beerId, incoming)`. Each null / blank inbound component leaves the stored value intact. `createdDate` preserved; `updateDate` always refreshed (even if all mutable fields are null/blank — see spec Edge Cases).
- **204 No Content** on success; **404** when not found.

### `DELETE /sfg7/api/v1/beer/{beerId}`

- **204 No Content** on success (idempotent — no 404 if id absent today, matching current behavior).

---

## What this feature explicitly does NOT change

- Field names, JSON types, ordering of components in the serialized object.
- Status codes for any endpoint.
- `Location` header format on `POST`.
- 404 mechanism (`NotFoundException`, no `@ControllerAdvice`).
- Unknown-field tolerance on inbound requests.
- Idempotency / optimistic-locking behavior (`version` is still cosmetic until a later feature enforces it).

**The implementation MUST be replay-safe:** 
- any captured request/response pair from the current code MUST produce identical responses against the new code.
