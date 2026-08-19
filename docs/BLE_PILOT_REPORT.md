# Phase 3 BLE Pilot Experiment Report

**Status:** PREPARATION COMPLETE — Two devices available, awaiting physical trial execution  
**Date:** 2026-08-17 (reconciled 2026-08-19)  
**Commit:** 125a998532e7acec02a99a46aa3d1d8d0bf9da59  
**CI Run:** 32213571392 (Success — 53/53 unit tests, APK build successful)  
**APK:** ble-feasibility-lab-debug.apk (58,624,397 bytes uncompressed / 15,374,775 bytes artifact, SHA-256: a6b798630e06cba95a66e9c8acf7d8af1b4c6dbdc76e43b991b2721155dfa993)  
**Protocol:** docs/BLE_FEASIBILITY_EXPERIMENT_PROTOCOL.md  

> **Reconciliation note (2026-08-19):** This report previously referenced commit 1131bef and a 24.6 MB APK. That checkpoint was superseded: 25 commits after 1131bef failed CI, and the build was restored at commit 125a998 (CI run 32213571392). The APK referenced below is the one built at 125a998. The earlier APK (SHA-256 e1f88c9b...) is obsolete and must not be used for trials. Device availability has also been corrected: two devices are now available. **No physical trials have been executed. No physical BLE measurements exist.**

---

## 1. Devices Used

