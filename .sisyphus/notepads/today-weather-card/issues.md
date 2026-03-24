# Issues & Gotchas — today-weather-card

## Known Gotchas
- Modifier.blur() is a no-op below API 31 — always use Canvas approach
- AGP 9.1.0: plugin is `org.jetbrains.kotlin.plugin.compose` (NOT `org.jetbrains.kotlin.android`)
- compose-material-icons-extended uses BOM — no version needed in toml
- TDD Task 3 won't compile until Task 4 creates WeatherViewModel — this is intentional
- FakeWeatherRepository.shouldThrowError must be set before calling the suspend function
- CityRepository.getCities() returns List<City> synchronously — no coroutine needed to call it

## Issues Found
(none yet)

## Task 4 Issues
- FakeCityRepository `var cities` property clashed with `override fun getCities()` (same JVM signature) — renamed to `var fakeCities` to fix
