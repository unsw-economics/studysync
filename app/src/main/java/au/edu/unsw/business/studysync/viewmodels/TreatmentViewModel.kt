package au.edu.unsw.business.studysync.viewmodels

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import au.edu.unsw.business.studysync.StudySyncApplication
import au.edu.unsw.business.studysync.support.TimeUtils
import au.edu.unsw.business.studysync.usage.UsageStatsAnalyzer
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.Duration

class TreatmentViewModel(private val application: StudySyncApplication): AndroidViewModel(application) {

    private val usageDriver = application.usageDriver

    private val _todayUsage = MutableLiveData<Duration>()
    val todayUsage get(): LiveData<Duration> = _todayUsage

    private val _totalEarned = MutableLiveData<Double>()
    val totalEarned get(): LiveData<Double> = _totalEarned

    private val _dailyIncentive = MutableLiveData<Double>()
    val dailyIncentive get(): LiveData<Double> = _dailyIncentive

    init {
        _todayUsage.value = Duration.ZERO
        // TODO Yet to implement search for how much they have earned in the treatment period
        _totalEarned.value = 0.0
        _dailyIncentive.value = application.subjectSettings.testGroup.value!! * 0.5
    }

    fun setTodayUsage() {
        viewModelScope.launch {
            _todayUsage.value = usageDriver.computeTodayUsage()
        }
    }

    fun setTotalEarned() {
        viewModelScope.launch {
            _totalEarned.value = usageDriver.computeTotalEarned()
        }
    }

}
