package com.eadiat.daylog.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DayTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DayTaskRepository
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    private val _tasks = MutableStateFlow<List<DayTask>>(emptyList())
    val tasks: StateFlow<List<DayTask>> = _tasks

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _completedDates = MutableStateFlow<Set<String>>(emptySet())
    val completedDates: StateFlow<Set<String>> = _completedDates

    private val _dayStats = MutableStateFlow<DayStats?>(null)
    val dayStats: StateFlow<DayStats?> = _dayStats

    private val _templateTasks = MutableStateFlow<List<MonthTemplate>>(emptyList())
    val templateTasks: StateFlow<List<MonthTemplate>> = _templateTasks

    private val _monthStats = MutableStateFlow<List<DayStats>>(emptyList())
    val monthStats: StateFlow<List<DayStats>> = _monthStats

    private val _completedDatesMonth = MutableStateFlow<List<String>>(emptyList())
    val completedDatesMonth: StateFlow<List<String>> = _completedDatesMonth

    private val _totalDatesMonth = MutableStateFlow<List<String>>(emptyList())
    val totalDatesMonth: StateFlow<List<String>> = _totalDatesMonth

    private val _monthGoal = MutableStateFlow<MonthGoal?>(null)
    val monthGoal: StateFlow<MonthGoal?> = _monthGoal

    init {
        val db = DaylogDatabase.getDatabase(application)
        repository = DayTaskRepository(
            db.dayTaskDao(),
            db.dayStatsDao(),
            db.monthTemplateDao(),
            db.monthGoalDao()
        )
        loadTasksForDate(LocalDate.now())
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadTasksForDate(date)
    }

    fun loadTasksForDate(date: LocalDate) {
        viewModelScope.launch {
            val dateStr = date.format(formatter)
            val monthStr = date.format(monthFormatter)
            repository.applyTemplateToDate(monthStr, dateStr)
            val result = repository.getTasksForDate(dateStr)
            _tasks.value = result
            _dayStats.value = repository.getStatsForDate(dateStr)
            checkIfDayComplete(dateStr)
        }
    }

    fun addTask(title: String) {
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(formatter)
            val task = DayTask(
                date = dateStr,
                title = title,
                order = _tasks.value.size
            )
            repository.insertTask(task)
            loadTasksForDate(_selectedDate.value)
        }
    }

    fun toggleTask(task: DayTask) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isDone = !task.isDone))
            loadTasksForDate(_selectedDate.value)
        }
    }

    fun deleteTask(task: DayTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
            loadTasksForDate(_selectedDate.value)
        }
    }

    fun editTask(task: DayTask, newTitle: String) {
        viewModelScope.launch {
            repository.updateTask(task.copy(title = newTitle))
            loadTasksForDate(_selectedDate.value)
        }
    }

    fun updateSleepTime(time: String) {
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(formatter)
            val current = _dayStats.value ?: DayStats(date = dateStr)
            repository.updateStats(current.copy(sleepTime = time))
            _dayStats.value = repository.getStatsForDate(dateStr)
        }
    }

    fun updateWakeTime(time: String) {
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(formatter)
            val current = _dayStats.value ?: DayStats(date = dateStr)
            repository.updateStats(current.copy(wakeTime = time))
            _dayStats.value = repository.getStatsForDate(dateStr)
        }
    }

    fun updateMood(mood: Int) {
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(formatter)
            val current = _dayStats.value ?: DayStats(date = dateStr)
            repository.updateStats(current.copy(mood = mood))
            _dayStats.value = repository.getStatsForDate(dateStr)
        }
    }

    fun updateNotes(notes: String) {
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(formatter)
            val current = _dayStats.value ?: DayStats(date = dateStr)
            repository.updateStats(current.copy(notes = notes))
            _dayStats.value = repository.getStatsForDate(dateStr)
        }
    }

    fun loadMonthStats(monthYear: String) {
        viewModelScope.launch {
            _monthStats.value = repository.getStatsForMonth(monthYear)
            _completedDatesMonth.value = repository.getCompletedDatesForMonth(monthYear)
            _totalDatesMonth.value = repository.getDatesWithTasksForMonth(monthYear)
        }
    }

    // Goal functions
    fun loadGoal(monthYear: String) {
        viewModelScope.launch {
            _monthGoal.value = repository.getGoalForMonth(monthYear)
        }
    }

    fun saveGoal(monthYear: String, title: String, target: Int, unit: String) {
        viewModelScope.launch {
            val goal = MonthGoal(
                monthYear = monthYear,
                title = title,
                target = target,
                current = _monthGoal.value?.current ?: 0,
                unit = unit
            )
            repository.saveGoal(goal)
            _monthGoal.value = goal
        }
    }

    fun updateGoalProgress(monthYear: String, progress: Int) {
        viewModelScope.launch {
            repository.updateGoalProgress(monthYear, progress)
            _monthGoal.value = repository.getGoalForMonth(monthYear)
        }
    }

    fun deleteGoal(monthYear: String) {
        viewModelScope.launch {
            _monthGoal.value?.let { repository.deleteGoal(it) }
            _monthGoal.value = null
        }
    }

    // Template functions
    fun loadTemplate(monthYear: String) {
        viewModelScope.launch {
            _templateTasks.value = repository.getTemplateForMonth(monthYear)
        }
    }

    fun addTemplateTask(monthYear: String, title: String) {
        viewModelScope.launch {
            repository.insertTemplate(
                MonthTemplate(
                    monthYear = monthYear,
                    title = title,
                    order = _templateTasks.value.size
                )
            )
            loadTemplate(monthYear)
        }
    }

    fun deleteTemplateTask(template: MonthTemplate) {
        viewModelScope.launch {
            repository.deleteTemplate(template)
            loadTemplate(template.monthYear)
        }
    }

    fun clearMonthTemplate(monthYear: String) {
        viewModelScope.launch {
            repository.clearMonth(monthYear)
            loadTemplate(monthYear)
        }
    }

    private suspend fun checkIfDayComplete(dateStr: String) {
        val all = repository.getTasksForDate(dateStr)
        if (all.isNotEmpty() && all.all { it.isDone }) {
            _completedDates.value = _completedDates.value + dateStr
        }
    }
}