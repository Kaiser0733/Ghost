# PROJECT CONSTITUTION — Ghost

**Version:** 1.0  
**Status:** PERMANENT — This document governs all future decisions.  
**Authority:** Supersedes all implementation plans, feature requests, and agent instructions.

---

## 1. PRODUCT IDENTITY

**Name:** Ghost  
**Tagline (internal):** *Where coincidence becomes connection*  
**Essence:** A social layer for the physical world. Ghost detects when two users cross paths in real life and creates an anonymous, temporary encounter. What happens next is entirely up to them.

**What Ghost Is:**
- A system that transforms physical proximity into social possibility
- An encounter engine — not a matching engine
- Anonymous by default, revealed by mutual consent
- Temporary by design — encounters expire

**What Ghost Is NOT (Anti-Identity):**
- A dating app — no preferences, no swiping, no "looking for"
- A social network — no feed, no followers, no posts, no likes
- A messaging app — no chat, no DMs, no inbox
- A location tracker — no maps, no check-ins, no location history
- An event app — no RSVPs, no calendars, no "happening nearby"
- A productivity tool — no networking, no professional features
- An AI chatbot — no suggestions, no conversation starters, no LLM features
- A follower-based platform — no audience, no influence, no metrics

---

## 2. CORE CONCEPT

**The Atomic Unit:** The Encounter  
An encounter is a factual record: *User A and User B were in proximity at Time T, in Venue Category V.*

**The Flow (Invariant):**
```
Physical Coincidence → Anonymous Encounter → Optional Mutual Reveal → Connection
```

**Three Principles:**
1. **Coincidence is the trigger** — not intent, not search, not algorithm
2. **Anonymity is the default** — identity is not required to participate
3. **Reveal is mutual** — unilateral reveal is impossible by design

---

## 3. PRIVACY PRINCIPLES (NON-NEGOTIABLE)

| Principle | Requirement | Violation = Block |
|-----------|-------------|-------------------|
| **No Raw Location Exposure** | Ghost never shares a user's exact location with another user. Ever. | BLOCK |
| **No Location History Retention** | The system does not store GPS coordinates, venue names, or movement traces. | BLOCK |
| **Proximity Without Tracking** | Encounter detection must verify proximity without either party (or the server) learning the other's location. | BLOCK |
| **Minimal Data** | Only data strictly necessary for encounter creation and TTL management exists. | BLOCK |
| **Ephemeral by Default** | Encounters have a TTL. Data expires automatically. No manual deletion needed. | BLOCK |
| **No Persistent Identity Linkage** | Cryptographic identifiers rotate. No long-term linkable profile exists. | BLOCK |
| **User-Controlled Reveal** | Identity disclosure requires explicit, mutual, informed consent. | BLOCK |

**Privacy Threat Model (Must Survive):**
- Server compromise → no location history to leak
- Passive surveillance → encounter graph reveals social proximity, not movement
- Active stalking → no persistent identity to target, encounters expire
- Data request → minimal data to surrender

---

## 4. USER-FRICTION PRINCIPLES

**Core Belief:** Value derives from what users already do (move through the world), not from what we make them do.

**Forbidden Patterns (Anti-Friction):**
- ❌ Require app to be open for encounters to work
- ❌ Manual encounter logging
- ❌ Profile creation/maintenance
- ❌ Forms, onboarding flows, preference centers
- ❌ Categorization, tagging, labeling
- ❌ "Check-in" or "share location" actions
- ❌ Notification spam ("You have 3 new encounters!")
- ❌ Daily/weekly engagement loops

**Required Patterns (Pro-Friction):**
- ✅ Zero-setup onboarding (install → works)
- ✅ Background operation (encounters happen while phone is in pocket)
- ✅ Encounters appear automatically
- ✅ Single-tap actions (reveal / dismiss)
- ✅ No "empty state" — the world provides content
- ✅ Respect for attention — no infinite scroll, no feed

---

## 5. TECHNICAL PRINCIPLES

**Architecture Separation (Mandatory):**
```
┌─────────────────────┐
│   Client Interface  │  ← Platform-specific (React Web / Compose Android)
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│   Ghost Core/Domain │  ← Pure logic: encounters, identity, TTL, reveal, crypto
│   (Platform-agnostic)│
└──────────┬──────────┘
           │
    ┌──────┴──────┐
    ▼             ▼
┌────────┐   ┌──────────────┐
│ Backend │   │  Proximity   │
│/Network│   │  Technology  │
└────────┘   └──────────────┘
```

**Technology Commitments (Current):**
- Web Prototype: React + TypeScript
- Future Android: Kotlin + Jetpack Compose
- Ghost Core: Language-agnostic (TypeScript first, portable to Kotlin)

**Technology Decisions DEFERRED (Do Not Choose Yet):**
- Backend: Firebase / Custom / P2P / Sync protocol
- Proximity: Bluetooth LE / UWB / WiFi RTT / Audio / Hybrid
- Identity: Key rotation / Group signatures / Anonymous credentials
- Storage: Local-first / Server-assisted / CRDTs
- Transport: WebRTC / WebSockets / MQTT / Custom

**Engineering Standards:**
- Privacy-first architecture (privacy is a requirement, not a feature)
- Local-first where possible
- Offline-capable core logic
- Deterministic encounter logic (testable, reproducible)
- No analytics, no telemetry, no crash reporting without explicit consent

