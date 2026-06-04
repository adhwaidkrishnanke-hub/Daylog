package com.eadiat.daylog.data

import androidx.room.*

@Entity(tableName = "day_tasks")
data class DayTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val title: String,
    val isDone: Boolean = false,
    val order: Int = 0,
    val isFromTemplate: Boolean = false
)

@Dao
interface DayTaskDao {
    @Query("SELECT * FROM day_tasks WHERE date = :date ORDER BY `order`")
    suspend fun getTasksForDate(date: String): List<DayTask>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: DayTask)

    @Update
    suspend fun updateTask(task: DayTask)

    @Delete
    suspend fun deleteTask(task: DayTask)

    @Query("SELECT * FROM day_tasks WHERE date = :date AND isDone = 0")
    suspend fun getUnfinishedTasks(date: String): List<DayTask>

    @Query("SELECT DISTINCT date FROM day_tasks WHERE date LIKE :monthPrefix || '%'")
    suspend fun getDatesWithTasksForMonth(monthPrefix: String): List<String>

    @Query("SELECT date FROM day_tasks WHERE date LIKE :monthPrefix || '%' GROUP BY date HAVING SUM(CASE WHEN isDone = 0 THEN 1 ELSE 0 END) = 0 AND COUNT(*) > 0")
    suspend fun getCompletedDatesForMonth(monthPrefix: String): List<String>
}

@Entity(tableName = "day_stats")
data class DayStats(
    @PrimaryKey val date: String,
    val sleepTime: String = "",
    val wakeTime: String = "",
    val mood: Int = 0,
    val notes: String = ""
)

@Dao
interface DayStatsDao {
    @Query("SELECT * FROM day_stats WHERE date = :date")
    suspend fun getStatsForDate(date: String): DayStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: DayStats)

    @Query("SELECT * FROM day_stats WHERE date LIKE :monthPrefix || '%'")
    suspend fun getStatsForMonth(monthPrefix: String): List<DayStats>
}

@Entity(tableName = "month_template")
data class MonthTemplate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monthYear: String,
    val title: String,
    val order: Int = 0
)

@Dao
interface MonthTemplateDao {
    @Query("SELECT * FROM month_template WHERE monthYear = :monthYear ORDER BY `order`")
    suspend fun getTemplateForMonth(monthYear: String): List<MonthTemplate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: MonthTemplate)

    @Delete
    suspend fun deleteTemplate(template: MonthTemplate)

    @Query("DELETE FROM month_template WHERE monthYear = :monthYear")
    suspend fun clearMonth(monthYear: String)
}

@Entity(tableName = "month_goal")
data class MonthGoal(
    @PrimaryKey val monthYear: String,
    val title: String = "",
    val target: Int = 0,
    val current: Int = 0,
    val unit: String = ""
)

@Dao
interface MonthGoalDao {
    @Query("SELECT * FROM month_goal WHERE monthYear = :monthYear")
    suspend fun getGoalForMonth(monthYear: String): MonthGoal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(goal: MonthGoal)

    @Delete
    suspend fun deleteGoal(goal: MonthGoal)
}

@Database(entities = [DayTask::class, DayStats::class, MonthTemplate::class, MonthGoal::class], version = 5)
abstract class DaylogDatabase : RoomDatabase() {
    abstract fun dayTaskDao(): DayTaskDao
    abstract fun dayStatsDao(): DayStatsDao
    abstract fun monthTemplateDao(): MonthTemplateDao
    abstract fun monthGoalDao(): MonthGoalDao

    companion object {
        @Volatile private var INSTANCE: DaylogDatabase? = null

        fun getDatabase(context: android.content.Context): DaylogDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    DaylogDatabase::class.java,
                    "daylog_database"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}