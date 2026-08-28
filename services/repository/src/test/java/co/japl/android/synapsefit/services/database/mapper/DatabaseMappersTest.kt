package co.japl.android.synapsefit.services.database.mapper

import co.japl.android.synapsefit.core.domain.model.BodyMeasurement
import co.japl.android.synapsefit.core.domain.model.Exercise
import co.japl.android.synapsefit.core.domain.model.LlmConfig
import co.japl.android.synapsefit.core.domain.model.LlmProvider
import co.japl.android.synapsefit.core.domain.model.SourceDevice
import co.japl.android.synapsefit.core.domain.model.WorkoutLog
import co.japl.android.synapsefit.core.domain.model.WorkoutPlan
import org.junit.Assert.assertEquals
import org.junit.Test

class DatabaseMappersTest {
    @Test
    fun `BodyMeasurementEntity mapper converts bidirectionally`() {
        val now = System.currentTimeMillis()
        val domain =
            BodyMeasurement(
                id = "bm1",
                weightKg = 75.5,
                chestCm = 100.0,
                waistCm = 80.0,
                hipCm = 95.0,
                bicepLeftCm = 35.0,
                bicepRightCm = 35.5,
                thighLeftCm = 55.0,
                thighRightCm = 55.5,
                notes = "Morning measurement",
                createdAt = now,
                updatedAt = now,
            )

        val entity = domain.toEntity()
        val converted = entity.toDomain()

        assertEquals(domain, converted)
    }

    @Test
    fun `WorkoutPlanEntity mapper converts bidirectionally`() {
        val now = System.currentTimeMillis()
        val domain =
            WorkoutPlan(
                id = "wp1",
                title = "Hypertrophy",
                goalDescription = "Build muscle",
                isActive = true,
                generatedByLlm = true,
                createdAt = now,
                updatedAt = now,
            )

        val entity = domain.toEntity()
        val converted = entity.toDomain()

        assertEquals(domain, converted)
    }

    @Test
    fun `ExerciseEntity mapper converts bidirectionally`() {
        val now = System.currentTimeMillis()
        val domain =
            Exercise(
                id = "ex1",
                planId = "wp1",
                name = "Bench Press",
                muscleGroup = "Chest",
                targetSets = 4,
                targetReps = "8-12",
                restSeconds = 90,
                day = 2,
                guideVideoUrl = "https://example.com/video",
                guideImageUrl = "https://example.com/image",
                createdAt = now,
                updatedAt = now,
            )

        val entity = domain.toEntity()
        val converted = entity.toDomain()

        assertEquals(domain, converted)
    }

    @Test
    fun `WorkoutLogEntity mapper converts bidirectionally`() {
        val now = System.currentTimeMillis()
        val domain =
            WorkoutLog(
                id = "wl1",
                exerciseId = "ex1",
                repsCompleted = 10,
                weightLiftedKg = 80.0,
                heartRateBpm = 135,
                sourceDevice = SourceDevice.MOBILE,
                timestamp = now,
                createdAt = now,
                updatedAt = now,
            )

        val entity = domain.toEntity()
        val converted = entity.toDomain()

        assertEquals(domain, converted)
    }

    @Test
    fun `LlmConfigEntity mapper converts bidirectionally`() {
        val now = System.currentTimeMillis()
        val domain =
            LlmConfig(
                id = "cfg1",
                provider = LlmProvider.GEMINI,
                apiKeyEncrypted = "encrypted_key",
                modelName = "gemini-pro",
                isActive = true,
                createdAt = now,
                updatedAt = now,
            )

        val entity = domain.toEntity()
        val converted = entity.toDomain()

        assertEquals(domain, converted)
    }
}
