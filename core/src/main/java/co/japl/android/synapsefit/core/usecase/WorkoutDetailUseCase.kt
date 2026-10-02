package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.WorkoutDetailGroup
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import kotlinx.coroutines.flow.Flow

class WorkoutDetailUseCase(
    private val getWorkoutDetailUseCase: GetWorkoutDetailUseCase,
) : IWorkoutDetailUseCase {
    override operator fun invoke(
        date: String,
        day: Int,
    ): Flow<WorkoutDetailGroup?> {
        return getWorkoutDetailUseCase(date, day)
    }

    companion object {
        fun create(workoutLogRepositoryPort: WorkoutLogRepositoryPort): WorkoutDetailUseCase {
            return WorkoutDetailUseCase(GetWorkoutDetailUseCase(workoutLogRepositoryPort))
        }
    }
}
