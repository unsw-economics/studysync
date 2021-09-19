package au.edu.unsw.business.studysync

object TimeUtil {
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
}