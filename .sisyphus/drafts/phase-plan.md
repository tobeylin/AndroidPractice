# Weather App — Phase Plan

## 現況評估

| 面向 | 現況 | 目標 |
|------|------|------|
| UI Framework | View-based (XML + AppCompatActivity) | Jetpack Compose + ComponentActivity |
| Kotlin Plugin | AGP 9.1 內建（不需額外 apply） | 需加 Compose compiler plugin |
| Architecture | 零架構 — 單一 Activity | 三層架構 (UI → Domain → Data) + UDF |
| Modularization | 單一 `:app` module | `:app` + `:feature:weather` + `:core:data` + `:core:model` + `:core:network` |
| DI | 無 | Hilt (≥2.59，AGP 9.x 相容) |
| Networking | 無 | Retrofit + OkHttp + Kotlinx Serialization |
| Dependencies | 基礎 AndroidX (appcompat, constraintlayout) | Compose BOM, Hilt, Retrofit, Coil 等 |

---

## 關鍵技術決策

| 決策 | 選擇 | 理由 |
|------|------|------|
| Weather API | OpenWeatherMap (free tier) | 最成熟免費選項，需註冊拿 API key |
| 模組化程度 | 最小合規（5 modules） | 滿足「至少一個 feature module」，不過度工程化 |
| 城市清單 | Hardcoded ~15 城市 | 簡化範圍，專注天氣功能本身 |
| 測試策略 | 關鍵路徑 unit tests（ViewModel + Repository） | 展示測試能力但不追求覆蓋率 |
| UI 設計 | Google Stitch（Phase 0） | 產出 DESIGN.md + 視覺參考，非直接 Compose code |
| 導航 | State-based screen switching | 比 Navigation Compose 更簡單，對此範圍足夠 |
| Serialization | Kotlinx Serialization | Kotlin-first，無 reflection |
| 圖片載入 | Coil（需確認 user approval） | Compose-native，載入天氣 icon |

---

## 模組結構

```
PlaygroundWeather/
├── :app                  # Entry point, Hilt init, Navigation host, Theme
├── :feature:weather      # 城市清單 + 天氣詳情畫面 + ViewModel
├── :core:data            # Repository + DTO→Model mapping
├── :core:model           # 共用 domain model（純 Kotlin）
└── :core:network         # Retrofit setup, API interface, DTOs
```

依賴方向：`:app` → `:feature:weather` → `:core:data` → `:core:network`，`:core:model` 被所有模組共用。

---

## 分階段交付計劃

### Phase 0 — UI 設計（Stitch）🎨
> **執行者**：人工操作，不在自動化範圍內
> **交付物**：所有畫面設計 + DESIGN.md + 互動原型

- 用 Stitch 設計城市清單、今日天氣、5 日預報畫面
- 匯出 DESIGN.md（色票、字型、間距規則）→ 作為 Phase 1 Theme 的輸入
- 建立互動原型驗證導航 flow（城市清單 → 天氣詳情 → 返回）

---

### Phase 1 — 基礎建設（Foundation）🏗️
> **交付物**：可編譯的多模組 Compose 空殼，顯示 Hello World
> **預估比重**：25%

- 更新 `libs.versions.toml`，加入所有新 dependencies（Compose BOM, Hilt, KSP, Retrofit 等）
- 建立 5 個 module 的目錄結構 + `build.gradle.kts`
- 設定 Hilt DI（`@HiltAndroidApp` + 跨模組 wiring 驗證）
- 遷移 `MainActivity` 到 `ComponentActivity` + Compose `setContent`
- 建立 Material 3 Theme（參考 Stitch DESIGN.md）
- 設定 `BuildConfig.WEATHER_API_KEY`（從 `local.properties` 讀取）
- 加入 `INTERNET` permission，刪除所有 XML layout
- **驗收**：`./gradlew assembleDebug` 全模組通過

**⚠️ 注意事項**：
- AGP 9.1 內建 Kotlin → 不可 apply `org.jetbrains.kotlin.android`
- Compose compiler 用 `org.jetbrains.kotlin.plugin.compose`（非舊版 `kotlinCompilerExtensionVersion`）
- `compileSdk` 用 `release(36) { minorApiLevel = 1 }` 語法（AGP 9.x）
- Hilt ≥2.59 + KSP 版本必須對應 Kotlin 版本

---

### Phase 2 — 資料管線（Data Pipeline）📡
> **交付物**：unit test 驗證可從 API 拿到正確的天氣資料
> **預估比重**：20%
> **可與 Phase 3 的 UI 部分平行**

