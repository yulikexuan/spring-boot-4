# Composing `/speckit-specify` Prompts

The Spec Kit framing — *what* and *why*, never *how* — only works if your prompt sets it up. 

Here's what produces good specs vs. mediocre ones.

## 1. State the **outcome**, not the steps

| Weaker                                                                                         | Stronger                                                                                                                                |
|------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| "Create `BeerDto`, change `BeerController`, change `BeerService`, adjust tests."               | "Stop persistence types from leaking into the HTTP layer of the Beer subdomain. Keep the public API contract unchanged."                |

A step list is fine as *constraints*, but leads to the goal, 
so the spec's user stories and success criteria have something to anchor on.

## 2. Name the **Scope Boundary** Explicitly

- *In Scope*: which module, which subdomain, which endpoints.
- *Out of Scope*: what looks similar but shouldn't move 
  - (e.g., "leave `Customer` and `Flashcard` alone for now").

Without this, the spec either overreaches or hedges with assumptions.

## 3. Name **Hard Constraints**, not Preferred Internals

Good constraints to pin in the prompt:

- API contract **must / must-not** change.
- Wire format / status codes are preserved.
- Specific layers that **may / may-not** see specific types.
- Performance, compatibility, or security BARS.

**Avoid Pinning *implementation choices* in the prompt** 
- (mapper library, builder pattern, where helper methods live) — those belong in `/speckit-plan`. 
- If you pin them too early, the spec gets polluted with HOW.

## 4. Make Conflicting Requirements **Explicit**, or Expect Questions

Example: "no `id` field on `BeerDto`" + "controller still returns `201 Created` with `Location`" can't trivially both be true. 
That tension forces a clarifying question. If you write:

> "BeerDto has no id field; the service should return the new id separately so the controller can build Location."

…the conflict would be pre-resolved and the question would be skipped.

**Rule of Thumb**: 
- If two of your bullets pull in opposite directions, name the tie-breaker.

## 5. Provide just Enough **Domain Anchor**, not a Tour

One sentence each on:

- **Where does this fit?** ("Beer subdomain of the `rest-mvc` module.")
- **What's the current shape?** ("Today the entity is used end-to-end.")
- **What's the success signal?** ("All existing Beer tests pass after retyping; controller/service have no Spring Data imports.")

Enough for the spec to ground its user stories. 
You don't need to paste code.

## 6. Skip these — they're planning or task concerns

- Specific Commit / Branch / PR Layout
- Code style or formatting
- Library choice (MapStruct vs. handwritten vs. records `from()`)
- Test framework specifics
- File paths and class names *beyond what's load-bearing for the contract*

## 7. Template that Works Well

```text
Goal: <one sentence — what outcome / why it matters>

In scope: <module / subdomain / endpoints>
Out of scope: <similar areas to leave alone>

Must hold:
- <observable contract constraint>
- <architectural boundary constraint>
- <test/quality bar>

Must change:
- <high-level shape changes, not file lists>

Open questions I want you to ask before deciding:
- <thing you genuinely don't know yet>

Open questions I've already decided:
- <thing + your answer>
```

The last two sections are the cheat code: 
They tell the agent where to spend clarification budget (max three questions in Spec Kit) 
and where to stop asking.

## 8. Worked example — applied to the `BeerDto` feature

The original prompt was:

> Use Beer DTO instead of using Beer Entity:
> - Create `BeerDto` based on `Beer` Entity and `BeerDto` should still be java `record` but should not have any Spring Data JDBC annotation
> - `BeerDto` should not have `id` field either.
> - `BeerDto` should be used by `BeerController` and `BeerService`.
> - `BeerRepository` should only use `Beer` entity.
> - All existing tests should be adjusted also.

**What it Did Well**:

- Named the constraint precisely ("no Spring Data JDBC annotations").
- Named the boundary direction ("controller/service use DTO; repository uses entity").
- Explicitly required tests be adjusted (often forgotten).
- Was short.

**What it would have benefited from**: 
- a one-line goal at the top, and pre-resolving the `id` / `Location` tension.

**A stronger rewrite using the Template:**

``` 
Goal: Stop Spring Data JDBC types from leaking into the HTTP/service layer of the `Beer` subdomain, 
while keeping the public API contract unchanged.

In scope: rest-mvc module, Beer subdomain only (controller, service, repository, tests).
Out of scope: Customer, Flashcard, schema.sql, BootstrapData.

Must hold:
- Wire format and status codes for every /sfg7/api/v1/beer endpoint unchanged.
- After the change, no class under web/controller/ or domain/beer/service/ imports org.springframework.data.*.
- All existing Beer tests still pass.

Must change:
- Introduce a `BeerDto` record (no Spring Data annotations).
- BeerController and BeerService exchange `BeerDto`; BeerRepository stays on `Beer`.
- Tests retyped to match.

Open questions I've already decided:
- BeerDto mirrors all of Beer's logical fields including version, createdDate, updateDate.
- updateWith/patchWith stay on the Beer entity; the service maps DTO -> Beer first.
- BeerDto keeps `id` as a read-only response field so POST can still build Location naturally.

Open questions I want you to ask before deciding:
- (none — proceed with the decisions above)
```

That version would have produced the same spec **without** a clarifying round.
