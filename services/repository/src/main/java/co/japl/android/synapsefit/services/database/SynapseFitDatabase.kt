package co.japl.android.synapsefit.services.database

import androidx.room.Database
import androidx.room.RoomDatabase
import co.japl.android.synapsefit.services.database.dao.BodyMeasurementDao
import co.japl.android.synapsefit.services.database.dao.LlmConfigDao
import co.japl.android.synapsefit.services.database.dao.UserProfileDao
import co.japl.android.synapsefit.services.database.dao.WorkoutLogDao
import co.japl.android.synapsefit.services.database.dao.WorkoutPlanDao
import co.japl.android.synapsefit.services.database.entity.BodyMeasurementEntity
import co.japl.android.synapsefit.services.database.entity.ExerciseEntity
import co.japl.android.synapsefit.services.database.entity.LlmConfigEntity
import co.japl.android.synapsefit.services.database.entity.MedicalRecommendationEntity
import co.japl.android.synapsefit.services.database.entity.UserProfileEntity
import co.japl.android.synapsefit.services.database.entity.WorkoutLogEntity
import co.japl.android.synapsefit.services.database.entity.WorkoutPlanEntity

@Database(
    entities = [
        UserProfileEntity::class,
        MedicalRecommendationEntity::class,
        BodyMeasurementEntity::class,
        WorkoutPlanEntity::class,
        ExerciseEntity::class,
        WorkoutLogEntity::class,
        LlmConfigEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class SynapseFitDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao

    abstract fun bodyMeasurementDao(): BodyMeasurementDao

    abstract fun workoutPlanDao(): WorkoutPlanDao

    abstract fun workoutLogDao(): WorkoutLogDao

    abstract fun llmConfigDao(): LlmConfigDao
}
