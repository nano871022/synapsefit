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

class GetGroupedWorkoutHistoryUseCase(
    private val workoutLogRepository: WorkoutLogRepositoryPort,
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())

    operator fun invoke(): Flow<List<WorkoutHistoryGroup>> {
        return workoutLogRepository.getHistoryRecords().map { records ->
            groupRecords(records)
        }
    }

    private fun groupRecords(records: List<WorkoutHistoryRecord>): List<WorkoutHistoryGroup> {
        if (records.isEmpty()) return emptyList()

        // 1. Sort by timestamp descending
        val sortedRecords = records.sortedByDescending { it.timestamp }

        // 2. Cluster into sessions by (Date + PlanId + Day)
        val sessionClusters = mutableListOf<MutableList<WorkoutHistoryRecord>>()
        var currentCluster: MutableList<WorkoutHistoryRecord>? = null
        var lastClusterKey: String? = null

        for (record in sortedRecords) {
            val date = dateFormatter.format(Instant.ofEpochMilli(record.timestamp))
            val clusterKey = "${date}_${record.planId}_${record.day}"

            if (currentCluster == null || clusterKey != lastClusterKey) {
                currentCluster = mutableListOf()
                sessionClusters.add(currentCluster)
                lastClusterKey = clusterKey
            }
            currentCluster.add(record)
        }

        // 3. Map each cluster to a WorkoutHistoryGroup
        return sessionClusters.map { clusterRecords ->
            val first = clusterRecords.first()

            // Group by exercise within the session
            val exerciseGroups = clusterRecords.groupBy { it.exerciseId }

            val exerciseHistories =
                exerciseGroups.map { (exId, exRecords) ->
                    // Sort sets by timestamp ascending
                    val sortedSets = exRecords.sortedBy { it.timestamp }
                    val sets =
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

            val totalVolume = clusterRecords.sumOf { it.repsCompleted * it.weightLiftedKg }
            val totalDuration = clusterRecords.sumOf { it.durationSeconds }
            val muscleGroups = clusterRecords.map { it.muscleGroup }.filter { it.isNotBlank() }.distinct()

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
