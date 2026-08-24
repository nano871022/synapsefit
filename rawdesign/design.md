---
name: Kinetic Pulse
colors:
  surface: '#101416'
  surface-dim: '#101416'
  surface-bright: '#363a3c'
  surface-container-lowest: '#0b0f11'
  surface-container-low: '#181c1e'
  surface-container: '#1c2023'
  surface-container-high: '#272a2d'
  surface-container-highest: '#323538'
  on-surface: '#e0e3e6'
  on-surface-variant: '#b9caca'
  inverse-surface: '#e0e3e6'
  inverse-on-surface: '#2d3133'
  outline: '#849495'
  outline-variant: '#3a494a'
  surface-tint: '#00dce5'
  primary: '#e9feff'
  on-primary: '#003739'
  primary-container: '#00f5ff'
  on-primary-container: '#006c71'
  inverse-primary: '#00696e'
  secondary: '#c6c6c9'
  on-secondary: '#2f3133'
  secondary-container: '#454749'
  on-secondary-container: '#b4b5b7'
  tertiary: '#faf9ff'
  on-tertiary: '#2e3035'
  tertiary-container: '#dddde4'
  on-tertiary-container: '#5f6167'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#63f7ff'
  primary-fixed-dim: '#00dce5'
  on-primary-fixed: '#002021'
  on-primary-fixed-variant: '#004f53'
  secondary-fixed: '#e2e2e5'
  secondary-fixed-dim: '#c6c6c9'
  on-secondary-fixed: '#1a1c1e'
  on-secondary-fixed-variant: '#454749'
  tertiary-fixed: '#e2e2e8'
  tertiary-fixed-dim: '#c5c6cc'
  on-tertiary-fixed: '#191c20'
  on-tertiary-fixed-variant: '#45474c'
  background: '#101416'
  on-background: '#e0e3e6'
  surface-variant: '#323538'
typography:
  display-lg:
    fontFamily: Hanken Grotesk
    fontSize: 48px
    fontWeight: '800'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Hanken Grotesk
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Hanken Grotesk
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
  title-md:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-sm:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  gutter: 16px
  margin-edge: 16px
---

## Brand & Style
The design system is engineered for a high-performance health and fitness experience on Android. It prioritizes a **Modern Corporate** aesthetic blended with **Minimalist** efficiency. The UI is designed to feel "athletic-tech"—fast, responsive, and data-driven.

The core philosophy utilizes a **Module UI** approach: every piece of data is encapsulated in a discrete container, allowing for a highly flexible, dashboard-like layout. The emotional response should be one of clarity, motivation, and technological precision. Whitespace is used strategically to prevent data-density fatigue, ensuring that critical health metrics are immediately legible.

## Colors
The palette is rooted in a deep **Dark Mode** first approach to reduce eye strain during early morning or late-night workouts. 

- **Primary (Electric Cyan):** Used for key actions, active progress states, and brand highlights. It represents energy and movement.
- **Secondary/Tertiary:** These form the "Module" surfaces. They sit slightly above the pure black background to provide depth.
- **Semantic Colors:** Green is reserved for positive trends (improvement/goals met), while Red indicates regression or missed targets.
- **Surface Tints:** Interactive elements receive a subtle primary-colored overlay (8-12% opacity) when pressed or focused, following Material 3 principles.

## Typography
The typography system balances high-impact motivation with technical precision.

- **Headlines:** Use **Hanken Grotesk** for its sharp, contemporary feel. Bold weights are used for metric totals (e.g., step counts) to make them the focal point.
- **Body:** **Inter** is used for all functional text, settings, and descriptions to ensure maximum legibility at smaller sizes.
- **Technical Labels:** **JetBrains Mono** is used for secondary data labels (e.g., timestamps, Google Drive sync status, or sensor units). This monospaced choice reinforces the "tech-forward" and precise nature of health tracking.

## Layout & Spacing
This design system follows a **Fluid Grid** logic optimized for the Android handheld experience, adhering to a 4dp/8dp rhythm.

- **Module Layout:** Content is organized into "Modules" (Cards). Modules typically span the full width of the screen (minus margins) or sit side-by-side in a 2-column grid.
- **Safe Areas:** A minimum 16px horizontal margin is maintained globally.
- **Vertical Rhythm:** A 16px gutter between vertical modules creates a clear separation of data points.
- **Adaptive Reflow:** On larger foldable screens or tablets, the 1-column mobile list reflows into a multi-column dashboard grid.

## Elevation & Depth
Elevation is communicated through **Tonal Layers** rather than heavy shadows, consistent with modern Jetpack Compose standards.

- **Level 0 (Background):** Pure dark neutral.
- **Level 1 (Module Surface):** A slightly lighter gray-blue. All primary data cards live here.
- **Level 2 (Overlays/Modals):** Lighter still, with a subtle 10% primary color tint to imply activity.
- **Outlines:** Instead of shadows, interactive containers use a 1px low-contrast stroke (`#FFFFFF` at 10% opacity) to define boundaries against the dark background.

## Shapes
The shape language is consistently **Rounded**. 

Large containers like metric cards use a 1rem (16px) radius to feel friendly and modern. Buttons and input fields use the same 0.5rem (8px) base radius. Small status badges and chips utilize a fully "Pill-shaped" radius (100px) to distinguish them as secondary interactive or informational elements.

## Components

### Buttons
- **Primary Action:** Solid Primary color with black text for maximum contrast. Use uppercase `label-sm` typography.
- **Secondary:** Outlined with 1px Primary color stroke.
- **FAB (Floating Action Button):** Prominent Primary color circle for "Start Workout" or "Log Data."

### Cards (Modules)
- Background: `tertiary_color_hex`.
- Padding: 16px internal padding.
- Content: Headline for the metric (e.g., 8,500), Label for the unit (e.g., STEPS), and a small Sparkline for trend visualization.

### Progress Indicators
- **Circular Rings:** Use for daily goals. The background track should be `secondary_color_hex`, and the active track should be `primary_color_hex` with a glow effect (subtle blur).

### Status Badges
- Used for "Google Drive Sync."
- **Synced:** Green background (low opacity) with Green text.
- **Syncing:** Primary color text with a small rotating icon.

### Inputs
- Filled style with a bottom-only indicator line. Background matches the card surface for a seamless "Module" feel.
