package au.edu.unsw.business.studysync.constants

import au.edu.unsw.business.studysync.support.TimeUtils
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object Constants {
    // keep lowercase for database compatibility
    const val PERIOD_BASELINE = "baseline"
    const val PERIOD_EXPERIMENT = "experiment"
    const val PERIOD_ENDLINE = "endline"
    const val PERIOD_OVER = "over"

    const val DEBUG_DATA = "DEBUG_DATA"
    const val GROUP_UNASSIGNED = -1
    const val GROUP_CONTROL = 0
    const val GROUP_INTERCEPT = 1
    const val GROUP_AFFINE = 2
    const val RECORD_AND_SUBMIT_WORK = "RECORD_AND_SUBMIT_WORK"
    const val DAILY_SCHEDULER_WORK = "DAILY_SCHEDULER_WORK"
    const val DAILY_SCHEDULER_BOUNCE_WORK = "DAILY_SCHEDULER_BOUNCE_WORK"
    const val FETCH_TEST_PARAMS_WORK = "FETCH_TEST_PARAMS_WORK"
    const val DAILY_CHECK_STUDY_DATES = "DAILY_CHECK_STUDY_DATES"
    const val PERODIC_SUBMIT_USAGE_WORK = "PERODIC_SUBMIT_USAGE_WORK"
    const val PERODIC_UPDATE_DATES_WORK = "PERODIC_UPDATE_DATES_WORK"
    const val PREFERENCES_NAME = "studysync-config"

    const val STUDY_PHASE_CHANNEL = "STUDY_PHASE_CHANNEL"

    const val TREATMENT_START_NOTIFICATION = 0
    const val TREATMENT_OVER_NOTIFICATION = 1
}

object Environment {
    val ZONE_ID: ZoneId by lazy {
        ZoneId.systemDefault()
    }

    const val BASELINE_DATE_STRING = "2024-02-19"
    val BASELINE_DATE: LocalDate by lazy {
        LocalDate.parse(BASELINE_DATE_STRING)
    }

    const val TREATMENT_DATE_STRING = "2024-03-04"
    val TREATMENT_DATE: LocalDate by lazy {
        LocalDate.parse(TREATMENT_DATE_STRING)
    }

    const val ENDLINE_DATE_STRING = "2024-04-01"
    val ENDLINE_DATE: LocalDate by lazy {
        LocalDate.parse(ENDLINE_DATE_STRING)
    }

    const val OVER_DATE_STRING = "2024-04-28"
    val OVER_DATE: LocalDate by lazy {
        LocalDate.parse(OVER_DATE_STRING)
    }

    val BASELINE_LENGTH: Int by lazy {
        ChronoUnit.DAYS.between(TimeUtils.studyDates.baselineDate, TimeUtils.studyDates.treatmentDate).toInt()
    }
}
