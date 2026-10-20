package dev.daymark.todo.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {
    val tasks: Flow<List<Task>> = dao.observeTasks()
    suspend fun add(task: Task) = dao.insert(task)
    suspend fun update(task: Task) = dao.update(task)
    suspend fun delete(task: Task) = dao.delete(task)
    suspend fun clearCompleted() = dao.deleteCompleted()
}

