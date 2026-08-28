package co.japl.android.synapsefit.services.repository

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import co.japl.android.synapsefit.services.database.dao.WorkoutPlanDao
import co.japl.android.synapsefit.services.database.mapper.toDomain
import co.japl.android.synapsefit.services.database.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class WorkoutPlanRepositoryAdapter(
    private val dao: WorkoutPlanDao,
) : WorkoutPlanRepositoryPort {
    override fun getActivePlan(): Flow<WorkoutPlan?> =
        dao.getActivePlan().map { entity ->
            entity?.toDomain()
        }

    override fun getPlanWithExercises(planId: String): Flow<Pair<WorkoutPlan, List<Exercise>>?> =
        combine(
            dao.getPlanById(planId),
            dao.getExercisesForPlan(planId),
        ) { planEntity, exerciseEntities ->
            if (planEntity == null) {
                null
            } else {
                Pair(planEntity.toDomain(), exerciseEntities.map { it.toDomain() })
            }
        }

    override fun getAllPlans(): Flow<List<WorkoutPlan>> =
        dao.getAllPlans().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun savePlan(
        plan: WorkoutPlan,
        exercises: List<Exercise>,
    ) {
        dao.insertPlanWithExercises(
            plan = plan.toEntity(),
            exercises = exercises.map { it.toEntity() },
        )
    }

    override suspend fun setActivePlan(planId: String) {
        dao.setActivePlan(planId)
    }

    override suspend fun deletePlan(planId: String) {
        dao.deletePlan(planId)
    }

    override suspend fun updateExerciseMedia(
        exerciseId: String,
        videoUrl: String?,
        imageUrl: String?,
    ) {
        dao.updateExerciseMedia(exerciseId, videoUrl, imageUrl)
    }

    @Suppress("ReturnCount")
    override suspend fun findMediaByExerciseName(exerciseName: String): Pair<String, String>? {
        val entity = dao.findMediaByExerciseName(exerciseName) ?: return null
        val video = entity.guideVideoUrl ?: return null
        val image = entity.guideImageUrl ?: return null
        return Pair(video, image)
    }
}
