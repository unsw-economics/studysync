package au.edu.unsw.business.studysync.usage

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageEvents.Event.*
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import au.edu.unsw.business.studysync.network.AppReport
import java.util.*
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

    private fun _getAppName(context: Context, packageName: String): String {
        return try {
            val info = context.packageManager.getApplicationInfo(packageName, 0)
            context.packageManager.getApplicationLabel(info).toString()
        } catch (err: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    fun getAppName(context: Context, packageName: String): String {
        if (packageToApp.containsKey(packageName)) {
            return packageToApp[packageName]!!
        }

        val appName = _getAppName(context, packageName)
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