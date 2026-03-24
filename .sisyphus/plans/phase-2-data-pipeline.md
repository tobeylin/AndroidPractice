# Phase 2 — Data Pipeline (`:core:model` + `:core:network` + `:core:data`)

## TL;DR

> **Quick Summary**: Build the complete data pipeline for an Android weather app — domain models, Retrofit network layer with OpenWeatherMap API integration, DTO→Model mapping with 5-day forecast aggregation, and Repository pattern with comprehensive unit tests. API key left as TODO.
>
> **Deliverables**:
> - Domain models (`City`, `CurrentWeather`, `DailyForecast`) in `:core:model`
> - Retrofit `WeatherApi` + DTO data classes in `:core:network`
> - OkHttp + Retrofit DI setup with API key interceptor in `:core:network`
> - `WeatherRepository` (interface + `DefaultWeatherRepository`) in `:core:data`
> - DTO→Model mapping + 5-day/3-hour→daily aggregation logic in `:core:data`
> - `FakeWeatherApi` + `FakeWeatherRepository` for testing
> - Comprehensive unit tests across all 3 core modules
>
> **Estimated Effort**: Medium
> **Parallel Execution**: YES — 3 waves
> **Critical Path**: Task 1 → Task 2 → Tasks 3-5 (parallel) → Tasks 6-7 (parallel) → Task 8 → Task 9

---

## Context

### Original Request
Implement Phase 2 of the weather app per `docs/phase-plan.md` — the Data Pipeline phase. API key to be left as TODO.

### Interview Summary
**Key Discussions**:
- Phase 2 scope: purely data layers (`:core:model`, `:core:network`, `:core:data`). No UI, no ViewModel, no Compose.
- OpenWeatherMap free tier 2.5 endpoints for current weather + 5-day/3-hour forecast
- API key mechanism (BuildConfig from `local.properties`) already exists from Phase 1 — key value left as TODO
- Hardcoded ~15 cities with lat/lon coordinates
- Fakes over Mocks for testing (per AGENTS.md)

**Research Findings**:
- Retrofit 3.0.0 now ships official `converter-kotlinx-serialization` — must replace Jake Wharton's `1.0.0` in version catalog
- OWM 2.5 `/forecast` returns 40 items (8/day × 5 days); `main.temp_min`/`temp_max` are per-3-hour-block ranges
- `dt_txt` format is `"yyyy-MM-dd HH:mm:ss"` in UTC — use first 10 chars for date grouping
- NiA pattern: DTOs in `:core:network`, mapping extensions in `:core:data`, plain data classes in `:core:model`

### Metis Review
**Identified Gaps** (all addressed in plan):
- **Wrong serialization converter artifact**: Jake Wharton's `1.0.0` incompatible with Retrofit 3.0.0 → Task 1 fixes this
- **Missing coroutines dependencies**: `kotlinx-coroutines-core` and `kotlinx-coroutines-test` not in version catalog → Task 1 adds them
- **Missing test dependencies in core modules**: No `testImplementation(libs.junit)` in any core module → Task 1 fixes
- **API key threading**: `BuildConfig.WEATHER_API_KEY` lives in `:app`, can't be imported by `:core:network` → Interceptor accepts key via constructor param; real wiring deferred to Phase 4
- **City list location**: `City` model in `:core:model`, hardcoded list in `:core:data`
- **DTO visibility**: DTOs and `WeatherApi` are `public` within `:core:network`; module boundary (Gradle `implementation`) prevents leaking to `:feature:weather`
- **Forecast aggregation edge cases**: Partial days, empty arrays, defensive `firstOrNull()` — all covered in test scenarios

---

## Work Objectives

### Core Objective
Build the complete data pipeline so that `./gradlew test` validates we can parse OpenWeatherMap API responses into clean domain models with correct 5-day forecast aggregation.

### Concrete Deliverables
- 3 domain model files in `core/model/src/main/kotlin/.../core/model/`
- 4+ DTO files in `core/network/src/main/java/.../core/network/`
- Retrofit interface + Hilt NetworkModule in `:core:network`
- Repository interface + implementation + Hilt DataModule in `:core:data`
- Mapping + aggregation logic in `:core:data`
- Fakes for both API and Repository layers
- 15+ unit tests across 3 modules

### Definition of Done
- [ ] `./gradlew assembleDebug` — 0 errors, all modules compile
- [ ] `./gradlew test` — all unit tests pass (≥15 tests)
- [ ] `./gradlew lintDebug` — 0 new lint errors
- [ ] `./gradlew :core:model:test` — model tests pass
- [ ] `./gradlew :core:network:test` — DTO deserialization tests pass
- [ ] `./gradlew :core:data:test` — mapping, aggregation, repository tests pass

### Must Have
- Domain models: `City`, `CurrentWeather`, `DailyForecast`
- Retrofit interface with `suspend` functions for current weather + forecast
- DTO data classes with `@Serializable` + `@SerialName` matching OWM JSON fields
- OkHttp Interceptor injecting API key as `appid` query parameter
- Hilt `@Module` for Retrofit/OkHttp (`NetworkModule`) and Repository binding (`DataModule`)
- 5-day/3-hour → daily aggregation: group by UTC date, compute max/min temp, pick dominant condition
- `FakeWeatherApi` (returns hardcoded DTOs) + `FakeWeatherRepository` (returns hardcoded models)
- Unit tests covering: DTO deserialization, DTO→Model mapping, forecast aggregation (full day, partial day, empty), repository behavior
- Hardcoded city list (~15 cities with name, country, lat, lon)

### Must NOT Have (Guardrails)
- ❌ Any UI code, ViewModel, Compose, or `@Preview` — Phase 3 concern
- ❌ Room, DataStore, or any local persistence — out of scope
- ❌ Coil or image loading — Phase 3/4 concern
- ❌ City search or geocoding API — hardcoded list only
- ❌ Real API calls in tests — fakes only, no `MockWebServer`
- ❌ New libraries without user approval (especially Turbine, MockK) — use basic `Flow.first()`/`runTest`
- ❌ `@Serializable` on domain models — only on DTOs
- ❌ Error wrapper types (`Result`, `NetworkResult`) — let `suspend` functions throw; ViewModel handles errors in Phase 3
- ❌ Hilt wiring that requires `:app` changes — NetworkModule/DataModule are self-contained; actual API key injection from `:app` is Phase 4
- ❌ Over-modeled domain (no wind direction, visibility, sunrise/sunset, UV index, etc.) — only fields the UI will display

---

## Verification Strategy

> **ZERO HUMAN INTERVENTION** — ALL verification is agent-executed. No exceptions.

### Test Decision
- **Infrastructure exists**: YES (JUnit 4 in `:app`; needs to be added to core modules — Task 1)
- **Automated tests**: YES (Tests-after, interleaved — each task includes its own tests)
- **Framework**: JUnit 4 + kotlinx-coroutines-test for `runTest`
- **TDD-lite**: Write tests alongside implementation within each task; test files and source files in same task

