package au.edu.unsw.business.studysync.constants

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
}

object Environment {
    const val JIAMIN_SERVER_URL = "http://192.168.20.12:3000"
    const val LOCALHOST_SERVER_URL = "https://10.0.2.2:8443"

    const val SERVER_URL = LOCALHOST_SERVER_URL

    val ZONE_ID: ZoneId by lazy {
        ZoneId.systemDefault()
    }

    const val BASELINE_START_DATE_STRING = "2021-09-22"
    val BASELINE_START_DATE: LocalDate by lazy {
        LocalDate.parse(BASELINE_START_DATE_STRING)
    }
    val BASELINE_START_DATE_MIDNIGHT: ZonedDateTime by lazy {
        BASELINE_START_DATE.atStartOfDay(ZONE_ID)
    }

    const val TREATMENT_START_DATE_STRING = "2021-10-11"
    val TREATMENT_START_DATE: LocalDate by lazy {
        LocalDate.parse(TREATMENT_START_DATE_STRING)
    }
    val TREATMENT_START_DATE_MIDNIGHT: ZonedDateTime by lazy {
        TREATMENT_START_DATE.atStartOfDay(ZONE_ID)
    }

    val BASELINE_LENGTH: Int by lazy {
        ChronoUnit.DAYS.between(BASELINE_START_DATE, TREATMENT_START_DATE).toInt()
    }
}
