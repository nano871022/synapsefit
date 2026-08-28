package co.japl.android.synapsefit.services.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["plan_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["plan_id"])],
)
data class ExerciseEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "plan_id")
    val planId: String,
    val name: String,
    @ColumnInfo(name = "muscle_group")
    val muscleGroup: String,
    @ColumnInfo(name = "target_sets")
    val targetSets: Int,
    @ColumnInfo(name = "target_reps")
    val targetReps: String,
    @ColumnInfo(name = "rest_seconds")
    val restSeconds: Int,
    @ColumnInfo(name = "day")
    val day: Int = 1,
    @ColumnInfo(name = "guide_video_url")
    val guideVideoUrl: String? = null,
    @ColumnInfo(name = "guide_image_url")
    val guideImageUrl: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)
