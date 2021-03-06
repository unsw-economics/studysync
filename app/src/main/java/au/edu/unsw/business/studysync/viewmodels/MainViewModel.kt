package au.edu.unsw.business.studysync.viewmodels

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import au.edu.unsw.business.studysync.StudySyncApplication
import au.edu.unsw.business.studysync.support.StudyDates
import au.edu.unsw.business.studysync.support.UsageUtils
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val application: StudySyncApplication): AndroidViewModel(application) {

    private val _navigateEvents: PublishRelay<Unit> = PublishRelay.create()
    val navigateEvents get(): Observable<Unit> = _navigateEvents

    private val _usageAccessEnabled = MutableLiveData<Boolean>()
    val usageAccessEnabled get(): LiveData<Boolean> = _usageAccessEnabled

    private val database = application.database

    val subjectSettings = application.subjectSettings

    init {
        _usageAccessEnabled.value = UsageUtils.hasUsageStatsPermission(application)
    }

    fun setUsageAccessEnabled(isEnabled: Boolean) {
        _usageAccessEnabled.value = isEnabled
    }

    fun identify(subjectId: String, authToken: String) {
        subjectSettings.identify(subjectId, authToken)
        _navigateEvents.accept(Unit)
    }

    fun identifyFully(subjectId: String, authToken: String, testGroup: Int, treatmentIntensity: Int, treatmentLimit: Int) {
        subjectSettings.identify(subjectId, authToken)
        subjectSettings.setTestParameters(testGroup, treatmentIntensity, treatmentLimit)
        _navigateEvents.accept(Unit)
    }

    fun clearData() {
        viewModelScope.launch {
            subjectSettings.clearData()

            withContext(Dispatchers.IO) {
                database.clearAllTables()
            }

            WorkManager.getInstance(application).cancelAllWork()

            _navigateEvents.accept(Unit)
        }
    }

    fun completeDebrief() {
        subjectSettings.completeDebrief()
        _navigateEvents.accept(Unit)
    }

    fun updateDates(baselineDate: String, treatmentDate: String, endlineDate: String, overDate: String) {
        subjectSettings.updateDates(baselineDate, treatmentDate, endlineDate, overDate)
        _navigateEvents.accept(Unit)
    }
}