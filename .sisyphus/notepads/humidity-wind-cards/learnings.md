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
