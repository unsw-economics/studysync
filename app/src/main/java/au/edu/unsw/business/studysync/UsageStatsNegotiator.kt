package au.edu.unsw.business.studysync

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object UsageStatsNegotiator {
    private val packageToApp: MutableMap<String, String> = HashMap()

    fun openUsageAccessPermissionsMenu(context: Context) {
        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow("android:get_usage_stats", Process.myUid(), context.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun generateUsageJson(context: Context, usageMap: Map<String, Long>): JSONArray {
        val usageJSON = JSONArray()

        usageJSON.put(0)

        var sum: Long = 0
        val usageList: MutableList<Pair<String, Long>> = ArrayList()

        for ((appName, time) in usageMap) {
            usageList.add(Pair(appName, time))
        }

        Collections.sort(usageList, compareBy { -it.second })

        for ((appName, time) in usageList) {
            val usage = JSONArray()
            usage.put(appName)
            usage.put(TimeUtil.humanizeTime(time))

            sum += time
            usageJSON.put(usage)
        }

        val meta = JSONObject()
        meta.put("today_usage", TimeUtil.humanizeTime(sum))

        usageJSON.put(0, meta)

        return usageJSON
    }

    fun getTodayUsageJson(context: Context): JSONArray {
        val today = GregorianCalendar()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val previousMidnight = today.timeInMillis
        val now = System.currentTimeMillis()

        val usageMap = computeUsagesSerial(context, previousMidnight, now)
        return generateUsageJson(context, usageMap)
    }

    fun computeUsagesSerial(context: Context, begin: Long, end: Long): Map<String, Long> {
        val manager = context.getSystemService(AppCompatActivity.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats = manager.queryEvents(begin, end)

        val usageMap: MutableMap<String, Long> = HashMap()

        val event = UsageEvents.Event()
        var last = begin

        var lastApp = ""

        while (stats.hasNextEvent()) {
            stats.getNextEvent(event)

            val type = event.eventType
            val packageName = event.packageName

            val appName = retrieveAppName(context, packageName)

            if (type == UsageEvents.Event.ACTIVITY_RESUMED || type == UsageEvents.Event.ACTIVITY_PAUSED) {
                val time = event.timeStamp

                if (type == UsageEvents.Event.ACTIVITY_RESUMED) {
                    last = time
                } else if (appName == lastApp) {
                    val difference = time - last
                    usageMap[appName] = usageMap.getOrDefault(appName, 0L) + difference
                }

                lastApp = appName
            }

        }

        if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
            val appName = retrieveAppName(context, event.packageName)
            usageMap[appName] = usageMap.getOrDefault(appName, 0L) + (end - event.timeStamp)
        }

        return usageMap
    }

    private fun getAppName(context: Context, packageName: String): String {
        return try {
            val info = context.packageManager.getApplicationInfo(packageName, 0)
            context.packageManager.getApplicationLabel(info).toString()
        } catch (err: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    private fun retrieveAppName(context: Context, packageName: String): String {
        if (packageToApp.containsKey(packageName)) {
            return packageToApp[packageName]!!
        }

        val appName = getAppName(context, packageName)
        packageToApp[packageName] = appName
        return appName
    }

    fun getLauncherActivities(context: Context): List<String> {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_HOME)
        val infos = context.packageManager.queryIntentActivities(intent, 0)

        return infos.map { it.activityInfo.packageName }
    }
}