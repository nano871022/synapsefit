package co.japl.android.synapsefit.services.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

private const val DB_VERSION_7 = 7

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
    version = DB_VERSION_7,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
    ],
    exportSchema = true,
)
abstract class SynapseFitDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao

    abstract fun bodyMeasurementDao(): BodyMeasurementDao

    abstract fun workoutPlanDao(): WorkoutPlanDao

    abstract fun workoutLogDao(): WorkoutLogDao

    abstract fun llmConfigDao(): LlmConfigDao

    companion object {
        private const val MIGRATION_VERSION_6 = 6
        private const val MIGRATION_VERSION_7 = 7

        val MIGRATION_6_7 =
            object : Migration(MIGRATION_VERSION_6, MIGRATION_VERSION_7) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // Recreate exercises table to fix missing default value for 'day' column
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `exercises_new` (
                            `id` TEXT NOT NULL,
                            `plan_id` TEXT NOT NULL,
                            `name` TEXT NOT NULL,
                            `muscle_group` TEXT NOT NULL,
                            `target_sets` INTEGER NOT NULL,
                            `target_reps` TEXT NOT NULL,
                            `rest_seconds` INTEGER NOT NULL,
                            `day` INTEGER NOT NULL DEFAULT 1,
                            `guide_video_url` TEXT,
                            `guide_image_url` TEXT,
                            `created_at` INTEGER NOT NULL,
                            `updated_at` INTEGER NOT NULL,
                            PRIMARY KEY(`id`),
                            FOREIGN KEY(`plan_id`) REFERENCES `workout_plans`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                        )
                        """.trimIndent(),
                    )

                    db.execSQL(
                        """
                        INSERT INTO `exercises_new` (`id`, `plan_id`, `name`, `muscle_group`, `target_sets`, `target_reps`, `rest_seconds`, `day`, `guide_video_url`, `guide_image_url`, `created_at`, `updated_at`)
                        SELECT `id`, `plan_id`, `name`, `muscle_group`, `target_sets`, `target_reps`, `rest_seconds`, IFNULL(`day`, 1), `guide_video_url`, `guide_image_url`, `created_at`, `updated_at` FROM `exercises`
                        """.trimIndent(),
                    )

                    db.execSQL("DROP TABLE `exercises`")
                    db.execSQL("ALTER TABLE `exercises_new` RENAME TO `exercises`")
                    db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercises_plan_id` ON `exercises` (`plan_id`)")
                }
            }
    }
}
