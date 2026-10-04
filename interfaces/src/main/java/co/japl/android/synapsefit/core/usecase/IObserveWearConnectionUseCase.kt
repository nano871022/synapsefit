package co.japl.android.synapsefit.core.usecase

import kotlinx.coroutines.flow.StateFlow

interface IObserveWearConnectionUseCase {
    val isPhoneConnected: StateFlow<Boolean>

    suspend fun checkConnection(): Boolean
}
