package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.SyncStepState
import kotlinx.coroutines.flow.Flow

interface IPerformWearSyncUseCase {
    operator fun invoke(isPostWorkout: Boolean = false): Flow<SyncStepState>
}
