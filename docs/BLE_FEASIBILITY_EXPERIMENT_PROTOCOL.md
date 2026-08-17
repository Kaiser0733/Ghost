# Phase 3 Physical BLE Experiment Protocol

**Status:** DRAFT — For Physical Experimentation Preparation  
**Lab Version:** Phase C (Advertiser) + Phase D (Scanner) Verified  
**Commit:** 1131bef09f5d58733145532583f0288dfe1b83a1  
**GitHub Actions Run:** 32022290322 (Success)  

---

## 1. Objective

Empirically determine whether BLE is technically viable for Ghost's proximity requirements on Android. The experiment must measure detection reliability, false positives, false negatives, RSSI distributions, latency, identifier rotation behavior, background survival, pocket attenuation, battery consumption, and OEM variance — without collecting GPS, Wi-Fi, MAC addresses, device names, or any personal identifiers.

This protocol defines the measurement framework. It does NOT declare GO/NO-GO. All thresholds are PROPOSED until physical data supports a decision.

---

## 2. Equipment

### Required Hardware (Minimum Viable)

| Role | Device | Android | Purpose |
|------|--------|---------|---------|
| Advertiser | Google Pixel 7/8 (or available Pixel) | 14+ | Reference advertiser |
| Scanner | Google Pixel 7/8 (or available Pixel) | 14+ | Reference scanner |

### Optional OEM Devices (If Available)

| Role | Device | Android | Purpose |
|------|--------|---------|---------|
| Advertiser/Scanner | Samsung Galaxy S23/24 | 14+ | One UI behavior |
| Advertiser/Scanner | Xiaomi/Redmi 13/14 | 14+ | HyperOS behavior |
| Advertiser/Scanner | OnePlus 11/12 | 14+ | OxygenOS behavior |

**If fewer devices are available:** Clearly state the reduced matrix in results. Do not extrapolate to unavailable OEMs.

### Software

- BLE Feasibility Lab APK (built from commit 1131bef)
- ADB for installation and log retrieval
- USB cables for charging during long tests
- nRF Connect (or similar) for independent BLE verification
- Stopwatch/timer for manual timing
- Notebook or digital log for trial metadata

---

## 3. Device Matrix

### Ideal Matrix (4 OEMs × 2 Roles = 8 Device Pairs)

| Pair ID | Advertiser | Scanner | Notes |
|---------|------------|---------|-------|
| 1 | Pixel | Pixel | Reference |
| 2 | Samsung | Pixel | Cross-OEM |
| 3 | Pixel | Samsung | Cross-OEM |
| 4 | Xiaomi | Pixel | Cross-OEM |
| 5 | Pixel | Xiaomi | Cross-OEM |
| 6 | OnePlus | Pixel | Cross-OEM |
| 7 | Pixel | OnePlus | Cross-OEM |
| 8 | Samsung | Xiaomi | Non-reference cross |

### Minimum Viable Matrix (1 OEM × 2 Roles = 2 Device Pairs)

| Pair ID | Advertiser | Scanner | Notes |
|---------|------------|---------|-------|
| 1 | Pixel | Pixel | Reference only |

**If only one device is available:** Cannot run advertiser+scanner simultaneously. Document as limitation.

---

## 4. Environment Matrix

| Environment ID | Description | Interference Level | Constraints |
|----------------|-------------|-------------------|-------------|
| E1 | Quiet office / meeting room | Low (few BLE devices, no crowd) | Controlled, repeatable |
| E2 | Apartment / home (walls, floors) | Low-Medium (Wi-Fi, neighbor BLE) | Adjacent room / floor tests |
| E3 | Outdoor park / open space | Low (LOS, minimal multipath) | 20m LOS test |
| E4 | Café / busy public space | Medium-High (crowd, many BLE) | Realistic but less controlled |

**If only E1 and E3 are available:** Document limitation. E2 is critical for adjacent-room false positives. E4 is realistic but noisy.

---

## 5. Distance Matrix

| Distance ID | Distance | Condition | Purpose |
|-------------|----------|-----------|---------|
| D1 | 1 m | LOS, same room | Intimate proximity |
| D2 | 3 m | LOS, same room | Conversation distance |
| D3 | 5 m | LOS, same room | Same-room threshold |
| D4 | 10 m | LOS, large room | Extended same-room |
| D5 | 20 m | LOS | Adjacent-room proxy |
| D6 | 5 m | Through 1 drywall | Adjacent room |
| D7 | 5 m | Through 1 concrete wall | Different room (heavy) |
| D8 | 5 m | Same floor, different room | Realistic separation |
| D9 | 5 m | Different floor (wood) | Vertical separation |
| D10 | 3 m | Both phones in pockets | Pocket attenuation |
| D11 | 5 m | One phone in pocket | Asymmetric pocket |

