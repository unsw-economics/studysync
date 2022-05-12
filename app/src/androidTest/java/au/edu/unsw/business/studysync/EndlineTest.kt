package au.edu.unsw.business.studysync

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import au.edu.unsw.business.studysync.constants.Constants
import au.edu.unsw.business.studysync.support.TimeUtils
import org.hamcrest.CoreMatchers.not
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndlineTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        TimeUtils.periodToday = Constants.PERIOD_ENDLINE
        scenario = ActivityScenario.launch(MainActivity::class.java)
        UsageHelpers.disableUsagePermission()
        Thread.sleep(1000)
    }

    @After
    fun tearDown() {
        TimeUtils.periodToday = null
        scenario.close()
        UsageHelpers.disableUsagePermission()
    }

    @Test
    fun testEndlineScreenAppears() {
        onView(withText(R.string.endline_title)).check(matches(isDisplayed()))
    }
}