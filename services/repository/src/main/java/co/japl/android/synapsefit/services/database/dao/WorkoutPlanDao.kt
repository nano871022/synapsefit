package co.japl.android.synapsefit.services.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import co.japl.android.synapsefit.services.database.entity.ExerciseEntity
import co.japl.android.synapsefit.services.database.entity.WorkoutPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
@Suppress("TooManyFunctions")
interface WorkoutPlanDao {
    @Query("SELECT * FROM workout_plans WHERE is_active = 1 LIMIT 1")
    fun getActivePlan(): Flow<WorkoutPlanEntity?>

    @Query("SELECT * FROM workout_plans WHERE id = :planId LIMIT 1")
    fun getPlanById(planId: String): Flow<WorkoutPlanEntity?>

    @Query("SELECT * FROM exercises WHERE plan_id = :planId ORDER BY created_at ASC")
    fun getExercisesForPlan(planId: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM workout_plans ORDER BY updated_at DESC")
    fun getAllPlans(): Flow<List<WorkoutPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: WorkoutPlanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Transaction
    suspend fun insertPlanWithExercises(
        plan: WorkoutPlanEntity,
        exercises: List<ExerciseEntity>,
    ) {
        insertPlan(plan)
        insertExercises(exercises)
    }

    @Query("UPDATE workout_plans SET is_active = 0 WHERE id != :activePlanId")
    suspend fun deactivateOtherPlans(activePlanId: String)

    @Query("UPDATE workout_plans SET is_active = 1 WHERE id = :planId")
    suspend fun activatePlan(planId: String)

    @Transaction
    suspend fun setActivePlan(planId: String) {
        deactivateOtherPlans(planId)
        activatePlan(planId)
    }

    @Query("DELETE FROM workout_plans WHERE id = :planId")
    suspend fun deletePlan(planId: String)

    @Query("UPDATE exercises SET guide_video_url = :videoUrl, guide_image_url = :imageUrl WHERE id = :exerciseId")
    suspend fun updateExerciseMedia(
        exerciseId: String,
        videoUrl: String?,
        imageUrl: String?,
    )
}
