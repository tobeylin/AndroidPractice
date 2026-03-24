# OpenWeatherMap API 研究報告

> 研究日期：2026-03-24
> 目標：了解 OpenWeatherMap 免費方案可用的 API 端點、回傳欄位、限制，作為 Weather App 資料層設計的依據。

---

## 免費方案可用端點

| 端點 | 用途 | 本專案是否需要 |
|------|------|---------------|
| Current Weather (`/data/2.5/weather`) | 即時天氣 | ✅ 必要 |
| 5-Day / 3-Hour Forecast (`/data/2.5/forecast`) | 5 天預報（每 3 小時一筆） | ✅ 必要 |
| Air Pollution (`/data/2.5/air_pollution`) | 空氣品質指數 (AQI) | ⚠️ 可選（加分項） |
| Geocoding (`/geo/1.0/direct`, `/geo/1.0/reverse`) | 城市名 ↔ 經緯度轉換 | ✅ 建議使用 |
| Weather Maps 1.0 | 15 種天氣地圖圖層 | ❌ 不需要 |

---

## Current Weather API

**端點**: `GET https://api.openweathermap.org/data/2.5/weather`

**必要參數**:
- `lat`, `lon` — 經緯度（建議搭配 Geocoding API 使用）
- `appid` — API Key

**可選參數**:
- `units` — 單位系統（見下方「單位系統」）
- `lang` — 回傳 description 的語言（支援 `zh_tw`、`zh_cn`）

### 回傳欄位

```json
{
  "coord": { "lon": 121.5654, "lat": 25.0330 },
  "weather": [
    {
      "id": 801,              // 天氣狀態代碼
      "main": "Clouds",       // 天氣群組（Rain, Snow, Clouds...）
      "description": "多雲",   // 天氣描述（受 lang 參數影響）
      "icon": "02d"           // 圖標代碼（d=白天, n=夜晚）
    }
  ],
  "main": {
    "temp": 28.5,             // 溫度
    "feels_like": 31.2,       // 體感溫度
    "temp_min": 27.0,         // ⚠️ 非每日最低（見備註）
    "temp_max": 30.1,         // ⚠️ 非每日最高（見備註）
    "pressure": 1013,         // 大氣壓力 (hPa)
    "humidity": 78,           // 濕度 (%)
    "sea_level": 1013,        // 海平面氣壓 (hPa)
    "grnd_level": 1008        // 地面氣壓 (hPa)
  },
  "visibility": 10000,        // 能見度 (公尺, 最大 10km)
  "wind": {
    "speed": 3.5,             // 風速
    "deg": 180,               // 風向 (角度)
    "gust": 5.2               // 陣風風速（不一定有）
  },
  "clouds": { "all": 25 },    // 雲量 (%)
  "rain": { "1h": 0.5 },      // 過去 1 小時降雨量 mm（不一定有）
  "snow": { "1h": 0 },        // 過去 1 小時降雪量 mm（不一定有）
  "dt": 1711267200,           // 資料計算時間 (Unix timestamp)
  "sys": {
    "country": "TW",
    "sunrise": 1711232400,    // 日出時間 (Unix timestamp)
    "sunset": 1711275600      // 日落時間 (Unix timestamp)
  },
  "timezone": 28800,          // UTC 偏移 (秒)，台灣 = +28800 (UTC+8)
  "name": "Taipei",           // 城市名稱
  "cod": 200
}
```

> ⚠️ **重要備註**：`temp_min` / `temp_max` 代表的是「目前該城市範圍內的最低/最高溫度」，**不是**當日的最低/最高溫。真正的每日最低/最高溫只有付費的 16-Day Forecast API 才提供。免費方案需透過 5-Day Forecast 的 3 小時資料自行聚合推算。

---

## 5-Day / 3-Hour Forecast API

**端點**: `GET https://api.openweathermap.org/data/2.5/forecast`

**參數**：與 Current Weather 相同（`lat`, `lon`, `appid`, `units`, `lang`）

### 回傳結構

回傳 `list` 陣列，包含 **40 筆資料**（每天 8 筆 × 5 天）。

