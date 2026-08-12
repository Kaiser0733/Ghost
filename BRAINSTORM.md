# GHOST BRAINSTORM — Deep Product Exploration

## Core Concept Analysis

**What is Ghost really?**
- A social layer that sits on top of physical reality
- Transforms coincidence into connection
- The "encounter" is the atomic unit — not profiles, not posts, not messages
- Anonymous by default, reveal is opt-in and mutual

**The Encounter Concept**
- Not a "match" — a match implies intent and evaluation
- An encounter is something that HAPPENED — past tense, factual
- "You crossed paths with someone" — this is a statement of fact
- The encounter exists independently of whether either party acts on it
- Temporary — it has a lifespan, then fades (unless both choose to extend)

**Anonymous Identity**
- Not "anonymous" as in "hidden identity" — anonymous as in "identity not yet relevant"
- You don't have a profile. You have encounters.
- Your "identity" in Ghost is the set of encounters you've had
- No username, no avatar, no bio — those are features of OTHER apps
- The encounter IS the profile. "This person was at the same coffee shop at 3pm on Tuesday."

**Temporary Encounter**
- Encounters have a TTL (time-to-live)
- Default: 24-72 hours? A week?
- During TTL: you can see the encounter, react to it, choose to reveal
- After TTL: encounter dissolves unless both parties chose to connect
- This creates urgency without pressure — "this moment is fleeting"

**Reveal/Connection**
- Mutual opt-in only. No unilateral reveal.
- Reveal = "I'm willing to know who you are, and I'm willing for you to know who I am"
- Connection = persistent link beyond the encounter TTL
- What does connection enable? Not messaging necessarily. Maybe just "we know each other now"
- The connection itself might be the product — a web of "people I've crossed paths with"

## Privacy Model Deep Dive

**Core principle: Ghost never knows your location either**
- Not just "doesn't share with other users" — the SYSTEM shouldn't have raw location history
- Proximity verification without location exposure
- This is a HARD technical problem — not solved in Phase 1
- Phase 1 simulates, but architecture must not bake in location tracking

**What data exists?**
- Encounter records: (user_a_hash, user_b_hash, timestamp_hash, venue_category?, encounter_id)
- No GPS coordinates stored
- No venue names stored (maybe category: "cafe", "park", "transit")
- User identifiers: cryptographic, rotated, not linkable to real identity

**Threat models:**
- Ghost server compromised → no location history to leak
- User A tries to deduce User B's location → impossible from encounter data alone
- Mass surveillance → encounter graph reveals social graph but not movement patterns
- Stalking/harassment → no persistent identity to target, encounters expire

## Anti-Scope-Creep Boundaries

**What Ghost is NOT:**
- NOT a dating app → no swiping, no preferences, no "looking for"
- NOT a social network → no feed, no followers, no posts, no likes
- NOT a messaging app → no chat, no DMs, no notifications "X messaged you"
- NOT a location tracker → no "where are my friends", no check-ins, no history map
- NOT an event app → no "happening nearby", no RSVP, no calendar
- NOT a productivity tool → no "networking", no "professional connections"
- NOT an AI chatbot → no "suggestions", no "conversation starters", no LLM features

**What Ghost IS:**
- A system that detects: "two Ghost users were near each other"
- A system that creates: "an encounter record"
- A system that allows: "mutual reveal → connection"
- That's it. Everything else is scope creep.

## Prototype Boundaries (Phase 1 Web)

**What we SIMULATE:**
- Two users exist
- An encounter occurs between them (triggered manually in prototype)
- Both users see the encounter
- Both can choose to reveal
- If both reveal → connection formed

**What we do NOT build in prototype:**
- Real proximity detection (Bluetooth, UWB, WiFi, GPS)
- Background location monitoring
- Cryptographic identity system
- Server infrastructure
- Push notifications
- Real-time sync
- Account creation/login (simulate with localStorage)
- Any "profile" concept

**What the prototype MUST demonstrate:**
- The emotional arc: "someone was near me" → "who was it?" → "do I want to know?" → "they want to know too" → connection
- The anonymity: no names, no photos, just "an encounter"
- The temporality: encounter has a countdown
- The mutual consent: reveal requires both parties

## Target User Philosophy

