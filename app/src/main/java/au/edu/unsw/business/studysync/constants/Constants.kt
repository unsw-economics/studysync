package au.edu.unsw.business.studysync.constants

import androidx.work.Constraints
import androidx.work.NetworkType
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

object Constants {
    const val PERIOD_BASELINE = "baseline"
    const val PERIOD_EXPERIMENT = "experiment"
    const val DEBUG_DATA = "DEBUG_DATA"
    const val GROUP_UNASSIGNED = -1
    const val GROUP_CONTROL = 0
}

object Environment {
    val ZONE_ID: ZoneId by lazy {
        ZoneId.systemDefault()
    }

    const val BASELINE_START_DATE_STRING = "2021-09-27"
    val BASELINE_START_DATE: LocalDate by lazy {
        LocalDate.parse(BASELINE_START_DATE_STRING)
    }
    val BASELINE_START_DATE_MIDNIGHT: ZonedDateTime by lazy {
        BASELINE_START_DATE.atStartOfDay(ZONE_ID)
    }

    const val TREATMENT_START_DATE_STRING = "2021-09-30"
    val TREATMENT_START_DATE: LocalDate by lazy {
        LocalDate.parse(TREATMENT_START_DATE_STRING)
    }
    val TREATMENT_START_DATE_MIDNIGHT: ZonedDateTime by lazy {
        TREATMENT_START_DATE.atStartOfDay(ZONE_ID)
    }

    val BASELINE_LENGTH: Int by lazy {
        ChronoUnit.DAYS.between(BASELINE_START_DATE, TREATMENT_START_DATE).toInt()
    }

    const val DAILY_REPORT_WORKER_TAG = "daily_report"

    val NETWORK_CONSTRAINT = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
}
