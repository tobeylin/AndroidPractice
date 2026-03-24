# Phase 1 — Foundation: Multi-Module Compose Shell

## TL;DR

> **Quick Summary**: Transform the single-module View-based Android project into a compilable 5-module Compose shell with Hilt DI, Material 3 theme (Stitch design tokens), and basic infrastructure — resulting in a "Hello World" Compose app.
> 
> **Deliverables**:
> - 5-module Gradle project (`:app`, `:feature:weather`, `:core:data`, `:core:model`, `:core:network`)
> - `MainActivity` on `ComponentActivity` with Compose `setContent`
> - Hilt DI wiring (`@HiltAndroidApp` + `@AndroidEntryPoint`)
> - Material 3 theme with Stitch color palette, typography (Manrope + Inter), and custom shapes
> - BuildConfig API key placeholder from `local.properties`
> - INTERNET permission, XML layout deleted
> 
> **Estimated Effort**: Medium
> **Parallel Execution**: YES - 4 waves
> **Critical Path**: T1 (Gradle infra) -> T2-T6 (module scaffolds) -> T7-T8 (Compose migration) -> T9 (verification)

---

## Context

### Original Request
Implement Phase 1 (Foundation) of the PlaygroundWeather app as defined in `docs/phase-plan.md`. Stitch design system available at `docs/stitch_playgroundweather/`.

### Current State
- **Single `:app` module** with `AppCompatActivity` + XML `ConstraintLayout` showing "Hello World!"
- **AGP 9.1.0**, Gradle 9.3.1, no Kotlin plugin (bundled by AGP)
- **View-based dependencies**: appcompat, constraintlayout, material (View), activity, core-ktx
- **XML theme**: `Theme.Material3.DayNight.NoActionBar` in `values/themes.xml` + `values-night/themes.xml`
- **Resources to preserve**: `values/strings.xml`, `values/colors.xml`, `xml/backup_rules.xml`, `xml/data_extraction_rules.xml`, all `mipmap-*`, `drawable/ic_launcher_*`
- **Existing tests**: `ExampleUnitTest.kt` (JVM), `ExampleInstrumentedTest.kt` (instrumented)

### Interview Summary
**Key Discussions**:
- Phase plan is fully documented in `docs/phase-plan.md` with explicit scope and acceptance criteria
- Stitch design system provides complete color tokens, typography spec (Manrope + Inter), and shape definitions
- AGP 9.1 bundles Kotlin — must NOT apply `org.jetbrains.kotlin.android`
- Compose compiler via `org.jetbrains.kotlin.plugin.compose` (separate from bundled Kotlin)

**Research Findings**:
- Hilt 2.59.2+ required for AGP 9.x compatibility
- Compose BOM ~2026.02.00 (latest stable)
- KSP version MUST match AGP's bundled Kotlin version (Critical: determine via `./gradlew buildEnvironment`)
- Retrofit 3.0.0 with native suspend support (stable)
- `:core:model` safe as pure Kotlin JVM module
- XML theme must remain for manifest — keep `com.google.android.material:material` dependency
- Each Android library module needs at minimum an empty `AndroidManifest.xml`

### Metis Review
**Identified Gaps** (addressed):
- XML layout deletion + MainActivity rewrite must be atomic (same task)
- `com.google.android.material:material` must be preserved for manifest XML theme
- KSP version must match AGP's bundled Kotlin — added diagnostic step
- Existing resources (themes, strings, backup rules, launcher icons) must be preserved
- `enableEdgeToEdge()` import from `androidx.activity` — verify available in `activity-compose`
- Compose plugin version must align with AGP's bundled Kotlin — auto-resolution or pinning
- Empty Android library modules need `AndroidManifest.xml` for AGP 9.x

---

## Work Objectives

### Core Objective
Create a compilable 5-module Compose project shell with Hilt DI and Material 3 theme, migrating from the current single-module View-based setup.

### Concrete Deliverables
- `gradle/libs.versions.toml` with all Phase 1-5 dependency declarations
- `settings.gradle.kts` including all 5 modules
- `build.gradle.kts` for each module (root + 5 modules)
- `WeatherApplication.kt` with `@HiltAndroidApp`
- `MainActivity.kt` on `ComponentActivity` with Compose `setContent`
- `ui/theme/Color.kt`, `Type.kt`, `Shape.kt`, `Theme.kt` — Material 3 theme from Stitch
- Font resource files (Manrope + Inter `.ttf`)
- `AndroidManifest.xml` updated with INTERNET permission + Application class
- `activity_main.xml` deleted (XML layout removed)

### Definition of Done
- [ ] `./gradlew clean assembleDebug` — BUILD SUCCESSFUL (all 5 modules)
- [ ] `./gradlew test` — BUILD SUCCESSFUL (ExampleUnitTest passes)
- [ ] `grep -r "AppCompatActivity" app/src/` — no matches
- [ ] `ls app/src/main/res/layout/` — no XML layouts exist
- [ ] `grep "HiltAndroidApp" app/src/main/java/playground/app/tobeylin/weather/WeatherApplication.kt` — found
- [ ] `grep "ComponentActivity" app/src/main/java/playground/app/tobeylin/weather/MainActivity.kt` — found
- [ ] `grep "INTERNET" app/src/main/AndroidManifest.xml` — found

### Must Have
- All 5 modules compile successfully
- Hilt `@HiltAndroidApp` in Application class
- `@AndroidEntryPoint` on `MainActivity`
- Compose `setContent` with `PlaygroundWeatherTheme` wrapping `Text("Hello World")`
- Material 3 theme with Stitch color tokens (light theme)
- INTERNET permission in manifest
- No XML layouts remaining
- Existing unit tests still pass

### Must NOT Have (Guardrails)
- **NO `org.jetbrains.kotlin.android` plugin** — AGP 9.1 bundles Kotlin; applying this causes conflict
- **NO Retrofit service interfaces or API implementations** — deps declared in Gradle only, no Kotlin code
- **NO Room/DataStore schemas** — `:core:data` is a shell only
- **NO ViewModels, UseCases, or Repositories** — business logic is Phase 2+
- **NO navigation (NavHost, navigation graph)** — one screen only
- **NO convention plugins** — direct plugin application per module
- **NO Hilt `@Module`, `@Provides`, `@Binds`** — just Application class + AndroidEntryPoint
- **NO business logic in composables** — only `Text("Hello World")` wrapped in theme
- **NO deletion of** `values/themes.xml`, `values-night/themes.xml`, `values/strings.xml`, `values/colors.xml`, `xml/backup_rules.xml`, `xml/data_extraction_rules.xml`, or any `mipmap-*`/`drawable/` resources
- **NO removal of `com.google.android.material:material`** — needed for manifest XML theme `Theme.Material3.DayNight.NoActionBar`

---

## Verification Strategy (MANDATORY)

> **ZERO HUMAN INTERVENTION** — ALL verification is agent-executed. No exceptions.

### Test Decision
- **Infrastructure exists**: YES (JUnit 4)
- **Automated tests**: None new (Phase 1 is scaffolding; existing ExampleUnitTest must pass)
- **Framework**: JUnit 4 (existing)
- **TDD**: Not applicable — Phase 1 has no testable business logic

### QA Policy
Every task includes agent-executed QA scenarios using Bash commands.
Evidence saved to `.sisyphus/evidence/task-{N}-{scenario-slug}.{ext}`.

- **Build verification**: `./gradlew assembleDebug` / `./gradlew test`
- **File checks**: `ls`, `grep`, `cat` commands to verify file existence and content
- **Anti-scope-creep**: `grep` commands to verify forbidden patterns are absent

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately — Gradle infrastructure):
└── Task 1: Version catalog + root build + settings + properties [quick]

Wave 2 (After Wave 1 — module scaffolding, MAX PARALLEL):
├── Task 2: :core:model module scaffold [quick]
├── Task 3: :core:network module scaffold [quick]
├── Task 4: :core:data module scaffold [quick]
├── Task 5: :feature:weather module scaffold [quick]
└── Task 6: Update :app build.gradle.kts [quick]

Wave 3 (After Wave 2 — Compose migration):
├── Task 7: Hilt Application class + Manifest updates [quick]
└── Task 8: MainActivity migration + Theme + Font resources + Cleanup [unspecified-high]

Wave 4 (After Wave 3 — verification + fix):
└── Task 9: Comprehensive build verification + fixes [deep]

Wave FINAL (After ALL tasks — 4 parallel reviews, then user okay):
├── Task F1: Plan compliance audit (oracle)
├── Task F2: Code quality review (unspecified-high)
├── Task F3: Real manual QA (unspecified-high)
└── Task F4: Scope fidelity check (deep)
-> Present results -> Get explicit user okay

