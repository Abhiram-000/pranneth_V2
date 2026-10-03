# RakshaSetu

Personal safety SOS app for Android (Kotlin, AGP 8.7, minSdk 28, targetSdk 36).

## What it does

One gesture — a distinctive phone shake, a volume-button combo, or a lock-screen SOS action — wakes a deliberate countdown. If you don't cancel, it fires a layered alert:

- SMS with live GPS (or honest network-estimated location) to every emergency contact
- Missed calls to each contact, then an SMS+call to 112/ERSS
- Escalates to the next contact if the first doesn't acknowledge within 30 s
- Continues sending the phone's position every ~2 min for up to 30 min
- Duress code lets you "cancel" under someone's watch while the alert quietly keeps running

## Architecture

| Layer | Location | Role |
|---|---|---|
| Triggers | `domain/trigger/` | Shake (multi-axis + periodicity veto) and volume-combo detection |
| Dispatch | `service/AlertDispatchService.kt`, `domain/sms/`, `domain/call/` | Multi-part SMS with retry + dual-SIM fallback, missed calls, 112 |
| Location | `domain/location/` | FusedLocationProvider, network fallback, live updates |
| Escalation | `domain/escalation/` | Tiered 30 s acknowledgement timeout, cancel-safe |
| Persistence | `data/` Room + `util/SecurePrefs.kt` | Contacts, alert log, live-track trail; `EncryptedSharedPreferences` |
| Reliability | `service/*`, `worker/ServiceWatchdogWorker.kt` | Foreground service, wake lock, health checks, reboot restart, 15-min watchdog |

## Build & run

```bash
cd /home/abhiram/AndroidStudioProjects/pranneth_V2
./gradlew :app:assembleDebug
# or on a connected device / emulator:
./gradlew :app:installDebug
```

## Tests

```bash
./gradlew :app:testDebugUnitTest
```

Pure-JVM unit tests pin the core rules: shake false-positive filter, volume trigger debounce, SMS retry plan + dual-SIM switch, escalation timeout, emergency-call hang-up policy, location accuracy labeling, watchdog policy, contact validator, countdown/silent/duress decisions, OEM battery guide mapping, secure-prefs migration.

## Presenting the code

- Spec → `prd.md`
- Implementation plan → `docs/superpowers/plans/2026-10-03-rakshasetu-v1-robustness.md`
- Live-fire flow → shake the phone (or hold Volume Up + Down) → countdown → cancel
- Alert path visible in the repo as `CountdownActivity → AlertDispatchService → SMSDispatcher / CallManager / AlertLog`

See `DEMO.md` for the 2-minute feature tour.
