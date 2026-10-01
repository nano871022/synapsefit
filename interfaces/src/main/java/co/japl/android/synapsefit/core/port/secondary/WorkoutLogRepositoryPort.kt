package co.japl.android.synapsefit.core.port.secondary

import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.domain.model.history.WorkoutDetailRecord
import co.japl.android.synapsefit.core.domain.model.history.WorkoutHistoryRecord
import co.japl.android.synapsefit.core.domain.model.history.WorkoutSummaryItem
import kotlinx.coroutines.flow.Flow

interface WorkoutLogRepositoryPort {
    fun getLogsForExercise(exerciseId: String): Flow<List<WorkoutLog>>

    fun getLogsForDateRange(
        startTimestamp: Long,
        endTimestamp: Long,
    ): Flow<List<WorkoutLog>>

    fun getAllLogs(): Flow<List<WorkoutLog>>

    fun getLatestLogsForPlan(planId: String): Flow<List<WorkoutLog>>

    fun getHistoryRecords(): Flow<List<WorkoutHistoryRecord>>

    fun getWorkoutSummaries(): Flow<List<WorkoutSummaryItem>>

    fun getWorkoutDetailRecords(
        date: String,
        day: Int,
    ): Flow<List<WorkoutDetailRecord>>

    suspend fun saveLog(log: WorkoutLog)

    suspend fun deleteLog(id: String)
}