Critical Path: T1 -> T2-T6 (any) -> T8 -> T9 -> F1-F4 -> user okay
Parallel Speedup: ~60% faster than sequential
Max Concurrent: 5 (Wave 2)
```

### Dependency Matrix

| Task | Blocked By | Blocks |
|------|-----------|--------|
| T1   | None      | T2, T3, T4, T5, T6 |
| T2   | T1        | T9 |
| T3   | T1        | T9 |
| T4   | T1        | T9 |
| T5   | T1        | T9 |
| T6   | T1        | T7, T8, T9 |
| T7   | T6        | T9 |
| T8   | T6        | T9 |
| T9   | T2-T8     | F1-F4 |
| F1-F4| T9        | User okay |

### Agent Dispatch Summary

- **Wave 1**: **1** — T1 -> `quick`
- **Wave 2**: **5** — T2-T5 -> `quick`, T6 -> `quick`
- **Wave 3**: **2** — T7 -> `quick`, T8 -> `unspecified-high`
- **Wave 4**: **1** — T9 -> `deep`
- **FINAL**: **4** — F1 -> `oracle`, F2 -> `unspecified-high`, F3 -> `unspecified-high`, F4 -> `deep`

---

## TODOs

- [x] 1. Gradle Infrastructure — Version Catalog + Root Build + Settings

  **What to do**:
  - **DIAGNOSTIC FIRST**: Run `./gradlew buildEnvironment` to determine the exact Kotlin compiler version bundled with AGP 9.1.0. Note this version — it determines the KSP plugin version (format `{kotlin-version}-{ksp-version}`).
  - Update `gradle/libs.versions.toml`:
    - **Keep existing**: `agp = "9.1.0"`, `coreKtx = "1.18.0"`, `junit = "4.13.2"`, `junitVersion = "1.3.0"`, `espressoCore = "3.7.0"`, `activity = "1.13.0"` (rename to `activityCompose`), `material = "1.13.0"` (keep for XML theme)
    - **Remove**: `appcompat`, `constraintlayout` versions
    - **Add versions**: `composeBom` (latest stable, ~`2026.02.00`), `hilt` (`2.59.2`), `ksp` (match bundled Kotlin from diagnostic), `retrofit` (`3.0.0`), `okhttp` (`5.0.0-alpha.14`), `kotlinxSerializationJson` (latest stable for Kotlin 2.x)
    - **Add libraries**: `compose-bom`, `compose-ui`, `compose-ui-graphics`, `compose-ui-tooling-preview`, `compose-material3`, `compose-ui-tooling` (debugImpl), `compose-ui-test-manifest` (debugImpl), `compose-ui-test-junit4`, `activity-compose`, `hilt-android`, `hilt-compiler`, `retrofit-core`, `retrofit-kotlinx-serialization`, `okhttp-logging`, `kotlinx-serialization-json`
    - **Remove libraries**: `androidx-appcompat`, `androidx-constraintlayout`
    - **Add plugins**: `android-library`, `kotlin-compose` (`org.jetbrains.kotlin.plugin.compose`, version = bundled Kotlin), `kotlin-serialization` (`org.jetbrains.kotlin.plugin.serialization`, version = bundled Kotlin), `ksp`, `hilt`
  - Update `build.gradle.kts` (root):
    - Add all new plugins with `apply false`: `android-library`, `kotlin-compose`, `kotlin-serialization`, `ksp`, `hilt`
    - Keep existing `android-application apply false`
  - Update `settings.gradle.kts`:
    - Add `include(":feature:weather")`, `include(":core:data")`, `include(":core:model")`, `include(":core:network")`
    - Keep existing `include(":app")`
  - Update `gradle.properties`:
    - Add `android.nonTransitiveRClass=true` (recommended for multi-module)
    - Uncomment/add `org.gradle.parallel=true` (speeds up multi-module builds)

  **Must NOT do**:
  - Do NOT apply `org.jetbrains.kotlin.android` plugin anywhere
  - Do NOT add Google Fonts dependencies (`ui-text-google-fonts`)
  - Do NOT add Room, DataStore, or Navigation dependencies
  - Do NOT modify any Kotlin source files or resources

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Configuration-only changes to 4 well-defined files. No complex logic.
  - **Skills**: []
    - No specialized skills needed — standard Gradle/TOML editing

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 1 (solo)
  - **Blocks**: T2, T3, T4, T5, T6
  - **Blocked By**: None (can start immediately)

  **References** (CRITICAL):

  **Pattern References**:
  - `gradle/libs.versions.toml:1-24` — Current version catalog structure; preserve format, extend with new entries
  - `build.gradle.kts:1-4` (root) — Current root build; extend plugins block
  - `settings.gradle.kts:1-27` — Current settings; add `include()` statements after line 26
  - `app/build.gradle.kts:7-11` — `compileSdk` syntax to replicate in library modules: `compileSdk { version = release(36) { minorApiLevel = 1 } }`

  **External References**:
  - AGP 9.x release notes: plugin declaration patterns for `com.android.library`
  - Compose BOM: `androidx.compose:compose-bom` (group is `androidx.compose`, NOT `androidx.compose.ui`)
  - KSP GitHub releases: match version to Kotlin compiler output from diagnostic
  - Hilt docs: `com.google.dagger.hilt.android` plugin ID, `com.google.dagger:hilt-android` artifact

  **WHY Each Reference Matters**:
  - `libs.versions.toml` current structure: MUST preserve the existing format (group/name/version.ref pattern) and extend consistently
  - `compileSdk` block syntax: AGP 9.x specific — all Android library modules must use this exact pattern
  - Root build plugins: ALL plugins used in ANY submodule must be declared here with `apply false`
  - Settings includes: module paths determine directory structure (`":feature:weather"` -> `feature/weather/`)

  **Acceptance Criteria**:

  - [ ] `./gradlew tasks --all` succeeds (no plugin resolution errors)
  - [ ] `grep "android-library" gradle/libs.versions.toml` — found
  - [ ] `grep "kotlin-compose" gradle/libs.versions.toml` — found
  - [ ] `grep "hilt" gradle/libs.versions.toml` — found
  - [ ] `grep "ksp" gradle/libs.versions.toml` — found
  - [ ] `grep "compose-bom" gradle/libs.versions.toml` — found
  - [ ] `grep "feature:weather" settings.gradle.kts` — found
  - [ ] `grep "core:data" settings.gradle.kts` — found
  - [ ] `grep "core:model" settings.gradle.kts` — found
  - [ ] `grep "core:network" settings.gradle.kts` — found
  - [ ] `grep -v "appcompat" gradle/libs.versions.toml` confirms appcompat removed (or `grep "appcompat"` returns no match)

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Gradle plugin resolution succeeds
    Tool: Bash
    Preconditions: Wave 1 files updated
    Steps:
      1. Run `./gradlew tasks --all 2>&1 | tail -20`
      2. Check output contains "BUILD SUCCESSFUL"
      3. Run `./gradlew buildEnvironment 2>&1 | grep -i "kotlin"` to capture Kotlin version
    Expected Result: No resolution errors, BUILD SUCCESSFUL
    Failure Indicators: "Could not resolve", "Plugin not found", "BUILD FAILED"
    Evidence: .sisyphus/evidence/task-1-gradle-tasks.txt

  Scenario: Version catalog entries are valid TOML
    Tool: Bash
    Preconditions: libs.versions.toml updated
    Steps:
      1. Run `./gradlew :app:dependencies --configuration implementation 2>&1 | head -50`
      2. Verify no "FAILED" or "Could not resolve" in output
    Expected Result: All dependencies resolve successfully
    Failure Indicators: "Could not resolve", "FAILED"
    Evidence: .sisyphus/evidence/task-1-deps-resolution.txt
  ```

  **Commit**: YES (C1)
  - Message: `build: add Compose, Hilt, KSP, Retrofit deps and multi-module structure to Gradle config`
  - Files: `gradle/libs.versions.toml`, `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`
  - Pre-commit: `./gradlew tasks`

---

