package au.edu.unsw.business.studysync

import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

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

    fun setTime(time: String) {
        // String format is "MMDDhhmmYYYY[.ss]"
        adbServer.performShell("su 0 toybox date $time; su 0 am broadcast -a android.intent.action.TIME_SET")
    }

    fun grantUsageAccess() {
        adbServer.performShell("su 0 pm grant au.edu.unsw.business.studysync android.permission.PACKAGE_USAGE_STATS")
    }
}