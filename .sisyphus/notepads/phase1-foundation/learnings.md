# Phase 1 Learnings

## 2026-03-24 Session Start

### Current State (verified by reading files)
- `gradle/libs.versions.toml`: 10 versions, 8 libraries, 1 plugin (android-application only)
- `build.gradle.kts` (root): Single `alias(libs.plugins.android.application) apply false`
- `settings.gradle.kts`: Only `include(":app")` — 27 lines
- `gradle.properties`: JVM args + kotlin.code.style=official, parallel commented out
- `app/build.gradle.kts`: AppCompatActivity-era deps: appcompat, constraintlayout, material, activity, core-ktx

### AGP 9.1.0 Kotlin Version (CRITICAL — must run diagnostic before pinning KSP)
- AGP 9.1 bundles Kotlin — the bundled version determines valid KSP plugin version
- Format: KSP = `{kotlin-version}-{ksp-patch}` (e.g., `2.1.20-1.0.31`)
- Must run `./gradlew buildEnvironment` to confirm BEFORE writing KSP version to catalog

### Plugin Rules
- MUST NOT apply `org.jetbrains.kotlin.android` (AGP bundles it)
- MUST use `org.jetbrains.kotlin.plugin.compose` for Compose compiler (separate from bundled Kotlin)
- `compileSdk` syntax: `compileSdk { version = release(36) { minorApiLevel = 1 } }` (AGP 9.x)
- KSP plugin ID: `com.google.devtools.ksp`
- Hilt plugin ID: `com.google.dagger.hilt.android`

### Dependency Naming Conventions (for libs.versions.toml)
- Compose BOM group: `androidx.compose` NOT `androidx.compose.ui`
- `activity-compose` lib entry needed for `setContent {}`
- `hilt-compiler` is used with `ksp()` not `kapt()`

### Resource Preservation (NEVER DELETE)
- `app/src/main/res/values/themes.xml`
- `app/src/main/res/values-night/themes.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/xml/backup_rules.xml`
- `app/src/main/res/xml/data_extraction_rules.xml`
- All `mipmap-*` and `drawable/` resources

- Successfully created :core:model as a pure Kotlin JVM module using alias(libs.plugins.kotlin.jvm).
- Verified that no AndroidManifest.xml is required for JVM modules.
- Build passed with ./gradlew :core:model:assemble.
## Task 8 — Compose Migration Learnings (2026-03-24)
- Google Fonts GitHub repo no longer has `static/` subfolder for Manrope or Inter — only variable fonts (`Manrope[wght].ttf`, `Inter[opsz,wght].ttf`)
- `curl -L` on `github.com/…/raw/…` URLs returns HTML pages, not raw files. Must use `raw.githubusercontent.com/…` for actual content.
- Even `raw.githubusercontent.com` returns 404/tiny files when the `static/` dir doesn't exist. Need to check directory structure first via GitHub API.
- For Manrope: downloaded variable font and used `fontTools.varLib.mutator.instantiateVariableFont()` to create static Bold (wght=700) and ExtraBold (wght=800) instances.
- For Inter: used release zip from `rsms/inter` GitHub repo (`v4.1`) — static fonts are in `extras/ttf/` inside the zip.
- `fontTools.varLib.mutator.instantiateVariableFont` is deprecated but works; newer `fontTools.instancer` module wasn't importable on Python 3.9 + fonttools 4.60.2.
- Android `res/font/` naming: only lowercase letters, digits, underscores. All downloaded/generated files were renamed accordingly.
- 33 color tokens created in Color.kt — exceeds the 30+ requirement.
- BUILD SUCCESSFUL on first attempt after all files were in place.

## T9 Verification (2026-03-24)
- Full sequence rerun completed successfully: `clean assembleDebug`, `test`, and per-module assemble targets for `:core:model`, `:core:network`, `:core:data`, `:feature:weather`.
- Anti-scope-creep checks all returned `EXIT:1` (no forbidden matches):
  - no `org.jetbrains.kotlin.android` in Gradle files
  - no `AppCompatActivity` in app sources
  - no `R.layout` in app main Java/Kotlin sources
  - no `Room`/`DataStore` deps in core:data and feature:weather build files
  - no `NavHost`/navigation wiring in core/app/feature build scripts
- Preserved resource inventory confirmed present, including values themes/colors/strings, xml backup/data extraction, and all mipmap launcher assets.
- Debug APK confirmed present at `app/build/outputs/apk/debug/app-debug.apk`.
- No code/build-file fix-up was required because all verifications passed as-is.

## T9 (WeatherRepository) — 2026-03-24
- DataModule binds both CityRepository and WeatherRepository in a single `@Module` — consolidates DI wiring for the entire `:core:data` module
- `DefaultWeatherRepository` is `internal class` so DTOs don't leak; `FakeWeatherRepository` is public for cross-module test usage in `:feature:weather`
- FakeWeatherApi (from `:core:network`) is a public class, so tests in `:core:data` can instantiate it directly without DI
- JUnit 4 `assertThrows` is not available; used `try/catch` + `fail()` pattern for exception assertions
- `catch (_: IOException)` — Kotlin allows underscore for unused catch variable, cleaner than naming it
- `kotlinx-coroutines-test` + `runTest` works out of the box — no special dispatcher setup needed for simple suspend function tests

## F4 Scope Fidelity Check — 2026-03-24
- Phase 2 data-pipeline source additions are confined to `core/model`, `core/network`, and `core/data`; no matching DTO references were found under `app/src` or `feature/`.
- Forbidden framework imports scan returned clean for `androidx.room`, `androidx.datastore`, and `androidx.compose` across core source sets.
- `FakeWeatherApi` resides in `core/network/src/main/.../FakeWeatherApi.kt`, and `FakeWeatherRepository` resides in `core/data/src/main/.../FakeWeatherRepository.kt`, matching intended module boundaries.
