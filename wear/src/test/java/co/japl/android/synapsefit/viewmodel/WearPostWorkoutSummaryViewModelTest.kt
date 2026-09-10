package co.japl.android.synapsefit.viewmodel

import co.japl.android.synapsefit.core.domain.model.history.ExerciseHistory
import co.japl.android.synapsefit.core.domain.model.history.ExerciseSetHistory
import co.japl.android.synapsefit.core.domain.model.history.WorkoutHistoryGroup
import co.japl.android.synapsefit.ui.viewmodel.WearPostWorkoutSummaryViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class WearPostWorkoutSummaryViewModelTest {
    @Test
    fun testPopulateStateFromGroupCalculatesMetricsCorrectly() {
        val viewModel = WearPostWorkoutSummaryViewModel()

        val dummySets =
            listOf(
                ExerciseSetHistory(
                    setIndex = 1,
                    repsCompleted = 10,
                    weightLiftedKg = 80.0,
                    heartRateBpm = 130,
                    durationSeconds = 45,
                    timestamp = System.currentTimeMillis(),
                ),
                ExerciseSetHistory(
                    setIndex = 2,
                    repsCompleted = 10,
                    weightLiftedKg = 80.0,
                    heartRateBpm = 150,
                    durationSeconds = 45,
                    timestamp = System.currentTimeMillis(),
                ),
            )

        val exerciseHistory =
            ExerciseHistory(
                exerciseId = "ex_1",
                exerciseName = "Bench Press",
                muscleGroup = "Chest",
                sets = dummySets,
                averageReps = 10.0,
                averageWeightKg = 80.0,
            )

        val group =
            WorkoutHistoryGroup(
                sessionId = "session_1",
                planId = "plan_1",
                planTitle = "Hypertrophy 4-Day",
                day = 1,
                timestamp = System.currentTimeMillis(),
                exercises = listOf(exerciseHistory),
                totalVolumeKg = 1600.0,
                // 45 mins total duration
                totalDurationSeconds = 2700,
                muscleGroups = listOf("Chest"),
            )

        viewModel.populateStateFromGroup(group)

        val state = viewModel.uiState.value
        assertEquals("Hypertrophy 4-Day", state.planTitle)
        assertEquals(1, state.day)
        assertEquals("45:00", state.formattedDuration)
        assertEquals(1600.0, state.totalVolumeKg, 0.01)
        assertEquals(140, state.averageHeartRateBpm)
        assertEquals(150, state.maxHeartRateBpm)
        assertEquals(1, state.exercises.size)
        assertFalse(state.isLoading)
    }
}