**If 20m is impossible:** Maximum available distance. Document limitation.

---

## 6. Phone-State Matrix

For BOTH advertiser and scanner independently:

| State ID | Screen | App | Lock | Recents | Description |
|----------|--------|-----|------|---------|-------------|
| S1 | On | Foreground | Unlocked | In | Baseline |
| S2 | On | Background | Unlocked | In | Backgrounded |
| S3 | Off | Background | Unlocked | In | Screen off |
| S4 | Off | Background | Locked | In | Locked |
| S5 | Off | Background | Locked | Removed | Removed from recents |
| S6 | Off | Background | Locked | In | In pocket (adds body attenuation) |

**Record exact state for each trial.** Do not assume equivalence.

---

## 7. Trial Procedure

### 7.1 Preparation (Per Session)

1. Charge both devices to ≥80% (record starting %)
2. Install BLE Feasibility Lab APK on both devices
3. Grant all permissions: BLUETOOTH_SCAN, BLUETOOTH_ADVERTISE, BLUETOOTH_CONNECT, FOREGROUND_SERVICE, FOREGROUND_SERVICE_CONNECTED_DEVICE, POST_NOTIFICATIONS, BATTERY_STATS
4. Disable battery optimization for the app on both devices
5. Lock app in recents (if OS supports)
6. Verify advertiser shows "Advertising" and scanner shows "Scanning"
7. Verify nRF Connect can see the experiment service UUID on both devices
8. Record: device models, Android versions, security patch levels, battery optimization state

### 7.2 Single Trial (Distance + Environment + Phone State)

1. **Set up positions** per distance/environment matrix
2. **Configure phone states** per phone-state matrix
3. **Start trial timer** (manual or app timestamp)
4. **Run for minimum 60 seconds** (captures ≥6 advertisement rotations at 10-min interval? No — at 10-min rotation, 60s captures only same ID. For rotation test, need ≥11 min. See Section 13.)
5. **Record trial metadata:**
   - Trial ID (format: `T-{YYYYMMDD}-{NNN}`)
   - Device pair, distance, environment, phone states
   - Start/end timestamps
   - Starting battery levels
   - Any anomalies (interference, movement, etc.)
6. **Stop trial**, export CSV/JSON from both devices
7. **Record ending battery levels**

### 7.3 Trial Duration Guidelines

| Test Type | Minimum Duration | Rationale |
|-----------|------------------|-----------|
| Detection rate (fixed state) | 60 seconds | Multiple scan windows, same ephemeral ID |
| Rotation verification | 12 minutes | Must cross ≥1 rotation boundary |
| Background survival (long) | 1–24 hours | Measures Doze/standby/OEM kill |
| Battery test | 2–4 hours controlled | Detectable drain above noise |

---

## 8. Data Schema

### 8.1 Per-Detection Record (from Scanner Export)

| Field | Type | Source | Notes |
|-------|------|--------|-------|
| trial_id | string | manual | `T-20260817-001` |
| timestamp_local | ISO8601 | scanner | Local device time |
| ephemeral_id | hex string (32 chars) | service data | 16 bytes |
| protocol_version | int | service data | Expected 1 |
| rssi_dbm | int | ScanResult | Raw measurement |
| scan_timestamp_nanos | long | ScanResult | Nanos since boot |
| raw_service_data | hex string | service data | 17 bytes |
| advertiser_device | string | manual | e.g., "Pixel_8_adv" |
| scanner_device | string | manual | e.g., "Pixel_8_scan" |
| distance_m | int | manual | From matrix |
| environment_id | string | manual | E1–E4 |
| wall_condition | string | manual | "none"/"drywall"/"concrete" |
| floor_condition | string | manual | "same"/"different" |
| pocket_condition | string | manual | "none"/"one"/"both" |
| adv_screen_state | string | manual | S1–S6 |
| scan_screen_state | string | manual | S1–S6 |
| adv_battery_start | int | manual | % |
| scan_battery_start | int | manual | % |

### 8.2 Per-Trial Summary (Manual)

