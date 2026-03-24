# AGENTS.md — PlaygroundWeather

## Project Overview

Android weather app. Single-module Kotlin project using **Jetpack Compose** for UI.

- **Package**: `playground.app.tobeylin.weather`
- **Min SDK**: 24 (Android 7.0) | **Target SDK**: 36 | **Compile SDK**: 36
- **Language**: Kotlin (Java 11 source/target compatibility)
- **Build system**: Gradle 9.3.1, AGP 9.1.0, Kotlin DSL, version catalogs
- **UI toolkit**: Jetpack Compose (Material 3). No XML layouts.
- **Architecture**: UDF three-layer (UI → Domain → Data), following [Android official architecture](https://developer.android.com/topic/architecture) and [Now in Android](https://github.com/android/nowinandroid) patterns

## Build Commands

All commands use the Gradle wrapper (`./gradlew`). Run from project root.

```bash
# Build
./gradlew assembleDebug                  # Debug APK
./gradlew assembleRelease                # Release APK
./gradlew build                          # Full build (compile + test + lint)

# Clean
./gradlew clean                          # Remove build outputs

# Unit tests (JVM-local, no device needed)
./gradlew test                           # All unit tests
./gradlew testDebugUnitTest              # Debug variant only
./gradlew test --tests "playground.app.tobeylin.weather.ExampleUnitTest"  # Single test class
./gradlew test --tests "playground.app.tobeylin.weather.ExampleUnitTest.addition_isCorrect"  # Single test method

# Instrumented tests (requires emulator or device)
./gradlew connectedAndroidTest           # All instrumented tests
./gradlew connectedDebugAndroidTest      # Debug variant only

# Lint
./gradlew lint                           # Android lint
./gradlew lintDebug                      # Lint for debug variant

# Dependencies
./gradlew app:dependencies               # Dependency tree
```

## Project Structure

```
PlaygroundWeather/
  app/
    src/
      main/
        java/playground/app/tobeylin/weather/   # Main source (Kotlin + Composables)
        res/                                      # Resources (values, drawables — no layout XMLs)
        AndroidManifest.xml
      test/                                       # JVM unit tests (JUnit 4)
      androidTest/                                # Instrumented tests (Compose UI tests)
    build.gradle.kts                              # App module build config
  build.gradle.kts                                # Root build config
  gradle/libs.versions.toml                       # Version catalog
  settings.gradle.kts                             # Module declarations
  gradle.properties                               # Gradle/Kotlin config
```

## Dependencies (Version Catalog)

Managed in `gradle/libs.versions.toml`. Always use catalog references, never hardcoded versions.

```kotlin
// CORRECT
implementation(libs.androidx.core.ktx)

// WRONG — never inline versions
implementation("androidx.core:core-ktx:1.18.0")
```

Key dependencies:
- `libs.androidx.core.ktx` — Core Android KTX extensions
- `libs.androidx.activity.compose` — Activity Compose integration
- `libs.androidx.compose.bom` — Compose BOM (version alignment)
- `libs.androidx.compose.ui` — Compose UI core
- `libs.androidx.compose.material3` — Material 3 components
- `libs.androidx.compose.ui.tooling.preview` — `@Preview` support
- `libs.junit` — JUnit 4 (unit tests)
- `libs.androidx.junit` — AndroidX test JUnit (instrumented tests)
- `libs.androidx.compose.ui.test.junit4` — Compose UI testing

## Architecture

Three-layer architecture following [Android official guide](https://developer.android.com/topic/architecture) and [Now in Android](https://github.com/android/nowinandroid). Higher layers depend on lower layers, never the reverse.

```
UI Layer  →  Domain Layer (optional)  →  Data Layer
(Screens,     (Use Cases)                 (Repositories,
 ViewModels)                               DataSources)
```

### Unidirectional Data Flow (UDF)

- **Events flow down**: UI → ViewModel → UseCase → Repository
- **Data flows up**: DataSource → Repository → UseCase → ViewModel → UI (via Kotlin Flow)
- UI is a consumer of state, never the owner of business logic

### Data Layer

- **Repository pattern**: Interface + implementation (e.g., `WeatherRepository` / `OfflineFirstWeatherRepository`)
- **Single source of truth (SSOT)**: Local database is the SSOT; remote data is fetched and persisted locally
- **Offline-first**: UI always observes local data. Remote fetch → persist to local → Flow emits update to UI
- **DataSource abstraction**: Repositories consume `LocalDataSource` and `RemoteDataSource`; other layers never access DataSources directly
- **Immutability**: Data exposed from repositories must be immutable

### Domain Layer

Optional per official guide, but recommended when business logic spans multiple repositories or is reused across ViewModels.

- **Use Cases**: One class per business operation, named `VerbNounUseCase` (e.g., `GetWeatherForecastUseCase`)
- **Callable pattern**: Use `operator fun invoke()` so Use Cases are callable like functions
- **No Android dependencies**: Use Cases are pure Kotlin, making them easy to unit test

```kotlin
class GetWeatherForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
) {
    operator fun invoke(cityId: String): Flow<WeatherForecast> =
        weatherRepository.getWeatherForecast(cityId)
}
```

- When a Domain layer exists, ViewModels call **Use Cases** (not Repositories directly)
- When no Use Case is needed for a simple operation, ViewModels **may** call Repositories directly (per official guide)

### UI Layer

- **ViewModel**: Use standard `ViewModel` (never `AndroidViewModel`). Inject dependencies via Hilt.
- **State exposure**: Expose UI state via `StateFlow` using `stateIn(WhileSubscribed(5_000))` to survive config changes
- **State collection**: In Compose, collect with `collectAsStateWithLifecycle()` for lifecycle-safe observation
- **UI State modeling**: Use `sealed interface` for clear state representation

```kotlin
sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val weather: WeatherData) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}
```

- **User actions**: Pass as simple function calls to ViewModel (e.g., `onRefreshClick()`, `onCitySelected(id)`)
- **One-time events** (snackbar, navigation): Prefer modeling as consumable state over `SharedFlow`/`Channel`, to avoid missing events during config changes. Use `SharedFlow` only when truly fire-and-forget.

### Dependency Injection

- Use **Hilt** for all DI
- Organize `@Module` classes by layer (e.g., `DataModule`, `NetworkModule`)
- Bind interface-to-implementation via `@Binds`; use `@Provides` for third-party classes
- Scope singletons with `@Singleton`

### Testing Strategy

- **Fakes over Mocks**: Prefer hand-written Fakes (e.g., `FakeWeatherRepository`) over mocking frameworks
- **Unit tests**: ViewModels, Use Cases, Repositories (JVM-local, no device)
- **UI tests**: Compose screen tests with `createComposeRule()` (instrumented)
- **Test coroutines**: Use `kotlinx-coroutines-test` with `runTest` and `TestDispatcher`

### Modularization

Follow [official modularization guide](https://developer.android.com/topic/modularization). Layers map to modules:

```
:app                          # Entry point, root navigation, Hilt init
:feature:home                 # Feature — UI layer (screen + ViewModel)
:feature:search               # Feature — each feature is independent
:core:data                    # Data layer — repositories, data sources
:core:domain                  # Domain layer — Use Cases (optional)
:core:model                   # Shared data/domain model classes
:core:network                 # Remote data sources (Retrofit, etc.)
:core:database                # Local data sources (Room, DataStore)
:core:ui                      # Shared Compose components, theme
:core:common                  # Utilities shared across modules
```

#### Module Dependency Rules

- `:app` → `:feature:*` → `:core:*` (top-down only)
- **Feature modules must NOT depend on each other** — communicate via `:app` navigation wiring
- `:core:data` depends on `:core:network` + `:core:database`; other layers never access DataSources directly
- Use `implementation` (not `api`) to prevent transitive dependency leaks
- Use Kotlin `internal` visibility to hide module implementation details

#### Naming Conventions

- Features: `:feature:<name>` (e.g., `:feature:forecast`, `:feature:settings`)
- Core/shared: `:core:<name>` (e.g., `:core:data`, `:core:ui`, `:core:network`)

#### Granularity

- **Greenfield**: Start modular from day one — at minimum `:app`, `:feature:*`, `:core:data`
- **Don't over-modularize**: A small project can keep all data layer code in a single `:core:data` module; split by domain (`:core:data:weather`) only when complexity warrants it
- **Split signal**: When a module has unrelated responsibilities, or build times degrade, it's time to split

#### Hilt Across Modules

- Each module defines its own `@Module` (e.g., `NetworkModule` in `:core:network`)
- `:app` is the `@HiltAndroidApp` entry point; pulls in all modules transitively
- Scope to smallest component possible (prefer `@ViewModelScoped` over `@Singleton` where appropriate)

## Code Style

### Kotlin Style

- **Code style**: `official` (set in `gradle.properties` as `kotlin.code.style=official`)
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- 4-space indentation, no tabs
- Opening braces on same line as declaration

### Naming Conventions

| Element              | Convention     | Example                              |
|----------------------|----------------|--------------------------------------|
| Classes              | PascalCase     | `MainActivity`, `WeatherRepository`  |
| Composable functions | PascalCase     | `WeatherScreen()`, `TemperatureCard()` |
| Non-composable funcs | camelCase      | `fetchWeather()`, `formatTemp()`     |
| Properties/variables | camelCase      | `weatherData`, `isLoading`           |
| State variables      | camelCase      | `val uiState by viewModel.uiState`   |
| Constants            | UPPER_SNAKE    | `MAX_RETRY_COUNT`, `API_BASE_URL`    |
| Packages             | lowercase      | `playground.app.tobeylin.weather`    |
| String resources     | snake_case     | `@string/app_name`                   |

### Imports

- No wildcard imports in production code (wildcard `*` imports are acceptable in test files only)
- Group imports: Android SDK, then AndroidX/Compose, then project classes
- Remove unused imports

### File Organization

```kotlin
package playground.app.tobeylin.weather.ui

// Imports (grouped, no wildcards)

@Composable
fun WeatherScreen(modifier: Modifier = Modifier) {
    // UI implementation
}

@Preview(showBackground = true)
@Composable
private fun WeatherScreenPreview() {
    WeatherScreen()
}
```

- One screen composable per file, named after the screen (e.g., `WeatherScreen.kt`)
- Place `@Preview` functions at the bottom of the file, marked `private`
- Accept `Modifier` as the first optional parameter with default `Modifier`

## Jetpack Compose

### Activity Setup

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlaygroundWeatherTheme {
                // Screen composables here
            }
        }
    }
}
```

### Compose Guidelines

- Use `ComponentActivity` (not `AppCompatActivity`) as the base Activity class
- Always wrap content in the app theme (`PlaygroundWeatherTheme`)
- Use Material 3 components (`androidx.compose.material3.*`)
- Use `remember` / `rememberSaveable` for local state
- Hoist state: composables should receive state and emit events, not own business logic
- Accept `Modifier` parameter (default `Modifier`) in all reusable composables
- Use `Scaffold` for screen-level layout structure
- Handle edge-to-edge via `enableEdgeToEdge()` + Scaffold's padding

### Theming

- Define theme in a `ui/theme/` package (Theme.kt, Color.kt, Type.kt)
- Use `MaterialTheme.colorScheme` and `MaterialTheme.typography` — never hardcode colors/fonts
- Support both light and dark themes via `isSystemInDarkTheme()`

## Testing

### Unit Tests (JVM)

- Framework: **JUnit 4**
- Location: `app/src/test/java/`
- Pattern: `<ClassName>Test.kt` in same package
- Test method naming: `snake_case` describing behavior (e.g., `addition_isCorrect`)
- No device/emulator required

### Compose UI Tests

- Framework: **Compose UI Test** (`androidx.compose.ui.test`)
- Location: `app/src/androidTest/java/`
- Use `createComposeRule()` to set composable content in tests
- Find elements via `onNodeWithText()`, `onNodeWithTag()`, `onNodeWithContentDescription()`
- Add `testTag` to composables for stable test selectors: `Modifier.testTag("weather_list")`
- Requires running emulator or connected device

## Guardrails

- **Documentation first**: Anchor all tasks in written sources (this file, specs, tickets). If scope or data contracts are missing, **stop and request documentation** before proceeding.
- **Least-scope changes**: Practice minimal, targeted edits. Do not perform opportunistic refactors unless the task explicitly demands it.
- **New libraries require approval**: Do NOT add new dependencies (libraries, frameworks, plugins) without explicit user approval. Propose the library, explain why it's needed, and wait for confirmation before modifying `build.gradle.kts` or `libs.versions.toml`.

## Common Pitfalls

- **Compose only**: UI uses Jetpack Compose. Do NOT add XML layout files or View-based widgets.
- **Version catalog only**: All dependency versions go in `gradle/libs.versions.toml`. Never hardcode versions in `build.gradle.kts`.
- **Kotlin DSL**: Build files use `.gradle.kts` (Kotlin DSL), not Groovy `.gradle`.
- **Java 11**: Source and target compatibility are Java 11. Do not use Java 17+ APIs.
- **Min SDK 24**: Do not use APIs unavailable below API 24 without version checks.
- **ComponentActivity**: Use `ComponentActivity`, not `AppCompatActivity`, for Compose activities.
- **State hoisting**: Do not put business logic inside composables. Hoist state up, pass lambdas down.
- **Compose BOM**: Use the Compose BOM for version alignment. Do not pin individual Compose artifact versions.
