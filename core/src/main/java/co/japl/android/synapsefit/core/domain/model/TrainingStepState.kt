package co.japl.android.synapsefit.core.domain.model

import co.japl.android.synapsefit.util.DateTimeUtils

sealed interface TrainingStepState {
    data class Active(
        val exerciseSession: ExerciseSession,
        val currentSet: Int,
    ) : TrainingStepState

    data class Cooldown(
        val exerciseSession: ExerciseSession,
        val targetTimestamp: Long,
        val remainingMillis: Long,
    ) : TrainingStepState

    data class ReadyForNext(
        val nextExerciseSession: ExerciseSession?,
    ) : TrainingStepState

    companion object {
        fun calculateRemainingMillis(
            targetTimestamp: Long,
            currentTimestamp: Long = DateTimeUtils.getCurrentTimestamp(),
        ): Long {
            return maxOf(0L, targetTimestamp - currentTimestamp)
        }
    }
}
