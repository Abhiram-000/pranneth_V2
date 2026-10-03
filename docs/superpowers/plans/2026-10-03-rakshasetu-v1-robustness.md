# RakshaSetu V1 Robustness Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Harden and complete the RakshaSetu V1 feature set so every listed feature is robust under OEM background killers, poor connectivity, and real-world false positives.

**Architecture:** Existing Kotlin/Hilt/Room structure is kept. Logic embedded in services/UI is extracted into small pure-JVM classes under `domain/` so behavior is unit-testable. Services keep Android-bound glue; repositories mediate storage. Foreground service + WorkManager watchdog guard 24/7 uptime; encrypted storage via `security-crypto`.

**Tech Stack:** Kotlin 2.0.21, AGP 8.7.0, minSdk 28 / targetSdk 36, Room, Hilt, WorkManager, Coroutines, play-services-location, JUnit4 (+ Robolectric only where Context is unavoidable).

**Spec:** `prd.md` (v2.0 §5–§11, §14 V1 scope)

## Global Constraints

- minSdk = 28, targetSdk = 36, compileSdk = 36; Java/Kotlin bytecode 17.
- No new network SDK; no Firebase, no MDM. v1 stays fully on-device.
- SMS path must work with voice-only SIM (no data).
- Every trigger must pass through the countdown before dispatch; no instant-fire.
- False-positive defenses (§5) are release gates, not niceties.
- Trigger-to-first-SMS latency target: ≤ 3 s post-countdown.

## Review Focus

- Rhythmic motion (running/cycling) must not reach the trigger counter → pin in `ShakeDetector` test.
- Volume combo must fire only inside the configured press window and not consume normal volume adjustments.
- Duress cancel must show identical UI to a real cancel while the alert pipeline continues and `AlertLog.isDuress=true`, `cancelMethod="duress"`.
- GPS loss → network fallback must include an explicit accuracy string in the SMS.
- Send-failure retries must back off 10 s/30 s/90 s and, after 3 failures, fall back to the secondary SIM.
- An abuser toggling airplane mode must trigger an alert before connectivity drops.
- OEM force-stop must be surfaced via watchdog notification, not silently ignored.

---

### Task 1: Shake detector hardening (false-positive filter, low-power batching)

**Files:**
- Modify: `app/src/main/java/com/rakshasetu/app/domain/trigger/ShakeDetector.kt`
- Test: `app/src/test/java/com/rakshasetu/app/domain/trigger/ShakeDetectorTest.kt`

**Interfaces:**
- Consumes: `ShakeDetector.ShakeConfig(threshold, eventCount, windowMs, sensitivityMultiplier)` and `ShakeListener.onShakeDetected/onShakePatternInvalid`
- Produces: public `isListening()`; internal counter reset preserved

- [ ] **Step 1: Write failing tests** for: (a) 3 multi-axis jolts within 1500 ms → `onShakeDetected`; (b) a periodic spike train (constant 400 ms interval) → `onShakePatternInvalid`, no detect; (c) 2 jolts → no detect; (d) single-axis-only jolts → no detect.
- [ ] **Step 2: Run tests** `./gradlew :app:testDebugUnitTest --tests ShakeDetectorTest` → expect FAIL on periodicity/multi-axis assertions.
- [ ] **Step 3: Implement/strengthen** in `ShakeDetector`: require acceleration delta on ≥2 axes within 150 ms; maintain `recentIntervals` periodicity check (CV < 0.15 ⇒ rhythmic ⇒ invalidate); ignore peaks shorter than 80 ms apart.
- [ ] **Step 4: Re-run tests** → PASS.
- [ ] **Step 5: Low-power rate** — in `service/ShakeDetectionService.kt` replace `SENSOR_DELAY_UI, maxLatencyUs=0` with `SENSOR_DELAY_GAME` and `maxLatencyUs = 100_000`; add test-less comment justifying battery tradeoff.
- [ ] **Step 6: Commit** `git commit -m "feat: harden shake detection against rhythmic false positives"`

