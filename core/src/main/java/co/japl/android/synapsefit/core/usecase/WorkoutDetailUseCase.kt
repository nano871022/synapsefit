package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.WorkoutDetailGroup
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import kotlinx.coroutines.flow.Flow

class WorkoutDetailUseCase(
    private val workoutLogRepository: WorkoutLogRepositoryPort,
) : IWorkoutDetailUseCase {
    private val getWorkoutDetailUseCase = GetWorkoutDetailUseCase(workoutLogRepository)

    override fun invoke(date: String, day: Int): Flow<WorkoutDetailGroup?> = getWorkoutDetailUseCase(date, day)
}
