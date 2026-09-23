package co.japl.android.synapsefit.services.database.entity

import androidx.room.ColumnInfo

data class WorkoutDetailRecordEntity(
    @ColumnInfo(name = "logId") val logId: String,
    @ColumnInfo(name = "exerciseId") val exerciseId: String,
    @ColumnInfo(name = "planId") val planId: String,
    @ColumnInfo(name = "planTitle") val planTitle: String,
    @ColumnInfo(name = "day") val day: Int,
    @ColumnInfo(name = "exerciseName") val exerciseName: String,
    @ColumnInfo(name = "exerciseDetail") val exerciseDetail: String?,
    @ColumnInfo(name = "muscleGroup") val muscleGroup: String,
    @ColumnInfo(name = "repsCompleted") val repsCompleted: Int,
    @ColumnInfo(name = "weightLiftedKg") val weightLiftedKg: Double,
    @ColumnInfo(name = "heartRateBpm") val heartRateBpm: Int?,
    @ColumnInfo(name = "durationSeconds") val durationSeconds: Long,
    @ColumnInfo(name = "sourceDevice") val sourceDevice: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "dateIso") val dateIso: String,
)
