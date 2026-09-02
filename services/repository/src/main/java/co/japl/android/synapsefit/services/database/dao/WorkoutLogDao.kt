package co.japl.android.synapsefit.services.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.japl.android.synapsefit.services.database.entity.WorkoutLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutLogDao {
    @Query("SELECT * FROM workout_logs WHERE exercise_id = :exerciseId ORDER BY timestamp DESC")
    fun getLogsForExercise(exerciseId: String): Flow<List<WorkoutLogEntity>>

    @Query(
        "SELECT * FROM workout_logs " +
            "WHERE timestamp >= :startTimestamp AND timestamp <= :endTimestamp " +
            "ORDER BY timestamp DESC",
    )
    fun getLogsForDateRange(
        startTimestamp: Long,
        endTimestamp: Long,
    ): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<WorkoutLogEntity>>

    @Query(
        "SELECT l.* FROM workout_logs l " +
            "INNER JOIN exercises e ON l.exercise_id = e.id " +
            "WHERE e.plan_id = :planId " +
            "ORDER BY l.timestamp DESC LIMIT 50",
    )
    fun getLatestLogsForPlan(planId: String): Flow<List<WorkoutLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: WorkoutLogEntity)

    @Query("DELETE FROM workout_logs WHERE id = :id")
    suspend fun deleteLog(id: String)
}