### QA Policy
Every task MUST include agent-executed QA scenarios.
Evidence saved to `.sisyphus/evidence/task-{N}-{scenario-slug}.{ext}`.

- **Module compilation**: Use Bash (`./gradlew :<module>:assembleDebug`)
- **Unit tests**: Use Bash (`./gradlew :<module>:test`)
- **Lint checks**: Use Bash (`./gradlew lintDebug`)
- **Diagnostics**: Use `lsp_diagnostics` on source directories

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Foundation — must go first):
└── Task 1: Fix version catalog + add missing dependencies [quick]

Wave 2 (Core layers — MAX PARALLEL after Wave 1):
├── Task 2: Domain models in :core:model [quick]
├── Task 3: DTO data classes in :core:network [unspecified-high]
└── Task 4: Hardcoded city list in :core:data [quick]

Wave 3 (Network setup — after Wave 2):
├── Task 5: Retrofit WeatherApi interface + FakeWeatherApi [unspecified-high]
└── Task 6: NetworkModule (OkHttp + Retrofit DI) [quick]

Wave 4 (Data layer — after Wave 2, 3):
├── Task 7: DTO→Model mapping functions [unspecified-high]
└── Task 8: Forecast aggregation logic [deep]

Wave 5 (Repository — after Wave 4):
└── Task 9: WeatherRepository + FakeWeatherRepository + DataModule [unspecified-high]

Wave FINAL (Verification — after ALL tasks):
├── Task F1: Plan compliance audit [oracle]
├── Task F2: Code quality review [unspecified-high]
├── Task F3: Full test suite QA [unspecified-high]
└── Task F4: Scope fidelity check [deep]
-> Present results -> Get explicit user okay
```

### Dependency Matrix

| Task | Depends On | Blocks | Wave |
|------|-----------|--------|------|
| 1 | — | 2, 3, 4, 5, 6, 7, 8, 9 | 1 |
| 2 | 1 | 5, 7, 8, 9 | 2 |
| 3 | 1 | 5, 7, 8, 9 | 2 |
| 4 | 1 | 9 | 2 |
| 5 | 2, 3 | 6, 9 | 3 |
| 6 | 5 | 9 | 3 |
| 7 | 2, 3 | 9 | 4 |
| 8 | 2, 3 | 9 | 4 |
| 9 | 4, 5, 6, 7, 8 | F1-F4 | 5 |

### Agent Dispatch Summary

- **Wave 1**: 1 task — T1 → `quick`
- **Wave 2**: 3 tasks — T2 → `quick`, T3 → `unspecified-high`, T4 → `quick`
- **Wave 3**: 2 tasks — T5 → `unspecified-high`, T6 → `quick`
- **Wave 4**: 2 tasks — T7 → `unspecified-high`, T8 → `deep`
- **Wave 5**: 1 task — T9 → `unspecified-high`
- **FINAL**: 4 tasks — F1 → `oracle`, F2 → `unspecified-high`, F3 → `unspecified-high`, F4 → `deep`

---

## TODOs

- [x] 1. Fix version catalog + add missing dependencies

  **What to do**:
  - In `gradle/libs.versions.toml`:
    - Replace `retrofitKotlinxSerializer = "1.0.0"` with removal of this version entry
    - Change `retrofit-kotlinx-serialization` library from `{ group = "com.jakewharton.retrofit", name = "retrofit2-kotlinx-serialization-converter", version.ref = "retrofitKotlinxSerializer" }` to `{ group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }` (uses same version `3.0.0` as Retrofit itself)
    - Add version: `coroutines = "1.10.1"`
    - Add library: `kotlinx-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutines" }`
    - Add library: `kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }`
  - In `core/model/build.gradle.kts`:
    - Add `testImplementation(libs.junit)` in a `dependencies {}` block
  - In `core/network/build.gradle.kts`:
    - Add `testImplementation(libs.junit)`
    - Add `testImplementation(libs.kotlinx.serialization.json)` (ensure serialization available in tests)
  - In `core/data/build.gradle.kts`:
    - Add `implementation(libs.kotlinx.coroutines.core)` (for `Flow`/`suspend` in repository)
    - Add `testImplementation(libs.junit)`
    - Add `testImplementation(libs.kotlinx.coroutines.test)` (for `runTest`)
  - Verify the existing `core/network/build.gradle.kts` dependency `implementation(libs.retrofit.kotlinx.serialization)` still resolves after the catalog change

  **Must NOT do**:
  - Do NOT add Turbine, MockK, MockWebServer, or any unapproved library
  - Do NOT change any plugin versions
  - Do NOT modify `:app` or `:feature:weather` build files

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Pure build config edits — version catalog + build.gradle.kts changes, no complex logic
  - **Skills**: []
    - No specialized skills needed for Gradle config edits

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 1 (solo)
  - **Blocks**: Tasks 2, 3, 4, 5, 6, 7, 8, 9
  - **Blocked By**: None (can start immediately)

  **References**:

  **Pattern References**:
  - `gradle/libs.versions.toml` — Current version catalog with all existing entries. The `retrofitKotlinxSerializer` version (line 15) and `retrofit-kotlinx-serialization` library (line 36) need updating. Add new entries after existing ones.
  - `core/network/build.gradle.kts:38-48` — Current dependencies block showing `implementation(libs.retrofit.kotlinx.serialization)` on line 42. This reference must still resolve after catalog change.
  - `core/data/build.gradle.kts:25-30` — Current dependencies block. Add coroutines + test deps here.
  - `core/model/build.gradle.kts` — Currently only 3 lines (just the JVM plugin). Needs a `dependencies {}` block added.

  **External References**:
  - Retrofit 3.0.0 changelog confirms `com.squareup.retrofit2:converter-kotlinx-serialization:3.0.0` is the official artifact
  - Kotlinx Coroutines 1.10.1 is the latest stable compatible with Kotlin 2.1.0

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: All modules compile after dependency changes
    Tool: Bash
    Preconditions: Version catalog and build files have been updated
    Steps:
      1. Run `./gradlew assembleDebug` — full project build
      2. Check exit code is 0
      3. Verify output contains "BUILD SUCCESSFUL"
    Expected Result: All 5 modules compile without errors
    Failure Indicators: Any "Could not resolve" or "Unresolved reference" errors
    Evidence: .sisyphus/evidence/task-1-assemble-debug.txt

  Scenario: Dependency resolution succeeds for new artifacts
    Tool: Bash
    Preconditions: Version catalog updated
    Steps:
      1. Run `./gradlew :core:network:dependencies --configuration debugCompileClasspath | grep converter-kotlinx-serialization`
      2. Verify output shows `com.squareup.retrofit2:converter-kotlinx-serialization:3.0.0` (NOT jakewharton)
      3. Run `./gradlew :core:data:dependencies --configuration debugCompileClasspath | grep coroutines-core`
      4. Verify output shows `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1`
    Expected Result: Correct artifacts resolved at correct versions
    Failure Indicators: Jake Wharton artifact still present, or coroutines missing
    Evidence: .sisyphus/evidence/task-1-dependency-resolution.txt
  ```

  **Commit**: YES (Commit 1)
  - Message: `build: fix version catalog and add missing dependencies for data pipeline`
  - Files: `gradle/libs.versions.toml`, `core/model/build.gradle.kts`, `core/network/build.gradle.kts`, `core/data/build.gradle.kts`
  - Pre-commit: `./gradlew assembleDebug`

- [x] 2. Domain models in `:core:model`

  **What to do**:
  - Create `core/model/src/main/kotlin/playground/app/tobeylin/weather/core/model/City.kt`:
    ```
    data class City(val name: String, val country: String, val latitude: Double, val longitude: Double)
    ```
  - Create `core/model/src/main/kotlin/playground/app/tobeylin/weather/core/model/CurrentWeather.kt`:
    ```
    data class CurrentWeather(
        val temperature: Double,       // Celsius
        val feelsLike: Double,         // Celsius
        val humidity: Int,             // percentage
        val windSpeed: Double,         // m/s
        val condition: String,         // e.g. "Clouds", "Rain"
        val conditionDescription: String, // e.g. "scattered clouds"
        val iconCode: String,          // e.g. "04d" — for Phase 3 icon loading
        val timestamp: Long,           // Unix timestamp
    )
    ```
  - Create `core/model/src/main/kotlin/playground/app/tobeylin/weather/core/model/DailyForecast.kt`:
    ```
    data class DailyForecast(
        val date: String,              // "yyyy-MM-dd" (UTC)
        val tempMax: Double,           // Celsius — daily high
        val tempMin: Double,           // Celsius — daily low
        val condition: String,         // dominant condition of the day
        val iconCode: String,          // icon from dominant condition
    )
    ```
  - Create `core/model/src/test/kotlin/playground/app/tobeylin/weather/core/model/ModelTest.kt`:
    - Test `City` data class construction and equality
    - Test `CurrentWeather` data class construction
    - Test `DailyForecast` data class construction
    - Test `copy()` behavior (changed field, others preserved)

  **Must NOT do**:
  - Do NOT add `@Serializable` or any kotlinx.serialization annotations
  - Do NOT add any Android framework imports — this is a pure Kotlin JVM module
  - Do NOT add fields beyond what the UI will need (no pressure, visibility, sunrise, etc.)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Simple data class creation + basic tests — straightforward Kotlin
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Tasks 3, 4)
  - **Blocks**: Tasks 5, 7, 8, 9
  - **Blocked By**: Task 1

  **References**:

  **Pattern References**:
  - `core/model/build.gradle.kts` — Pure Kotlin JVM module (no Android). Source dir is `src/main/kotlin/`, test dir is `src/test/kotlin/`.
  - `docs/phase-plan.md:85` — "定義 domain models（City, CurrentWeather, DailyForecast）於 :core:model"
  - `AGENTS.md` "Architecture > Data Layer" section — "Data exposed from repositories must be immutable" → use `val` only in data classes

  **External References**:
  - OpenWeatherMap 2.5 Current Weather response fields: temp, feels_like, humidity, wind speed, weather[0].main/description/icon
  - OpenWeatherMap 2.5 Forecast response: same fields per 3-hour block + dt_txt for date

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Model tests pass
    Tool: Bash
    Preconditions: Model files created in correct package
    Steps:
      1. Run `./gradlew :core:model:test`
      2. Check exit code is 0
      3. Verify output contains "BUILD SUCCESSFUL"
    Expected Result: All model tests pass
    Failure Indicators: Compilation errors or test failures
    Evidence: .sisyphus/evidence/task-2-model-tests.txt

  Scenario: Models have no framework dependencies
    Tool: Bash
    Preconditions: Model files exist
    Steps:
      1. Use grep to search for `import android` or `import androidx` or `@Serializable` in `core/model/src/main/`
      2. Verify zero matches
    Expected Result: No Android or serialization framework imports in domain models
    Failure Indicators: Any framework import found
    Evidence: .sisyphus/evidence/task-2-no-framework-deps.txt
  ```

  **Commit**: YES (groups with Task 4 in Commit 2)
  - Message: `feat(model): add domain models and hardcoded city list`
  - Files: `core/model/src/main/kotlin/.../City.kt`, `CurrentWeather.kt`, `DailyForecast.kt`, `core/model/src/test/.../ModelTest.kt`
  - Pre-commit: `./gradlew :core:model:test`

