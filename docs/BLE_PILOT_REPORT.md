# Phase 3 BLE Pilot Experiment Report

**Status:** PREPARATION COMPLETE — Awaiting Physical Installation  
**Date:** 2026-08-17  
**Commit:** 1131bef09f5d58733145532583f0288dfe1b83a1  
**APK:** ble-feasibility-lab-debug.apk (24.6 MB, SHA-256: e1f88c9bc5fca193638780a0a7ad89e8ba7ba1d563afd073e10413b57d7f7293)  
**Protocol:** docs/BLE_FEASIBILITY_EXPERIMENT_PROTOCOL.md  

---

## 1. Devices Used

| Role | Device | Manufacturer | Model | Android Version | API Level | Bluetooth |
|------|--------|--------------|-------|-----------------|-----------|-----------|
| Primary (Advertiser/Scanner) | Samsung Galaxy Tab S9 FE / Similar | Samsung | SM-X216B | 16 | 36 | 5.x |

**Note:** Only one physical device currently available (the Samsung device running Termux). Second device needed for advertiser+scanner pair.

---

## 2. Android Versions

- **Device OS:** Android 16 (API 36) — exceeds minimum API 31 requirement
- **SELinux:** Enforcing (prevents direct `pm install` from Termux)
- **Root:** Not available (Termux does not provide root)

---

## 3. Conditions Tested

**NOT YET TESTED** — Awaiting APK installation on second device.

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
| 1 | Only one physical device available | Cannot run advertiser+scanner simultaneously | Need second device |
| 2 | SELinux prevents `pm install` from Termux | Cannot install APK via command line | Must install manually via file manager or adb from host |
| 3 | No root/su available | Cannot bypass SELinux | Manual install required |
| 4 | No adb host connected | Cannot install via `adb install` | Connect device to host machine |

---

## 15. Fixes Made

- **APK built and verified** on GitHub Actions (Run 32022290322)
- **APK copied to device** at `/sdcard/ble-feasibility-lab-debug.apk` (24.6 MB)
- **Pilot protocol written** at `docs/BLE_FEASIBILITY_EXPERIMENT_PROTOCOL.md`
- **All source code committed** at 1131bef09f5d58733145532583f0288dfe1b83a1

**No implementation fixes needed** — build passed, all tests passed.

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
| APK builds | ✅ PASS | GitHub Actions Run 32022290322 |
| Unit tests pass | ✅ PASS | 44/44 tests (including AdvertisePayloadTest) |
| APK installs on target Android | ⚠️ PENDING | Requires manual install on Samsung + second device |
| Advertiser starts | ⚠️ UNTESTED | Requires two devices |
| Scanner detects advertisements | ⚠️ UNTESTED | Requires two devices |
| Service UUID filtering works | ✅ STATIC VERIFIED | Code review confirms filter |
| Ephemeral ID parsing works | ✅ STATIC VERIFIED | AdvertisePayloadTest covers |
| RSSI recorded | ✅ STATIC VERIFIED | ScanResultProcessor captures |
| Timestamps recorded | ✅ STATIC VERIFIED | ScanResult.timestampNanos |
| Export works | ✅ STATIC VERIFIED | FileProvider + CSV/JSON |
| Privacy compliance | ✅ STATIC VERIFIED | No forbidden permissions/data |

**OVERALL: LAB READY FOR PILOT — Awaiting second device and manual APK installation**

---

## Next Steps Required

1. **Obtain second Android device** (any Android 12+ device with BLE)
2. **Install APK on both devices** — via file manager (tap APK in Downloads) or host adb
3. **Grant permissions** on both:
   - Nearby devices (BLUETOOTH_SCAN/ADVERTISE/CONNECT)
   - Notifications
   - Battery optimization: Unrestricted
4. **Run pilot conditions** C1–C6 per protocol
5. **Export and inspect** CSV/JSON
6. **If pilot succeeds** → Proceed to full measurement matrix

---

**STATUS:** PREPARATION COMPLETE — Awaiting Physical Installation  
**BLOCKER:** Second device needed + manual APK install required  

*This report reflects preparation status only. No physical experiment data exists yet.*