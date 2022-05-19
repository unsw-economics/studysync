package au.edu.unsw.business.studysync

import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import java.util.*

// A wrapper around Kaspresso's ADB server.
// The adb server for desktop must be running. You can find it at:
// https://github.com/KasperskyLab/Kaspresso/tree/master/artifacts
// A copy can also be found in the app/libs folder in this project.
object AdbHelpers : TestCase() {

    fun setAdbAutoTime(enabled: Boolean) {
        if (enabled) {
            adbServer.performShell("settings put global auto_time 1")
        } else {
            adbServer.performShell("settings put global auto_time 0")
        }
    }

    private fun setTime(time: String) {
        // String format is "MMDDhhmmYYYY[.ss]"
        adbServer.performShell("su 0 toybox date $time; su 0 am broadcast -a android.intent.action.TIME_SET")
    }

    fun setOneMinuteBeforeMidnightToday() {
        setAdbAutoTime(false)
        // Get the current month and day from standard library as padded string
        val calendar = Calendar.getInstance()
        val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')

        val time = "${month}${day}2359"
        setTime(time)
    }
}