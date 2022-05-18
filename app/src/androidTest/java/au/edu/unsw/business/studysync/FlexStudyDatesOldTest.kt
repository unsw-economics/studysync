package au.edu.unsw.business.studysync

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FlexStudyDatesOldTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testOverScreenAppearsWhenUsingOldId() {
        // Type in subjectId for id that has already done the experiment
        onView(withId(R.id.subjectIdField))
            .perform(typeText("aaaaaa000000"))
            .perform(closeSoftKeyboard())

        // Press submit button
        onView(withId(R.id.identifyButton))
            .perform(click())

        // Wait for the next screen to load and check that it's the experiment over screen
        Thread.sleep(5000)
        onView(withText(R.string.over_title)).check(matches(isDisplayed()))
    }
}