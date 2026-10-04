package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.ActiveWorkoutSessionState
import co.japl.android.synapsefit.core.domain.model.WorkoutSessionItem
import kotlinx.coroutines.flow.Flow

interface IDaySessionSelectionUseCase {
    fun getTodayRoutines(): Flow<List<WorkoutSessionItem>>

    fun getActiveSessionState(): Flow<ActiveWorkoutSessionState>
}
