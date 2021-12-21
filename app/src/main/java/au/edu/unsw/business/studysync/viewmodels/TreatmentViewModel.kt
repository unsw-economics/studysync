package au.edu.unsw.business.studysync.viewmodels

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import au.edu.unsw.business.studysync.StudySyncApplication
import au.edu.unsw.business.studysync.constants.Constants.GROUP_INTERCEPT
import kotlinx.coroutines.launch
import java.time.Duration

class TreatmentViewModel(private val application: StudySyncApplication): AndroidViewModel(application) {

    private val usageDriver = application.usageDriver

    private val _todayUsage = MutableLiveData<Duration>()
    val todayUsage get(): LiveData<Duration> = _todayUsage

    private val _successes = MutableLiveData<Int>()
    val successes get(): LiveData<Int> = _successes

    private val _dailyIncentive = MutableLiveData<Double>()
    val dailyIncentive get(): LiveData<Double> = _dailyIncentive

    private var _interceptGroup: Boolean?
    val interceptGroup get(): Boolean = _interceptGroup!!

    init {
        _todayUsage.value = Duration.ZERO
        _successes.value = 0
        _dailyIncentive.value = application.subjectSettings.treatmentIntensity.value!! * 0.5
        _interceptGroup = application.subjectSettings.testGroup.value!! == GROUP_INTERCEPT
    }

    fun setTodayUsage() {
        viewModelScope.launch {
            _todayUsage.value = usageDriver.computeTodayUsage()
        }
    }

    fun setSuccesses() {
        viewModelScope.launch {
            _successes.value = usageDriver.countSuccesses()
        }
    }

}
