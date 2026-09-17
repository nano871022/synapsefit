package co.japl.android.synapsefit.service

import co.japl.android.synapsefit.DependencyContainer
import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.services.wear.WearableStateMirrorAdapter
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.UUID

class MobileWearableListenerService : WearableListenerService() {
    private val scope = CoroutineScope(Dispatchers.IO)

    @Suppress("TooGenericExceptionCaught")
    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        val container = DependencyContainer(applicationContext)

        when {
            messageEvent.path == "/request_active_plan" -> handleActivePlanRequest(messageEvent, container)
            isLiveSyncPath(messageEvent.path) -> handleLiveSyncEvent(messageEvent.data, container)
            messageEvent.path == "/workout_logs" -> handleWorkoutLogs(messageEvent.data, container)
        }
    }

    private fun isLiveSyncPath(path: String): Boolean =
        path == WearableStateMirrorAdapter.PATH_LIVE_SYNC_EVENT || path == "/live_sync_event"

    private fun handleLiveSyncEvent(
        data: ByteArray,
        container: DependencyContainer,
    ) {
        val json = String(data, StandardCharsets.UTF_8)
        val event = WearableStateMirrorAdapter.deserializeEvent(json)
        if (event != null) {
            container.wearStateMirrorPort.onEventReceived(event)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun handleActivePlanRequest(
        messageEvent: MessageEvent,
        container: DependencyContainer,
    ) {
        scope.launch {
            try {
                val activePlan = container.workoutPlanRepository.getActivePlan().firstOrNull() ?: return@launch
                val pair = container.workoutPlanRepository.getPlanWithExercises(activePlan.id).firstOrNull()
                val exercises = pair?.second ?: emptyList()

                val json = buildActivePlanJson(activePlan.id, activePlan.title, activePlan.goalDescription, exercises)
                val payloadBytes = json.toString().toByteArray(StandardCharsets.UTF_8)
                Wearable.getMessageClient(applicationContext)
                    .sendMessage(messageEvent.sourceNodeId, "/active_workout_plan", payloadBytes)
            } catch (e: Exception) {
                android.util.Log.e("MobileWearableListener", "Error responding to active plan request", e)
            }
        }
    }

    private fun buildActivePlanJson(
        id: String,
        title: String,
        goalDescription: String,
        exercises: List<co.japl.android.synapsefit.core.domain.model.Exercise>,
    ): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("planId", id)
        json.put("title", title)
        json.put("goalDescription", goalDescription)
        json.put("day", 1)

        val exercisesArray = JSONArray()
        for (ex in exercises) {
            val exObj = JSONObject()
            exObj.put("id", ex.id)
            exObj.put("name", ex.name)
            exObj.put("muscleGroup", ex.muscleGroup)
            exObj.put("targetSets", ex.targetSets)
            exObj.put("targetReps", ex.targetReps)
            exObj.put("restSeconds", ex.restSeconds)
            exObj.put("day", ex.day)
            exercisesArray.put(exObj)
        }
        json.put("exercises", exercisesArray)
        return json
    }

    @Suppress("TooGenericExceptionCaught")
    private fun handleWorkoutLogs(
        data: ByteArray,
        container: DependencyContainer,
    ) {
        val logsJson = String(data, StandardCharsets.UTF_8)
        if (logsJson.isBlank()) return
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
                    container.workoutLogRepository.saveLog(log)
                }
            } catch (e: Exception) {
                android.util.Log.e("MobileWearableListener", "Error persisting incoming workout logs", e)
            }
        }
    }
}
