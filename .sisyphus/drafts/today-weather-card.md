# Draft: Today's Weather Card (WeatherHomeScreen — Phase 3 Partial)

## Requirements (confirmed)
- Implement the "Today's Weather" hero card composable — the top blue gradient card from the Stitch design
- This is the FIRST card only; other cards (Humidity, Wind, Sunrise/Sunset, 5-Day Forecast) are OUT OF SCOPE
- Part of Phase 3 (UI Implementation) from `docs/phase-plan.md`
- Must be placed inside `:feature:weather` module

## Stitch Design Analysis (from screen.png + code.html)

### Today's Weather Hero Card Structure
```
┌──────────────────────────────────────┐
│  (Blue gradient background 135°)     │
│                                      │
│     24°    ☀️  (weather icon)        │
│                                      │
│         Clear Sky                    │
│                                      │
│   [↑ 28°C]    [↓ 18°C]             │
│   (pill chips with white/10 bg)     │
└──────────────────────────────────────┘
```

### Design Tokens for This Card
- **Background**: Linear gradient 135° from `Primary` (#005F9C) → `PrimaryContainer` (#39A6FF)
- **Corner radius**: `lg` = 2rem = 32.dp (matches Shapes.large)
- **Padding**: p-8 = 2rem ≈ 32.dp
- **Text color**: `OnPrimary` (#ECF3FF)
- **Temperature**: Manrope ExtraBold, ~80sp (display-level, `text-[5rem]`)
- **Condition text**: Manrope Bold, ~24sp (headline level, `text-2xl`)
- **High/Low chips**: `white/10` background + backdrop-blur, pill-shaped (full roundedness), Inter SemiBold
- **Weather icon**: Material Symbols `sunny`, filled, ~64sp, colored `TertiaryFixed` (#FEB64C)
- **Decorative**: Two blurred circles (white/10) positioned at top-right and bottom-left for atmospheric effect

## Codebase State (confirmed)

### What EXISTS:
- **Module**: `:feature:weather` declared in settings.gradle.kts, has build.gradle.kts with Compose + Hilt deps
- **NO Kotlin files yet** in feature/weather/src/ — completely empty
- **Domain models** in `:core:model`:
  - `CurrentWeather(temperature, feelsLike, humidity, windSpeed, condition, conditionDescription, iconCode, timestamp)`
  - `City(name, country, latitude, longitude)`
  - `DailyForecast(date, tempMax, tempMin, condition, iconCode)`
- **Repository**: `WeatherRepository` interface with `getCurrentWeather()` and `getDailyForecasts()`
- **Theme**: Color.kt, Type.kt, Shape.kt, Theme.kt all set up in `:app` module
  - Manrope (Bold, ExtraBold) + Inter (Regular, Medium, SemiBold) font families
  - Shapes: small=8dp, medium=16dp, large=32dp, extraLarge=48dp
  - Light color scheme only (no dark theme)
- **MainActivity**: Shows "Hello World" — needs wiring to actual screens

### What's MISSING (for this card):
- `CurrentWeather` model is missing `tempMax` and `tempMin` fields (needed for high/low pills)
  - WAIT: Actually, these come from DailyForecast for today's entry, or could be added to CurrentWeather
  - The OpenWeatherMap current weather API DOES return temp_min/temp_max in the main object
  - Need to check if the API response DTO has these fields
- No ViewModel exists
- No composables exist
- No weather icon mapping utility (iconCode → Material icon)
- Gradient brush not defined in theme (but can be created inline)
- Theme is in `:app` — feature module needs access to it

## Technical Decisions
- **Card scope**: Only the hero gradient card (today's weather). Not humidity, wind, sunrise, or forecast cards.
- **Data model**: Use `CurrentWeather` model. Check if tempMax/tempMin are available from API.
- **Icon approach**: Use Material Symbols / Material Icons. Map OpenWeatherMap `iconCode` to Compose Material Icons.
- **Preview**: Must include @Preview function with hardcoded sample data.
- **Theme access**: Feature module likely can't directly access `:app`'s theme package. Need to check dependency direction.

## Critical Findings

### tempMax/tempMin — RESOLVED
- The API DTO (`NetworkMain`) already has `tempMin` and `tempMax` fields
- BUT the mapper (`WeatherMapper.kt`) does NOT map them to `CurrentWeather`
- `CurrentWeather` model also LACKS `tempMax`/`tempMin` fields
- **Action needed**: Add `tempMax`/`tempMin` to `CurrentWeather` model + update mapper
- This touches `:core:model` and `:core:data` — technically Phase 2 scope, but minimal change

### Theme access — RESOLVED
- Theme (Color.kt, Type.kt, Shape.kt) is in `:app` module
- `:feature:weather` depends on `:app`? NO — dependency is `:app` → `:feature:weather` (one-way)
- Feature module CAN still use `MaterialTheme.colorScheme` / `MaterialTheme.typography` at runtime because `:app` wraps everything in `PlaygroundWeatherTheme`
- Gradient brush uses `Primary` and `PrimaryContainer` — feature module can reference these via `MaterialTheme.colorScheme.primary` and `.primaryContainer`
- **No architectural issue** — standard Compose pattern

### Icon library — NEEDS DECISION
- Stitch uses Google Material Symbols Outlined (font-based)
- Compose has `compose-material-icons-extended` (vector-based, same icon names)
- Currently NOT in dependencies — needs to be added to version catalog + feature module
- Alternative: use basic icons from `compose-material3` (limited set)

### No existing feature code
- `feature/weather/src/` is completely empty — no directories, no files
- Need to create full package structure

## Decisions (ALL CONFIRMED)
1. **Task scope**: Composable + ViewModel 一起做 — build TodayWeatherCard, WeatherViewModel, WeatherUiState sealed interface
2. **Model fix**: 一起補上 — add tempMax/tempMin to CurrentWeather model + update mapper
3. **Icon library**: 核准加入 — add compose-material-icons-extended to version catalog + feature module
4. **Decorative effects**: 實作 — implement the atmospheric blur circles on the hero card

## Scope Boundaries
- **INCLUDE**: TodayWeatherCard composable + @Preview + any needed utility (icon mapping)
- **EXCLUDE**: Other cards (Humidity, Wind, Sunrise/Sunset, 5-Day), ViewModel, navigation, real data integration
