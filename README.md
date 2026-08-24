# SynapseFit (`co.japl.android.synapsefit`)

> An offline-first, multi-device Android & Wear OS fitness platform engineered with a multi-module Hexagonal Architecture. SynapseFit combines AI-driven workout generation, precise body metric tracking, and total user data sovereignty.

![Android API](https://img.shields.io/badge/API-26%20%E2%80%93%2036-brightgreen)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0%2B-purple)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue)
![Architecture](https://img.shields.io/badge/Architecture-Hexagonal%20Multi--Module-orange)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 🚀 Key Features

* **Context-Aware AI Coach:** Multi-LLM integration (Gemini, OpenAI, Anthropic) with local encrypted API key storage. Generates customized routines tailored to home calisthenics, free weights, or specific gym chain equipment (with automated web inventory inspection).
* **Dimensional Metric Tracking:** Comprehensive body measurement logging across anatomical zones (chest, waist, hips, biceps, thighs) with interactive trend graphs and historical records.
* **Offline-First & Privacy (Web 4.0):** Pure local persistence via Room (SQLite) paired with automated, encrypted backups to the private `AppData` folder on Google Drive (secured with SHA-256 integrity hashing).
* **Multi-Device & Wear OS Companion:** Fully adaptive Jetpack Compose UI optimized for smartphones, foldables (e.g., Z Fold 4), tablets, and Wear OS smartwatches (e.g., Galaxy Watch 4) featuring real-time heart rate telemetry and deferred sync.

---

## 🧩 Multi-Module Hexagonal Architecture

The project is strictly structured into decoupled modules to ensure high testability, separation of concerns, and atomic development:

```text
root/
├── :app          # Entry points, navigation controller, and UI screens
├── :core         # Domain models, business logic use cases, and interfaces
├── :services     # Room database, Google Drive API, and LLM network adapters
├── :ui           # SynapseFit Design System (Compose tokens, theme, components)
├── :util         # Shared extension functions, formatters, and helpers
└── :wear         # Wear OS companion app (optimized circular UI & local sensors)
