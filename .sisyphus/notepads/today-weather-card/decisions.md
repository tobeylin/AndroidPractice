# Decisions — today-weather-card

## 2026-03-24 — Plan Approved

- Scope: TodayWeatherCard composable + WeatherViewModel together
- Model fix (tempMax/tempMin) included in this phase
- Icons library (compose-material-icons-extended) approved by user
- Decorative blur circles: Canvas + radialGradient (not Modifier.blur)
- ViewModel testing: TDD approach — tests first (Task 3), then implementation (Task 4)
- Selected city: default to first city from CityRepository.getCities() (Taipei)
- Temperature display: displayLarge (57sp Manrope ExtraBold) — not custom 80sp
- No stateIn() — WeatherRepository is suspend-based, use MutableStateFlow
- No MainActivity.kt changes in this phase — wiring is a separate task
- No Compose UI (instrumented) tests — JVM unit tests only
