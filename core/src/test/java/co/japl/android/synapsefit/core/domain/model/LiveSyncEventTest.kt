package co.japl.android.synapsefit.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class LiveSyncEventTest {
    @Test
    fun testStartSessionDataClass() {
        val event = LiveSyncEvent.StartSession(planId = "plan_123", day = 2, sessionStartTimestamp = 1000L)
        assertEquals("plan_123", event.planId)
        assertEquals(2, event.day)
        assertEquals(1000L, event.sessionStartTimestamp)
    }

    @Test
    fun testSelectExerciseDataClass() {
        val event = LiveSyncEvent.SelectExercise(exerciseId = "ex_1", exerciseName = "Bench Press")
        assertEquals("ex_1", event.exerciseId)
        assertEquals("Bench Press", event.exerciseName)
    }

    @Test
    fun testCompleteSetDataClass() {
        val event =
            LiveSyncEvent.CompleteSet(
                exerciseId = "ex_1",
                setIndex = 3,
                reps = 12,
                weightKg = 60.0,
                targetTimestamp = 50000L,
                cooldownDurationSeconds = 60,
            )
        assertEquals("ex_1", event.exerciseId)
        assertEquals(3, event.setIndex)
        assertEquals(12, event.reps)
        assertEquals(60.0, event.weightKg, 0.001)
        assertEquals(50000L, event.targetTimestamp)
        assertEquals(60, event.cooldownDurationSeconds)
    }

    @Test
    fun testSkipExerciseDataClass() {
        val event = LiveSyncEvent.SkipExercise(exerciseId = "ex_2")
        assertEquals("ex_2", event.exerciseId)
    }

    @Test
    fun testFinishSessionDataClass() {
        val event = LiveSyncEvent.FinishSession(planId = "plan_123", totalDurationSeconds = 1800L)
        assertEquals("plan_123", event.planId)
        assertEquals(1800L, event.totalDurationSeconds)
    }
}
