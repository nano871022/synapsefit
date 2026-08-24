@file:Suppress("FunctionNaming")

package co.japl.android.synapsefit.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val KineticDarkColorScheme =
    darkColorScheme(
        primary = PrimaryCyan,
        onPrimary = OnPrimaryDark,
        primaryContainer = OnPrimaryDark,
        onPrimaryContainer = PrimaryCyan,
        secondary = PrimaryFixedDim,
        onSecondary = OnPrimaryDark,
        background = BackgroundDark,
        onBackground = PrimaryCyan,
        surface = SurfaceContainer,
        onSurface = MetricCardBackground,
        surfaceVariant = SurfaceContainerHigh,
        onSurfaceVariant = OnMetricCardText,
        surfaceContainerLow = SurfaceContainerLow,
        surfaceContainer = SurfaceContainer,
        surfaceContainerHigh = SurfaceContainerHigh,
        surfaceContainerHighest = SurfaceContainerHighest,
        outline = OutlineDark,
        outlineVariant = OutlineVariantDark,
        error = ErrorDark,
        errorContainer = ErrorContainerDark,
        onErrorContainer = OnErrorContainerDark,
    )

@Composable
fun SynapseFitTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
    ) {
        MaterialTheme(
            colorScheme = KineticDarkColorScheme,
            typography = KineticTypography,
            content = content,
        )
    }
}

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current
