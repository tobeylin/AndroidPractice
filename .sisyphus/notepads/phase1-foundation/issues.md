# Phase 1 Issues

## 2026-03-24 Session Start

### Known Risks
1. **KSP version mismatch** — HIGHEST risk. Executor must run `./gradlew buildEnvironment` first and use the exact Kotlin version to find matching KSP release.
2. **Compose compiler plugin version** — Must match AGP's bundled Kotlin. Cannot use arbitrary version.
3. **Empty library modules** — AGP 9.x requires `AndroidManifest.xml` even for empty Android library modules. Must create even if blank.
4. **`enableEdgeToEdge()` availability** — Comes from `androidx.activity` artifact; `activity-compose` pulls it in transitively.
5. **Font file naming** — Android res/font names must be lowercase letters, digits, underscores only. Google Fonts zip contains e.g. `Manrope-Bold.ttf` → rename to `manrope_bold.ttf`.

### T1 Special Warning
- If the `kotlin.jvm` plugin is NOT declared in version catalog by T1, T2 will fail.
- T1 executor must ensure `kotlin-jvm` plugin alias IS added for use in `:core:model`.

### Forbidden Patterns
- `org.jetbrains.kotlin.android` plugin — causes conflict with AGP's bundled Kotlin
- `kotlinCompilerExtensionVersion` — old compose plugin style, NOT for AGP 9.x
- `kapt` — replaced by KSP for Hilt; do NOT use kapt plugin

## 2026-03-24 Code Quality Review (F2)

### BLOCKER Found
- **`libs.versions.toml:35`** — `retrofit-kotlinx-serialization` uses inline `version = "1.0.0"` instead of `version.ref`. Must extract to `[versions]` section for consistency with project convention (all versions centralized).

### INFO
- **`Theme.kt`** — Only light color scheme defined. AGENTS.md recommends dark theme support. Acceptable for Phase 1 scaffolding but should be addressed in future phases.

### Clean Checks (all passed)
- No `TODO/FIXME/HACK/XXX` markers in source
- No `@Suppress` annotations
- No wildcard imports in non-test files
- No `println`/`Log.` debug statements
- No `org.jetbrains.kotlin.android` plugin
- No `kapt` usage (correctly uses KSP)
- Build passes, tests pass
