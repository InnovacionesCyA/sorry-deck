# Sorry Deck

A digital deck of cards inspired by the classic Sorry! game. Built with Kotlin + Jetpack Compose for Android.

## Features

- Canonical 45-card deck (1×5, 2×4, 3×4, 4×4, 5×4, 7×4, 8×4, 10×4, 11×4, 12×4, Sorry!×4 — no 6, no 9)
- Each card shows the rule (e.g. "4 — Move 4 BACKWARD", "2 — DRAW AGAIN")
- Tap card to draw next; card-flip animation with sound
- Comedic "dun-dun-duuun" sting on Sorry! cards
- Shuffle button appears when deck is empty
- Scroll back through previously drawn cards to verify play (◀ / ▶ arrows)
- Sorry!-inspired visual styling (no trademarks used)

## Build

The GitHub Actions workflow at `.github/workflows/build-apk.yml` builds a debug APK on every push to `main`. Download it from the workflow run's **Artifacts** section.

To build locally:

```bash
./gradlew :app:assembleDebug
# APK at: app/build/outputs/apk/debug/app-debug.apk
```

Install on a phone via:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

Or transfer the APK file to the phone and tap to install (enable "Install from unknown sources" if prompted).

## Project layout

- `app/src/main/java/com/cavacollective/sorrydeck/Deck.kt` — deck data & shuffle
- `app/src/main/java/com/cavacollective/sorrydeck/SoundManager.kt` — SoundPool wrapper
- `app/src/main/java/com/cavacollective/sorrydeck/MainActivity.kt` — UI & animation
- `app/src/main/res/raw/` — generated sound effects (royalty-free, synthesized with ffmpeg)
