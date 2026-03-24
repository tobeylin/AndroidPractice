# Humidity & Wind Cards for WeatherHomeScreen

## TL;DR

> **Quick Summary**: Add humidity and wind speed metric cards to WeatherHomeScreen, matching the Stitch design. Extends the data pipeline to include wind direction (deg), adds weather conversion utilities (dew point, compass direction, m/s→km/h), and builds a reusable `WeatherMetricCard` composable.
> 
> **Deliverables**:
> - Extended `NetworkWind` DTO with `deg` field
> - Extended `CurrentWeather` model with `windDeg`
> - New `WeatherConversions.kt` utility functions (dew point, compass, unit conversion)
> - Extended `WeatherUiState.Success` with humidity/wind display data
> - New `WeatherMetricCard.kt` reusable composable
> - Updated `WeatherHomeScreen` with 2-column card layout
> - Updated unit tests covering all new logic
> 
> **Estimated Effort**: Short (4 commits, ~2 hours)
> **Parallel Execution**: YES - 2 waves (Wave 1: data + utils, Wave 2: UI state + composables)
> **Critical Path**: Task 1 → Task 3 → Task 4

---

## Context

### Original Request
Add humidity and wind speed cards to WeatherHomeScreen matching the Stitch design. Focus only on these two cards. Must not break existing implementation.

### Interview Summary
**Key Discussions**:
- Data layer is already complete for humidity/windSpeed — only `wind.deg` is missing from DTO
- User chose full Stitch fidelity: approximate dew point via Magnus formula, parse `wind.deg` for compass direction
- User chose reusable `WeatherMetricCard` composable over individual cards
- Wind speed conversion from m/s → km/h confirmed
- Tests to be updated alongside implementation

**Research Findings**:
- `CurrentWeather` model already has `humidity: Int` and `windSpeed: Double` — no new model fields except `windDeg: Int?`
- `NetworkWind` only has `speed` — `deg` needs adding as nullable with default (API doesn't always provide it)
- `FakeWeatherRepository` has `humidity = 70`, `windSpeed = 3.5` — needs `windDeg` added
- Material Icons Extended already available (`WaterDrop`, `Air` icons)
- Stitch HTML confirms card layout: `surfaceContainerLowest` background, `rounded-lg` (32dp), icon+uppercase label header, large bold value, small subtitle

### Metis Review
**Identified Gaps** (addressed):
- `NetworkWind.deg` must be `Int? = null` (nullable) — API doesn't guarantee this field
- `calculateDewPoint` must guard against `humidity = 0` (causes `ln(0)` = -infinity) — use `coerceIn(1, 100)`
- Commit 3 (UI state data class change) cascades to 5+ callsites — must be atomic, use `ast_grep_search` to find all `WeatherUiState.Success(` before editing
- `WeatherConversions.kt` functions should be `internal` (matching `WeatherIconMapper.kt` pattern)
- Wind direction subtitle should gracefully handle null `windDeg` — show speed without compass direction

---

## Work Objectives

### Core Objective
Add humidity and wind metric cards below the existing TodayWeatherCard in WeatherHomeScreen, with full data pipeline from network DTO through ViewModel to UI composables.

### Concrete Deliverables
- `core/network/.../NetworkCurrentWeatherResponse.kt` — `NetworkWind.deg: Int? = null` field
- `core/model/.../CurrentWeather.kt` — `windDeg: Int? = null` field
- `core/data/.../WeatherMapper.kt` — `windDeg = wind.deg` mapping
- `feature/weather/.../WeatherConversions.kt` — 3 pure conversion functions
- `feature/weather/.../WeatherUiState.kt` — 4 new fields on `Success`
- `feature/weather/.../WeatherViewModel.kt` — map new fields with conversions
- `feature/weather/.../WeatherMetricCard.kt` — reusable card composable
- `feature/weather/.../WeatherHomeScreen.kt` — 2-column card layout
- Updated tests across all modified modules

### Definition of Done
- [ ] `./gradlew build` passes with 0 errors
- [ ] `./gradlew test` passes with 0 failures
- [ ] All existing tests still pass (no regressions)
- [ ] Humidity and Wind cards visible in `@Preview` composables
- [ ] No new library dependencies added

### Must Have
- Humidity card showing percentage value with dew point subtitle
- Wind card showing km/h value with compass direction subtitle
- Reusable `WeatherMetricCard` composable
- Null-safe handling of missing wind direction data
- Unit tests for all conversion functions
- Updated ViewModel tests asserting new fields

### Must NOT Have (Guardrails)
- Do NOT touch `TodayWeatherCard.kt` internal implementation — only update its preview constructor calls
- Do NOT add Sunrise/Sunset card, 5-Day Forecast, or any other UI sections
- Do NOT add slot APIs, generic type parameters, or animations to `WeatherMetricCard`
- Do NOT add new library dependencies
- Do NOT add computed fields (`dewPoint`, `windSpeedKmh`) to the `CurrentWeather` domain model — these are UI-layer display concerns
- Do NOT modify `CurrentWeather` beyond adding `windDeg: Int? = null`
- Do NOT use hardcoded colors or text styles — always use `MaterialTheme.colorScheme.*` and `MaterialTheme.typography.*`

---

## Verification Strategy

> **ZERO HUMAN INTERVENTION** — ALL verification is agent-executed. No exceptions.

### Test Decision
- **Infrastructure exists**: YES
- **Automated tests**: YES (tests-after, integrated with implementation commits)
- **Framework**: JUnit 4 + kotlinx-coroutines-test (existing)

### QA Policy
Every task includes `./gradlew` test commands as primary verification. Preview compilation success serves as UI smoke test. Evidence saved to `.sisyphus/evidence/task-{N}-{scenario-slug}.{ext}`.

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately — data layer + pure functions):
├── Task 1: Extend NetworkWind DTO + CurrentWeather model + mapper [quick]
└── Task 2: WeatherConversions utility functions + tests (TDD) [quick]

