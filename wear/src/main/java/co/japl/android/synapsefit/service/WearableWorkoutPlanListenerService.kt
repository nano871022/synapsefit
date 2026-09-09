package co.japl.android.synapsefit.service

import androidx.room.Room
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.services.database.SynapseFitDatabase
import co.japl.android.synapsefit.services.repository.WorkoutPlanRepositoryAdapter
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WearableWorkoutPlanListenerService(
    private var customRepository: WorkoutPlanRepositoryPort? = null,
) : WearableListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        if (customRepository == null) {
            val db =
                Room.databaseBuilder(
                    applicationContext,
                    SynapseFitDatabase::class.java,
                    "synapsefit_database.db",
                ).addMigrations(SynapseFitDatabase.MIGRATION_6_7)
                    .build()
            customRepository = WorkoutPlanRepositoryAdapter(db.workoutPlanDao())
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val uri = event.dataItem.uri
                if (uri.path == "/active_workout_plan" || uri.path?.contains("workout_plan") == true) {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val payloadJson = dataMap.getString("payload_json") ?: ""
                    processAndPersistPayload(payloadJson)
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        if (messageEvent.path == "/active_workout_plan" || messageEvent.path.contains("workout_plan")) {
            val payloadJson = String(messageEvent.data, Charsets.UTF_8)
            processAndPersistPayload(payloadJson)
        }
    }

    fun processAndPersistPayload(jsonPayload: String) {
        val parsed = WorkoutPlanPayloadParser.parseJsonPayload(jsonPayload) ?: return
        val repo = customRepository ?: return
        scope.launch {
            repo.savePlan(parsed.plan, parsed.exercises)
            repo.setActivePlan(parsed.plan.id)
        }
    }
}
