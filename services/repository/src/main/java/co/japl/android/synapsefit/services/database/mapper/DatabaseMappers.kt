package co.japl.android.synapsefit.services.database.mapper

import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import co.japl.android.synapsefit.services.database.entity.BodyMeasurementEntity
import co.japl.android.synapsefit.services.database.entity.ExerciseEntity
import co.japl.android.synapsefit.services.database.entity.LlmConfigEntity
import co.japl.android.synapsefit.services.database.entity.WorkoutLogEntity
import co.japl.android.synapsefit.services.database.entity.WorkoutPlanEntity

fun BodyMeasurementEntity.toDomain(): BodyMeasurement =
    BodyMeasurement(
        id = id,
        weightKg = weightKg,
        chestCm = chestCm,
        waistCm = waistCm,
        hipCm = hipCm,
        bicepLeftCm = bicepLeftCm,
        bicepRightCm = bicepRightCm,
        thighLeftCm = thighLeftCm,
        thighRightCm = thighRightCm,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun BodyMeasurement.toEntity(): BodyMeasurementEntity =
    BodyMeasurementEntity(
        id = id,
        weightKg = weightKg,
        chestCm = chestCm,
        waistCm = waistCm,
        hipCm = hipCm,
        bicepLeftCm = bicepLeftCm,
        bicepRightCm = bicepRightCm,
        thighLeftCm = thighLeftCm,
        thighRightCm = thighRightCm,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun WorkoutPlanEntity.toDomain(): WorkoutPlan =
    WorkoutPlan(
        id = id,
        title = title,
        goalDescription = goalDescription,
        isActive = isActive,
        generatedByLlm = generatedByLlm,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun WorkoutPlan.toEntity(): WorkoutPlanEntity =
    WorkoutPlanEntity(
        id = id,
        title = title,
        goalDescription = goalDescription,
        isActive = isActive,
        generatedByLlm = generatedByLlm,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun ExerciseEntity.toDomain(): Exercise =
    Exercise(
        id = id,
        planId = planId,
        name = name,
        muscleGroup = muscleGroup,
        targetSets = targetSets,
        targetReps = targetReps,
        restSeconds = restSeconds,
        guideVideoUrl = guideVideoUrl,
        guideImageUrl = guideImageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun Exercise.toEntity(): ExerciseEntity =
    ExerciseEntity(
        id = id,
        planId = planId,
        name = name,
        muscleGroup = muscleGroup,
        targetSets = targetSets,
        targetReps = targetReps,
        restSeconds = restSeconds,
        guideVideoUrl = guideVideoUrl,
        guideImageUrl = guideImageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun WorkoutLogEntity.toDomain(): WorkoutLog =
    WorkoutLog(
        id = id,
        exerciseId = exerciseId,
        repsCompleted = repsCompleted,
        weightLiftedKg = weightLiftedKg,
        heartRateBpm = heartRateBpm,
        sourceDevice = runCatching { SourceDevice.valueOf(sourceDevice) }.getOrDefault(SourceDevice.MOBILE),
        timestamp = timestamp,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun WorkoutLog.toEntity(): WorkoutLogEntity =
    WorkoutLogEntity(
        id = id,
        exerciseId = exerciseId,
        repsCompleted = repsCompleted,
        weightLiftedKg = weightLiftedKg,
        heartRateBpm = heartRateBpm,
        sourceDevice = sourceDevice.name,
        timestamp = timestamp,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun LlmConfigEntity.toDomain(): LlmConfig =
    LlmConfig(
        id = id,
        provider = runCatching { LlmProvider.valueOf(provider) }.getOrDefault(LlmProvider.GEMINI),
        apiKeyEncrypted = apiKeyEncrypted,
        modelName = modelName,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun LlmConfig.toEntity(): LlmConfigEntity =
    LlmConfigEntity(
        id = id,
        provider = provider.name,
        apiKeyEncrypted = apiKeyEncrypted,
        modelName = modelName,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
