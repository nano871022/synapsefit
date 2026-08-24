package co.japl.android.synapsefit.services.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_logs",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["exercise_id"])],
)
data class WorkoutLogEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: String,
    @ColumnInfo(name = "reps_completed")
    val repsCompleted: Int,
    @ColumnInfo(name = "weight_lifted_kg")
    val weightLiftedKg: Double,
    @ColumnInfo(name = "heart_rate_bpm")
    val heartRateBpm: Int? = null,
    @ColumnInfo(name = "source_device", defaultValue = "'MOBILE'")
    val sourceDevice: String,
    val timestamp: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)
