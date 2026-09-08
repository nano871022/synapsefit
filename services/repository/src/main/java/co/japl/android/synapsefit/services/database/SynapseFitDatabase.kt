package co.japl.android.synapsefit.services.database

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

private const val DB_VERSION_1 = 1
private const val DB_VERSION_2 = 2
private const val DB_VERSION_3 = 3
private const val DB_VERSION_4 = 4
private const val DB_VERSION_5 = 5
private const val DB_VERSION_6 = 6

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
    version = DB_VERSION_6,
    exportSchema = true,
)
abstract class SynapseFitDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao

    abstract fun bodyMeasurementDao(): BodyMeasurementDao

    abstract fun workoutPlanDao(): WorkoutPlanDao

    abstract fun workoutLogDao(): WorkoutLogDao

    abstract fun llmConfigDao(): LlmConfigDao

    companion object {
        val MIGRATION_1_2 =
            object : Migration(DB_VERSION_1, DB_VERSION_2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `tbl_medical_result` (" +
                            "`id` TEXT NOT NULL, " +
                            "`profile_code` TEXT NOT NULL, " +
                            "`result` TEXT NOT NULL, " +
                            "`create_dt` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`id`))",
                    )
                }
            }

        val MIGRATION_2_3 =
            object : Migration(DB_VERSION_2, DB_VERSION_3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE `exercises` ADD COLUMN `day` INTEGER NOT NULL DEFAULT 1")
                }
            }

        val MIGRATION_3_4 =
            object : Migration(DB_VERSION_3, DB_VERSION_4) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE `workout_plans` ADD COLUMN `total_sessions` INTEGER NOT NULL DEFAULT 12")
                }
            }

        val MIGRATION_4_5 =
            object : Migration(DB_VERSION_4, DB_VERSION_5) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE `exercises` ADD COLUMN `guide_video_url` TEXT DEFAULT NULL")
                    db.execSQL("ALTER TABLE `exercises` ADD COLUMN `guide_image_url` TEXT DEFAULT NULL")
                }
            }

        val MIGRATION_5_6 =
            object : Migration(DB_VERSION_5, DB_VERSION_6) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE `workout_logs` ADD COLUMN `duration_seconds` INTEGER NOT NULL DEFAULT 0")
                }
            }

        val ALL_MIGRATIONS =
            arrayOf(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6,
            )
    }
}
