package au.edu.unsw.business.studysync.logic

import au.edu.unsw.business.studysync.constants.Constants.PERIOD_BASELINE
import au.edu.unsw.business.studysync.constants.Constants.PERIOD_EXPERIMENT
import au.edu.unsw.business.studysync.constants.Environment.BASELINE_LENGTH
import au.edu.unsw.business.studysync.constants.Environment.TREATMENT_START_DATE
import au.edu.unsw.business.studysync.constants.Environment.ZONE_ID
import java.lang.Long.*
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object TimeUtils {
    fun extractHms(duration: Duration): Triple<Long, Long, Long> {
        val milliseconds = duration.toMillis()

        val totalSeconds = milliseconds / 1000
        val seconds = totalSeconds % 60
        val totalMinutes = (totalSeconds - seconds) / 60
        val minutes = totalMinutes % 60
        val hours = (totalMinutes - minutes) / 60

        return Triple(hours, minutes, seconds)
    }

    fun humanizeTimeHms(duration: Duration): String {
        val (hours, minutes, seconds) = extractHms(duration)
        return "$hours hours $minutes minutes $seconds seconds"
    }

    fun humanizeTimeHm(duration: Duration): String {
        val (hours, minutes, _) = extractHms(duration)
        return "$hours hours $minutes minutes"
    }

    fun digitalTimeHm(duration: Duration): String {
        val (hours, minutes, _) = extractHms(duration)
        return String.format("%d:%02d", hours, minutes)
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

    fun percentage(numerator: Duration, denominator: Duration): Int {
        val percentage = divideUnsigned(numerator.seconds * 100, denominator.seconds)
        return min(percentage, 100L).toInt()
    }

    fun lessThan(a: Duration, b: Duration): Boolean {
        return a.compareTo(b) < 0
    }
}