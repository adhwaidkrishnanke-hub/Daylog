package com.eadiat.daylog.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DayTaskRepository(
    private val taskDao: DayTaskDao,
    private val statsDao: DayStatsDao,
    private val templateDao: MonthTemplateDao,
    private val goalDao: MonthGoalDao
) {

    // Tasks
    suspend fun getTasksForDate(date: String): List<DayTask> =
        withContext(Dispatchers.IO) { taskDao.getTasksForDate(date) }

    suspend fun insertTask(task: DayTask) =
        withContext(Dispatchers.IO) { taskDao.insertTask(task) }

    suspend fun updateTask(task: DayTask) =
        withContext(Dispatchers.IO) { taskDao.updateTask(task) }

    suspend fun deleteTask(task: DayTask) =
        withContext(Dispatchers.IO) { taskDao.deleteTask(task) }

    // Stats
    suspend fun getStatsForDate(date: String): DayStats? =
        withContext(Dispatchers.IO) { statsDao.getStatsForDate(date) }

    suspend fun updateStats(stats: DayStats) =
        withContext(Dispatchers.IO) { statsDao.insertOrUpdate(stats) }

    suspend fun getStatsForMonth(monthPrefix: String): List<DayStats> =
        withContext(Dispatchers.IO) { statsDao.getStatsForMonth(monthPrefix) }

    // Monthly data
    suspend fun getCompletedDatesForMonth(monthPrefix: String): List<String> =
        withContext(Dispatchers.IO) { taskDao.getCompletedDatesForMonth(monthPrefix) }

    suspend fun getDatesWithTasksForMonth(monthPrefix: String): List<String> =
        withContext(Dispatchers.IO) { taskDao.getDatesWithTasksForMonth(monthPrefix) }

    // Templates
    suspend fun getTemplateForMonth(monthYear: String): List<MonthTemplate> =
        withContext(Dispatchers.IO) { templateDao.getTemplateForMonth(monthYear) }

    suspend fun insertTemplate(template: MonthTemplate) =
        withContext(Dispatchers.IO) { templateDao.insertTemplate(template) }

    suspend fun deleteTemplate(template: MonthTemplate) =
        withContext(Dispatchers.IO) { templateDao.deleteTemplate(template) }

    suspend fun clearMonth(monthYear: String) =
        withContext(Dispatchers.IO) { templateDao.clearMonth(monthYear) }

    suspend fun applyTemplateToDate(monthYear: String, date: String) =
        withContext(Dispatchers.IO) {
            val existing = taskDao.getTasksForDate(date)
            if (existing.isEmpty()) {
                val templates = templateDao.getTemplateForMonth(monthYear)
                templates.forEachIndexed { index, template ->
                    taskDao.insertTask(
                        DayTask(
                            date = date,
                            title = template.title,
                            order = index,
                            isFromTemplate = true
                        )
                    )
                }
            }
        }

    // Goals
    suspend fun getGoalForMonth(monthYear: String): MonthGoal? =
        withContext(Dispatchers.IO) { goalDao.getGoalForMonth(monthYear) }

    suspend fun saveGoal(goal: MonthGoal) =
        withContext(Dispatchers.IO) { goalDao.insertOrUpdate(goal) }

    suspend fun deleteGoal(goal: MonthGoal) =
        withContext(Dispatchers.IO) { goalDao.deleteGoal(goal) }

    suspend fun updateGoalProgress(monthYear: String, progress: Int) =
        withContext(Dispatchers.IO) {
            val existing = goalDao.getGoalForMonth(monthYear)
            existing?.let { goalDao.insertOrUpdate(it.copy(current = progress)) }
        }
}