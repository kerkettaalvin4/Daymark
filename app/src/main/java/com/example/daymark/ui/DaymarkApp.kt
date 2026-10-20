package dev.daymark.todo.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.daymark.todo.TaskFilter
import dev.daymark.todo.TaskViewModel
import dev.daymark.todo.data.Priority
import dev.daymark.todo.data.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val TodayTint = Color(0xFFE9EDE4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaymarkApp(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    var filter by remember { mutableStateOf(TaskFilter.ALL) }
    var showComposer by remember { mutableStateOf(false) }
    val todayStart = remember { Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis }
    val tomorrowStart = todayStart + 24 * 60 * 60 * 1000L
    val remaining = tasks.count { !it.isCompleted }
    val visible = when (filter) {
        TaskFilter.ALL -> tasks
        TaskFilter.TODAY -> tasks.filter { !it.isCompleted && (it.dueDate == null || (it.dueDate >= todayStart && it.dueDate < tomorrowStart)) }
        TaskFilter.UPCOMING -> tasks.filter { !it.isCompleted && it.dueDate != null && it.dueDate >= tomorrowStart }
        TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBar(title = { Text("daymark", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showComposer = true }, containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Text(greeting(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Text("Make room for\nwhat matters.", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold, lineHeight = MaterialTheme.typography.headlineLarge.lineHeight)
            Spacer(Modifier.height(18.dp))
            ProgressCard(remaining, tasks.size)
            Spacer(Modifier.height(23.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Your list", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                if (tasks.any { it.isCompleted }) TextButton(onClick = { viewModel.clearCompleted() }) { Text("Clear done") }
            }
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f).padding(bottom = 12.dp)) {
                item {
                    FilterRow(filter, onFilter = { filter = it })
                    Spacer(Modifier.height(8.dp))
                }
                if (visible.isEmpty()) item { EmptyState(filter, onAdd = { showComposer = true }) }
                items(visible, key = { it.id }) { task -> TaskCard(task, onToggle = { viewModel.toggle(task) }, onDelete = { viewModel.delete(task) }) }
                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }
    if (showComposer) AddTaskDialog(onDismiss = { showComposer = false }, onSave = { title, note, priority, date -> viewModel.add(title, note, priority, date); showComposer = false })
}

@Composable
private fun ProgressCard(remaining: Int, total: Int) {
    val completed = (total - remaining).coerceAtLeast(0)
    Card(colors = CardDefaults.cardColors(containerColor = TodayTint), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(if (remaining == 0 && total > 0) "All caught up" else "$remaining ${if (remaining == 1) "task" else "tasks"} to go", fontWeight = FontWeight.SemiBold)
                Text(if (total == 0) "Start with one small thing" else "$completed of $total completed", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
            Text(if (total == 0) "✳" else "${if (total == 0) 0 else (completed * 100 / total)}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FilterRow(selected: TaskFilter, onFilter: (TaskFilter) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(TaskFilter.ALL to "All", TaskFilter.TODAY to "Today", TaskFilter.UPCOMING to "Upcoming", TaskFilter.COMPLETED to "Done").forEach { (filter, label) ->
            FilterChip(selected = selected == filter, onClick = { onFilter(filter) }, label = { Text(label) })
        }
    }
}

@Composable
private fun TaskCard(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().animateContentSize()) {
        Row(Modifier.fillMaxWidth().padding(start = 14.dp, end = 8.dp, top = 13.dp, bottom = 13.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(26.dp).clip(CircleShape).background(if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant).clickable(onClick = onToggle).semantics { role = Role.Checkbox; selected = task.isCompleted; contentDescription = if (task.isCompleted) "Completed: ${task.title}" else "Mark ${task.title} complete" }, contentAlignment = Alignment.Center) {
                if (task.isCompleted) Icon(Icons.Default.Check, contentDescription = "Mark ${task.title} incomplete", tint = Color.White, modifier = Modifier.size(16.dp))
                else Surface(shape = CircleShape, color = MaterialTheme.colorScheme.outline, modifier = Modifier.size(9.dp)) {}
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis, textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None, color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface)
                if (task.note.isNotBlank()) Text(task.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
                    PriorityPill(task.priority)
                    task.dueDate?.let { Text(formatDate(it), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall) }
                }
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, contentDescription = "Delete ${task.title}", tint = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}

@Composable
private fun PriorityPill(priority: Priority) {
    val color = when (priority) { Priority.HIGH -> Color(0xFFB45040); Priority.MEDIUM -> Color(0xFF9B6948); Priority.LOW -> Color(0xFF657A5C) }
    Text(priority.name.lowercase().replaceFirstChar { it.uppercase() }, color = color, style = MaterialTheme.typography.labelSmall, modifier = Modifier.clip(RoundedCornerShape(50)).background(color.copy(alpha = .10f)).padding(horizontal = 7.dp, vertical = 2.dp))
}

@Composable
private fun EmptyState(filter: TaskFilter, onAdd: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 45.dp, bottom = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("✳", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(10.dp))
        Text(if (filter == TaskFilter.COMPLETED) "Nothing completed just yet" else "A little space to breathe", fontWeight = FontWeight.SemiBold)
        Text(if (filter == TaskFilter.COMPLETED) "Finished tasks will find their way here." else "Add a task when something comes to mind.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        if (filter == TaskFilter.ALL || filter == TaskFilter.TODAY) TextButton(onClick = onAdd) { Text("Add your first task") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(onDismiss: () -> Unit, onSave: (String, String, Priority, Long?) -> Unit) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var menuExpanded by remember { mutableStateOf(false) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("A note (optional)") }, minLines = 2, maxLines = 3, modifier = Modifier.fillMaxWidth())
                Box {
                    AssistChip(onClick = { menuExpanded = true }, label = { Text("${priority.name.lowercase().replaceFirstChar { it.uppercase() }} priority") }, leadingIcon = { PriorityPill(priority) })
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        Priority.entries.forEach { choice -> DropdownMenuItem(text = { Text(choice.name.lowercase().replaceFirstChar { it.uppercase() }) }, onClick = { priority = choice; menuExpanded = false }) }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssistChip(onClick = { showDatePicker = true }, label = { Text(dueDate?.let(::formatDate) ?: "Add due date") }, leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) })
                    if (dueDate != null) IconButton(onClick = { dueDate = null }) { Icon(Icons.Default.Close, contentDescription = "Remove due date") }
                }
            }
        },
        confirmButton = { Button(onClick = { onSave(title, note, priority, dueDate) }, enabled = title.isNotBlank()) { Text("Add task") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
    if (showDatePicker) {
        val dateState = rememberDatePickerState(initialSelectedDateMillis = dueDate?.let(::localDateToUtcMillis) ?: System.currentTimeMillis())
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = { TextButton(onClick = { dueDate = dateState.selectedDateMillis?.let(::utcDateToLocalMillis); showDatePicker = false }) { Text("Set date") } }, dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }) {
            DatePicker(state = dateState, showModeToggle = false)
        }
    }
}

private fun greeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when { hour < 12 -> "Good morning"; hour < 17 -> "Good afternoon"; else -> "Good evening" }
}

private fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    return formatter.format(Date(millis))
}

private fun localDateToUtcMillis(millis: Long): Long {
    val local = Calendar.getInstance().apply { timeInMillis = millis }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

private fun utcDateToLocalMillis(millis: Long): Long {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
    return Calendar.getInstance().apply {
        clear()
        set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

