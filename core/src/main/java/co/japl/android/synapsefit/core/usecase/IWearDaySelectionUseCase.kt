package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.Flow

interface IWearDaySelectionUseCase {
    fun getActivePlan(): Flow<WorkoutPlan?>
}

class WearDaySelectionUseCase(
    private val workoutPlanRepository: WorkoutPlanRepositoryPort,
) : IWearDaySelectionUseCase {
    override fun getActivePlan(): Flow<WorkoutPlan?> = workoutPlanRepository.getActivePlan()
}
