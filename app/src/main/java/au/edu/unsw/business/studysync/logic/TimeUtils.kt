package au.edu.unsw.business.studysync.logic

import au.edu.unsw.business.studysync.constants.Constants.PERIOD_BASELINE
import au.edu.unsw.business.studysync.constants.Constants.PERIOD_EXPERIMENT
import au.edu.unsw.business.studysync.constants.Environment.BASELINE_LENGTH
import au.edu.unsw.business.studysync.constants.Environment.TREATMENT_START_DATE
import au.edu.unsw.business.studysync.constants.Environment.ZONE_ID
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

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

    fun getStudyPeriodAndDay(d: LocalDate): Pair<String, Int> {
        val diff = ChronoUnit.DAYS.between(TREATMENT_START_DATE, d).toInt()

        return if (diff >= 0) {
            Pair(PERIOD_EXPERIMENT, diff)
        } else {
            Pair(PERIOD_BASELINE, diff + BASELINE_LENGTH)
        }
    }

    fun toMilliseconds(d: ZonedDateTime): Long {
        return d.toInstant().toEpochMilli()
    }

    fun toMilliseconds(d: LocalDate): Long {
        return toMilliseconds(d.atStartOfDay(ZONE_ID))
    }

    fun now(): Long {
        return toMilliseconds(ZonedDateTime.now())
    }

    fun midnight(): Long {
        return toMilliseconds(LocalDate.now())
    }

    fun prettyHoursMinutesSeconds(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val seconds = totalSeconds % 60
        val totalMinutes = (totalSeconds - seconds) / 60
        val minutes = totalMinutes % 60
        val hours = (totalMinutes - minutes) / 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}