**Who is this for?**
- People who feel social apps are performative and draining
- People who value serendipity over curation
- People who want connection without the "profile maintenance" tax
- People who miss "running into someone" in a world of algorithms

**What do they NOT want?**
- To curate a profile
- To swipe
- To message strangers
- To be found by strangers
- To have their location tracked
- To perform sociality

**The "Anti-User" — who we explicitly design against:**
- People who want to "build a following"
- People who want to "network"
- People who want to browse profiles
- People who want to message anyone
- People who want location history

## Technical Architecture Direction

**Separation of concerns (eventual):**
```
┌─────────────────┐
│  Client Interface  │  (React Web / Compose Android)
└────────┬────────┘
         │
┌────────▼────────┐
│  Ghost Core/Domain  │  (Encounter logic, identity, reveal, TTL)
└────────┬────────┘
         │
    ┌────┴────┐
    ▼         ▼
┌───────┐ ┌──────────┐
│ Backend│ │ Proximity │
│/Network│ │ Technology│
└───────┘ └──────────┘
```

**Key architectural decisions NOT made yet:**
- Backend: Firebase? Custom? P2P? Sync protocol?
- Proximity: Bluetooth LE? UWB? WiFi RTT? Audio? Hybrid?
- Identity: Key rotation? Group signatures? Anonymous credentials?
- Storage: Local-first? Server-assisted? CRDTs?
- Transport: WebRTC? WebSockets? MQTT? Custom?

**Phase 1 prototype architecture:**
- Single HTML/JS/TS file? Or minimal React?
- localStorage for "user" and "encounters"
- Manual "simulate encounter" button
- No backend at all
- This validates the EXPERIENCE, not the tech

## Decision-Making Rules

**How do we decide what to build?**
1. Does it serve the core concept: coincidence → encounter → connection?
2. Does it violate privacy principle?
3. Does it add user friction?
4. Is it scope creep?
5. If yes to any → DON'T BUILD (or mark FUTURE/UNKNOWN)

**Roadmap changes require:**
- Explicit decision document (DECISIONS.md)
- Reference to constitution principle
- Clear rationale
- No silent changes

## AI Agent Rules

**For future agents working on Ghost:**
- Read constitution first — it's the contract
- Read roadmap — it's the plan
- Read spec — it's the product
- Don't improvise features
- Don't "helpfully" add things
- If unsure → ask, don't assume
- Privacy is not negotiable
- Scope creep is not negotiable

## Unresolved Questions (Mark as UNKNOWN)

1. Encounter TTL duration — 24h? 72h? 1 week? Configurable?
2. What "venue category" granularity? "indoor/outdoor" vs "cafe/park/transit"?
3. Reveal mechanism — what exactly is revealed? Just "willing to connect"? Name? Photo? Contact?
4. Connection persistence — what does a connection enable? Just "mutual knowledge"? Messaging? Something else?
5. Encounter density — what if you have 50 encounters/day? UI handling?
6. Fake/spam encounters — how prevented in real system?
7. Battery impact of real proximity — acceptable threshold?
8. Cross-platform (iOS) — ever? When?
9. Monetization — never? Freemium? Donations? Unknown.
10. Legal jurisdiction for privacy — GDPR? CCPA? Others?

## Confirmed vs Proposed vs Future vs Unknown

**CONFIRMED (from user spec):**
- Core concept: physical crossing → anonymous encounter → optional connection
- Privacy: never expose exact location to other users
- User friction: minimal deliberate input
- Anti-scope: not dating, not social network, not messaging, not tracker
- Phase 1: Web prototype (React + TypeScript)
- Future Android: Kotlin + Jetpack Compose
- Architecture separation: client / core / backend / proximity

**PROPOSED (my suggestions for discussion):**
- Encounter TTL: 72 hours default
- Venue category only (no names)
- Mutual reveal = connection (no messaging)
- Local-first prototype with localStorage

**FUTURE (explicitly deferred):**
- Real proximity detection
- Cryptographic identity
- Backend infrastructure
- Push notifications
- iOS version
- Monetization
- Any feature not in core concept

**UNKNOWN (needs research/decision):**
- All the unresolved questions above
- Technical feasibility of privacy-preserving proximity
- Battery/performance constraints
- Legal/compliance requirements
- Network effects / cold start problem

