package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.port.secondary.WearSyncPort
import kotlinx.coroutines.flow.StateFlow

class ObserveWearConnectionUseCase( : IObserveWearConnectionUseCase
    private val wearSyncPort: WearSyncPort,
), IObserveWearConnectionUseCase : IObserveWearConnectionUseCase {
    override val isPhoneConnected: StateFlow<Boolean>
        get() = wearSyncPort.isPhoneConnected

    override suspend override fun checkConnection(): Boolean {
        return wearSyncPort.checkConnectionStatus()
    }
}