- [x] 2. Create `:core:model` Module — Pure Kotlin JVM

  **What to do**:
  - Create directory: `core/model/`
  - Create `core/model/build.gradle.kts`:
    ```
    plugins {
        alias(libs.plugins.kotlin.jvm)  // Use the plugin declared for JVM Kotlin
    }
    // Note: If `kotlin.jvm` is not in version catalog, use the appropriate plugin.
    // For AGP 9.1 with bundled Kotlin, this may need `id("org.jetbrains.kotlin.jvm")`
    // with the version matching AGP's bundled Kotlin.
    ```
    **IMPORTANT**: If `org.jetbrains.kotlin.jvm` plugin is NOT in the version catalog from T1, add it. This plugin is separate from the Android Kotlin compilation (which AGP handles). It's needed for pure JVM modules.
  - Create source directory: `core/model/src/main/kotlin/playground/app/tobeylin/weather/core/model/`
  - NO `AndroidManifest.xml` needed (pure JVM, not Android library)
  - NO source files needed (empty module — models will be added in Phase 2)

  **Must NOT do**:
  - Do NOT create any data classes, interfaces, or business logic
  - Do NOT add any dependencies beyond what the JVM plugin provides
  - Do NOT make this an Android library module

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Create 1 file + 1 directory. Trivial scaffolding.
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with T3, T4, T5, T6)
  - **Blocks**: T9
  - **Blocked By**: T1

  **References**:

  **Pattern References**:
  - `app/build.gradle.kts:1-3` — Plugin declaration pattern using `alias(libs.plugins.xxx)`

  **External References**:
  - Kotlin JVM plugin: `org.jetbrains.kotlin.jvm` — for pure Kotlin modules without Android framework
  - Now in Android `:core:model`: Uses JVM plugin for pure domain models

  **WHY Each Reference Matters**:
  - Plugin alias pattern: must match version catalog format from T1
  - JVM plugin: `:core:model` has NO Android dependencies — pure Kotlin data classes for domain models

  **Acceptance Criteria**:

  - [ ] `ls core/model/build.gradle.kts` — file exists
  - [ ] `ls core/model/src/main/kotlin/playground/app/tobeylin/weather/core/model/` — directory exists
  - [ ] `grep "kotlin" core/model/build.gradle.kts` — contains kotlin plugin reference

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: :core:model compiles as JVM module
    Tool: Bash
    Preconditions: T1 complete, module directory and build.gradle.kts created
    Steps:
      1. Run `./gradlew :core:model:assemble 2>&1 | tail -5`
      2. Check output contains "BUILD SUCCESSFUL"
    Expected Result: Module compiles with zero errors
    Failure Indicators: "BUILD FAILED", "Plugin not found", "Could not resolve"
    Evidence: .sisyphus/evidence/task-2-core-model-build.txt

  Scenario: Module is NOT an Android module
    Tool: Bash
    Preconditions: Module created
    Steps:
      1. Run `ls core/model/src/main/AndroidManifest.xml 2>&1`
      2. Check output contains "No such file" (no manifest = not Android)
    Expected Result: No AndroidManifest.xml exists
    Failure Indicators: File exists (means it was incorrectly made an Android module)
    Evidence: .sisyphus/evidence/task-2-no-manifest.txt
  ```

  **Commit**: YES (groups with C2)
  - Message: `feat(core): scaffold :core:model as pure Kotlin JVM module`
  - Files: `core/model/build.gradle.kts`
  - Pre-commit: `./gradlew :core:model:assemble`

---

- [x] 3. Create `:core:network` Module — Android Library with Networking Deps

  **What to do**:
  - Create directory: `core/network/`
  - Create `core/network/build.gradle.kts`:
    - Plugins: `android-library`, `kotlin-compose` (NO — not needed here), `kotlin-serialization`, `ksp`, `hilt`
    - Actually: Plugins: `android-library`, `kotlin-serialization`, `ksp`, `hilt`
    - `android` block: `namespace = "playground.app.tobeylin.weather.core.network"`, same `compileSdk`, `minSdk = 24`, `compileOptions` (Java 11)
    - Dependencies: `implementation(project(":core:model"))`, `implementation(libs.retrofit.core)`, `implementation(libs.retrofit.kotlinx.serialization)`, `implementation(libs.okhttp.logging)`, `implementation(libs.kotlinx.serialization.json)`, `implementation(libs.hilt.android)`, `ksp(libs.hilt.compiler)`
  - Create `core/network/src/main/AndroidManifest.xml` (minimal: just `<manifest />` — namespace is in build.gradle.kts)
  - Create source directory: `core/network/src/main/java/playground/app/tobeylin/weather/core/network/`
  - NO Kotlin source files (shell only)

  **Must NOT do**:
  - Do NOT create Retrofit service interfaces, OkHttp clients, or interceptors
  - Do NOT create DTO data classes
  - Do NOT apply `org.jetbrains.kotlin.android` — AGP handles Kotlin for Android modules
  - Do NOT add Compose dependencies (this module has no UI)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Create 2 files + 1 directory. Straightforward Gradle config.
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with T2, T4, T5, T6)
  - **Blocks**: T9
  - **Blocked By**: T1

  **References**:

  **Pattern References**:
  - `app/build.gradle.kts:5-35` — Android block structure (compileSdk, defaultConfig, compileOptions) — replicate for library modules
  - `gradle/libs.versions.toml` (after T1) — Dependency aliases for Retrofit, OkHttp, Serialization, Hilt

  **External References**:
  - AGP `com.android.library` plugin: same Android block as application but no `applicationId`
  - Hilt in library modules: apply plugin + `ksp(libs.hilt.compiler)` for annotation processing

  **WHY Each Reference Matters**:
  - `app/build.gradle.kts` Android block: library modules need the same `compileSdk`, `minSdk`, `compileOptions` — copy the pattern
  - Empty AndroidManifest: AGP 9.x requires manifest even for library modules; namespace is set in Gradle

  **Acceptance Criteria**:

  - [ ] `ls core/network/build.gradle.kts` — file exists
  - [ ] `ls core/network/src/main/AndroidManifest.xml` — file exists
  - [ ] `grep "namespace" core/network/build.gradle.kts` — contains `playground.app.tobeylin.weather.core.network`
  - [ ] `grep "retrofit" core/network/build.gradle.kts` — contains retrofit dependency
  - [ ] `grep "hilt" core/network/build.gradle.kts` — contains hilt dependency

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: :core:network compiles as Android library
    Tool: Bash
    Preconditions: T1 complete, module files created
    Steps:
      1. Run `./gradlew :core:network:assembleDebug 2>&1 | tail -5`
      2. Check output contains "BUILD SUCCESSFUL"
    Expected Result: Module compiles, all dependencies resolve
    Failure Indicators: "BUILD FAILED", "Could not resolve", dependency version mismatch
    Evidence: .sisyphus/evidence/task-3-core-network-build.txt

  Scenario: Dependencies resolve correctly
    Tool: Bash
    Preconditions: Module build.gradle.kts created
    Steps:
      1. Run `./gradlew :core:network:dependencies --configuration implementation 2>&1 | head -30`
      2. Verify Retrofit, OkHttp, Kotlinx Serialization, Hilt appear in dependency tree
    Expected Result: All declared dependencies resolve without errors
    Failure Indicators: "FAILED", "Could not resolve"
    Evidence: .sisyphus/evidence/task-3-core-network-deps.txt
  ```

  **Commit**: YES (groups with C2)
  - Message: `feat(core): scaffold :core:network with Retrofit, OkHttp, Hilt deps`
  - Files: `core/network/build.gradle.kts`, `core/network/src/main/AndroidManifest.xml`
  - Pre-commit: `./gradlew :core:network:assembleDebug`

