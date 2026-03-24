# Draft: Phase 3 — UI Implementation

## Requirements (confirmed from phase-plan.md)
- Implement `WeatherViewModel` (`@HiltViewModel`) + `WeatherUiState` sealed interface
- Implement city list screen (`CityListScreen`) — LazyColumn + click events
- Implement weather detail screen (`WeatherDetailScreen`) — current weather + 5-day forecast
- Reference Stitch design drafts for UI layout
- All composables include `@Preview` functions
- ViewModel unit tests (Loading→Success, Loading→Error state transitions)
- Acceptance: `./gradlew assembleDebug` + `./gradlew test` pass

## Research Findings

### Current State
- `:feature:weather` module: **empty shell** — build.gradle.kts configured with Compose, Hilt, KSP, depends on `:core:data` + `:core:model`
- `MainActivity`: Shows "Hello World" — needs to be wired to weather screens
- Theme: Fully implemented in `:app` (Color.kt, Type.kt, Shape.kt, Theme.kt) — "Atmospheric Editorial" Stitch design
- No `hilt-navigation-compose` in version catalog — **needed for `hiltViewModel()` in Compose**
- No `lifecycle-runtime-compose` in version catalog — **needed for `collectAsStateWithLifecycle()`**
- No Coil dependency — weather icons will need alternative approach

### Data Layer API Surface (Phase 2 — complete)
- `WeatherRepository` interface: `getCurrentWeather(lat, lon): CurrentWeather`, `getDailyForecasts(lat, lon): List<DailyForecast>`
- `CityRepository` interface: `getCities(): List<City>`
- `FakeWeatherRepository`: test doubles with configurable error throwing
- DI: `DataModule` binds DefaultWeatherRepository + DefaultCityRepository

### Domain Models
- `City(name, country, latitude, longitude)`
- `CurrentWeather(temperature, feelsLike, humidity, windSpeed, condition, conditionDescription, iconCode, timestamp)`
- `DailyForecast(date, tempMax, tempMin, condition, iconCode)`

### Design Reference (Stitch)
Two screens designed:
1. **City List Screen** (`search_cities_updated/screen.png`)
   - Search bar (pill-shaped) at top with back arrow
   - "POPULAR CITIES" section label
   - White rounded cards: city name, country, temperature, condition with emoji icon
   - Light gray background (#F0F4F8)

2. **Weather Detail Screen** (`today_s_weather_5_day_forecast_updated_location/screen.png`)
   - Header: city name (blue) + search icon
   - Hero card: blue gradient, large temp, sun icon, condition, hi/lo pills
   - Humidity + Wind side-by-side cards
   - Sunrise/Sunset card
   - 5-Day Forecast card with day rows

### Missing Dependencies (need user approval per AGENTS.md)
- `androidx.hilt:hilt-navigation-compose` — for `hiltViewModel()` in Compose
- `androidx.lifecycle:lifecycle-runtime-compose` — for `collectAsStateWithLifecycle()`
- Coil (optional) — for weather icon loading from URL

## Technical Decisions
- [pending] Navigation approach: state-based switching (phase-plan says this)
- [pending] Weather icons: emoji text vs Coil URL loading vs custom vector drawables
- [pending] Search functionality: real filtering or just display the hardcoded list?
- [pending] Sunrise/sunset data: not in current domain models — include or skip?
- [pending] Humidity/wind are in CurrentWeather model — display in detail screen

## Open Questions
1. Weather icon approach — Coil (needs approval) or emoji/text fallback?
2. Search bar in city list — functional filter or just visual placeholder?
3. Sunrise/sunset card — skip (no data in model) or add to model?
4. Theme is in `:app` module — `:feature:weather` can't access it directly. Need a `:core:ui` module or move theme?
5. City list needs current weather preview (temp + condition per city) — this means fetching weather for ALL cities on list screen

## Scope Boundaries
- INCLUDE: CityListScreen, WeatherDetailScreen, WeatherViewModel, UiState, @Previews, ViewModel tests
- EXCLUDE: Navigation wiring in MainActivity (Phase 4), real API calls (Phase 4), dark theme (Phase 5)
