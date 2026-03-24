# Phase 3: Today's Weather Hero Card — TodayWeatherCard + WeatherViewModel

## TL;DR

> **Quick Summary**: Build the "Today's Weather" hero card composable with its ViewModel, sealed UiState, and unit tests in `:feature:weather`. Also fix `CurrentWeather` model to include tempMax/tempMin, and add 3 new dependencies to the version catalog.
> 
> **Deliverables**:
> - `CurrentWeather` model updated with `tempMax`/`tempMin` fields + mapper/fake updated
> - 3 new version catalog entries (`lifecycle-viewmodel-compose`, `hilt-navigation-compose`, `compose-material-icons-extended`)
> - `WeatherViewModel` + `WeatherUiState` sealed interface (Loading/Success/Error)
> - `TodayWeatherCard` composable with 135° gradient, atmospheric blur circles, high/low pills
> - `WeatherHomeScreen` composable wiring ViewModel to card
> - `WeatherViewModelTest` with 3 state-transition tests (TDD)
> - `@Preview` functions for Success and Loading states
> 
> **Estimated Effort**: Medium
> **Parallel Execution**: YES — 3 waves
> **Critical Path**: Task 1 → Task 2 → Task 3 → Task 4 → Task 5 → Task 6

---

## Context

### Original Request
Implement Phase 3's `WeatherHomeScreen`, focusing only on the first card — "Today's Weather" (今日天氣) — matching the Stitch design in `docs/stitch_playgroundweather/today_s_weather_5_day_forecast_updated_location/screen.png`.

### Interview Summary
**Key Discussions**:
- **Scope**: Composable + ViewModel together (not composable-only)
- **Model fix**: Include tempMax/tempMin addition to CurrentWeather in this task
- **Icons library**: User approved adding `compose-material-icons-extended`
- **Decorative effects**: Implement atmospheric blur circles on the hero card
- **Testing**: Include ViewModel unit tests with TDD approach

**Research Findings**:
- `:feature:weather` module is completely empty — no .kt files exist yet
- `CurrentWeather` model lacks `tempMax`/`tempMin` but API DTO (`NetworkMain`) already has them
- `WeatherRepository` uses `suspend` functions (not `Flow`) — ViewModel must use `MutableStateFlow`
- Theme is in `:app` but accessible at runtime via `MaterialTheme` composable
- Version catalog missing `lifecycle-viewmodel-compose`, `hilt-navigation-compose`, and `compose-material-icons-extended`
- `Modifier.blur()` is no-op below API 31 — must use Canvas + radialGradient for decorative circles
- `FakeWeatherRepository` is in `main/` source set (not test) — reusable from feature module tests

### Metis Review
**Identified Gaps** (addressed):
- **Missing ViewModel dependencies**: Added explicit task for `lifecycle-viewmodel-compose` and `hilt-navigation-compose` catalog entries
- **Typography mismatch**: Plan uses `displayLarge` (57sp) per design system, not custom 80sp
- **Blur API limitation**: Use Canvas + radialGradient, not `Modifier.blur()`
- **FakeWeatherRepository compilation**: Model change, mapper update, and fake update must be atomic
- **No MainDispatcherRule**: Include creation of JUnit 4 TestRule for `Dispatchers.Main` override
- **Selected city**: Default to first city in `CityRepository.getCities()` — city selection is separate feature
- **Material Icons Extended build impact**: Proceed as user approved; note build time trade-off

---

## Work Objectives

### Core Objective
Build the TodayWeatherCard hero composable with its ViewModel layer, matching the Stitch "Atmospheric Editorial" design, with full unit test coverage for state transitions.

### Concrete Deliverables
- `core/model/.../CurrentWeather.kt` — 2 new fields: `tempMax`, `tempMin`
- `core/data/.../mapper/WeatherMapper.kt` — 2 new mapping lines
- `core/data/.../FakeWeatherRepository.kt` — sample data updated
- `gradle/libs.versions.toml` — 3 new library entries
- `feature/weather/build.gradle.kts` — new dependencies added
- `feature/weather/.../WeatherUiState.kt` — sealed interface
- `feature/weather/.../WeatherViewModel.kt` — @HiltViewModel
- `feature/weather/.../TodayWeatherCard.kt` — hero card composable + @Preview
- `feature/weather/.../WeatherHomeScreen.kt` — screen composable wiring ViewModel to card
- `feature/weather/.../WeatherIconMapper.kt` — iconCode → ImageVector mapping utility
- `feature/weather/src/test/.../WeatherViewModelTest.kt` — 3 unit tests
- `feature/weather/src/test/.../FakeCityRepository.kt` — test fake
- `feature/weather/src/test/.../MainDispatcherRule.kt` — JUnit 4 test rule

### Definition of Done
- [ ] `./gradlew build` passes (all modules, all tests, lint)
- [ ] 3+ ViewModel unit tests pass
- [ ] TodayWeatherCard renders in @Preview with correct gradient, typography, and layout
- [ ] No regressions in existing `:core:data` tests

### Must Have
- Blue gradient hero card (135° from `primary` → `primaryContainer`)
- Current temperature (displayLarge, Manrope ExtraBold 57sp)
- Weather condition text (headlineSmall, Manrope Bold 24sp)
- High/Low temperature pills (white/10 bg, pill-shaped)
- Weather icon (Material Icons, tertiary-colored for sun variants)
- Atmospheric decorative circles (Canvas + radialGradient)
- Card corner radius = `Shapes.large` (32dp)
- ViewModel with Loading/Success/Error states
- Unit tests for all 3 state transitions