- [x] 3. DTO data classes + deserialization tests in `:core:network`

  **What to do**:
  - Create `core/network/src/main/java/playground/app/tobeylin/weather/core/network/model/NetworkCurrentWeatherResponse.kt`:
    - Top-level response: `@Serializable data class NetworkCurrentWeatherResponse(coord, weather list, main, wind, dt, name)`
    - Nested `@Serializable data class NetworkWeatherItem(@SerialName("id") val id: Int, @SerialName("main") val main: String, @SerialName("description") val description: String, @SerialName("icon") val icon: String)`
    - Nested `@Serializable data class NetworkMain(@SerialName("temp") val temp: Double, @SerialName("feels_like") val feelsLike: Double, @SerialName("temp_min") val tempMin: Double, @SerialName("temp_max") val tempMax: Double, @SerialName("humidity") val humidity: Int)`
    - Nested `@Serializable data class NetworkWind(@SerialName("speed") val speed: Double)`
    - Nested `@Serializable data class NetworkCoord(@SerialName("lat") val lat: Double, @SerialName("lon") val lon: Double)`
  - Create `core/network/src/main/java/playground/app/tobeylin/weather/core/network/model/NetworkForecastResponse.kt`:
    - Top-level: `@Serializable data class NetworkForecastResponse(@SerialName("list") val items: List<NetworkForecastItem>, val city: NetworkCity)`
    - `@Serializable data class NetworkForecastItem(@SerialName("dt") val dt: Long, @SerialName("main") val main: NetworkMain, @SerialName("weather") val weather: List<NetworkWeatherItem>, @SerialName("dt_txt") val dtTxt: String)`
    - `@Serializable data class NetworkCity(@SerialName("name") val name: String, @SerialName("country") val country: String, @SerialName("timezone") val timezone: Int)` — timezone is UTC offset in seconds
    - Reuse `NetworkMain`, `NetworkWeatherItem` from current weather response (import from same package)
  - Create `core/network/src/test/java/playground/app/tobeylin/weather/core/network/model/NetworkCurrentWeatherResponseTest.kt`:
    - Test: deserialize a realistic JSON string (based on actual OWM response) into `NetworkCurrentWeatherResponse`
    - Assert all fields are mapped correctly (temp, feels_like, humidity, weather[0].main, etc.)
    - Test: JSON with unknown fields is ignored (verify `ignoreUnknownKeys` works with the DTO)
  - Create `core/network/src/test/java/playground/app/tobeylin/weather/core/network/model/NetworkForecastResponseTest.kt`:
    - Test: deserialize a realistic forecast JSON (include 3-4 items, not all 40) into `NetworkForecastResponse`
    - Assert `items.size`, `items[0].dtTxt`, `items[0].main.temp`, `items[0].weather[0].main`
    - Test: empty weather array in an item doesn't crash (defensive coding)
    - Test: missing optional fields use defaults gracefully

  **Must NOT do**:
  - Do NOT make DTOs `internal` — they need to be accessible by `:core:data` (which depends on `:core:network` via Gradle `implementation`)
  - Do NOT add any mapping logic here — mapping lives in `:core:data`
  - Do NOT make real API calls — tests use `Json.decodeFromString()` only

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Multiple DTO classes with precise `@SerialName` mappings to OWM JSON + comprehensive deserialization tests require careful attention
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Tasks 2, 4)
  - **Blocks**: Tasks 5, 7, 8, 9
  - **Blocked By**: Task 1

  **References**:

  **Pattern References**:
  - `core/network/build.gradle.kts:3` — `alias(libs.plugins.kotlin.serialization)` is already applied, enabling `@Serializable`
  - `core/network/build.gradle.kts:44` — `implementation(libs.kotlinx.serialization.json)` already present

  **External References**:
  - OpenWeatherMap Current Weather API response: https://openweathermap.org/current#example_JSON
    Key fields: `coord{lat,lon}`, `weather[{id,main,description,icon}]`, `main{temp,feels_like,temp_min,temp_max,humidity}`, `wind{speed}`, `dt`, `name`
  - OpenWeatherMap 5-Day Forecast API response: https://openweathermap.org/forecast5#JSON
    Key fields: `list[{dt,main{...},weather[{...}],dt_txt}]`, `city{name,country,timezone}`
  - Sample OWM current weather JSON for tests:
    ```json
    {"coord":{"lon":121.5654,"lat":25.033},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03d"}],"main":{"temp":28.5,"feels_like":31.2,"temp_min":27.0,"temp_max":30.0,"humidity":70},"wind":{"speed":3.5},"dt":1711296000,"name":"Taipei"}
    ```
  - Sample OWM forecast JSON for tests:
    ```json
    {"list":[{"dt":1711296000,"main":{"temp":28.5,"feels_like":31.2,"temp_min":27.0,"temp_max":30.0,"humidity":70},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03d"}],"dt_txt":"2026-03-24 12:00:00"}],"city":{"name":"Taipei","country":"TW","timezone":28800}}
    ```

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: DTO deserialization tests pass
    Tool: Bash
    Preconditions: DTO classes and test files created
    Steps:
      1. Run `./gradlew :core:network:test`
      2. Check exit code is 0
      3. Verify test report shows ≥ 4 tests passed
    Expected Result: All deserialization tests pass
    Failure Indicators: SerializationException, missing @SerialName, wrong field type
    Evidence: .sisyphus/evidence/task-3-dto-tests.txt

  Scenario: DTOs handle unknown JSON fields gracefully
    Tool: Bash
    Preconditions: Tests include JSON with extra unknown fields
    Steps:
      1. Verify test for unknown fields exists in test file
      2. Run the specific test
    Expected Result: Deserialization succeeds despite extra JSON fields
    Failure Indicators: UnknownFieldException thrown
    Evidence: .sisyphus/evidence/task-3-unknown-fields.txt
  ```

  **Commit**: YES (groups with Task 5 in Commit 3)
  - Message: `feat(network): add WeatherApi, DTOs, and FakeWeatherApi with deserialization tests`
  - Files: `core/network/src/main/java/.../model/NetworkCurrentWeatherResponse.kt`, `NetworkForecastResponse.kt`, test files
  - Pre-commit: `./gradlew :core:network:test`

- [x] 4. Hardcoded city list in `:core:data`

  **What to do**:
  - Create `core/data/src/main/java/playground/app/tobeylin/weather/core/data/CityRepository.kt`:
    - Define `interface CityRepository` with `fun getCities(): List<City>`
    - Define `internal class DefaultCityRepository @Inject constructor() : CityRepository`
    - Hardcode ~15 cities covering: Asia (Taipei, Tokyo, Seoul, Bangkok, Singapore, Hong Kong), Europe (London, Paris, Berlin, Rome), Americas (New York, Los Angeles, São Paulo, Toronto), Oceania (Sydney)
    - Each city: `City(name = "Taipei", country = "TW", latitude = 25.033, longitude = 121.565)`
  - Create `core/data/src/test/java/playground/app/tobeylin/weather/core/data/CityRepositoryTest.kt`:
    - Test: `getCities()` returns non-empty list
    - Test: each city has valid lat/lon ranges (lat: -90..90, lon: -180..180)
    - Test: no duplicate city names

  **Must NOT do**:
  - Do NOT use a JSON file or resource for the city list — hardcoded Kotlin is simpler
  - Do NOT add geocoding or search API

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Simple interface + hardcoded constant list + basic tests
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Tasks 2, 3)
  - **Blocks**: Task 9
  - **Blocked By**: Task 1

  **References**:

  **Pattern References**:
  - `core/data/build.gradle.kts:26-27` — Already depends on `:core:model` (for `City` class) and `:core:network`
  - `docs/phase-plan.md:23` — "城市清單: Hardcoded ~15 城市"

  **External References**:
  - City coordinates (lat/lon) can be found at https://openweathermap.org/current — use major world cities

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: City list tests pass
    Tool: Bash
    Preconditions: CityRepository created with hardcoded cities
    Steps:
      1. Run `./gradlew :core:data:test --tests "*.CityRepositoryTest"`
      2. Check exit code is 0
    Expected Result: All city tests pass, ≥15 cities returned
    Failure Indicators: Empty list, invalid coordinates
    Evidence: .sisyphus/evidence/task-4-city-tests.txt

  Scenario: City coordinates are valid
    Tool: Bash
    Preconditions: Tests assert lat/lon ranges
    Steps:
      1. Verify test checks all cities have lat in -90..90 and lon in -180..180
      2. Run the test
    Expected Result: All coordinates within valid range
    Failure Indicators: Any city with invalid coordinates
    Evidence: .sisyphus/evidence/task-4-city-coords.txt
  ```

  **Commit**: YES (groups with Task 2 in Commit 2)
  - Message: `feat(model): add domain models and hardcoded city list`
  - Files: `core/data/src/main/java/.../CityRepository.kt`, `core/data/src/test/.../CityRepositoryTest.kt`
  - Pre-commit: `./gradlew :core:data:test --tests "*.CityRepositoryTest"`

