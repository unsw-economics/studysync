package au.edu.unsw.business.studysync

import android.content.SharedPreferences
import androidx.lifecycle.*
import au.edu.unsw.business.studysync.database.AppDatabase
import au.edu.unsw.business.studysync.database.DbAppReport
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import java.util.*

class TreatmentViewModel(application: StudySyncApplication): AndroidViewModel(application) {

    private val _timeSpentToday = MutableLiveData<Long>()
    val timeSpentToday get(): LiveData<Long> = _timeSpentToday

    private val _valueEarned = MutableLiveData<Int>()
    val valueEarned get(): LiveData<Int> = _valueEarned

    private val _dailyIncentive = MutableLiveData<Double>()
    val dailyIncentive get(): LiveData<Double> = _dailyIncentive

    private val reportDao = application.database.reportDao()

    fun setTimeSpentToday(timeSpentToday: Long) {
        _timeSpentToday.value = timeSpentToday
    }

    init {
        _timeSpentToday.value = 0
        // Yet to implement search for how much they have earned in the treatment period
        _valueEarned.value = 0
        // Assumption for now that preferences is set
        _dailyIncentive.value = application.subjectSettings.treatmentGroup.value!! * 0.5
    }

    fun clearData() {
        viewModelScope.launch {
            _timeSpentToday.value = 0
            _valueEarned.value = 0
            _dailyIncentive.value = 0.0
        }
    }

    suspend fun getAllAppReports(): List<DbAppReport> {
        return reportDao.getAllAppReports()
    }
}