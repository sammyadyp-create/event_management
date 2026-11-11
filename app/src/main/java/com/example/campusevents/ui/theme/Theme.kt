package com.example.campusevents.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CampusEventsLightColors = lightColorScheme(
    primary = Navy,
    onPrimary = White,
    primaryContainer = PurpleLight,
    onPrimaryContainer = Navy,
    secondary = Purple,
    onSecondary = White,
    secondaryContainer = PurpleLight,
    onSecondaryContainer = Navy,
    background = PageBackground,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = BorderLight,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = White
)

@Composable
fun CampusEventsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CampusEventsLightColors,
        typography = MaterialTheme.typography,
        content = content
    )
}
