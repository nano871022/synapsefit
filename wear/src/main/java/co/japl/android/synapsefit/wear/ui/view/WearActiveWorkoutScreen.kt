package co.japl.android.synapsefit.wear.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.CurvedLayout
import androidx.wear.compose.foundation.curvedRow
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.curvedText
import co.com.japl.ui.theme.BackgroundDark
import co.com.japl.ui.theme.ErrorContainerDark
import co.com.japl.ui.theme.OnPrimaryDark
import co.com.japl.ui.theme.OnSurfaceDark
import co.com.japl.ui.theme.PrimaryCyan
import co.com.japl.ui.theme.SurfaceContainerHigh
import co.japl.android.synapsefit.wear.R
import co.japl.android.synapsefit.wear.ui.viewmodel.WearActiveWorkoutUiState

@Suppress("LongMethod")
@Composable
fun WearActiveWorkoutScreen(
    uiState: WearActiveWorkoutUiState,
    onIncrementReps: () -> Unit,
    onDecrementReps: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(BackgroundDark),
        contentAlignment = Alignment.Center,
    ) {
        val exerciseTitle =
            uiState.exerciseName.ifEmpty {
                stringResource(R.string.wear_default_exercise)
            }
        CurvedLayout(
            modifier = Modifier.fillMaxSize(),
        ) {
            curvedRow {
                curvedText(
                    text = exerciseTitle,
                    style =
                        androidx.wear.compose.foundation.CurvedTextStyle(
                            color = PrimaryCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                )
            }
        }

        CentralWorkoutContent(
            uiState = uiState,
            onIncrementReps = onIncrementReps,
            onDecrementReps = onDecrementReps,
        )
    }
}

@Composable
private fun CentralWorkoutContent(
    uiState: WearActiveWorkoutUiState,
    onIncrementReps: () -> Unit,
    onDecrementReps: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "${uiState.currentHeartRateBpm}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ErrorContainerDark,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.wear_bpm_unit),
                fontSize = 12.sp,
                color = OnSurfaceDark,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.wear_reps_label),
            fontSize = 12.sp,
            color = OnSurfaceDark,
        )
        Text(
            text = "${uiState.currentReps}",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryCyan,
        )

        Spacer(modifier = Modifier.height(4.dp))

        RepControlButtons(
            onIncrementReps = onIncrementReps,
            onDecrementReps = onDecrementReps,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text =
                if (uiState.isSyncedWithPhone) {
                    stringResource(R.string.wear_synced)
                } else {
                    stringResource(R.string.wear_not_synced)
                },
            fontSize = 10.sp,
            color = if (uiState.isSyncedWithPhone) PrimaryCyan else OnSurfaceDark,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RepControlButtons(
    onIncrementReps: () -> Unit,
    onDecrementReps: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = onDecrementReps,
            modifier = Modifier.size(36.dp),
            colors =
                ButtonDefaults.buttonColors(
                    backgroundColor = SurfaceContainerHigh,
                    contentColor = OnSurfaceDark,
                ),
            shape = CircleShape,
        ) {
            Text(
                text = stringResource(R.string.wear_dec_reps),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = onIncrementReps,
            modifier = Modifier.size(36.dp),
            colors =
                ButtonDefaults.buttonColors(
                    backgroundColor = PrimaryCyan,
                    contentColor = OnPrimaryDark,
                ),
            shape = CircleShape,
        ) {
            Text(
                text = stringResource(R.string.wear_inc_reps),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
