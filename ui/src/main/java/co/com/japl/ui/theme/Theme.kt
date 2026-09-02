@file:Suppress("FunctionNaming")

package co.com.japl.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val KineticDarkColorScheme =
    darkColorScheme(
        primary = PrimaryCyan,
        onPrimary = OnPrimaryDark,
        primaryContainer = PrimaryContainerCyan,
        onPrimaryContainer = OnPrimaryContainerDark,
        inversePrimary = InversePrimaryDark,
        secondary = SecondaryGray,
        onSecondary = OnSecondaryDark,
        secondaryContainer = SecondaryContainerDark,
        onSecondaryContainer = OnSecondaryContainerDark,
        tertiary = TertiaryLight,
        onTertiary = OnTertiaryDark,
        tertiaryContainer = TertiaryContainerLight,
        onTertiaryContainer = OnTertiaryContainerDark,
        background = BackgroundDark,
        onBackground = OnBackgroundDark,
        surface = SurfaceDark,
        onSurface = OnSurfaceDark,
        surfaceVariant = SurfaceVariantDark,
        onSurfaceVariant = OnSurfaceVariantDark,
        surfaceTint = SurfaceTintCyan,
        inverseSurface = InverseSurfaceDark,
        inverseOnSurface = InverseOnSurfaceDark,
        surfaceContainerLowest = SurfaceContainerLowest,
        surfaceContainerLow = SurfaceContainerLow,
        surfaceContainer = SurfaceContainer,
        surfaceContainerHigh = SurfaceContainerHigh,
        surfaceContainerHighest = SurfaceContainerHighest,
        outline = OutlineDark,
        outlineVariant = OutlineVariantDark,
        error = ErrorDark,
        onError = OnErrorDark,
        errorContainer = ErrorContainerDark,
        onErrorContainer = OnErrorContainerDark,
    )

@Composable
fun MaterialThemeComposeUI(content: @Composable () -> Unit) {
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