Wave 2 (After Wave 1 — UI state + composables):
├── Task 3: Extend WeatherUiState + ViewModel + update ALL callsites [unspecified-high]
└── Task 4: WeatherMetricCard composable + WeatherHomeScreen layout [visual-engineering]
    (depends on Task 3)

Wave FINAL (After ALL tasks):
├── F1: Plan compliance audit [oracle]
├── F2: Code quality review [unspecified-high]
├── F3: Full build verification [unspecified-high]
└── F4: Scope fidelity check [deep]
-> Present results -> Get explicit user okay

Critical Path: Task 1 → Task 3 → Task 4 → F1-F4 → user okay
Parallel Speedup: Tasks 1 & 2 run in parallel; Task 4 follows Task 3
Max Concurrent: 2 (Wave 1)
```

### Dependency Matrix

| Task | Depends On | Blocks |
|------|-----------|--------|
| 1    | —         | 3      |
| 2    | —         | 3      |
| 3    | 1, 2      | 4      |
| 4    | 3         | F1-F4  |

### Agent Dispatch Summary

- **Wave 1**: **2** — T1 → `quick`, T2 → `quick`
- **Wave 2**: **2** — T3 → `unspecified-high`, T4 → `visual-engineering`
- **FINAL**: **4** — F1 → `oracle`, F2 → `unspecified-high`, F3 → `unspecified-high`, F4 → `deep`

---

## TODOs

- [ ] 1. Add wind degree field to network DTO and domain model

  **What to do**:
  - Add `@SerialName("deg") val deg: Int? = null` to `NetworkWind` data class in `NetworkCurrentWeatherResponse.kt`
  - Add `val windDeg: Int? = null` to `CurrentWeather` data class in `CurrentWeather.kt` (append after `windSpeed`)
  - Add `windDeg = wind.deg` mapping to `WeatherMapper.kt` `asExternalModel()` function
  - Update `FakeWeatherRepository.kt`: add `windDeg = 180` to `currentWeatherResult`
  - Update `FakeWeatherApi.kt`: add `deg = 180` to `NetworkWind` constructor
  - Update `WeatherMapperTest.kt`: add `deg = 180` to test `NetworkWind` constructor, assert `windDeg == 180`; add new test case with `deg = null` asserting `windDeg == null`
  - Update `NetworkCurrentWeatherResponseTest.kt`: verify JSON with `"deg":180` parses correctly; verify existing JSON without `deg` still parses (null default)

  **Must NOT do**:
  - Do NOT add computed fields to `CurrentWeather` (no `dewPoint`, `windSpeedKmh`)
  - Do NOT modify any field other than adding `windDeg`
  - Do NOT make `deg` non-nullable — the API may omit this field

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Small, well-defined data class additions across ~7 files with clear patterns to follow
  - **Skills**: []
    - No external skills needed — standard Kotlin data class modifications

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Task 2)
  - **Parallel Group**: Wave 1 (with Task 2)
  - **Blocks**: Task 3
  - **Blocked By**: None (can start immediately)

  **References**:

  **Pattern References** (existing code to follow):
  - `core/network/src/main/java/playground/app/tobeylin/weather/core/network/model/NetworkCurrentWeatherResponse.kt:39-42` — `NetworkWind` data class (add `deg` field here following `@SerialName` pattern)
  - `core/model/src/main/kotlin/playground/app/tobeylin/weather/core/model/CurrentWeather.kt:3-14` — `CurrentWeather` data class (add `windDeg` after line 9)
  - `core/data/src/main/java/playground/app/tobeylin/weather/core/data/mapper/WeatherMapper.kt:6-17` — mapper function (add `windDeg = wind.deg` mapping)

  **Test References** (testing patterns to follow):
  - `core/data/src/test/java/playground/app/tobeylin/weather/core/data/mapper/WeatherMapperTest.kt` — existing mapper test structure (follow same assertion pattern)
  - `core/network/src/test/java/playground/app/tobeylin/weather/core/network/model/NetworkCurrentWeatherResponseTest.kt` — existing JSON deserialization test (add `deg` to JSON string)

  **Fake References** (test doubles to update):
  - `core/data/src/main/java/playground/app/tobeylin/weather/core/data/FakeWeatherRepository.kt:9-20` — add `windDeg = 180` to `currentWeatherResult` constructor
  - `core/network/src/main/java/playground/app/tobeylin/weather/core/network/FakeWeatherApi.kt` — add `deg = 180` to `NetworkWind` constructor

  **Acceptance Criteria**:

  - [ ] `NetworkWind` has `val deg: Int? = null` field with `@SerialName("deg")`
  - [ ] `CurrentWeather` has `val windDeg: Int? = null` field
  - [ ] `WeatherMapper.asExternalModel()` maps `windDeg = wind.deg`
  - [ ] Existing JSON deserialization tests still pass (backward compatible)
  - [ ] New test: JSON with `"deg":180` parses to `NetworkWind(speed=X, deg=180)`
  - [ ] New test: JSON without `"deg"` parses to `NetworkWind(speed=X, deg=null)`

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Data layer tests pass with new wind degree field
    Tool: Bash
    Preconditions: All file modifications complete
    Steps:
      1. Run `./gradlew :core:network:testDebugUnitTest`
      2. Assert exit code 0
      3. Run `./gradlew :core:data:testDebugUnitTest`
      4. Assert exit code 0
    Expected Result: Both modules' tests pass with 0 failures
    Failure Indicators: Any test failure mentioning NetworkWind, CurrentWeather, or WeatherMapper
    Evidence: .sisyphus/evidence/task-1-data-layer-tests.txt

  Scenario: Null deg field backward compatibility
    Tool: Bash
    Preconditions: NetworkCurrentWeatherResponseTest updated
    Steps:
      1. Run `./gradlew :core:network:testDebugUnitTest --tests "*.NetworkCurrentWeatherResponseTest"`
      2. Verify test for JSON without `deg` field passes (null default works)
    Expected Result: Test passes — existing API responses without `deg` still deserialize correctly
    Failure Indicators: kotlinx.serialization.MissingFieldException for `deg`
    Evidence: .sisyphus/evidence/task-1-null-deg-compat.txt
  ```

  **Commit**: YES (Commit 1)
  - Message: `feat(data): add wind degree field to network DTO and domain model`
  - Files: `NetworkCurrentWeatherResponse.kt`, `CurrentWeather.kt`, `WeatherMapper.kt`, `FakeWeatherRepository.kt`, `FakeWeatherApi.kt`, `WeatherMapperTest.kt`, `NetworkCurrentWeatherResponseTest.kt`
  - Pre-commit: `./gradlew :core:network:testDebugUnitTest :core:data:testDebugUnitTest`

