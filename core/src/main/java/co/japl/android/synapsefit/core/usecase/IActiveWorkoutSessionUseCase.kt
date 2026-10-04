package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface IActiveWorkoutSessionUseCase {
    suspend fun saveWorkoutLog(log: WorkoutLog)

    fun getActivePlan(): Flow<WorkoutPlan?>
}
