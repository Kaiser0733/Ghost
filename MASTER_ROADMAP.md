# MASTER ROADMAP — Ghost

**Version:** 1.0
**Status:** CONTROLLED DOCUMENT — Changes require explicit decision record (DECISIONS.md)
**Authority:** Subordinate to PROJECT_CONSTITUTION.md

---

## ROADMAP OVERVIEW

| Phase | Name | Status | Objective |
|-------|------|--------|-----------|
| 0 | Foundation | PLANNED | Establish project infrastructure, constitution, spec, tooling |
| 1 | Web Prototype | PLANNED | Build React/TS prototype simulating encounter experience |
| 2 | Prototype Testing | PLANNED | Validate emotional resonance and UX with real users |
| 3 | Proximity Feasibility | PLANNED | Research privacy-preserving proximity detection |
| 4 | Native Android Prototype | PLANNED | Build Kotlin/Compose app with real proximity |
| 5 | Closed Testing | PLANNED | Small-group real-world testing |
| 6 | Privacy/Security Hardening | PLANNED | Audit, threat model, harden |
| 7 | Play Store Preparation | PLANNED | Compliance, store assets, release pipeline |
| 8 | Public Launch | PLANNED | Launch to public |
| 9 | Early Network Growth | FUTURE | Organic growth, network effects |
| 10 | Long-term Evolution | UNKNOWN | Post-launch evolution |

---

## PHASE 0 — FOUNDATION

**Objective:** Establish unshakeable project foundation before any code.

**What Must Be Completed:**
- [x] PROJECT_CONSTITUTION.md (permanent principles)
- [x] MASTER_ROADMAP.md (this document)
- [x] PRODUCT_SPEC.md (product definition)
- [ ] DECISIONS.md (decision log template)
- [ ] Git repository initialized
- [ ] Development environment documented
- [ ] Agent onboarding guide (AGENTS.md or similar)
- [ ] Basic project structure created

**What Must NOT Be Built Yet:**
- Any application code (React, Android, backend)
- Proximity detection code
- Encounter logic implementation
- UI components

**Exit Criteria:**
- All three foundation documents complete and consistent
- No contradictions between constitution, roadmap, spec
- Human approval of all three documents
- DECISIONS.md template ready

**Timeline Estimate:** 1-2 sessions

---

## PHASE 1 — WEB PROTOTYPE

**Objective:** Build a React + TypeScript prototype that simulates the Ghost encounter experience end-to-end.

**What Must Be Completed:**
- Minimal React + TypeScript + Vite setup
- Local "user" simulation (localStorage-based, no auth)
- Encounter data model (TypeScript types)
- Encounter simulation trigger (manual button for testing)
- Two "user" views (User A / User B switchable)
- Encounter display: anonymous, temporal, venue category only
- Countdown timer (TTL visualization)
- Reveal action (mutual consent logic)
- Connection state (mutual reveal = connection)
- Basic styling (minimal, atmospheric, not "app-like")
- No backend — all local simulation

**What Must NOT Be Built Yet:**
- Real proximity detection (Bluetooth, GPS, etc.)
- Backend/server of any kind
- Real user accounts/authentication
- Push notifications
- Real-time sync between devices
- Cryptographic identity
- Venue names or exact locations
- Messaging/chat
- Profile system
- Settings/preferences
- Onboarding flow
- Analytics/telemetry

**Prototype Scope (Explicit Boundary):**
```
+-----------------------------------------+
|  PHASE 1 PROTOTYPE SCOPE                |
+-----------------------------------------+
|  [x] Two simulated users (A/B toggle)   |
|  [x] Manual "create encounter" button   |
|  [x] Encounter appears for both users   |
|  [x] Shows: time, venue category, TTL   |
|  [x] "Reveal" button (mutual consent)   |
|  [x] Connection state on mutual reveal  |
|  [x] Encounter expires after TTL        |
|  [ ] Real proximity                     |
|  [ ] Backend                            |
|  [ ] Auth/accounts                      |
|  [ ] Cross-device sync                  |
|  [ ] Messaging                          |
|  [ ] Profiles                           |
+-----------------------------------------+
```

**Exit Criteria:**
- Two people can sit side-by-side, open prototype on two devices
- One triggers "simulate encounter"
- Both see the anonymous encounter with countdown
- Both can press "Reveal"
- On mutual reveal -> connection state shown
- Encounter expires after TTL if no mutual reveal
- Experience feels emotionally resonant without explanation
- No console errors, clean TypeScript build

**Timeline Estimate:** 3-5 sessions

**Architecture Note:**
- Ghost Core logic (encounter, TTL, reveal) extracted as pure TypeScript module
- Client is thin React layer
- This core module ports to Kotlin in Phase 4

---

## PHASE 2 — PROTOTYPE TESTING

**Objective:** Validate that the core experience is compelling before investing in native/proximity.

