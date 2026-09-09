package co.japl.android.synapsefit.viewmodel

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.ExerciseSession
import co.japl.android.synapsefit.core.domain.model.TrainingStepState
import co.japl.android.synapsefit.services.wear.WearHeartRateSensorAdapter
import co.japl.android.synapsefit.services.wear.WearableSyncAdapter
import co.japl.android.synapsefit.service.WorkoutPlanPayloadParser
import co.japl.android.synapsefit.ui.viewmodel.WearActiveWorkoutViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@Suppress("LongMethod")
class WearActiveWorkoutViewModelTest {
    private lateinit var sensorAdapter: WearHeartRateSensorAdapter
    private lateinit var syncAdapter: WearableSyncAdapter
    private lateinit var viewModel: WearActiveWorkoutViewModel

    @Before
    fun setUp() {
        sensorAdapter = WearHeartRateSensorAdapter()
        syncAdapter = WearableSyncAdapter()
        viewModel =
            WearActiveWorkoutViewModel(
                sensorPort = sensorAdapter,
                syncPort = syncAdapter,
            )
    }

    @Test
    fun testInitialUiState() {
        val state = viewModel.uiState.value
        assertEquals("", state.exerciseName)
        assertEquals(0, state.currentHeartRateBpm)
        assertEquals(0, state.currentReps)
        assertTrue(state.isSyncedWithPhone)
    }

    @Test
    fun testUpdateExerciseName() {
        viewModel.updateExerciseName("Sentadillas")
        assertEquals("Sentadillas", viewModel.uiState.value.exerciseName)
    }

    @Test
    fun testUpdateHeartRatePassesThroughCorePort() {
        sensorAdapter.startHeartRateMonitoring()
        viewModel.updateHeartRate(145)

        assertEquals(145, viewModel.uiState.value.currentHeartRateBpm)
        assertEquals(145, sensorAdapter.heartRateBpm.value)
    }

    @Test
    fun testIncrementAndDecrementReps() {
        viewModel.incrementReps()
        viewModel.incrementReps()
        assertEquals(2, viewModel.uiState.value.currentReps)

        viewModel.decrementReps()
        assertEquals(1, viewModel.uiState.value.currentReps)

        viewModel.decrementReps()
        viewModel.decrementReps()
        assertEquals(0, viewModel.uiState.value.currentReps)
    }

    @Test
    fun testSetSyncStatusPassesThroughCorePort() {
        viewModel.setSyncStatus(false)

        assertFalse(viewModel.uiState.value.isSyncedWithPhone)
        assertFalse(syncAdapter.isPhoneConnected.value)

        viewModel.setSyncStatus(true)
        assertTrue(viewModel.uiState.value.isSyncedWithPhone)
        assertTrue(syncAdapter.isPhoneConnected.value)
    }

    @Test
    fun testLoadExerciseSessionsSetsActiveState() {
        val sessions =
            listOf(
                ExerciseSession(
                    exerciseId = "ex1",
                    planId = "p1",
                    name = "Press de Banca",
                    muscleGroup = "Pecho",
                    targetSets = 2,
                    targetReps = "10",
                    restSeconds = 60,
                ),
            )

        viewModel.loadExerciseSessions(sessions)

        val state = viewModel.trainingStepState.value
        assertTrue(state is TrainingStepState.Active)
        val active = state as TrainingStepState.Active
        assertEquals("Press de Banca", active.exerciseSession.name)
        assertEquals(1, active.currentSet)
    }

    @Test
    fun testCompleteSetTransitionsToCooldownThenReadyForNext() {
        val sessions =
            listOf(
                ExerciseSession(
                    exerciseId = "ex1",
                    planId = "p1",
                    name = "Sentadilla",
                    muscleGroup = "Piernas",
                    targetSets = 2,
                    targetReps = "12",
                    restSeconds = 30,
                ),
            )

        viewModel.loadExerciseSessions(sessions)

        viewModel.completeSet()
        assertTrue(viewModel.trainingStepState.value is TrainingStepState.Cooldown)
        val cooldown = viewModel.trainingStepState.value as TrainingStepState.Cooldown
        assertEquals(1, cooldown.exerciseSession.completedSets)
        assertFalse(cooldown.exerciseSession.isCompleted)

        viewModel.setCooldownTargetTimestamp(System.currentTimeMillis() - 1000L)
        viewModel.recalculateCooldownTimer()

        assertTrue(viewModel.trainingStepState.value is TrainingStepState.ReadyForNext)

        viewModel.startNextExercise()
        val activeState = viewModel.trainingStepState.value
        assertTrue(activeState is TrainingStepState.Active)

        viewModel.completeSet()
        val finalState = viewModel.trainingStepState.value
        assertTrue(finalState is TrainingStepState.ReadyForNext)

        val completedSession = viewModel.uiState.value.exerciseSessions.first()
        assertTrue(completedSession.isCompleted)
        assertEquals(2, completedSession.completedSets)
    }

    @Test
    fun testWorkoutPlanPayloadParser() {
        val jsonPayload =
            """
            {
              "planId": "plan_123",
              "title": "Rutina Fuerza",
              "goalDescription": "Aumento de masa",
              "day": 1,
              "exercises": [
                {
                  "id": "ex_1",
                  "name": "Dominadas",
                  "muscleGroup": "Espalda",
                  "targetSets": 4,
                  "targetReps": "8",
                  "restSeconds": 90,
                  "day": 1
                }
              ]
            }
            """.trimIndent()

        val parsed = WorkoutPlanPayloadParser.parseJsonPayload(jsonPayload)
        assertNotNull(parsed)
        assertEquals("plan_123", parsed?.plan?.id)
        assertEquals("Rutina Fuerza", parsed?.plan?.title)
        assertEquals(1, parsed?.exercises?.size)
        assertEquals("Dominadas", parsed?.exercises?.get(0)?.name)
        assertEquals(4, parsed?.exercises?.get(0)?.targetSets)
    }

    @Test
    fun testStartSessionAndSelectExercise() {
        val exercise =
            Exercise(
                id = "ex_1",
                planId = "plan_1",
                name = "Press de Banca (Compuesto)",
                muscleGroup = "Pecho",
                targetSets = 4,
                targetReps = "12",
                restSeconds = 90,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            )

        viewModel.selectExercise(exercise, 0)
        assertTrue(viewModel.uiState.value.isSessionStarted)
        assertEquals("Press de Banca (Compuesto)", viewModel.uiState.value.exerciseName)

        viewModel.exitToSelectionHub()
        assertFalse(viewModel.uiState.value.isSessionStarted)

        viewModel.startSession()
        assertTrue(viewModel.uiState.value.isSessionStarted)
    }
}