```json
{
  "list": [
    {
      "dt": 1711278000,
      "main": {
        "temp": 26.3,
        "feels_like": 28.1,
        "temp_min": 25.8,
        "temp_max": 27.1,
        "pressure": 1012,
        "humidity": 82
      },
      "weather": [
        {
          "id": 500,
          "main": "Rain",
          "description": "小雨",
          "icon": "10d"
        }
      ],
      "clouds": { "all": 75 },
      "wind": { "speed": 4.1, "deg": 210, "gust": 6.8 },
      "visibility": 10000,
      "pop": 0.65,             // 🆕 降水機率 (0.0 ~ 1.0)
      "rain": { "3h": 1.2 },   // 過去 3 小時降雨量 mm
      "sys": { "pod": "d" },   // 🆕 日夜標記 (d=白天, n=夜晚)
      "dt_txt": "2026-03-24 09:00:00"
    }
    // ... 共 40 筆
  ],
  "city": {
    "name": "Taipei",
    "coord": { "lat": 25.033, "lon": 121.5654 },
    "country": "TW",
    "population": 7871900,
    "timezone": 28800,
    "sunrise": 1711232400,
    "sunset": 1711275600
  }
}
```

### 與 Current Weather 的差異

| 欄位 | Current Weather | 5-Day Forecast |
|------|----------------|----------------|
| `pop` (降水機率) | ❌ 無 | ✅ 有 |
| `sys.pod` (日夜標記) | ❌ 無 | ✅ 有 |
| `rain` / `snow` | 過去 1 小時 (`1h`) | 過去 3 小時 (`3h`) |
| `sunrise` / `sunset` | 在每筆 `sys` 中 | 在 `city` 物件中（全域） |
| 資料筆數 | 1 筆 | 40 筆（每 3 小時一筆 × 5 天） |

### 每日摘要聚合策略

免費 API 不直接提供「每日」預報，需自行從 3 小時資料推算：

- **每日最高溫** → 取當天所有 `temp_max` 的最大值
- **每日最低溫** → 取當天所有 `temp_min` 的最小值
- **天氣狀態** → 取當天白天（`pod: "d"`）出現頻率最高的 `weather.main`
- **降水機率** → 取當天所有 `pop` 的最大值
- **圖標** → 與天氣狀態對應，取白天版本（`d` 後綴）

---

## 天氣狀態代碼

### 代碼群組

| 代碼範圍 | 群組 | 說明 |
|---------|------|------|
| 2xx | Thunderstorm | 雷暴（200-232） |
| 3xx | Drizzle | 毛毛雨（300-321） |
| 5xx | Rain | 雨（500-531） |
| 6xx | Snow | 雪（600-622） |
| 7xx | Atmosphere | 大氣現象：霧(701)、霾(711)、沙塵(731)等 |
| 800 | Clear | 晴天 |
| 80x | Clouds | 多雲（801 少雲 → 804 陰天） |

### 圖標對照

| 圖標代碼 | 白天 | 夜晚 | 說明 |
|---------|------|------|------|
| `01d` | ☀️ | `01n` 🌙 | 晴天 |
| `02d` | 🌤️ | `02n` | 少雲 |
| `03d` | ⛅ | `03n` | 多雲 |
| `04d` | ☁️ | `04n` | 陰天 |
| `09d` | 🌧️ | `09n` | 陣雨 |
| `10d` | 🌦️ | `10n` | 雨 |
| `11d` | ⛈️ | `11n` | 雷暴 |
| `13d` | ❄️ | `13n` | 雪 |
| `50d` | 🌫️ | `50n` | 霧 |

**圖標 URL**:
- 標準：`https://openweathermap.org/img/wn/{icon}.png`
- 高解析度 (2x)：`https://openweathermap.org/img/wn/{icon}@2x.png`
- 範例：`https://openweathermap.org/img/wn/10d@2x.png`

> 💡 **建議**：官方圖標品質普通，未來可考慮自訂圖標或使用第三方天氣圖標庫，提升 UI 質感。

---

## Geocoding API

將城市名稱轉換為經緯度（Current Weather 與 Forecast API 皆需要經緯度作為參數）。

