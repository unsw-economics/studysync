package au.edu.unsw.business.studysync

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.*
import au.edu.unsw.business.studysync.constants.Environment.BASELINE_START_DATE
import au.edu.unsw.business.studysync.constants.Environment.BASELINE_START_DATE_STRING
import au.edu.unsw.business.studysync.constants.Environment.TREATMENT_START_DATE_STRING
import au.edu.unsw.business.studysync.constants.Environment.ZONE_ID
import au.edu.unsw.business.studysync.database.DailyReport
import au.edu.unsw.business.studysync.database.DailyReportDao
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.*

class MainViewModel(
    private val preferences: SharedPreferences,
    private val dailyReportDao: DailyReportDao
): ViewModel() {
    private val _identified = MutableLiveData<Boolean>()
    val identified get(): LiveData<Boolean> = _identified

    private val _subjectId = MutableLiveData<String?>()
    val subjectId get(): LiveData<String?> = _subjectId

    private val _authToken = MutableLiveData<String?>()
    val authToken get(): LiveData<String?> = _authToken

    private val _lastRecorded = MutableLiveData<LocalDate>()
    val lastRecorded get(): LiveData<LocalDate> = _lastRecorded

    init {
        _identified.value = preferences.getBoolean("identified", false)
        _subjectId.value = preferences.getString("subject-id", null)
        _authToken.value = preferences.getString("auth-token", null)
        _lastRecorded.value = LocalDate.parse(preferences.getString("last-recorded", BASELINE_START_DATE_STRING))
    }

    fun identify(subjectId: String, authToken: String) {
        viewModelScope.launch {
            preferences.edit {
                putBoolean("identified", true)
                putString("subject-id", subjectId)
                putString("auth-token", authToken)
                commit()
            }

            _identified.value = true
            _subjectId.value = subjectId
            _authToken.value = authToken
        }
    }

    fun clearData() {
        viewModelScope.launch {
            preferences.edit {
                putBoolean("identified", false)
                putString("subject-id", null)
                putString("auth-token", null)
                putString("last-recorded", null)
                commit()
            }

            _identified.value = false
            _subjectId.value = null
            _authToken.value = null
            _lastRecorded.value = BASELINE_START_DATE
        }
    }

    suspend fun getRecordedReports(): List<DailyReport> {
        return dailyReportDao.getRecordedBaselineReports()
    }
}