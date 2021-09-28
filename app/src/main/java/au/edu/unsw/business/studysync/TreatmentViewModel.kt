package au.edu.unsw.business.studysync

import android.content.SharedPreferences
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.edit
import androidx.lifecycle.*
import au.edu.unsw.business.studysync.constants.Environment
import au.edu.unsw.business.studysync.constants.Environment.BASELINE_START_DATE
import au.edu.unsw.business.studysync.database.DailyReport
import au.edu.unsw.business.studysync.database.DailyReportDao
import au.edu.unsw.business.studysync.network.SyncApi
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.*

class TreatmentViewModel(
    private val preferences: SharedPreferences,
    private val dailyReportDao: DailyReportDao
): ViewModel() {
    private val _timeSpentToday = MutableLiveData<Long>()
    val timeSpentToday get(): LiveData<Long> = _timeSpentToday

    private val _valueEarned = MutableLiveData<Int>()
    val valueEarned get(): LiveData<Int> = _valueEarned

    private val _dailyIncentive = MutableLiveData<Double>()
    val dailyIncentive get(): LiveData<Double> = _dailyIncentive

    fun setTimeSpentToday(timeSpentToday: Long) {
        _timeSpentToday.value = timeSpentToday
    }

    init {
        _timeSpentToday.value = 0
        _valueEarned.value = 0
        // Assumption for now that preferences is set
        _dailyIncentive.value = preferences.getInt("treatment-group", 0) * 0.5
    }

    fun clearData() {
        viewModelScope.launch {
            _timeSpentToday.value = 0
            _valueEarned.value = 0
            _dailyIncentive.value = 0.0
        }
    }

    suspend fun getRecordedReports(): List<DailyReport> {
        return dailyReportDao.getRecordedReports()
    }
}