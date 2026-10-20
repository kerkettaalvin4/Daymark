package dev.daymark.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dev.daymark.todo.ui.DaymarkApp
import dev.daymark.todo.ui.theme.DaymarkTheme

class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DaymarkTheme {
                DaymarkApp(viewModel)
            }
        }
    }
}

