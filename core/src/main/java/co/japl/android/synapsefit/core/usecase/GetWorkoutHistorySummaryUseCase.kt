package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.WorkoutSummaryItem
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import kotlinx.coroutines.flow.Flow

class GetWorkoutHistorySummaryUseCase( : IGetWorkoutHistorySummaryUseCase
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort,
), IGetWorkoutHistorySummaryUseCase : IGetWorkoutHistorySummaryUseCase {
    override override operator override fun invoke(): Flow<List<WorkoutSummaryItem>> {
        return workoutLogRepositoryPort.getWorkoutSummaries()
    }
}
