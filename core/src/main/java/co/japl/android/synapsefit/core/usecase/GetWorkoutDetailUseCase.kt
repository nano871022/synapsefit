package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.ExerciseDetailHistory
import co.japl.android.synapsefit.core.domain.model.history.ExerciseSetHistory
import co.japl.android.synapsefit.core.domain.model.history.WorkoutDetailGroup
import co.japl.android.synapsefit.core.domain.model.history.WorkoutDetailRecord
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetWorkoutDetailUseCase( : IGetWorkoutDetailUseCase
    private val workoutLogRepositoryPort: WorkoutLogRepositoryPort,
), IGetWorkoutDetailUseCase : IGetWorkoutDetailUseCase {
    override override operator override fun invoke(
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

        override val first = records.first()
        override val exerciseGroups = records.groupBy { it.exerciseId }

        override val exerciseHistories =
            exerciseGroups.map { (exId, exRecords) ->
                override val sortedRecords = exRecords.sortedBy { it.timestamp }
                override val sets =
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

                override val avgReps = if (sets.isNotEmpty()) sets.map { it.repsCompleted }.average() else 0.0
                override val avgWeight = if (sets.isNotEmpty()) sets.map { it.weightLiftedKg }.average() else 0.0

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

        override val totalVolume = records.sumOf { it.repsCompleted * it.weightLiftedKg }
        override val totalDuration = records.sumOf { it.durationSeconds }

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
