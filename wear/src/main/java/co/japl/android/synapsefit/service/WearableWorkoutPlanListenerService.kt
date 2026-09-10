package co.japl.android.synapsefit.service

import android.net.Uri
import androidx.room.Room
import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.services.database.SynapseFitDatabase
import co.japl.android.synapsefit.services.repository.WorkoutLogRepositoryAdapter
import co.japl.android.synapsefit.services.repository.WorkoutPlanRepositoryAdapter
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.util.UUID

class WearableWorkoutPlanListenerService(
    private var customPlanRepository: WorkoutPlanRepositoryPort? = null,
    private var customLogRepository: WorkoutLogRepositoryPort? = null,
) : WearableListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        if (customPlanRepository == null || customLogRepository == null) {
            val db =
                Room.databaseBuilder(
                    applicationContext,
                    SynapseFitDatabase::class.java,
                    "synapsefit_database.db",
                ).addMigrations(SynapseFitDatabase.MIGRATION_6_7)
                    .build()
            if (customPlanRepository == null) {
                customPlanRepository = WorkoutPlanRepositoryAdapter(db.workoutPlanDao())
            }
            if (customLogRepository == null) {
                customLogRepository = WorkoutLogRepositoryAdapter(db.workoutLogDao())
            }
        }
        requestActivePlanStartupIngestion()
    }

    fun requestActivePlanStartupIngestion() {
        scope.launch {
            try {
                val dataClient = Wearable.getDataClient(applicationContext)
                dataClient.getDataItems(Uri.parse("wear://*/active_workout_plan"))
                    .addOnSuccessListener { dataItemBuffer ->
                        try {
                            for (item in dataItemBuffer) {
                                val isPlanPath =
                                    item.uri.path == "/active_workout_plan" ||
                                        item.uri.path?.contains("workout_plan") == true
                                if (isPlanPath) {
                                    val dataMap = DataMapItem.fromDataItem(item).dataMap
                                    val payloadJson = dataMap.getString("payload_json") ?: ""
                                    if (payloadJson.isNotBlank()) {
                                        processAndPersistPayload(payloadJson)
                                    }
                                }
                            }
                        } finally {
                            dataItemBuffer.release()
                        }
                    }
            } catch (e: IllegalStateException) {
                android.util.Log.d("WearListener", "Startup ingestion unavailable", e)
            } catch (e: SecurityException) {
                android.util.Log.d("WearListener", "Startup ingestion permission denied", e)
            }
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
                } else if (uri.path == "/workout_logs" || uri.path?.contains("workout_logs") == true) {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val logsJson = dataMap.getString("logs_json") ?: ""
                    processAndPersistLogs(logsJson)
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        if (messageEvent.path == "/active_workout_plan" || messageEvent.path.contains("workout_plan")) {
            val payloadJson = String(messageEvent.data, Charsets.UTF_8)
            processAndPersistPayload(payloadJson)
        } else if (messageEvent.path == "/workout_logs" || messageEvent.path.contains("workout_logs")) {
            val logsJson = String(messageEvent.data, Charsets.UTF_8)
            processAndPersistLogs(logsJson)
        }
    }

    fun processAndPersistPayload(jsonPayload: String) {
        val parsed = WorkoutPlanPayloadParser.parseJsonPayload(jsonPayload) ?: return
        val repo = customPlanRepository ?: return
        scope.launch {
            repo.savePlan(parsed.plan, parsed.exercises)
            repo.setActivePlan(parsed.plan.id)
        }
    }

    fun processAndPersistLogs(logsJson: String) {
        if (logsJson.isBlank()) return
        val logRepo = customLogRepository ?: return
        scope.launch {
            try {
                val jsonArray = JSONArray(logsJson)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val hr =
                        if (obj.has("heartRateBpm") && !obj.isNull("heartRateBpm")) {
                            obj.getInt("heartRateBpm")
                        } else {
                            null
                        }
                    val log =
                        WorkoutLog(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            exerciseId = obj.getString("exerciseId"),
                            repsCompleted = obj.getInt("repsCompleted"),
                            weightLiftedKg = obj.optDouble("weightLiftedKg", 0.0),
                            heartRateBpm = hr,
                            sourceDevice = SourceDevice.WEAR_OS,
                            durationSeconds = obj.optLong("durationSeconds", 0L),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                            updatedAt = obj.optLong("updatedAt", System.currentTimeMillis()),
                        )
                    logRepo.saveLog(log)
                }
            } catch (e: JSONException) {
                android.util.Log.e("WearListener", "Error parsing workout logs payload", e)
            }
        }
    }
}
