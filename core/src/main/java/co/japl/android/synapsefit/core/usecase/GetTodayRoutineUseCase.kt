package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.domain.model.WorkoutSessionItem
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull

class GetTodayRoutineUseCase(
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort,
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort,
) {
    operator fun invoke(): Flow<List<WorkoutSessionItem>> =
        combine(
            workoutPlanRepositoryPort.getActivePlan(),
            workoutPlanRepositoryPort.getAllPlans(),
        ) { activePlan, allPlans ->
            val targetPlan = activePlan ?: allPlans.maxByOrNull { it.updatedAt } ?: return@combine emptyList()
            val sessionItems = mutableListOf<WorkoutSessionItem>()

            for (plan in allPlans) {
                val exercises = workoutPlanRepositoryPort.getPlanWithExercises(plan.id).firstOrNull()?.second.orEmpty()
                if (exercises.isNotEmpty()) {
                    sessionItems.addAll(buildPlanSessionItems(plan, exercises, targetPlan.id))
                }
            }

            sessionItems.sortedWith(
                compareByDescending<WorkoutSessionItem> { it.isTodayScheduled }
                    .thenBy { it.planTitle }
                    .thenBy { it.day },
            )
        }

    private suspend fun buildPlanSessionItems(
        plan: WorkoutPlan,
        exercises: List<Exercise>,
        targetPlanId: String,
    ): List<WorkoutSessionItem> {
        val totalPlanDays = exercises.maxOfOrNull { it.day } ?: 1
        val groupedExercises = exercises.groupBy { it.day }
        val scheduledDay = if (plan.id == targetPlanId) calculateScheduledDay(plan.id, exercises, totalPlanDays) else -1

        return groupedExercises.map { (dayNumber, dayExercises) ->
            WorkoutSessionItem(
                planId = plan.id,
                planTitle = plan.title,
                day = dayNumber,
                exerciseCount = dayExercises.size,
                isTodayScheduled = (plan.id == targetPlanId && dayNumber == scheduledDay),
                exercises = dayExercises,
            )
        }
    }

    private suspend fun calculateScheduledDay(
        planId: String,
        exercises: List<Exercise>,
        totalPlanDays: Int,
    ): Int {
        val latestLogs = workoutLogRepositoryPort.getLatestLogsForPlan(planId).firstOrNull().orEmpty()
        val lastLog = latestLogs.firstOrNull()
        val lastEx = exercises.find { it.id == lastLog?.exerciseId }
        val lastDay = lastEx?.day ?: 0
        return if (lastDay == 0) 1 else (lastDay % totalPlanDays) + 1
    }
}
