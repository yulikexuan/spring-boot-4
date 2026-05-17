# Retrospective Audit Checklist: Fast Persistence-Slice Tests for Beer and Customer

- **Purpose**: Author retrospective on requirements quality, written after implementation. Each item asks whether the spec was *written* well enough — not whether the tests *work*. Findings flag what to do differently next time a similar slice-test feature is specified.

- **Created**: 2026-05-16
- **Feature**: [spec.md](../spec.md)
- **Focus**: Behavioral term clarity · Edge-case completeness · Coverage / SC traceability · Assumption validation
- **Depth**: Lightweight (author retrospective)

## Behavioral Term Clarity

- [ ] CHK001 Is SC-002's wall-clock target ("feels instant", "well under the time a `@SpringBootTest` takes to print its first banner") quantified with a numeric ceiling so a second author could independently verify it? [Clarity, Spec §SC-002]
- [ ] CHK002 Does FR-008 define a precision tolerance for the `Instant ↔ TIMESTAMP` round-trip, or is "preserves the values written" left for the implementer to invent (1 ms? 1 μs? exact)? [Clarity, Spec §FR-008]
- [ ] CHK003 Does FR-005 disambiguate *which* SQL file is the canonical DDL? The file named `schema.sql` actually contains `TRUNCATE`/`VACUUM`, and the DDL lives in `schema_renew.sql` — surfaced in research D-3, not in the spec itself. [Clarity, Spec §FR-005]

## Edge-Case Completeness

- [ ] CHK004 Does Edge Case 1 ("update targets a non-existent id") specify the expected contract (exception type? silent no-op? returned entity vs. row count?), or does the phrase "pins the actual repository contract" delegate discovery to the implementer? Spring Data JDBC's actual behavior (non-null `@Id` ⇒ UPDATE ⇒ 0 rows silently) was only learned during implementation. [Completeness, Spec §Edge Cases]
- [ ] CHK005 Is "an empty repository returns an empty list from `findAll`, not `null`" backed by its own acceptance criterion, or is it only implicitly covered by the positive-path `find_all` scenario? [Coverage, Spec §Edge Cases]

## Coverage / SC Traceability

- [ ] CHK006 Does the SC-003 matrix ("minimum 12 tests") account for the explicit-UUID-save edge case that landed as a 13th/14th required test? The matrix yields 12 but the implementation needed 14 to satisfy Edge Case 1 — a sign the matrix and the edge-case list are not jointly closed. [Consistency, Spec §SC-003 vs §Edge Cases]
- [ ] CHK007 Is every FR (FR-001…FR-010) traceable to at least one Acceptance Scenario *and* one Success Criterion, with no orphan FR? FR-010 ("methods explicitly out of scope") in particular has no positive SC mapping. [Traceability, Spec §FR-010]

## Assumption Validation

- [ ] CHK008 Is the assumption "Zonky 2.x supports Spring Boot 4 / Java 25" stated in the spec as a falsifiable, must-verify claim — not just flagged inside research D-1 as "verify on Javadoc Central before adding"? The transitive `embedded-postgres` dependency was missed at planning time and only caught at first test run. [Assumption, Research §D-1]
- [ ] CHK011 Was the deprecation status of `DatabaseProvider.ZONKY` (renamed to `EMBEDDED`, scheduled for removal in next major) checked at planning time? Spec/plan/tasks all referenced `ZONKY` and the rename was only caught from an IDE deprecation warning after implementation. Lesson: scan enum constants and annotation attributes for `@Deprecated` markers during research D-1, not after. [Assumption, Research §D-1]
- [ ] CHK009 Is the assumed Zonky cold-start cost (~3–5 s, stated in §Assumptions and quickstart) a falsifiable claim with a stated measurement method? Observed cold start on a developer Windows machine was materially higher (~18 s for `initdb` alone), which means the assumption was directional rather than testable. [Assumption, Spec §Assumptions]
- [ ] CHK010 Is SC-005's "repeatable: running N times produces N identical green results" given a concrete N and a measurement protocol, or is "repeatable" left as an unmeasured property? Without an N and a protocol, SC-005 cannot be objectively closed. [Measurability, Spec §SC-005]

## Notes

- Check items off as completed: `[x]`. Findings on each item belong inline as a one-line note under the item.
- Items use `[Clarity / Completeness / Consistency / Coverage / Traceability / Assumption / Measurability]` tags so a future retrospective can filter by dimension.
- This checklist validates the *spec*, not the implementation. If an item passes, the requirement was well-written; if it fails, that's a lesson for the next slice-test spec — not a defect in the tests.
