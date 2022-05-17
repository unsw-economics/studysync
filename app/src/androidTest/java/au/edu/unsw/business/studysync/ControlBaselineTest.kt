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
class ControlBaselineTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        TimeUtils.periodToday = Constants.PERIOD_BASELINE
        scenario = ActivityScenario.launch(MainActivity::class.java)
        UsageHelpers.disableUsagePermission()
    }

    @After
    fun tearDown() {
        TimeUtils.periodToday = null
        scenario.close()
        UsageHelpers.disableUsagePermission()
    }

    private fun delay() {
        // Helper function to delay test execution
        // for manually inspecting the UI
        Thread.sleep(1000)
    }

    @Test
    fun testControlGroupFlowDuringBaseline() {
        loginScreenTest()
        requestPermissionTest()
        baselineTextTest()
    }

    private fun loginScreenTest() {
        // Type in subjectId for control group subject
        onView(withId(R.id.subjectIdField))
            .perform(typeText("aaaaaa000000"))
            .perform(closeSoftKeyboard())

        // Press submit button
        onView(withId(R.id.identifyButton))
            .perform(click())

        // Wait for request permission screen to appear
        onView(isRoot()).perform(waitForView(R.id.requestPermissionButton, 5000))

        // Check that the screen navigated to the request permission screen
        onView(withText(R.string.request_permission_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.requestPermissionButton))
            .check(matches(isDisplayed()))
        onView(withId(R.id.continueButton))
            .check(matches(isDisplayed()))
    }

    private fun requestPermissionTest() {
        // Check that the continue button is disabled
        onView(withId(R.id.continueButton))
            .check(matches(not(isEnabled())))

        // Check that the request permission button is enabled
        onView(withId(R.id.requestPermissionButton))
            .check(matches(isEnabled()))

        // Press request permission button
        delay()
        onView(withId(R.id.requestPermissionButton))
            .perform(click())

        // Grant permission
        delay()
        UsageHelpers.enableUsagePermission()
        UsageHelpers.goBack()

        // Check that the continue button is enabled
        onView(withId(R.id.continueButton))
            .check(matches(isEnabled()))

        // Check that the request permission button is disabled
        onView(withId(R.id.requestPermissionButton))
            .check(matches(not(isEnabled())))

        // Press continue button
        delay()
        onView(withId(R.id.continueButton))
            .perform(click())
    }

    private fun baselineTextTest() {
        // Check that the debrief screen is displayed with the baseline string
        delay()
        onView(withText(R.string.baseline_title)).check(matches(isDisplayed()))
    }
}