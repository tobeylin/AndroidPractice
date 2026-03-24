# Draft: Humidity & Wind Cards for WeatherHomeScreen

## Requirements (confirmed)
- Add humidity card and wind card to WeatherHomeScreen
- Focus ONLY on these two cards — no other UI changes
- Must NOT break existing implementation (TodayWeatherCard, ViewModel, etc.)

## Current State Analysis

### Data Pipeline Status
- **CurrentWeather model**: Already has `humidity: Int` and `windSpeed: Double` — READY
- **Network DTO**: `NetworkMain.humidity` and `NetworkWind.speed` — READY
- **WeatherMapper**: Already maps both fields from DTO→Model — READY
- **FakeWeatherRepository**: Already provides `humidity = 70`, `windSpeed = 3.5` — READY

### Gap: UI State
- **WeatherUiState.Success**: Missing `humidity` and `windSpeed` fields
- **WeatherViewModel**: Does not map `weather.humidity` or `weather.windSpeed` to UI state

### Gap: UI Composables
- No humidity or wind card composables exist yet
- WeatherHomeScreen only renders `TodayWeatherCard` in Success state

### Stitch Design Reference (screen.png + code.html)
- **Layout**: Two cards side-by-side in a 2-column grid below TodayWeatherCard
- **Card style**: `surface-container-lowest` (white) background, `rounded-lg` (2rem = 32dp → matches `shapes.large`), padding 24dp (p-6)
- **Header row**: Icon (primary color) + uppercase label (labelSmall, onSurfaceVariant, semibold, tracking-wider)
- **Value**: Manrope bold, ~30sp (headlineLarge or displaySmall), onSurface
- **Subtitle**: bodySmall, onSurfaceVariant
- **Humidity card**: Icon=humidity_percentage, "HUMIDITY", "64%", "Dew point is 14°"
- **Wind card**: Icon=air, "WIND", "12 km/h", "NW Direction"

## Technical Decisions
- Cards placed in a `Row` with equal weight (2-col grid pattern)
- Card composable: reusable `WeatherDetailCard` or individual `HumidityCard`/`WindCard`
- Use `Surface` composable with `surfaceContainerLowest` color, `shapes.large` corner
- Use Material Icons Extended (already a dependency): `Icons.Filled.WaterDrop` for humidity, `Icons.Filled.Air` for wind
- Typography: `headlineLarge` for value (Manrope Bold 32sp), `labelSmall` for header, `bodySmall` for subtitle
- Dew point: Not available from API — placeholder text or compute from humidity+temp
- Wind direction: Not available from current API DTO (only speed, not deg)

## Resolved Questions
- **Dew point**: Approximate using Magnus formula from temp + humidity
- **Wind direction**: Extend NetworkWind DTO to parse `deg` field, add to CurrentWeather, convert to compass direction
- **Card composable**: Reusable `WeatherMetricCard` with icon/label/value/unit/subtitle params
- **Wind speed unit**: Convert m/s → km/h (× 3.6)
- **Tests**: Update existing WeatherViewModelTest + add tests for dew point & wind direction logic

## Scope Boundaries
- INCLUDE: WeatherUiState.Success new fields, ViewModel mapping, HumidityCard, WindCard, WeatherHomeScreen layout, @Preview, unit test updates
- EXCLUDE: Sunrise/Sunset card, 5-Day Forecast card, navigation changes, new API fields, dark theme
