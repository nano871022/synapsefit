package co.japl.android.synapsefit.navigation

object Routes {
    const val DASHBOARD = "dashboard"
    const val USER_PROFILE = "profile"
    const val MEASUREMENTS_ENTRY = "measurements/entry"
    const val MEASUREMENTS_PROGRESS = "measurements/progress"
    const val WORKOUT_PLANS = "workout/plans"
    const val WORKOUT_AI_GENERATOR = "workout/ai-generator"
    const val WORKOUT_DETAIL = "workout/detail/{planId}"

    fun workoutDetail(planId: String) = "workout/detail/$planId"

    const val WORKOUT_ACTIVE = "workout/active/{planId}"

    fun workoutActive(planId: String) = "workout/active/$planId"

    const val WORKOUT_HISTORY = "workout/history"
    const val SETTINGS_BACKUP = "settings/backup"
    const val SETTINGS_LLM = "settings/llm"

    fun settingsLlm(openForm: Boolean = false) = "settings/llm?openForm=$openForm"

    const val SETTINGS_ABOUT = "settings/about"
}
