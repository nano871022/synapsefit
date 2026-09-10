package co.japl.android.synapsefit.navigation

import co.japl.android.synapsefit.ui.navigation.WearRoutes
import org.junit.Assert.assertEquals
import org.junit.Test

class WearRoutesTest {
    @Test
    fun testRouteFormattingAndArguments() {
        assertEquals("day_selection", WearRoutes.DAY_SELECTION)
        assertEquals("pre_workout/{planId}/{day}", WearRoutes.PRE_WORKOUT)
        assertEquals("active_workout/{planId}/{day}", WearRoutes.ACTIVE_WORKOUT)
        assertEquals("cooldown/{planId}/{day}", WearRoutes.COOLDOWN)
        assertEquals("post_workout_summary/{planId}/{day}", WearRoutes.POST_WORKOUT_SUMMARY)

        assertEquals("pre_workout/plan_123/2", WearRoutes.preWorkout("plan_123", 2))
        assertEquals("active_workout/plan_123/2", WearRoutes.activeWorkout("plan_123", 2))
        assertEquals("cooldown/plan_123/2", WearRoutes.cooldown("plan_123", 2))
        assertEquals("post_workout_summary/plan_123/2", WearRoutes.postWorkoutSummary("plan_123", 2))
    }

    @Test
    fun testRouteFormattingWithBlankPlanIdFallback() {
        assertEquals("pre_workout/default/1", WearRoutes.preWorkout("", 1))
        assertEquals("active_workout/default/1", WearRoutes.activeWorkout("", 1))
        assertEquals("cooldown/default/1", WearRoutes.cooldown("", 1))
        assertEquals("post_workout_summary/default/1", WearRoutes.postWorkoutSummary("", 1))
    }
}