- 定義 domain models（`City`, `CurrentWeather`, `DailyForecast`）於 `:core:model`
- 實作 Retrofit `WeatherApi` interface + DTO data classes 於 `:core:network`
- 實作 5-day/3-hour → 每日摘要的聚合邏輯（daily high/low）
- 實作 `WeatherRepository`（interface + `DefaultWeatherRepository`）+ DTO→Model mapping
- 建立 `FakeWeatherApi`、`FakeWeatherRepository` 供測試用
- **驗收**：`./gradlew test` 所有 unit tests 通過

**⚠️ 注意事項**：
- OpenWeatherMap 免費方案只有 5 天預報（非 7 天），UI 需對應
- API key 透過 OkHttp Interceptor 注入，不寫死在 code 中
- DTO 不可洩漏到 `:core:data` 之外

---

### Phase 3 — UI 實作（UI Implementation）📱
> **交付物**：ViewModel + 所有畫面 composable（可用假資料運行）
> **預估比重**：25%
> **UI composable 部分可與 Phase 2 平行；ViewModel 需等 Repository interface**

- 實作 `WeatherViewModel`（`@HiltViewModel`）+ `WeatherUiState` sealed interface
- 實作城市清單畫面（`CityListScreen`）— LazyColumn + 點擊事件
- 實作天氣詳情畫面（`WeatherDetailScreen`）— 當日天氣 + 5 日預報
- 參考 Stitch 設計稿實作 UI layout
- 所有 composable 含 `@Preview` 函式
- ViewModel unit tests（Loading→Success, Loading→Error state transitions）
- **驗收**：`./gradlew assembleDebug` + `./gradlew test` 通過

---

### Phase 4 — 整合（Integration）🔗
> **交付物**：End-to-end 可用的完整功能
> **預估比重**：15%
> **依賴 Phase 2 + Phase 3 都完成**

- 接線 Navigation（state-based screen switching：城市清單 ↔ 天氣詳情）
- 接上真實 API data flow（ViewModel → Repository → Retrofit → OpenWeatherMap）
- 實作 Error state UI（網路錯誤顯示友善訊息 + retry）
- 實作 Loading state UI（`CircularProgressIndicator`）
- 處理 edge cases（空 API key、API 429 rate limit、partial forecast data）
- **驗收**：`./gradlew build` 通過（compile + test + lint）

---

### Phase 5 — 收尾與交付（Polish & Delivery）✨
> **交付物**：提交就緒的 GitHub repository
> **預估比重**：15%

- 套用 Stitch Theme（微調 Color.kt、Type.kt）
- 確認 Light / Dark theme 正常
- 撰寫 `README.md`（setup 說明、API key 設定、架構概覽、模組依賴圖）
- 撰寫 AI tools 使用說明 `.md`
- `local.properties.example` 放入 API key placeholder
- 最終 QA：`./gradlew build` 全過
- **驗收**：clone → 設 API key → build → run 完整流程可走通

---

## 平行化機會

```
Phase 0: Stitch 設計 (手動)
         │
         ▼
Phase 1: Foundation (sequential)
         │
         ├──────────────────┐
         ▼                  ▼
Phase 2: Data Pipeline   Phase 3: UI Composables  ← 可平行
         │                  │
         └──────┬───────────┘
                ▼
Phase 4: Integration
                │
                ▼
Phase 5: Polish & Delivery
```

**最大加速點**：Phase 2（Data）與 Phase 3（UI composables）之間無依賴，可同時進行。
Phase 3 的 ViewModel 需要 Phase 2 的 Repository interface，但 composable 本身不需要。

---

## 風險與注意事項

| 風險 | 影響 | 緩解措施 |
|------|------|---------|
| AGP 9.1 + Hilt + KSP 版本不相容 | Phase 1 卡關 | 每一步 `./gradlew assembleDebug` 驗證 |
| OpenWeatherMap API 格式變更 | Phase 2 解析失敗 | 先 curl 驗證 API response，再寫 DTO |
| 5-day/3-hour 聚合邏輯有 edge case | 預報顯示不正確 | 用 unit test 覆蓋 partial day 場景 |
| Stitch DESIGN.md 與 Material 3 不完全對應 | Theme 調整額外花時間 | Phase 5 預留 buffer |
| Coil 需 user approval | 天氣 icon 無法顯示 | 如不批准，fallback 到純文字顯示 |

---

## Scope 邊界

**IN scope**：
- 3 核心功能（當日天氣、5 日預報、城市清單）
- 多模組 Clean Architecture
- Hilt DI + Compose UI
- 關鍵路徑 unit tests
- README + AI tools 文件

**OUT of scope**：
- 城市搜尋 API / Geocoding
- 離線快取（Room）
- GPS 定位
- Widget / Notifications
- Settings 畫面 / 溫度單位切換
- Compose UI 測試（需要 device）
- 動畫（超出 Compose 預設）
