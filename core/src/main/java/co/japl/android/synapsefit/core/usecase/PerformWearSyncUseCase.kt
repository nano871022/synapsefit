package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.LiveSyncEvent
import co.japl.android.synapsefit.core.domain.model.SyncStepState
import co.japl.android.synapsefit.core.port.secondary.ActiveSessionRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WearStateMirrorPort
import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull

class PerformWearSyncUseCase(
    private val wearSyncPort: WearSyncPort,
    private val wearStateMirrorPort: WearStateMirrorPort,
    private val workoutPlanRepository: WorkoutPlanRepositoryPort,
    private val workoutLogRepository: WorkoutLogRepositoryPort,
    private val activeSessionRepository: ActiveSessionRepositoryPort,
) {
    @Suppress("MagicNumber", "LongMethod")
    operator fun invoke(isPostWorkout: Boolean = false): Flow<SyncStepState> =
        flow {
            // Step 1: Downloading plans and exercises
            emit(SyncStepState.DownloadingPlans)
            wearSyncPort.flushSyncQueue()
            delay(300)

            // Step 2: Configuring next plan
            emit(SyncStepState.ConfiguringNextPlan)
            workoutPlanRepository.getActivePlan().firstOrNull()
            delay(300)

            // Step 3: Verifying offline workouts
            emit(SyncStepState.VerifyingOfflineWorkouts)
            val offlineLogs = workoutLogRepository.getAllLogs().firstOrNull() ?: emptyList()
            val pendingCount =
                wearSyncPort.pendingSyncDataCount.value.coerceAtLeast(
                    offlineLogs.count { it.sourceDevice.name == "WEAR_OS" },
                )
            delay(300)

            // Step 4: Uploading logs (Count Indicator)
            if (pendingCount > 0) {
                for (i in 1..pendingCount) {
                    emit(SyncStepState.UploadingLogs(current = i, total = pendingCount))
                    delay(200)
                }
                wearSyncPort.flushSyncQueue()
            } else {
                emit(SyncStepState.UploadingLogs(current = 0, total = 0))
                delay(200)
            }

            // Step 5: Process finished & cleanup
            emit(SyncStepState.Cleanup)
            if (!isPostWorkout) {
                activeSessionRepository.clearActiveSession()
            }
            delay(300)

            // Step 6: Verifying active mobile training
            emit(SyncStepState.VerifyingActiveTraining)
            var activeSessionDetected = false
            var activePlanId: String? = null
            var activeDay: Int? = null
            var activeExerciseId: String? = null

            wearStateMirrorPort.sendEvent(LiveSyncEvent.PingSession("WEAR_OS"))

            val activePayload =
                withTimeoutOrNull(1000L) {
                    var payload: LiveSyncEvent.ActiveSessionStatePayload? = null
                    wearStateMirrorPort.liveSyncEvents.collect { event ->
                        if (event is LiveSyncEvent.ActiveSessionStatePayload && event.isLiveActive) {
                            payload = event
                            return@collect
                        }
                    }
                    payload
                }

            if (activePayload != null && activePayload.isLiveActive) {
                activeSessionDetected = true
                activePlanId = activePayload.planId
                activeDay = activePayload.day
                activeExerciseId = activePayload.currentExerciseId
            }

            delay(200)
            emit(
                SyncStepState.Finished(
                    activeSessionDetected = activeSessionDetected,
                    planId = activePlanId,
                    day = activeDay,
                    exerciseId = activeExerciseId,
                ),
            )
        }
}
