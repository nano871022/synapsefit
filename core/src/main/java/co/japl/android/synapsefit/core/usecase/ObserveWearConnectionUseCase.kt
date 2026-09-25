package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import kotlinx.coroutines.flow.StateFlow

class ObserveWearConnectionUseCase(
    private val wearSyncPort: WearSyncPort,
) {
    val isPhoneConnected: StateFlow<Boolean>
        get() = wearSyncPort.isPhoneConnected

    suspend fun checkConnection(): Boolean {
        return wearSyncPort.checkConnectionStatus()
    }
}
