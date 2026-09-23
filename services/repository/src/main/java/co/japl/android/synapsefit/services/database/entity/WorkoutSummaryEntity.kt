package co.japl.android.synapsefit.services.database.entity

import androidx.room.ColumnInfo

data class WorkoutSummaryEntity(
    @ColumnInfo(name = "sessionId") val sessionId: String,
    @ColumnInfo(name = "sessionTitle") val sessionTitle: String,
    @ColumnInfo(name = "avgWeightKg") val avgWeightKg: Double,
    @ColumnInfo(name = "totalDurationSeconds") val totalDurationSeconds: Long,
    @ColumnInfo(name = "dateIso") val dateIso: String,
    @ColumnInfo(name = "day") val day: Int,
    @ColumnInfo(name = "totalExercisesCount") val totalExercisesCount: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
)