- [x] 4. Create `:core:data` Module — Android Library with Repository Deps

  **What to do**:
  - Create directory: `core/data/`
  - Create `core/data/build.gradle.kts`:
    - Plugins: `android-library`, `ksp`, `hilt`
    - `android` block: `namespace = "playground.app.tobeylin.weather.core.data"`, `compileSdk { version = release(36) { minorApiLevel = 1 } }`, `minSdk = 24`, `compileOptions` (Java 11 source + target)
    - Dependencies: `implementation(project(":core:model"))`, `implementation(project(":core:network"))`, `implementation(libs.hilt.android)`, `ksp(libs.hilt.compiler)`
  - Create `core/data/src/main/AndroidManifest.xml` — minimal: just `<manifest />`
  - Create source directory: `core/data/src/main/java/playground/app/tobeylin/weather/core/data/`
  - NO Kotlin source files (shell only — repositories come in Phase 2)

  **Must NOT do**:
  - Do NOT create Repository interfaces, implementations, or data source abstractions
  - Do NOT add Room, DataStore, or any local storage dependencies
  - Do NOT apply `org.jetbrains.kotlin.android` — AGP handles Kotlin
  - Do NOT add Compose dependencies (this module has no UI)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Create 2 files + 1 directory. Identical pattern to T3 with different deps.
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with T2, T3, T5, T6)
  - **Blocks**: T9
  - **Blocked By**: T1

  **References**:

  **Pattern References**:
  - `core/network/build.gradle.kts` (created by T3) — Android library module pattern to replicate. Copy the `android {}` block structure, adjust namespace and dependencies.
  - `app/build.gradle.kts:5-35` — `compileSdk` block syntax and `compileOptions` pattern

  **External References**:
  - Hilt in library modules: same pattern as `:core:network` — plugin + ksp annotation processor

  **WHY Each Reference Matters**:
  - `:core:network` build file: `:core:data` follows the exact same Android library template, only differing in namespace and dependency list
  - `compileSdk` block: Must use AGP 9.x `release(36)` syntax, not integer literal

  **Acceptance Criteria**:

  - [ ] `ls core/data/build.gradle.kts` — file exists
  - [ ] `ls core/data/src/main/AndroidManifest.xml` — file exists
  - [ ] `ls core/data/src/main/java/playground/app/tobeylin/weather/core/data/` — directory exists
  - [ ] `grep "namespace" core/data/build.gradle.kts` — contains `playground.app.tobeylin.weather.core.data`
  - [ ] `grep ":core:model" core/data/build.gradle.kts` — depends on :core:model
  - [ ] `grep ":core:network" core/data/build.gradle.kts` — depends on :core:network
  - [ ] `grep "hilt" core/data/build.gradle.kts` — contains hilt dependency

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: :core:data compiles as Android library
    Tool: Bash
    Preconditions: T1 complete, T2 (:core:model) and T3 (:core:network) complete (dependencies)
    Steps:
      1. Run `./gradlew :core:data:assembleDebug 2>&1 | tail -5`
      2. Check output contains "BUILD SUCCESSFUL"
    Expected Result: Module compiles, project dependencies resolve
    Failure Indicators: "BUILD FAILED", "Could not resolve project :core:model", "Could not resolve project :core:network"
    Evidence: .sisyphus/evidence/task-4-core-data-build.txt

  Scenario: No Room/DataStore dependencies leaked in
    Tool: Bash
    Preconditions: Module build.gradle.kts created
    Steps:
      1. Run `grep -i "room\|datastore" core/data/build.gradle.kts`
      2. Check output is empty (exit code 1, no matches)
    Expected Result: Zero matches — no storage deps in Phase 1
    Failure Indicators: Any match found
    Evidence: .sisyphus/evidence/task-4-no-storage-deps.txt
  ```

  **Commit**: YES (groups with C2)
  - Message: `feat(core): scaffold :core:data with Hilt and project deps`
  - Files: `core/data/build.gradle.kts`, `core/data/src/main/AndroidManifest.xml`
  - Pre-commit: `./gradlew :core:data:assembleDebug`

---

- [x] 5. Create `:feature:weather` Module — Android Library with Compose + Hilt

  **What to do**:
  - Create directory: `feature/weather/`
  - Create `feature/weather/build.gradle.kts`:
    - Plugins: `android-library`, `kotlin-compose`, `ksp`, `hilt`
    - `android` block:
      - `namespace = "playground.app.tobeylin.weather.feature.weather"`
      - `compileSdk { version = release(36) { minorApiLevel = 1 } }`
      - `defaultConfig { minSdk = 24 }`
      - `compileOptions` (Java 11 source + target)
      - `buildFeatures { compose = true }` — REQUIRED for Compose in library module
    - Dependencies:
      - `implementation(project(":core:data"))`
      - `implementation(project(":core:model"))`
      - `implementation(platform(libs.compose.bom))`
      - `implementation(libs.compose.ui)`
      - `implementation(libs.compose.ui.graphics)`
      - `implementation(libs.compose.material3)`
      - `implementation(libs.compose.ui.tooling.preview)`
      - `debugImplementation(libs.compose.ui.tooling)`
      - `implementation(libs.hilt.android)`
      - `ksp(libs.hilt.compiler)`
  - Create `feature/weather/src/main/AndroidManifest.xml` — minimal: just `<manifest />`
  - Create source directory: `feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/`
  - NO Kotlin source files (screens/ViewModels come in Phase 2+)

  **Must NOT do**:
  - Do NOT create Composable functions, ViewModels, or screen files
  - Do NOT apply `org.jetbrains.kotlin.android` — AGP handles Kotlin
  - Do NOT add Navigation dependencies (Phase 3)
  - Do NOT add `activity-compose` here — that belongs in `:app` only

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Create 2 files + 1 directory. Slightly more complex than T3/T4 due to Compose config, but still straightforward scaffolding.
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with T2, T3, T4, T6)
  - **Blocks**: T9
  - **Blocked By**: T1

  **References**:

  **Pattern References**:
  - `core/network/build.gradle.kts` (created by T3) — Base Android library module pattern; `:feature:weather` adds Compose on top
  - `app/build.gradle.kts:5-35` — `android {}` block structure and `compileSdk` syntax

  **External References**:
  - Compose in library modules: `buildFeatures { compose = true }` + `kotlin-compose` plugin required
  - Compose BOM: `platform(libs.compose.bom)` aligns all Compose artifact versions automatically
  - Now in Android feature modules: Each feature module applies `compose` plugin + depends on `:core:data` and `:core:model`

  **WHY Each Reference Matters**:
  - `:core:network` build file: Base template for Android library — add Compose plugins + deps on top
  - `buildFeatures { compose = true }`: Without this, Compose compiler won't process `@Composable` annotations in this module
  - BOM platform: Prevents version mismatches across Compose artifacts

  **Acceptance Criteria**:

  - [ ] `ls feature/weather/build.gradle.kts` — file exists
  - [ ] `ls feature/weather/src/main/AndroidManifest.xml` — file exists
  - [ ] `ls feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/` — directory exists
  - [ ] `grep "namespace" feature/weather/build.gradle.kts` — contains `playground.app.tobeylin.weather.feature.weather`
  - [ ] `grep "compose" feature/weather/build.gradle.kts` — contains compose references (plugin + buildFeatures)
  - [ ] `grep ":core:data" feature/weather/build.gradle.kts` — depends on :core:data
  - [ ] `grep ":core:model" feature/weather/build.gradle.kts` — depends on :core:model
  - [ ] `grep "material3" feature/weather/build.gradle.kts` — contains Material 3 dep

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: :feature:weather compiles with Compose enabled
    Tool: Bash
    Preconditions: T1 complete, T2-T4 complete (transitive deps)
    Steps:
      1. Run `./gradlew :feature:weather:assembleDebug 2>&1 | tail -5`
      2. Check output contains "BUILD SUCCESSFUL"
    Expected Result: Module compiles, Compose BOM resolves, all project deps resolve
    Failure Indicators: "BUILD FAILED", "Compose compiler plugin not applied", "Could not resolve"
    Evidence: .sisyphus/evidence/task-5-feature-weather-build.txt

  Scenario: Compose plugin is properly applied
    Tool: Bash
    Preconditions: Module build.gradle.kts created
    Steps:
      1. Run `grep -c "compose" feature/weather/build.gradle.kts`
      2. Verify count >= 3 (plugin alias + buildFeatures + at least one compose dep)
    Expected Result: Multiple compose references present
    Failure Indicators: Count < 3 (missing compose config)
    Evidence: .sisyphus/evidence/task-5-compose-config.txt
  ```

  **Commit**: YES (groups with C2)
  - Message: `feat(feature): scaffold :feature:weather with Compose and Hilt deps`
  - Files: `feature/weather/build.gradle.kts`, `feature/weather/src/main/AndroidManifest.xml`
  - Pre-commit: `./gradlew :feature:weather:assembleDebug`

