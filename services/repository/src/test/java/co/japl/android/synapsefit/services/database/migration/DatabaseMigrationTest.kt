package co.japl.android.synapsefit.services.database.migration

import androidx.sqlite.db.SupportSQLiteDatabase
import co.japl.android.synapsefit.services.database.SynapseFitDatabase
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class DatabaseMigrationTest {
    @Test
    fun allMigrationsArrayContainsMigrations1To6() {
        assertEquals(5, SynapseFitDatabase.ALL_MIGRATIONS.size)
        assertEquals(1, SynapseFitDatabase.MIGRATION_1_2.startVersion)
        assertEquals(2, SynapseFitDatabase.MIGRATION_1_2.endVersion)

        assertEquals(2, SynapseFitDatabase.MIGRATION_2_3.startVersion)
        assertEquals(3, SynapseFitDatabase.MIGRATION_2_3.endVersion)

        assertEquals(3, SynapseFitDatabase.MIGRATION_3_4.startVersion)
        assertEquals(4, SynapseFitDatabase.MIGRATION_3_4.endVersion)

        assertEquals(4, SynapseFitDatabase.MIGRATION_4_5.startVersion)
        assertEquals(5, SynapseFitDatabase.MIGRATION_4_5.endVersion)

        assertEquals(5, SynapseFitDatabase.MIGRATION_5_6.startVersion)
        assertEquals(6, SynapseFitDatabase.MIGRATION_5_6.endVersion)
    }

    @Test
    fun migration1To2CreatesTblMedicalResultTable() {
        val mockDb = mockk<SupportSQLiteDatabase>(relaxed = true)
        SynapseFitDatabase.MIGRATION_1_2.migrate(mockDb)

        verify {
            mockDb.execSQL(match { it.contains("CREATE TABLE IF NOT EXISTS `tbl_medical_result`") })
        }
    }

    @Test
    fun migration2To3AddsDayColumnToExercises() {
        val mockDb = mockk<SupportSQLiteDatabase>(relaxed = true)
        SynapseFitDatabase.MIGRATION_2_3.migrate(mockDb)

        verify {
            mockDb.execSQL("ALTER TABLE `exercises` ADD COLUMN `day` INTEGER NOT NULL DEFAULT 1")
        }
    }

    @Test
    fun migration3To4AddsTotalSessionsColumnToWorkoutPlans() {
        val mockDb = mockk<SupportSQLiteDatabase>(relaxed = true)
        SynapseFitDatabase.MIGRATION_3_4.migrate(mockDb)

        verify {
            mockDb.execSQL("ALTER TABLE `workout_plans` ADD COLUMN `total_sessions` INTEGER NOT NULL DEFAULT 12")
        }
    }

    @Test
    fun migration4To5AddsGuideVideoAndImageUrlsToExercises() {
        val mockDb = mockk<SupportSQLiteDatabase>(relaxed = true)
        SynapseFitDatabase.MIGRATION_4_5.migrate(mockDb)

        verify {
            mockDb.execSQL("ALTER TABLE `exercises` ADD COLUMN `guide_video_url` TEXT DEFAULT NULL")
            mockDb.execSQL("ALTER TABLE `exercises` ADD COLUMN `guide_image_url` TEXT DEFAULT NULL")
        }
    }

    @Test
    fun migration5To6AddsDurationSecondsColumnToWorkoutLogs() {
        val mockDb = mockk<SupportSQLiteDatabase>(relaxed = true)
        SynapseFitDatabase.MIGRATION_5_6.migrate(mockDb)

        verify {
            mockDb.execSQL("ALTER TABLE `workout_logs` ADD COLUMN `duration_seconds` INTEGER NOT NULL DEFAULT 0")
        }
    }
}