| Field | Type | Notes |
|-------|------|-------|
| trial_id | string | Matches detection records |
| start_time | ISO8601 | |
| end_time | ISO8601 | |
| duration_seconds | int | |
| adv_device | string | |
| scan_device | string | |
| distance_m | int | |
| environment_id | string | |
| wall_condition | string | |
| pocket_condition | string | |
| adv_phone_state | string | |
| scan_phone_state | string | |
| adv_battery_start | int | % |
| adv_battery_end | int | % |
| scan_battery_start | int | % |
| scan_battery_end | int | % |
| notes | string | Anomalies, interference, etc. |

### 8.3 Export Format

- **CSV** for spreadsheet analysis (one row per detection)
- **JSON** for programmatic analysis (nested trial → detections)
- **No cloud upload** — local files only via FileProvider

---

## 9. False-Positive Procedure

### 9.1 Adjacent Room Test

1. Place advertiser in Room A, scanner in Room B (shared drywall)
2. Distance: 3–5m through wall
3. Run 10 trials × 60 seconds each
4. **Expected:** Very low or zero detections
5. **Record:** Any detection = false positive

### 9.2 Different Floor Test

1. Advertiser on Floor 1, scanner on Floor 2 (wood/concrete)
2. Distance: 5m vertical + horizontal
3. Run 10 trials × 60 seconds each

### 9.3 Separated But Nearby

1. Both devices in same room but >10m apart (if space allows)
2. Or: advertiser in bag/pocket, scanner on table 5m away
3. Run 5 trials

### 9.4 Unrelated BLE Noise

1. Run scanner alone in café/office (no advertiser)
2. Duration: 5 minutes
4. **Expected:** Zero valid experiment detections (service UUID filter should reject all)

### 9.5 False Positive Rate Calculation

```
False Positive Rate = (False Positive Detections) / (Total Trial-Seconds in Negative Conditions)
```

**Report per condition.** Do not aggregate across different negative conditions.

---

## 10. False-Negative Procedure

### 10.1 Known-Positive Baseline

1. Place advertiser and scanner at 1m LOS, both S1 (screen on, foreground)
2. Run 20 trials × 60 seconds
3. **Expected:** Near 100% detection (every scan window should see advertiser)
4. **Record:** Any 60-second trial with zero detections = false negative

### 10.2 False Negative at Target Distances

For each distance D3 (5m), D4 (10m), D6 (through drywall):

1. Run 20 trials × 60 seconds in known-positive configuration
2. **Record:** Trials with zero valid detections
3. **False Negative Rate =** (Trials with zero detections) / (Total trials)

### 10.3 False Negative Reporting

| Distance | Phone States | Trials | Zero-Detection Trials | False Negative Rate |
|----------|-------------|--------|----------------------|---------------------|
| 5m LOS | S1/S1 | 20 | X | X/20 |
| 5m drywall | S1/S1 | 20 | Y | Y/20 |
| ... | ... | ... | ... | ... |

---

## 11. RSSI Analysis Method

### 11.1 Raw Data Collection

Collect **raw RSSI** for every valid detection. Do NOT convert to distance.

### 11.2 Per-Condition Statistics

For each (distance, environment, phone-state, pocket) combination:

| Statistic | Calculation |
|-----------|-------------|
| Count | Number of valid detections |
| Median | 50th percentile |
| Mean | Arithmetic mean |
| Std Dev | Population standard deviation |
| Min / Max | Range |
| P10 / P25 / P75 / P90 | Percentiles |
| IQR | P75 – P25 |

### 11.3 Distribution Comparison

**Key question:** Do RSSI distributions for "meaningful proximity" (same room, ≤10m) overlap with "non-meaningful" (adjacent room, different floor)?

- Plot histograms / KDE for each condition
- Compute overlap coefficient between same-room vs adjacent-room distributions
- If overlap > 50%, RSSI alone cannot reliably distinguish

### 11.4 No Distance Conversion

Do NOT report "RSSI -65 = 2 meters." Report only the statistics above.

---

## 12. Latency Method

### 12.1 Definition

Latency = `scan_timestamp_nanos` (from ScanResult) – `advertisement_transmit_time` (unknown)

**Since advertisement transmit time is not directly observable,** we approximate:

- **Best case:** Advertiser and scanner clocks are synchronized via NTP before test
- **Practical approximation:** Measure time from manual "start trial" to first detection record
- **End-to-end latency** = (First detection local timestamp) – (Trial start time)

### 12.2 Latency Measurements

