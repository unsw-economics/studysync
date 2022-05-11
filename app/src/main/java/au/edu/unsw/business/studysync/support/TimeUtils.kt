package au.edu.unsw.business.studysync.support

import au.edu.unsw.business.studysync.constants.Constants.PERIOD_BASELINE
import au.edu.unsw.business.studysync.constants.Constants.PERIOD_ENDLINE
import au.edu.unsw.business.studysync.constants.Constants.PERIOD_EXPERIMENT
import au.edu.unsw.business.studysync.constants.Constants.PERIOD_OVER
import au.edu.unsw.business.studysync.constants.Environment.BASELINE_LENGTH
import au.edu.unsw.business.studysync.constants.Environment.ENDLINE_DATE
import au.edu.unsw.business.studysync.constants.Environment.OVER_DATE
import au.edu.unsw.business.studysync.constants.Environment.TREATMENT_DATE
import au.edu.unsw.business.studysync.constants.Environment.ZONE_ID
import java.lang.Long.divideUnsigned
import java.lang.Long.min
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object TimeUtils {
    // val TIME_DELAY = Duration.ofHours(23).plusMinutes(54)
    val TIME_DELAY = Duration.ZERO
    var periodToday: String? = null // Used for testing purposes to mock date

    fun getPeriod(date: LocalDate): String {
        return when {
            date.isBefore(TREATMENT_DATE) -> PERIOD_BASELINE
            date.isBefore(ENDLINE_DATE) -> PERIOD_EXPERIMENT
            date.isBefore(OVER_DATE) -> PERIOD_ENDLINE
            else -> PERIOD_OVER
        }
    }

    fun nowZDT(): ZonedDateTime {
        val now = ZonedDateTime.now()

        return if (TIME_DELAY.equals(Duration.ZERO)) {
            now
        } else {
            now.minus(TIME_DELAY)
        }
    }

    fun nowLD(): LocalDate {
        return if (TIME_DELAY.equals(Duration.ZERO)) {
            LocalDate.now()
        } else {
            nowZDT().toLocalDate()
        }
    }

    fun getTodayPeriod(): String {
        return periodToday ?: getPeriod(nowLD())

        // for testing
        // return getPeriod(ZonedDateTime.now().minusMinutes(105).toLocalDate())
    }

    private fun extractHms(duration: Duration): Triple<Int, Int, Int> {
        val milliseconds = duration.toMillis()

        val totalSeconds = milliseconds / 1000
        val seconds = totalSeconds % 60
        val totalMinutes = (totalSeconds - seconds) / 60
        val minutes = totalMinutes % 60
        val hours = (totalMinutes - minutes) / 60

        return Triple(hours.toInt(), minutes.toInt(), seconds.toInt())
    }

    fun humanizeTimeHms(duration: Duration): String {
        val (hours, minutes, seconds) = extractHms(duration)
        return "$hours hours $minutes minutes $seconds seconds"
    }

    fun humanizeTimeHm(duration: Duration): String {
        val (hours, minutes, _) = extractHms(duration)
        if (hours == 0 && minutes == 0) return "0 minutes"

        val hourStr = when (hours) {
            1 -> "1 hour"
            0 -> ""
            else -> "$hours hours"
        }
        val minuteStr = when {
            minutes == 1 -> "1 minute"
            (minutes == 0) && (hours != 0) -> ""
            else -> "$minutes minutes"
        }

        return "$hourStr $minuteStr".trim()
    }

    fun digitalTimeHm(duration: Duration): String {
        val (hours, minutes, _) = extractHms(duration)
        return String.format("%d:%02d", hours, minutes)
    }

    fun getStudyPeriodAndDay(d: LocalDate): Pair<String, Int> {
        // Days start at 0
        val treatmentTime = ChronoUnit.DAYS.between(TREATMENT_DATE, d).toInt()
        val endlineTime = ChronoUnit.DAYS.between(ENDLINE_DATE, d).toInt()

        return when {
            treatmentTime < 0 -> Pair(PERIOD_BASELINE, treatmentTime + BASELINE_LENGTH)
            endlineTime < 0 -> Pair(PERIOD_EXPERIMENT, treatmentTime)
            else -> Pair(PERIOD_ENDLINE, endlineTime)
        }
    }

    fun toMilliseconds(d: ZonedDateTime): Long {
        return d.toInstant().toEpochMilli()
    }

    fun toMilliseconds(d: LocalDate): Long {
        return toMilliseconds(d.atStartOfDay(ZONE_ID))
    }

    fun now(): Long {
        return toMilliseconds(nowZDT())
    }

    fun midnight(): Long {
        return toMilliseconds(nowLD())
    }

    fun percentage(numerator: Duration, denominator: Duration): Int {
        if (denominator.seconds == 0L) return 0

        val percentage = divideUnsigned(numerator.seconds * 100, denominator.seconds)
        return min(percentage, 100L).toInt()
    }

    fun lessThan(a: Duration, b: Duration): Boolean {
        return a < b
    }
}