package co.japl.android.synapsefit.services.wear

import android.content.Context
import android.util.Log
import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import kotlin.coroutines.resume

class WearableSyncAdapter(private val context: Context? = null) : WearSyncPort {
    private val _isPhoneConnected = MutableStateFlow(false)
    override val isPhoneConnected: StateFlow<Boolean> = _isPhoneConnected.asStateFlow()

    private val _pendingSyncDataCount = MutableStateFlow(0)
    override val pendingSyncDataCount: StateFlow<Int> = _pendingSyncDataCount.asStateFlow()

    private val pendingLogsQueue = mutableListOf<Triple<String, Int, Int>>()

    override fun onConnectionStateChanged(isConnected: Boolean) {
        _isPhoneConnected.value = isConnected
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun checkConnectionStatus(): Boolean {
        val ctx = context ?: return _isPhoneConnected.value
        return suspendCancellableCoroutine { continuation ->
            try {
                Wearable.getNodeClient(ctx).connectedNodes
                    .addOnSuccessListener { nodes ->
                        val hasConnected = nodes.isNotEmpty()
                        _isPhoneConnected.value = hasConnected
                        if (continuation.isActive) continuation.resume(hasConnected)
                    }
                    .addOnFailureListener {
                        _isPhoneConnected.value = false
                        if (continuation.isActive) continuation.resume(false)
                    }
            } catch (_: Exception) {
                _isPhoneConnected.value = false
                if (continuation.isActive) continuation.resume(false)
            }
        }
    }

    override fun queueDataForDeferredSync(
        exerciseId: String,
        reps: Int,
        heartRateBpm: Int,
    ) {
        pendingLogsQueue.add(Triple(exerciseId, reps, heartRateBpm))
        _pendingSyncDataCount.value = pendingLogsQueue.size
    }

    @Suppress("TooGenericExceptionCaught")
    override fun flushSyncQueue() {
        if (pendingLogsQueue.isEmpty()) return

        if (_isPhoneConnected.value) {
            val ctx = context
            if (ctx != null) {
                val jsonArray = JSONArray()
                for (item in pendingLogsQueue) {
                    val obj = JSONObject()
                    obj.put("exerciseId", item.first)
                    obj.put("repsCompleted", item.second)
                    obj.put("heartRateBpm", item.third)
                    obj.put("timestamp", System.currentTimeMillis())
                    jsonArray.put(obj)
                }

                val payloadBytes = jsonArray.toString().toByteArray(StandardCharsets.UTF_8)

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val nodeClient = Wearable.getNodeClient(ctx)
                        val messageClient = Wearable.getMessageClient(ctx)
                        nodeClient.connectedNodes.addOnSuccessListener { nodes ->
                            for (node in nodes) {
                                messageClient.sendMessage(node.id, "/workout_logs", payloadBytes)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("WearableSyncAdapter", "Error flushing sync queue", e)
                    }
                }
            }

            pendingLogsQueue.clear()
            _pendingSyncDataCount.value = 0
        }
    }
}
