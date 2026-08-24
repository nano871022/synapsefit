package co.japl.android.synapsefit.core.port.secondary

import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import kotlinx.coroutines.flow.Flow

interface WorkoutLogRepositoryPort {
    fun getLogsForExercise(exerciseId: String): Flow<List<WorkoutLog>>
    fun getLogsForDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<WorkoutLog>>
    suspend fun saveLog(log: WorkoutLog)
    suspend fun deleteLog(id: String)
}