- [x] 6. Update `:app` Module — Add Compose, Hilt, BuildConfig, Remove View Deps

  **What to do**:
  - Update `app/build.gradle.kts`:
    - **Add plugins**: `kotlin-compose` (alias from catalog), `ksp`, `hilt`
    - **Add to `android {}` block**:
      - `buildFeatures { compose = true; buildConfig = true }`
      - Add `buildConfigField` for API key:
        ```kotlin
        defaultConfig {
            // ... existing fields ...
            val weatherApiKey: String = project.findProperty("WEATHER_API_KEY") as? String ?: ""
            buildConfigField("String", "WEATHER_API_KEY", "\"$weatherApiKey\"")
        }
        ```
    - **Update dependencies**:
      - ADD: `implementation(project(":feature:weather"))`, `implementation(platform(libs.compose.bom))`, `implementation(libs.compose.ui)`, `implementation(libs.compose.ui.graphics)`, `implementation(libs.compose.material3)`, `implementation(libs.compose.ui.tooling.preview)`, `debugImplementation(libs.compose.ui.tooling)`, `debugImplementation(libs.compose.ui.test.manifest)`, `implementation(libs.activity.compose)`, `implementation(libs.hilt.android)`, `ksp(libs.hilt.compiler)`
      - REMOVE: `implementation(libs.androidx.appcompat)`, `implementation(libs.androidx.constraintlayout)`
      - KEEP: `implementation(libs.androidx.core.ktx)`, `implementation(libs.material)` (for XML theme), `implementation(libs.androidx.activity)` (or replace with `activity-compose` — use catalog alias), `testImplementation(libs.junit)`, `androidTestImplementation(libs.androidx.junit)`, `androidTestImplementation(libs.androidx.espresso.core)`
    - **Note**: If `activity-compose` replaces `activity`, the `enableEdgeToEdge()` function is available from `androidx.activity.enableEdgeToEdge` — it's an extension on `ComponentActivity` from the `activity` artifact, and `activity-compose` depends on `activity` transitively.

  **Must NOT do**:
  - Do NOT remove `implementation(libs.material)` — still needed for XML theme in manifest
  - Do NOT apply `org.jetbrains.kotlin.android` — AGP bundles Kotlin
  - Do NOT add Navigation, Room, or DataStore dependencies
  - Do NOT modify any Kotlin source files in this task
  - Do NOT modify `AndroidManifest.xml` in this task (that's T7)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Single file modification with well-defined adds/removes. No complex logic.
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with T2, T3, T4, T5)
  - **Blocks**: T7, T8, T9
  - **Blocked By**: T1

  **References**:

  **Pattern References**:
  - `app/build.gradle.kts:1-47` — CURRENT state of the file; this task modifies it in-place
  - `feature/weather/build.gradle.kts` (created by T5) — Compose plugin + buildFeatures pattern to match in `:app`

  **External References**:
  - `activity-compose` artifact: `androidx.activity:activity-compose` — provides `setContent {}` for Compose in Activity
  - Hilt plugin in app module: `com.google.dagger.hilt.android` — processes `@HiltAndroidApp` and `@AndroidEntryPoint`
  - BuildConfig API key pattern: `project.findProperty()` reads from `local.properties` or `gradle.properties`

  **WHY Each Reference Matters**:
  - Current `app/build.gradle.kts`: This IS the file being modified — executor must read it first, then make precise edits
  - `:feature:weather` Compose config: `:app` uses the same Compose setup pattern (plugin + buildFeatures + BOM)
  - `activity-compose`: REQUIRED for `setContent {}` — without it, `ComponentActivity.setContent` is not available

  **Acceptance Criteria**:

  - [ ] `grep "kotlin-compose\|kotlin.compose" app/build.gradle.kts` — compose plugin applied
  - [ ] `grep "hilt" app/build.gradle.kts` — hilt plugin applied
  - [ ] `grep "ksp" app/build.gradle.kts` — ksp plugin applied
  - [ ] `grep "compose = true" app/build.gradle.kts` — compose enabled in buildFeatures
  - [ ] `grep "buildConfig = true" app/build.gradle.kts` — buildConfig enabled
  - [ ] `grep "WEATHER_API_KEY" app/build.gradle.kts` — API key field present
  - [ ] `grep "feature:weather" app/build.gradle.kts` — depends on feature module
  - [ ] `grep "appcompat" app/build.gradle.kts` returns NO match — appcompat removed
  - [ ] `grep "constraintlayout" app/build.gradle.kts` returns NO match — constraintlayout removed
  - [ ] `grep "material" app/build.gradle.kts` returns match — material (View) kept

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: :app compiles with Compose + Hilt plugins
    Tool: Bash
    Preconditions: T1 complete, T2-T5 complete (all module deps exist)
    Steps:
      1. Run `./gradlew :app:assembleDebug 2>&1 | tail -10`
      2. Check output contains "BUILD SUCCESSFUL"
    Expected Result: App module compiles with all new plugins and dependencies
    Failure Indicators: "BUILD FAILED", "Plugin not found", "Could not resolve", "Duplicate class"
    Evidence: .sisyphus/evidence/task-6-app-build.txt

  Scenario: Removed deps are gone, kept deps remain
    Tool: Bash
    Preconditions: app/build.gradle.kts updated
    Steps:
      1. Run `grep "appcompat" app/build.gradle.kts; echo "EXIT:$?"`
      2. Run `grep "constraintlayout" app/build.gradle.kts; echo "EXIT:$?"`
      3. Run `grep "material" app/build.gradle.kts; echo "EXIT:$?"`
    Expected Result: appcompat → no match (exit 1), constraintlayout → no match (exit 1), material → match found (exit 0)
    Failure Indicators: appcompat or constraintlayout still present, or material missing
    Evidence: .sisyphus/evidence/task-6-deps-audit.txt
  ```

  **Commit**: YES (groups with C2)
  - Message: `build(app): add Compose, Hilt, KSP plugins and deps; remove appcompat + constraintlayout`
  - Files: `app/build.gradle.kts`
  - Pre-commit: `./gradlew :app:assembleDebug`

---

- [x] 7. Hilt Application Class + Manifest Updates

  **What to do**:
  - Create `app/src/main/java/playground/app/tobeylin/weather/WeatherApplication.kt`:
    ```kotlin
    package playground.app.tobeylin.weather

    import android.app.Application
    import dagger.hilt.android.HiltAndroidApp

    @HiltAndroidApp
    class WeatherApplication : Application()
    ```
    - Empty body — no initialization logic in Phase 1
  - Update `app/src/main/AndroidManifest.xml`:
    - Add `android:name=".WeatherApplication"` attribute to `<application>` tag
    - Add `<uses-permission android:name="android.permission.INTERNET" />` BEFORE `<application>` tag
    - Keep ALL existing attributes and elements unchanged

  **Must NOT do**:
  - Do NOT add any initialization logic in `WeatherApplication` (no `onCreate` override, no DI module setup)
  - Do NOT create any Hilt `@Module`, `@Provides`, or `@Binds` classes
  - Do NOT modify `MainActivity.kt` in this task (that's T8)
  - Do NOT change the theme reference in manifest — keep `android:theme="@style/Theme.PlaygroundWeather"`

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Create 1 small file + edit 2 lines in manifest. Very straightforward.
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 3 (with T8)
  - **Blocks**: T9
  - **Blocked By**: T6 (app module needs Hilt plugin applied first)

  **References**:

  **Pattern References**:
  - `app/src/main/AndroidManifest.xml` — CURRENT manifest; add `android:name` to `<application>` and `<uses-permission>` before it
  - `app/src/main/java/playground/app/tobeylin/weather/MainActivity.kt` — Existing Kotlin file in same package; `WeatherApplication.kt` goes in same directory

  **External References**:
  - Hilt setup guide: `@HiltAndroidApp` annotation triggers Hilt's code generation for the Application class
  - Android INTERNET permission: Required for network access in `:core:network` (Retrofit calls)

  **WHY Each Reference Matters**:
  - `AndroidManifest.xml`: Must read current state to find exact `<application>` tag for attribute insertion
  - Package directory: `WeatherApplication.kt` must be in same package as `MainActivity.kt` for the `.WeatherApplication` shorthand in manifest

  **Acceptance Criteria**:

  - [ ] `ls app/src/main/java/playground/app/tobeylin/weather/WeatherApplication.kt` — file exists
  - [ ] `grep "HiltAndroidApp" app/src/main/java/playground/app/tobeylin/weather/WeatherApplication.kt` — annotation present
  - [ ] `grep "WeatherApplication" app/src/main/AndroidManifest.xml` — referenced in manifest
  - [ ] `grep "INTERNET" app/src/main/AndroidManifest.xml` — permission present

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Hilt Application class compiles and is wired in manifest
    Tool: Bash
    Preconditions: T6 complete (Hilt plugin in :app), manifest exists
    Steps:
      1. Run `./gradlew :app:assembleDebug 2>&1 | tail -5`
      2. Check output contains "BUILD SUCCESSFUL"
      3. Run `grep "android:name" app/src/main/AndroidManifest.xml`
      4. Verify output contains ".WeatherApplication"
    Expected Result: Build succeeds, manifest correctly references WeatherApplication
    Failure Indicators: "BUILD FAILED", "@HiltAndroidApp not found", "android:name" missing
    Evidence: .sisyphus/evidence/task-7-hilt-app.txt

  Scenario: INTERNET permission is declared correctly
    Tool: Bash
    Preconditions: Manifest updated
    Steps:
      1. Run `grep "uses-permission" app/src/main/AndroidManifest.xml`
      2. Verify output contains "android.permission.INTERNET"
      3. Run `grep -n "uses-permission" app/src/main/AndroidManifest.xml` — verify it's BEFORE <application>
    Expected Result: INTERNET permission declared before <application> tag
    Failure Indicators: Permission missing, or placed inside <application> tag
    Evidence: .sisyphus/evidence/task-7-internet-permission.txt
  ```

  **Commit**: YES (groups with C3)
  - Message: `feat(app): add HiltAndroidApp WeatherApplication + INTERNET permission`
  - Files: `app/src/main/java/playground/app/tobeylin/weather/WeatherApplication.kt`, `app/src/main/AndroidManifest.xml`
  - Pre-commit: `./gradlew :app:assembleDebug`

