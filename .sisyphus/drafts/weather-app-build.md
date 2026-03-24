# Draft: Weather App Build from Scratch

## Requirements (confirmed)
- Display weather forecast for **current day**
- Display weather forecast for **the week**
- **City list** where users can select a city to view its weather
- Tech: Kotlin, Coroutines, Jetpack Compose, Clean Architecture
- **Must contain at least one feature-module**
- 100% executable
- AI tools usage `.md` file

## Technical Decisions
- **Weather API**: OpenWeatherMap (free tier, need API key)
- **Modularization**: 最小合規 — `:app` + `:feature:weather` + `:core:data` + `:core:model` (+ `:core:network` possibly merged into `:core:data`)
- **City list data**: Hardcoded list of ~10-20 major cities
- **Testing**: 關鍵路徑 unit tests (ViewModel + Repository layer)
- **DI**: Hilt (per AGENTS.md)
- **Networking**: Retrofit + OkHttp + Kotlinx.serialization (or Moshi)
- **UI**: Jetpack Compose + Material 3, ComponentActivity

## Current State Assessment
- Blank Android Studio project — View-based, no Kotlin plugin, no Compose, no architecture
- Must migrate entirely to Compose-based stack
- Single `:app` module, must add feature modules

## Scope Boundaries
- INCLUDE: 3 core features, modularization, Hilt DI, Clean Architecture, key unit tests
- EXCLUDE: City search API, offline caching, location-based weather, widget, notifications

## Open Questions
- Serialization library: Kotlinx.serialization vs Moshi? (leaning Kotlinx for Kotlin-native)
- Navigation: Compose Navigation vs type-safe navigation? (leaning Compose Navigation)
- OpenWeatherMap plan: Free tier gives current weather + 5-day/3-hour forecast — close enough for "weekly"