- [x] 5. Retrofit `WeatherApi` interface + `FakeWeatherApi`

  **What to do**:
  - Create `core/network/src/main/java/playground/app/tobeylin/weather/core/network/WeatherApi.kt`:
    ```kotlin
    interface WeatherApi {
        @GET("data/2.5/weather")
        suspend fun getCurrentWeather(
            @Query("lat") latitude: Double,
            @Query("lon") longitude: Double,
            @Query("units") units: String = "metric",
        ): NetworkCurrentWeatherResponse

        @GET("data/2.5/forecast")
        suspend fun getForecast(
            @Query("lat") latitude: Double,
            @Query("lon") longitude: Double,
            @Query("units") units: String = "metric",
        ): NetworkForecastResponse
    }
    ```
    - Import Retrofit annotations: `retrofit2.http.GET`, `retrofit2.http.Query`
    - Note: `appid` is NOT a parameter — it's injected by OkHttp Interceptor
  - Create `core/network/src/main/java/playground/app/tobeylin/weather/core/network/FakeWeatherApi.kt`:
    - `class FakeWeatherApi : WeatherApi` — implements both `suspend` functions
    - Returns hardcoded `NetworkCurrentWeatherResponse` and `NetworkForecastResponse` objects
    - The fake forecast response should include items spanning 2+ days (to test aggregation downstream)
    - Include a way to simulate errors: `var shouldThrowError = false` — if true, throw `IOException("Fake network error")`

  **Must NOT do**:
  - Do NOT add `appid` query parameter to the interface — Interceptor handles it
  - Do NOT add any actual HTTP client setup here — that's Task 6 (NetworkModule)

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Retrofit interface design + comprehensive fake implementation with error simulation
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 3 (with Task 6)
  - **Blocks**: Tasks 6, 9
  - **Blocked By**: Tasks 2, 3

  **References**:

  **Pattern References**:
  - `core/network/build.gradle.kts:41` — `implementation(libs.retrofit.core)` already present for Retrofit annotations
  - Task 3 deliverables — `NetworkCurrentWeatherResponse`, `NetworkForecastResponse` DTOs (must be created first)
  - Task 2 deliverables — Domain models (referenced indirectly through DTOs)

  **External References**:
  - Retrofit `@GET`/`@Query` annotation docs: https://square.github.io/retrofit/
  - OWM API: base URL is `https://api.openweathermap.org/`, current weather path is `data/2.5/weather`, forecast path is `data/2.5/forecast`
  - Required query params: `lat`, `lon`, `appid` (interceptor), `units=metric`

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: WeatherApi interface compiles with correct annotations
    Tool: Bash
    Preconditions: WeatherApi.kt created with Retrofit annotations
    Steps:
      1. Run `./gradlew :core:network:assembleDebug`
      2. Use lsp_diagnostics on WeatherApi.kt to check for errors
    Expected Result: Compiles without errors, all Retrofit annotations resolve
    Failure Indicators: Unresolved reference to GET, Query, or DTO types
    Evidence: .sisyphus/evidence/task-5-api-compiles.txt

  Scenario: FakeWeatherApi returns valid data
    Tool: Bash
    Preconditions: FakeWeatherApi created
    Steps:
      1. Verify FakeWeatherApi implements WeatherApi interface
      2. Verify getCurrentWeather returns a valid NetworkCurrentWeatherResponse
      3. Verify getForecast returns a response with items spanning 2+ days
      4. Run `./gradlew :core:network:assembleDebug`
    Expected Result: Fake compiles and has multi-day forecast data
    Failure Indicators: Type mismatch, missing override, single-day data only
    Evidence: .sisyphus/evidence/task-5-fake-api.txt
  ```

  **Commit**: YES (groups with Task 3 in Commit 3)
  - Message: `feat(network): add WeatherApi, DTOs, and FakeWeatherApi with deserialization tests`
  - Files: `core/network/src/main/java/.../WeatherApi.kt`, `FakeWeatherApi.kt`
  - Pre-commit: `./gradlew :core:network:assembleDebug`

- [x] 6. NetworkModule — OkHttp + Retrofit DI setup

  **What to do**:
  - Create `core/network/src/main/java/playground/app/tobeylin/weather/core/network/di/NetworkModule.kt`:
    ```kotlin
    @Module
    @InstallIn(SingletonComponent::class)
    internal object NetworkModule {

        @Provides
        @Singleton
        fun providesNetworkJson(): Json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        @Provides
        @Singleton
        fun providesOkHttpClient(): OkHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(ApiKeyInterceptor(apiKey = "")) // TODO: inject real API key from :app in Phase 4
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
                )
                .build()

        @Provides
        @Singleton
        fun providesRetrofit(
            networkJson: Json,
            okHttpClient: OkHttpClient,
        ): Retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(okHttpClient)
            .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
            .build()

        @Provides
        @Singleton
        fun providesWeatherApi(retrofit: Retrofit): WeatherApi =
            retrofit.create(WeatherApi::class.java)
    }
    ```
  - Create `core/network/src/main/java/playground/app/tobeylin/weather/core/network/ApiKeyInterceptor.kt`:
    ```kotlin
    internal class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val url = originalRequest.url.newBuilder()
                .addQueryParameter("appid", apiKey)
                .build()
            return chain.proceed(originalRequest.newBuilder().url(url).build())
        }
    }
    ```
    - The `apiKey` parameter is `""` (empty) for now — TODO comment for Phase 4

  **Must NOT do**:
  - Do NOT hardcode a real API key
  - Do NOT create any Hilt wiring in `:app` module — Phase 4 concern
  - Do NOT add authentication header — OWM uses query parameter `appid`, not header

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Boilerplate Hilt module with standard Retrofit/OkHttp setup — well-documented pattern
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 3 (with Task 5)
  - **Blocks**: Task 9
  - **Blocked By**: Task 5

  **References**:

  **Pattern References**:
  - `core/network/build.gradle.kts:5` — `alias(libs.plugins.hilt)` already applied
  - `core/network/build.gradle.kts:41-44` — Retrofit, OkHttp, and Kotlinx Serialization dependencies all present
  - `app/src/main/java/.../WeatherApplication.kt` — `@HiltAndroidApp` already configured
  - Task 5 deliverables — `WeatherApi` interface (this module `@Provides` it via Retrofit)

  **External References**:
  - NiA NetworkModule pattern: Provides Json, OkHttpClient, Retrofit as @Singleton
  - OkHttp Interceptor API: `chain.request().url.newBuilder().addQueryParameter()`
  - `Json.asConverterFactory()` from `kotlinx.serialization.json.okio` extension

  **WHY Each Reference Matters**:
  - NiA pattern: canonical approach for Hilt + Retrofit in multi-module Android app
  - OkHttp Interceptor: OWM authenticates via `appid` query param, interceptor adds it transparently

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: NetworkModule compiles and Hilt processes it
    Tool: Bash
    Preconditions: NetworkModule.kt and ApiKeyInterceptor.kt created
    Steps:
      1. Run `./gradlew :core:network:assembleDebug`
      2. Check for KSP/Hilt generated files in build output (optional)
      3. Verify no compilation errors
    Expected Result: Module compiles with Hilt annotation processing
    Failure Indicators: KSP errors, missing imports, Hilt component errors
    Evidence: .sisyphus/evidence/task-6-network-module-compiles.txt

  Scenario: No hardcoded API key in source
    Tool: Bash
    Preconditions: All network source files exist
    Steps:
      1. Grep all `.kt` files in `core/network/src/main/` for strings that look like API keys (32+ char hex strings)
      2. Verify ApiKeyInterceptor uses constructor param, not hardcoded value
    Expected Result: No hardcoded API keys found; interceptor receives key via param
    Failure Indicators: Hardcoded hex string found in source
    Evidence: .sisyphus/evidence/task-6-no-hardcoded-key.txt
  ```

  **Commit**: YES (Commit 4)
  - Message: `feat(network): add NetworkModule with Retrofit and OkHttp DI setup`
  - Files: `core/network/src/main/java/.../di/NetworkModule.kt`, `ApiKeyInterceptor.kt`
  - Pre-commit: `./gradlew :core:network:assembleDebug`

