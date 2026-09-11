@file:Suppress("MaxLineLength", "CyclomaticComplexMethod")

package co.japl.android.synapsefit.services.wear

import android.content.Context
import co.japl.android.synapsefit.core.domain.model.LiveSyncEvent
import co.japl.android.synapsefit.core.port.secondary.WearStateMirrorPort
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

class WearableStateMirrorAdapter(
    context: Context? = null,
    private val messageClient: MessageClient? = context?.let { Wearable.getMessageClient(it) },
    private val coroutineScope: CoroutineScope? = null,
) : WearStateMirrorPort {
    private val liveSyncEventsFlow = MutableSharedFlow<LiveSyncEvent>(replay = 1, extraBufferCapacity = 64)
    override val liveSyncEvents: SharedFlow<LiveSyncEvent> = liveSyncEventsFlow.asSharedFlow()

    private val isConnectedState = MutableStateFlow(true)
    override val isConnected: StateFlow<Boolean> = isConnectedState.asStateFlow()

    override suspend fun sendEvent(event: LiveSyncEvent) {
        val payloadStr = serializeEvent(event)
        val payloadBytes = payloadStr.toByteArray(StandardCharsets.UTF_8)

        messageClient?.let { client ->
            contextLetNodeSend(client, payloadBytes)
        }
        liveSyncEventsFlow.emit(event)
    }

    private fun contextLetNodeSend(
        client: MessageClient,
        payloadBytes: ByteArray,
    ) {
        val scope = coroutineScope ?: CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                Wearable.getNodeClient(client.applicationContext).connectedNodes
                    .addOnSuccessListener { nodes ->
                        for (node in nodes) {
                            client.sendMessage(node.id, PATH_LIVE_SYNC_EVENT, payloadBytes)
                        }
                    }
            } catch (_: Exception) {
                // Connection or transmission fallback handled gracefully
            }
        }
    }

    override fun onEventReceived(event: LiveSyncEvent) {
        liveSyncEventsFlow.tryEmit(event)
    }

    override fun onConnectionStateChanged(isConnected: Boolean) {
        isConnectedState.value = isConnected
    }

    companion object {
        const val PATH_LIVE_SYNC_EVENT = "/live_sync_event"

        fun serializeEvent(event: LiveSyncEvent): String {
            return when (event) {
                is LiveSyncEvent.StartSession -> {
                    val pId = event.planId
                    val day = event.day
                    val ts = event.sessionStartTimestamp
                    "{\"type\":\"StartSession\",\"planId\":\"$pId\",\"day\":$day,\"sessionStartTimestamp\":$ts}"
                }
                is LiveSyncEvent.SelectExercise -> {
                    val exId = event.exerciseId
                    val exName = event.exerciseName
                    "{\"type\":\"SelectExercise\",\"exerciseId\":\"$exId\",\"exerciseName\":\"$exName\"}"
                }
                is LiveSyncEvent.CompleteSet -> {
                    val exId = event.exerciseId
                    val setIdx = event.setIndex
                    val reps = event.reps
                    val weight = event.weightKg
                    val targetTs = event.targetTimestamp ?: "null"
                    val duration = event.cooldownDurationSeconds ?: "null"
                    "{\"type\":\"CompleteSet\",\"exerciseId\":\"$exId\",\"setIndex\":$setIdx,\"reps\":$reps," +
                        "\"weightKg\":$weight,\"targetTimestamp\":$targetTs,\"cooldownDurationSeconds\":$duration}"
                }
                is LiveSyncEvent.SkipExercise ->
                    "{\"type\":\"SkipExercise\",\"exerciseId\":\"${event.exerciseId}\"}"
                is LiveSyncEvent.FinishSession -> {
                    val planId = event.planId
                    val duration = event.totalDurationSeconds
                    "{\"type\":\"FinishSession\",\"planId\":\"$planId\",\"totalDurationSeconds\":$duration}"
                }
            }
        }

        fun deserializeEvent(json: String): LiveSyncEvent? {
            val type = extractString(json, "type") ?: return null
            return when (type) {
                "StartSession" -> {
                    val planId = extractString(json, "planId") ?: ""
                    val day = extractInt(json, "day") ?: 1
                    val sessionStartTimestamp = extractLong(json, "sessionStartTimestamp") ?: 0L
                    LiveSyncEvent.StartSession(planId, day, sessionStartTimestamp)
                }
                "SelectExercise" -> {
                    val exerciseId = extractString(json, "exerciseId") ?: ""
                    val exerciseName = extractString(json, "exerciseName") ?: ""
                    LiveSyncEvent.SelectExercise(exerciseId, exerciseName)
                }
                "CompleteSet" -> {
                    val exerciseId = extractString(json, "exerciseId") ?: ""
                    val setIndex = extractInt(json, "setIndex") ?: 1
                    val reps = extractInt(json, "reps") ?: 0
                    val weightKg = extractDouble(json, "weightKg") ?: 0.0
                    val targetTimestamp = extractLong(json, "targetTimestamp")
                    val cooldownDurationSeconds = extractInt(json, "cooldownDurationSeconds")
                    LiveSyncEvent.CompleteSet(
                        exerciseId = exerciseId,
                        setIndex = setIndex,
                        reps = reps,
                        weightKg = weightKg,
                        targetTimestamp = targetTimestamp,
                        cooldownDurationSeconds = cooldownDurationSeconds,
                    )
                }
                "SkipExercise" -> {
                    val exerciseId = extractString(json, "exerciseId") ?: ""
                    LiveSyncEvent.SkipExercise(exerciseId)
                }
                "FinishSession" -> {
                    val planId = extractString(json, "planId") ?: ""
                    val totalDurationSeconds = extractLong(json, "totalDurationSeconds") ?: 0L
                    LiveSyncEvent.FinishSession(planId, totalDurationSeconds)
                }
                else -> null
            }
        }

        private fun extractString(
            json: String,
            key: String,
        ): String? {
            val regex = """" $key "\s*:\s* "([^"]*)" """.toRegex(RegexOption.COMMENTS)
            return regex.find(json)?.groupValues?.get(1)
        }

        private fun extractInt(
            json: String,
            key: String,
        ): Int? {
            val regex = """" $key "\s*:\s* (-?\d+) """.toRegex(RegexOption.COMMENTS)
            return regex.find(json)?.groupValues?.get(1)?.toIntOrNull()
        }

        private fun extractLong(
            json: String,
            key: String,
        ): Long? {
            val regex = """" $key "\s*:\s* (-?\d+) """.toRegex(RegexOption.COMMENTS)
            return regex.find(json)?.groupValues?.get(1)?.toLongOrNull()
        }

        private fun extractDouble(
            json: String,
            key: String,
        ): Double? {
            val regex = """" $key "\s*:\s* (-?\d+(?:\.\d+)?) """.toRegex(RegexOption.COMMENTS)
            return regex.find(json)?.groupValues?.get(1)?.toDoubleOrNull()
        }
    }
}
