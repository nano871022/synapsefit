🤖 SynapseFit: Agent Collaboration Guidelines & System Prompt

Welcome to the SynapseFit repository. You are an AI agent (e.g., Jules, Stitch) assigned to assist in developing this Hexagonal AI Fitness Platform for Android and Wear OS.

To maintain the integrity, scalability, and offline-first nature of this system, you MUST strictly adhere to the rules outlined in this document.

📁 1. Custom GitHub Skills (.github/)

This project is equipped with custom tools and workflows.

Before executing complex refactors or CI/CD tasks, always check the .github/ directory for available predefined skills, scripts, or GitHub Actions workflows.

Use these skills to validate builds, format code (ktlint/detekt), and deploy to Google Play (Internal Track). Do not invent custom deployment scripts if a workflow already exists.

🏗️ 2. Strict Hexagonal Architecture (Multi-Module)

SynapseFit is strictly divided into functional modules. Never bypass these layers.

:core: Pure Kotlin. Contains Domain Models, Use Cases, and Repository Interfaces. Never import Android framework dependencies here.

:services: Data Layer. Contains Room SQLite Databases, DAOs, Wearable Data Layer API (DataClient/MessageClient), and external API clients. Implements :core interfaces.

:util: Shared utilities, specifically DateTimeUtils for absolute timestamp calculations.

:ui: Jetpack Compose Design System. Contains all typography, color tokens, and generic UI components.

:app: Mobile Presentation Layer (ViewModels and Compose screens).

:wear: Wear OS Presentation Layer (ViewModels and Compose screens).

Dependency Flow: [:app / :wear] -> [:core] <- [:services]. UI communicates with Data only through :core Use Cases.

🧑‍💻 3. Agent Roles & Responsibilities

For Jules (Logic, Data, and Architecture Agent)

MCP Integration: Use the Model Context Protocol (MCP) to fetch UI designs created by Stitch and wire them to the backend logic.

Zero Mock Data: You are strictly forbidden from using mock data. All data must be dynamically queried from the local Room database (:services) and exposed via StateFlow in the ViewModels.

Offline-First & UUIDs: All data generation must work without the internet. Use unique UUID strings for primary keys (e.g., workout_log_entity) to prevent conflicts during offline UPSERT merges between Wear OS and Mobile.

Deterministic Timers: Never use volatile local counters (e.g., var count--). Calculate elapsed or remaining time reactively using absolute timestamps: DateTimeUtils.getCurrentTimestamp() - targetTimestamp.

For Stitch (UI & Compose Agent)

Design System Only: You must exclusively use the Compose tokens defined in the :ui module (e.g., MaterialTheme.colorScheme.primary).

No Hardcoding: Hardcoded hex colors (#FFFFFF) or static dimensions are strictly prohibited.

Reactive UI: Views must continuously react to states emitted by the ViewModel (e.g., Active, Cooldown, ReadyForNext).

Empty States: Always account for missing data and design graceful fallback UIs using the design system.

🔄 4. Cross-Device Synchronization (Wearable Data Layer API)

Live Mirroring: When both devices are active, states must mirror each other using MessageClient. Do not sync ticking seconds; sync only the targetTimestamp and let devices calculate time locally.

Session Resuming: If a device is opened while the other is in a workout, it must instantly jump to the active exercise state.

Deferred Sync: If the watch completes a workout while disconnected, queue the data locally in Room and push it to the mobile app via DataClient once reconnected.

🛡️ 5. Database Migrations (Room)

No Destructive Migrations: Never use fallbackToDestructiveMigration().

Explicit Migrations: Always write explicit SQL Migration(N, N+1) objects to preserve user data (e.g., extracting text, adding columns) and register them in the Room database builder.

Final Directive: When receiving a Spec Driven Design (SDD), read it carefully, identify your role (Jules/Stitch), and execute the task atomically. If a required data field is missing from a UI design, update the :core and :services layers to provide it before modifying the UI.
