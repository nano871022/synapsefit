package co.japl.android.synapsefit.ui

import co.japl.android.synapsefit.app.controller.dashboard.DashboardViewModel
import co.japl.android.synapsefit.app.controller.history.WorkoutHistoryViewModel
import co.japl.android.synapsefit.app.controller.measurements.BodyMeasurementsViewModel
import co.japl.android.synapsefit.app.controller.measurements.MeasurementProgressViewModel
import co.japl.android.synapsefit.app.controller.profile.UserProfileViewModel
import co.japl.android.synapsefit.app.controller.settings.AboutDeveloperViewModel
import co.japl.android.synapsefit.app.controller.settings.BackupSyncViewModel
import co.japl.android.synapsefit.app.controller.settings.LlmSettingsViewModel
import co.japl.android.synapsefit.app.controller.workout.AICoachGeneratorViewModel
import co.japl.android.synapsefit.app.controller.workout.ActiveWorkoutSessionViewModel
import co.japl.android.synapsefit.app.controller.workout.WorkoutPlanDetailViewModel
import co.japl.android.synapsefit.app.controller.workout.WorkoutPlansViewModel
import co.japl.android.synapsefit.core.domain.model.AnatomicalZone
import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.domain.model.TrainingEnvironment
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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

    @Test
    fun userProfileViewModel_onBirthDateChange_updatesState() =
        runTest {
            val viewModel = UserProfileViewModel()
            viewModel.onBirthDateChange("1995-05-20")
            assertEquals("1995-05-20", viewModel.uiState.value.birthDate)
        }

    @Test
    fun userProfileViewModel_evaluateMedicalConditions_showsDialogAndHandlesRetry() =
        runTest {
            val mockEvalUseCase = mockk<co.japl.android.synapsefit.core.usecase.EvaluateMedicalConditionsUseCase>()
            io.mockk.coEvery {
                mockEvalUseCase(any(), any(), any(), any())
            } returns Result.failure(RuntimeException("Error de conexión con LLM"))

            val viewModel =
                UserProfileViewModel(
                    evaluateMedicalConditionsUseCase = mockEvalUseCase,
                )

            viewModel.onFullNameChange("Juan Perez")
            viewModel.onHeightCmChange("175")
            viewModel.onMedicalConditionsChange("Hernia lumbar")

            viewModel.saveProfile()

            val state = viewModel.uiState.value
            assertTrue(state.showMedicalDialog)
            assertTrue(state.medicalEvaluationFailed)
            assertEquals("Error de conexión con LLM", state.medicalEvaluationError)

            viewModel.dismissMedicalDialog()
            assertEquals(false, viewModel.uiState.value.showMedicalDialog)
        }

    @Test
    fun workoutHistoryViewModel_groupsMultipleExercisesOnSameDay() =
        runTest {
            val mockLogPort = mockk<WorkoutLogRepositoryPort>()
            val baseTime = System.currentTimeMillis()
            val logs =
                (1..5).map { index ->
                    WorkoutLog(
                        id = "log-$index",
                        exerciseId = "ex-$index",
                        repsCompleted = 10,
                        weightLiftedKg = 50.0,
                        timestamp = baseTime + (index * 3600 * 1000L),
                        sourceDevice = SourceDevice.MOBILE,
                        createdAt = baseTime,
                        updatedAt = baseTime,
                    )
                }
            every { mockLogPort.getAllLogs() } returns flowOf(logs)

            val viewModel = WorkoutHistoryViewModel(workoutLogRepositoryPort = mockLogPort)
            val groups = viewModel.uiState.value.sessionGroups

            assertEquals(1, groups.size)
            assertEquals(5, groups.first().totalExercisesCount)
            assertEquals(5, groups.first().exercises.size)
        }

    @Test
    fun workoutHistoryViewModel_loadsAllWorkoutSessionsAcrossMultipleDays() =
        runTest {
            val mockLogPort = mockk<WorkoutLogRepositoryPort>()
            val baseTime = System.currentTimeMillis()
            val dayMillis = 86400000L
            val logs =
                (1..10).map { index ->
                    WorkoutLog(
                        id = "log-$index",
                        exerciseId = "ex-$index",
                        repsCompleted = 10,
                        weightLiftedKg = 50.0,
                        timestamp = baseTime - (index * dayMillis),
                        sourceDevice = SourceDevice.MOBILE,
                        createdAt = baseTime,
                        updatedAt = baseTime,
                    )
                }
            every { mockLogPort.getAllLogs() } returns flowOf(logs)

            val viewModel = WorkoutHistoryViewModel(workoutLogRepositoryPort = mockLogPort)
            val state = viewModel.uiState.value

            assertEquals(10, state.recordedSessions.size)
            assertEquals(10, state.sessionGroups.size)
            assertEquals(10, state.weeklySessionsCount)
        }
}
