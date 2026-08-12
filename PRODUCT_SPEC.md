# PRODUCT SPECIFICATION — Ghost

**Version:** 1.0  
**Status:** CONTROLLED — Subordinate to PROJECT_CONSTITUTION.md and MASTER_ROADMAP.md  
**Scope:** Defines the current product concept and Phase 1 prototype experience.

---

## LABELING CONVENTION

Every statement in this document is labeled with its certainty level:

- **CONFIRMED** — Decided, committed, will be built
- **PROPOSED** — Strong candidate, awaiting explicit decision
- **FUTURE** — Explicitly deferred to later phase, not in current scope
- **UNKNOWN** — Genuinely undecided, needs research/decision

**Rule:** No FUTURE/UNKNOWN item may be implemented without promotion to CONFIRMED via DECISIONS.md.

---

## 1. PROBLEM / OPPORTUNITY

**CONFIRMED:** Modern social apps optimize for engagement, not connection. They require profiles, curation, performance, and constant input. They replace serendipity with algorithms.

**CONFIRMED:** Physical coincidence — crossing paths with someone interesting — is a fundamental human experience that digital products have not captured. Current apps either track location (creepy) or require intent (swiping, checking in).

**CONFIRMED:** There is a latent desire for connection that emerges from shared physical presence without the pressure of identity, performance, or persistence.

**PROPOSED:** The "right" product creates a thin digital layer over physical coincidence: *you were near someone → an encounter exists → you both decide what (if anything) to do with it.*

---

## 2. GHOST'S CORE INTERACTION

**CONFIRMED — The Atomic Loop:**
```
1. Two Ghost users cross paths in physical space (proximity detected)
2. Ghost creates an anonymous Encounter record on both devices
3. Encounter appears: time, venue category, TTL countdown
4. Each user independently chooses: Reveal or Let Expire
5. If BOTH choose Reveal → Connection formed
6. If either lets expire → Encounter dissolves, no trace
```

**CONFIRMED — Key Properties:**
- **Asymmetric information:** Neither user knows the other's choice until mutual reveal
- **No rejection signal:** Letting expire is indistinguishable from "hasn't seen it yet"
- **No pressure:** TTL creates natural deadline without social obligation
- **Mutual consent is absolute:** Unilateral reveal is impossible by design

**PROPOSED — Encounter TTL:** 72 hours (3 days) default. Configurable later? UNKNOWN.

**PROPOSED — Venue Categories:** Indoor / Outdoor / Transit / Commercial / Park / Unknown. No venue names. CONFIRMED: No exact locations.

---

## 3. TARGET USER PHILOSOPHY

**CONFIRMED — Primary User:**
- Values serendipity over curation
- Finds profile maintenance draining
- Wants connection without performance
- Comfortable with ambiguity and anonymity
- Protects their privacy instinctively
- Misses "running into people" in algorithmic world

**CONFIRMED — Anti-User (Explicitly Not Designed For):**
- Wants to "build a following" or "network"
- Wants to browse profiles before deciding
- Wants to message strangers unilaterally
- Wants location history of self or others
- Wants gamification, streaks, metrics
- Wants AI suggestions or conversation starters

**PROPOSED — Adoption Model:** Organic, word-of-mouth, density-dependent. No viral loops, no referrals, no growth hacking.

---

## 4. USER JOURNEY

### 4.1 Idealized Real-World Journey (Post-Launch)

**CONFIRMED:**
```
Day 0: Install Ghost → Grant permissions → Put phone in pocket → Live life
Day 3: Open app → See "Encounter: Tuesday 2pm, Cafe" → 48h remaining
Day 3: Tap "Reveal" → Wait...
Day 4: Notification: "Connection formed" → See mutual willingness
Day 4+: What happens next? (UNKNOWN - intentionally undefined)
```

**CONFIRMED:** The app is not the destination. The encounter is the artifact. The connection is the outcome.

### 4.2 Phase 1 Prototype Journey (Simulation)

**CONFIRMED:**
```
Open prototype → See "User A" badge
Tap "Simulate Encounter" → Encounter appears for User A
Switch to "User B" → Same encounter appears
User A: Tap "Reveal" → State: "Waiting for other..."
User B: Tap "Reveal" → State: "CONNECTED"
Observe: Connection UI, encounter expires if no mutual reveal
```

---

## 5. ENCOUNTER CONCEPT

### 5.1 Encounter Data Model (CONFIRMED for Prototype)

```typescript
interface Encounter {
  id: string;                    // UUID, local only
  timestamp: number;             // Unix ms when proximity detected
  venueCategory: VenueCategory;  // Enum, no names
  ttl: number;                   // Ms until expiry (default: 72h)
  status: EncounterStatus;       // ACTIVE | REVEALED_ME | REVEALED_BOTH | EXPIRED
  myReveal: boolean;             // Did I press reveal?
  otherReveal: boolean;          // Did they press reveal? (synced in real app)
  connectionId?: string;         // Set if mutual reveal
}
```

### 5.2 VenueCategory Enum (PROPOSED)

