```
Goal: Prompt for running `/speckit.plan`.
Replace every <placeholder>. Delete bullets that do not apply.
Keep it tight — the spec is the source of truth; this prompt only locks
the technical decisions the spec deferred.
```

**Spec:** @./specs/<NNN-feature-slug>/spec.md

**Tech context:**
<one or two lines: framework + version, persistence, JSON lib, Java version,
test stack, target module(s)>

**Lock in these technical choices:**
- <New type> placement: in package `<package.path>`
- Conversion / mapping strategy:
  <e.g. MapStruct mapper in `<package>`, `@Mapper(componentModel = "spring")`;
   note any constraints — records need explicit `@Mapping` per component>
- Build wiring:
  <dependency coordinates + version; annotation-processor ordering if
   Lombok and another processor coexist (e.g. lombok-mapstruct-binding,
   lombok before mapstruct)>
- Boundary discipline: <which layer owns the conversion / where each type
  may appear; e.g. "all DTO<->Entity mapping inside <ServiceImpl>; controller
  sees DTO only">
- Type shape: <record vs. class; Lombok annotations; what must NOT be on it
  (e.g. no Spring Data annotations)>
- Cross-cutting helpers to preserve:
  <e.g. `WebUtils.buildLocation(path, () -> savedDto.id())`>
- Domain methods that stay put: <e.g. `updateWith` / `patchWith` remain on
  the entity; service maps DTO→entity before calling them>
- Tests:
  <retype `ArgumentCaptor<OldType>` -> `ArgumentCaptor<NewType>`;
   request-body builders construct <NewType> instead of <OldType>;
   serialized JSON shape is unchanged>

**Constraints to preserve (echoed from spec so the plan does not drift):**
- <Forbidden imports under specific packages — e.g. "no org.springframework.data.*
  under web/controller/ or domain/<x>/service/">
- <Repository signature / boundary that must not change>
- <Wire format unchanged>
- <Other subdomains / modules untouched>
- <Bootstrap / seed data untouched>

**Deliverables:**
- File-by-file change list (new files, modified files, deleted files)
- Implementation order
- pom.xml / build-file diffs if dependencies change
- Verification command: `mvn <goal> -pl <module>` (and any DB / env prereqs)

---

### Authoring checklist (delete before submitting)

- [ ] Spec link uses the real feature folder (`NNN-feature-slug`).
- [ ] Every choice the spec's **Assumptions** section flagged as
      "plan-phase decision" is locked here.
- [ ] If you introduce a new build-time annotation processor, you've named
      the version AND the ordering relative to Lombok.
- [ ] Each "Constraint to preserve" maps to an FR or success criterion in
      the spec — no constraints invented here.
- [ ] No new functional requirements snuck in. If you find yourself adding
      one, update the spec instead and re-run `/speckit.specify`.
- [ ] Deliverables include a concrete verification command, not "run tests".
