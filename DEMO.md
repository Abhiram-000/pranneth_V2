# RakshaSetu — 2-minute Demo / Presentation Script

## Slide 1 — The problem
In a safety emergency you cannot unlock your phone, open an app, and type. Existing SOS apps need the screen unlocked and fire on every bumpy bus ride.

## Slide 2 — The gesture
1. Open the app, finish onboarding, run calibration (measures your real shake).
2. Put the phone in your pocket.
3. **Either** shake (distinct 3 non-rhythmic multi-axis jolts), **or** press Volume Up + Down together.
4. A full-screen countdown appears — with distinct vibration so you feel it through fabric.

## Slide 3 — The escalation ladder
- Cancel? → "I'm Safe" — done.
- Do nothing → SMS with GPS (or honest network estimate) to every contact, missed calls, then 112.
- First contact doesn't acknowledge within 30 s → next contact gets escalated to.
- Live position updates every ~2 min for 30 min.

## Slide 4 — If someone is watching you
- Long-press Cancel, enter your **duress PIN**.
- UI looks like a normal cancel; toast says "Alert cancelled."
- The alert actually **keeps running silently**, and the DB row is flagged `is_duress=true`.

## Slide 5 — The tricky details
- Don't shake? It's a distinct pattern → periodic running/walking is filtered, a shaking washing-machine is filtered.
- No GPS indoors? Network fix used, labeled "network estimate."
- No data? The SMS/call path still works.
- SMS send fails? Retry at 10 s, 30 s, 90 s; after that, switch to the secondary SIM.
- OS kills the background service? Watchdog restarts it (opt-in) — and forces the app to again warn you.
- OEM battery killer (Xiaomi/Vivo/Oppo)? Onboarding walks you through the vendor-specific autostart exemption.

## What to show in the code
- `ShakeDetector.kt` — false-positive defenses pinned by tests
- `AlertDispatchService.kt` — SMS + missed calls + 112, alert log
- `CountdownActivity.kt` — visible vs silent vs duress paths
- `SecurePrefs.kt` — credentials-level encrypted preferences

## What we did not demo
- Real carrier 112 SMS acceptance
- Physical OEM force-stop recovery
- Battery drain over 24 h

*These are the known "real device required" items.*
