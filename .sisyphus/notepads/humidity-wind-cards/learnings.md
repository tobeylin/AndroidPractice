# Learnings

## Project Conventions
- All Kotlin files in feature/weather use `package playground.app.tobeylin.weather.feature.weather`
- Internal utility functions follow WeatherIconMapper.kt pattern: top-level `internal fun` in feature module
- Test classes use JUnit 4 with `@Rule val mainDispatcherRule = MainDispatcherRule()`
- FakeWeatherRepository is in `core/data/src/main/` (not test/) — accessible from feature tests
- No wildcard imports in production code; test files may use wildcards
- Data classes use `@SerialName` from kotlinx.serialization

## Data Layer
- NetworkWind currently only has `speed: Double` — need to add `deg: Int? = null`
- CurrentWeather has `humidity: Int` and `windSpeed: Double` already mapped
- FakeWeatherRepository uses: temperature=28.5, tempMax=32.0, tempMin=25.0, feelsLike=31.2, humidity=70, windSpeed=3.5
- FakeWeatherApi is in core/network/src/main/ (not test/)

## Theme System
- surfaceContainerLowest = #FFFFFF (pure white for cards)
- shapes.medium = RoundedCornerShape(16.dp) for bento grid cards
- shapes.large = RoundedCornerShape(32.dp) for hero card only
- Typography: headlineLarge = Manrope Bold 32sp for card values

## Architecture
- WeatherMapper uses: NetworkCurrentWeatherResponse.asExternalModel() extension function
- WeatherUiState.Success has 7 fields currently
- WeatherViewModel calls weatherRepository.getCurrentWeather() + cityRepository.getCities().first()
- Added `windDeg` to `CurrentWeather` domain model and `deg` to `NetworkWind` DTO.
- Wind direction is nullable (`Int?`) because OpenWeatherMap API doesn't always provide it.
- `WeatherMapper` now handles the mapping from `wind.deg` to `windDeg`.
- Updated fakes (`FakeWeatherRepository`, `FakeWeatherApi`) to provide a consistent `windDeg = 180` for testing.
- Verified that `kotlinx-serialization` correctly handles missing `deg` field in JSON by defaulting it to `null`.
## Weather Conversion Learnings (2026-03-24)

- **Wind Direction Calculation**: Use `(((degrees.toDouble() % 360 + 360) % 360 + 11.25) / 22.5).toInt() % 16` for a robust compass index that handles negative values and 360-degree wraps.
- **Dew Point Accuracy**: The Magnus formula provides a good approximation (±0.5°C) for typical outdoor temperatures. Coercing humidity to ≥1% prevents `ln(0)` mathematical errors.
- **TDD Flow**: Writing tests first helped identify the exact boundary behavior for compass directions (e.g., 348.75 being the cutoff for North).

## UI State Extension (Task 3 - 2026-03-24)

- **WeatherUiState.Success** now has 11 fields (was 7): added `humidity`, `windSpeedKmh`, `windDirection`, `dewPoint` after `iconCode`.
- **Callsite cascade**: Adding non-default fields to a data class breaks ALL constructor callsites. `ast_grep_search` found exactly 3 callsites: ViewModel (production), WeatherHomeScreen (preview), TodayWeatherCard (preview). The second TodayWeatherCard preview is a loading state and doesn't use `WeatherUiState.Success`.
- **Conversion functions in same package**: `metersPerSecondToKmh`, `windDegreesToCompass`, `calculateDewPoint` are `internal` in `playground.app.tobeylin.weather.feature.weather` — no import needed in ViewModel since it's the same package.
- **Test delta values**: `assertEquals(12.6, success.windSpeedKmh, 0.1)` and `assertEquals(22.6, success.dewPoint, 0.5)` — dew point needs a wider delta due to Magnus formula approximation.
