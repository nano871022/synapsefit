package co.japl.android.synapsefit.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.japl.android.synapsefit.util.DateTimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Locale

@Suppress("MagicNumber")
fun formatElapsedTime(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hrs > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    }
}

@Suppress("MagicNumber")
@Composable
fun rememberElapsedTimeSeconds(startTimestamp: Long?): Long {
    var elapsedSeconds by remember(startTimestamp) {
        mutableStateOf(DateTimeUtils.calculateElapsedTimeSeconds(startTimestamp))
    }
    LaunchedEffect(startTimestamp) {
        if (startTimestamp != null && startTimestamp > 0) {
            while (isActive) {
                elapsedSeconds = DateTimeUtils.calculateElapsedTimeSeconds(startTimestamp)
                delay(1000L)
            }
        } else {
            elapsedSeconds = 0L
        }
    }
    return elapsedSeconds
}
