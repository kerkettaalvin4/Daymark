package dev.daymark.todo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.daymark.todo.data.DaymarkDatabase
import dev.daymark.todo.data.Priority
import dev.daymark.todo.data.Task
import dev.daymark.todo.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter { ALL, TODAY, UPCOMING, COMPLETED }

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TaskRepository(DaymarkDatabase.get(application).taskDao())
    val tasks = repository.tasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(title: String, note: String, priority: Priority, dueDate: Long?) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        viewModelScope.launch { repository.add(Task(title = cleanTitle, note = note.trim(), priority = priority, dueDate = dueDate)) }
    }

    fun toggle(task: Task) = viewModelScope.launch { repository.update(task.copy(isCompleted = !task.isCompleted)) }
    fun delete(task: Task) = viewModelScope.launch { repository.delete(task) }
    fun clearCompleted() = viewModelScope.launch { repository.clearCompleted() }
}