- [x] 8. MainActivity Migration + Material 3 Theme + Font Resources + XML Layout Cleanup

  **What to do**:

  **Step A — Download font files**:
  - Download Manrope font files to `app/src/main/res/font/`:
    - `manrope_bold.ttf` (700 weight)
    - `manrope_extrabold.ttf` (800 weight)
  - Download Inter font files to `app/src/main/res/font/`:
    - `inter_regular.ttf` (400 weight)
    - `inter_medium.ttf` (500 weight)
    - `inter_semibold.ttf` (600 weight)
  - Source: Google Fonts API — use `curl` to download `.ttf` files directly:
    - Manrope: `https://fonts.google.com/download?family=Manrope` (zip, extract needed weights)
    - Inter: `https://fonts.google.com/download?family=Inter` (zip, extract needed weights)
  - Rename files to snake_case per Android convention (e.g., `Manrope-Bold.ttf` -> `manrope_bold.ttf`)
  - Android `res/font/` naming rule: lowercase letters, digits, underscores only

  **Step B — Create theme files** in `app/src/main/java/playground/app/tobeylin/weather/ui/theme/`:

  1. **Color.kt** — All Stitch design tokens as Compose `Color` values:
     ```kotlin
     package playground.app.tobeylin.weather.ui.theme

     import androidx.compose.ui.graphics.Color

     // Primary
     val Primary = Color(0xFF005F9C)
     val PrimaryContainer = Color(0xFF39A6FF)
     val OnPrimary = Color(0xFFECF3FF)
     val OnPrimaryContainer = Color(0xFF002440)

     // Secondary
     val Secondary = Color(0xFF455F6B)
     val SecondaryContainer = Color(0xFFCBE7F5)
     val OnSecondary = Color(0xFFE4F5FF)
     val OnSecondaryContainer = Color(0xFF3C5561)

     // Tertiary
     val Tertiary = Color(0xFF7F5200)
     val TertiaryContainer = Color(0xFFFEB64C)
     val OnTertiary = Color(0xFFFFF0E2)
     val OnTertiaryContainer = Color(0xFF583700)

     // Surface
     val Surface = Color(0xFFF5F7F9)
     val SurfaceContainerLowest = Color(0xFFFFFFFF)
     val SurfaceContainerLow = Color(0xFFEEF1F3)
     val SurfaceContainer = Color(0xFFE5E9EB)
     val SurfaceContainerHigh = Color(0xFFDFE3E6)
     val SurfaceContainerHighest = Color(0xFFD9DDE0)
     val OnSurface = Color(0xFF2C2F31)
     val OnSurfaceVariant = Color(0xFF595C5E)
     val SurfaceVariant = Color(0xFFD9DDE0)

     // Background
     val Background = Color(0xFFF5F7F9)
     val OnBackground = Color(0xFF2C2F31)

     // Error
     val Error = Color(0xFFB31B25)
     val ErrorContainer = Color(0xFFFB5151)
     val OnError = Color(0xFFFFEFEE)
     val OnErrorContainer = Color(0xFF570008)

     // Outline
     val Outline = Color(0xFF747779)
     val OutlineVariant = Color(0xFFABADAF)

     // Inverse
     val InverseSurface = Color(0xFF0B0F10)
     val InverseOnSurface = Color(0xFF9A9D9F)
     val InversePrimary = Color(0xFF289EF8)

     // Surface tint
     val SurfaceTint = Color(0xFF005F9C)
     ```

  2. **Type.kt** — Typography using bundled font families:
     ```kotlin
     package playground.app.tobeylin.weather.ui.theme

     import androidx.compose.material3.Typography
     import androidx.compose.ui.text.TextStyle
     import androidx.compose.ui.text.font.Font
     import androidx.compose.ui.text.font.FontFamily
     import androidx.compose.ui.text.font.FontWeight
     import androidx.compose.ui.unit.sp
     import playground.app.tobeylin.weather.R

     val ManropeFontFamily = FontFamily(
         Font(R.font.manrope_bold, FontWeight.Bold),
         Font(R.font.manrope_extrabold, FontWeight.ExtraBold),
     )

     val InterFontFamily = FontFamily(
         Font(R.font.inter_regular, FontWeight.Normal),
         Font(R.font.inter_medium, FontWeight.Medium),
         Font(R.font.inter_semibold, FontWeight.SemiBold),
     )

     val Typography = Typography(
         displayLarge = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 57.sp, lineHeight = 64.sp),
         displayMedium = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 45.sp, lineHeight = 52.sp),
         displaySmall = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp),
         headlineLarge = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
         headlineMedium = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
         headlineSmall = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp),
         titleLarge = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
         titleMedium = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
         titleSmall = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
         bodyLarge = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
         bodyMedium = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
         bodySmall = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
         labelLarge = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
         labelMedium = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
         labelSmall = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp),
     )
     ```

  3. **Shape.kt** — Custom shapes from Stitch:
     ```kotlin
     package playground.app.tobeylin.weather.ui.theme

     import androidx.compose.foundation.shape.RoundedCornerShape
     import androidx.compose.material3.Shapes
     import androidx.compose.ui.unit.dp

     val Shapes = Shapes(
         small = RoundedCornerShape(8.dp),
         medium = RoundedCornerShape(16.dp),
         large = RoundedCornerShape(32.dp),
         extraLarge = RoundedCornerShape(48.dp),
     )
     ```

  4. **Theme.kt** — `PlaygroundWeatherTheme` composable:
     ```kotlin
     package playground.app.tobeylin.weather.ui.theme

     import androidx.compose.material3.MaterialTheme
     import androidx.compose.material3.lightColorScheme
     import androidx.compose.runtime.Composable

     private val LightColorScheme = lightColorScheme(
         primary = Primary,
         primaryContainer = PrimaryContainer,
         onPrimary = OnPrimary,
         onPrimaryContainer = OnPrimaryContainer,
         secondary = Secondary,
         secondaryContainer = SecondaryContainer,
         onSecondary = OnSecondary,
         onSecondaryContainer = OnSecondaryContainer,
         tertiary = Tertiary,
         tertiaryContainer = TertiaryContainer,
         onTertiary = OnTertiary,
         onTertiaryContainer = OnTertiaryContainer,
         surface = Surface,
         surfaceContainerLowest = SurfaceContainerLowest,
         surfaceContainerLow = SurfaceContainerLow,
         surfaceContainer = SurfaceContainer,
         surfaceContainerHigh = SurfaceContainerHigh,
         surfaceContainerHighest = SurfaceContainerHighest,
         onSurface = OnSurface,
         onSurfaceVariant = OnSurfaceVariant,
         surfaceVariant = SurfaceVariant,
         background = Background,
         onBackground = OnBackground,
         error = Error,
         errorContainer = ErrorContainer,
         onError = OnError,
         onErrorContainer = OnErrorContainer,
         outline = Outline,
         outlineVariant = OutlineVariant,
         inverseSurface = InverseSurface,
         inverseOnSurface = InverseOnSurface,
         inversePrimary = InversePrimary,
         surfaceTint = SurfaceTint,
     )

     @Composable
     fun PlaygroundWeatherTheme(
         content: @Composable () -> Unit,
     ) {
         MaterialTheme(
             colorScheme = LightColorScheme,
             typography = Typography,
             shapes = Shapes,
             content = content,
         )
     }
     ```

  **Step C — Rewrite MainActivity.kt**:
  - Replace ENTIRE content of `app/src/main/java/playground/app/tobeylin/weather/MainActivity.kt`:
    ```kotlin
    package playground.app.tobeylin.weather

    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.activity.enableEdgeToEdge
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.material3.Surface
    import androidx.compose.material3.Text
    import androidx.compose.ui.Modifier
    import dagger.hilt.android.AndroidEntryPoint
    import playground.app.tobeylin.weather.ui.theme.PlaygroundWeatherTheme

    @AndroidEntryPoint
    class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContent {
                PlaygroundWeatherTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Text("Hello World")
                    }
                }
            }
        }
    }
    ```

  **Step D — Delete XML layout**:
  - Delete `app/src/main/res/layout/activity_main.xml`
  - If `app/src/main/res/layout/` is now empty, delete the directory too
  - Verify NO references to `R.layout.activity_main` remain in source

  **Must NOT do**:
  - Do NOT create dark theme / `darkColorScheme()` — only light theme in Phase 1
  - Do NOT add dynamic color (`dynamicDarkColorScheme` / `dynamicLightColorScheme`)
  - Do NOT add `@Preview` composables (no screen composables to preview yet)
  - Do NOT create any screen-level composables beyond `Text("Hello World")`
  - Do NOT delete `values/themes.xml`, `values-night/themes.xml`, `values/strings.xml`, `values/colors.xml`
  - Do NOT delete any `mipmap-*`, `drawable/`, `xml/` resources
  - Do NOT use Google Fonts API (`ui-text-google-fonts`) — fonts are bundled as TTF resources

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Multiple files to create (4 theme files + font downloads + MainActivity rewrite + XML deletion). Requires careful coordination — font download, theme compilation, and Activity migration are tightly coupled. More complex than a single-file `quick` task.
  - **Skills**: []
    - No specialized skills needed — standard Kotlin/Compose file creation and font resource handling

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 3 (with T7)
  - **Blocks**: T9
  - **Blocked By**: T6 (needs Compose + Hilt in `:app` build.gradle.kts)

  **References**:

  **Pattern References**:
  - `app/src/main/java/playground/app/tobeylin/weather/MainActivity.kt` — CURRENT file to replace; read first to understand what's being removed
  - `app/src/main/res/layout/activity_main.xml` — File to DELETE; verify it exists before deleting
  - `app/build.gradle.kts` (after T6) — Confirms Compose and Hilt plugins are applied; `R.font.*` references will work because Android resource system generates them

  **API/Type References**:
  - `androidx.compose.material3.lightColorScheme` — Function signature accepts all Material 3 color slots; reference parameter names exactly
  - `androidx.compose.material3.Typography` — Constructor accepts 15 text style slots (displayLarge through labelSmall)
  - `androidx.compose.material3.Shapes` — Constructor accepts small, medium, large, extraLarge

  **External References**:
  - Stitch DESIGN.md (`docs/stitch_playgroundweather/stratus_flux/DESIGN.md`) — Source of truth for all color hex values, typography specs, and shape values. Executor should cross-reference final Color.kt against this document.
  - Google Fonts: Manrope (`https://fonts.google.com/specimen/Manrope`), Inter (`https://fonts.google.com/specimen/Inter`) — TTF download source
  - Material 3 Typography guide: Standard `TextStyle` params (`fontFamily`, `fontWeight`, `fontSize`, `lineHeight`)
  - `enableEdgeToEdge()` API: Extension on `ComponentActivity` from `androidx.activity` — must be called before `setContent`

  **WHY Each Reference Matters**:
  - Current `MainActivity.kt`: Must understand import for `AppCompatActivity` and `setContentView` that are being removed
  - `activity_main.xml`: Must verify existence before delete; if missing, skip delete step
  - Stitch DESIGN.md: THE authoritative source for every color hex, font weight, and corner radius — do NOT deviate
  - `enableEdgeToEdge()`: Call order matters — must come before `setContent` in `onCreate`

  **Acceptance Criteria**:

  - [ ] `ls app/src/main/java/playground/app/tobeylin/weather/ui/theme/Color.kt` — exists
  - [ ] `ls app/src/main/java/playground/app/tobeylin/weather/ui/theme/Type.kt` — exists
  - [ ] `ls app/src/main/java/playground/app/tobeylin/weather/ui/theme/Shape.kt` — exists
  - [ ] `ls app/src/main/java/playground/app/tobeylin/weather/ui/theme/Theme.kt` — exists
  - [ ] `ls app/src/main/res/font/manrope_bold.ttf` — exists
  - [ ] `ls app/src/main/res/font/manrope_extrabold.ttf` — exists
  - [ ] `ls app/src/main/res/font/inter_regular.ttf` — exists
  - [ ] `ls app/src/main/res/font/inter_medium.ttf` — exists
  - [ ] `ls app/src/main/res/font/inter_semibold.ttf` — exists
  - [ ] `grep "ComponentActivity" app/src/main/java/playground/app/tobeylin/weather/MainActivity.kt` — found
  - [ ] `grep "AndroidEntryPoint" app/src/main/java/playground/app/tobeylin/weather/MainActivity.kt` — found
  - [ ] `grep "PlaygroundWeatherTheme" app/src/main/java/playground/app/tobeylin/weather/MainActivity.kt` — found
  - [ ] `grep "enableEdgeToEdge" app/src/main/java/playground/app/tobeylin/weather/MainActivity.kt` — found
  - [ ] `grep "setContent" app/src/main/java/playground/app/tobeylin/weather/MainActivity.kt` — found
  - [ ] `grep "AppCompatActivity" app/src/main/java/playground/app/tobeylin/weather/MainActivity.kt` — NOT found
  - [ ] `ls app/src/main/res/layout/activity_main.xml 2>&1` — "No such file" (deleted)
  - [ ] `grep "005F9C" app/src/main/java/playground/app/tobeylin/weather/ui/theme/Color.kt` — primary color present
  - [ ] `grep "ManropeFontFamily" app/src/main/java/playground/app/tobeylin/weather/ui/theme/Type.kt` — font family defined
  - [ ] `grep "InterFontFamily" app/src/main/java/playground/app/tobeylin/weather/ui/theme/Type.kt` — font family defined

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Full app compiles and runs with Compose theme
    Tool: Bash
    Preconditions: T1-T7 complete, all theme files created, MainActivity rewritten, XML layout deleted
    Steps:
      1. Run `./gradlew :app:assembleDebug 2>&1 | tail -10`
      2. Check output contains "BUILD SUCCESSFUL"
      3. Run `ls app/build/outputs/apk/debug/app-debug.apk`
      4. Verify APK file exists
    Expected Result: App compiles with Compose theme, produces debug APK
    Failure Indicators: "BUILD FAILED", "Unresolved reference: R.font", "Unresolved reference: PlaygroundWeatherTheme", "Cannot find symbol activity_main"
    Evidence: .sisyphus/evidence/task-8-compose-build.txt

  Scenario: XML layout fully removed, no stale references
    Tool: Bash
    Preconditions: Layout deleted, MainActivity rewritten
    Steps:
      1. Run `ls app/src/main/res/layout/ 2>&1`
      2. Verify "No such file or directory" (layout dir removed)
      3. Run `grep -r "R.layout" app/src/main/java/ 2>&1`
      4. Verify no matches (no stale layout references)
    Expected Result: No layout directory, no R.layout references
    Failure Indicators: Layout directory exists, or R.layout reference found
    Evidence: .sisyphus/evidence/task-8-no-xml-layout.txt

  Scenario: Theme files contain all Stitch design tokens
    Tool: Bash
    Preconditions: Color.kt created
    Steps:
      1. Run `grep -c "Color(0x" app/src/main/java/playground/app/tobeylin/weather/ui/theme/Color.kt`
      2. Verify count >= 30 (all Stitch color tokens)
      3. Run `grep "lightColorScheme" app/src/main/java/playground/app/tobeylin/weather/ui/theme/Theme.kt`
      4. Verify lightColorScheme is used
    Expected Result: >= 30 color definitions, lightColorScheme in Theme.kt
    Failure Indicators: Color count < 30 (missing tokens), no lightColorScheme
    Evidence: .sisyphus/evidence/task-8-theme-tokens.txt

  Scenario: Font resources are valid Android font files
    Tool: Bash
    Preconditions: Font files downloaded
    Steps:
      1. Run `ls -la app/src/main/res/font/*.ttf 2>&1`
      2. Verify 5 files exist with size > 10KB each
      3. Run `file app/src/main/res/font/manrope_bold.ttf`
      4. Verify output contains "TrueType" or "font"
    Expected Result: 5 TTF files, each >10KB, valid font format
    Failure Indicators: Missing files, zero-byte files, non-font files
    Evidence: .sisyphus/evidence/task-8-font-files.txt
  ```

  **Commit**: YES (C3)
  - Message: `feat(app): migrate to Compose with Material 3 Stitch theme, bundled fonts, and Hilt`
  - Files: `app/src/main/java/playground/app/tobeylin/weather/MainActivity.kt`, `app/src/main/java/playground/app/tobeylin/weather/ui/theme/Color.kt`, `app/src/main/java/playground/app/tobeylin/weather/ui/theme/Type.kt`, `app/src/main/java/playground/app/tobeylin/weather/ui/theme/Shape.kt`, `app/src/main/java/playground/app/tobeylin/weather/ui/theme/Theme.kt`, `app/src/main/res/font/*.ttf`, `app/src/main/res/layout/activity_main.xml` (deleted)
  - Pre-commit: `./gradlew clean assembleDebug && ./gradlew test`

---

- [x] 9. Comprehensive Build Verification + Fix-Up

  **What to do**:
  - Run full verification sequence:
    1. `./gradlew clean` — start clean
    2. `./gradlew assembleDebug` — full project build (all 5 modules)
    3. `./gradlew test` — all unit tests pass (ExampleUnitTest)
    4. `./gradlew :core:model:assemble` — individual module verification
    5. `./gradlew :core:network:assembleDebug` — individual module verification
    6. `./gradlew :core:data:assembleDebug` — individual module verification
    7. `./gradlew :feature:weather:assembleDebug` — individual module verification
  - **If ANY build fails**, diagnose and fix:
    - Read error output carefully — common issues:
      - KSP version mismatch with bundled Kotlin → update `ksp` version in `libs.versions.toml`
      - Compose compiler version mismatch → update `kotlin-compose` plugin version
      - Missing manifest in library module → add `<manifest />` in `src/main/AndroidManifest.xml`
      - `R.font.*` not resolving → verify font files are in correct directory with correct names
      - Duplicate class errors → check for conflicting dependencies
      - `enableEdgeToEdge()` not found → verify `activity-compose` depends on `activity` transitively
    - Make minimal fixes — do NOT add scope beyond Phase 1
  - Run anti-scope-creep checks:
    - `grep -r "org.jetbrains.kotlin.android" .` — must return NO matches (forbidden plugin)
    - `grep -r "AppCompatActivity" app/src/` — must return NO matches
    - `grep -r "R.layout" app/src/main/java/` — must return NO matches
    - `grep -r "Room\|DataStore" */build.gradle.kts` — must return NO matches
    - `grep -r "NavHost\|navigation" */build.gradle.kts` — must return NO matches (except comments)
    - `ls app/src/main/res/values/themes.xml` — must exist (preserved)
    - `ls app/src/main/res/values-night/themes.xml` — must exist (preserved)
    - `ls app/src/main/res/values/strings.xml` — must exist (preserved)
  - Verify all acceptance criteria from tasks T1-T8
  - Run `./gradlew :app:lintDebug` (informational — fix only errors, not warnings)

  **Must NOT do**:
  - Do NOT add ANY new features, dependencies, or code beyond fixing build errors
  - Do NOT "improve" code quality (no reformatting, no optimization, no refactoring)
  - Do NOT suppress lint warnings with annotations
  - Do NOT add `@Suppress` annotations anywhere

  **Recommended Agent Profile**:
  - **Category**: `deep`
    - Reason: Diagnostic task requiring careful analysis of build errors, understanding dependency graphs, and making targeted fixes. May require multiple investigation-fix-verify cycles.
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 4 (solo — runs after ALL implementation tasks)
  - **Blocks**: F1, F2, F3, F4
  - **Blocked By**: T2, T3, T4, T5, T6, T7, T8

  **References**:

  **Pattern References**:
  - ALL files from T1-T8 — This task verifies and fixes everything. No specific file reference; the executor should use error messages to locate issues.

  **External References**:
  - KSP version compatibility: `https://github.com/google/ksp/releases` — version must match `{kotlin-version}-{ksp-build}`
  - Compose compiler compatibility: Version must align with the Kotlin version bundled by AGP

  **WHY Each Reference Matters**:
  - KSP releases: The #1 most common build failure in multi-module Compose+Hilt projects is KSP/Kotlin version mismatch
  - Compose compiler: If the compose plugin version doesn't match Kotlin, Compose annotation processing fails silently or with cryptic errors

  **Acceptance Criteria**:

  - [ ] `./gradlew clean assembleDebug` → "BUILD SUCCESSFUL"
  - [ ] `./gradlew test` → "BUILD SUCCESSFUL" (ExampleUnitTest passes)
  - [ ] `./gradlew :core:model:assemble` → "BUILD SUCCESSFUL"
  - [ ] `./gradlew :core:network:assembleDebug` → "BUILD SUCCESSFUL"
  - [ ] `./gradlew :core:data:assembleDebug` → "BUILD SUCCESSFUL"
  - [ ] `./gradlew :feature:weather:assembleDebug` → "BUILD SUCCESSFUL"
  - [ ] All anti-scope-creep checks pass (no forbidden patterns)
  - [ ] All preserved resources still exist

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Full clean build succeeds
    Tool: Bash
    Preconditions: All T1-T8 tasks complete
    Steps:
      1. Run `./gradlew clean assembleDebug 2>&1 | tee /tmp/build-output.txt | tail -20`
      2. Check output contains "BUILD SUCCESSFUL"
      3. Run `ls app/build/outputs/apk/debug/app-debug.apk`
      4. Verify APK exists
    Expected Result: Full clean build succeeds, APK produced
    Failure Indicators: "BUILD FAILED" with any error
    Evidence: .sisyphus/evidence/task-9-clean-build.txt

  Scenario: All unit tests pass
    Tool: Bash
    Preconditions: Build succeeds
    Steps:
      1. Run `./gradlew test 2>&1 | tail -10`
      2. Check output contains "BUILD SUCCESSFUL"
      3. Run `find . -name "TEST-*.xml" -path "*/test-results/*" | head -5`
      4. Verify test result XML exists
    Expected Result: All tests pass, test results generated
    Failure Indicators: "BUILD FAILED", test failures
    Evidence: .sisyphus/evidence/task-9-tests.txt

  Scenario: Anti-scope-creep checks all pass
    Tool: Bash
    Preconditions: Build verified
    Steps:
      1. Run `grep -r "org.jetbrains.kotlin.android" . --include="*.kts" --include="*.gradle" 2>/dev/null; echo "EXIT:$?"`
      2. Run `grep -r "AppCompatActivity" app/src/ 2>/dev/null; echo "EXIT:$?"`
      3. Run `grep -r "R.layout" app/src/main/java/ 2>/dev/null; echo "EXIT:$?"`
      4. Run `ls app/src/main/res/values/themes.xml app/src/main/res/values-night/themes.xml app/src/main/res/values/strings.xml 2>&1`
      5. Verify: steps 1-3 return exit 1 (no matches), step 4 shows all files exist
    Expected Result: Zero forbidden patterns, all preserved files intact
    Failure Indicators: Any grep match in steps 1-3, any "No such file" in step 4
    Evidence: .sisyphus/evidence/task-9-anti-creep.txt

  Scenario: Each module compiles independently
    Tool: Bash
    Preconditions: Full build succeeds
    Steps:
      1. Run `./gradlew :core:model:assemble 2>&1 | tail -3`
      2. Run `./gradlew :core:network:assembleDebug 2>&1 | tail -3`
      3. Run `./gradlew :core:data:assembleDebug 2>&1 | tail -3`
      4. Run `./gradlew :feature:weather:assembleDebug 2>&1 | tail -3`
      5. Verify each contains "BUILD SUCCESSFUL"
    Expected Result: All 4 new modules compile independently
    Failure Indicators: Any "BUILD FAILED"
    Evidence: .sisyphus/evidence/task-9-individual-modules.txt
  ```

  **Commit**: NO (fix-ups fold into the nearest prior commit group, or create a C-fix commit if needed)

---

## Final Verification Wave (MANDATORY — after ALL implementation tasks)

> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.

- [x] F1. **Plan Compliance Audit** — `oracle`
  Read the plan end-to-end. For each "Must Have": verify implementation exists (read file, run command). For each "Must NOT Have": search codebase for forbidden patterns — reject with file:line if found. Check evidence files exist in `.sisyphus/evidence/`. Compare deliverables against plan.
  Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT: APPROVE/REJECT`

