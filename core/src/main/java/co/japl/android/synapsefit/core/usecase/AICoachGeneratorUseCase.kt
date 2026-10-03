package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.EquipmentPreference
import co.japl.android.synapsefit.core.domain.model.TrainingLocation
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.core.port.secondary.LlmClientPort
import co.japl.android.synapsefit.core.port.secondary.LlmConfigRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.UserProfileRepositoryPort
import co.japl.android.synapsefit.core.port.secondary.WorkoutPlanRepositoryPort

class AICoachGeneratorUseCase(
    private val llmConfigRepository: LlmConfigRepositoryPort,
    private val llmClient: LlmClientPort,
    private val workoutPlanRepository: WorkoutPlanRepositoryPort,
    private val userProfileRepository: UserProfileRepositoryPort,
) : IAICoachGeneratorUseCase {
    private val generateWorkoutPlanUseCase = GenerateWorkoutPlanUseCase(
        llmConfigRepositoryPort = llmConfigRepository,
        llmClientPort = llmClient,
        workoutPlanRepositoryPort = workoutPlanRepository,
        userProfileRepositoryPort = userProfileRepository,
    )
    private val optimizeWorkoutPromptUseCase = OptimizeWorkoutPromptUseCase(llmConfigRepository, llmClient)

    override suspend fun generatePlan(prompt: String): Result<WorkoutPlan> =
        generateWorkoutPlanUseCase(
            promptContext = prompt,
            location = TrainingLocation.GYM,
            equipment = EquipmentPreference.DUMBBELLS,
        ).map { it.first }

    override suspend fun optimizePrompt(userPrompt: String): Result<String> =
        optimizeWorkoutPromptUseCase(
            userPrompt = userPrompt,
            location = TrainingLocation.GYM,
            equipment = EquipmentPreference.DUMBBELLS,
        )
}