- [x] 7. DTO→Model mapping functions in `:core:data`

  **What to do**:
  - Create `core/data/src/main/java/playground/app/tobeylin/weather/core/data/mapper/WeatherMapper.kt`:
    - `internal fun NetworkCurrentWeatherResponse.asExternalModel(): CurrentWeather` — maps:
      - `main.temp` → `temperature`
      - `main.feelsLike` → `feelsLike`
      - `main.humidity` → `humidity`
      - `wind.speed` → `windSpeed`
      - `weather.firstOrNull()?.main ?: "Unknown"` → `condition` (defensive!)
      - `weather.firstOrNull()?.description ?: ""` → `conditionDescription`
      - `weather.firstOrNull()?.icon ?: ""` → `iconCode`
      - `dt` → `timestamp`
    - `internal fun NetworkForecastItem.asExternalModel(): DailyForecast` — single item mapping (used internally by aggregator)
  - Create `core/data/src/test/java/playground/app/tobeylin/weather/core/data/mapper/WeatherMapperTest.kt`:
    - Test: map a complete `NetworkCurrentWeatherResponse` → verify all `CurrentWeather` fields
    - Test: map with empty `weather` list → condition is "Unknown", description/icon are empty strings
    - Test: map with multiple items in `weather` list → uses first item (index 0)

  **Must NOT do**:
  - Do NOT put mapping logic in `:core:network` — it belongs in `:core:data`
  - Do NOT add any error handling/wrapping — just map fields directly, let exceptions propagate

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Precise field-by-field mapping with edge case handling + tests
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 4 (with Task 8)
  - **Blocks**: Task 9
  - **Blocked By**: Tasks 2, 3

  **References**:

  **Pattern References**:
  - Task 2 deliverables — `CurrentWeather` model (target of mapping)
  - Task 3 deliverables — `NetworkCurrentWeatherResponse`, `NetworkForecastItem` DTOs (source of mapping)
  - `core/data/build.gradle.kts:26-27` — Depends on `:core:model` and `:core:network`

  **External References**:
  - NiA mapping pattern: `internal fun NetworkNewsResource.asExternalModel() = NewsResource(...)` — extension function in data layer

  **WHY Each Reference Matters**:
  - Task 2/3 deliverables define exact field names and types on both sides of the mapping
  - NiA pattern shows the canonical approach: extension function named `asExternalModel()`

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Mapper tests pass
    Tool: Bash
    Preconditions: WeatherMapper.kt and test file created
    Steps:
      1. Run `./gradlew :core:data:test --tests "*.WeatherMapperTest"`
      2. Check exit code is 0
    Expected Result: All mapping tests pass, including empty weather list edge case
    Failure Indicators: NullPointerException on empty weather list, wrong field mapping
    Evidence: .sisyphus/evidence/task-7-mapper-tests.txt

  Scenario: Mapping handles empty weather array defensively
    Tool: Bash
    Preconditions: Test for empty weather list exists
    Steps:
      1. Verify test creates a NetworkCurrentWeatherResponse with weather = emptyList()
      2. Run the test
      3. Verify mapped condition is "Unknown"
    Expected Result: No crash, condition defaults to "Unknown"
    Failure Indicators: IndexOutOfBoundsException or NPE
    Evidence: .sisyphus/evidence/task-7-empty-weather.txt
  ```

  **Commit**: YES (groups with Tasks 8, 9 in Commit 5)
  - Message: `feat(data): add WeatherRepository, DTO mapping, forecast aggregation with tests`
  - Files: `core/data/src/main/java/.../mapper/WeatherMapper.kt`, test file
  - Pre-commit: `./gradlew :core:data:test --tests "*.WeatherMapperTest"`

- [x] 8. Forecast aggregation logic (3-hour → daily)

  **What to do**:
  - Create `core/data/src/main/java/playground/app/tobeylin/weather/core/data/mapper/ForecastAggregator.kt`:
    - `internal fun List<NetworkForecastItem>.toDailyForecasts(): List<DailyForecast>`
    - Algorithm:
      1. Group items by UTC date: `groupBy { it.dtTxt.substring(0, 10) }` (extracts "yyyy-MM-dd")
      2. For each day's group:
         - `tempMax = items.maxOf { it.main.tempMax }`
         - `tempMin = items.minOf { it.main.tempMin }`
         - `condition` = mode (most frequent) of `weather.firstOrNull()?.main ?: "Unknown"` — first-seen wins ties
         - `iconCode` = icon from the item whose condition matches the dominant condition (pick first match)
      3. Sort result by date ascending
      4. Return `List<DailyForecast>`
    - Handle edge cases:
      - Empty input list → return empty list
      - Day with single item → valid output (tempMax = tempMin from that item is OK)
      - Partial first/last day (< 8 items) → still aggregated normally

  - Create `core/data/src/test/java/playground/app/tobeylin/weather/core/data/mapper/ForecastAggregatorTest.kt`:
    - **Test: Full day** — 8 items for same date, varying temps → correct max/min, dominant condition
    - **Test: Partial day** — 3 items for a date → still produces valid DailyForecast
    - **Test: Multiple days** — items spanning 3 days → produces 3 DailyForecast entries, sorted by date
    - **Test: Single item** — 1 item → 1 DailyForecast with that item's data
    - **Test: Empty list** — `emptyList()` → `emptyList()` returned
    - **Test: Dominant condition** — 5 "Clouds" + 3 "Rain" items → dominant is "Clouds"
    - **Test: Tie-breaking** — 4 "Clouds" + 4 "Rain" → first-seen wins (whichever appears first in the list)

  **Must NOT do**:
  - Do NOT use timezone conversion — group by UTC date (document as known limitation)
  - Do NOT filter out partial days — include all days in the result
  - Do NOT add `java.time` imports — simple string substring for date extraction

  **Recommended Agent Profile**:
  - **Category**: `deep`
    - Reason: Core business logic with multiple edge cases + 7 specific test scenarios — needs careful implementation
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 4 (with Task 7)
  - **Blocks**: Task 9
  - **Blocked By**: Tasks 2, 3

  **References**:

  **Pattern References**:
  - Task 3 deliverables — `NetworkForecastItem`, `NetworkMain`, `NetworkWeatherItem` DTOs (input to aggregation)
  - Task 2 deliverables — `DailyForecast` model (output of aggregation)

  **External References**:
  - OWM `dt_txt` format: `"2026-03-24 12:00:00"` — first 10 chars = date
  - OWM returns 40 items total, 8 per day (every 3 hours: 00, 03, 06, 09, 12, 15, 18, 21)
  - Librarian research: `items.groupBy { it.dt_txt.substring(0, 10) }` is the canonical grouping approach

  **WHY Each Reference Matters**:
  - DTO structure determines the exact field access paths (e.g., `it.main.tempMax`, `it.weather.firstOrNull()?.main`)
  - Understanding OWM's 3-hour interval pattern explains why partial days occur (first/last day of forecast window)

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Aggregation tests all pass
    Tool: Bash
    Preconditions: ForecastAggregator.kt and test file created
    Steps:
      1. Run `./gradlew :core:data:test --tests "*.ForecastAggregatorTest"`
      2. Check exit code is 0
      3. Verify test count ≥ 7
    Expected Result: All 7+ aggregation tests pass
    Failure Indicators: Incorrect max/min, wrong dominant condition, crash on edge cases
    Evidence: .sisyphus/evidence/task-8-aggregation-tests.txt

  Scenario: Empty forecast list doesn't crash
    Tool: Bash
    Preconditions: Test for empty list exists
    Steps:
      1. Verify test passes emptyList() to toDailyForecasts()
      2. Run the test
      3. Verify result is emptyList()
    Expected Result: Empty input → empty output, no exception
    Failure Indicators: NoSuchElementException, NPE, or non-empty result
    Evidence: .sisyphus/evidence/task-8-empty-forecast.txt

  Scenario: Partial day produces valid output
    Tool: Bash
    Preconditions: Test with < 8 items for a day exists
    Steps:
      1. Verify test creates 3 items for one date
      2. Run the test
      3. Verify DailyForecast has correct max/min from those 3 items
    Expected Result: Partial day aggregated correctly
    Failure Indicators: Wrong temp values or missing day
    Evidence: .sisyphus/evidence/task-8-partial-day.txt
  ```

  **Commit**: YES (groups with Tasks 7, 9 in Commit 5)
  - Message: `feat(data): add WeatherRepository, DTO mapping, forecast aggregation with tests`
  - Files: `core/data/src/main/java/.../mapper/ForecastAggregator.kt`, test file
  - Pre-commit: `./gradlew :core:data:test --tests "*.ForecastAggregatorTest"`

