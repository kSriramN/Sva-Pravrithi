# Sva-Pravrithi — Android App (v1.0)

A mindful expense tracker built around the Three Gunas and a Needs / Wants / Pleasures
framework, per the PRD and TDD. Kotlin + Jetpack Compose + Room + Hilt, fully offline.

## Getting started

1. Open this folder (`SvaPravrithi/`) directly in **Android Studio** (Koala or newer).
   Android Studio will detect there's no Gradle wrapper jar and offer to generate one —
   accept that, or run `gradle wrapper` yourself once if you have Gradle installed locally.
2. Let Gradle sync (first sync will download Compose, Room, Hilt, Navigation dependencies —
   needs an internet connection).
3. Run on an emulator or device with **minSdk 26 (Android 8.0)+**.

### Add the real Poppins font (optional, ~30 seconds)

The design reference specifies Poppins. The app currently falls back to the system
sans-serif font (`app/.../ui/theme/Type.kt`) since I couldn't fetch font files without
network access while building this. To use real Poppins:

1. Right-click `app/src/main/res/font` → **New → Font Resource File**.
2. In the dialog, switch to the **Google Fonts** tab, search "Poppins", and add
   Regular, Medium, and SemiBold weights (Android Studio downloads them and wires up
   the certificates automatically — no manual config needed).
3. In `Type.kt`, uncomment the `Poppins` FontFamily block and set
   `val AppFontFamily = Poppins`.

### Enable Google Drive backup (required for the Backup & Restore feature)

The app can back up/restore all local data (expenses, plans, declarations, scoring
config) to the signed-in user's own Google Drive, using a restrictive scope
(`drive.file`) that only lets the app see files it created itself — never the rest
of the user's Drive.

This needs a one-time setup in [Google Cloud Console](https://console.cloud.google.com)
before sign-in will work (you can't skip this — Google requires the app's real
package name + signing certificate to be registered):

1. Create (or reuse) a project, then enable the **Google Drive API** under
   "APIs & Services → Library".
2. Get your debug (and later, release) SHA-1 fingerprint:
   ```
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
3. Under "APIs & Services → Credentials → Create Credentials → OAuth client ID",
   choose **Android**, set the package name to `com.svapravrithi.app`, and paste in
   the SHA-1 from step 2.
4. Configure the **OAuth consent screen** (External is fine for testing) and add
   your own Google account as a test user while the app is unpublished.
5. That's it — no `google-services.json` or API key needs to go in the app itself;
   Google Sign-In matches sign-in requests against the package name + SHA-1 you
   registered.
6. Repeat step 2–3 with your **release** keystore's SHA-1 before publishing.

Until this is set up, tapping "Sign in with Google" in Backup & Restore will fail
with an authentication error — everything else in the app is unaffected.

- **UI**: Jetpack Compose, Material3, single-Activity + Navigation Compose
- **State**: MVVM, `StateFlow` per screen, Hilt-injected ViewModels
- **Persistence**: Room (`expenses`, `plans`, `declarations`, `scoring_config` tables) — fully offline, no accounts
- **DI**: Hilt
- **Charts**: hand-built Compose `Canvas` (donut chart, Guna Mandala, progress bars) — no external chart library dependency

## Where the business logic lives

- `domain/engine/ReflectionEngine.kt` — Savings/Wants/Pleasures scoring, verbatim from the TDD (no caps, per product decision)
- `domain/engine/DominantGunaEngine.kt` — derives Sattva/Rajas/Tamas from the three pillar scores (not from per-expense tags)
- `domain/engine/MonthlyFinancials.kt` — plain shared shape for the month's budgets vs. actuals
- `domain/engine/ScoringConfig.kt` — tunable divisors/multipliers, persisted in Room, editable without a code change
- `data/backup/` — Google Drive export/import (JSON snapshot of all local data)

## Key product decisions baked into this build (confirmed in chat)

1. **Actual Savings is tracked directly, not derived.** It's a habit parameter — the
   amount the user has actually put aside for investment — completely independent of
   income or of Needs/Wants/Pleasures spend. The user updates it themselves any time via
   **Profile → Update Savings**. (An earlier version of this build incorrectly derived
   it from an implied income calculation; that's been removed.)
2. **No score caps in v1.0** — scores can go arbitrarily above/below the 50-point base;
   the "X / 150" shown in analytics is a nominal label only, not an enforced ceiling.
3. **Dominant Guna** is derived from goal adherence (Savings/Wants/Pleasures scores),
   *not* from per-expense Guna tags. The exact severity-comparison rule is documented
   in `DominantGunaEngine.kt` and is easy to retune if the thresholds need adjusting.
4. **Per-expense Guna tag is optional**, shown only as a secondary "personal reflection"
   chart in Guna Analytics — it does not affect the score or the dominant Guna.
5. **Fully offline** — no login, no cloud sync, no Life Balance Matrix (deferred).

## Screens implemented (routes in `ui/navigation/Destinations.kt`)

Splash → Onboarding (3-page) → Monthly Declaration (Savings Goal → Needs → Wants →
Pleasures Budget) → Home (Guna Mandala + Needs/Wants/Pleasures pager) → Add Expense →
Plan List / Add Plan → Analytics Overview → Guna / Spending / Savings Analytics →
Monthly Reflection → Profile & Settings → Update Savings.

## Known gaps / next steps

- Launcher icon is a simple placeholder vector — swap in real brand artwork when ready.
- Edit-existing-plan (tapping a plan row) currently opens Add Plan blank rather than
  pre-filled; wire `AddPlanViewModel.load()` to fetch the existing `PlanEntity` by id
  (a one-line addition to `PlanRepository`) if in-place editing is needed.
- No unit test module yet — `ReflectionEngine`'s math was hand-verified against the
  TDD's worked examples (Savings 70, Wants 40, Pleasures 25) but isn't wired into
  Android's test runner. Worth adding a `test/` source set for regression safety.
