package co.japl.android.synapsefit

import android.content.Context
import android.util.Log
import androidx.room.Room
import co.japl.android.synapsefit.core.port.secondary.ActiveSessionRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WearSensorPort
import co.japl.android.synapsefit.core.port.secondary.WearStateMirrorPort
import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.GetActiveWorkoutSessionUseCase
import co.japl.android.synapsefit.core.usecase.GetGroupedWorkoutHistoryUseCase
import co.japl.android.synapsefit.core.usecase.GetTodayRoutineUseCase
import co.japl.android.synapsefit.core.usecase.ObserveWearConnectionUseCase
import co.japl.android.synapsefit.core.usecase.PerformWearSyncUseCase
import co.japl.android.synapsefit.core.usecase.SyncPendingWorkoutLogsUseCase
import co.japl.android.synapsefit.services.database.SynapseFitDatabase
import co.japl.android.synapsefit.services.repository.InMemoryActiveSessionAdapter
import co.japl.android.synapsefit.services.repository.WorkoutLogRepositoryAdapter
import co.japl.android.synapsefit.services.repository.WorkoutPlanRepositoryAdapter
import co.japl.android.synapsefit.services.wear.WearHeartRateSensorAdapter
import co.japl.android.synapsefit.services.wear.WearableStateMirrorAdapter
import co.japl.android.synapsefit.services.wear.WearableSyncAdapter
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WearDependencyProvider {
    private var isInitialized = false

    lateinit var database: SynapseFitDatabase
    lateinit var workoutPlanRepository: WorkoutPlanRepositoryPort
    lateinit var workoutLogRepository: WorkoutLogRepositoryPort
    lateinit var activeSessionRepository: ActiveSessionRepositoryPort
    lateinit var wearSensorPort: WearSensorPort
    lateinit var wearSyncPort: WearSyncPort
    lateinit var wearStateMirrorPort: WearStateMirrorPort

    lateinit var getTodayRoutineUseCase: GetTodayRoutineUseCase
    lateinit var getActiveWorkoutSessionUseCase: GetActiveWorkoutSessionUseCase
    lateinit var getGroupedWorkoutHistoryUseCase: GetGroupedWorkoutHistoryUseCase
    lateinit var performWearSyncUseCase: PerformWearSyncUseCase
    lateinit var observeWearConnectionUseCase: ObserveWearConnectionUseCase
    lateinit var syncPendingWorkoutLogsUseCase: SyncPendingWorkoutLogsUseCase

    @Synchronized
    fun initialize(context: Context) {
        if (isInitialized) return

        val appContext = context.applicationContext
        database =
            Room.databaseBuilder(
                appContext,
                SynapseFitDatabase::class.java,
                "synapsefit_database.db",
            ).addMigrations(SynapseFitDatabase.MIGRATION_6_7, SynapseFitDatabase.MIGRATION_7_8)
                .build()

        workoutPlanRepository = WorkoutPlanRepositoryAdapter(database.workoutPlanDao())
        workoutLogRepository = WorkoutLogRepositoryAdapter(database.workoutLogDao())
        activeSessionRepository = InMemoryActiveSessionAdapter()
        wearSensorPort = WearHeartRateSensorAdapter(appContext)
        wearSyncPort = WearableSyncAdapter(appContext)
        wearStateMirrorPort = WearableStateMirrorAdapter(appContext)

        getTodayRoutineUseCase = GetTodayRoutineUseCase(workoutPlanRepository, workoutLogRepository)
        getActiveWorkoutSessionUseCase = GetActiveWorkoutSessionUseCase(activeSessionRepository)
        getGroupedWorkoutHistoryUseCase = GetGroupedWorkoutHistoryUseCase(workoutLogRepository)
        observeWearConnectionUseCase = ObserveWearConnectionUseCase(wearSyncPort)
        syncPendingWorkoutLogsUseCase = SyncPendingWorkoutLogsUseCase(wearSyncPort)
        performWearSyncUseCase =
            PerformWearSyncUseCase(
                wearSyncPort = wearSyncPort,
                wearStateMirrorPort = wearStateMirrorPort,
                workoutPlanRepository = workoutPlanRepository,
                workoutLogRepository = workoutLogRepository,
                activeSessionRepository = activeSessionRepository,
            )

        isInitialized = true
        requestActivePlanFromPhone(appContext)
    }

    @Suppress("TooGenericExceptionCaught")
    fun requestActivePlanFromPhone(context: Context) {
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val nodeClient = Wearable.getNodeClient(appContext)
                val messageClient = Wearable.getMessageClient(appContext)
                nodeClient.connectedNodes.addOnSuccessListener { nodes ->
                    for (node in nodes) {
                        messageClient.sendMessage(node.id, "/request_active_plan", ByteArray(0))
                    }
                }
            } catch (e: Exception) {
                Log.e("WearDependencyProvider", "Error requesting active plan", e)
            }
        }
    }
}
