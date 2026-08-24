package co.japl.android.synapsefit.services.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_plans")
data class WorkoutPlanEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    @ColumnInfo(name = "goal_description")
    val goalDescription: String,
    @ColumnInfo(name = "is_active", defaultValue = "1")
    val isActive: Boolean,
    @ColumnInfo(name = "generated_by_llm", defaultValue = "0")
    val generatedByLlm: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)
