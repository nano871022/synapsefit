package co.japl.android.synapsefit.wear.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import co.japl.android.synapsefit.wear.ui.viewmodel.WearActiveWorkoutViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WearActiveWorkoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            WearActiveWorkoutScreen(
                uiState = uiState,
                onIncrementReps = { viewModel.incrementReps() },
                onDecrementReps = { viewModel.decrementReps() }
            )
        }
    }
}
