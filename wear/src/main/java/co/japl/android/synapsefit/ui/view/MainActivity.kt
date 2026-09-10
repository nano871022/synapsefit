package co.japl.android.synapsefit.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import co.japl.android.synapsefit.core.domain.model.TrainingStepState
import co.japl.android.synapsefit.ui.viewmodel.WearActiveWorkoutViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: WearActiveWorkoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiState by viewModel.uiState.collectAsState()

            if (uiState.isSessionStarted) {
                when (uiState.trainingStepState) {
                    is TrainingStepState.Active -> {
                        WearActiveWorkoutScreen(
                            uiState = uiState,
                            onIncrementReps = { viewModel.incrementReps() },
                            onDecrementReps = { viewModel.decrementReps() },
                            onCompleteSet = { viewModel.completeSet() },
                            onStartNextExercise = { viewModel.startNextExercise() },
                        )
                    }
                    is TrainingStepState.Cooldown, is TrainingStepState.ReadyForNext -> {
                        WearCooldownTransitionScreen(
                            trainingStepState = uiState.trainingStepState,
                            exerciseSessions = uiState.exerciseSessions,
                            heartRateBpm = uiState.currentHeartRateBpm,
                            onAddExtraTime = { viewModel.addExtraCooldownTime() },
                            onSkipRest = { viewModel.skipCooldown() },
                            onStartNextExercise = { viewModel.startNextExercise() },
                            onSelectExercise = { exerciseSession, index ->
                                val exercise =
                                    uiState.availableExercises.firstOrNull {
                                        it.id == exerciseSession.exerciseId
                                    }
                                if (exercise != null) {
                                    viewModel.selectExercise(exercise, index)
                                }
                            },
                        )
                    }
                }
            } else {
                WearPreWorkoutSelectionHubScreen(
                    planTitle = uiState.activePlanTitle,
                    currentDay = uiState.currentDay,
                    exercises = uiState.availableExercises,
                    onSelectExercise = { exercise, index ->
                        viewModel.selectExercise(exercise, index)
                    },
                    onStartSession = {
                        viewModel.startSession()
                    },
                )
            }
        }
    }
}
