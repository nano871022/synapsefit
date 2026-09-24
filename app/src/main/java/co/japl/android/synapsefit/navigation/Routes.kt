package co.japl.android.synapsefit.navigation

object Routes {
    const val SPLASH = "splash"
    const val DASHBOARD = "dashboard"
    const val USER_PROFILE = "profile"
    const val MEASUREMENTS_ENTRY = "measurements/entry"
    const val MEASUREMENTS_PROGRESS = "measurements/progress"
    const val WORKOUT_PLANS = "workout/plans"
    const val WORKOUT_AI_GENERATOR = "workout/ai-generator"
    const val WORKOUT_DETAIL = "workout/detail/{planId}"

    fun workoutDetail(planId: String) = "workout/detail/$planId"

    const val WORKOUT_ACTIVE = "workout/active/{planId}?day={day}"

    fun workoutActive(
        planId: String,
        day: Int = 1,
    ) = "workout/active/$planId?day=$day"

    const val WORKOUT_HISTORY = "workout/history"
    const val WORKOUT_HISTORY_DETAIL = "workout/history/detail/{date}/{day}"

    fun workoutHistoryDetail(
        date: String,
        day: Int,
    ) = "workout/history/detail/$date/$day"

    const val SETTINGS_BACKUP = "settings/backup"
    const val DATABASE_EXPLORER = "settings/db-explorer"
    const val SETTINGS_LLM = "settings/llm"

    fun settingsLlm(openForm: Boolean = false) = "settings/llm?openForm=$openForm"

    const val SETTINGS_ABOUT = "settings/about"
}