- [x] F2. **Code Quality Review** — `unspecified-high`
  Run `./gradlew assembleDebug` + `./gradlew test` + `./gradlew :app:lintDebug`. Review all changed files for: `as any`/`@Suppress`, empty catches, hardcoded strings (except "Hello World"), unused imports. Check AI slop: excessive comments, over-abstraction, boilerplate beyond what's needed.
  Output: `Build [PASS/FAIL] | Test [PASS/FAIL] | Lint [PASS/FAIL] | Files [N clean/N issues] | VERDICT`

- [x] F3. **Real Manual QA** — `unspecified-high`
  Start from clean state. Execute EVERY QA scenario from EVERY task — follow exact steps, capture evidence. Run `./gradlew clean assembleDebug`. Verify each module compiles independently. Check anti-scope-creep criteria (AC-13 through AC-16). Save to `.sisyphus/evidence/final-qa/`.
  Output: `Scenarios [N/N pass] | Modules [5/5 compile] | Anti-creep [N/N pass] | VERDICT`

- [x] F4. **Scope Fidelity Check** — `deep`
  For each task: read "What to do", read actual files created/modified. Verify 1:1 — everything in spec was built (no missing), nothing beyond spec was built (no creep). Check "Must NOT do" compliance. Detect unaccounted files. Verify existing resources preserved (themes.xml, strings.xml, backup_rules, launcher icons).
  Output: `Tasks [N/N compliant] | Preserved [N/N] | Unaccounted [CLEAN/N files] | VERDICT`