- [ ] 2. Add weather conversion utility functions with tests (TDD)

  **What to do**:
  - **Write tests FIRST** in `feature/weather/src/test/java/playground/app/tobeylin/weather/feature/weather/WeatherConversionsTest.kt`:
    - `metersPerSecondToKmh`: test `3.5 → 12.6`, `0.0 → 0.0`, `100.0 → 360.0`
    - `windDegreesToCompass`: test all 16 cardinal directions: `0→"N"`, `22.5→"NNE"`, `45→"NE"`, `67.5→"ENE"`, `90→"E"`, `112.5→"ESE"`, `135→"SE"`, `157.5→"SSE"`, `180→"S"`, `202.5→"SSW"`, `225→"SW"`, `247.5→"WSW"`, `270→"W"`, `292.5→"WNW"`, `315→"NW"`, `337.5→"NNW"`, boundary at `348.75→"N"`, `null→""`
    - `calculateDewPoint`: test `(28.5, 70) → ≈22.6 (±0.5)`, `(0.0, 50) → ≈-9.3 (±0.5)`, edge case `(25.0, 0) → guarded` (humidity coerced to 1)
  - **Then implement** in `feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/WeatherConversions.kt`:
    - `internal fun metersPerSecondToKmh(mps: Double): Double = mps * 3.6`
    - `internal fun windDegreesToCompass(degrees: Int?): String` — 16 compass directions using array index `((deg + 11.25) / 22.5).toInt() % 16`, return `""` if null
    - `internal fun calculateDewPoint(tempCelsius: Double, humidityPercent: Int): Double` — Magnus formula with `a = 17.27, b = 237.7`, guard `humidityPercent.coerceIn(1, 100)`

  **Must NOT do**:
  - Do NOT put conversion logic inside ViewModel — keep as pure top-level functions
  - Do NOT use external math libraries — standard Kotlin `kotlin.math.ln` is sufficient
  - Do NOT make functions `public` — use `internal` visibility

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Two small files (test + implementation) with pure Kotlin math functions, no Android dependencies
  - **Skills**: []
    - No external skills needed — pure Kotlin math

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Task 1)
  - **Parallel Group**: Wave 1 (with Task 1)
  - **Blocks**: Task 3
  - **Blocked By**: None (can start immediately)

  **References**:

  **Pattern References** (existing code to follow):
  - `feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/WeatherIconMapper.kt:13-22` — pattern for `internal fun` top-level utility functions in feature module (follow same file structure, visibility, naming style)

  **Test References** (testing patterns to follow):
  - `feature/weather/src/test/java/playground/app/tobeylin/weather/feature/weather/WeatherViewModelTest.kt:23-51` — JUnit 4 test class structure, `@Test` annotation, `assertEquals` with delta for doubles

  **External References**:
  - Magnus formula: `Td = (b × α) / (a − α)` where `α = (a × T) / (b + T) + ln(RH/100)`, constants `a = 17.27, b = 237.7`
  - Compass directions array: `["N","NNE","NE","ENE","E","ESE","SE","SSE","S","SSW","SW","WSW","W","WNW","NW","NNW"]`

  **Acceptance Criteria**:

  - [ ] `WeatherConversionsTest.kt` created with ≥10 test methods covering all 3 functions
  - [ ] `WeatherConversions.kt` created with 3 `internal fun` functions
  - [ ] `metersPerSecondToKmh(3.5)` returns `12.6` (±0.01)
  - [ ] `windDegreesToCompass(180)` returns `"S"`, `windDegreesToCompass(null)` returns `""`
  - [ ] `calculateDewPoint(28.5, 70)` returns `≈22.6` (±0.5)
  - [ ] `calculateDewPoint(25.0, 0)` does NOT throw (humidity coerced to 1)

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: All conversion function tests pass
    Tool: Bash
    Preconditions: Both WeatherConversions.kt and WeatherConversionsTest.kt created
    Steps:
      1. Run `./gradlew :feature:weather:testDebugUnitTest --tests "*.WeatherConversionsTest"`
      2. Assert exit code 0
      3. Verify output shows ≥10 tests passed
    Expected Result: All tests PASS with 0 failures
    Failure Indicators: Any assertion error in conversion logic
    Evidence: .sisyphus/evidence/task-2-conversions-tests.txt

  Scenario: Edge case — humidity 0% does not crash dew point calculation
    Tool: Bash
    Preconditions: WeatherConversionsTest includes edge case test
    Steps:
      1. Run `./gradlew :feature:weather:testDebugUnitTest --tests "*.WeatherConversionsTest.calculateDewPoint_humidity_zero_does_not_crash"`
      2. Assert exit code 0 (test exists and passes)
    Expected Result: Function returns a finite Double value, no exception thrown
    Failure Indicators: ArithmeticException, NaN, or -Infinity in output
    Evidence: .sisyphus/evidence/task-2-dewpoint-edge-case.txt
  ```

  **Commit**: YES (Commit 2)
  - Message: `feat(weather): add weather conversion utilities (dew point, compass, km/h)`
  - Files: `WeatherConversions.kt`, `WeatherConversionsTest.kt`
  - Pre-commit: `./gradlew :feature:weather:testDebugUnitTest`

- [ ] 3. Extend WeatherUiState and ViewModel with humidity and wind data

  **What to do**:
  - **FIRST**: Use `ast_grep_search` with pattern `WeatherUiState.Success(` in Kotlin to find ALL constructor callsites — this is CRITICAL to avoid compilation errors
  - Add 4 new fields to `WeatherUiState.Success` in `WeatherUiState.kt`:
    - `val humidity: Int` — raw humidity percentage
    - `val windSpeedKmh: Double` — converted from m/s
    - `val windDirection: String` — compass direction string (empty if deg unavailable)
    - `val dewPoint: Double` — approximated from Magnus formula
  - Update `WeatherViewModel.kt` `loadWeather()` to compute and map:
    - `humidity = weather.humidity`
    - `windSpeedKmh = metersPerSecondToKmh(weather.windSpeed)`
    - `windDirection = windDegreesToCompass(weather.windDeg)`
    - `dewPoint = calculateDewPoint(weather.temperature, weather.humidity)`
    - Import `WeatherConversions` functions
  - Update **EVERY** `WeatherUiState.Success(` constructor call — these are callsites that will break:
    - `WeatherViewModel.kt:32-40` — production mapping (add computed values)
    - `WeatherViewModelTest.kt:44-50` — test assertion (add assertions for new fields)
    - `WeatherHomeScreen.kt:74-82` — preview (add sample values: `humidity = 64, windSpeedKmh = 12.0, windDirection = "NW", dewPoint = 14.0`)
    - `TodayWeatherCard.kt:147-155` — preview (add same sample values)
    - `TodayWeatherCard.kt:161-177` — loading preview does NOT construct `Success`, no change needed
  - Update `WeatherViewModelTest.kt` `when_weather_loads_successfully_state_is_success` test:
    - Assert `success.humidity == 70` (from FakeWeatherRepository)
    - Assert `success.windSpeedKmh ≈ 12.6` (3.5 × 3.6, delta 0.1)
    - Assert `success.windDirection == "S"` (FakeWeatherRepository has `windDeg = 180` from Task 1)
    - Assert `success.dewPoint ≈ 22.6` (from temp=28.5 + humidity=70, delta 0.5)

  **Must NOT do**:
  - Do NOT modify `TodayWeatherCard.kt` composable function body — only update its `@Preview` constructor calls
  - Do NOT add conditional display logic here — that's Task 4's concern
  - Do NOT add fields beyond the 4 specified

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: This is the riskiest task — data class change cascading to 5+ files across production and test code. Requires careful coordination and `ast_grep_search` to find all callsites.
  - **Skills**: []
    - No external skills needed — but agent MUST use `ast_grep_search` before editing

  **Parallelization**:
  - **Can Run In Parallel**: NO (depends on Tasks 1 and 2)
  - **Parallel Group**: Wave 2 (sequential)
  - **Blocks**: Task 4
  - **Blocked By**: Task 1 (windDeg field), Task 2 (conversion functions)

  **References**:

  **Pattern References** (existing code to follow):
  - `feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/WeatherUiState.kt:3-15` — current `Success` data class (add 4 fields after `iconCode`)
  - `feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/WeatherViewModel.kt:27-45` — current `loadWeather()` mapping logic (add conversion calls)

  **API/Type References** (contracts to implement against):
  - `core/model/src/main/kotlin/playground/app/tobeylin/weather/core/model/CurrentWeather.kt:3-14` — source model fields: `humidity`, `windSpeed`, `windDeg` (from Task 1)
  - `feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/WeatherConversions.kt` — 3 conversion functions to import (from Task 2)

  **Test References** (testing patterns to follow):
  - `feature/weather/src/test/java/playground/app/tobeylin/weather/feature/weather/WeatherViewModelTest.kt:38-51` — existing Success state test (add new field assertions following same `assertEquals` pattern)

  **Callsite References** (CRITICAL — all places constructing `WeatherUiState.Success`):
  - `WeatherViewModel.kt:32-40` — production code
  - `WeatherViewModelTest.kt:44-50` — test assertion reading
  - `WeatherHomeScreen.kt:74-82` — preview composable
  - `TodayWeatherCard.kt:147-155` — preview composable

  **Acceptance Criteria**:

  - [ ] `WeatherUiState.Success` has 4 new fields: `humidity`, `windSpeedKmh`, `windDirection`, `dewPoint`
  - [ ] `WeatherViewModel` correctly computes all 4 values using conversion functions
  - [ ] ALL `WeatherUiState.Success(` constructor calls compile (no missing arguments)
  - [ ] `WeatherViewModelTest` asserts: `humidity == 70`, `windSpeedKmh ≈ 12.6`, `windDirection == "S"`, `dewPoint ≈ 22.6`
  - [ ] Project compiles: `./gradlew :feature:weather:compileDebugKotlin` succeeds

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: ViewModel correctly maps humidity and wind data
    Tool: Bash
    Preconditions: WeatherUiState, ViewModel, and test all updated
    Steps:
      1. Run `./gradlew :feature:weather:testDebugUnitTest --tests "*.WeatherViewModelTest"`
      2. Assert exit code 0
      3. Verify all 3 existing tests still pass + new assertions pass
    Expected Result: 3 tests PASS with 0 failures, including new humidity/wind assertions
    Failure Indicators: AssertionError on humidity, windSpeedKmh, windDirection, or dewPoint values
    Evidence: .sisyphus/evidence/task-3-viewmodel-tests.txt

  Scenario: All preview constructors compile with new fields
    Tool: Bash
    Preconditions: All WeatherUiState.Success constructor calls updated
    Steps:
      1. Run `./gradlew :feature:weather:compileDebugKotlin`
      2. Assert exit code 0
      3. Verify no "No value passed for parameter" errors
    Expected Result: Feature module compiles cleanly
    Failure Indicators: Kotlin compiler error mentioning missing parameters in Success constructor
    Evidence: .sisyphus/evidence/task-3-compile-check.txt
  ```

  **Commit**: YES (Commit 3)
  - Message: `feat(weather): extend UI state with humidity and wind data`
  - Files: `WeatherUiState.kt`, `WeatherViewModel.kt`, `WeatherViewModelTest.kt`, `WeatherHomeScreen.kt` (preview), `TodayWeatherCard.kt` (preview)
  - Pre-commit: `./gradlew :feature:weather:testDebugUnitTest`

- [ ] 4. Add WeatherMetricCard composable and humidity/wind cards to home screen

  **What to do**:
  - Create `feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/WeatherMetricCard.kt`:
    - Composable signature: `fun WeatherMetricCard(icon: ImageVector, label: String, value: String, subtitle: String, modifier: Modifier = Modifier)`
    - Card layout matching Stitch design:
      - `Surface` with `color = MaterialTheme.colorScheme.surfaceContainerLowest`, `shape = MaterialTheme.shapes.medium` (16dp rounded — matching Stitch `rounded-lg` for inner cards, which is 1rem = 16dp in the bento grid context)
      - `Column` with `padding(24.dp)` (matches Stitch p-6)
      - Header row: `Row` with `Icon` (24dp, `tint = MaterialTheme.colorScheme.primary`) + `Text` (label, `style = labelSmall`, `color = onSurfaceVariant`, uppercase via `.uppercase()`, `letterSpacing = 1.sp`)
      - `Spacer(modifier = Modifier.height(16.dp))` between header and value
      - Value: `Text` (value string, `style = headlineLarge`, `color = onSurface`) — Manrope Bold 32sp matches Stitch `text-3xl`
      - Subtitle: `Text` (subtitle, `style = bodySmall`, `color = onSurfaceVariant`, `Modifier.padding(top = 4.dp)`)
    - Two `@Preview` functions at bottom (private): one for Humidity card, one for Wind card
  - Update `WeatherHomeScreen.kt` Success branch:
    - After `TodayWeatherCard(...)`, add `Spacer(modifier = Modifier.height(16.dp))`
    - Add `Row(horizontalArrangement = Arrangement.spacedBy(12.dp))` containing:
      - Humidity card: `WeatherMetricCard(icon = Icons.Filled.WaterDrop, label = "Humidity", value = "${state.humidity}%", subtitle = "Dew point is ${state.dewPoint.toInt()}°", modifier = Modifier.weight(1f))`
      - Wind card: `WeatherMetricCard(icon = Icons.Filled.Air, label = "Wind", value = "${state.windSpeedKmh.toInt()} km/h", subtitle = if (state.windDirection.isNotEmpty()) "${state.windDirection} Direction" else "", modifier = Modifier.weight(1f))`
    - Handle empty wind direction gracefully in subtitle

  **Must NOT do**:
  - Do NOT add slot APIs, `@Composable` content lambdas, or generic type parameters to `WeatherMetricCard`
  - Do NOT add animations or transitions
  - Do NOT add Sunrise/Sunset card or 5-Day Forecast
  - Do NOT use hardcoded `Color(...)` values — only `MaterialTheme.colorScheme.*`
  - Do NOT use hardcoded `TextStyle(...)` — only `MaterialTheme.typography.*`
  - Do NOT add `testTag` modifiers (no instrumented UI tests in scope)

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: UI composable work requiring Stitch design fidelity — card layout, spacing, typography, colors
  - **Skills**: []
    - No external skills needed — standard Jetpack Compose Material 3

  **Parallelization**:
  - **Can Run In Parallel**: NO (depends on Task 3)
  - **Parallel Group**: Wave 2 (after Task 3)
  - **Blocks**: F1-F4
  - **Blocked By**: Task 3 (WeatherUiState fields must exist)

  **References**:

  **Pattern References** (existing code to follow):
  - `feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/TodayWeatherCard.kt:31-116` — composable structure pattern: `@Composable fun`, Modifier parameter, Material theme tokens, `@Preview` at bottom
  - `feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/TodayWeatherCard.kt:110-113` — `Row(horizontalArrangement = Arrangement.spacedBy(12.dp))` pattern for side-by-side elements
  - `feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/WeatherHomeScreen.kt:29-67` — screen Column layout structure (add cards within Success branch after TodayWeatherCard)

  **Design References** (Stitch visual spec):
  - `docs/stitch_playgroundweather/today_s_weather_5_day_forecast_updated_location/screen.png` — visual reference showing 2-column card layout below hero card
  - `docs/stitch_playgroundweather/today_s_weather_5_day_forecast_updated_location/code.html:137-159` — exact HTML/CSS for Humidity and Wind widgets: `bg-surface-container-lowest rounded-lg p-6`, icon+label header, bold 3xl value, xs subtitle

  **Theme References** (tokens to use):
  - `app/src/main/java/playground/app/tobeylin/weather/ui/theme/Color.kt:25` — `SurfaceContainerLowest = #FFFFFF` → `MaterialTheme.colorScheme.surfaceContainerLowest`
  - `app/src/main/java/playground/app/tobeylin/weather/ui/theme/Shape.kt:9` — `medium = RoundedCornerShape(16.dp)` → `MaterialTheme.shapes.medium`
  - `app/src/main/java/playground/app/tobeylin/weather/ui/theme/Type.kt:26` — `headlineLarge = Manrope Bold 32sp` → `MaterialTheme.typography.headlineLarge`
  - `app/src/main/java/playground/app/tobeylin/weather/ui/theme/Type.kt:37` — `labelSmall = Inter Medium 11sp` → `MaterialTheme.typography.labelSmall`

  **Icon References**:
  - `Icons.Filled.WaterDrop` — humidity icon (already used in `WeatherIconMapper.kt:7` import)
  - `Icons.Filled.Air` — wind icon (available via `compose-material-icons-extended` dependency)

  **Acceptance Criteria**:

  - [ ] `WeatherMetricCard.kt` created with reusable composable accepting `icon`, `label`, `value`, `subtitle`, `modifier`
  - [ ] `WeatherHomeScreen.kt` renders 2 cards in a `Row` below `TodayWeatherCard` in Success state
  - [ ] Humidity card shows: humidity icon, "HUMIDITY" label, "XX%" value, "Dew point is XX°" subtitle
  - [ ] Wind card shows: air icon, "WIND" label, "XX km/h" value, "XX Direction" subtitle (or empty if no direction)
  - [ ] All colors use `MaterialTheme.colorScheme.*`, all text styles use `MaterialTheme.typography.*`
  - [ ] Two `@Preview` functions exist in `WeatherMetricCard.kt`
  - [ ] `./gradlew build` succeeds with 0 errors

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Full project builds successfully with new cards
    Tool: Bash
    Preconditions: All 4 tasks complete
    Steps:
      1. Run `./gradlew clean build`
      2. Assert exit code 0
      3. Verify no warnings related to unused imports or deprecations in new files
    Expected Result: BUILD SUCCESSFUL with 0 errors
    Failure Indicators: Compilation error in WeatherMetricCard.kt or WeatherHomeScreen.kt
    Evidence: .sisyphus/evidence/task-4-full-build.txt

  Scenario: All module tests pass after UI changes
    Tool: Bash
    Preconditions: All files updated
    Steps:
      1. Run `./gradlew test`
      2. Assert exit code 0
      3. Verify total test count matches or exceeds previous count
    Expected Result: All tests PASS across all modules
    Failure Indicators: Any test failure in any module
    Evidence: .sisyphus/evidence/task-4-all-tests.txt

  Scenario: No hardcoded colors or styles in new composables
    Tool: ast_grep_search
    Preconditions: WeatherMetricCard.kt created
    Steps:
      1. Search for `Color(` pattern in WeatherMetricCard.kt — expect 0 matches
      2. Search for `TextStyle(` pattern in WeatherMetricCard.kt — expect 0 matches
      3. Verify all color usages are `MaterialTheme.colorScheme.*`
      4. Verify all text style usages are `MaterialTheme.typography.*`
    Expected Result: Zero hardcoded colors or text styles
    Failure Indicators: Any `Color(0x...)` or `TextStyle(fontFamily=...)` in the new file
    Evidence: .sisyphus/evidence/task-4-no-hardcoded-styles.txt
  ```

  **Commit**: YES (Commit 4)
  - Message: `feat(weather): add humidity and wind metric cards to home screen`
  - Files: `WeatherMetricCard.kt` (new), `WeatherHomeScreen.kt`
  - Pre-commit: `./gradlew build`

---

## Final Verification Wave (MANDATORY — after ALL implementation tasks)

> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.

- [ ] F1. **Plan Compliance Audit** — `oracle`
  Read the plan end-to-end. For each "Must Have": verify implementation exists (read file, run tests). For each "Must NOT Have": search codebase for forbidden patterns — reject with file:line if found. Check all 4 commits landed. Compare deliverables against plan.
  Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT: APPROVE/REJECT`

- [ ] F2. **Code Quality Review** — `unspecified-high`
  Run `./gradlew build`. Review all changed files for: `as any`/`@ts-ignore` Kotlin equivalents (`as`, unchecked casts), empty catches, hardcoded colors/dimensions, unused imports, commented-out code. Check AI slop: excessive comments, over-abstraction, generic names. Verify all functions use `internal` visibility where appropriate.
  Output: `Build [PASS/FAIL] | Tests [N pass/N fail] | Files [N clean/N issues] | VERDICT`

- [ ] F3. **Full Build Verification** — `unspecified-high`
  Run `./gradlew clean build` from clean state. Run `./gradlew :core:network:testDebugUnitTest :core:data:testDebugUnitTest :feature:weather:testDebugUnitTest` individually. Verify each module's tests pass independently. Check that preview composables compile without errors.
  Output: `Clean Build [PASS/FAIL] | Module Tests [N/N pass] | VERDICT`

- [ ] F4. **Scope Fidelity Check** — `deep`
  For each task: read "What to do", read actual diff (git log/diff). Verify 1:1 — everything in spec was built, nothing beyond spec was built. Check `TodayWeatherCard.kt` internal implementation is UNCHANGED (only preview updated). Check no files outside the listed deliverables were modified. Flag unaccounted changes.
  Output: `Tasks [N/N compliant] | Scope [CLEAN/N violations] | Unaccounted [CLEAN/N files] | VERDICT`

---

## Commit Strategy

| # | Message | Key Files | Pre-commit Test |
|---|---------|-----------|-----------------|
| 1 | `feat(data): add wind degree field to network DTO and domain model` | `NetworkCurrentWeatherResponse.kt`, `CurrentWeather.kt`, `WeatherMapper.kt`, fakes, tests | `./gradlew :core:network:testDebugUnitTest :core:data:testDebugUnitTest` |
| 2 | `feat(weather): add weather conversion utilities (dew point, compass, km/h)` | `WeatherConversions.kt`, `WeatherConversionsTest.kt` | `./gradlew :feature:weather:testDebugUnitTest` |
| 3 | `feat(weather): extend UI state with humidity and wind data` | `WeatherUiState.kt`, `WeatherViewModel.kt`, `WeatherViewModelTest.kt`, preview updates | `./gradlew :feature:weather:testDebugUnitTest` |
| 4 | `feat(weather): add humidity and wind metric cards to home screen` | `WeatherMetricCard.kt`, `WeatherHomeScreen.kt` | `./gradlew build` |

---

## Success Criteria

### Verification Commands
```bash
./gradlew clean build                    # Expected: BUILD SUCCESSFUL
./gradlew test                           # Expected: All tests pass, 0 failures
./gradlew :core:network:testDebugUnitTest  # Expected: PASSED (existing + new deg tests)
./gradlew :core:data:testDebugUnitTest     # Expected: PASSED (mapper + windDeg)
./gradlew :feature:weather:testDebugUnitTest # Expected: PASSED (conversions + ViewModel)
```

### Final Checklist
- [ ] All "Must Have" present (humidity card, wind card, reusable composable, null-safe, tests)
- [ ] All "Must NOT Have" absent (no extra cards, no slot APIs, no hardcoded colors, no new deps)
- [ ] All existing tests still pass (no regressions)
- [ ] TodayWeatherCard internal implementation unchanged
- [ ] All new functions use `internal` visibility
