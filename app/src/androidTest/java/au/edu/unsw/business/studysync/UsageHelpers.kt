package au.edu.unsw.business.studysync

import androidx.test.platform.app.InstrumentationRegistry

object UsageHelpers {

    fun enableUsagePermission() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("appops set " + context.packageName + " android:get_usage_stats allow")
    }

    fun disableUsagePermission() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("appops set " + context.packageName + " android:get_usage_stats ignore")
    }

    fun goBack() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("input keyevent 4")
    }
}