### Task 2: Volume trigger robustness

**Files:**
- Modify: `app/src/main/java/com/rakshasetu/app/domain/trigger/VolumeButtonDetector.kt`
- Test: `app/src/test/java/com/rakshasetu/app/domain/trigger/VolumeButtonDetectorTest.kt`

**Interfaces:**
- Consumes: `VolumeConfig(requiredPresses, windowMs, requireBothButtons)`; `onKeyEvent(keyCode, isDown)`
- Produces: same listener; adds 500 ms re-arm cooldown after a detection

- [ ] **Step 1: Write failing tests**: combo of UP+DOWN within 300 ms → fired once; repeated within 500 ms → not re-fired; keys 400 ms apart → not fired.
- [ ] **Step 2:** Run test → FAIL.
- [ ] **Step 3:** Add 500 ms arm-cooldown and clear pending state on detection.
- [ ] **Step 4:** Run test → PASS.
- [ ] **Step 5:** In `VolumeKeyAccessibilityService`, respect `prefs.volumePressCount/volumeWindowMs` from `PreferencesRepository` instead of hardcoded 2/300.
- [ ] **Step 6:** Commit.

### Task 3: Lock-screen SOS quick action

**Files:**
- Create: `app/src/main/java/com/rakshasetu/app/ui/sos/SosQuickActionReceiver.kt` (optional) — or reuse notification PendingIntent
- Modify: `app/src/main/java/com/rakshasetu/app/service/ShakeDetectionService.kt` (notification SOS action)
- Modify: `app/src/main/AndroidManifest.xml`

**Interfaces:**
- Produces: tapping the SOS action on the persistent notification launches `CountdownActivity` even with the device locked; fallback to `USE_FULL_SCREEN_INTENT` notification on Android 14+ background-start block.

- [ ] **Step 1:** Add manifest `USE_FULL_SCREEN_INTENT` fallback notification channel `sos_quick_action` (exists).
- [ ] **Step 2:** In `ShakeDetectionService.createNotification()`, also build a high-priority full-screen-intent notification path when the SOS action is used.
- [ ] **Step 3:** Manual verify on device/emulator (locked screen → notification action → countdown shown).
- [ ] **Step 4:** Commit.

### Task 4: Countdown with cancel + silent mode

**Files:**
- Modify: `app/src/main/java/com/rakshasetu/app/ui/countdown/CountdownActivity.kt`
- Test: `app/src/test/java/com/rakshasetu/app/ui/countdown/CountdownPolicyTest.kt` (extract `CountdownPolicy` deciding silent vs visible behavior from prefs)

- [ ] **Step 1:** Write failing test for `CountdownPolicy`: `isSilentCountdown=true` ⇒ no sound/vibration/flash; `false` ⇒ vibrate pattern SOS signature.
- [ ] **Step 2:** Extract `CountdownPolicy` and wire in `setupSilentCountdown()/setupVisibleCountdown()`.
- [ ] **Step 3:** Ensure silent mode shows a *dimmed, low-contrast* countdown (torch-less, screen-dim via WindowManager layout alpha) and does not play sounds.
- [ ] **Step 4:** Commit.

### Task 5: Duress cancel code end-to-end

**Files:**
- Modify: `app/src/main/java/com/rakshasetu/app/ui/countdown/CountdownActivity.kt` (`showDuressCancelDialog` path → dispatch with `isDuress=true`)
- Modify: `app/src/main/java/com/rakshasetu/app/service/AlertDispatchService.kt` (EXTRA_IS_DURESS → `AlertLog.isDuress=true`, `cancelMethod="duress"`)
- Modify: `app/src/main/java/com/rakshasetu/app/ui/settings/SettingsActivity.kt` (duress code saved alongside app lock flag)
- Test: `app/src/test/java/com/rakshasetu/app/domain/DuressFlagTest.kt`

