package au.edu.unsw.business.studysync.constants

import java.time.LocalDate
import java.time.Period
import java.util.*

object Constants {
    const val PERIOD_BASELINE = "baseline"
    const val PERIOD_EXPERIMENT = "experiment"
}

object Environment {
    const val SERVER_URL = "https://10.0.2.2:8443"

    val TREATMENT_START_DATE by lazy {
        GregorianCalendar(2021, 9, 11) // 10-11-2021
    }
}
