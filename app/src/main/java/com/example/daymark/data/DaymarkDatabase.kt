package dev.daymark.todo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class PriorityConverter {
    @TypeConverter fun fromPriority(priority: Priority): String = priority.name
    @TypeConverter fun toPriority(value: String): Priority = Priority.valueOf(value)
}

@Database(entities = [Task::class], version = 1, exportSchema = false)
@TypeConverters(PriorityConverter::class)
abstract class DaymarkDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile private var instance: DaymarkDatabase? = null
        fun get(context: Context): DaymarkDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, DaymarkDatabase::class.java, "daymark.db")
                .build().also { instance = it }
        }
    }
}