### Must NOT Have (Guardrails)
- ❌ City selection UI, navigation, or search screen
- ❌ Forecast list, hourly scroll, humidity/wind/sunrise cards
- ❌ Animation or motion effects
- ❌ Dark theme handling (only light theme exists)
- ❌ Error UI composable (only the UiState.Error data class)
- ❌ Domain/Use Case layer (ViewModel calls Repository directly)
- ❌ `Modifier.blur()` usage (no-op on API 24-30)
- ❌ Hardcoded colors, font sizes, or shapes (always use MaterialTheme tokens)
- ❌ 1px borders on any element (DESIGN.md "No-Line" rule)
- ❌ Changes to `MainActivity.kt` (wiring is a separate task)
- ❌ Mockito/MockK — fakes only
- ❌ Compose UI tests (instrumented) — JVM unit tests only
- ❌ Modifications to `DataModule.kt` or any Hilt module
- ❌ More than 2 @Preview functions

---

## Verification Strategy

> **ZERO HUMAN INTERVENTION** — ALL verification is agent-executed. No exceptions.

### Test Decision
- **Infrastructure exists**: YES (JUnit 4 + coroutines-test in project)
- **Automated tests**: TDD — tests written before implementation for ViewModel
- **Framework**: JUnit 4 + kotlinx-coroutines-test (existing)

### QA Policy
Every task MUST include agent-executed QA scenarios.
Evidence saved to `.sisyphus/evidence/task-{N}-{scenario-slug}.{ext}`.

- **Model/Data**: Use Bash (`./gradlew`) — compile, run tests, verify output
- **UI Composable**: Use Bash (`./gradlew compileDebugKotlin + lintDebug`) — verify compilation and lint
- **ViewModel**: Use Bash (`./gradlew testDebugUnitTest`) — run JUnit tests

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately — model fix + dependencies):
├── Task 1: Add tempMax/tempMin to CurrentWeather + mapper + fake [quick]
├── Task 2: Add 3 new deps to version catalog + feature build.gradle.kts [quick]

Wave 2 (After Wave 1 — ViewModel TDD):
├── Task 3: Write ViewModel tests + fakes + MainDispatcherRule (TDD red) [unspecified-high]
├── Task 4: Implement WeatherViewModel + WeatherUiState (TDD green) [unspecified-high]

Wave 3 (After Wave 2 — UI Composables):
├── Task 5: Build TodayWeatherCard composable + icon mapper [visual-engineering]
├── Task 6: Build WeatherHomeScreen composable wiring ViewModel to card [quick]

Wave FINAL (After ALL tasks — 4 parallel reviews):
├── Task F1: Plan compliance audit (oracle)
├── Task F2: Code quality review (unspecified-high)
├── Task F3: Real manual QA (unspecified-high)
└── Task F4: Scope fidelity check (deep)
-> Present results -> Get explicit user okay

Critical Path: Task 1 → Task 2 → Task 3 → Task 4 → Task 5 → Task 6 → F1-F4
Parallel Speedup: Tasks 1+2 parallel, 3+4 sequential (TDD), 5+6 can overlap partially
Max Concurrent: 2 (Wave 1)
```

### Dependency Matrix

| Task | Depends On | Blocks | Wave |
|------|-----------|--------|------|
| 1 | — | 3, 4 | 1 |
| 2 | — | 3, 4, 5, 6 | 1 |
| 3 | 1, 2 | 4 | 2 |
| 4 | 3 | 5, 6 | 2 |
| 5 | 2, 4 | 6 | 3 |
| 6 | 4, 5 | F1-F4 | 3 |
| F1-F4 | 6 | — | FINAL |

### Agent Dispatch Summary

- **Wave 1**: **2 tasks** — T1 → `quick`, T2 → `quick`
- **Wave 2**: **2 tasks** — T3 → `unspecified-high`, T4 → `unspecified-high`
- **Wave 3**: **2 tasks** — T5 → `visual-engineering`, T6 → `quick`
- **FINAL**: **4 tasks** — F1 → `oracle`, F2 → `unspecified-high`, F3 → `unspecified-high`, F4 → `deep`

---

## TODOs

- [x] 1. Add tempMax/tempMin to CurrentWeather model, mapper, and fake

  **What to do**:
  - Add `tempMax: Double` and `tempMin: Double` fields to `CurrentWeather` data class (after `temperature` field for logical grouping)
  - Update `WeatherMapper.asExternalModel()` to map `main.tempMax` and `main.tempMin`
  - Update `FakeWeatherRepository.currentWeatherResult` to include `tempMax = 32.0` and `tempMin = 25.0`
  - Run existing `WeatherMapperTest` to verify the mapper change doesn't break serialization — update the test's expected assertions to include new fields

  **Must NOT do**:
  - Do NOT change any other fields in CurrentWeather
  - Do NOT modify `NetworkCurrentWeatherResponse.kt` or `NetworkMain` (already has tempMin/tempMax)
  - Do NOT change `WeatherRepository` interface signatures

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Small, well-defined change across 3-4 files, <10 lines total
  - **Skills**: `[]`
    - No special skills needed for data class field additions

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Task 2)
  - **Parallel Group**: Wave 1 (with Task 2)
  - **Blocks**: Tasks 3, 4
  - **Blocked By**: None (can start immediately)

  **References**:

  **Pattern References**:
  - `core/model/src/main/kotlin/playground/app/tobeylin/weather/core/model/CurrentWeather.kt:3-12` — Current model with 8 fields. Add `tempMax`/`tempMin` after line 4 (`temperature`) for logical grouping
  - `core/data/src/main/java/playground/app/tobeylin/weather/core/data/mapper/WeatherMapper.kt:6-15` — Current mapper. Add `tempMax = main.tempMax` and `tempMin = main.tempMin` lines
  - `core/data/src/main/java/playground/app/tobeylin/weather/core/data/FakeWeatherRepository.kt:9-18` — Fake with sample data. Add `tempMax = 32.0` and `tempMin = 25.0` to the `currentWeatherResult`

  **API/Type References**:
  - `core/network/src/main/java/playground/app/tobeylin/weather/core/network/model/NetworkCurrentWeatherResponse.kt:34-35` — DTO already has `tempMin: Double` and `tempMax: Double` in `NetworkMain`

  **Test References**:
  - `core/data/src/test/java/playground/app/tobeylin/weather/core/data/mapper/WeatherMapperTest.kt` — Existing mapper test. Must update expected values to include new fields
  - `core/data/src/test/java/playground/app/tobeylin/weather/core/data/DefaultWeatherRepositoryTest.kt:24-28` — Repository test pattern. Existing tests should still pass as-is (they test temperature, not tempMax/tempMin)

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Model compiles and existing tests pass after field addition
    Tool: Bash
    Preconditions: Fields added to CurrentWeather, WeatherMapper, and FakeWeatherRepository
    Steps:
      1. Run: ./gradlew :core:model:compileDebugKotlin
      2. Assert: BUILD SUCCESSFUL
      3. Run: ./gradlew :core:data:compileDebugKotlin
      4. Assert: BUILD SUCCESSFUL
      5. Run: ./gradlew :core:data:testDebugUnitTest
      6. Assert: BUILD SUCCESSFUL, all tests pass (including updated WeatherMapperTest)
    Expected Result: All 3 gradle commands succeed with BUILD SUCCESSFUL
    Failure Indicators: Compilation error mentioning missing fields, or test assertion failure
    Evidence: .sisyphus/evidence/task-1-model-compile.txt

  Scenario: FakeWeatherRepository returns tempMax/tempMin in sample data
    Tool: Bash
    Preconditions: FakeWeatherRepository updated
    Steps:
      1. Run: grep -n "tempMax" core/data/src/main/java/playground/app/tobeylin/weather/core/data/FakeWeatherRepository.kt
      2. Assert: Output contains "tempMax = 32.0"
      3. Run: grep -n "tempMin" core/data/src/main/java/playground/app/tobeylin/weather/core/data/FakeWeatherRepository.kt
      4. Assert: Output contains "tempMin = 25.0"
    Expected Result: Both grep commands find the expected values
    Failure Indicators: Empty grep output or different values
    Evidence: .sisyphus/evidence/task-1-fake-data-check.txt
  ```

  **Commit**: YES
  - Message: `fix(model): add tempMax/tempMin to CurrentWeather and update mapper`
  - Files: `CurrentWeather.kt`, `WeatherMapper.kt`, `FakeWeatherRepository.kt`, `WeatherMapperTest.kt`
  - Pre-commit: `./gradlew :core:model:compileDebugKotlin :core:data:testDebugUnitTest`

