package co.japl.android.synapsefit.ui.util

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RotaryScrollAdapter(
    private val scrollableState: ScrollableState,
    private val coroutineScope: CoroutineScope,
) {
    fun handleScroll(pixels: Float): Boolean {
        coroutineScope.launch {
            scrollableState.scrollBy(pixels)
        }
        return true
    }
}

@Composable
fun rememberRotaryScrollAdapter(scrollableState: ScrollableState): RotaryScrollAdapter {
    val scope = rememberCoroutineScope()
    return remember(scrollableState, scope) {
        RotaryScrollAdapter(scrollableState, scope)
    }
}

fun Modifier.rotaryScrollable(
    focusRequester: FocusRequester,
    adapter: RotaryScrollAdapter,
): Modifier =
    this
        .focusRequester(focusRequester)
        .focusable()
        .onRotaryScrollEvent { event ->
            adapter.handleScroll(event.verticalScrollPixels)
        }

fun Modifier.rotaryScrollable(
    focusRequester: FocusRequester,
    listState: ScalingLazyListState,
    coroutineScope: CoroutineScope,
): Modifier =
    this
        .focusRequester(focusRequester)
        .focusable()
        .onRotaryScrollEvent { event ->
            coroutineScope.launch {
                listState.scrollBy(event.verticalScrollPixels)
            }
            true
        }
