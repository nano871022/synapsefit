# Skill: Multi-Module Hexagonal Architecture (Ports & Adapters)

> **File Location:** `.github/skills/hexagonal-architecture.md`
> **Target Scope:** Architecture patterns, domain boundaries, ports, adapters, and module dependencies in `co.japl.android.synapsefit`.

---

## 1. Hexagonal Architecture Core Principles

SynapseFit strictly follows a Ports & Adapters (Hexagonal) architecture organized in isolated Gradle modules:

* **Inside (Domain / `:core`):** Contains pure business logic, domain entities, use cases, primary ports (use case interfaces), and secondary ports (repository/adapter interfaces). Zero external framework dependencies.
* **Outside (Adapters / `:services`, `:app`, `:wear`, `:ui`):** Implements secondary ports (Room DB, Drive AppData API, LLM API client switchboard) and primary ports (UI presentation, ViewModels, navigation).

---

## 2. Module Boundaries & Dependency Flow

```text
+-------------------------------------------------------+
|                    :app Module                        |
|  (Dumb UI Screens, AppNavHost, ViewModels, DI)       |
+--------------------------+----------------------------+
|
v
+-------------------------------------------------------+
|                    :core Module                       |
|  (Domain Models, UseCases, Primary/Secondary Ports)   |
+------------+-----------------------------+------------+
|                             |
v                             v
+--------------------------------------+   +----------------------------------+
|          :services Module            |   |            :ui Module            |
|  (Room DB, Drive API, LLM Adapters)  |   |  (Design System, Design Tokens)  |
+--------------------------------------+   +----------------------------------+
^                             ^
|                             |
+------------+-----------------------------+------------+
|                    :wear Module                       |
|  (Standalone Wear OS App, Sensors, Deferred Sync)     |
+-------------------------------------------------------+
```

### Module Directives:
1. **`:core` (Pure Kotlin):**
   - **STRICT RULE: Zero `android.*` dependencies.**
   - Defines Domain Models (e.g., `BodyMeasurement`, `WorkoutPlan`, `Exercise`, `WorkoutLog`, `LlmConfig`).
   - Defines Secondary Ports (interfaces e.g. `BodyMeasurementRepositoryPort`, `LlmClientPort`).
   - Implements Use Cases (e.g., `SaveBodyMeasurementUseCase`, `GenerateWorkoutPlanUseCase`).
2. **`:services` (Infrastructure & Adapters):**
   - Implements secondary ports declared in `:core`.
   - Contains Room DAOs and `@Entity` models.
   - Converts Room Entities <-> Core Domain Models via explicit mappers.
3. **`:app` & `:wear` (Presentation / App Shells):**
   - Responsible for entry points, ViewModels, Compose screens, and navigation.
4. **`:ui` & `:util` (Design System & Cross-Cutting Helpers):**
   - Standalone leaf modules containing reusable design components/tokens and helper extensions.

---

## 3. Naming Conventions & Patterns

* **Primary/Secondary Ports:** `[Entity]RepositoryPort`, `[Feature]ClientPort`.
* **Use Cases:** `[Verb][Noun]UseCase` (e.g., `GenerateWorkoutPlanUseCase`).
* **Adapters:** `[Technology][Domain]Adapter` (e.g., `RoomDatabaseAdapter`, `GoogleDriveAppDataAdapter`).