```typescript
enum VenueCategory {
  INDOOR_COMMERCIAL = 'indoor_commercial',  // Cafe, shop, mall
  INDOOR_PUBLIC = 'indoor_public',          // Library, museum, station
  OUTDOOR_PARK = 'outdoor_park',            // Park, plaza, garden
  OUTDOOR_STREET = 'outdoor_street',        // Sidewalk, crossing
  TRANSIT = 'transit',                      // Train, bus, subway
  UNKNOWN = 'unknown'                       // Fallback
}
```

**CONFIRMED:** No GPS coordinates, no venue names, no addresses stored or displayed.

### 5.3 Encounter Status Transitions (CONFIRMED)

```
ACTIVE
  ├── [TTL expires] → EXPIRED
  ├── [Me: Reveal] → REVEALED_ME
  │     └── [Other: Reveal] → REVEALED_BOTH → CONNECTION
  │     └── [TTL expires] → EXPIRED (no connection)
  └── [Other: Reveal first] → REVEALED_OTHER (hidden from me)
        └── [Me: Reveal] → REVEALED_BOTH → CONNECTION
        └── [TTL expires] → EXPIRED
```

**CONFIRMED:** User never sees "Other revealed first" state. Only sees: Active / I Revealed / Connected / Expired.

---

## 6. ANONYMOUS IDENTITY CONCEPT

**CONFIRMED — No Profiles:**
- No username, display name, avatar, bio
- No "account" in traditional sense
- No email, phone, social login

**CONFIRMED — Identity = Encounter History:**
- Your "identity" in Ghost is the set of encounters you've had
- Encounters are anonymous: "Someone at a cafe Tuesday 2pm"
- No persistent identifier visible to other users

**FUTURE — Cryptographic Identity (Phase 4+):**
- Rotating anonymous key pairs
- Private set intersection for proximity verification
- No long-term linkable identifier
- Server (if any) learns nothing about identity graph

**UNKNOWN:** Exact cryptographic scheme. Phase 3 research will determine.

---

## 7. TEMPORARY ENCOUNTER CONCEPT

**CONFIRMED — TTL (Time-To-Live):**
- Every encounter has an expiry
- Default: PROPOSED 72 hours
- Countdown visible to user
- After expiry: encounter data deleted locally (real app) or marked EXPIRED (prototype)

**CONFIRMED — Why Temporary?**
- Creates urgency without pressure
- Prevents accumulation of "stale" encounters
- Aligns with privacy principle: data expires by default
- Forces decision: act now or let go

**PROPOSED — No Extension:** TTL cannot be extended. If connection forms, it persists. If not, it's gone.

**UNKNOWN:** What if both users are offline for 72h? (Phase 4+ sync problem)

---

## 8. REVEAL / CONNECTION CONCEPT

### 8.1 Reveal Action (CONFIRMED)

- Single tap: "Reveal willingness"
- No text, no message, no choice of what to reveal
- Binary: willing / not willing
- Irrevocable once pressed (cannot "un-reveal")

### 8.2 Mutual Reveal = Connection (CONFIRMED)

- Connection forms IFF both users press Reveal before TTL
- Connection is a persistent mutual link
- What a connection *enables* is UNKNOWN (intentionally)

### 8.3 What Connection Enables (UNKNOWN — Explicitly Deferred)

**Options (all FUTURE/UNKNOWN):**
- Nothing beyond "we know each other mutually revealed"
- Ability to see each other's future encounters (opt-in)
- Messaging (BANNED per Constitution — would require decision to unban)
- Contact exchange (manual, outside app)
- Shared "connection space" (calendar? notes? UNKNOWN)
- Simply a persistent record: "Connected since [date]"

**CONSTITUTIONAL CONSTRAINT:** Any connection feature must pass anti-scope-creep test (Constitution Section 6). Messaging, profiles, social features are BANNED unless explicitly amended.

---

## 9. PRIVACY EXPECTATIONS

### 9.1 User-Facing Promises (CONFIRMED)

| Promise | Implementation Requirement |
|---------|---------------------------|
| "Ghost never knows where you are" | No GPS upload, no location history on server |
| "Other users never see your location" | Encounter contains only venue category + time |
| "Encounters expire automatically" | TTL enforcement, local deletion |
| "Reveal is mutual or nothing" | Cryptographic commitment scheme (Phase 4+) |
| "No profile, no tracking" | No persistent ID, rotating keys |
| "Install and forget" | Background operation, no engagement loops |

### 9.2 Threat Model (CONFIRMED)

**Must Defend Against:**
- Passive server surveillance → encounter graph only, no location
- Active server compromise → minimal data at rest
- User A inferring User B's location → impossible from encounter data
- Stalking/harassment → no persistent identity, encounters expire
- Spam/fake encounters → rate limiting, proximity proof (Phase 4+)
- Legal data requests → minimal data to surrender

### 9.3 Data Minimization Inventory (CONFIRMED)