**What Must Be Completed:**
- Recruit 5-10 test users (pairs)
- Testing protocol defined (tasks, questions, observation)
- Conduct testing sessions (remote or in-person)
- Document findings: emotional response, comprehension, friction
- Go/No-Go decision for Phase 3/4

**What Must NOT Be Built Yet:**
- Real proximity detection
- Native Android app
- Backend infrastructure
- Any feature not in Phase 1 prototype

**Testing Questions (Must Answer):**
1. Do users understand the concept without explanation?
2. Does the encounter feel meaningful or arbitrary?
3. Is the anonymity comforting or confusing?
4. Does the TTL create urgency or anxiety?
5. Is mutual reveal intuitive?
6. Would they use this in real life?
7. What's missing? What's unnecessary?

**Exit Criteria:**
- Testing completed with >=5 user pairs
- Clear Go/No-Go decision documented in DECISIONS.md
- If Go: specific UX improvements identified for Phase 4
- If No-Go: fundamental concept revision (return to Constitution)

**Timeline Estimate:** 2-3 sessions + recruiting time

---

## PHASE 3 — TECHNICAL FEASIBILITY OF REAL PROXIMITY DETECTION

**Objective:** Determine if privacy-preserving proximity detection is technically viable on Android.

**What Must Be Completed:**
- Research survey: Bluetooth LE, UWB, WiFi RTT, Audio, Hybrid approaches
- Privacy analysis for each: what data leaks, what's required
- Battery impact assessment (literature + small experiments)
- Background execution constraints on Android (Doze, app standby, foreground service)
- iOS feasibility (background restrictions)
- Server vs. P2P vs. hybrid architecture trade-offs
- Cryptographic primitive selection (PSI, private set intersection, etc.)
- Prototype: minimal Android app testing one proximity approach
- Feasibility report with Go/No-Go for Phase 4

**What Must NOT Be Built Yet:**
- Full Android app
- Production proximity system
- Backend for proximity
- Integration with Phase 1 prototype

**Key Research Questions:**
1. Can we detect "meaningful proximity" (same room, ~10-50m) without GPS?
2. Can we do it without either device learning the other's location?
3. Can we do it in background with <5% battery/hour?
4. Does Android allow the required background scans?
5. What's the false positive/negative rate?
6. How do we prevent relay/spam attacks?

**Exit Criteria:**
- Feasibility report complete
- Clear technical path identified OR fundamental blocker found
- Go/No-Go decision in DECISIONS.md
- If No-Go: pivot strategy documented (P2P? Different tech? Sunset?)

**Timeline Estimate:** 4-8 sessions (research-heavy)

---

## PHASE 4 — NATIVE ANDROID PROTOTYPE

**Objective:** Build a Kotlin + Jetpack Compose app with real proximity detection.

**What Must Be Completed:**
- Kotlin + Jetpack Compose project setup
- Ghost Core ported from TypeScript to Kotlin (shared logic)
- Proximity detection integration (based on Phase 3 choice)
- Foreground service for background scanning
- Local encounter database (Room/SQLDelight)
- Cryptographic identity system (key rotation, anonymous IDs)
- Encounter creation from proximity events
- UI matching Phase 1 prototype experience
- Two-device real-world testing

**What Must NOT Be Built Yet:**
- Backend/server (still local-only)
- Cross-device sync (encounters only local)
- Push notifications
- iOS version
- Play Store release
- Messaging/profiles/social features

**Architecture Target:**
```
+---------------------------------------------------------+
|  Android App (Kotlin + Compose)                         |
+---------------------------------------------------------+
|  UI Layer (Compose)                                     |
|       |                                                 |
|  Ghost Core (Kotlin) -- pure logic, unit-testable       |
|       |                                                 |
|  +-----------+-----------+                              |
|  |           |           |                              |
|  v           v           v                              |
| Proximity    Local       (Future: Backend Sync)         |
| Engine       Storage     (Room)                         |
| (BLE/UWB)    Encounter DB, Identity Keys                |
+---------------------------------------------------------+
```

**Exit Criteria:**
- App installs and runs on two physical Android devices
- Devices detect proximity in background (pocket-to-pocket)
- Encounter created automatically on both devices
- Encounter UI matches Phase 1 experience
- Mutual reveal -> connection works
- Battery impact measured and documented
- No crashes, clean build

**Timeline Estimate:** 6-10 sessions

---

## PHASE 5 — CLOSED TESTING

**Objective:** Real-world validation with small group over extended period.

**What Must Be Completed:**
- Recruit 20-50 testers (opt-in, informed consent)
- TestFlight/Play Console internal testing track
- 2-4 week testing period
- Feedback collection (quantitative + qualitative)
- Bug fixes, UX iterations
- Privacy audit of actual data flows
- Battery/performance monitoring in wild

**What Must NOT Be Built Yet:**
- Public launch infrastructure
- Marketing/landing page
- Monetization
- iOS app
- Backend (still local-only or minimal sync)

**Exit Criteria:**
- Testing period complete
- >=80% of testers report "would recommend"
- No critical privacy violations found
- Battery impact acceptable (<5%/day)
- Go/No-Go for Phase 6/7 in DECISIONS.md