- [x] 9. WeatherRepository + FakeWeatherRepository + DataModule

  **What to do**:
  - Create `core/data/src/main/java/playground/app/tobeylin/weather/core/data/WeatherRepository.kt`:
    ```kotlin
    interface WeatherRepository {
        suspend fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeather
        suspend fun getDailyForecasts(latitude: Double, longitude: Double): List<DailyForecast>
    }
    ```
  - Create `core/data/src/main/java/playground/app/tobeylin/weather/core/data/DefaultWeatherRepository.kt`:
    ```kotlin
    internal class DefaultWeatherRepository @Inject constructor(
        private val weatherApi: WeatherApi,
    ) : WeatherRepository {
        override suspend fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeather =
            weatherApi.getCurrentWeather(latitude, longitude).asExternalModel()

        override suspend fun getDailyForecasts(latitude: Double, longitude: Double): List<DailyForecast> =
            weatherApi.getForecast(latitude, longitude).items.toDailyForecasts()
    }
    ```
    - Uses `asExternalModel()` from Task 7's WeatherMapper
    - Uses `toDailyForecasts()` from Task 8's ForecastAggregator
  - Create `core/data/src/main/java/playground/app/tobeylin/weather/core/data/FakeWeatherRepository.kt`:
    ```kotlin
    class FakeWeatherRepository : WeatherRepository {
        var currentWeatherResult: CurrentWeather = /* default hardcoded CurrentWeather */
        var dailyForecastsResult: List<DailyForecast> = /* default hardcoded list */
        var shouldThrowError: Boolean = false

        override suspend fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeather {
            if (shouldThrowError) throw IOException("Fake error")
            return currentWeatherResult
        }

        override suspend fun getDailyForecasts(latitude: Double, longitude: Double): List<DailyForecast> {
            if (shouldThrowError) throw IOException("Fake error")
            return dailyForecastsResult
        }
    }
    ```
    - Public class — intended for consumption by `:feature:weather` tests in Phase 3
  - Create `core/data/src/main/java/playground/app/tobeylin/weather/core/data/di/DataModule.kt`:
    ```kotlin
    @Module
    @InstallIn(SingletonComponent::class)
    internal abstract class DataModule {
        @Binds
        abstract fun bindsWeatherRepository(impl: DefaultWeatherRepository): WeatherRepository

        @Binds
        abstract fun bindsCityRepository(impl: DefaultCityRepository): CityRepository
    }
    ```
  - Create `core/data/src/test/java/playground/app/tobeylin/weather/core/data/DefaultWeatherRepositoryTest.kt`:
    - Test with `FakeWeatherApi`:
      - `getCurrentWeather()` → returns correctly mapped `CurrentWeather`
      - `getDailyForecasts()` → returns aggregated `List<DailyForecast>`
      - Error case: `FakeWeatherApi.shouldThrowError = true` → repository propagates `IOException`
    - Use `runTest` from kotlinx-coroutines-test for suspend function testing

  **Must NOT do**:
  - Do NOT add Room/DataStore offline caching — out of scope
  - Do NOT add `Flow` return types — use simple `suspend` functions (no SSOT pattern needed without local DB)
  - Do NOT create error wrapper types — let exceptions propagate naturally

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Repository pattern + Hilt binding + comprehensive tests with FakeWeatherApi + coroutine testing
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 5 (solo — depends on all previous tasks)
  - **Blocks**: F1-F4
  - **Blocked By**: Tasks 4, 5, 6, 7, 8

  **References**:

  **Pattern References**:
  - Task 5 deliverables — `WeatherApi` interface and `FakeWeatherApi` (injected into repository)
  - Task 7 deliverables — `asExternalModel()` mapping extension (used by repository)
  - Task 8 deliverables — `toDailyForecasts()` aggregation function (used by repository)
  - Task 4 deliverables — `CityRepository` interface and `DefaultCityRepository` (bound in DataModule)
  - `core/data/build.gradle.kts:28` — `implementation(libs.hilt.android)` already present for DI annotations

  **External References**:
  - NiA Repository pattern: interface + `Default*` implementation with `@Inject constructor`
  - NiA DataModule: `@Binds` to connect interface to implementation
  - `kotlinx-coroutines-test` `runTest` for testing suspend functions: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/

  **WHY Each Reference Matters**:
  - Task 5-8 deliverables are the exact dependencies this task composes together
  - NiA pattern shows `@Binds` for interface→impl binding (not `@Provides`)
  - `runTest` is required to call `suspend` functions in JUnit tests

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Repository tests pass with FakeWeatherApi
    Tool: Bash
    Preconditions: Repository, DataModule, and test file created
    Steps:
      1. Run `./gradlew :core:data:test --tests "*.DefaultWeatherRepositoryTest"`
      2. Check exit code is 0
      3. Verify ≥ 3 tests pass (success current, success forecast, error propagation)
    Expected Result: All repository tests pass
    Failure Indicators: Mapping errors, aggregation errors, missing runTest import
    Evidence: .sisyphus/evidence/task-9-repository-tests.txt

  Scenario: Full test suite across all modules
    Tool: Bash
    Preconditions: All tasks 1-9 complete
    Steps:
      1. Run `./gradlew test`
      2. Check exit code is 0
      3. Count total tests (should be ≥ 15)
      4. Run `./gradlew assembleDebug`
      5. Run `./gradlew lintDebug`
    Expected Result: All tests pass, build succeeds, lint clean
    Failure Indicators: Any test failure, build error, or lint error
    Evidence: .sisyphus/evidence/task-9-full-suite.txt

  Scenario: Error propagation from API to repository
    Tool: Bash
    Preconditions: Test with shouldThrowError=true exists
    Steps:
      1. Verify test sets FakeWeatherApi.shouldThrowError = true
      2. Verify test expects IOException to be thrown
      3. Run the test
    Expected Result: IOException from FakeWeatherApi propagates through repository
    Failure Indicators: Exception swallowed, wrong exception type
    Evidence: .sisyphus/evidence/task-9-error-propagation.txt
  ```

  **Commit**: YES (Commit 5)
  - Message: `feat(data): add WeatherRepository, DTO mapping, forecast aggregation with tests`
  - Files: `core/data/src/main/java/.../WeatherRepository.kt`, `DefaultWeatherRepository.kt`, `FakeWeatherRepository.kt`, `di/DataModule.kt`, test file
  - Pre-commit: `./gradlew test`

---

## Final Verification Wave (MANDATORY — after ALL implementation tasks)

> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.

- [ ] F1. **Plan Compliance Audit** — `oracle`
  Read the plan end-to-end. For each "Must Have": verify implementation exists (read file, run `./gradlew test`). For each "Must NOT Have": search codebase for forbidden patterns (Room imports, Compose imports in core modules, `@Serializable` in `:core:model`). Check evidence files exist in `.sisyphus/evidence/`. Compare deliverables against plan.
  Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT: APPROVE/REJECT`

