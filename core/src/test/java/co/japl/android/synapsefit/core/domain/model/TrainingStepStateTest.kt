package co.japl.android.synapsefit.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TrainingStepStateTest {
    @Test
    fun testExerciseSessionInitializationAndCompletion() {
        val session =
            ExerciseSession(
                exerciseId = "ex1",
                planId = "p1",
                name = "Bench Press",
                muscleGroup = "Chest",
                targetSets = 3,
                targetReps = "10",
                restSeconds = 60,
                completedSets = 0,
                isCompleted = false,
            )

        assertFalse(session.isCompleted)
        assertEquals(0, session.completedSets)

        val updated = session.copy(completedSets = 3, isCompleted = true)
        assertTrue(updated.isCompleted)
        assertEquals(3, updated.completedSets)
    }

    @Test
    fun testDeterministicTimerCalculation() {
        val targetTimestamp = 1000000L
        val currentTimestamp = 995000L

        val remaining =
            TrainingStepState.calculateRemainingMillis(
                targetTimestamp = targetTimestamp,
                currentTimestamp = currentTimestamp,
            )

        assertEquals(5000L, remaining)
    }

    @Test
    fun testTimerCalculationClampsAtZeroWhenElapsed() {
        val targetTimestamp = 1000000L
        val currentTimestamp = 1005000L

        val remaining =
            TrainingStepState.calculateRemainingMillis(
                targetTimestamp = targetTimestamp,
                currentTimestamp = currentTimestamp,
            )

        assertEquals(0L, remaining)
    }
}