- [ ] Step 1–4: Define `AlertIds.duressIntentExtra` constant; test that dispatch extras map to `AlertLog` fields via a pure mapper; wire UI identically to normal cancel; ensure duress path launches `AlertDispatchService` with silent=true and starts cooldown.
- [ ] Step 5: Commit.

### Task 6: GPS with network fallback + honest accuracy in SMS

**Files:**
- Modify: `app/src/main/java/com/rakshasetu/app/domain/location/LocationTracker.kt`
- Modify: `app/src/main/java/com/rakshasetu/app/service/AlertDispatchService.kt`
- Test: `app/src/test/java/com/rakshasetu/app/domain/location/LocationSmsFormatterTest.kt`

- [ ] **Step 1:** Failing test: `formatLocationForSms(null)` → "location unavailable"; `Location(accuracy=800)` → contains "accurate to ~800m"; network source → appends "(network estimate)".
- [ ] **Step 2:** Run → FAIL.
- [ ] **Step 3:** Implement formatter + use in `LocationTracker.getLocationForSms()` and SMS body template in `AlertDispatchService`.
- [ ] **Step 4:** Run → PASS.
- [ ] **Step 5:** Commit.

### Task 7: Live location updates during alert

**Files:**
- Modify: `app/src/main/java/com/rakshasetu/app/service/LocationTrackingService.kt`
- Test: `app/src/test/java/com/rakshasetu/app/domain/location/LocationTrackingPolicyTest.kt`

- [ ] **Step 1:** Failing test for `LocationTrackingPolicy`: interval default 120 s, duration cap 30 min, ticks fire at t=120s,240s…; ends at duration cap even if not cancelled.
- [ ] **Step 2:** Extract policy from service loop; service calls `SMSDispatcher` for each tick with fresh fix or network fallback; stores `LocationUpdate` rows with alertId.
- [ ] **Step 3:** Run → PASS. Commit.

### Task 8: Emergency contacts validation + verification

**Files:**
- Create: `app/src/main/java/com/rakshasetu/app/domain/contacts/ContactValidator.kt`
- Modify: `app/src/main/java/com/rakshasetu/app/ui/contacts/AddContactActivity.kt`
- Test: `app/src/test/java/com/rakshasetu/app/domain/contacts/ContactValidatorTest.kt`

- [ ] **Step 1:** Failing test: empty name rejected; phone `"12"` rejected; `"+919876543210"` accepted; relation "Other" requires custom; country code default `+91`.
- [ ] **Step 2:** Implement `ContactValidator.validate(name, phone, countryCode, relation, customRelation): ValidationResult`; wire into AddContactActivity (disable save + inline error).
- [ ] **Step 3:** Verification SMS body = "TEST — no action needed. I've added you as an emergency contact on RakshaSetu. Real alerts will look like: …"; reuse `SmsVerificationHelper`.
- [ ] **Step 4:** Run → PASS. Commit.

### Task 9: SMS alerts — multipart + Unicode + backoff + dual-SIM

**Files:**
- Create: `app/src/main/java/com/rakshasetu/app/domain/sms/SmsRetryScheduler.kt`
- Modify: `app/src/main/java/com/rakshasetu/app/domain/sms/SMSDispatcher.kt`, `service/AlertDispatchService.kt`
- Test: `app/src/test/java/com/rakshasetu/app/domain/sms/SmsRetrySchedulerTest.kt`

- [ ] **Step 1:** Failing test: schedule returns delays `[10s,30s,90s]`; after all fail, active SIM index flips to secondary when dual-SIM available.
- [ ] **Step 2:** Implement `SmsRetryScheduler` using coroutines delay + injected `Dispatchers.Main`/TestCoroutineScheduler; use `SubscriptionManager.activeSubscriptionIdList` to pick non-primary SIM for final retry via `SmsManager.createForSubscriptionId`.
- [ ] **Step 3:** Keep existing multi-part `sendMultipartTextMessage`; ensure Unicode messages (any char > GSM7 table) are split via `divideMessage` and never truncated.
- [ ] **Step 4:** Run → PASS. Commit.