- [ ] F2. **Code Quality Review** — `unspecified-high`
  Run `./gradlew lintDebug`. Review all new files for: `as Any`/`@Suppress`, empty catches, `println` in prod, commented-out code, unused imports. Check AI slop: excessive comments, over-abstraction, generic names (data/result/item/temp). Verify `internal` visibility on DTOs and mapping functions within their respective modules.
  Output: `Build [PASS/FAIL] | Lint [PASS/FAIL] | Tests [N pass/N fail] | Files [N clean/N issues] | VERDICT`

- [ ] F3. **Full Test Suite QA** — `unspecified-high`
  Run `./gradlew test --info` and capture output. Verify each module has tests: `:core:model:test`, `:core:network:test`, `:core:data:test`. Check test count ≥ 15. Verify forecast aggregation edge cases are tested (partial day, empty list, single item). Run `./gradlew assembleDebug` to confirm build. Save outputs to `.sisyphus/evidence/final-qa/`.
  Output: `Scenarios [N/N pass] | Test Count [N total] | Modules [3/3 tested] | VERDICT`

- [ ] F4. **Scope Fidelity Check** — `deep`
  For each task: read "What to do", read actual files. Verify 1:1 — everything in spec was built (no missing), nothing beyond spec was built (no creep). Check "Must NOT" compliance: no UI code, no Room, no Coil, no `@Serializable` on domain models. Flag any file not mentioned in the plan. Verify module dependency direction: `:core:data` → `:core:network` → `:core:model` (no reverse).
  Output: `Tasks [N/N compliant] | Scope Creep [CLEAN/N issues] | Module Dependencies [CORRECT/ISSUES] | VERDICT`

