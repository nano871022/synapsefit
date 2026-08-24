# AI Agent Instructions - SynapseFit (`co.japl.android.synapsefit`)

> **Repository Context:** This repository contains the source code for **SynapseFit**, an offline-first, multi-module Android & Wear OS application built with Jetpack Compose, Room SQLite, multi-LLM integration, and a Hexagonal Architecture.

---

## 1. Core Engineering Principles

1. **Passive/Dumb UI:** All `@Composable` components must remain strictly passive. They must never perform business logic, execute database queries, or directly invoke API endpoints. All actions must be emitted via event lambdas to the ViewModel/Controller.
2. **Decoupled Navigation:** Screens must never reference `NavController` directly. All routing must be delegated to the `AppNavigator` interface.
3. **Atomic & Granular Execution (Jules Flow):** Code modifications must be executed as isolated, 1-task-per-execution operations. Do not introduce speculative features or refactor unrelated modules in a single step.
4. **Pure `:core` Domain:** The `:core` module is pure Kotlin. It must contain zero Android dependencies (`android.*`). All Android framework interactions belong in `:services`, `:app`, `:ui`, `:util`, or `:wear`.
5. **Data Sovereignty:** All user health data and logs must persist locally in Room DB (`synapsefit_database.db`) first. Remote operations (Google Drive AppData sync) are secondary adapters.

---

## 2. Skills & Knowledge Base Index

When performing tasks in specific domains of this repository, inspect and adhere to the specialized guidelines located in `.github/skills/`:

| Domain / Subsystem | Skill Reference File | Primary Scope |
| :--- | :--- | :--- |
| **Hexagonal Architecture** | `.github/skills/hexagonal-architecture.md` | Port & Adapter interfaces, UseCase conventions, domain boundaries. |
| **Compose & UI System** | `.github/skills/compose-ui.md` | "Kinetic Pulse" Design Tokens, Material 3, `WindowSizeClass` reflow rules. |
| **Data & Persistence** | `.github/skills/room-persistence.md` | Room DAOs, migration scripts, audit fields (`created_at`, `updated_at`). |
| **LLM & External APIs** | `.github/skills/llm-adapters.md` | Gemini/OpenAI/Anthropic switchboard, local credential encryption. |
| **Wear OS Companion** | `.github/skills/wear-os.md` | Wear Compose, Health Services API, Horologist, deferred Bluetooth sync. |

---

## 3. Module Boundaries & Rules

```text
:app (Application & UI Screens)
 ├── depends on ──> :core
 ├── depends on ──> :ui
 └── depends on ──> :util

:services (Infrastructure & Adapters)
 ├── depends on ──> :core
 └── depends on ──> :util

:wear (Wear OS Application)
 ├── depends on ──> :core
 ├── depends on ──> :ui
 └── depends on ──> :util

:ui (Design System) & :util (Helpers)
 └── standalone leaf modules

:core (Domain Models & Ports)
 └── pure Kotlin (NO DEPENDENCIES)
