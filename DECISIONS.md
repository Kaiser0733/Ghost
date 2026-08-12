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

DECISION-002: SPEC-PROMOTE | PHASE-1-ENCOUNTER-FLOW | Ghost Phase 1 UX formally approved by founder. The encounter/reveal/connection flow, pre-reveal anonymity, first-reveal notification, mutual reveal profile, connection history, non-mutual fade behavior, emotional tone, privacy constraints, and Phase 1 boundaries are CONFIRMED for the web prototype. | CONFIRMED — Phase 1 encounter flow as specified | Founder approval of complete Phase 1 UX. This decision uses the Constitution Section 6 Exception Process to promote profile elements (photo, username, bio, Ghost ID) and connection history display from BANNED/FUTURE to CONFIRMED for Phase 1 prototype scope. | 2026-08-12

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

## DECISION-002 DETAIL

**Title:** PHASE-1-ENCOUNTER-FLOW
**Status:** APPROVED
**Date:** 2026-08-12
**Category:** SPEC-PROMOTE / SCOPE-RULE

### Context
Ghost Phase 1 UX has been formally approved by the founder. This decision records the complete encounter and reveal flow for the Phase 1 web prototype, promoting several items from BANNED/FUTURE/UNKNOWN to CONFIRMED via the Constitution Section 6 Exception Process.

### Decision Summary

#### 1. ENCOUNTER
- Simulated encounter between two simulated users
- Represents meaningful physical proximity
- Communicates approximate elapsed time (e.g., "18 minutes ago")
- No exact coordinates or exact location exposed

#### 2. PRE-REVEAL STATE (Anonymous)
- No username, profile photo, Ghost ID, exact location, or exact address visible
- Encounter remains anonymous and mysterious

#### 3. FIRST REVEAL
- User A chooses REVEAL
- User B receives notification/state change: "Someone remembers this encounter" + encounter context + "They chose to reveal themselves"
- Actions available: REVEAL BACK | LET IT FADE
- First user's identity NOT revealed before reciprocation

#### 4. MUTUAL REVEAL
- User B chooses REVEAL BACK
- Both users simultaneously gain access to each other's limited profiles
- Clear communication: "It's mutual" / "You both remembered"

#### 5. PROFILE AFTER MUTUAL REVEAL (PROMOTED FROM BANNED)
The revealed profile contains ONLY:
- One profile photo
- Username
- Permanent Ghost ID
- Short bio

Explicitly EXCLUDED:
- Profile gallery
- Follower count
- Likes
- Public posts
- Popularity ranking

#### 6. CONNECTION
- After mutual reveal → Ghost connection
- Connection represented in user's Ghost history
- Display: CONNECTION @username | First crossed paths: date/time | Status: Mutual
- NO direct messaging in Phase 1 (may be considered later)

#### 7. NON-MUTUAL REVEAL
- User B chooses LET IT FADE
- Encounter fades
- NO notification to User A of explicit rejection
- NO rejection counter
- NO exposure of who rejected whom
- Encounter simply expires from active state

#### 8. PRODUCT EMOTION
- Intended tone: mysterious, slightly romantic, intriguing, human, subtle
- NOT: dating, matchmaking, social feed, messaging app, popularity system
- Romantic tension from uncertainty and mutual reveal, not explicit dating framing

#### 9. PRIVACY (NON-NEGOTIABLE)
- Exact location never exposed
- Identity not revealed before mutual reveal
- Prototype must not imply real proximity detection exists
- Simulated encounter data remains clearly simulated internally

#### 10. PHASE 1 BOUNDARY
**Tech Stack:**
- React + TypeScript
- Simulated User A/User B
- Simulated encounter events
- localStorage/local state

**NOT Implemented:**
- Real Bluetooth/GPS proximity detection
- Nearby-user discovery
- Firebase/production backend
- Native Android
- DM/messaging
- AI
- Monetization

### Exception Process Compliance (Constitution Section 6)
This decision promotes the following from BANNED to CONFIRMED for Phase 1 scope:

| Item | Previously | Now | Rationale |
|------|------------|-----|-----------|
| Profile photo | BANNED (Constitution §6, Spec §6) | CONFIRMED | Required for mutual reveal moment |
| Username | BANNED (Constitution §6, Spec §6) | CONFIRMED | Required for connection history display |
| Short bio | BANNED (Constitution §6, Spec §6) | CONFIRMED | Human context for connection |
| Ghost ID (permanent) | BANNED (Constitution §3: "no long-term linkable") | CONFIRMED (prototype only) | Prototype simulation; production uses rotating keys |
| Connection history display | FUTURE (Spec §11) | CONFIRMED | Core to encounter → connection arc |
| Profile system (Phase 1) | NOT BUILT (Roadmap Phase 1) | CONFIRMED (minimal, post-reveal only) | Required to validate mutual reveal UX |

**Privacy Impact Assessment:** Profile data only exposed AFTER mutual consent. No location data in profile. Prototype uses local simulation only.

**Friction Impact Assessment:** Single-tap reveal actions. No profile creation/maintenance required pre-reveal. Profile is revealed, not constructed.

**Explicit Human Approval:** Founder approved complete Phase 1 UX including these elements.

### References
- PROJECT_CONSTITUTION.md Section 6 (Anti-Scope-Creep Rules, Exception Process)
- PROJECT_CONSTITUTION.md Section 3 (Privacy Principles — maintained)
- MASTER_ROADMAP.md Phase 1 Scope (extended per this decision)
- PRODUCT_SPEC.md Sections 5, 6, 8, 10, 11 (promotions from PROPOSED/FUTURE/UNKNOWN/BANNED → CONFIRMED)

---

## TEMPLATE FOR FUTURE DECISIONS

```
DECISION-XXX: CATEGORY | Title | Context | Decision | Rationale | Date
```

**Example:**
```
DECISION-003: TECH-SELECT | Proximity: Bluetooth LE + PSI | Phase 3 research concluded BLE with Private Set Intersection meets privacy/battery requirements | SELECTED — BLE + PSI for Phase 4 | Best balance of Android background support, privacy (no raw location exchange), and battery efficiency (<3%/hr) | 2026-XX-XX
```

---

**END OF DECISIONS LOG (v1.1)**