---

## Commit Strategy

| Commit | Scope | Message | Verify |
|--------|-------|---------|--------|
| 1 | Task 1 | `build: fix version catalog and add missing dependencies for data pipeline` | `./gradlew assembleDebug` |
| 2 | Tasks 2, 4 | `feat(model): add domain models and hardcoded city list` | `./gradlew :core:model:test` |
| 3 | Tasks 3, 5 | `feat(network): add WeatherApi, DTOs, and FakeWeatherApi with deserialization tests` | `./gradlew :core:network:test` |
| 4 | Task 6 | `feat(network): add NetworkModule with Retrofit and OkHttp DI setup` | `./gradlew :core:network:assembleDebug` |
| 5 | Tasks 7, 8, 9 | `feat(data): add WeatherRepository, DTO mapping, forecast aggregation with tests` | `./gradlew test` |

---

## Success Criteria

### Verification Commands
```bash
./gradlew assembleDebug       # Expected: BUILD SUCCESSFUL
./gradlew test                # Expected: BUILD SUCCESSFUL, ≥15 tests pass
./gradlew lintDebug           # Expected: BUILD SUCCESSFUL, 0 new errors
./gradlew :core:model:test    # Expected: BUILD SUCCESSFUL
./gradlew :core:network:test  # Expected: BUILD SUCCESSFUL
./gradlew :core:data:test     # Expected: BUILD SUCCESSFUL
```

### Final Checklist
- [ ] All "Must Have" items present and verified
- [ ] All "Must NOT Have" items absent from codebase
- [ ] All unit tests pass across 3 core modules
- [ ] DTO deserialization tests verify correct JSON→Kotlin mapping
- [ ] Forecast aggregation handles: full day (8 items), partial day, single item, empty list
- [ ] No DTOs visible outside `:core:data` module boundary
- [ ] Domain models are plain data classes with no framework annotations
- [ ] API key interceptor accepts key as constructor param (actual value is TODO)
- [ ] `FakeWeatherApi` + `FakeWeatherRepository` ready for Phase 3 consumption