- [x] 2. Add ViewModel and Icons dependencies to version catalog and feature module

  **What to do**:
  - Add 3 new entries to `gradle/libs.versions.toml` under `[libraries]`:
    - `lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version = "2.9.0" }`
    - `hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.2.0" }`
    - `compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }` (uses Compose BOM, no version needed)
  - Add to `feature/weather/build.gradle.kts` dependencies block:
    - `implementation(libs.lifecycle.viewmodel.compose)`
    - `implementation(libs.hilt.navigation.compose)`
    - `implementation(libs.compose.material.icons.extended)`
    - `testImplementation(libs.junit)`
    - `testImplementation(libs.kotlinx.coroutines.test)`
  - Verify versions resolve correctly

  **Must NOT do**:
  - Do NOT hardcode versions in build.gradle.kts — use catalog refs only
  - Do NOT modify app/build.gradle.kts or any other module's build file
  - Do NOT add dependencies that aren't needed (e.g., Room, Navigation)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Config file changes only, well-defined additions
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Task 1)
  - **Parallel Group**: Wave 1 (with Task 1)
  - **Blocks**: Tasks 3, 4, 5, 6
  - **Blocked By**: None (can start immediately)

  **References**:

  **Pattern References**:
  - `gradle/libs.versions.toml:18-40` — Current library entries. Follow exact naming pattern: kebab-case for catalog names, group/name/version structure
  - `feature/weather/build.gradle.kts:41-55` — Current dependencies block. Add new entries after line 49 (compose-ui-tooling-preview)

  **External References**:
  - `libs.versions.toml` line 10: `composeBom = "2026.02.00"` — icons-extended uses BOM so no version needed
  - Lifecycle 2.9.0 is compatible with Compose BOM 2026.02.00
  - Hilt Navigation Compose 1.2.0 is compatible with Hilt 2.59.2

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Dependencies resolve correctly
    Tool: Bash
    Preconditions: Version catalog and build.gradle.kts updated
    Steps:
      1. Run: ./gradlew :feature:weather:dependencies --configuration debugCompileClasspath 2>&1 | head -100
      2. Assert: Output contains "lifecycle-viewmodel-compose"
      3. Assert: Output contains "hilt-navigation-compose"
      4. Assert: Output contains "material-icons-extended"
      5. Assert: No FAILED or UNRESOLVED entries
    Expected Result: All 3 new dependencies appear in resolved dependency tree
    Failure Indicators: "Could not resolve" errors, missing entries
    Evidence: .sisyphus/evidence/task-2-deps-resolve.txt

  Scenario: Feature module compiles with new dependencies
    Tool: Bash
    Preconditions: Dependencies added
    Steps:
      1. Run: ./gradlew :feature:weather:compileDebugKotlin
      2. Assert: BUILD SUCCESSFUL
    Expected Result: Compilation succeeds with no errors
    Failure Indicators: Version conflict, missing class, or resolution failure
    Evidence: .sisyphus/evidence/task-2-compile.txt
  ```

  **Commit**: YES
  - Message: `build(deps): add viewmodel-compose, hilt-navigation-compose, icons-extended`
  - Files: `libs.versions.toml`, `feature/weather/build.gradle.kts`
  - Pre-commit: `./gradlew :feature:weather:dependencies`

- [x] 3. Write WeatherViewModel tests + fakes + MainDispatcherRule (TDD Red Phase)

  **What to do**:
  - Create test directory structure: `feature/weather/src/test/java/playground/app/tobeylin/weather/feature/weather/`
  - Create `MainDispatcherRule.kt` — a JUnit 4 `TestRule` that replaces `Dispatchers.Main` with `UnconfinedTestDispatcher` before each test and resets after
  - Create `FakeCityRepository.kt` in test source — implements `CityRepository` with a controllable `cities` var returning a default list with one city (Taipei)
  - Create `WeatherViewModelTest.kt` with 3 test methods:
    1. `initial_state_is_loading` — verify ViewModel's stateFlow initial value is `WeatherUiState.Loading`
    2. `when_weather_loads_successfully_state_is_success` — use `FakeWeatherRepository` + `FakeCityRepository`, advance coroutine, verify `Success` state with correct cityName, temperature, tempMax, tempMin, condition, iconCode
    3. `when_weather_load_fails_state_is_error` — set `shouldThrowError = true`, verify `Error` state with message
  - **TDD NOTE**: These tests will NOT compile yet because `WeatherViewModel` and `WeatherUiState` don't exist. This is intentional — it's the RED phase. The executor should verify test logic by inspection, then proceed immediately to Task 4. Commits 3+4 can be squashed if "every commit must compile" is preferred.

  **Must NOT do**:
  - Do NOT use Mockito, MockK, or any mocking framework — fakes only
  - Do NOT write more than 5 tests total
  - Do NOT write Compose UI tests (instrumented)
  - Do NOT create WeatherViewModel yet (that's Task 4)

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Requires understanding of coroutine testing patterns, Dispatchers.Main replacement, and ViewModel lifecycle
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: NO (sequential with Task 4)
  - **Parallel Group**: Wave 2 (sequential: Task 3 → Task 4)
  - **Blocks**: Task 4
  - **Blocked By**: Tasks 1, 2

  **References**:

  **Pattern References**:
  - `core/data/src/test/java/playground/app/tobeylin/weather/core/data/DefaultWeatherRepositoryTest.kt:12-48` — Test structure pattern: `@Before` setUp with fakes, `@Test` with `runTest`, direct constructor injection
  - `core/data/src/main/java/playground/app/tobeylin/weather/core/data/FakeWeatherRepository.kt:7-36` — Fake pattern: controllable result vars, `shouldThrowError` flag. Tests will use this existing fake.
  - `core/data/src/main/java/playground/app/tobeylin/weather/core/data/CityRepository.kt:6-8` — Interface to implement for `FakeCityRepository`

  **API/Type References**:
  - `core/model/src/main/kotlin/playground/app/tobeylin/weather/core/model/CurrentWeather.kt` — Fields to assert in Success state
  - `core/model/src/main/kotlin/playground/app/tobeylin/weather/core/model/City.kt` — City fields for FakeCityRepository sample data

  **External References**:
  - `kotlinx.coroutines.test.UnconfinedTestDispatcher` — for MainDispatcherRule
  - `kotlinx.coroutines.Dispatchers.setMain` / `resetMain` — for test dispatcher override
  - `kotlinx.coroutines.test.runTest` — for coroutine test scope

  **WHY Each Reference Matters**:
  - `DefaultWeatherRepositoryTest` → Copy the exact @Before/@Test/@runTest structure
  - `FakeWeatherRepository` → Tests will pass this to ViewModel constructor
  - `CityRepository` interface → FakeCityRepository must implement the same `getCities()` method
  - `CurrentWeather` fields → Assertions need to match the exact field names and types

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Test files exist with correct structure
    Tool: Bash
    Preconditions: Test files created
    Steps:
      1. Run: find feature/weather/src/test -name "*.kt" | sort
      2. Assert: Output contains WeatherViewModelTest.kt, FakeCityRepository.kt, MainDispatcherRule.kt
      3. Run: grep -c "@Test" feature/weather/src/test/java/playground/app/tobeylin/weather/feature/weather/WeatherViewModelTest.kt
      4. Assert: Output is "3" (exactly 3 test methods)
    Expected Result: 3 test files exist, test file has exactly 3 @Test methods
    Failure Indicators: Missing files, wrong count
    Evidence: .sisyphus/evidence/task-3-test-structure.txt

  Scenario: Tests compile after Task 4 completes (verified retrospectively)
    Tool: Bash
    Preconditions: Task 4 complete (ViewModel exists)
    Steps:
      1. Run: ./gradlew :feature:weather:testDebugUnitTest
      2. Assert: BUILD SUCCESSFUL, 3 tests run, 0 failures
    Expected Result: All 3 tests pass
    Failure Indicators: Compilation error or test assertion failure
    Evidence: .sisyphus/evidence/task-3-tests-pass.txt (captured after Task 4)
  ```

  **Commit**: YES (can be squashed with Task 4 if preferred)
  - Message: `test(weather): add WeatherViewModel tests with fakes (TDD red)`
  - Files: `WeatherViewModelTest.kt`, `FakeCityRepository.kt`, `MainDispatcherRule.kt`
  - Pre-commit: N/A (TDD red — won't compile until Task 4)

- [x] 4. Implement WeatherViewModel and WeatherUiState (TDD Green Phase)

  **What to do**:
  - Create `WeatherUiState.kt` in `feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/`:
    ```kotlin
    sealed interface WeatherUiState {
        data object Loading : WeatherUiState
        data class Success(
            val cityName: String,
            val temperature: Double,
            val tempMax: Double,
            val tempMin: Double,
            val condition: String,
            val conditionDescription: String,
            val iconCode: String,
        ) : WeatherUiState
        data class Error(val message: String) : WeatherUiState
    }
    ```
  - Create `WeatherViewModel.kt`:
    - Annotate with `@HiltViewModel`, inject `WeatherRepository` and `CityRepository`
    - Private `_uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)`
    - Public `uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()`
    - In `init {}`: call `loadWeather()` which launches a coroutine in `viewModelScope`
    - `loadWeather()`: get first city from `CityRepository.getCities()`, call `weatherRepository.getCurrentWeather(city.latitude, city.longitude)`, map to `Success` state. On exception → `Error(message)`.
  - Run Task 3's tests — all should pass (TDD GREEN)

  **Must NOT do**:
  - Do NOT use `stateIn()` — repository returns `suspend` values, not `Flow`
  - Do NOT use `AndroidViewModel` — use plain `ViewModel`
  - Do NOT add navigation arguments or city selection logic
  - Do NOT create a Hilt @Module — `@HiltViewModel` with `@Inject constructor` is sufficient
  - Do NOT add a public `refresh()` or `retry()` method — keep scope minimal

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Requires correct ViewModel patterns, coroutine scoping, and Hilt integration
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: NO (must follow Task 3)
  - **Parallel Group**: Wave 2 (sequential: Task 3 → Task 4)
  - **Blocks**: Tasks 5, 6
  - **Blocked By**: Task 3

  **References**:

  **Pattern References**:
  - `core/data/src/main/java/playground/app/tobeylin/weather/core/data/WeatherRepository.kt:6-9` — Interface the ViewModel depends on: `suspend fun getCurrentWeather(lat, lon): CurrentWeather`
  - `core/data/src/main/java/playground/app/tobeylin/weather/core/data/CityRepository.kt:6-8` — `fun getCities(): List<City>` — synchronous, returns hardcoded list
  - `core/data/src/main/java/playground/app/tobeylin/weather/core/data/di/DataModule.kt` — Hilt module pattern. ViewModel does NOT need its own module — `@HiltViewModel` + `@Inject constructor` auto-wires

  **API/Type References**:
  - `core/model/src/main/kotlin/playground/app/tobeylin/weather/core/model/CurrentWeather.kt` — Fields to map into `WeatherUiState.Success`
  - `core/model/src/main/kotlin/playground/app/tobeylin/weather/core/model/City.kt:3-8` — `name` field used for `cityName` in UiState

  **External References**:
  - `androidx.lifecycle.ViewModel` — base class
  - `androidx.lifecycle.viewModelScope` — coroutine scope for async work
  - `dagger.hilt.android.lifecycle.HiltViewModel` — annotation
  - `javax.inject.Inject` — constructor injection annotation

  **WHY Each Reference Matters**:
  - `WeatherRepository` → ViewModel calls `getCurrentWeather()` in init block
  - `CityRepository` → ViewModel gets default city from `getCities().first()`
  - `DataModule` → Shows Hilt wiring pattern but ViewModel doesn't need one (auto-injected)
  - `CurrentWeather` → Map fields to `WeatherUiState.Success` properties

  **Acceptance Criteria**:

  **If TDD:**
  - [ ] Test file from Task 3 compiles: `feature/weather/src/test/.../WeatherViewModelTest.kt`
  - [ ] `./gradlew :feature:weather:testDebugUnitTest` → PASS (3 tests, 0 failures)

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: All 3 ViewModel tests pass (TDD green)
    Tool: Bash
    Preconditions: WeatherViewModel.kt and WeatherUiState.kt created
    Steps:
      1. Run: ./gradlew :feature:weather:testDebugUnitTest --info 2>&1 | grep -E "(PASSED|FAILED|tests completed)"
      2. Assert: All 3 tests show PASSED
      3. Assert: "0 failures" in summary
    Expected Result: 3 tests PASSED, 0 failures
    Failure Indicators: Any test shows FAILED, or compilation error
    Evidence: .sisyphus/evidence/task-4-tests-green.txt

  Scenario: ViewModel follows correct architecture patterns
    Tool: Bash
    Preconditions: WeatherViewModel.kt exists
    Steps:
      1. Run: grep "@HiltViewModel" feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/WeatherViewModel.kt
      2. Assert: Output contains "@HiltViewModel"
      3. Run: grep "MutableStateFlow" feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/WeatherViewModel.kt
      4. Assert: Output contains "MutableStateFlow"
      5. Run: grep -c "stateIn" feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/WeatherViewModel.kt
      6. Assert: Output is "0" (no stateIn usage — suspend-based repo)
    Expected Result: Uses @HiltViewModel, MutableStateFlow, and NOT stateIn
    Failure Indicators: Missing annotation, wrong state management pattern
    Evidence: .sisyphus/evidence/task-4-architecture-check.txt
  ```

  **Commit**: YES
  - Message: `feat(weather): add WeatherViewModel and WeatherUiState`
  - Files: `WeatherViewModel.kt`, `WeatherUiState.kt`
  - Pre-commit: `./gradlew :feature:weather:testDebugUnitTest`

- [x] 5. Build TodayWeatherCard composable with atmospheric design + icon mapper

  **What to do**:
  - Create `WeatherIconMapper.kt` in `feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/`:
    - Function `fun weatherIconFor(iconCode: String): ImageVector` that maps OpenWeatherMap icon codes to Material Icons:
      - `"01d"`, `"01n"` → `Icons.Filled.WbSunny` (or night equivalent)
      - `"02d"`, `"02n"` → `Icons.Filled.WbCloudy` (partly cloudy — use a reasonable mapping)
      - `"03d"`, `"03n"`, `"04d"`, `"04n"` → `Icons.Filled.Cloud`
      - `"09d"`, `"09n"`, `"10d"`, `"10n"` → `Icons.Filled.WaterDrop` (rain)
      - `"11d"`, `"11n"` → `Icons.Filled.Thunderstorm`
      - `"13d"`, `"13n"` → `Icons.Filled.AcUnit` (snow)
      - `"50d"`, `"50n"` → `Icons.Filled.Foggy` (mist)
      - Default → `Icons.Filled.Cloud`
    - Function `fun isWarmIcon(iconCode: String): Boolean` — returns true for `"01d"`, `"01n"`, `"02d"`, `"02n"` (sun-related icons should be Tertiary color per DESIGN.md)
  - Create `TodayWeatherCard.kt` composable:
    - **Signature**: `@Composable fun TodayWeatherCard(uiState: WeatherUiState.Success, modifier: Modifier = Modifier)`
    - **Structure** (matching Stitch code.html lines 114-135):
      ```
      Box(modifier with clip(MaterialTheme.shapes.large) + background(gradient brush))
        ├── Canvas decorative circle (top-right, white/10 radial gradient, offset)
        ├── Canvas decorative circle (bottom-left, white/10 radial gradient, offset)
        └── Column(horizontalAlignment = CenterHorizontally, padding = 32.dp)
              ├── Row(temperature + icon)
              │     ├── Text("24°", style = displayLarge, color = onPrimary)
              │     └── Icon(weatherIconFor(iconCode), tint = tertiary or primary based on isWarmIcon)
              ├── Text("Clear Sky", style = headlineSmall, color = onPrimary)
              └── Row(high/low pills)
                    ├── Pill(↑ 28°C) — Row with Surface(white/10, pill shape): arrow icon + text
                    └── Pill(↓ 18°C) — same structure
      ```
    - **Gradient**: `Brush.linearGradient(colors = listOf(primary, primaryContainer), start = Offset(0f, 0f), end = Offset(width, height))` at 135° angle
    - **Decorative circles**: Two `drawCircle` calls in a `Canvas` or `Modifier.drawBehind`:
      - Top-right: `Brush.radialGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent))`, center offset top-right, radius ~96.dp
      - Bottom-left: similar, smaller radius ~64.dp, center offset bottom-left
    - **High/Low pills**: `Surface(color = Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(50))` containing Row with arrow icon + temperature text
    - **Temperature format**: `"${temperature.toInt()}°"` (round to integer, append degree symbol)
    - **@Preview functions** (2 total):
      - `TodayWeatherCardPreview()` — Success state with sample data matching Stitch design (24°, Clear Sky, 28°/18°)
      - `TodayWeatherCardLoadingPreview()` — Show a simple loading placeholder (optional, can be just the card shape with CircularProgressIndicator)
    - All text colors use `MaterialTheme.colorScheme.onPrimary`
    - No 1px borders anywhere
    - Icon tint: `MaterialTheme.colorScheme.tertiary` for warm icons (sun), `MaterialTheme.colorScheme.onPrimary` for others

  **Must NOT do**:
  - Do NOT use `Modifier.blur()` — no-op below API 31
  - Do NOT hardcode hex color values — use `MaterialTheme.colorScheme.*`
  - Do NOT hardcode font sizes — use `MaterialTheme.typography.*`
  - Do NOT add animation
  - Do NOT use 1px borders (No-Line rule)
  - Do NOT create more than 2 @Preview functions
  - Do NOT add Humidity, Wind, Sunrise/Sunset, or Forecast cards

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: This is a pure UI composable with gradient, canvas drawing, layout composition, and design system adherence
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: NO (needs Task 4's WeatherUiState.Success type)
  - **Parallel Group**: Wave 3
  - **Blocks**: Task 6
  - **Blocked By**: Tasks 2, 4

  **References**:

  **Pattern References**:
  - `app/src/main/java/playground/app/tobeylin/weather/ui/theme/Color.kt:6-8` — `Primary = #005F9C`, `PrimaryContainer = #39A6FF`, `OnPrimary = #ECF3FF` — these are the gradient + text colors. Access via `MaterialTheme.colorScheme.primary`, `.primaryContainer`, `.onPrimary`
  - `app/src/main/java/playground/app/tobeylin/weather/ui/theme/Type.kt:22-38` — `displayLarge` (Manrope ExtraBold 57sp) for temperature, `headlineSmall` (Manrope Bold 24sp) for condition, `titleSmall` (Inter SemiBold 14sp) for pill text
  - `app/src/main/java/playground/app/tobeylin/weather/ui/theme/Shape.kt:7-12` — `Shapes.large = RoundedCornerShape(32.dp)` for card corner radius

  **Design References** (Stitch mockup — primary visual spec):
  - `docs/stitch_playgroundweather/today_s_weather_5_day_forecast_updated_location/screen.png` — Visual reference for the hero card
  - `docs/stitch_playgroundweather/today_s_weather_5_day_forecast_updated_location/code.html:87-89` — `.hero-gradient { background: linear-gradient(135deg, #005f9c 0%, #39a6ff 100%); }` — gradient spec
  - `docs/stitch_playgroundweather/today_s_weather_5_day_forecast_updated_location/code.html:114-135` — Full HTML structure of the hero card: layout, nesting, spacing, decorative elements
  - `docs/stitch_playgroundweather/today_s_weather_5_day_forecast_updated_location/code.html:117` — Temperature: `font-headline font-extrabold text-[5rem]` → map to `displayLarge` (57sp)
  - `docs/stitch_playgroundweather/today_s_weather_5_day_forecast_updated_location/code.html:118` — Icon: `text-[4rem] text-tertiary-fixed` → ~48-64sp, tint with `tertiary` color for sun
  - `docs/stitch_playgroundweather/today_s_weather_5_day_forecast_updated_location/code.html:120` — Condition: `font-headline font-bold text-2xl` → `headlineSmall` (24sp)
  - `docs/stitch_playgroundweather/today_s_weather_5_day_forecast_updated_location/code.html:122-129` — High/Low pills: `bg-white/10 backdrop-blur-md px-4 py-2 rounded-full` → `Color.White.copy(alpha = 0.1f)`, `RoundedCornerShape(50)`, padding 16.dp horizontal / 8.dp vertical
  - `docs/stitch_playgroundweather/today_s_weather_5_day_forecast_updated_location/code.html:133-134` — Decorative circles: `bg-white/10 rounded-full blur-3xl` with `absolute` positioning

  **API/Type References**:
  - `feature/weather/src/main/java/.../WeatherUiState.kt` (from Task 4) — `WeatherUiState.Success` properties: `cityName`, `temperature`, `tempMax`, `tempMin`, `condition`, `conditionDescription`, `iconCode`

  **External References**:
  - OpenWeatherMap icon codes: https://openweathermap.org/weather-conditions — maps like `01d` = clear sky day, `10d` = rain day

  **WHY Each Reference Matters**:
  - `Color.kt` → Never hardcode colors; gradient must use theme tokens
  - `Type.kt` → Temperature must be `displayLarge`, condition must be `headlineSmall`
  - `Shape.kt` → Card radius must be `Shapes.large` (32dp)
  - `code.html` lines 114-135 → THE primary structural reference — follow HTML nesting order for composable structure
  - `screen.png` → Visual validation target — the composable should look like this

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: TodayWeatherCard compiles and passes lint
    Tool: Bash
    Preconditions: TodayWeatherCard.kt and WeatherIconMapper.kt created
    Steps:
      1. Run: ./gradlew :feature:weather:compileDebugKotlin
      2. Assert: BUILD SUCCESSFUL
      3. Run: ./gradlew :feature:weather:lintDebug
      4. Assert: No errors (warnings acceptable)
    Expected Result: Both compile and lint succeed
    Failure Indicators: Unresolved references, missing imports, lint errors
    Evidence: .sisyphus/evidence/task-5-compile-lint.txt

  Scenario: Card uses theme tokens, not hardcoded values
    Tool: Bash
    Preconditions: TodayWeatherCard.kt exists
    Steps:
      1. Run: grep -c "0xFF" feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/TodayWeatherCard.kt
      2. Assert: Output is "0" (no hardcoded hex colors — exception: Color.White and Color.Transparent are acceptable)
      3. Run: grep "MaterialTheme.colorScheme" feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/TodayWeatherCard.kt | wc -l
      4. Assert: Output is >= 3 (at least primary, primaryContainer, onPrimary references)
      5. Run: grep -c "Modifier.blur" feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/TodayWeatherCard.kt
      6. Assert: Output is "0" (no blur modifier)
    Expected Result: No hardcoded colors, uses MaterialTheme tokens, no blur modifier
    Failure Indicators: Hardcoded hex values, missing theme references, blur usage
    Evidence: .sisyphus/evidence/task-5-theme-compliance.txt

  Scenario: @Preview functions exist
    Tool: Bash
    Preconditions: TodayWeatherCard.kt exists
    Steps:
      1. Run: grep -c "@Preview" feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/TodayWeatherCard.kt
      2. Assert: Output is >= 1 and <= 2
    Expected Result: 1-2 @Preview functions exist
    Failure Indicators: No @Preview or more than 2
    Evidence: .sisyphus/evidence/task-5-preview-check.txt
  ```

  **Commit**: YES
  - Message: `feat(weather): add TodayWeatherCard composable with atmospheric design`
  - Files: `TodayWeatherCard.kt`, `WeatherIconMapper.kt`
  - Pre-commit: `./gradlew :feature:weather:compileDebugKotlin :feature:weather:lintDebug`

- [x] 6. Build WeatherHomeScreen composable wiring ViewModel to TodayWeatherCard

  **What to do**:
  - Create `WeatherHomeScreen.kt` in `feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/`:
    - **Signature**: `@Composable fun WeatherHomeScreen(viewModel: WeatherViewModel = hiltViewModel(), modifier: Modifier = Modifier)`
    - Collect `viewModel.uiState` with `collectAsStateWithLifecycle()`
    - Use `when (uiState)` to render:
      - `Loading` → `Box(fillMaxSize, contentAlignment = Center) { CircularProgressIndicator() }`
      - `Success` → `TodayWeatherCard(uiState = uiState, modifier = modifier)`
      - `Error` → `Box(fillMaxSize, contentAlignment = Center) { Text(uiState.message) }` (minimal error display)
    - The screen should have a `Scaffold` or simple `Column` with `verticalScroll` for future cards — but only show `TodayWeatherCard` for now
    - Add `@Preview` with a hardcoded Success state
  - Import `hiltViewModel()` from `androidx.hilt.navigation.compose.hiltViewModel`
  - Import `collectAsStateWithLifecycle()` from `androidx.lifecycle.compose.collectAsStateWithLifecycle`
  - Verify full project builds

  **Must NOT do**:
  - Do NOT modify `MainActivity.kt` — that wiring is a separate integration task
  - Do NOT add navigation logic
  - Do NOT add TopAppBar with city name and search icon (that's the full WeatherHomeScreen scope, future task)
  - Do NOT add other cards (Humidity, Wind, Sunrise, Forecast)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Small composable wiring ViewModel state to existing card component
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: NO (needs Task 5)
  - **Parallel Group**: Wave 3 (after Task 5)
  - **Blocks**: F1-F4
  - **Blocked By**: Tasks 4, 5

  **References**:

  **Pattern References**:
  - `app/src/main/java/playground/app/tobeylin/weather/MainActivity.kt:19-25` — Shows how `PlaygroundWeatherTheme` wraps content + `Surface(fillMaxSize)` pattern
  - `feature/weather/src/main/java/.../WeatherViewModel.kt` (from Task 4) — `uiState: StateFlow<WeatherUiState>` — the state to collect
  - `feature/weather/src/main/java/.../TodayWeatherCard.kt` (from Task 5) — `TodayWeatherCard(uiState: WeatherUiState.Success, modifier: Modifier)` — composable to render
  - `feature/weather/src/main/java/.../WeatherUiState.kt` (from Task 4) — `sealed interface` with Loading, Success, Error states

  **External References**:
  - `androidx.hilt.navigation.compose.hiltViewModel` — ViewModel provider in Compose
  - `androidx.lifecycle.compose.collectAsStateWithLifecycle` — lifecycle-aware state collection

  **WHY Each Reference Matters**:
  - `MainActivity` → Shows the wrapping pattern; WeatherHomeScreen will be called from here in a future task
  - `WeatherViewModel.uiState` → The StateFlow to collect with `collectAsStateWithLifecycle()`
  - `TodayWeatherCard` → The composable to render inside the `Success` branch
  - `WeatherUiState` → The sealed interface to `when` against

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Full project builds with all changes
    Tool: Bash
    Preconditions: All 6 tasks complete
    Steps:
      1. Run: ./gradlew build
      2. Assert: BUILD SUCCESSFUL
      3. Run: ./gradlew :feature:weather:testDebugUnitTest --info 2>&1 | grep -E "tests completed"
      4. Assert: "3 tests completed, 0 failed"
      5. Run: ./gradlew :core:data:testDebugUnitTest --info 2>&1 | grep -E "tests completed"
      6. Assert: "3 tests completed, 0 failed" (regression check)
    Expected Result: Full build passes, all 6 tests pass (3 feature + 3 core:data)
    Failure Indicators: Any BUILD FAILED, test failures, or lint errors
    Evidence: .sisyphus/evidence/task-6-full-build.txt

  Scenario: WeatherHomeScreen uses correct Compose patterns
    Tool: Bash
    Preconditions: WeatherHomeScreen.kt exists
    Steps:
      1. Run: grep "collectAsStateWithLifecycle" feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/WeatherHomeScreen.kt
      2. Assert: Output contains "collectAsStateWithLifecycle"
      3. Run: grep "hiltViewModel" feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/WeatherHomeScreen.kt
      4. Assert: Output contains "hiltViewModel"
      5. Run: grep -c "MainActivity" feature/weather/src/main/java/playground/app/tobeylin/weather/feature/weather/WeatherHomeScreen.kt
      6. Assert: Output is "0" (no direct MainActivity reference)
    Expected Result: Uses lifecycle-aware collection and hilt ViewModel injection, no MainActivity coupling
    Failure Indicators: Missing patterns, direct Activity references
    Evidence: .sisyphus/evidence/task-6-pattern-check.txt
  ```

  **Commit**: YES
  - Message: `feat(weather): add WeatherHomeScreen wiring ViewModel to TodayWeatherCard`
  - Files: `WeatherHomeScreen.kt`
  - Pre-commit: `./gradlew build`

