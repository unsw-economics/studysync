package au.edu.unsw.business.studysync.usage

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import au.edu.unsw.business.studysync.network.AppReport
import java.util.*
import kotlin.collections.HashMap

object UsageStatsAnalyzer {
    fun getEvents(context: Context, begin: Long, end: Long): UsageEvents {
        val manager = context.getSystemService(AppCompatActivity.USAGE_STATS_SERVICE) as UsageStatsManager
        return manager.queryEvents(begin, end)
    }

    fun getFilteredEventsWithInitialState(context: Context, begin: Long, end: Long): Pair<List<Triple<String, Int, Long>>, Boolean> {
        val stats = getEvents(context, begin, end)

        var initialState: Boolean? = null
        val filteredEvents: MutableList<Triple<String, Int, Long>> = LinkedList()
        val event = UsageEvents.Event()

        while (stats.hasNextEvent()) {
            stats.getNextEvent(event)

            val type = event.eventType

            if (
                type != UsageEvents.Event.ACTIVITY_RESUMED &&
                type != UsageEvents.Event.SCREEN_INTERACTIVE &&
                type != UsageEvents.Event.SCREEN_NON_INTERACTIVE
            ) continue

            val packageName = event.packageName
            val appName = UsageStatsNegotiator.getAppName(context, packageName)

            filteredEvents.add(Triple(appName, type, event.timeStamp))

            if (initialState == null) {
                if (type == UsageEvents.Event.SCREEN_INTERACTIVE) {
                    initialState = false
                } else if (type == UsageEvents.Event.SCREEN_NON_INTERACTIVE) {
                    initialState = true
                }
            }
        }

        if (initialState == null) {
            initialState = false
        }

        return Pair(filteredEvents, initialState)
    }

    fun computeUsage(context: Context, begin: Long, end: Long): Map<String, Long> {
        val (filteredEvents, initialState) = getFilteredEventsWithInitialState(context, begin, end)

        val usageMap: MutableMap<String, Long> = HashMap()
        var interactive = initialState
        var currentApp = ""
        var beginTime = begin

        for ((appName, type, timestamp) in filteredEvents) {

            if (type == UsageEvents.Event.SCREEN_INTERACTIVE) {
                interactive = true
                currentApp = ""
            } else if (type == UsageEvents.Event.SCREEN_NON_INTERACTIVE) {
                if (currentApp != "") {
                    usageMap[currentApp] = usageMap.getOrDefault(currentApp, 0) + timestamp - beginTime
                }

                interactive = false
            } else {
                if (!interactive) continue

                if (currentApp != "" && currentApp != appName) {
                    usageMap[currentApp] = usageMap.getOrDefault(currentApp, 0) + timestamp - beginTime
                    beginTime = timestamp
                }

                currentApp = appName
            }
        }

        if (interactive) {
            usageMap[currentApp] = usageMap.getOrDefault(currentApp, 0) + end - beginTime
        }

        return usageMap
    }

    fun prepareReports(map: Map<String, Long>): List<AppReport> {
        val reports: MutableList<AppReport> = LinkedList()

        for ((appName, usageMilliseconds) in map) {
            reports.add(AppReport(appName, usageMilliseconds / 1000))
        }

        return reports
    }

}