# Skill: Quality Assurance, Code Standards & Architecture Compliance

> **File Location:** `.github/skills/quality-check.md`  
> **Target Scope:** Static analysis, code formatting, architectural guardrails, and pull request readiness for `co.japl.android.synapsefit`.

---

## 1. Architectural Guardrails (Non-Negotiable Rules)

Every pull request or code modification must pass the following core architectural checks before approval:

| Rule ID | Module | Enforced Rule | Verification Method |
| :--- | :--- | :--- | :--- |
| **ARCH-01** | `:core` | **Pure Kotlin Only:** Zero imports from `android.*`, `androidx.*`, or Room annotations. | Static analysis / Compilation |
| **ARCH-02** | `:app` / `:wear` | **Passive UI:** No direct DB queries, HTTP calls, or domain mutations inside `@Composable` functions or ViewModels. | Code review / UI Test rules |
| **ARCH-03** | `:app` / `:wear` | **Decoupled Navigation:** Composables must not accept `NavController` directly. Must use `AppNavigator`. | Inspection |
| **ARCH-04** | `:services` | **Domain Isolation:** Room `@Entity` and REST DTO classes must never leak to `:app` or `:core`. Mappers are mandatory. | Package inspection |
| **ARCH-05** | All | **Audit Enforcement:** Every primary entity in SQLite must populate non-null `created_at` and `updated_at` epoch timestamps. | Room DAO / Unit tests |

---

## 2. Code Quality & Formatting Guidelines

### A. Formatting & Style Standards
* **Kotlin Style Guide:** Enforce official Kotlin coding conventions using `ktlint`.
* **Naming Conventions:**
  * **Composables:** PascalCase Nouns (`DashboardScreen`, `SetTrackingTable`).
  * **Use Cases:** PascalCase Verb + Subject + UseCase (`GenerateWorkoutPlanUseCase`).
  * **Interfaces / Ports:** PascalCase describing capability (`BodyMeasurementRepositoryPort`).
  * **State Flow Variables:** Named `uiState` (`StateFlow<UiState>`) or `navigationCommands` (`SharedFlow<NavigationCommand>`).

### B. Security & Credentials Rules
* **Zero Hardcoded Secrets:** API keys, certificates, or tokens must NEVER be committed to version control.
* **Encrypted Storage:** LLM API keys must be encrypted using `EncryptedSharedPreferences` backed by the Android KeyStore (`AES256_GCM`).
* **Private Cloud Isolation:** Google Drive backups must use `DriveScopes.DRIVE_APPDATA` to prohibit file access to other applications.

---

## 3. Static Analysis Configuration

### A. Detekt Rules Focus (`detekt.yml`)
* **ComplexMethod:** Max cyclomatic complexity set to `15`.
* **LongMethod:** Max length set to `60` lines for standard functions, `100` lines for complex Composables.
* **TooManyFunctions:** Max threshold set to `10` public functions per class/file.
* **UnusedPrivateMember:** Strictly enabled (no orphan functions or imports).

### B. ktlint Rules
* Explicit trailing commas on multiline arguments enabled.
* Wildcard imports (`import x.y.z.*`) strictly prohibited.
* Indentation set to 4 spaces (no tabs).

---

## 4. Code Review & Verification Checklist

Before submitting a Pull Request or completing a task, verify the code against this checklist:

### 1. Architecture & Boundaries
* [ ] No `android.*` packages imported inside `:core`.
* [ ] Mappers explicitly convert Room `@Entity` and DTOs into pure Domain Models.
* [ ] ViewModels consume `:core` Use Cases via Dependency Injection rather than direct repository implementation.

### 2. Jetpack Compose & UI
* [ ] Composables accept an immutable `UiState` and emit user interaction lambdas (State Hoisting).
* [ ] `@Preview` annotations are included with default mock data wrapped in `SynapseFitTheme`.
* [ ] UI elements support adaptive reflow for Foldables (`WindowSizeClass`) and Dark Mode palette (`#101416`).

### 3. Data & Security
* [ ] New or modified database tables include `created_at` and `updated_at` INTEGER columns.
* [ ] SHA-256 integrity checksums are generated for exportable backup files.
* [ ] Credentials use Android KeyStore encryption (`EncryptedSharedPreferences`).

### 4. Testing
* [ ] Unit tests for new `:core` Use Cases pass (`./gradlew :core:test`).
* [ ] DAO changes include in-memory Room integration tests.
* [ ] State Flow emissions are verified using `Turbine`.

---

## 5. Automated Execution Commands

Run these commands locally to ensure quality compliance before committing:

```bash
# 1. Format code according to ktlint rules
./gradlew ktlintFormat

# 2. Execute static analysis and linting
./gradlew ktlintCheck detekt

# 3. Run Android Lint for performance and accessibility issues
./gradlew lintDebug

# 4. Execute all unit and architecture tests
./gradlew testDebugUnitTest


