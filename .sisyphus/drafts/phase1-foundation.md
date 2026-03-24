# Draft: Phase 1 — Foundation

## Requirements (confirmed from docs/phase-plan.md)
- Deliverable: Compilable multi-module Compose shell displaying Hello World
- Acceptance: `./gradlew assembleDebug` passes for all 5 modules
- Weight: ~25% of total project

## Current State
- **MainActivity.kt**: `AppCompatActivity` + XML layout (`setContentView(R.layout.activity_main)`)
- **Build**: Single `:app` module, AGP 9.1.0, no Kotlin plugin (bundled), no Compose
- **Dependencies**: Basic AndroidX only (appcompat, constraintlayout, material, activity, core-ktx)
- **Resources**: XML layout `activity_main.xml`, XML themes, colors.xml with just black/white
- **Tests**: JUnit 4 test stub exists

## Phase 1 Tasks (from phase-plan.md)
1. Update `libs.versions.toml` — Compose BOM, Hilt, KSP, Retrofit, Kotlinx Serialization, etc.
2. Create 5 module directory structure + `build.gradle.kts` for each
3. Setup Hilt DI (`@HiltAndroidApp` + cross-module wiring)
4. Migrate `MainActivity` → `ComponentActivity` + Compose `setContent`
5. Create Material 3 Theme (from Stitch DESIGN.md)
6. Setup `BuildConfig.WEATHER_API_KEY` (from `local.properties`)
7. Add INTERNET permission, delete all XML layouts
8. Wire all modules so `assembleDebug` passes

## Design System Extracted (from Stitch)
### Colors (light theme)
- primary: #005f9c
- primaryContainer: #39a6ff
- onPrimary: #ecf3ff
- onPrimaryContainer: #002440
- secondary: #455f6b
- secondaryContainer: #cbe7f5
- onSecondary: #e4f5ff
- onSecondaryContainer: #3c5561
- tertiary: #7f5200
- tertiaryContainer: #feb64c
- onTertiary: #fff0e2
- onTertiaryContainer: #583700
- surface: #f5f7f9
- surfaceContainerLowest: #ffffff
- surfaceContainerLow: #eef1f3
- surfaceContainer: #e5e9eb
- surfaceContainerHigh: #dfe3e6
- surfaceContainerHighest: #d9dde0
- onSurface: #2c2f31
- onSurfaceVariant: #595c5e
- surfaceVariant: #d9dde0
- background: #f5f7f9
- onBackground: #2c2f31
- error: #b31b25
- errorContainer: #fb5151
- onError: #ffefee
- onErrorContainer: #570008
- outline: #747779
- outlineVariant: #abadaf
- inverseSurface: #0b0f10
- inverseOnSurface: #9a9d9f
- inversePrimary: #289ef8
- surfaceTint: #005f9c

### Typography
- Display/Headline: Manrope (bold/extrabold)
- Body/Label: Inter (regular/medium/semibold)

### Shapes
- Default corner radius: 1rem (16dp)
- Large: 2rem (32dp)
- XL: 3rem (48dp)
- Full: 9999px (pill)

## Technical Warnings (from phase-plan.md)
- AGP 9.1 bundles Kotlin → DO NOT apply `org.jetbrains.kotlin.android`
- Compose compiler via `org.jetbrains.kotlin.plugin.compose` (NOT old `kotlinCompilerExtensionVersion`)
- `compileSdk` uses `release(36) { minorApiLevel = 1 }` syntax
- Hilt ≥2.59 + KSP version must match bundled Kotlin version
- `kotlinOptions { jvmTarget }` → AGP 9.x uses `compileOptions` with Java 11

## Module Structure
```
:app                  → entry point, Hilt init, navigation, theme
:feature:weather      → city list + weather detail screens + ViewModel
:core:data            → repository + DTO→Model mapping
:core:model           → shared domain models (pure Kotlin)
:core:network         → Retrofit setup, API interface, DTOs
```

Dependency direction: `:app` → `:feature:weather` → `:core:data` → `:core:network`
`:core:model` is shared by all modules.

## Test Strategy for Phase 1
- No unit tests in Phase 1 (foundation only)
- Acceptance: `./gradlew assembleDebug` all modules pass
- Agent-executed QA: Run build command, verify APK output

## Open Research (background agents dispatched)
- [ ] Exact version numbers for Compose BOM, Hilt, KSP, Retrofit, OkHttp, Kotlinx Serialization
- [ ] Material 3 theme setup patterns (Color.kt, Type.kt, Theme.kt)
- [ ] Multi-module Gradle setup patterns from Now in Android
