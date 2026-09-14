package co.japl.android.synapsefit.service

import co.japl.android.synapsefit.DependencyContainer
import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
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

    @Suppress("LongMethod", "TooGenericExceptionCaught")
    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        val container = DependencyContainer(applicationContext)

        if (messageEvent.path == "/request_active_plan") {
            scope.launch {
                try {
                    val activePlan = container.workoutPlanRepository.getActivePlan().firstOrNull()
                    if (activePlan != null) {
                        val pair = container.workoutPlanRepository.getPlanWithExercises(activePlan.id).firstOrNull()
                        val exercises = pair?.second ?: emptyList()

                        val json = JSONObject()
                        json.put("id", activePlan.id)
                        json.put("planId", activePlan.id)
                        json.put("title", activePlan.title)
                        json.put("goalDescription", activePlan.goalDescription)
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

                        val payloadBytes = json.toString().toByteArray(StandardCharsets.UTF_8)
                        Wearable.getMessageClient(applicationContext)
                            .sendMessage(messageEvent.sourceNodeId, "/active_workout_plan", payloadBytes)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("MobileWearableListener", "Error responding to active plan request", e)
                }
            }
        } else if (messageEvent.path == "/workout_logs") {
            val logsJson = String(messageEvent.data, StandardCharsets.UTF_8)
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
}
