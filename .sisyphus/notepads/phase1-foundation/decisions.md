# Phase 1 Decisions

## 2026-03-24 Session Start

### Module Structure Decisions
- `:core:model` = pure Kotlin JVM module (`org.jetbrains.kotlin.jvm` plugin)
- All other new modules = Android library (`com.android.library` plugin)
- Fonts: bundled TTF in `res/font/` — NOT Google Fonts API

### Dependency Strategy
- Keep `com.google.android.material:material` — needed for XML theme `Theme.Material3.DayNight.NoActionBar` in manifest
- Remove: appcompat, constraintlayout
- Add `activity-compose` (replaces `activity` dep — `activity-compose` transitively includes `activity`)
- Compose BOM: single platform() call aligns all Compose artifacts

### API Key
- BuildConfig field `WEATHER_API_KEY` reads from `local.properties` via `project.findProperty("WEATHER_API_KEY")`
- Default is empty string `""` if property not found

### Theme Scope
- Light theme ONLY in Phase 1 — no dark theme, no dynamic color
- Shapes: small=8dp, medium=16dp, large=32dp, extraLarge=48dp

### Commit Groups
- C1: Gradle infra (libs.versions.toml, root build.gradle.kts, settings.gradle.kts, gradle.properties)
- C2: All 4 new modules + app/build.gradle.kts update
- C3: WeatherApplication, MainActivity, theme files, fonts, manifest, delete activity_main.xml
