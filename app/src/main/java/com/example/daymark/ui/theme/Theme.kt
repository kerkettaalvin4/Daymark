package dev.daymark.todo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DaymarkColors = lightColorScheme(
    primary = Color(0xFF52694C), onPrimary = Color.White,
    primaryContainer = Color(0xFFE5EBDD), onPrimaryContainer = Color(0xFF283A25),
    secondary = Color(0xFF9B6948), onSecondary = Color.White,
    background = Color(0xFFF7F6F2), onBackground = Color(0xFF252820),
    surface = Color(0xFFFFFEFB), onSurface = Color(0xFF252820),
    surfaceVariant = Color(0xFFF0F0EA), onSurfaceVariant = Color(0xFF72766D),
    outline = Color(0xFFE4E4DC), error = Color(0xFFB45040),
)

@Composable
fun DaymarkTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DaymarkColors, content = content)
}

