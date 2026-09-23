package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.WorkoutSummaryItem
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import kotlinx.coroutines.flow.Flow

class GetWorkoutHistorySummaryUseCase(
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort,
) {
    operator fun invoke(): Flow<List<WorkoutSummaryItem>> {
        return workoutLogRepositoryPort.getWorkoutSummaries()
    }
}
