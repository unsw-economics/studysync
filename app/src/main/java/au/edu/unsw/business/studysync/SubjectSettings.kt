package au.edu.unsw.business.studysync

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import au.edu.unsw.business.studysync.constants.Constants.GROUP_UNASSIGNED
import au.edu.unsw.business.studysync.constants.Environment
import au.edu.unsw.business.studysync.constants.Environment.BASELINE_DATE_STRING
import au.edu.unsw.business.studysync.constants.Environment.ENDLINE_DATE_STRING
import au.edu.unsw.business.studysync.constants.Environment.OVER_DATE_STRING
import au.edu.unsw.business.studysync.constants.Environment.TREATMENT_DATE_STRING
import au.edu.unsw.business.studysync.support.StudyDates
import au.edu.unsw.business.studysync.support.TimeUtils
import org.acra.ACRA
import java.time.Duration
import java.time.LocalDate

class SubjectSettings(private val preferences: SharedPreferences) {
    private val _identified = MutableLiveData<Boolean>()
    val identified get(): LiveData<Boolean> = _identified

    private val _subjectId = MutableLiveData<String?>()
    val subjectId get(): LiveData<String?> = _subjectId

    private val _authToken = MutableLiveData<String?>()
    val authToken get(): LiveData<String?> = _authToken

    private val _lastRecorded = MutableLiveData<LocalDate>()
    val lastRecorded get(): LiveData<LocalDate> = _lastRecorded

    private val _testGroup = MutableLiveData<Int>()
    val testGroup get(): LiveData<Int> = _testGroup

    private val _treatmentIntensity = MutableLiveData<Int>()
    val treatmentIntensity get(): LiveData<Int> = _treatmentIntensity

    private val _treatmentDebriefed = MutableLiveData<Boolean>()
    val treatmentDebriefed get(): LiveData<Boolean> = _treatmentDebriefed

    private val _treatmentLimit = MutableLiveData<Duration>()
    val treatmentLimit get(): LiveData<Duration> = _treatmentLimit

    init {
        val static = StaticSubjectSettings(preferences)
        _identified.value = static.identified
        _subjectId.value = static.subjectId
        _authToken.value = static.authToken
        _lastRecorded.value = static.lastRecorded
        _testGroup.value = static.testGroup
        _treatmentIntensity.value = static.treatmentIntensity
        _treatmentDebriefed.value = static.treatmentDebriefed
        _treatmentLimit.value = static.treatmentLimit

        if (static.subjectId != null) {
            ACRA.errorReporter.putCustomData("SUBJECT_ID", static.subjectId)
        }

        TimeUtils.studyDates = StudyDates(
            static.baselineDate,
            static.treatmentDate,
            static.endlineDate,
            static.overDate
        )
    }

    fun identify(subjectId: String, authToken: String) {
        preferences.edit {
            putBoolean("identified", true)
            putString("subject-id", subjectId)
            putString("auth-token", authToken)
            commit()
        }

        ACRA.errorReporter.putCustomData("SUBJECT_ID", subjectId)

        _identified.value = true
        _subjectId.value = subjectId
        _authToken.value = authToken
    }

    fun setTestParameters(group: Int, intensity: Int, limit: Int) {
        preferences.edit {
            putInt("test-group", group)
            putInt("treatment-intensity", intensity)
            putInt("treatment-limit", limit)
        }

        _testGroup.value = group
        _treatmentIntensity.value = intensity
        _treatmentLimit.value = Duration.ofSeconds(limit.toLong())
    }

    fun clearData() {
        preferences.edit {
            clear()
            commit()
        }

        _identified.value = false
        _subjectId.value = null
        _authToken.value = null
        _lastRecorded.value = Environment.BASELINE_DATE
        _testGroup.value = GROUP_UNASSIGNED
        _treatmentIntensity.value = 0
        _treatmentDebriefed.value = false
        _treatmentLimit.value = Duration.ZERO
    }

    fun setLastRecorded(date: LocalDate) {
        preferences.edit {
            putString("last-recorded", date.toString())
        }

        _lastRecorded.value = date
    }

    fun completeDebrief() {
        preferences.edit {
            putBoolean("treatment-debriefed", true)
        }

        _treatmentDebriefed.value = true
    }

    fun updateDates(baselineDate: String, treatmentDate: String, endlineDate: String, overDate: String) {
        preferences.edit {
            putString("baseline-date", baselineDate)
            putString("treatment-date", treatmentDate)
            putString("endline-date", endlineDate)
            putString("over-date", overDate)
        }

        TimeUtils.studyDates = StudyDates(
            baselineDate = LocalDate.parse(baselineDate),
            treatmentDate = LocalDate.parse(treatmentDate),
            endlineDate = LocalDate.parse(endlineDate),
            overDate = LocalDate.parse(overDate)
        )
    }
}

data class StaticSubjectSettings(val preferences: SharedPreferences) {
    val identified: Boolean = preferences.getBoolean("identified", false)
    val subjectId: String? = preferences.getString("subject-id", null)
    val authToken: String? = preferences.getString("auth-token", null)
    val lastRecorded: LocalDate = LocalDate.parse(preferences.getString("last-recorded", BASELINE_DATE_STRING)!!)
    val testGroup: Int = preferences.getInt("test-group", GROUP_UNASSIGNED)
    val treatmentIntensity: Int = preferences.getInt("treatment-intensity", 0)
    val treatmentDebriefed: Boolean = preferences.getBoolean("treatment-debriefed", false)
    val treatmentLimit: Duration = Duration.ofSeconds(preferences.getInt("treatment-limit", 0).toLong())
    val baselineDate: LocalDate = LocalDate.parse(preferences.getString("baseline-date", BASELINE_DATE_STRING)!!)
    val treatmentDate: LocalDate = LocalDate.parse(preferences.getString("treatment-date", TREATMENT_DATE_STRING)!!)
    val endlineDate: LocalDate = LocalDate.parse(preferences.getString("endline-date", ENDLINE_DATE_STRING)!!)
    val overDate: LocalDate = LocalDate.parse(preferences.getString("over-date", OVER_DATE_STRING)!!)
}