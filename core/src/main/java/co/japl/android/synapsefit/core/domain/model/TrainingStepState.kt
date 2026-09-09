package co.japl.android.synapsefit.core.domain.model

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
            currentTimestamp: Long = System.currentTimeMillis(),
        ): Long {
            return maxOf(0L, targetTimestamp - currentTimestamp)
        }
    }
}