| Metric | Method |
|--------|--------|
| First detection latency | Trial start → first detection |
| Median detection interval | Median of (detection N+1 – detection N) |
| Rotation detection latency | Time from rotation boundary to new ID detection |

### 12.3 Clock Sync Requirement

For precise radio-layer latency, both devices must sync to same NTP source before test. Document if not done.

---

## 13. Rotation Test

### 13.1 Objective

Verify that ephemeral IDs rotate at the configured interval (default 10 min) and scanner sees the new ID while old ID stops appearing.

### 13.2 Procedure

1. Configure rotation interval = 10 minutes (default)
2. Start advertiser + scanner at 1m LOS, S1/S1
3. Run continuous trial for **≥15 minutes** (crosses 1 rotation boundary)
4. Export scanner data
5. **Analyze:**
   - Group detections by ephemeral ID
   - Count unique IDs observed
   - Verify exactly 2 IDs observed (initial + 1 rotation)
   - Verify no detections of old ID after rotation boundary
   - Record observed rotation timestamp vs expected

### 13.3 Rotation Metrics

| Metric | Target |
|--------|--------|
| Unique IDs observed | 2 (for 15-min test) |
| Old ID persistence after boundary | 0 detections |
| New ID first appearance | Within 1 scan window of boundary |
| Observed rotation interval | 10 min ± 30 seconds |

---

## 14. Background Survival Test

### 14.1 Objective

Measure whether BLE scanning/advertising continues when phone is in pocket, locked, backgrounded, or removed from recents.

### 14.2 Procedure

1. Configure both devices for background survival:
   - Disable battery optimization
   - Lock app in recents
   - Start foreground service (persistent notification visible)
2. Set both to S5 (screen off, locked, in pocket)
3. Place at 3m LOS
4. Run for **≥2 hours** (covers multiple Doze maintenance windows)
5. Export data from both devices
6. **Analyze:**
   - Divide timeline into 1-hour windows
   - Count windows with ≥1 valid detection
   - **Survival Rate =** (Windows with detections) / (Total windows)

### 14.4 Extended Test (If Feasible)

Run 8-hour overnight test. Document sleep/wake cycles.

---

## 15. Battery Test

### 15.1 Objective

Measure battery consumption of continuous scanning + advertising.

### 15.2 Controlled Test

1. Charge both devices to 100%
2. Configure: advertiser S1, scanner S1, 3m LOS
3. Run for **4 hours** (minimum for measurable drain)
4. Record start/end battery % via system settings (not just app)
5. **Consumption Rate =** (Start % – End %) / (Hours) → %/hour

### 15.3 Background Battery Test

1. Same but S5/S5 (pocket, locked)
2. Run 4+ hours
3. Compare foreground vs background consumption

### 15.4 Reporting

| Test | Device | Role | Start % | End % | Duration | Rate (%/hr) |
|------|--------|------|---------|-------|----------|-------------|
| Foreground | Pixel | Adv | 100 | 96 | 4h | 1.0 |
| Foreground | Pixel | Scan | 100 | 94 | 4h | 1.5 |
| Background | Pixel | Adv | 100 | 97 | 4h | 0.75 |
| Background | Pixel | Scan | 100 | 95 | 4h | 1.25 |

**Extrapolate to daily:** Rate × 24h. Compare to PROPOSED threshold <5%/day.

---

## 16. OEM Comparison Method

### 16.1 Standardized Test Suite

Run the **same trial set** on each available device pair:

| Test | Duration | Conditions |
|------|----------|------------|
| Detection baseline | 20 × 60s | 5m LOS, S1/S1 |
| Adjacent room FP | 10 × 60s | Drywall, S1/S1 |
| Pocket attenuation | 10 × 60s | 5m, both in pocket |
| Background survival | 2 hours | 3m LOS, S5/S5 |
| Battery drain | 4 hours | 3m LOS, S1/S1 |
| Rotation | 15 min | 1m LOS, S1/S1 |

### 16.2 Comparison Metrics

| Metric | Pixel (Ref) | Samsung | Xiaomi | OnePlus |
|--------|-------------|---------|--------|---------|
| Detection rate @ 5m LOS | X% | Y% | Z% | W% |
| False positive rate | X% | Y% | Z% | W% |
| Background survival (2h) | X% | Y% | Z% | W% |
| Battery drain rate | X%/hr | Y%/hr | Z%/hr | W%/hr |
| Rotation accuracy | X% | Y% | Z% | W% |

