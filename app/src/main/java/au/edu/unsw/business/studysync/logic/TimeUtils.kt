package au.edu.unsw.business.studysync.logic

import au.edu.unsw.business.studysync.constants.Constants.PERIOD_BASELINE
import au.edu.unsw.business.studysync.constants.Constants.PERIOD_EXPERIMENT
import au.edu.unsw.business.studysync.constants.Environment.TREATMENT_START_DATE
import java.util.*

object TimeUtils {
    fun humanizeTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val seconds = totalSeconds % 60
        val totalMinutes = (totalSeconds - seconds) / 60
        val minutes = totalMinutes % 60
        val hours = (totalMinutes - minutes) / 60

        if (hours != 0L) return "${hours}h ${minutes}m ${seconds}s"
        if (minutes != 0L) return "${minutes}m ${seconds}s"
        return "${seconds}s"
    }

    fun getStartOfDay(c: Calendar): Calendar {
        val day = c.clone() as Calendar
        day.set(Calendar.HOUR_OF_DAY, 0)
        day.set(Calendar.MINUTE, 0)
        day.set(Calendar.SECOND, 0)
        day.set(Calendar.MILLISECOND, 0)
        return day
    }

    fun getToday(): Calendar {
        return getStartOfDay(GregorianCalendar())
    }

    fun daysDifference(c: Calendar, d: Calendar): Int {
        // assumes calendars are set to midnight at the start of the day
        val diff = d.timeInMillis - c.timeInMillis
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    fun getStudyPeriodAndDay(c: Calendar): Pair<String, Int> {
        val day = getStartOfDay(c)
        val diff = daysDifference(TREATMENT_START_DATE, day)

        return if (diff >= 0) {
            Pair(PERIOD_EXPERIMENT, diff)
        } else {
            Pair(PERIOD_BASELINE, diff + 7)
        }
    }
}