**Data That EXISTS:**
- Local: Encounter records (id, time, category, TTL, reveal states)
- Local: Rotating identity keys
- Local: Connection records (mutual reveal confirmations)
- Network (Phase 4+): Encrypted proximity proofs, no plaintext location

**Data That NEVER EXISTS:**
- GPS coordinates (client or server)
- Venue names/addresses
- User profiles (name, photo, bio, contacts)
- Message content
- Social graph (followers, friends, blocks)
- Analytics/telemetry
- Device identifiers (advertising ID, hardware ID)

---

## 10. PROTOTYPE BOUNDARIES (Phase 1 Web)

### 10.1 What Prototype INCLUDES (CONFIRMED)

| Feature | Implementation |
|---------|----------------|
| Two simulated users | localStorage key: `ghost_user` = 'A' or 'B' |
| Manual encounter trigger | Button: "Simulate Encounter" |
| Encounter display | Card: time, category, countdown |
| Reveal action | Button: "Reveal" → updates local state |
| Mutual reveal detection | Poll localStorage for other user's reveal |
| Connection state | Visual: "Connected" badge |
| TTL expiry | setInterval check, auto-expire |
| User switch | Toggle: "View as User A / User B" |
| No backend | All localStorage, single-tab or multi-tab |

### 10.2 What Prototype EXCLUDES (CONFIRMED)

| Excluded | Reason |
|----------|--------|
| Real proximity detection | Phase 3/4 |
| Backend/server | Phase 4+ |
| Cross-device sync | Phase 4+ (local-only prototype) |
| Cryptographic identity | Phase 4+ |
| Background operation | Web limitation, not product |
| Push notifications | Phase 4+ |
| Real time sync | localStorage polling is fine |
| Onboarding | Zero-setup is the point |
| Settings/preferences | No prefs in MVP |
| Analytics | Never |

### 10.3 Prototype Success Criteria (CONFIRMED)

- [ ] Two browser tabs (or devices) can simulate the full loop
- [ ] Encounter appears on both "users" simultaneously
- [ ] Countdown timer visible and accurate
- [ ] Reveal → Waiting → Connected flow works
- [ ] Expiry → Expired state works
- [ ] No explanation needed — concept self-evident
- [ ] Emotional resonance: "Oh, this is interesting" not "How does this work?"

---

## 11. FUTURE vs CURRENT FUNCTIONALITY

| Area | Current (Phase 1) | Future (Phase 4+) | Status |
|------|-------------------|-------------------|--------|
| Proximity Detection | Manual button | BLE/UWB background | FUTURE |
| Identity | localStorage 'A'/'B' | Rotating crypto keys | FUTURE |
| Storage | localStorage | Room/SQLDelight + encryption | FUTURE |
| Sync | None (single device sim) | P2P or server-assisted | FUTURE |
| Platform | Web (React/TS) | Android (Kotlin/Compose) | FUTURE |
| iOS | Not planned | UNKNOWN | UNKNOWN |
| Backend | None | Minimal / P2P / Custom | UNKNOWN |
| Connection Features | "Connected" badge only | UNKNOWN | UNKNOWN |
| Monetization | None | UNKNOWN | UNKNOWN |
| Legal/Compliance | N/A (prototype) | GDPR, CCPA, etc. | FUTURE |

---

## 12. OPEN QUESTIONS (UNKNOWN — Require Decisions)

| # | Question | Phase to Resolve |
|---|----------|------------------|
| 1 | Exact TTL duration (24h/72h/1w?) | Phase 1 testing → Phase 2 decision |
| 2 | Venue category granularity (5 vs 10 vs dynamic?) | Phase 1 testing |
| 3 | What does "Connection" enable? | Phase 2 testing → Phase 4 decision |
| 4 | Encounter density handling (50/day UI?) | Phase 2 testing |
| 5 | Fake/spam encounter prevention | Phase 3 research |
| 6 | Battery budget for background proximity | Phase 3 research |
| 7 | iOS background proximity feasibility | Phase 3 research |
| 8 | Legal jurisdiction for privacy compliance | Phase 6 |
| 9 | Cryptographic protocol for private proximity | Phase 3 research |
| 10 | Server vs P2P vs hybrid architecture | Phase 3 research |
| 11 | Cross-device sync protocol | Phase 4 |
| 12 | Connection persistence semantics | Phase 2 testing → Phase 4 decision |

---

## 13. DECISION LOG REFERENCE

**All promotions from PROPOSED/FUTURE/UNKNOWN → CONFIRMED must be recorded in DECISIONS.md:**

```
DECISION-XXX: SPEC-PROMOTE | Encounter TTL: 72h | Phase 2 testing showed 72h optimal | 2026-XX-XX
DECISION-XXX: SPEC-CONFIRM | Connection enables: contact exchange only | Constitution amendment required | 2026-XX-XX
```

---

**END OF PRODUCT SPECIFICATION**

*This spec defines what we're building NOW (Phase 1) and what we've explicitly deferred. Anything not labeled CONFIRMED or PROPOSED is not in scope. When in doubt: check Constitution → Roadmap → Spec → Decisions. If still unclear: ask, don't assume.*