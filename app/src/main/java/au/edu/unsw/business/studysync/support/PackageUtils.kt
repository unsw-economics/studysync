package au.edu.unsw.business.studysync.support

import android.content.Context
import android.content.pm.PackageManager

object PackageUtils {
    private val packageToApp: MutableMap<String, String> = HashMap()

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
}