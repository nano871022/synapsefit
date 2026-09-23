package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.ExerciseDetailHistory
import co.japl.android.synapsefit.core.domain.model.history.ExerciseSetHistory
import co.japl.android.synapsefit.core.domain.model.history.WorkoutDetailGroup
import co.japl.android.synapsefit.core.domain.model.history.WorkoutDetailRecord
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetWorkoutDetailUseCase(
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort,
) {
    operator fun invoke(
        date: String,
        day: Int,
    ): Flow<WorkoutDetailGroup?> {
        return workoutLogRepositoryPort.getWorkoutDetailRecords(date, day).map { records ->
            mapRecordsToDetailGroup(records, date, day)
        }
    }

    private fun mapRecordsToDetailGroup(
        records: List<WorkoutDetailRecord>,
        date: String,
        day: Int,
    ): WorkoutDetailGroup? {
        if (records.isEmpty()) return null

        val first = records.first()
        val exerciseGroups = records.groupBy { it.exerciseId }

        val exerciseHistories =
            exerciseGroups.map { (exId, exRecords) ->
                val sortedRecords = exRecords.sortedBy { it.timestamp }
                val sets =
                    sortedRecords.mapIndexed { index, rec ->
                        ExerciseSetHistory(
                            setIndex = index + 1,
                            repsCompleted = rec.repsCompleted,
                            weightLiftedKg = rec.weightLiftedKg,
                            heartRateBpm = rec.heartRateBpm,
                            durationSeconds = rec.durationSeconds,
                            timestamp = rec.timestamp,
                        )
                    }

                val avgReps = if (sets.isNotEmpty()) sets.map { it.repsCompleted }.average() else 0.0
                val avgWeight = if (sets.isNotEmpty()) sets.map { it.weightLiftedKg }.average() else 0.0

                ExerciseDetailHistory(
                    exerciseId = exId,
                    exerciseName = exRecords.first().exerciseName,
                    exerciseDetail = exRecords.first().exerciseDetail,
                    muscleGroup = exRecords.first().muscleGroup,
                    sets = sets,
                    averageReps = avgReps,
                    averageWeightKg = avgWeight,
                )
            }

        val totalVolume = records.sumOf { it.repsCompleted * it.weightLiftedKg }
        val totalDuration = records.sumOf { it.durationSeconds }

        return WorkoutDetailGroup(
            sessionId = "${first.planId}_${day}_$date",
            planId = first.planId,
            planTitle = first.planTitle,
            day = day,
            dateIso = date,
            timestamp = first.timestamp,
            exercises = exerciseHistories,
            totalVolumeKg = totalVolume,
            totalDurationSeconds = totalDuration,
        )
    }
}
