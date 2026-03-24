# Design System Document: Atmospheric Editorial

## 1. Overview & Creative North Star
### Creative North Star: "The Ethereal Forecast"
This design system moves away from the utilitarian, "widget-heavy" look of traditional weather apps toward a high-end editorial experience. Instead of a rigid grid of data points, we treat weather as a living environment. The interface should feel like looking through a clean, high-end lens at the sky—utilizing **Soft Minimalism** and **Atmospheric Layering** to convey information through depth, light, and motion rather than lines and boxes.

By prioritizing intentional asymmetry and expansive breathing room, we elevate daily utility into a premium digital ritual.

---

## 2. Colors & Surface Philosophy
The palette is rooted in the shifting tones of the sky, utilizing Material Design 3 logic but executed with a "No-Line" editorial philosophy.

### The "No-Line" Rule
**Strict Mandate:** Designers are prohibited from using 1px solid borders to define sections. Layout boundaries must be established solely through background color shifts or tonal transitions. 
*   *Example:* A weather detail card should not have an outline; it should be a `surface-container-lowest` element sitting on a `surface` background.

### Surface Hierarchy & Nesting
Treat the UI as a series of stacked, physical layers—like fine paper or frosted glass.
- **Base Level:** `surface` (#f5f7f9) for the overall background.
- **Information Clusters:** Use `surface-container-low` (#eef1f3) for broad groupings.
- **High-Priority Data:** Use `surface-container-lowest` (#ffffff) for foreground "hero" cards to create a natural, soft lift.

### The "Glass & Gradient" Rule
To capture the essence of weather, use **Glassmorphism** for floating headers or navigation bars.
- **Effect:** Apply `surface` color at 70% opacity with a `24px` backdrop-blur. 
- **Signature Textures:** For main CTAs or "Current Condition" hero backgrounds, use a subtle linear gradient transitioning from `primary` (#005f9c) to `primary_container` (#39a6ff) at a 135-degree angle to provide "visual soul."

---

## 3. Typography: The Editorial Voice
We utilize a pairing of **Manrope** for high-impact data and **Inter** for functional clarity.

| Level | Token | Font | Size | Intent |
| :--- | :--- | :--- | :--- | :--- |
| **Display** | `display-lg` | Manrope | 3.5rem | Temperature hero numbers; bold and authoritative. |
| **Headline**| `headline-md`| Manrope | 1.75rem| Weather condition titles (e.g., "Thunderstorm"). |
| **Title**   | `title-lg`   | Inter | 1.375rem| Location names and section headers. |
| **Body**    | `body-md`    | Inter | 0.875rem| Descriptive forecasts and detailed metrics. |
| **Label**   | `label-md`   | Inter | 0.75rem | Small metadata, timestamps, and unit labels. |

**Editorial Note:** Use `on_surface_variant` (#595c5e) for secondary labels to create a sophisticated typographic contrast against the deep `on_surface` (#2c2f31) primary text.

---

## 4. Elevation & Depth
Depth is achieved through **Tonal Layering**, mimicking how light filters through clouds.

- **The Layering Principle:** Stack `surface-container` tiers. A `surface-container-high` element should only ever sit on a `surface-container` or `surface` base. Never skip more than two levels of "elevation" to maintain a soft transition.
- **Ambient Shadows:** Standard shadows are forbidden. If an element must "float" (e.g., a FAB or a critical alert), use an extra-diffused shadow: `Offset: 0, 8px | Blur: 32px | Color: rgba(0, 95, 156, 0.08)` (a tinted version of the primary color).
- **The "Ghost Border" Fallback:** If accessibility requires a container boundary, use the `outline_variant` token at **15% opacity**. 100% opaque borders are considered a "system failure."
- **Glassmorphism:** For the "Hourly Forecast" scroll, use semi-transparent cards that allow the background weather-state gradient to bleed through, softening the layout's edges.

---

## 5. Components
### Cards & Lists
*   **Forbid Dividers:** Do not use line separators. Use vertical whitespace (Spacing Scale `4` or `6`) or a shift from `surface` to `surface-container-low` to separate daily forecast items.
*   **Corner Radius:** Use `lg` (2rem) for main dashboard cards and `DEFAULT` (1rem) for inner data chips.

### Buttons & Selection
*   **Primary Button:** Pill-shaped (`full` roundedness), using the `primary` to `primary_container` gradient. No shadow.
*   **Selection Chips:** Use `secondary_container` (#cbe7f5) for the active state and `surface_container_high` (#dfe3e6) for inactive. Text should be `on_secondary_container`.

### Dynamic Weather Icons
*   Icons should be custom, "soft-edge" vectors. Use `primary` for water/rain elements and `tertiary` (#7f5200) for sun/warmth elements to create a high-contrast, intentional color story.

### Input Fields
*   **Styling:** Use a `surface-container-highest` fill with no border. Upon focus, transition the background to `surface-container-lowest` and apply a "Ghost Border" of `primary` at 20% opacity.

---

## 6. Do’s and Don’ts

### Do
*   **Do** use asymmetrical margins. For example, a "Current Temperature" display can be slightly off-center to feel more like a magazine layout than an app.
*   **Do** lean into white space. If a screen feels "busy," increase the spacing between containers using the `8` (2.75rem) scale.
*   **Do** use `tertiary_fixed` (#feb64c) sparingly for high-alert weather warnings (e.g., Heat Advisories) to break the blue-tone monotony.

### Don’t
*   **Don’t** use pure black (#000000) for shadows or text. It breaks the "atmospheric" immersion. Use `on_background` (#2c2f31).
*   **Don’t** use standard Material 2-style cards with heavy drop shadows.
*   **Don’t** crowd the screen. If you have 10 data points, hide 5 behind a "More Details" progressive disclosure to maintain the editorial aesthetic.
*   **Don’t** use 90-degree corners. Everything must feel "eroded" and soft, using the provided roundedness scale.