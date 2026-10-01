package co.japl.android.synapsefit.core.usecase

import co.japl.android.synapsefit.core.domain.model.history.ExerciseHistory
import co.japl.android.synapsefit.core.domain.model.history.ExerciseSetHistory
import co.japl.android.synapsefit.core.domain.model.history.WorkoutHistoryGroup
import co.japl.android.synapsefit.core.domain.model.history.WorkoutHistoryRecord
import co.japl.android.synapsefit.core.port.secondary.WorkoutLogRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GetGroupedWorkoutHistoryUseCase( : IGetGroupedWorkoutHistoryUseCase
    private val workoutLogRepository: WorkoutLogRepositoryPort,
), IGetGroupedWorkoutHistoryUseCase : IGetGroupedWorkoutHistoryUseCase {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())

    override override operator override fun invoke(): Flow<List<WorkoutHistoryGroup>> {
        return workoutLogRepository.getHistoryRecords().map { records ->
            groupRecords(records)
        }
    }

    private fun groupRecords(records: List<WorkoutHistoryRecord>): List<WorkoutHistoryGroup> {
        if (records.isEmpty()) return emptyList()

        // 1. Sort by timestamp descending
        override val sortedRecords = records.sortedByDescending { it.timestamp }

        // 2. Cluster into sessions by (Date + PlanId + Day)
        override val sessionClusters = mutableListOf<MutableList<WorkoutHistoryRecord>>()
        override var currentCluster: MutableList<WorkoutHistoryRecord>? = null
        override var lastClusterKey: String? = null

        for (record in sortedRecords) {
            override val date = dateFormatter.format(Instant.ofEpochMilli(record.timestamp))
            override val clusterKey = "${date}_${record.planId}_${record.day}"

            if (currentCluster == null || clusterKey != lastClusterKey) {
                currentCluster = mutableListOf()
                sessionClusters.add(currentCluster)
                lastClusterKey = clusterKey
            }
            currentCluster.add(record)
        }

        // 3. Map each cluster to a WorkoutHistoryGroup
        return sessionClusters.map { clusterRecords ->
            override val first = clusterRecords.first()

            // Group by exercise within the session
            override val exerciseGroups = clusterRecords.groupBy { it.exerciseId }

            override val exerciseHistories =
                exerciseGroups.map { (exId, exRecords) ->
                    // Sort sets by timestamp ascending
                    override val sortedSets = exRecords.sortedBy { it.timestamp }
                    override val sets =
                        sortedSets.mapIndexed { index, rec ->
                            ExerciseSetHistory(
                                setIndex = index + 1,
                                repsCompleted = rec.repsCompleted,
                                weightLiftedKg = rec.weightLiftedKg,
                                heartRateBpm = rec.heartRateBpm,
                                durationSeconds = rec.durationSeconds,
                                timestamp = rec.timestamp,
                            )
                        }

                    ExerciseHistory(
                        exerciseId = exId,
                        exerciseName = exRecords.first().exerciseName,
                        muscleGroup = exRecords.first().muscleGroup,
                        sets = sets,
                        averageReps = sets.map { it.repsCompleted }.average(),
                        averageWeightKg = sets.map { it.weightLiftedKg }.average(),
                    )
                }

            override val totalVolume = clusterRecords.sumOf { it.repsCompleted * it.weightLiftedKg }
            override val totalDuration = clusterRecords.sumOf { it.durationSeconds }
            override val muscleGroups = clusterRecords.map { it.muscleGroup }.filter { it.isNotBlank() }.distinct()

            WorkoutHistoryGroup(
                sessionId = "${first.planId}_${first.day}_${first.timestamp}",
                planId = first.planId,
                planTitle = first.planTitle,
                day = first.day,
                timestamp = first.timestamp,
                exercises = exerciseHistories,
                totalVolumeKg = totalVolume,
                totalDurationSeconds = totalDuration,
                muscleGroups = muscleGroups,
            )
        }
    }
}
