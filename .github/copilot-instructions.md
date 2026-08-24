# GitHub Copilot Instructions - SynapseFit (`co.japl.android.synapsefit`)

Refer to `AGENTS.md` in the root directory and `.github/skills/` for technical specifications and architecture guidelines.

## Quick Guardrails:
1. Pure `:core` module: Zero `android.*` dependencies.
2. Passive UI: Composables accept state and emit event lambdas. No business logic in Composables.
3. Decoupled navigation: Do not pass `NavController` into Composables.
4. Database Audit Fields: SQLite tables must contain `created_at` and `updated_at` timestamps.
