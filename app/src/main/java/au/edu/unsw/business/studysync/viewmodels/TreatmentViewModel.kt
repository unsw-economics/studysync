package au.edu.unsw.business.studysync.viewmodels

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import au.edu.unsw.business.studysync.StudySyncApplication
import java.time.Duration

class TreatmentViewModel(application: StudySyncApplication): AndroidViewModel(application) {

    private val _timeSpentToday = MutableLiveData<Duration>()
    val timeSpentToday get(): LiveData<Duration> = _timeSpentToday

    private val _valueEarned = MutableLiveData<Double>()
    val valueEarned get(): LiveData<Double> = _valueEarned

    private val _dailyIncentive = MutableLiveData<Double>()
    val dailyIncentive get(): LiveData<Double> = _dailyIncentive

    fun setTimeSpentToday(timeSpentToday: Duration) {
        _timeSpentToday.value = timeSpentToday
    }

    init {
        _timeSpentToday.value = Duration.ZERO
        // TODO Yet to implement search for how much they have earned in the treatment period
        _valueEarned.value = 0.0
        _dailyIncentive.value = application.subjectSettings.testGroup.value!! * 0.5
    }

}
