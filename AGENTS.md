# AGENTS.md - System Rules & Agent Instructions for SynapseFit (`co.japl.android.synapsefit`)

> **Repository Context:** SynapseFit is an offline-first, multi-module Android & Wear OS application built with Jetpack Compose, Room SQLite, multi-LLM integration, and a Hexagonal Architecture.

---

## 1. Core System Rules

1. **Passive / Dumb UI:** All `@Composable` components must remain strictly passive. They must never perform business logic, execute database queries, or directly invoke API endpoints. All actions must be emitted via event lambdas to the ViewModel/Controller.
2. **Decoupled Navigation:** Screens must never reference `NavController` directly. All routing must be delegated to the `AppNavigator` interface.
3. **Atomic Execution:** Code modifications must be executed as isolated, 1-task-per-execution operations. Do not introduce speculative features or refactor unrelated modules in a single step.
4. **Pure `:core` Domain (Strict Rule):** The `:core` module is pure Kotlin. It must contain ZERO Android dependencies (`android.*`). All Android framework interactions belong in `:services`, `:app`, `:ui`, `:util`, or `:wear`.
5. **Data Sovereignty:** All user health data and logs must persist locally in Room DB (`synapsefit_database.db`) first. Remote operations (Google Drive AppData sync) are secondary adapters.

---

## 2. Skills & Knowledge Base Index

The repository contains technical skill guides under `.github/skills/`:

| Domain / Subsystem | Skill Reference File | Scope |
| :--- | :--- | :--- |
| **Hexagonal Architecture** | `.github/skills/hexagonal-architecture.md` | Port & Adapter interfaces, UseCase conventions, domain boundaries. |
| **Compose UI System** | `.github/skills/compose-ui.md` | "Kinetic Pulse" Design Tokens, Material 3, `WindowSizeClass` reflow rules. |
| **Testing Strategy** | `.github/skills/testing.md` | JVM Unit testing, Turbine, MockK, Room in-memory testing. |
| **Quality & Architecture Compliance** | `.github/skills/quality-check.md` | Architectural guardrails, ktlint, detekt, audit field enforcement. |

---

## 3. Multi-Module Hierarchy & Rules

```text
:app (Application ID: co.japl.android.synapsefit)
 ├── depends on ──> :core
 ├── depends on ──> :services
 ├── depends on ──> :ui
 └── depends on ──> :util

:services (Namespace: co.japl.android.synapsefit.services)
 ├── depends on ──> :core
 └── depends on ──> :util

:wear (Application ID / Namespace: co.japl.android.synapsefit.wear)
 ├── depends on ──> :core
 ├── depends on ──> :ui
 └── depends on ──> :util

:ui (Namespace: co.japl.android.synapsefit.ui) & :util (Namespace: co.japl.android.synapsefit.util)
 └── standalone leaf modules

:core (Pure Kotlin module — zero android.* dependencies)
 └── pure Kotlin (NO DEPENDENCIES)
