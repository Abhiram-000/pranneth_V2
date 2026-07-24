# Product Requirements Document
## RakshaSetu — Personal Safety Trigger App for Android
*(working title — rename as you like)*

**Platform:** Android (Java/Kotlin)
**Doc version:** 2.0
**Author:** Abhiram
**Date:** July 2026

---

## 1. Problem Statement

In a personal safety emergency (assault, stalking, harassment, accident), the first 30–60 seconds matter most, and the victim usually cannot afford to unlock a phone, open an app, and type a message. Existing "panic button" apps fail in practice because they:

- Require the phone to be unlocked and the app foregrounded
- Depend on a single trigger method (easy to disable under duress)
- Send only a generic SMS with no proof of urgency (no missed call = recipient may ignore an unknown alert text)
- Have no path to actual law-enforcement response, only to personal contacts
- Drain battery or get killed by OEM background-process restrictions (a huge problem on Xiaomi/Vivo/Oppo/Realme devices in India)
- Fire constantly on false positives until the user disables them out of frustration — the single biggest reason safety apps get uninstalled

This app closes that gap: **multiple discreet triggers, working from the lock screen, that fire a layered response** — contacts, missed call, and a real emergency-services channel — with zero required interaction once triggered, and a deliberate, engineered defense against false alarms.

---

## 2. Goals

1. Let the user trigger an alert **without unlocking the phone**, via a physical/motion gesture that works even if the phone is in a pocket or bag.
2. Guarantee the alert reaches emergency contacts through **two redundant channels** (SMS + missed call) so absence of an app/data connection doesn't silently fail.
3. Route a real distress signal to **India's actual emergency response channel (112/ERSS)**, not an invented "police SMS" that no station will read.
4. Survive OEM battery-optimization killers well enough to actually fire when needed.
5. Minimize false positives aggressively — a shake-triggered SOS on a crowded bus, in a gym, or in a washing machine's spin cycle is worse than no feature at all, because it erodes trust and gets the app disabled.
6. Degrade gracefully under every realistic failure condition (no signal, no GPS, dead SIM, low battery, forced shutdown) rather than failing silently.