| Role | Device | Manufacturer | Model | Android Version | API Level | Bluetooth |
|------|--------|--------------|-------|-----------------|-----------|-----------|
| Advertiser or Scanner | Samsung Galaxy Tab (owner's primary device) | Samsung | SM-X216B | 16 | 36 | 5.x |
| Advertiser or Scanner | Samsung Galaxy A03 | Samsung | SM-A035F/DS | 13 (One UI Core 5.1) | 33 | 5.x |

**Note (updated 2026-08-19):** Two physical devices are now available. Roles should be swapped across trials. Both devices are Samsung — results will describe Samsung/One UI behavior only and must not be extrapolated to other OEMs.

---

## 2. Android Versions

- **Device A OS:** Android 16 (API 36) — exceeds minimum API 31 requirement
- **Device B OS:** Android 13 (API 33) — exceeds minimum API 31 requirement
- **SELinux:** Enforcing on Device A (prevents direct `pm install` from Termux)
- **Root:** Not available (Termux does not provide root)

---

## 3. Conditions Tested

**NOT YET TESTED** — Two devices are available; APK installation and trial execution have not happened.

**Planned Pilot Conditions (from protocol):**

| Condition ID | Distance | Phone State | Trials Planned |
|--------------|----------|-------------|----------------|
| C1 | 1 m | Screen on / foreground | 5–10 |
| C2 | 3 m | Screen on / foreground | 5–10 |
| C3 | 5 m | Screen on / foreground | 5–10 |
| C4 | 5 m | Screen off / locked | 5–10 |
| C5 | 5 m | Scanner backgrounded | 5–10 |
| C6 | 5 m | One phone in pocket | 5–10 |

**Rotation Pilot:** 15-minute continuous trial to observe ephemeral ID rotation.

---

## 4. Trial Counts

**NOT YET EXECUTED** — Target: 5–10 trials per condition.

---

## 5. Detection Counts

**UNTESTED** — No advertiser/scanner pair running yet.

---

## 6. Missed Detections

**UNTESTED** — No data to analyze.

---

## 7. RSSI Observations

**UNTESTED** — No scanner detections recorded.

---

## 8. Latency Observations

**UNTESTED** — No timing data available.

---

## 9. Screen-Off/Locked Observations

**UNTESTED** — Condition C4 not yet executed.

---

## 10. Pocket Observations

**UNTESTED** — Condition C6 not yet executed.

---

## 11. Rotation Observations

**UNTESTED** — Rotation pilot not yet executed.

---

## 12. Export Verification

**UNTESTED** — No data to export yet.

---

## 13. Privacy Verification

**APK DESIGN VERIFIED (Static Analysis):**
- ✅ No ACCESS_FINE_LOCATION permission requested
- ✅ No ACCESS_COARSE_LOCATION permission requested
- ✅ No background location permission
- ✅ Only BLUETOOTH_SCAN, BLUETOOTH_ADVERTISE, BLUETOOTH_CONNECT with neverForLocation
- ✅ Service UUID filtering (d4e5f6a7-b8c9-4d0e-8f1a-2b3c4d5e6f70)
- ✅ Payload: protocol version (1 byte) + ephemeral ID (16 bytes)
- ✅ No MAC addresses, device names, GPS, Wi-Fi, usernames in code
- ✅ Export via FileProvider (local only, no network)

**RUNTIME VERIFICATION:** PENDING (requires installed app)

---

## 14. Problems Encountered

| # | Problem | Impact | Resolution |
|---|---------|--------|------------|
| 1 | ~~Only one physical device available~~ | ~~Cannot run advertiser+scanner simultaneously~~ | RESOLVED 2026-08-19: second device (Samsung Galaxy A03, SM-A035F/DS, Android 13) now available |
| 2 | SELinux prevents `pm install` from Termux | Cannot install APK via command line | Must install manually via file manager or adb from host |
| 3 | No root/su available | Cannot bypass SELinux | Manual install required |
| 4 | No adb host connected | Cannot install via `adb install` | Connect device to host machine |
| 5 | 25 commits after 1131bef failed CI (compile errors) | No verified APK existed between 2026-08-17 and 2026-08-19 | RESOLVED at commit 125a998 (CI run 32213571392) |

---

## 15. Fixes Made

- **APK built and verified** on GitHub Actions (Run 32213571392, commit 125a998)
- **Compilation errors fixed** at commit 125a998 (Result.flatMap extension, ListSerializer, Compose imports/types) after 25 failing commits since 1131bef
- **Pilot protocol written** at `docs/BLE_FEASIBILITY_EXPERIMENT_PROTOCOL.md`
- **All source code committed and pushed** — HEAD 125a998532e7acec02a99a46aa3d1d8d0bf9da59

**Note:** The earlier statement "APK copied to device at /sdcard/ble-feasibility-lab-debug.apk (24.6 MB)" referred to the obsolete 1131bef APK. That APK must not be used; the current verified APK is from commit 125a998.

---

## 16. Dataset Location

**NO DATASET YET** — Awaiting first trial execution.

**Expected locations after trials:**
- CSV/JSON exports via FileProvider → accessible via `adb pull` or file manager
- Internal app storage → `Android/data/com.ghost.blelab/files/exports/`

---

## 17. Whether Lab Is Ready for Full Physical Experimentation

| Criterion | Status | Notes |
|-----------|--------|-------|
| APK builds | ✅ PASS | GitHub Actions Run 32213571392, commit 125a998 |
| Unit tests pass | ✅ PASS | 53/53 tests |
| APK installs on target Android | ⚠️ PENDING | Requires manual install on both Samsung devices |
| Advertiser starts | ⚠️ UNTESTED | Requires installed app on both devices |
| Scanner detects advertisements | ⚠️ UNTESTED | Requires installed app on both devices |
| Foreground service for background operation | ❌ NOT IMPLEMENTED | BleExperimentService declared in manifest but no source exists (lab Phase F) |
| Battery monitoring | ❌ NOT IMPLEMENTED | Lab Phase H not started |
| Service UUID filtering works | ✅ STATIC VERIFIED | Code review confirms filter |
| Ephemeral ID parsing works | ✅ STATIC VERIFIED | AdvertisePayloadTest covers |
| RSSI recorded | ✅ STATIC VERIFIED | ScanResultProcessor captures |
| Timestamps recorded | ✅ STATIC VERIFIED | ScanResult.timestampNanos |
| Export works | ✅ STATIC VERIFIED | FileProvider + CSV (JSON/plain text removed) |
| Privacy compliance | ✅ STATIC VERIFIED | No forbidden permissions/data |

**OVERALL: LAB READY FOR FOREGROUND PILOT ONLY.** Background-survival and battery portions of the full experiment are BLOCKED until lab Phases F and H are implemented. Two devices are available; manual APK installation is the remaining prerequisite.

---

## Next Steps Required

1. ~~Obtain second Android device~~ — DONE: Samsung Galaxy A03 (SM-A035F/DS, Android 13) available as of 2026-08-19
2. **Install APK (commit 125a998 build) on both devices** — via file manager (tap APK in Downloads) or host adb
3. **Grant permissions** on both:
   - Nearby devices (BLUETOOTH_SCAN/ADVERTISE/CONNECT)
   - Notifications
   - Battery optimization: Unrestricted
4. **Run pilot conditions** C1–C6 per protocol (foreground conditions only; background conditions limited until Phase F exists)
5. **Export and inspect** CSV
6. **If pilot succeeds** → Proceed to full measurement matrix (after Phases F and H are implemented)

---

**STATUS:** PREPARATION COMPLETE — Two devices available, awaiting physical trial execution  
**BLOCKER:** Manual APK install on both devices; lab Phases F (foreground service) and H (battery monitor) not yet implemented  
**VERDICT:** BLE feasibility remains UNKNOWN. No physical measurements exist. No GO/NO-GO has been declared.

*This report reflects preparation status only. No physical experiment data exists yet.*