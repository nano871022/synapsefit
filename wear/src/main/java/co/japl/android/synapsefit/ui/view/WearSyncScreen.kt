@file:Suppress("MagicNumber")

package co.japl.android.synapsefit.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.core.domain.model.SyncStepState
import co.japl.android.synapsefit.ui.viewmodel.WearSyncUiState

@Composable
fun WearSyncScreen(
    uiState: WearSyncUiState,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stepDescription =
        when (val step = uiState.stepState) {
            is SyncStepState.UploadingLogs -> {
                if (step.total <= 0) {
                    stringResource(id = R.string.wear_sync_step4_no_logs)
                } else {
                    stringResource(id = R.string.wear_sync_step4_uploading_logs, step.current, step.total)
                }
            }
            else -> stringResource(id = uiState.stepDescriptionResId)
        }

    Scaffold(
        timeText = { TimeText() },
        modifier = modifier.fillMaxSize().background(MaterialTheme.colors.background),
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.isDisconnected) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(id = R.string.wear_sync_disconnected_prompt),
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Chip(
                        onClick = onCancel,
                        colors =
                            ChipDefaults.primaryChipColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = MaterialTheme.colors.onPrimary,
                            ),
                        label = {
                            Text(
                                text = stringResource(id = R.string.wear_sync_cancel),
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        modifier = Modifier.fillMaxWidth(0.85f),
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(72.dp),
                    ) {
                        CircularProgressIndicator(
                            progress = uiState.progress,
                            modifier = Modifier.fillMaxSize(),
                            indicatorColor = MaterialTheme.colors.primary,
                            trackColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
                            strokeWidth = 4.dp,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stepDescription,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
internal fun WearSyncScreenPreview() {
    MaterialThemeComposeUI {
        WearSyncScreen(
            uiState =
                WearSyncUiState(
                    stepState = SyncStepState.UploadingLogs(1, 3),
                ),
            onCancel = {},
        )
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
internal fun WearSyncScreenDisconnectedPreview() {
    MaterialThemeComposeUI {
        WearSyncScreen(
            uiState =
                WearSyncUiState(
                    isDisconnected = true,
                    stepState = SyncStepState.Disconnected(SyncStepState.DownloadingPlans),
                ),
            onCancel = {},
        )
    }
}
