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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TitleCard
import co.com.japl.ui.theme.BackgroundDark
import co.com.japl.ui.theme.OnPrimaryDark
import co.com.japl.ui.theme.OnSurfaceDark
import co.com.japl.ui.theme.PrimaryCyan
import co.com.japl.ui.theme.SurfaceContainerHigh
import co.japl.android.synapsefit.R
import co.japl.android.synapsefit.core.domain.model.WorkoutSessionItem

@Composable
fun WearDaySelectionScreen(
    sessions: List<WorkoutSessionItem>,
    onSelectSession: (planId: String, day: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberScalingLazyListState()

    Scaffold(
        modifier = modifier.fillMaxSize().background(BackgroundDark),
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