**Direct Geocoding**（城市名 → 座標）:
```
GET http://api.openweathermap.org/geo/1.0/direct?q={city name}&limit=5&appid={API key}
```

**Reverse Geocoding**（座標 → 城市名）:
```
GET http://api.openweathermap.org/geo/1.0/reverse?lat={lat}&lon={lon}&limit=5&appid={API key}
```

> 💡 本專案使用硬編碼城市清單，可直接在 app 內預存城市對應的經緯度，減少 API 呼叫次數。若未來改為搜尋功能，Geocoding API 就會派上用場。

---

## Air Pollution API（可選）

**端點**: `GET http://api.openweathermap.org/data/2.5/air_pollution`

回傳：
- **AQI** (Air Quality Index)：1 (Good) → 5 (Very Poor)
- **汙染物濃度**：CO、NO、NO₂、O₃、SO₂、PM2.5、PM10、NH₃

> 適合作為加分功能，在天氣詳情頁顯示空氣品質資訊。

---

## 單位系統

透過 `units` 參數控制：

| 欄位 | Standard（預設） | Metric (`units=metric`) | Imperial (`units=imperial`) |
|------|-----------------|------------------------|---------------------------|
| 溫度 | Kelvin (K) | 攝氏 (°C) | 華氏 (°F) |
| 風速 | m/s | m/s | miles/hour |
| 降水量 | mm | mm | mm |

> 💡 **建議**：本專案面向台灣用戶，預設使用 `units=metric`（攝氏）。

---

## 多語系支援

透過 `lang` 參數可改變 `weather.description` 欄位的語言：

- `zh_tw` — 繁體中文
- `zh_cn` — 簡體中文
- `en` — 英文（預設）
- 支援超過 40 種語言

> ⚠️ `lang` 參數只影響 `description` 欄位，`weather.main`（如 "Rain", "Clouds"）永遠是英文。

---

## 免費方案限制

| 限制項目 | 數值 |
|---------|------|
| 每分鐘呼叫次數 | 60 次 |
| 每月呼叫總量 | 1,000,000 次 |
| 建議單一地點呼叫頻率 | ≤ 每 10 分鐘 1 次 |
| 歷史資料 | ❌ 不提供 |
| 每小時預報 | ❌ 不提供（需 Student 以上方案） |
| 每日預報（16天） | ❌ 不提供（需 Student 以上方案） |

### 與付費方案差異（Student Plan, 免費）

Student Plan 額外提供：
- Hourly Forecast（4 天逐時預報）
- Daily Forecast（16 天每日預報，含真正的 daily min/max）
- History API
- Statistical Weather API

> 如果能申請到 Student Plan，可直接使用 Daily Forecast API 取得每日最高/最低溫，省去手動聚合的複雜度。

---

## 對本專案的影響 & 設計建議

### 資料來源規劃

| 功能需求 | 資料來源 | 備註 |
|---------|---------|------|
| 目前天氣 | Current Weather API | 直接使用 |
| 5 天預報 | 5-Day Forecast API | 需聚合為每日摘要 |
| 每日最高/最低溫 | 5-Day Forecast 聚合 | 非精確值，為估算 |
| 降水機率 | 5-Day Forecast (`pop`) | 取每日最大值 |
| 天氣圖標 | Icon URL 或自訂圖標 | 建議用 Coil 載入 |
| 城市座標 | App 內硬編碼 | 約 15 個城市 |
| 空氣品質 | Air Pollution API | 可選加分項 |

### API 呼叫策略

1. **最少呼叫原則**：切換城市時同時呼叫 Current Weather + Forecast（2 次呼叫）
2. **快取機制**：同一城市 10 分鐘內不重複呼叫（遵循官方建議）
3. **預設參數**：`units=metric&lang=zh_tw`
4. **錯誤處理**：需處理 401（Key 無效）、404（城市不存在）、429（超過限制）

### 必要的資料轉換

1. **Unix timestamp → 可讀時間**：`dt`, `sunrise`, `sunset` 皆為 Unix timestamp
2. **3 小時預報 → 每日摘要**：分組聚合邏輯（上方已說明策略）
3. **風向角度 → 方位**：`deg` 需轉換為「北」「東北」等文字
