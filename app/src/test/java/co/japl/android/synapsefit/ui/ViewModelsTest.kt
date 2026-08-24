package co.japl.android.synapsefit.ui

import co.japl.android.synapsefit.app.ui.dashboard.DashboardViewModel
import co.japl.android.synapsefit.app.ui.history.WorkoutHistoryViewModel
import co.japl.android.synapsefit.app.ui.measurements.BodyMeasurementsViewModel
import co.japl.android.synapsefit.app.ui.measurements.MeasurementProgressViewModel
import co.japl.android.synapsefit.app.ui.settings.AboutDeveloperViewModel
import co.japl.android.synapsefit.app.ui.settings.BackupSyncViewModel
import co.japl.android.synapsefit.app.ui.settings.LlmSettingsViewModel
import co.japl.android.synapsefit.app.ui.workout.AICoachGeneratorViewModel
import co.japl.android.synapsefit.app.ui.workout.ActiveWorkoutSessionViewModel
import co.japl.android.synapsefit.app.ui.workout.WorkoutPlanDetailViewModel
import co.japl.android.synapsefit.app.ui.workout.WorkoutPlansViewModel
import co.japl.android.synapsefit.core.domain.model.AnatomicalZone
import co.japl.android.synapsefit.core.domain.model.TrainingEnvironment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun dashboardViewModel_initialState_hasDefaultValues() =
        runTest {
            val viewModel = DashboardViewModel()
            val state = viewModel.uiState.value
            assertEquals("Atleta SynapseFit", state.userName)
            assertEquals("Sin rutina activa", state.todayWorkoutTitle)
        }

    @Test
    fun bodyMeasurementsViewModel_validation_failsWithoutWeight() =
        runTest {
            val viewModel = BodyMeasurementsViewModel()
            viewModel.onWeightChange("")
            viewModel.saveMeasurement()
            val state = viewModel.uiState.value
            assertNotNull(state.errorMessage)
        }

    @Test
    fun bodyMeasurementsViewModel_validation_succeedsWithWeight() =
        runTest {
            val viewModel = BodyMeasurementsViewModel()
            viewModel.onWeightChange("75.5")
            viewModel.saveMeasurement()
            val state = viewModel.uiState.value
            assertTrue(state.isSavedSuccess)
        }

    @Test
    fun measurementProgressViewModel_metricSelected_updatesState() =
        runTest {
            val viewModel = MeasurementProgressViewModel()
            viewModel.onMetricSelected(AnatomicalZone.CHEST)
            assertEquals(AnatomicalZone.CHEST, viewModel.uiState.value.selectedMetric)
        }

    @Test
    fun workoutPlansViewModel_initialState_loaded() =
        runTest {
            val viewModel = WorkoutPlansViewModel()
            val state = viewModel.uiState.value
            assertEquals(false, state.isLoading)
        }

    @Test
    fun aiCoachGeneratorViewModel_environmentSelection_updatesState() =
        runTest {
            val viewModel = AICoachGeneratorViewModel()
            viewModel.onEnvironmentSelected(TrainingEnvironment.CHAIN_GYM)
            assertEquals(TrainingEnvironment.CHAIN_GYM, viewModel.uiState.value.selectedEnvironment)
        }

    @Test
    fun workoutPlanDetailViewModel_loadDetail_updatesState() =
        runTest {
            val viewModel = WorkoutPlanDetailViewModel()
            viewModel.loadPlanDetail("test-plan-id")
            assertEquals("test-plan-id", viewModel.uiState.value.planId)
        }

    @Test
    fun activeWorkoutSessionViewModel_startSession_setsPlanId() =
        runTest {
            val viewModel = ActiveWorkoutSessionViewModel()
            viewModel.startSession("plan-123")
            assertEquals("plan-123", viewModel.uiState.value.planId)
        }

    @Test
    fun workoutHistoryViewModel_initialState_loaded() =
        runTest {
            val viewModel = WorkoutHistoryViewModel()
            assertEquals(false, viewModel.uiState.value.isLoading)
        }

    @Test
    fun backupSyncViewModel_initialState_loaded() =
        runTest {
            val viewModel = BackupSyncViewModel()
            assertEquals(false, viewModel.uiState.value.isSyncing)
        }

    @Test
    fun llmSettingsViewModel_initialState_loaded() =
        runTest {
            val viewModel = LlmSettingsViewModel()
            assertEquals(false, viewModel.uiState.value.isLoading)
        }

    @Test
    fun aboutDeveloperViewModel_defaults_correct() =
        runTest {
            val viewModel = AboutDeveloperViewModel()
            assertEquals("1.0.0", viewModel.uiState.value.versionName)
            assertEquals("co.japl.android.synapsefit", viewModel.uiState.value.applicationId)
        }
}
