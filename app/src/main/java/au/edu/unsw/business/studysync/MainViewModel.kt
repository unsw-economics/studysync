package au.edu.unsw.business.studysync

import androidx.core.content.edit
import androidx.lifecycle.*
import au.edu.unsw.business.studysync.constants.Constants.GROUP_UNASSIGNED
import au.edu.unsw.business.studysync.constants.Environment.BASELINE_START_DATE
import au.edu.unsw.business.studysync.constants.Environment.BASELINE_START_DATE_STRING
import au.edu.unsw.business.studysync.database.DbAppReport
import au.edu.unsw.business.studysync.database.DbReport
import au.edu.unsw.business.studysync.usage.UsageStatsNegotiator
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class MainViewModel(application: StudySyncApplication): AndroidViewModel(application) {

    private val _navigateEvents: PublishRelay<Unit> = PublishRelay.create()
    val navigateEvents get(): Observable<Unit> = _navigateEvents

    private val _usageAccessEnabled = MutableLiveData<Boolean>()
    val usageAccessEnabled get(): LiveData<Boolean> = _usageAccessEnabled

    private val database = application.database
    private val reportDao = database.reportDao()

    val subjectSettings = application.subjectSettings

    init {
        _usageAccessEnabled.value = UsageStatsNegotiator.hasUsageStatsPermission(application)
    }

    fun setUsageAccessEnabled(isEnabled: Boolean) {
        _usageAccessEnabled.value = isEnabled
    }

    fun identify(subjectId: String, authToken: String) {
        subjectSettings.identify(subjectId, authToken)
        _navigateEvents.accept(Unit)
    }

    fun clearData() {
        viewModelScope.launch {
            subjectSettings.clearData()

            withContext(Dispatchers.IO) {
                database.clearAllTables()
            }

            _navigateEvents.accept(Unit)
        }
    }

    fun setLastRecorded(date: LocalDate) {
        subjectSettings.setLastRecorded(date)
    }

    fun completeDebrief() {
        subjectSettings.completeDebrief()
        _navigateEvents.accept(Unit)
    }

    suspend fun insertMultipleDayReports(reports: List<DbReport>, appReports: List<DbAppReport>) {
        reportDao.insertMultipleDayReports(reports, appReports)
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