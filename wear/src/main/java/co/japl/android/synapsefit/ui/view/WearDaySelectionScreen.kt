package co.japl.android.synapsefit.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TitleCard
import co.com.japl.ui.theme.BackgroundDark
import co.com.japl.ui.theme.MaterialThemeComposeUI
import co.com.japl.ui.theme.OnPrimaryDark
import co.com.japl.ui.theme.OnSurfaceDark
import co.com.japl.ui.theme.PrimaryCyan
import co.com.japl.ui.theme.SurfaceContainerHigh
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.core.domain.model.WorkoutSessionItem

@Suppress("LongParameterList")
@Composable
fun WearDaySelectionScreen(
    sessions: List<WorkoutSessionItem>,
    onSelectSession: (planId: String, day: Int) -> Unit,
    modifier: Modifier = Modifier,
    checkingUpdate: Boolean = false,
    updateMessageResId: Int = R.string.wear_check_update,
    isUpdateAvailable: Boolean = false,
    onCheckUpdate: () -> Unit = {},
    onPerformUpdate: () -> Unit = {},
) {
    val listState = rememberScalingLazyListState()

    Scaffold(
        modifier =
            modifier
                .fillMaxSize()
                .background(BackgroundDark),
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) },
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text(
                    text = stringResource(R.string.wear_select_session_title),
                    style = MaterialTheme.typography.caption1,
                    color = OnSurfaceDark.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }

            if (sessions.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.wear_no_sessions),
                        style = MaterialTheme.typography.body2,
                        color = OnSurfaceDark,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                items(sessions) { sessionItem ->
                    SessionCardItem(
                        sessionItem = sessionItem,
                        onClick = { onSelectSession(sessionItem.planId, sessionItem.day) },
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.size(8.dp))
            }

            item {
                Chip(
                    onClick = {
                        if (isUpdateAvailable) {
                            onPerformUpdate()
                        } else {
                            onCheckUpdate()
                        }
                    },
                    enabled = !checkingUpdate,
                    label = {
                        Text(
                            text = stringResource(updateMessageResId),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    },
                    colors =
                        if (isUpdateAvailable) {
                            ChipDefaults.chipColors(
                                backgroundColor = PrimaryCyan,
                                contentColor = OnPrimaryDark,
                                iconColor = OnPrimaryDark,
                            )
                        } else {
                            ChipDefaults.chipColors(
                                backgroundColor = SurfaceContainerHigh,
                                contentColor = OnSurfaceDark,
                                iconColor = OnSurfaceDark,
                            )
                        },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun SessionCardItem(
    sessionItem: WorkoutSessionItem,
    onClick: () -> Unit,
) {
    if (sessionItem.isTodayScheduled) {
        TitleCard(
            onClick = onClick,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = OnPrimaryDark,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = sessionItem.planTitle,
                        fontWeight = FontWeight.Bold,
                        color = OnPrimaryDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            },
            time = {
                Text(
                    text = stringResource(R.string.wear_today_tag),
                    style = MaterialTheme.typography.caption2,
                    fontWeight = FontWeight.Bold,
                    color = OnPrimaryDark,
                )
            },
            backgroundPainter =
                CardDefaults.cardBackgroundPainter(
                    startBackgroundColor = PrimaryCyan,
                    endBackgroundColor = PrimaryCyan,
                ),
            contentColor = OnPrimaryDark,
            titleColor = OnPrimaryDark,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Column {
                Text(
                    text =
                        stringResource(
                            R.string.wear_day_title_with_exercises,
                            sessionItem.day,
                            sessionItem.exerciseCount,
                        ),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.SemiBold,
                    color = OnPrimaryDark,
                )
            }
        }
    } else {
        TitleCard(
            onClick = onClick,
            title = {
                Text(
                    text = sessionItem.planTitle,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            backgroundPainter =
                CardDefaults.cardBackgroundPainter(
                    startBackgroundColor = SurfaceContainerHigh,
                    endBackgroundColor = SurfaceContainerHigh,
                ),
            contentColor = OnSurfaceDark,
            titleColor = OnSurfaceDark,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 3.dp),
        ) {
            Text(
                text =
                    stringResource(
                        R.string.wear_day_title_with_exercises,
                        sessionItem.day,
                        sessionItem.exerciseCount,
                    ),
                style = MaterialTheme.typography.caption1,
                color = OnSurfaceDark.copy(alpha = 0.8f),
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
internal fun WearDaySelectionScreenPreview() {
    MaterialThemeComposeUI {
        WearDaySelectionScreen(
            sessions = emptyList(),
            onSelectSession = { plan, id -> },
            modifier = Modifier,
            checkingUpdate = true,
            updateMessageResId = R.string.wear_check_update,
            isUpdateAvailable = true,
            onCheckUpdate = { },
            onPerformUpdate = { },
        )
    }
}
