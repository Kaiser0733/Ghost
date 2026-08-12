# DECISIONS LOG — Ghost

**Version:** 1.0  
**Status:** CONTROLLED DOCUMENT — All architectural decisions must be recorded here  
**Authority:** Subordinate to PROJECT_CONSTITUTION.md and MASTER_ROADMAP.md  
**Format:** `DECISION-XXX: CATEGORY | Title | Context | Decision | Rationale | Date`

---

## DECISION FORMAT

```
DECISION-XXX: CATEGORY | Title | Context | Decision | Rationale | Date
```

**Categories:**
- `ROADMAP-ADVANCE` — Phase transition
- `ROADMAP-AMEND` — Roadmap scope/timeline change
- `SPEC-PROMOTE` — PROPOSED/FUTURE/UNKNOWN → CONFIRMED
- `SPEC-CLARIFY` — Ambiguity resolution in product spec
- `TECH-SELECT` — Technology choice (backend, proximity, crypto, etc.)
- `ARCH-DECIDE` — Architectural boundary or separation decision
- `CONST-AMEND` — Constitution amendment (human-only)
- `SCOPE-RULE` — Anti-scope-creep exception or clarification

**Rules:**
- Every decision must be recorded before implementation
- Decisions are append-only — no edits to previous entries
- Reference constitution/roadmap/spec section when applicable
- Human approval required for all entries

---

## DECISIONS

DECISION-001: ROADMAP-ADVANCE | Phase 0 → Phase 1 | The Ghost project foundation documents have been reviewed and approved. Development may proceed to Phase 1 Web Prototype. | APPROVED — Advance to Phase 1 | Phase 0 exit criteria met: all three foundation documents complete, consistent, and approved. No application code written. No dependencies installed. | 2026-08-11

---

## DECISION-001 DETAIL

**Title:** ROADMAP-ADVANCE | Phase 0 → Phase 1  
**Status:** ACCEPTED  
**Date:** 2026-08-11  
**Category:** ROADMAP-ADVANCE

### Context
The Ghost project foundation documents have been reviewed and approved:
- PROJECT_CONSTITUTION.md — Permanent governing principles
- MASTER_ROADMAP.md — 11-phase controlled development roadmap
- PRODUCT_SPEC.md — Product concept with CONFIRMED/PROPOSED/FUTURE/UNKNOWN labeling

### Phase 0 Outcome
- PROJECT_CONSTITUTION.md: APPROVED
- MASTER_ROADMAP.md: APPROVED
- PRODUCT_SPEC.md: APPROVED
- No application code was written during Phase 0
- No dependencies were installed during Phase 0

### Phase 1 Scope (Confirmed)
- React + TypeScript web prototype only
- Simulated encounters using localStorage/local state
- No native Android development
- No real proximity detection
- No Firebase requirement
- No production backend requirement
- No AI requirement

### Architectural Clarification (Critical)
The Phase 1 prototype uses localStorage (or equivalent local state) to simulate the interaction. This means:

- The Phase 1 prototype is NOT a two-device networking test
- It is NOT evidence that real phones can detect each other
- It is NOT evidence that proximity detection works
- It exists solely to test the Ghost product experience (encounter → reveal → connection flow)

The initial Phase 1 simulation may use:
```
User A ↔ User B
within one browser/device (via user toggle)
```

### Constitution/Roadmap/Spec Impact
No alterations required to PROJECT_CONSTITUTION.md, MASTER_ROADMAP.md, or PRODUCT_SPEC.md. This decision records the phase advance and clarifies prototype scope per existing documents.

### References
- PROJECT_CONSTITUTION.md Section 8 (Roadmap Modification Rules)
- MASTER_ROADMAP.md Phase 0 Exit Criteria / Phase 1 Scope
- PRODUCT_SPEC.md Section 10 (Prototype Boundaries)

---

## TEMPLATE FOR FUTURE DECISIONS

```
DECISION-XXX: CATEGORY | Title | Context | Decision | Rationale | Date
```

**Example:**
```
DECISION-002: TECH-SELECT | Proximity: Bluetooth LE + PSI | Phase 3 research concluded BLE with Private Set Intersection meets privacy/battery requirements | SELECTED — BLE + PSI for Phase 4 | Best balance of Android background support, privacy (no raw location exchange), and battery efficiency (<3%/hr) | 2026-XX-XX
```

---

**END OF DECISIONS LOG (v1.0)**