package co.japl.android.synapsefit

import android.content.Context
import androidx.room.Room
import co.japl.android.synapsefit.core.port.secondary.WearSensorPort
import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.core.usecase.GetGroupedWorkoutHistoryUseCase
import co.japl.android.synapsefit.core.usecase.GetTodayRoutineUseCase
import co.japl.android.synapsefit.services.database.SynapseFitDatabase
import co.japl.android.synapsefit.services.repository.WorkoutLogRepositoryAdapter
import co.japl.android.synapsefit.services.repository.WorkoutPlanRepositoryAdapter
import co.japl.android.synapsefit.services.wear.WearHeartRateSensorAdapter
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
    lateinit var wearSensorPort: WearSensorPort
    lateinit var wearSyncPort: WearSyncPort

    lateinit var getTodayRoutineUseCase: GetTodayRoutineUseCase
    lateinit var getGroupedWorkoutHistoryUseCase: GetGroupedWorkoutHistoryUseCase

    @Synchronized
    fun initialize(context: Context) {
        if (isInitialized) return

        val appContext = context.applicationContext
        database = Room.databaseBuilder(
            appContext,
            SynapseFitDatabase::class.java,
            "synapsefit_database.db"
        ).addMigrations(SynapseFitDatabase.MIGRATION_6_7)
            .build()

        workoutPlanRepository = WorkoutPlanRepositoryAdapter(database.workoutPlanDao())
        workoutLogRepository = WorkoutLogRepositoryAdapter(database.workoutLogDao())
        wearSensorPort = WearHeartRateSensorAdapter()
        wearSyncPort = WearableSyncAdapter(appContext)

        getTodayRoutineUseCase = GetTodayRoutineUseCase(workoutPlanRepository, workoutLogRepository)
        getGroupedWorkoutHistoryUseCase = GetGroupedWorkoutHistoryUseCase(workoutLogRepository)

        isInitialized = true
    }

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
                android.util.Log.e("WearDependencyProvider", "Error requesting active plan", e)
            }
        }
    }
}
