package co.japl.android.synapsefit.ui.navigation

object WearRoutes {
    const val DAY_SELECTION = "day_selection"
    const val PRE_WORKOUT = "pre_workout/{planId}/{day}"
    const val ACTIVE_WORKOUT = "active_workout/{planId}/{day}"
    const val COOLDOWN = "cooldown/{planId}/{day}"
    const val POST_WORKOUT_SUMMARY = "post_workout_summary/{planId}/{day}"

    const val ARG_PLAN_ID = "planId"
    const val ARG_DAY = "day"

    fun preWorkout(
        planId: String,
        day: Int,
    ): String {
        val safePlanId = planId.ifBlank { "default" }
        return "pre_workout/$safePlanId/$day"
    }

    fun activeWorkout(
        planId: String,
        day: Int,
    ): String {
        val safePlanId = planId.ifBlank { "default" }
        return "active_workout/$safePlanId/$day"
    }

    fun cooldown(
        planId: String,
        day: Int,
    ): String {
        val safePlanId = planId.ifBlank { "default" }
        return "cooldown/$safePlanId/$day"
    }

    fun postWorkoutSummary(
        planId: String,
        day: Int,
    ): String {
        val safePlanId = planId.ifBlank { "default" }
        return "post_workout_summary/$safePlanId/$day"
    }
}