**Timeline Estimate:** 4-6 weeks calendar time, 3-5 sessions active work

---

## PHASE 6 — PRIVACY/SECURITY HARDENING

**Objective:** Production-grade privacy and security before any public exposure.

**What Must Be Completed:**
- Third-party security audit (or thorough self-audit with checklist)
- Threat model document (STRIDE or similar)
- Penetration testing of proximity protocol
- Data flow audit: every byte stored/transmitted justified
- Encryption at rest (local DB) and in transit (if any)
- Key management review (rotation, compromise recovery)
- Anti-spam/anti-abuse for proximity (rate limiting, reputation?)
- Legal compliance review (GDPR, CCPA, local laws)
- Data deletion/export implementation
- Privacy policy (minimal, honest)

**What Must NOT Be Built Yet:**
- Public launch
- Marketing
- Growth features

**Exit Criteria:**
- Audit complete, critical findings resolved
- Threat model signed off
- Legal compliance confirmed
- Data flows documented and minimal
- DECISIONS.md records all security decisions

**Timeline Estimate:** 3-6 sessions

---

## PHASE 7 — PLAY STORE PREPARATION

**Objective:** Prepare for public Android release.

**What Must Be Completed:**
- Play Console listing (description, screenshots, privacy policy URL)
- Target API level compliance
- Permissions justification (foreground service, Bluetooth, etc.)
- Release signing setup
- Staged rollout plan (1% -> 5% -> 25% -> 100%)
- Crash reporting (opt-in, privacy-respecting)
- In-app feedback mechanism (optional, no PII)
- App bundle optimization (size, dynamic delivery)

**What Must NOT Be Built Yet:**
- Public marketing
- iOS version
- Backend scaling (still minimal)

**Exit Criteria:**
- App passes Play Console review
- Internal test track stable
- Staged rollout plan approved
- Launch date set

**Timeline Estimate:** 2-3 sessions + Play review time

---

## PHASE 8 — PUBLIC LAUNCH

**Objective:** Launch Ghost to the public on Android.

**What Must Be Completed:**
- Public Play Store release
- Minimal landing page (privacy policy, concept, download link)
- Launch day monitoring
- First-week incident response
- Early user feedback triage

**What Must NOT Be Built Yet:**
- Growth hacking
- Referral systems
- Social features
- iOS app
- Monetization

**Exit Criteria:**
- Live on Play Store
- No critical incidents in first week
- Basic metrics: installs, encounter rate, retention (privacy-safe)

**Timeline Estimate:** 1 session + monitoring

---

## PHASE 9 — EARLY NETWORK GROWTH

**Status:** FUTURE — Details TBD after launch data

**Objective:** Organic growth to critical mass in target areas.

**Potential Activities (ALL PROPOSED/UNKNOWN):**
- City/region launch strategy
- Community building (organic only)
- Press/word of mouth
- Network effect measurement
- Density optimization (encounters per user per week)

**Constraints:**
- No paid acquisition
- No viral loops that violate friction principles
- No features that violate anti-scope rules
- Growth serves the concept, not vice versa

**Exit Criteria:** TBD (defined when Phase 8 complete)

---

## PHASE 10 — LONG-TERM EVOLUTION

**Status:** UNKNOWN — Cannot be planned from here.

**Potential Directions (ALL SPECULATIVE):**
- iOS support
- Web companion (read-only encounter history)
- Connection deepening (what happens after reveal?)
- Proximity technology evolution (UWB, 6G, etc.)
- International expansion
- Open protocol / federation
- Research partnerships

**Governance:**
- Every evolution step requires constitutional alignment check
- No feature without DECISIONS.md entry
- Constitution amendment process for fundamental changes

---

## ROADMAP CONTROL RULES

**This Document Is Controlled.**

| Action | Required |
|--------|----------|
| Advance phase | Exit criteria met + DECISIONS.md entry |
| Modify phase scope | Constitution reference + DECISIONS.md entry |
| Add phase | Constitutional justification + DECISIONS.md entry |
| Remove phase | Explicit sunset decision + DECISIONS.md entry |
| Promote FUTURE->CONFIRMED | Explicit decision + DECISIONS.md entry |
| Change timeline | Allowed within phase, no decision needed |

**Decision Log Format (DECISIONS.md):**
```
DECISION-001: ROADMAP-ADVANCE | Phase 0 -> Phase 1 | Exit criteria met: all 3 foundation docs approved | 2026-08-11
DECISION-002: TECH-SELECT | Proximity: Bluetooth LE + PSI | Phase 3 research conclusion | 2026-XX-XX
```

---

## CURRENT PHASE: 0 (FOUNDATION)

**Next Action:** Complete foundation documents, human review, then advance to Phase 1.

---

**END OF ROADMAP**

*This roadmap is a commitment to disciplined development. Every phase gate exists to prevent wasted work. If a phase's exit criteria aren't met, we don't advance -- we iterate or pivot.*