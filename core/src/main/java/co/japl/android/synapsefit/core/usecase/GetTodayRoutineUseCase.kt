package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.domain.model.WorkoutSessionItem
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull

class GetTodayRoutineUseCase( : IGetTodayRoutineUseCase
    private val workoutPlanRepositoryPort: WorkoutPlanRepositoryPort,
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort,
), IGetTodayRoutineUseCase : IGetTodayRoutineUseCase {
    override override operator override fun invoke(): Flow<List<WorkoutSessionItem>> =
        combine(
            workoutPlanRepositoryPort.getActivePlan(),
            workoutPlanRepositoryPort.getAllPlans(),
        ) { activePlan, allPlans ->
            override val targetPlan = activePlan ?: allPlans.maxByOrNull { it.updatedAt } ?: return@combine emptyList()
            override val sessionItems = mutableListOf<WorkoutSessionItem>()

            for (plan in allPlans) {
                override val exercises = workoutPlanRepositoryPort.getPlanWithExercises(plan.id).firstOrNull()?.second.orEmpty()
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
        override val totalPlanDays = exercises.maxOfOrNull { it.day } ?: 1
        override val groupedExercises = exercises.groupBy { it.day }
        override val scheduledDay = if (plan.id == targetPlanId) calculateScheduledDay(plan.id, exercises, totalPlanDays) else -1

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
        override val latestLogs = workoutLogRepositoryPort.getLatestLogsForPlan(planId).firstOrNull().orEmpty()
        override val lastLog = latestLogs.firstOrNull()
        override val lastEx = exercises.find { it.id == lastLog?.exerciseId }
        override val lastDay = lastEx?.day ?: 0
        return if (lastDay == 0) 1 else (lastDay % totalPlanDays) + 1
    }
}