### Task 10: Missed calls (sequential, ring ~3–5 s)

**Files:**
- Modify: `app/src/main/java/com/rakshasetu/app/domain/call/CallManager.kt`

- [ ] **Step 1:** Change default `ringDurationMs` from 8000 to 4000; serialize calls with a `Mutex` queue so contacts are rung one-at-a-time; ensure `endCall()` swallow-and-continue on failure.
- [ ] **Step 2:** Device smoke-verify rings then disconnects.
- [ ] **Step 3:** Commit.

### Task 11: 112 SMS and call

**Files:**
- Modify: `app/src/main/java/com/rakshasetu/app/service/AlertDispatchService.kt`, `domain/sms/SMSDispatcher.kt`, `domain/call/CallManager.kt`

- [ ] **Step 1:** Use `prefs.emergencyNumber` (default "112") for both SMS (`sendEmergencySms`) and call (`placeEmergencyCall`); body includes custom danger message + location + battery line.
- [ ] **Step 2:** Add `hasDataConnection=false` note when offline.
- [ ] **Step 3:** Commit.

### Task 12: Tiered escalation with retry

**Files:**
- Modify: `app/src/main/java/com/rakshasetu/app/domain/escalation/EscalationManager.kt`
- Test: `app/src/test/java/com/rakshasetu/app/domain/escalation/EscalationManagerTest.kt`

- [ ] **Step 1:** Failing tests with `TestScope`: start → first contact notified level 1; after 30 s timeout → level 2; cancel → no further escalation; `getNotifiedCount` tracks set; all contacts → `onEscalationComplete`.
- [ ] **Step 2:** Make timeout injectable; replace raw `delay` with virtual-time-friendly injected `CoroutineScope`; wire acknowledgment = SMS delivered receipt for the contact (graceful default: timeout always escalates since acks are best-effort).
- [ ] **Step 3:** Run → PASS. Commit.

### Task 13: Onboarding & calibration wiring

**Files:**
- Modify: `app/src/main/java/com/rakshasetu/app/ui/onboarding/OnboardingActivity.kt`, `ui/calibration/CalibrationActivity.kt`, `ui/calibration/CalibrationHelper.kt`

- [ ] **Step 1:** Calibration screen records 5 sample shakes, computes median magnitude, writes `shakeThreshold = median * 0.7` into prefs; verify-volume combo writes `volumePressCount`.
- [ ] **Step 2:** Onboarding last page sends the labeled TEST SMS via `SmsVerificationHelper`; blocks completion until attempted (allowed to skip with clear warning).
- [ ] **Step 3:** Device smoke verify. Commit.

### Task 14: OEM battery optimization guide

**Files:**
- Modify: `app/src/main/java/com/rakshasetu/app/util/OEMHelper.kt`
- Test: `app/src/test/java/com/rakshasetu/app/util/OEMHelperTest.kt`

- [ ] **Step 1:** Failing test: `getBatteryOptimizationInstructions` for "Xiaomi" returns autostart step + steps non-empty; unknown → generic guide.
- [ ] **Step 2:** Already mostly present; pin mapping list for `xiaomi`, `vivo`, `oppo`, `realme`, `oneplus`, `samsung`.
- [ ] **Step 3:** In onboarding battery page, show the returned steps with the "Open settings" CTA using `openBatteryOptimizationSettings`.
- [ ] **Step 4:** Run → PASS. Commit.

### Task 15: Foreground service + health watchdog

**Files:**
- Create: `app/src/main/java/com/rakshasetu/app/worker/ServiceWatchdogWorker.kt`
- Create: `app/src/main/java/com/rakshasetu/app/worker/WatchdogScheduler.kt`
- Modify: `app/src/main/java/com/rakshasetu/app/RakshaSetuApp.kt`, `service/ShakeDetectionService.kt`, `service/BootReceiver.kt`

