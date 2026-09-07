@file:Suppress("MaxLineLength")

package co.japl.android.synapsefit.app.controller.workout

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WorkoutSessionStateManagerTest {
    private val context = mockk<Context>(relaxed = true)
    private val prefs = mockk<SharedPreferences>(relaxed = true)
    private val editor = mockk<SharedPreferences.Editor>(relaxed = true)

    @Before
    fun setUp() {
        every { context.getSharedPreferences(any(), Context.MODE_PRIVATE) } returns prefs
        every { prefs.edit() } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.putLong(any(), any()) } returns editor
        every { editor.putInt(any(), any()) } returns editor
        every { editor.remove(any()) } returns editor
    }

    @Test
    fun testHasActiveSession_whenTrue_returnsTrue() {
        every { prefs.getBoolean("has_active_session", false) } returns true

        val result = WorkoutSessionStateManager.hasActiveSession(context)

        assertTrue(result)
    }

    @Test
    fun testLoadSession_whenNoActiveSession_returnsNull() {
        every { prefs.getBoolean("has_active_session", false) } returns false

        val result = WorkoutSessionStateManager.loadSession(context)

        assertNull(result)
    }

    @Test
    fun testLoadSession_whenActiveSessionExists_returnsRestoredState() {
        every { prefs.getBoolean("has_active_session", false) } returns true
        every { prefs.getString("plan_id", "") } returns "plan_123"
        every { prefs.getString("plan_title", "") } returns "Plan Fuerza (Día 1)"
        every { prefs.getLong("session_start_timestamp", any()) } returns 1000000L
        every { prefs.getInt("current_exercise_index", 0) } returns 1
        every { prefs.getString("current_exercise_id", "") } returns "ex_2"
        every { prefs.getString("current_exercise_name", "") } returns "Press Banca"
        every { prefs.getInt("current_set_index", 1) } returns 2
        every { prefs.getInt("total_sets", 3) } returns 4
        every { prefs.getString("target_reps", "10") } returns "12"
        every { prefs.getString("current_set_weight", "") } returns "60.0"
        every { prefs.getString("current_set_reps", "") } returns "12"
        every { prefs.getString("exercises_json", "[]") } returns "[]"
        every { prefs.getString("time_spent_json", "{}") } returns "{\"ex_2\":120}"
        every { prefs.getString("completed_sets_json", "{}") } returns "{\"ex_2\":1}"
        every { prefs.getString("max_weight_json", "{}") } returns "{\"ex_2\":60.0}"

        val restored = WorkoutSessionStateManager.loadSession(context)

        assertNotNull(restored)
        assertEquals("plan_123", restored?.uiState?.planId)
        assertEquals("Plan Fuerza (Día 1)", restored?.uiState?.planTitle)
        assertEquals("Press Banca", restored?.uiState?.currentExerciseName)
        assertEquals(2, restored?.uiState?.currentSetIndex)
        assertEquals(4, restored?.uiState?.totalSetsForCurrentExercise)
        assertEquals(120L, restored?.exerciseTimeSpent?.get("ex_2"))
    }

    @Test
    fun testSaveSession_validState_savesToPrefs() {
        val uiState =
            ActiveWorkoutUiState(
                planId = "plan_456",
                planTitle = "Rutina Hipertrofia",
                currentExerciseId = "ex_1",
                currentExerciseName = "Sentadilla",
            )

        WorkoutSessionStateManager.saveSession(
            context = context,
            uiState = uiState,
            sessionStartTimestamp = 2000000L,
            exerciseTimeSpent = mapOf("ex_1" to 300L),
            exerciseCompletedSets = mapOf("ex_1" to 3),
            exerciseMaxWeight = mapOf("ex_1" to 80.0),
        )

        val booleanSlot = slot<Boolean>()
        verify { editor.putBoolean("has_active_session", capture(booleanSlot)) }
        assertTrue(booleanSlot.captured)

        verify { editor.putString("plan_id", "plan_456") }
        verify { editor.putString("plan_title", "Rutina Hipertrofia") }
        verify { editor.putLong("session_start_timestamp", 2000000L) }
    }

    @Test
    fun testClearSession_resetsActiveFlagAndRemovesKeys() {
        WorkoutSessionStateManager.clearSession(context)

        val booleanSlot = slot<Boolean>()
        verify { editor.putBoolean("has_active_session", capture(booleanSlot)) }
        assertEquals(false, booleanSlot.captured)
        verify { editor.remove("plan_id") }
    }
}