### Non-Goals (v1)
- Not a full personal-safety suite (no live-streaming to a monitoring center, no wearable hardware — that's a v2+ conversation).
- Not attempting to build a direct data integration with individual police stations — there is no public per-station API for this in India (see §8).
- Not targeting iOS in this doc.

---

## 3. Primary Persona

A woman commuting, walking alone, in a cab, or at a workplace/campus, who may be grabbed, followed, or need to signal distress **without the aggressor noticing she's using her phone** — and who needs the app to stay silent and invisible during ordinary daily activity like running, dancing, or cycling.

---

## 4. Trigger Mechanisms

| Trigger | Works from lock screen? | Notes |
|---|---|---|
| **Shake detection** | Yes (background service using accelerometer) | Must require a *distinct pattern*, not just any jolt — see §5 for the full false-positive defense design. |
| **Power button — long press 5s** | Yes, but **constrained by Android** | See §8.1 — biggest technical risk in the original spec. |
| **Lock-screen widget** | Partially — see §8.2 | True keyguard widgets don't exist since Android 5.0; a notification quick-action is the real substitute. |
| **Volume-button pattern (recommended addition)** | Yes | Volume up+down together, or triple-press, is far more reliable than power button on modern Android and doesn't fight the OS's own power-menu/Emergency SOS gesture. Recommend as the flagship trigger alongside shake. |
| **In-app SOS button** | N/A (app open) | Manual fallback. |
| **Duress / silent-cancel code (recommended addition)** | N/A | See §5.4 — lets the user "cancel" under an attacker's watch while secretly keeping the alert live. |

---

## 5. False-Positive & False-Negative Defense (Core Design Priority)

This is treated as a first-class design problem, not an afterthought, because an app that cries wolf gets deleted within a week.

### 5.1 Known false-positive scenarios and how each is handled

| Scenario | Why it's a risk | Mitigation |
|---|---|---|
| Running / jogging with phone in hand or armband | Rhythmic high-acceleration spikes look shake-like | Require a **non-rhythmic, multi-axis jolt pattern** (sudden acceleration change across at least 2 axes within a tight time window), not just magnitude threshold. Rhythmic, periodic signals (running cadence) are filtered out via a simple periodicity check before the shake counter increments. |
| Phone in a bag while cycling/on a bumpy vehicle/train | Continuous vibration triggers naive accelerometer thresholds | Use **Android's Activity Recognition API** (`ActivityRecognitionClient`) to detect "in vehicle"/"on bicycle"/"walking" and dynamically raise the shake threshold or require an extra confirmation shake in these states. |
| Dropped phone | Single sharp spike | Require **3 distinct shake events within a 2-second rolling window**, not a single spike — a drop registers as one event and is ignored. |
| Washing machine / vibrating surface nearby (phone left on a counter, in laundry, etc.) | Sustained low-frequency vibration | Gate shake detection behind a **screen-off + phone-carried heuristic** — if the phone has been stationary on a flat surface (checked via a short gyroscope/orientation stability read) for an extended period, treat sustained vibration as ambient, not a shake gesture. |
| Kids playing with the phone | Random vigorous shaking | Cannot be fully eliminated by sensors alone — mitigated by the mandatory countdown screen (§5.2), which a child is unlikely to correctly cancel *or* let fire silently without curiosity, and which gives the real user a window to cancel it themselves if nearby. |
| Dancing, sports, rough play | Similar signature to a genuine shake gesture | User-adjustable sensitivity (Low/Medium/High) set during onboarding calibration (§10), plus the periodicity + multi-axis checks above. |
| Volume buttons pressed accidentally in pocket | Could misfire the volume-combo trigger | Require both keys within a tight ~300ms window *and* a specific press count (e.g., double-press both together), which is very unlikely to occur from incidental pocket pressure. |

### 5.2 The countdown is the real safety net
No trigger fires an alert instantly. Every trigger opens either:
- A **full-screen, high-contrast countdown (5–10 sec)** with a large "I'm Safe / Cancel" button and a distinct, unmistakable vibration pattern (so the user recognizes "this is the SOS countdown, not a random notification," even through fabric), **or**
- A **fully silent countdown** (no screen change, only a subtle distinct vibration) for situations where showing a countdown screen would tip off an attacker — user chooses this mode in settings.

If nothing cancels it, the alert fires automatically. This single mechanism is what converts "sensor thinks something happened" into "confirmed emergency," and is non-negotiable in the design.

### 5.3 Cool-down and repeat-trigger handling
After any cancellation, a short cool-down period (e.g., 60 seconds) suppresses new shake-based triggers so a genuinely twitchy walk right after cancelling doesn't immediately refire. Volume and manual triggers remain live during cool-down, since those are deliberate actions.

### 5.4 Duress cancel (recommended v1.5 feature)
A realistic edge case: an attacker sees the countdown screen and forces the victim to cancel it. Solution — offer a **decoy cancel**: entering a distinct "duress code" (vs. the normal safe code) on the cancel screen *appears* to cancel the alert to anyone watching, but silently keeps the SMS/call/location pipeline running in the background. This is a well-established pattern in personal-safety and ATM-panic-PIN design and is a strong differentiator.

### 5.5 False-negative handling (the gesture didn't fire when it should have)
- Onboarding includes a **guided calibration** where the user practices the real shake/volume gesture so thresholds are tuned to her actual phone, grip, and strength (§10).
- Always keep at least 2 independent trigger paths live (e.g., shake + volume, or volume + notification quick-action) so a single sensor failure or missed gesture doesn't leave her with zero options.
- A **"did you mean to trigger this?" learning loop** (opt-in): if the user manually opens the app and hits SOS shortly after a shake event that didn't fire, log it locally to help her recalibrate sensitivity — no data leaves the device.

---

## 6. Core Feature Set

### 6.1 Emergency Contact Management
- Add contact: Name, phone number, relation (dropdown: Parent, Sibling, Spouse, Friend, Colleague, Other + custom).
- Minimum 1, recommended 3–5 contacts, with drag-to-reorder priority.
- Phone number validation (format + country-code check) at entry time to prevent silent failures from typos.
- **Verification step**: send a one-time test SMS so the contact knows they're registered and what a real alert will look like — dramatically reduces "is this a scam text?" confusion in the real moment.
- **Tiered escalation** (see §7): if a top-priority contact doesn't answer/receive within a set window, automatically escalate to the next.

### 6.2 On-Trigger Actions
1. **SMS to all emergency contacts**, containing: pre-set custom message (editable template), live GPS coordinates + a Maps link, timestamp, and — if relevant — a battery/connectivity status line (§9).
2. **Missed call to each contact, sequentially** (ring ~3–5 sec then end) so the contact gets a call notification and instantly knows to check the SMS for details.
3. **SMS/call to 112 (ERSS)** with location and a custom danger message — see §8.3 for why this replaces a literal "nearest police station" implementation.
4. **Continuing location updates** every 2–3 minutes for a fixed window (e.g., 30 min) or until cancelled, so contacts can track movement rather than just the starting point.

---

## 7. Delivery Reliability & Escalation Logic

A trigger firing is only half the job — the alert has to actually land. This section defines what happens when it doesn't.

| Failure mode | Handling |
|---|---|
| SMS send fails (no signal, carrier rejects, blocked number) | Retry with exponential backoff (e.g., at 10s, 30s, 90s); after 3 failures on the primary SIM, retry via the secondary SIM on dual-SIM devices. |
| Recipient's phone is off / unreachable | The missed call and SMS will simply queue/land whenever their phone reconnects (standard carrier behavior); meanwhile auto-escalate the alert to the next contact in priority order so there's no single point of failure. |
| No SMS delivery receipt within a timeout | Treat as unconfirmed, continue escalating down the contact list, and note it in the local incident log for post-event review. |
| GPS has no lock (indoors, tunnel, poor sky view) | Fall back to `FusedLocationProviderClient`'s network/cell-based location; send it with an explicit accuracy radius in the message ("location accurate to ~500m") rather than a false precise-looking pin. |
| No data connection at all | Live-tracking link and cloud upload features are skipped entirely — SMS and calls run over the cellular voice/SMS path, which doesn't need data, so the core alert still goes out. |
| Airplane mode toggled (accidentally or by an attacker) | Detect the mode change via a `BroadcastReceiver` and immediately fire an alert with the last known location **before** connectivity is lost, rather than waiting and silently failing. |
| Phone powered off entirely | Out of scope for software alone — no app can act after full shutdown. Documented as a known limitation; a v3 companion-device/wearable ping (§13) is the only real mitigation path. |
| Low battery during an active alert | Auto-switch to a reduced-power mode: keep SMS/call/location alive, but pause non-essential background recording/upload to conserve the battery for the parts that matter most. |
| Multiple rapid re-triggers | Debounce with a minimum interval between full alert cycles (distinct from the cool-down in §5.3) so a malfunctioning sensor can't spam contacts and carriers. |
| App force-stopped or uninstalled by an abuser with phone access | Mitigated via an app-lock/PIN on settings and uninstall-protection prompts where the OS allows (Device Admin-based uninstall confirmation); full prevention isn't possible on stock Android without enterprise MDM, and this limitation should be disclosed honestly to users rather than overpromised. |
| Regional-language SMS length | Non-GSM (Unicode) SMS segments are ~70 characters vs. 160 for GSM-7 — explicitly handle multi-part SMS concatenation so a Maps link doesn't get silently truncated when the message is in a regional language. |
| Roaming / travel outside India | 112 will not route correctly abroad. Detect SIM/network country code and, as a v2 item, map to the correct local emergency number (911, 999, 112-EU, etc.) rather than blindly dialing 112 everywhere. |
| OS kills the background service | Foreground service with a persistent low-priority "Safety monitoring active" notification + `WorkManager` periodic health-check + `BOOT_COMPLETED` receiver to restart after reboot (see §11 for OEM-specific onboarding to prevent this in the first place). |
| Permission silently revoked (Android auto-revokes unused permissions after months of inactivity) | Periodic self-check on app open / periodic background check; if a critical permission is missing, surface a persistent, unmissable warning rather than failing silently the day it's needed. |

---

## 8. Critical Technical Realities to Design Around

### 8.1 Power button long-press — Android does *not* expose this to third-party apps
There is **no public Android API that lets a normal (non-system, non-device-owner) app detect a power-button long-press**, because the power button is intercepted by the OS before it reaches apps (it triggers the power menu / Emergency SOS screen). Real workarounds, roughly in order of practicality:

- **Volume button combo instead** (recommended primary): Volume Up + Down held together, or triple-press, can be captured via an Accessibility Service, and is the mechanism most existing Indian safety apps actually use in place of the power button.
- **Accessibility Service approach**: register an `AccessibilityService` to intercept volume key events even from the lock screen — legal and store-approved as long as you clearly disclose why the permission is needed (Play Store is strict about Accessibility Service justification).
- Device Admin/Device Owner APIs *can* get closer to power-key interception but require enterprise provisioning (kiosk mode) — impractical for a consumer app installed by a regular user.
- **Recommendation**: keep power-button-long-press as a "best effort, may not work on all OEMs" secondary trigger, and make the **volume button combo the flagship, reliable trigger**, alongside shake.

### 8.2 "Lock-screen widget" — also restricted since Android 5.0
Keyguard (lock-screen) widgets were removed from stock Android. The realistic substitute:
- A **notification with quick-action buttons**, set to show on the lock screen (high-priority notification channel, `VISIBILITY_PUBLIC`) — tapping an action button fires the SOS without unlocking.
- Some OEM launchers (Samsung, some MIUI versions) allow custom lock-screen shortcuts, but this isn't a portable Android API — treat it as a bonus, not core.

### 8.3 "SMS + danger message to the nearby police station" — no such public interface exists
There is no per-station SMS gateway or open API for the ~17,000+ police stations in India, so a literal implementation isn't achievable as originally specced. What's real and achievable:
- **ERSS-112**, India's unified emergency system, explicitly accepts distress signals over SMS, voice call, panic-button signals, and its own app, and routes them to the correct state Public Safety Answering Point (PSAP), which then dispatches police/fire/health as needed.
- The practical version of this feature: **send the SMS (with location + danger message) to 112, and/or programmatically place a call to 112**, rather than to "the nearest police station." 112 already handles the geographic routing to the right station.
- Some states additionally support WhatsApp and web-portal channels into the same PSAP system — verify per-state availability before committing, since coverage varies by state.
- Recommendation: rename this feature internally to **"Emergency services alert (112/ERSS)"** and be explicit in onboarding that it reaches the government emergency responder, not a specific station.

### 8.4 Permission & Android-version considerations
- `SEND_SMS`, `CALL_PHONE`, `ACCESS_FINE_LOCATION`, `ACCESS_BACKGROUND_LOCATION` (Android 10+ needs a separate background-location grant, and Play Store review for this is strict), `ACCESSIBILITY_SERVICE` (volume-key capture), `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_LOCATION` (Android 14+ requires typed foreground service declarations), `RECEIVE_BOOT_COMPLETED`, `POST_NOTIFICATIONS` (Android 13+), `HIGH_SAMPLING_RATE_SENSORS` if using fast accelerometer sampling.
- Placing a call via `Intent.ACTION_CALL` works silently in the background with `CALL_PHONE` granted — no dialer UI shown, so the "missed call" behavior is achievable; ending it programmatically uses `TelecomManager.endCall()`, which has version-specific restrictions worth testing per OEM.
- Google Play has a **restricted "SMS and Call Log" permissions policy** — apps requesting `SEND_SMS`/`CALL_PHONE` must qualify via a declaration form explaining the safety use case. Budget review time; this is a common rejection point for safety apps.
- Consider `WorkManager` + `ForegroundService` with a persistent low-priority notification to keep the shake-detector alive — Android will kill a plain background service quickly otherwise.

---

## 9. Additional Features Worth Adding (practical, real-world value)

1. **Silent audio/video recording on trigger** — auto-record to a hidden/encrypted folder, auto-upload to cloud storage in the background if data is available. Often the single most useful evidence-gathering feature in real incidents.
2. **Fake incoming call button** — one tap simulates a realistic incoming call with a chosen contact name, giving a socially acceptable exit from an uncomfortable situation.
3. **Live location shareable link** (data-dependent) — a temporary tracking link contacts can open in a browser to watch real-time movement, not just a single pinned point.
4. **Works without internet** — SMS + call path runs over the cellular network only; only live-tracking-link/upload features need data, with graceful fallback (§7).
5. **Battery optimization whitelisting flow** — first-launch walkthrough to disable battery optimization / enable autostart, tailored per detected OEM (Xiaomi/Vivo/Oppo/Realme all differ). Without this, background shake detection gets killed within hours on most Indian Android devices.
6. **Decoy/disguise mode** — disguise the app icon and name (e.g., as a calculator or notes app) for situations of domestic surveillance/abuse.
7. **Geofencing safe/unsafe zones + "Follow Me" journeys** — auto-alert a trusted contact if the user hasn't checked in after entering a marked unsafe area or after an expected arrival time.
8. **Battery/network status included in the alert SMS** — e.g., "Battery: 12%, no data connection," so contacts know whether to expect further updates or act immediately.
9. **Multi-language SMS templates** — India has huge regional-language variance; let the pre-set message be composed in the user's preferred language (see §7 for the multi-part-SMS handling this requires).
10. **Offline SMS queueing** — automatic retry rather than silently dropping a failed send.
11. **Accessibility for low-light/one-handed use** — large tap targets, high contrast, works with the screen reader off.
12. **Post-incident report export** — a simple PDF/log (timestamps, location trail, recording, contacts notified) that can be handed to police or family as an evidence record.
13. **Auto-timeout safeguard** — continuous location updates and recording auto-disable after a fixed window even if not manually cancelled, so the phone doesn't die mid-emergency.

---

## 10. Suggested Technical Architecture

- **Language**: Kotlin (preferred over Java — coroutines make the async SMS/call/location sequencing on trigger much cleaner).
- **Trigger listeners**: `SensorEventListener` (accelerometer, batched low-power mode) + `ActivityRecognitionClient` (context gating, §5.1) + `AccessibilityService` (volume-key capture) + `BroadcastReceiver` (boot restart, airplane-mode change) — all hosted inside one long-running `ForegroundService`.
- **Location**: `FusedLocationProviderClient` for fast, accurate last-known + live updates, with accuracy-radius reporting.
- **SMS**: `SmsManager.sendTextMessage()`/`sendMultipartTextMessage()` — works without any server backend.
- **Calls**: `Intent.ACTION_CALL` with a `Handler`-based delayed hang-up via `TelecomManager.endCall()`.
- **Live tracking link (optional, data-dependent)**: Firebase Realtime Database/Firestore, periodic location pings keyed by a shareable alert ID; a lightweight web page reads and displays it on a map.
- **Local storage**: Room database for contacts, alert history, message templates, and the local false-positive/negative log (§5.5).
- **Encryption**: encrypt recordings/location logs at rest using Jetpack Security's `EncryptedFile`.
- **Backend**: not required for MVP — v1 can be fully on-device; add a minimal backend later only for live-tracking links or cloud backup of recordings.

---

## 11. Non-Functional Requirements

- **Trigger-to-first-SMS latency**: under 3 seconds from confirmed trigger (post-countdown) to first SMS dispatch.
- **Battery footprint**: batched/low-power sensor mode for shake detection, not continuous high-frequency polling.
- **Reliability across OEMs**: explicit test matrix for Samsung, Xiaomi/Redmi, Vivo, Oppo, Realme, OnePlus — these differ significantly in background-process killing behavior.
- **Privacy**: all recordings/location history stored locally and encrypted by default; nothing leaves the device unless live-tracking is explicitly enabled by the user.
- **Accessibility**: large touch targets, high-contrast countdown screen, works one-handed, distinct vibration signature.
- **False-positive rate target**: define and track a measurable target (e.g., fewer than 1 unintended full-alert firing per 30 days of normal use in beta testing) as an explicit release gate, not just a design intention.

---

## 12. Onboarding Flow

1. Permission walkthrough with a plain-language "why" for each (reduces both user drop-off and Play Store rejection risk).
2. OEM-specific battery-optimization exemption walkthrough (detect manufacturer, show tailored steps).
3. Add at least one emergency contact (blocking step).
4. Send a test/verification alert (clearly labeled "TEST — no action needed") so contacts know what a real one will look like.
5. **Guided trigger calibration** — a short practice session for both the shake gesture and the volume combo, tuning thresholds to the user's actual phone, grip, and strength, and directly feeding the false-positive defenses in §5.
6. Optional: set up the duress code (§5.4) and decoy/disguise mode (§9.6).

---

## 13. Testing Plan Highlights

- **False-positive suite**: normal walking, running, phone in bag while cycling, phone in pocket during a commute, dancing, gym workout, washing machine/dryer proximity, kids handling the phone, being dropped — none should fire a full alert.
- **False-negative suite**: simulated struggle/shake patterns at varying intensities and phone positions (hand, pocket, bag) — must fire reliably within the calibrated threshold.
- **OEM background-kill testing**: leave the app untouched for 24/48/72 hours on each target OEM and confirm the service still responds.
- **Connectivity-degradation testing**: airplane-mode-except-calls, no-data-only-voice, dual-SIM primary-fails-fallback-to-secondary, zero-signal-then-recovers.
- **GPS-degradation testing**: indoors, underground parking, dense urban canyon — confirm graceful fallback to network location with honest accuracy reporting.
- **Battery-drain benchmarking** over a full day of passive monitoring, and separately during an active multi-minute alert.
- **Escalation-path testing**: primary contact unreachable → confirm automatic escalation to next contact within the defined timeout.
- **Duress-cancel testing**: confirm the decoy cancel visually behaves identically to a real cancel while the alert pipeline keeps running silently.

---

## 14. Suggested Phased Roadmap

**V1 (MVP)**: Shake trigger with full false-positive defense stack, volume-button trigger, countdown/cancel (visible + silent modes), cool-down debounce, contact management with verification, SMS+missed call to contacts, SMS/call to 112, lock-screen notification quick action, boot-persistence, escalation logic, OEM battery-optimization onboarding.

**V1.5**: Duress/decoy cancel code, activity-recognition-based context gating, multi-part regional-language SMS handling, local false-positive learning log.

**V2**: Silent audio/video recording, live-tracking shareable link, fake call, decoy app icon, geofencing/"Follow Me" check-in mode, post-incident report export.

**V3**: Wearable integration (smartwatch trigger, functions even if the phone is grabbed), community/volunteer alert layer (in the spirit of 112 India's SHOUT feature), state-specific WhatsApp/portal integrations where available, international emergency-number auto-mapping for roaming.

---

## 15. Open Questions to Resolve Before Build

- Which OEMs are must-support for v1 vs. best-effort?
- Should "missed call" hang up automatically, or ring until the contact answers (some users may prefer an actual conversation)?
- Data retention policy for recordings — how long stored locally, and is cloud backup in scope for v1?
- Will you pursue Play Store's Core App Quality / sensitive-permissions declaration process now, since it can add real review time?
- What's the acceptable false-positive rate for beta release, and how will it be measured before wider rollout?
- Is uninstall-protection (Device Admin-based) worth the added onboarding friction, given it isn't foolproof?
o