---

## 6. ANTI-SCOPE-CREEP RULES

**The Scope Boundary (Enforced):**
> If a feature does not directly serve: *Physical Coincidence → Anonymous Encounter → Optional Mutual Reveal → Connection*, it is OUT OF SCOPE.

**Explicitly Banned Feature Categories:**
| Category | Examples | Status |
|----------|----------|--------|
| Dating | Preferences, swiping, matching algorithms | BANNED |
| Social Feed | Posts, likes, comments, shares, stories | BANNED |
| Messaging | Chat, DMs, typing indicators, read receipts | BANNED |
| Location Tracking | Maps, history, check-ins, "nearby now" | BANNED |
| Events | RSVPs, calendars, ticketing, discovery | BANNED |
| Profiles | Bios, photos, usernames, followers | BANNED |
| Gamification | Streaks, badges, leaderboards, XP | BANNED |
| AI Features | Suggestions, summaries, conversation help | BANNED |
| Notifications | Push for engagement, "come back" prompts | BANNED |
| Monetization | Ads, subscriptions, premium features | BANNED (for now) |

**Exception Process:**
1. Feature proposed in DECISIONS.md with rationale
2. Maps to which constitution principle?
3. Privacy impact assessment
4. Friction impact assessment
5. Explicit approval required before implementation
6. No "just trying it out" — either approved or not

---

## 7. DECISION-MAKING RULES

**Decision Hierarchy:**
1. **Constitution** (this document) — Immutable without formal amendment
2. **Roadmap** (MASTER_ROADMAP.md) — Phase gates, exit criteria
3. **Product Spec** (PRODUCT_SPEC.md) — Current confirmed behavior
4. **Decisions Log** (DECISIONS.md) — Recorded architectural choices
5. **Implementation** — Code, subject to above

**How Decisions Are Made:**
- No silent changes to constitution, roadmap, or spec
- Every architectural decision recorded in DECISIONS.md
- Format: `DECISION-XXX: Title | Context | Options | Decision | Rationale | Date`
- Agents must check DECISIONS.md before implementing related features

**Conflict Resolution:**
- Constitution > Roadmap > Spec > Decisions > Implementation
- If implementation contradicts constitution → STOP, fix constitution or implementation
- If roadmap contradicts spec → STOP, resolve before proceeding
- Ambiguity → Ask human, don't assume

---

## 8. ROADMAP MODIFICATION RULES

**MASTER_ROADMAP.md is a Controlled Document.**

**Allowed Changes:**
- Phase exit criteria met → advance to next phase (recorded in DECISIONS.md)
- Timeline adjustments within phase (no scope change)
- Adding detail to future phases (marked PROPOSED/FUTURE/UNKNOWN)

**Forbidden Changes:**
- Adding phases without constitutional justification
- Removing phases without explicit sunset decision
- Changing phase objectives without decision record
- Promoting FUTURE/UNKNOWN to CONFIRMED without decision
- Scope expansion within a phase

**Amendment Process:**
1. Create DECISIONS.md entry: `ROADMAP-AMEND-XXX`
2. Reference constitution section affected
3. State proposed change and rationale
4. Human approval required
5. Update roadmap and decision log atomically

---

## 9. AI CODING AGENT RULES

**Every Agent Working on Ghost Must:**

1. **Read First, Write Second**
   - Read PROJECT_CONSTITUTION.md completely before any modification
   - Read MASTER_ROADMAP.md before starting a new phase
   - Read PRODUCT_SPEC.md before implementing product behavior
   - Read DECISIONS.md (when exists) before architectural changes

2. **Never Silently Change Core Documents**
   - No edits to constitution, roadmap, spec without explicit instruction
   - No "helpful" additions to product behavior
   - No "obvious" feature implementations

3. **Respect the Boundaries**
   - If a request violates constitution → REFUSE, cite section
   - If a request expands scope → REFUSE, cite anti-scope rule
   - If uncertain → ASK, don't assume

4. **Explain Conflicts Before Proceeding**
   - "This implementation would violate Constitution Section 3.2 because..."
   - "This feature falls under banned category X per Section 6..."
   - Present the conflict, don't work around it

5. **Keep Changes Small and Testable**
   - One logical change per commit/PR
   - Each change traceable to a spec requirement
   - No "refactoring" that changes behavior

6. **Record Architectural Decisions**
   - Once DECISIONS.md exists, every architectural choice → decision entry
   - Format: `DECISION-XXX: Title | Context | Options | Decision | Rationale | Date`

7. **Treat Privacy as First-Class**
   - Every data structure: "What privacy principle does this serve?"
   - Every network call: "Does this leak location or identity?"
   - Every storage: "What expires when?"

8. **Validation Before Completion**
   - Re-read constitution/roadmap/spec after changes
   - Verify no contradictions introduced
   - Run any existing tests
   - Confirm no application code written when only documents requested

---

## 10. CONSTITUTION AMENDMENT PROCESS

**This document can only be amended by explicit human instruction.**

**Amendment Format:**
```
AMENDMENT-XXX: Section X.Y | Old Text | New Text | Rationale | Date | Approved By
```

**No agent may propose, draft, or implement constitutional amendments.**

---

**END OF CONSTITUTION**

*This document is the contract between Ghost's vision and its implementation. Every line of code exists to serve these principles. If code contradicts constitution, the code is wrong.*
