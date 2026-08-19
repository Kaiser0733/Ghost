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

DECISION-003: ARCH-DECIDE | PHASE-1-SIMULATION | Single-device dual-user encounter simulation | The Phase 1 web prototype will simulate two Ghost users inside one browser/device with an explicit developer/test-only user switcher. The active simulated user may be switched between User A and User B. The encounter state is centrally modeled with explicit phase transitions: ANONYMOUS → REVEALED_BY_A/REVEALED_BY_B → WAITING_FOR_OTHER → MUTUAL → CONNECTED | FADED. Both reveal directions supported. No identity before mutual reveal. No persistence, no networking, no backend. The user switcher is visually distinct as a dev/test control and designed for clean removal. | Single-device simulation enables one person to test both sides of the encounter flow. Central state model ensures deterministic transitions. Dev switcher isolates test concerns from product UI. | 2026-08-12

DECISION-004: ARCH-DECIDE | PHASE-1-UI | Atmosphere and Interaction Design Pass | Phase 1 receives a focused UI/interaction design pass to make the experience feel mysterious, intimate, slightly romantic, quiet, modern, and memorable. Visual system: Cormorant Garamond serif display, Inter UI sans, warm dark palette (#0a0a0c) with warm accent (#e8dcc8) for waiting, cool accent (#d4d4dc) for mutual. Encounter glyph system (○/��/●) with state transitions. Mutual reveal moment with scale+fade animation. Home screen sparse with glyph state indicators. Profile with connection records. Bottom nav with glyph icons. User switcher visually distinct as dev control. No scope expansion — no new features, no networking, no backend, no AI. | Phase 1 prototype felt "basic" — core flow worked but lacked emotional resonance. Visual and interaction design needed to make the experience feel mysterious, intimate, slightly romantic, quiet, modern, and human — per the Constitution's emotional tone requirements — without expanding product scope. | 2026-08-13

DECISION-005: ARCH-DECIDE | PHASE-1-UI | Warm Noir / Streetlight Design Direction | Phase 1 prototype receives a Warm Noir / Streetlight visual redesign to make the experience feel warm, modern, intimate, approachable, cinematic, socially attractive, slightly mysterious, and alive. The redesign replaces the flat black presentation with layered dark/charcoal surfaces, introduces a restrained warm amber accent for human presence and reveal states, uses neutral/moonlight tones for anonymous states, improves body text contrast, uses serif typography for emotional voice and sans-serif typography for utility, improves contrast for readability, adds subtle ambient depth, and refines state transitions. The design preserves all existing mechanics, privacy rules, and scope boundaries. The design principle is: MYSTERY ABOUT THE PERSON = GOOD, MYSTERY ABOUT HOW THE APP WORKS = BAD. | Visual audit identified: too dark/flat/empty; mystery created through obscurity not person; emotional arc not visible; encounter feels like database record; waiting state dead; incoming reveal indistinguishable from normal; mutual reveal looks like table row; insufficient text contrast; venue labels feel like surveillance metadata; Home over-prioritizes wordmark; dev switcher too prominent. Redesign addresses all while preserving core mechanics. | 2026-08-14

DECISION-006: ROADMAP-AMEND | PHASE-2-DEFERRED | Phase 2 (Prototype Testing) was not performed; recorded as DEFERRED, not completed | Reconciliation record (2026-08-19): Phase 2 user-pair testing (>=5 pairs, testing protocol, findings, Go/No-Go) has NOT been executed. Development proceeded from the frozen Phase 1 prototype (645cb99) directly into Phase 3 research and lab work without a Phase 2 exit decision. This entry records that situation honestly. It does NOT fabricate testing results and does NOT constitute a waiver. An explicit human decision is still required: run Phase 2 testing, defer it further, or formally waive it. Status: HUMAN-RATIFIED by project owner 2026-08-19 (ratification confirms Phase 2 DEFERRED, no findings fabricated, no completion claimed; later explicit decision may run, waive, or otherwise resolve Phase 2). | Governance requires a decision record for phase transitions; the absence of Phase 2 was a documentation gap discovered during the 2026-08-19 repository audit. | 2026-08-19

DECISION-007: ARCH-DECIDE | PHASE-3-BLE-LAB-STATUS | Phase 3 research complete; isolated BLE feasibility lab build-verified; BLE feasibility UNDECIDED | Reconciliation record (2026-08-19): (1) Phase 3 proximity research is COMPLETE (Ghost_Phase3_Proximity_Feasibility_Research.md, commit 9bd84c3); BLE is the experimental candidate only. (2) An isolated disposable lab (ble-feasibility-lab/) was authorized for evidence gathering; it contains no production Ghost code, no encounters/reveal/connection/backend/accounts/PSI. (3) Lab green checkpoint: commit 125a998, CI run 32213571392, 53/53 unit tests, APK build successful. (4) Physical experiment NOT started; no physical measurements exist. (5) BLE feasibility verdict: UNKNOWN. No GO/NO-GO decision exists and none may be declared before physical data. Status: HUMAN-RATIFIED by project owner 2026-08-19 (ratification confirms research complete, lab authorized as experiment, checkpoint 125a998 / CI 32213571392 / 53/53 tests / APK build successful, physical experimentation NOT yet occurred, BLE feasibility UNKNOWN, no GO/NO-GO made). | Phase 3 work proceeded without a formal authorization entry; this record reconciles the decision log with verified repository state. | 2026-08-19

DECISION-008: ARCH-DECIDE | PHASE-3-BLE-LAB-SCANNER-CONFIG | Scanner ScanSettings unified to CALLBACK_TYPE_ALL_MATCHES + reportDelay 0 across the fleet; AUTO_BATCH dropped | Physical scanner-start failures on both test devices: Galaxy A03 (API 33) "invalid callback type-8" (CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH does not exist below API 34 — verified in AOSP android-13.0.0_r1 ScanSettings.Builder.isValidCallbackType) and Galaxy Tab A9+ (API 36) "report delay for auto batch must be >= 600000" (AOSP android-14/16 build() enforces reportDelayMillis >= AUTO_BATCH_MIN_REPORT_DELAY_MILLIS = 600000; old code never called setReportDelay, so delay was 0). Fix: pure-JVM ScanConfigResolver returns one unified config (ALL_MATCHES, reportDelay 0) valid on every API level; BleScannerImpl.createLowPowerScanSettings() consumes it. DOCUMENTED METHODOLOGY CHANGE: the framework-guaranteed screen-off batch cadence (AUTO_BATCH) is dropped — it was impossible on API 33 and its mandatory >=10-minute delivery cadence on API 34+ would have destroyed the 60-second detection-rate windows in states S3-S6. Screen-off behavior now depends on foreground service + PendingIntent broadcast delivery. All recorded observables (service-UUID filter, RSSI, timestampNanos, ephemeral ID parsing, raw observation recording) and privacy constraints are unchanged. No silent fallback: this entry is the explicit record of what changed. Physical verification NOT yet performed; devices must be manually retested with the new APK. | No single AUTO_BATCH configuration is valid on both API 33 and API 36; a unified config preserves cross-device comparability when roles are swapped (protocol section 5). | 2026-08-19

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
- It IS NOT evidence that proximity detection works
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

## DECISION-003 DETAIL

**Title:** PHASE-1-SIMULATION | Single-device dual-user encounter simulation
**Status:** APPROVED
**Date:** 2026-08-12
**Category:** ARCH-DECIDE

### Context
The Phase 1 web prototype requires a single-device simulation of two Ghost users to enable one person to test both sides of the encounter flow. This decision records the architectural approach for the simulation layer.

### Decision

The Phase 1 web prototype will simulate two Ghost users inside one browser/device using an explicit developer/test-only user switcher.

**Active simulated user:** Switchable between User A and User B.

**User switcher:**
- Clearly marked as a development/test control
- Visually distinct from normal Ghost UI
- Does not reset or create new encounters when switched
- Designed for clean removal before production

**Encounter state model (central, shared):**
```
ANONYMOUS
    ��
REVEALED_BY_A | REVEALED_BY_B
    ��
WAITING_FOR_OTHER
    ��
MUTUAL
    ��
CONNECTED

Alternative terminal state:
FADED
```

**Reveal flow (both directions):**

User A reveals
→ User B receives reveal notification/state
→ User B may REVEAL BACK or LET IT FADE

User B reveals
→ User A receives reveal notification/state
→ User A may REVEAL BACK or LET IT FADE

**Privacy maintained:**
- Identity NEVER revealed before mutual reveal
- Exact location NEVER exposed
- No localStorage persistence (in-memory state only)
- No real networking, no backend, no Firebase
- No GPS, no Bluetooth, no real proximity detection
- No AI, no messaging

**Prototype constraints:**
- Single browser/device
- Simulated users only
- Local, in-memory React state
- Dev switcher removable without affecting product code

### Rationale

Single-device simulation enables one person to test both sides of the encounter flow without requiring two devices or real proximity detection. Central state model ensures deterministic, testable transitions. Dev-only switcher isolates test infrastructure from product UI, enabling clean removal. In-memory state avoids persistence complexity for this prototype slice.

### References
- PROJECT_CONSTITUTION.md Section 5 (Technical Principles — local-first, deterministic logic)
- MASTER_ROADMAP.md Phase 1 Scope (simulation-only prototype)
- PRODUCT_SPEC.md Section 10 (Prototype Boundaries)
- DECISION-002 (Phase 1 Encounter Flow UX)

---

## DECISION-004 DETAIL

**Title:** PHASE-1-UI | Atmosphere and Interaction Design Pass
**Status:** APPROVED
**Date:** 2026-08-13
**Category:** ARCH-DECIDE

### Context
Phase 1 prototype was functional but felt "basic" per human testing. The core encounter/reveal flow worked correctly per DECISION-002/003, but the visual and interaction design lacked emotional resonance — it felt like a generic dark-mode app rather than Ghost's intended mysterious, intimate, slightly romantic, quiet, modern, human experience.

### Decision

Phase 1 receives a focused UI/interaction design pass to make the experience feel:
- Mysterious
- Intimate
- Slightly romantic
- Quiet
- Modern
- Memorable

**Without expanding product scope.** The existing information architecture (Home / Encounters / Profile) and encounter state machine (DECISION-003) remain unchanged.

**Visual System:**
- **Display Typography:** Cormorant Garamond (serif) — Ghost wordmark, emotional statements, mutual reveal
- **UI Typography:** Inter (sans) — controls, utility text, metadata
- **Monospace:** SF Mono / Fira Code — Ghost IDs
- **Color Palette:** Warm dark base (#0a0a0c), cards (#131317), elevated (#1a1a1f)
- **Accents:** Warm (#e8dcc8) for waiting states, cool (#d4d4dc) for mutual/connected
- **Glyph System:** ○ (anonymous) → �� (waiting/notification) → ● (mutual/connected) with state transitions

**Screen Redesigns:**

**Home:** Sparse, intentional. Ghost wordmark in Cormorant Garamond serif. "The people you almost met." tagline. State glyphs (○/��/●) with encounter counts. Sparse CTA. No feed, no stats.

**Encounters:** Glyph-centered cards (56px breathing glyph). Serif lead copy for emotional statements. Warm accent glyph for waiting, cool for mutual. Subtle hover/tap transitions. Glyph breathes (pulse) on waiting states.

**First Reveal (Waiting):** "You remembered this encounter." / "You've revealed yourself. Now it's their choice." Button → "Waiting…" (disabled). Glyph pulses warm.

**Incoming Reveal (Notification):** "Someone remembers this encounter." / "They chose to reveal themselves." Actions: Reveal back / Let it fade. Glyph pulses cool.

**Mutual Reveal:** "It's mutual." / "You both remembered." Centered profile reveal with scale+fade animation. Avatar, username, Ghost ID, bio. Connection record below.

**Profile:** Serif username, monospace Ghost ID, avatar with initials, bio, connection history with glyph dots.

**Bottom Nav:** Glyph icons (○/��/●), minimal labels, active accent color.

**User Switcher:** Sticky banner, clearly marked "Prototype — viewing as", visually distinct from product UI.

**Interaction:** Subtle transitions (120-350ms ease-out). Glyph state changes (border/color/background). Mutual reveal scale+fade (350ms). No particle effects, no loading spinners, no fake network activity.

**Accessibility:** Reduced motion support, focus-visible outlines, semantic buttons, sufficient contrast.

**No Scope Changes:** No new features, no networking, no backend, no AI, no messaging, no persistence.

### Rationale
Phase 1 prototype was functionally correct per DECISION-002/003 but lacked emotional resonance — "pretty basic" per human testing. This design pass implements the Constitution's intended emotional tone (mysterious, intimate, slightly romantic, quiet, modern, human) through typography, glyph system, color, motion, and spacing — without any scope expansion or new features.

### References
- PROJECT_CONSTITUTION.md Section 1 (Product Identity — emotional tone)
- PROJECT_CONSTITUTION.md Section 6 (Anti-Scope-Creep — no new features)
- MASTER_ROADMAP.md Phase 1 Scope (UI polish within existing scope)
- DECISION-002 (Phase 1 Encounter Flow UX)
- DECISION-003 (Phase 1 Simulation Architecture)
- PRODUCT_SPEC.md Section 10 (Prototype Boundaries)

---

## DECISION-005 DETAIL

**Title:** PHASE-1-UI | Warm Noir / Streetlight Design Direction
**Status:** APPROVED
**Date:** 2026-08-14
**Category:** ARCH-DECIDE

### Context
An independent visual/UX audit identified the following major problems with the current Phase 1 implementation:
1. The current UI is too dark, flat, empty, and developer-oriented.
2. Mystery is being created through visual obscurity instead of through the anonymous person.
3. The emotional arc is not visible in the interface.
4. The encounter feels like a database record instead of a real coincidence.
5. The waiting state feels dead.
6. The incoming reveal is visually indistinguishable from a normal encounter.
7. The mutual reveal is the emotional climax but currently looks like a table row.
8. Functional text has insufficient contrast.
9. "INDOOR · COMMERCIAL" feels like surveillance metadata rather than human context.
10. The Home screen over-prioritizes the wordmark instead of the user's current state.
11. The developer/test switcher is too visually prominent.
12. The current product copy is strong and should be preserved.

### Decision

The Phase 1 prototype will adopt a Warm Noir / Streetlight visual direction.

**The redesign must:**
- preserve the Ghost concept
- preserve the approved encounter state machine
- preserve the three-screen information architecture
- preserve the minimal social model
- preserve privacy rules
- improve visual warmth, clarity, contrast, state differentiation, and emotional progression

**The design should use:**
- layered dark/charcoal surfaces instead of flat black
- a restrained warm amber accent for human presence and reveal states
- neutral/moonlight tones for anonymous states
- higher body-text contrast
- serif typography for emotional/product voice
- human-readable sans-serif typography for utility information
- monospace only for Ghost IDs
- subtle ambient depth rather than empty black space
- restrained motion for state transitions

**The redesign should NOT introduce:**
- AI
- messaging
- followers
- likes
- feed
- stories
- dating mechanics
- maps
- GPS
- real proximity detection
- gamification
- analytics
- payments
- backend
- persistence

**The design principle is:**
> MYSTERY ABOUT THE PERSON = GOOD
> MYSTERY ABOUT HOW THE APP WORKS = BAD

### Rationale

The visual audit identified 12 major problems with the current implementation:
1. Too dark, flat, empty, developer-oriented
2. Mystery from obscurity, not the person
3. Emotional arc not visible
5. Waiting state feels dead
6. Incoming reveal indistinguishable
7. Mutual reveal looks like table row
8. Insufficient text contrast
9. Venue labels feel like surveillance
10. Home over-prioritizes wordmark
11. Dev switcher too prominent

The Warm Noir / Streetlight direction addresses all these while preserving:
- Core encounter mechanics
- Privacy rules
- Three-screen architecture
- Minimal social model
- Privacy-first behavior
- All existing product copy (which is strong)

**Design Principles:**
- MYSTERY ABOUT THE PERSON = GOOD
- MYSTERY ABOUT HOW THE APP WORKS = BAD

### References
- PROJECT_CONSTITUTION.md Section 1 (Product Identity — emotional tone)
- PROJECT_CONSTITUTION.md Section 6 (Anti-Scope-Creep — no new features)
- MASTER_ROADMAP.md Phase 1 Scope (UI polish within existing scope)
- DECISION-002 (Phase 1 Encounter Flow UX)
- DECISION-003 (Phase 1 Simulation Architecture)
- DECISION-004 (Phase 1 Atmosphere Design Pass)
- PRODUCT_SPEC.md Section 10 (Prototype Boundaries)

---

## TEMPLATE FOR FUTURE DECISIONS

```
DECISION-XXX: CATEGORY | Title | Context | Decision | Rationale | Date
```

**Example:**
```
DECISION-006: TECH-SELECT | Proximity: Bluetooth LE + PSI | Phase 3 research concluded BLE with Private Set Intersection meets privacy/battery requirements | SELECTED — BLE + PSI for Phase 4 | Best balance of Android background support, privacy (no raw location exchange), and battery efficiency (<3%/hr) | 2026-XX-XX
```

---

**END OF DECISIONS LOG (v1.7 — 2026-08-19; DECISION-006/007 HUMAN-RATIFIED by project owner; DECISION-008 pending ratification)**
