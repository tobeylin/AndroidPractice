# Learnings — today-weather-card

## Project Conventions
- Package root: `playground.app.tobeylin.weather`
- Feature package: `playground.app.tobeylin.weather.feature.weather`
- Build files use Kotlin DSL (.gradle.kts)
- All deps via version catalog `gradle/libs.versions.toml`
- Compose BOM version: 2026.02.00
- Hilt version: 2.59.2
- AGP 9.1.0 (use `org.jetbrains.kotlin.plugin.compose`, NOT `org.jetbrains.kotlin.android`)
- compileSdk uses `release(36) { minorApiLevel = 1 }` syntax

## Architecture Patterns
- Repositories use `suspend` functions (not Flow) → ViewModel must use MutableStateFlow, NOT stateIn()
- FakeWeatherRepository is in main/ source set — reusable from feature module tests
- CityRepository.getCities() is synchronous — returns hardcoded list
- @HiltViewModel + @Inject constructor — no separate Hilt @Module needed for ViewModel

## UI/Compose Patterns
- Modifier.blur() is NO-OP below API 31 (minSdk=24) → use Canvas + Brush.radialGradient
- No 1px borders anywhere (DESIGN.md "No-Line" rule)
- All colors via MaterialTheme.colorScheme.* (never hardcode hex)
- All typography via MaterialTheme.typography.* (never hardcode font sizes)
- Card corners: MaterialTheme.shapes.large = RoundedCornerShape(32.dp)
- Gradient: 135° from primary (#005F9C) → primaryContainer (#39A6FF)
- Max 2 @Preview functions per file

## Testing Conventions
- JUnit 4 (NOT JUnit 5)
- Fakes over Mocks — never Mockito/MockK
- coroutines-test with runTest
- MainDispatcherRule replaces Dispatchers.Main with UnconfinedTestDispatcher
- Test class naming: <ClassName>Test.kt
