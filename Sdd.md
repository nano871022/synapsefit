# Software Design Document (SDD): SynapseFit

**Project Name:** SynapseFit  
**Application ID:** `co.japl.android.synapsefit`  
**Wear OS Application ID:** `co.japl.android.synapsefit.wear`  
**Architecture:** Multi-Module Hexagonal Architecture (Ports & Adapters)  
**SDK Support:** API 26 (Android 8.0) – API 36 (Android 16)  
**Primary Tech Stack:** Kotlin, Jetpack Compose, Room (SQLite), Kotlin Coroutines & Flow, Material 3, Wear OS Horologist, Jetpack Security.

---

## 1. System Architecture Overview

SynapseFit is an offline-first fitness and body metric application engineered to guarantee complete data sovereignty, AI-assisted training plan generation, and wearable telemetry integration.


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

---

## 2. Module Specifications

### `:app` (Application & Presentation Layer)
* **Namespace:** `co.japl.android.synapsefit`
* **Responsibility:** App entry points (`MainActivity`), dependency injection bindings, `AppNavHost` top-level routing, ViewModels, and passive UI screens.
* **Key Components:**
  * `AppNavigator`: Centralized navigation manager decoupling Compose UI and ViewModels from `NavController`.
  * `MenuHandler`: Manages `TopAppBar` global menu (`menu.xml` for LLM Settings and About) and `ModalNavigationDrawer` (Main App Sections).

### `:core` (Domain Layer - Pure Kotlin)
* **Namespace:** `co.japl.android.synapsefit.core`
* **Responsibility:** Business logic, use cases, domain entities, and port definitions. Zero Android framework dependencies.
* **Key Use Cases:**
  * `GenerateWorkoutPlanUseCase`: Aggregates user metrics, environment selection, and invokes the active LLM port.
  * `PerformDriveSyncUseCase`: Handles SQLite database encryption, SHA-256 hash validation, and Drive upload.
  * `RecordWorkoutSessionUseCase`: Processes real-time set entries, volume totals, and wearable heart-rate telemetry.

### `:services` (Infrastructure Layer - Adapters)
* **Namespace:** `co.japl.android.synapsefit.services`
* **Responsibility:** Implements interfaces defined in `:core`. Encapsulates external APIs and local storage.
* **Key Adapters:**
  * `RoomDatabaseAdapter`: Local SQLite persistence via Room (`synapsefit_database.db`).
  * `GoogleDriveAppDataAdapter`: REST client for `DriveScopes.DRIVE_APPDATA` private backup/restore.
  * `MultiLlmClientAdapter`: Provider switchboard handling Gemini, OpenAI, and Anthropic APIs with local credential encryption (`EncryptedSharedPreferences`).

### `:ui` (Design System Layer)
* **Namespace:** `co.japl.android.synapsefit.ui`
* **Responsibility:** Houses the "Kinetic Pulse" design tokens, typography scale, custom canvas graph renderers, and reusable atomic components.

### `:util` (Cross-Cutting Utilities)
* **Namespace:** `co.japl.android.synapsefit.util`
* **Responsibility:** Date/time formatters, SHA-256 checksum generators, math helpers, and Kotlin extensions.

### `:wear` (Wear OS Companion App)
* **Namespace:** `co.japl.android.synapsefit.wear`
* **Responsibility:** Circular standalone/connected UI, Health Services sensor integrations (Heart Rate BPM), and `WearableListenerService` for deferred Bluetooth data syncing.

---

## 3. Database Schema Specification (Room SQLite)

**Database Name:** `synapsefit_database.db`  
**Audit Requirements:** All primary tables enforce `created_at` and `updated_at` INTEGER (epoch timestamp) columns.

