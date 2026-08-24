package co.japl.android.synapsefit.wear.viewmodel

import co.japl.android.synapsefit.services.wear.WearHeartRateSensorAdapter
import co.japl.android.synapsefit.services.wear.WearableSyncAdapter
import co.japl.android.synapsefit.wear.ui.viewmodel.WearActiveWorkoutViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

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
}