---

## Final Verification Wave (MANDATORY — after ALL implementation tasks)

> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.

- [x] F1. **Plan Compliance Audit** — `oracle`
  Read the plan end-to-end. For each "Must Have": verify implementation exists (read file, run command). For each "Must NOT Have": search codebase for forbidden patterns — reject with file:line if found. Check evidence files exist in `.sisyphus/evidence/`. Compare deliverables against plan.
  Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT: APPROVE/REJECT`

- [x] F2. **Code Quality Review** — `unspecified-high`
  Run `./gradlew build`. Review all changed files for: `as any`/`@Suppress`, empty catches, hardcoded colors/sizes, unused imports. Check AI slop: excessive comments, over-abstraction, generic names. Verify all composables accept `Modifier` parameter. Verify no `Modifier.blur()`. Verify no 1px borders.
  Output: `Build [PASS/FAIL] | Lint [PASS/FAIL] | Tests [N pass/N fail] | Files [N clean/N issues] | VERDICT`

- [x] F3. **Real Manual QA** — `unspecified-high`
  Start from clean state. Execute EVERY QA scenario from EVERY task — follow exact steps, capture evidence. Test `./gradlew build` end-to-end. Verify @Preview renders by checking compilation succeeds. Save to `.sisyphus/evidence/final-qa/`.
  Output: `Scenarios [N/N pass] | Build [PASS/FAIL] | VERDICT`

- [x] F4. **Scope Fidelity Check** — `deep`
  For each task: read "What to do", read actual files. Verify 1:1 — everything in spec was built, nothing beyond spec. Check "Must NOT do" compliance. Detect cross-task contamination. Flag unaccounted changes. Verify no changes to `MainActivity.kt`, `DataModule.kt`, or any file outside scope.
  Output: `Tasks [N/N compliant] | Contamination [CLEAN/N issues] | Unaccounted [CLEAN/N files] | VERDICT`

---

## Commit Strategy

| # | Message | Files | Pre-commit Verification |
|---|---------|-------|------------------------|
| 1 | `fix(model): add tempMax/tempMin to CurrentWeather and update mapper` | `CurrentWeather.kt`, `WeatherMapper.kt`, `FakeWeatherRepository.kt`, `WeatherMapperTest.kt` | `./gradlew :core:model:compileDebugKotlin :core:data:testDebugUnitTest` |
| 2 | `build(deps): add viewmodel-compose, hilt-navigation-compose, icons-extended` | `libs.versions.toml`, `feature/weather/build.gradle.kts` | `./gradlew :feature:weather:dependencies` |
| 3 | `test(weather): add WeatherViewModel tests with fakes (TDD red)` | `WeatherViewModelTest.kt`, `FakeCityRepository.kt`, `MainDispatcherRule.kt` | Compile-expected-to-fail (TDD red) |
| 4 | `feat(weather): add WeatherViewModel and WeatherUiState` | `WeatherViewModel.kt`, `WeatherUiState.kt` | `./gradlew :feature:weather:testDebugUnitTest` |
| 5 | `feat(weather): add TodayWeatherCard composable with atmospheric design` | `TodayWeatherCard.kt`, `WeatherIconMapper.kt` | `./gradlew :feature:weather:compileDebugKotlin :feature:weather:lintDebug` |
| 6 | `feat(weather): add WeatherHomeScreen wiring ViewModel to TodayWeatherCard` | `WeatherHomeScreen.kt` | `./gradlew build` |

---

## Success Criteria

### Verification Commands
```bash
./gradlew build                                    # Expected: BUILD SUCCESSFUL
./gradlew :feature:weather:testDebugUnitTest       # Expected: 3+ tests, 0 failures
./gradlew :core:data:testDebugUnitTest             # Expected: 3 tests, 0 failures (regression)
./gradlew :feature:weather:lintDebug               # Expected: No errors
```

### Final Checklist
- [ ] All "Must Have" present
- [ ] All "Must NOT Have" absent
- [ ] All tests pass (feature:weather + core:data)
- [ ] @Preview functions render (compilation success)
- [ ] No hardcoded colors/sizes — all via MaterialTheme
- [ ] No 1px borders (No-Line rule)
- [ ] Gradient is 135° from primary → primaryContainer
- [ ] Decorative circles use Canvas + radialGradient (not Modifier.blur)