### 16.3 OEM Configuration Recording

For each device, record:
- Manufacturer, model, Android version
- Security patch level
- Battery optimization setting for app (Unrestricted/Optimized/Restricted)
- Foreground service permission granted (Y/N)
- Any OEM-specific "auto-start" or "background run" settings

---

## 17. Minimum Viable Sample Size

| Test Type | Minimum Trials | Rationale |
|-----------|---------------|-----------|
| Detection rate (per condition) | 20 | Binomial CI ±22% at 95% for p=0.5 |
| False positive (per condition) | 10 | Lower bound on rare events |
| Background survival (per hour) | 2 hours | Covers ≥1 Doze window |
| Battery test | 4 hours | Detectable above noise |
| Rotation | 15 minutes | Crosses 1 boundary |
| OEM comparison | All above per device | Consistency |

**Total minimum trial time per device pair: ~8–10 hours**

---

## 18. Ideal Sample Size

| Test Type | Ideal Trials | Rationale |
|-----------|--------------|-----------|
| Detection rate | 50 | Binomial CI ±14% at 95% for p=0.5 |
| False positive | 30 | Better tail estimation |
| Background survival | 24 hours | Full day/night cycle |
| Battery test | 8–12 hours | Multiple charge cycles |
| OEM comparison | Full suite × 4 OEMs | Statistical power |

**Total ideal trial time per device pair: ~40–50 hours**

---

## 19. Proposed Analysis Criteria (PROPOSED — Not Final)

| Category | PROPOSED GO Threshold | PROPOSED NO-GO Threshold |
|----------|----------------------|-------------------------|
| Detection @ 5m LOS | >90% | <70% |
| False positive (adjacent room) | <10% | >30% |
| Background survival (24h) | >95% windows | <80% |
| Battery impact | <5%/day | >10%/day |
| Cross-OEM consistency | All >80% survival | Any <50% |
| Rotation accuracy | 100% | <95% |

**DECISION RULE (PROPOSED):** ALL GO thresholds met → GO. ANY NO-GO threshold met → NO-GO (pivot/rethink).

---

## 20. What Would Constitute Evidence of Feasibility

- **Detection:** >90% at 5m LOS across all tested OEMs
- **Discrimination:** RSSI distributions for same-room vs adjacent-room show <50% overlap
- **Background:** >95% 1-hour windows with detections on all OEMs for 24h
- **Battery:** <5%/day extrapolated from controlled tests
- **Rotation:** 100% clean transitions, no persistent old IDs
- **False positives:** <10% in adjacent-room test
- **False negatives:** <10% at 5m LOS in known-positive configuration

---

## 21. What Would Constitute Evidence of a Blocker

- **Any OEM** shows <50% background survival in 24h test
- **Battery** >10%/day on any OEM in controlled test
- **False positive** >30% in adjacent-room test on any OEM
- **False negative** >30% at 5m LOS on any OEM
- **Rotation failure** on any OEM (old IDs persist, new IDs not seen)
- **Detection** <70% at 5m LOS on any OEM

---

## 22. Known Limitations

1. **No NTP clock sync** in current app — latency measurements are approximate
2. **No scanner UI integration** — manual trial coordination required
3. **Single advertiser/scanner at a time** — no concurrent multi-device test
4. **Rotation interval fixed at 10 min** — cannot test shorter/longer without rebuild
5. **No automated trial orchestration** — human timing introduces variance
6. **Environment control limited** — cannot perfectly control interference, multipath
7. **Device availability** — OEM matrix may be reduced
8. **No UWB test** — scanner only filters BLE service UUID
8. **Export manual** — no automated data aggregation
9. **No long-term storage analysis** — app stores limited history
10. **Body attenuation variability** — pocket position, clothing, body type not controlled

---

## 23. Next Steps

1. **Assess available devices** — confirm which OEMs can be tested
2. **Prepare test environment** — measure room dimensions, wall types
3. **Install APK on all devices** — verify permissions, foreground service
4. **Run pilot trials** — validate procedure, fix any app issues
5. **Execute full trial matrix** — per available devices
6. **Export and analyze data** — CSV/JSON → statistical analysis
7. **Report results** — compare to PROPOSED criteria, document evidence

---

**END OF PROTOCOL**

*This protocol defines the measurement framework for Phase 3 physical BLE experimentation. It does not declare GO/NO-GO. All thresholds are PROPOSED until physical data supports a decision.*