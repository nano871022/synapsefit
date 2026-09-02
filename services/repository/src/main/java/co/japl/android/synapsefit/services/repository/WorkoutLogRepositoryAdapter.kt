package co.japl.android.synapsefit.services.repository

import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.services.database.dao.WorkoutLogDao
import co.japl.android.synapsefit.services.database.mapper.toDomain
import co.japl.android.synapsefit.services.database.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkoutLogRepositoryAdapter(
    private val dao: WorkoutLogDao,
) : WorkoutLogRepositoryPort {
    override fun getLogsForExercise(exerciseId: String): Flow<List<WorkoutLog>> =
        dao.getLogsForExercise(exerciseId).map { entities -> entities.map { it.toDomain() } }

    override fun getLogsForDateRange(
        startTimestamp: Long,
        endTimestamp: Long,
    ): Flow<List<WorkoutLog>> =
        dao.getLogsForDateRange(startTimestamp, endTimestamp).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getAllLogs(): Flow<List<WorkoutLog>> =
        dao.getAllLogs().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getLatestLogsForPlan(planId: String): Flow<List<WorkoutLog>> =
        dao.getLatestLogsForPlan(planId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveLog(log: WorkoutLog) {
        dao.insertLog(log.toEntity())
    }

    override suspend fun deleteLog(id: String) {
        dao.deleteLog(id)
    }
}