---

## Commit Strategy

| Commit | Scope | Files | Pre-commit |
|--------|-------|-------|-----------|
| C1 | Gradle Infrastructure | `gradle/libs.versions.toml`, `build.gradle.kts` (root), `settings.gradle.kts`, `gradle.properties` | `./gradlew tasks` |
| C2 | Module Scaffolds | All 4 new module `build.gradle.kts` + `AndroidManifest.xml` + placeholder sources + `:app/build.gradle.kts` update | `./gradlew assembleDebug` |
| C3 | Compose Migration | `WeatherApplication.kt`, `MainActivity.kt`, `Theme.kt`, `Color.kt`, `Type.kt`, `Shape.kt`, font resources, `AndroidManifest.xml`, delete `activity_main.xml` | `./gradlew clean assembleDebug && ./gradlew test` |

---

## Success Criteria

### Verification Commands
```bash
./gradlew clean assembleDebug       # Expected: BUILD SUCCESSFUL
./gradlew test                       # Expected: BUILD SUCCESSFUL (ExampleUnitTest passes)
./gradlew :core:model:assemble       # Expected: BUILD SUCCESSFUL
./gradlew :core:network:assembleDebug # Expected: BUILD SUCCESSFUL
./gradlew :core:data:assembleDebug    # Expected: BUILD SUCCESSFUL
./gradlew :feature:weather:assembleDebug # Expected: BUILD SUCCESSFUL
```

### Final Checklist
- [ ] All 5 modules compile
- [ ] Hilt Application + AndroidEntryPoint wired
- [ ] Compose setContent showing Hello World in custom theme
- [ ] Material 3 theme with Stitch colors, Manrope/Inter fonts, custom shapes
- [ ] INTERNET permission in manifest
- [ ] No XML layouts remaining
- [ ] No AppCompatActivity references
- [ ] No `org.jetbrains.kotlin.android` plugin applied
- [ ] Existing ExampleUnitTest passes
- [ ] All preserved resources intact
