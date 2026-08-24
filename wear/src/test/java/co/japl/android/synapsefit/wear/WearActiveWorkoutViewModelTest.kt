package co.japl.android.synapsefit.wear

import co.japl.android.synapsefit.wear.service.WearHeartRateSensorManager
import co.japl.android.synapsefit.wear.service.WearableSyncService
import co.japl.android.synapsefit.wear.ui.WearActiveWorkoutViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WearActiveWorkoutViewModelTest {

    private lateinit var viewModel: WearActiveWorkoutViewModel

    @Before
    fun setUp() {
        viewModel = WearActiveWorkoutViewModel()
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
    fun testUpdateHeartRate() {
        viewModel.updateHeartRate(145)
        assertEquals(145, viewModel.uiState.value.currentHeartRateBpm)
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
    fun testSetSyncStatus() {
        viewModel.setSyncStatus(false)
        assertFalse(viewModel.uiState.value.isSyncedWithPhone)

        viewModel.setSyncStatus(true)
        assertTrue(viewModel.uiState.value.isSyncedWithPhone)
    }

    @Test
    fun testWearHeartRateSensorManager() {
        val sensorManager = WearHeartRateSensorManager()
        assertFalse(sensorManager.isMonitoring.value)

        sensorManager.startHeartRateMonitoring()
        assertTrue(sensorManager.isMonitoring.value)

        sensorManager.onHeartRateSensorChanged(120)
        assertEquals(120, sensorManager.heartRateBpm.value)

        sensorManager.stopHeartRateMonitoring()
        assertFalse(sensorManager.isMonitoring.value)
    }

    @Test
    fun testWearableSyncService() {
        val syncService = WearableSyncService()
        assertTrue(syncService.isPhoneConnected.value)

        syncService.queueDataForDeferredSync("ex-1", 10, 130)
        assertEquals(1, syncService.pendingSyncDataCount.value)

        syncService.onConnectionStateChanged(false)
        assertFalse(syncService.isPhoneConnected.value)

        syncService.flushSyncQueue()
        assertEquals(1, syncService.pendingSyncDataCount.value)

        syncService.onConnectionStateChanged(true)
        syncService.flushSyncQueue()
        assertEquals(0, syncService.pendingSyncDataCount.value)
    }
}
