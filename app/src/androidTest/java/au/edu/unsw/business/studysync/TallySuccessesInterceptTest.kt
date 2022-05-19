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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TallySuccessesInterceptTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        AdbHelpers.setOneMinuteBeforeMidnightToday()
        scenario = ActivityScenario.launch(MainActivity::class.java)
        UsageHelpers.disableUsagePermission()
    }

    @After
    fun tearDown() {
        scenario.close()
        UsageHelpers.disableUsagePermission()
        AdbHelpers.setAdbAutoTime(true)
    }

    private fun delay() {
        // Helper function to delay test execution
        // for manually inspecting the UI
        Thread.sleep(1000)
    }

    @Test
    fun testTallyForInterceptGroup() {
        loginScreenTest()
        requestPermissionTest()
        checkTreatmentDebriefInstructions()
        checkTreatementScreenDetails()
        testTimeAndTallyIncrementsCorrectly()
    }

    private fun loginScreenTest() {
        // Type in subjectId for control group subject
        onView(withId(R.id.subjectIdField))
            .perform(typeText("info"))
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

    private fun checkTreatmentDebriefInstructions() {
        // Check that the screen is on the debrief instructions screen
        onView(withText(R.string.debrief_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.continueButton)).check(matches(isDisplayed()))
        onView(withId(R.id.continueButton)).check(matches(isEnabled()))

        // Press continue button
        delay()
        onView(withId(R.id.continueButton))
            .perform(click())
        delay()
    }

    private fun checkTreatementScreenDetails() {
        // Check that we are on the treatment screen
        onView(withId(R.id.linearLayout))
            .check(matches(isDisplayed()))

        // Check that the correct treatment hint is displayed for the intercept group
        onView(withId(R.id.treatmentHintLabel))
            .check(matches(withText(R.string.treatment_hint_intercept)))

        // Check that the progress bar is shown
        onView(withId(R.id.progress))
            .check(matches(isDisplayed()))
        onView(withId(R.id.todayUsageView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.maxUsageView))
            .check(matches(isDisplayed()))

        // Check that the incentive label does not appear
        onView(withId(R.id.incentiveLabel))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.incentiveView))
            .check(matches(not(isDisplayed())))

        // Check that the amount earned does not appear
        onView(withId(R.id.totalEarnedLabel))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.totalEarnedView))
            .check(matches(not(isDisplayed())))

        // Check that the number of successful treatments is displayed
        onView(withId(R.id.successes_message))
            .check(matches(isDisplayed()))
    }

    private fun testTimeAndTallyIncrementsCorrectly() {
        // Record the value of the progress bar
        val initialText = getText(onView(withId(R.id.todayUsageView)))

        // Record the value of the target counter
        val initialTarget = getText(onView(withId(R.id.successes_message)))

        // Do nothing for 1 minute and 1 second
        Thread.sleep(61000)

        // Retrieve text and check that it isn't the same as the initial value
        scenario.recreate()
        val finalText = getText(onView(withId(R.id.todayUsageView)))
        assertNotEquals(initialText, finalText)

        // Check that the target incremented by 1
        val finalTarget = getText(onView(withId(R.id.successes_message)))
        assertNotEquals(initialTarget, finalTarget)
        delay()
    }
}