- [ ] **Step 1:** Failing JVM test not practical for Context — extract `WatchdogPolicy.shouldRestart(isRunning, cooldownActive, lastPingAge)` and test it.
- [ ] **Step 2:** `ServiceWatchdogWorker` (HiltWorker) checks `OEMHelper.isServiceRunning` for `ShakeDetectionService` + `AlertDispatchService`; restarts if not running and monitoring was enabled; persists `last_watchdog_ping`.
- [ ] **Step 3:** Enqueue unique periodic work (15 min) from `RakshaSetuApp.onCreate()` and `BootReceiver`.
- [ ] **Step 4:** `ShakeDetectionService` writes a heartbeat timestamp every sensor-health tick to prefs; watchdog uses it.
- [ ] **Step 5:** Device verify: force-stop via Settings → watchdog notices within 15 min window and surfaces notification. Commit.

### Task 16: Encryption at rest

**Files:**
- Modify: `app/src/main/java/com/rakshasetu/app/hilt/AppModule.kt`, `data/entity/AppPreferences.kt`
- Create: `app/src/main/java/com/rakshasetu/app/util/SecurePrefs.kt`

- [ ] **Step 1:** Replace plain `getSharedPreferences` with `EncryptedSharedPreferences` (migration: on first run, copy old keys into encrypted prefs, then clear plain file).
- [ ] **Step 2:** Update `PreferencesRepository` and all call sites that still call `getSharedPreferences("rakshasetu_prefs")` directly (ShakeDetectionService, CountdownActivity, SettingsActivity) to use the encrypted provider singleton.
- [ ] **Step 3:** Room DB remains plaintext v1 tradeoff (note in code comment); recorded evidence features are out of scope until v2 with `EncryptedFile`.
- [ ] **Step 4:** Build + install + verify settings persist across restarts. Commit.

### Task 17: Airplane-mode pre-emptive alert + multiple rapid re-trigger debounce

**Files:**
- Modify: `app/src/main/java/com/rakshasetu/app/service/AirplaneModeReceiver.kt`
- Test: `app/src/test/java/com/rakshasetu/app/domain/AirplaneFirePolicyTest.kt`

- [ ] **Step 1:** Failing test: when airplane mode is turned ON, policy returns FIRE with last known location captured before connectivity drop; when turned OFF, no fire.
- [ ] **Step 2:** Extract policy; receiver reads `lastKnownLocation` from `LocationTracker` and calls `AlertDispatchService.dispatchAlert(context, "airplane_mode", silent=true)`.
- [ ] **Step 3:** Ensure global re-trigger debounce (minimum interval between full alert cycles, e.g., 60 s) shared by shake/volume/manual.
- [ ] **Step 4:** Run → PASS. Commit.

### Task 18: Permission self-check warning on app open

**Files:**
- Modify: `app/src/main/java/com/rakshasetu/app/ui/main/MainActivity.kt`
- Test: `app/src/test/java/com/rakshasetu/app/domain/PermissionWarningPolicyTest.kt`

- [ ] **Step 1:** Failing test: `PermissionHelper.checkAllPermissions` missing any critical → policy shows persistent warning banner with "Fix" CTA.
- [ ] **Step 2:** MainActivity onCreate evaluates and renders banner; banner opens system permission settings.
- [ ] **Step 3:** Run → PASS. Commit.

---

**Self-review summary:** Spec V1 items all mapped (shake✓1, volume✓2, lock-screen SOS✓3, countdown cancel✓4, silent countdown✓4, duress✓5, GPS fallback✓6, live location✓7, contacts validation✓8, SMS multipart✓9, missed calls✓10, 112✓11, tiered escalation w/ retry✓12, onboarding/calibration✓13, OEM guide✓14, foregrous service watchdog✓15, encryption✓16). Debounce+airplane✓17. Permission self-check✓18. Known tradeoff: Room plaintext v1 (documented), 112 roaming mapping deferred to v3.
