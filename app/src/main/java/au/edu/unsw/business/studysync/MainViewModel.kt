package au.edu.unsw.business.studysync

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.*
import au.edu.unsw.business.studysync.constants.Constants.GROUP_UNASSIGNED
import au.edu.unsw.business.studysync.constants.Environment.BASELINE_START_DATE
import au.edu.unsw.business.studysync.constants.Environment.BASELINE_START_DATE_STRING
import au.edu.unsw.business.studysync.database.AppDatabase
import au.edu.unsw.business.studysync.database.DbAppReport
import au.edu.unsw.business.studysync.database.DbReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MainViewModel(
    private val preferences: SharedPreferences,
    private val database: AppDatabase
): ViewModel() {
    private val _identified = MutableLiveData<Boolean>()
    val identified get(): LiveData<Boolean> = _identified

    private val _subjectId = MutableLiveData<String?>()
    val subjectId get(): LiveData<String?> = _subjectId

    private val _authToken = MutableLiveData<String?>()
    val authToken get(): LiveData<String?> = _authToken

    private val _lastRecorded = MutableLiveData<LocalDate>()
    val lastRecorded get(): LiveData<LocalDate> = _lastRecorded

    private val _group = MutableLiveData<Int>()
    val group get(): LiveData<Int> = _group

    private val _treatmentDebriefed = MutableLiveData<Boolean>()
    val treatmentDebriefed get(): LiveData<Boolean> = _treatmentDebriefed

    // Seconds
    private val _treatmentLimit = MutableLiveData<Int>()
    val treatmentLimit get(): LiveData<Int> = _treatmentLimit

    private val reportDao = database.reportDao()

    init {
        _identified.value = preferences.getBoolean("identified", false)
        _subjectId.value = preferences.getString("subject-id", null)
        _authToken.value = preferences.getString("auth-token", null)
        _lastRecorded.value = LocalDate.parse(preferences.getString("last-recorded", BASELINE_START_DATE_STRING))
        _group.value = preferences.getInt("group", GROUP_UNASSIGNED)
        _treatmentDebriefed.value = preferences.getBoolean("treatment-debriefed", false)
        _treatmentLimit.value = preferences.getInt("treatment-limit", 0)
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
                clear()
                commit()
            }

            _identified.value = false
            _subjectId.value = null
            _authToken.value = null
            _lastRecorded.value = BASELINE_START_DATE
            _group.value = GROUP_UNASSIGNED

            withContext(Dispatchers.IO) {
                database.clearAllTables()
            }
        }
    }

    suspend fun insertMultipleDayReports(reports: List<DbReport>, appReports: List<DbAppReport>) {
        reportDao.insertMultipleDayReports(reports, appReports)
    }

    fun setLastRecorded(date: LocalDate) {
        preferences.edit {
            putString("last-recorded", date.toString())
        }

        _lastRecorded.value = date
    }

    suspend fun getAllAppReports(): List<DbAppReport> {
        return reportDao.getAllAppReports()
    }

    suspend fun getUnsyncedAppReports(): List<DbAppReport> {
        return reportDao.getUnsyncedAppReports()
    }

    suspend fun markReportSynced(period: String, day: Int) {
        reportDao.markReportSynced(period, day)
    }
}