```sql
-- 1. Body Measurements
CREATE TABLE IF NOT EXISTS body_measurements (
    id TEXT PRIMARY KEY NOT NULL,
    weight_kg REAL NOT NULL,
    chest_cm REAL,
    waist_cm REAL,
    hip_cm REAL,
    bicep_left_cm REAL,
    bicep_right_cm REAL,
    thigh_left_cm REAL,
    thigh_right_cm REAL,
    notes TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

-- 2. Workout Plans
CREATE TABLE IF NOT EXISTS workout_plans (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    goal_description TEXT NOT NULL,
    is_active INTEGER NOT NULL DEFAULT 1,
    generated_by_llm INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

-- 3. Exercises
CREATE TABLE IF NOT EXISTS exercises (
    id TEXT PRIMARY KEY NOT NULL,
    plan_id TEXT NOT NULL,
    name TEXT NOT NULL,
    muscle_group TEXT NOT NULL,
    target_sets INTEGER NOT NULL,
    target_reps TEXT NOT NULL,
    rest_seconds INTEGER NOT NULL,
    guide_video_url TEXT,
    guide_image_url TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY(plan_id) REFERENCES workout_plans(id) ON DELETE CASCADE
);

-- 4. Workout Logs
CREATE TABLE IF NOT EXISTS workout_logs (
    id TEXT PRIMARY KEY NOT NULL,
    exercise_id TEXT NOT NULL,
    reps_completed INTEGER NOT NULL,
    weight_lifted_kg REAL NOT NULL,
    heart_rate_bpm INTEGER,
    source_device TEXT NOT NULL DEFAULT 'MOBILE', -- 'MOBILE' or 'WEAR_OS'
    timestamp INTEGER NOT NULL,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY(exercise_id) REFERENCES exercises(id) ON DELETE CASCADE
);

-- 5. LLM Configurations
CREATE TABLE IF NOT EXISTS llm_configs (
    id TEXT PRIMARY KEY NOT NULL,
    provider TEXT NOT NULL, -- 'GEMINI', 'OPENAI', 'ANTHROPIC'
    api_key_encrypted TEXT NOT NULL,
    model_name TEXT NOT NULL,
    is_active INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

4. Navigation & View Mapping
The presentation layer utilizes a single-activity architecture (MainActivity) coordinated via AppNavigator.
sealed class NavigationCommand {
    data class ToRoute(
        val route: String,
        val popUpToRoute: String? = null,
        val inclusive: Boolean = false
    ) : NavigationCommand()
    
    object NavigateUp : NavigationCommand()
}

interface AppNavigator {
    val navigationCommands: SharedFlow<NavigationCommand>
    suspend fun navigateTo(route: String, popUpToRoute: String? = null, inclusive: Boolean = false)
    suspend fun navigateUp()
}

Supported Screen Inventory
| View ID | Screen Name | Route String | Access Point |
|---|---|---|---|
| V1 | DashboardView | dashboard | Drawer |
| V2 | BodyMeasurementsView | measurements/entry | Drawer |
| V3 | MeasurementProgressGraphView | measurements/progress | Drawer |
| V4 | WorkoutPlansView | workout/plans | Drawer |
| V5 | AICoachGeneratorView | workout/ai-generator | FAB / Action |
| V6 | WorkoutPlanDetailView | workout/detail/{planId} | Plan Card |
| V7 | ActiveWorkoutSessionView | workout/active/{planId} | FAB / Start Session |
| V8 | WorkoutHistoryView | workout/history | Drawer |
| V9 | BackupSyncView | settings/backup | Drawer |
| V10 | LLMSettingsView | settings/llm | TopAppBar (menu.xml) |
| V11 | AboutDeveloperView | settings/about | TopAppBar (menu.xml) |
| V12 | WearOSActiveSessionView | Native Wear Activity | Wear OS App |
5. Design System Specification ("Kinetic Pulse")
Color Tokens
 * Background / Level 0: #101416
 * Surface Container / Level 1: #1C2023
 * Surface Container High / Level 2: #272A2D
 * Primary Accent (Electric Cyan): #00F5FF
 * Primary Fixed Dim: #00DCE5
 * On Primary: #003739
 * Tertiary Surface (Metrics): #FAF9FF
 * On Tertiary Text: #2E3035
 * Error Container: #93000A
Typography System
 * Display & Headline: Hanken Grotesk (Bold/ExtraBold for high-impact metric totals)
 * Body & Titles: Inter (Regular/SemiBold for functional text and settings)
 * Labels & Monospace Data: JetBrains Mono (Medium for sensor units, timestamps, SHA-256 hashes)
Adaptive Layout Rules (WindowSizeClass)
 * Compact (<600dp - Smartphones / Z Fold 4 Folded): 1-column stacked layouts, bottom navigation bar enabled.
 * Medium (600dp–840dp - Z Fold 4 Unfolded): 2-pane layouts (ListDetailPaneScaffold / SupportingPaneScaffold), navigation rail enabled.
 * Expanded (>840dp - Redmi Pad Pro / Tablets): Multi-column dashboard grid with persistent navigation drawer.
6. AI Architecture & Data Security
 * Multi-LLM Switchboard: Handles Gemini, OpenAI, and Anthropic API connections. Prioritizes active configuration ordered by ORDER BY updated_at DESC, created_at DESC LIMIT 1.
 * Credential Encryption: All API keys are encrypted using EncryptedSharedPreferences backed by the Android KeyStore (AES256_GCM) before being stored in Room.
 * Web 4.0 Backup Sovereignty: Backups use the private DriveScopes.DRIVE_APPDATA folder on Google Drive. Export files (co.japl.android.synapsefit.backup.enc) are compressed, encrypted, and validated via SHA-256 hash checksums.
7. CI/CD DevOps Pipeline (GitHub Actions)
 * Trigger: Pushes to main branch or pull requests with Conventional Commits (feat:, fix:, refactor:).
 * Automated Tasks:
   * Static code analysis (ktlint, detekt).
   * Unit and Architecture tests (./gradlew testDebugUnitTest).
   * Dynamic version increment from Git tags.
   * Android App Bundle (.aab) compilation and signing.
   * Deployment to Google Play Console Internal Testing track under Application ID co.japl.android.synapsefit.

