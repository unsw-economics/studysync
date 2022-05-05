package au.edu.unsw.business.studysync.constants

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.temporal.ChronoUnit

internal class ConstantsTest {
    @Test
    fun checkLogicalDates() {
        // Checks that the dates are in the correct order
        assertTrue(Environment.BASELINE_DATE_STRING < Environment.TREATMENT_DATE_STRING)
        assertTrue(Environment.TREATMENT_DATE_STRING < Environment.ENDLINE_DATE_STRING)
        assertTrue(Environment.ENDLINE_DATE_STRING < Environment.OVER_DATE_STRING)

        // Checks that the lexical comparison is correct
        assertFalse(Environment.BASELINE_DATE_STRING >= Environment.TREATMENT_DATE_STRING)
        assertFalse(Environment.TREATMENT_DATE_STRING >= Environment.ENDLINE_DATE_STRING)
        assertFalse(Environment.ENDLINE_DATE_STRING >= Environment.OVER_DATE_STRING)
    }

    @Test
    fun checkTreatmentLengthIsFourWeeks() {
        // Checks that the treatment length is four weeks for the hardcoded dates
        assertEquals(28, ChronoUnit.DAYS.between(Environment.TREATMENT_DATE, Environment.ENDLINE_DATE).toInt()
        )
    }

    @Test
    fun checkEnviromentVariablesAreNotNullOrEmpty() {
        // Checks that the environment variables are not null
        assertNotNull(Environment.ZONE_ID)
        assertNotNull(Environment.BASELINE_DATE)
        assertNotNull(Environment.TREATMENT_DATE)
        assertNotNull(Environment.ENDLINE_DATE)
        assertNotNull(Environment.OVER_DATE)
        assertNotNull(Environment.BASELINE_DATE_STRING)
        assertNotNull(Environment.TREATMENT_DATE_STRING)
        assertNotNull(Environment.ENDLINE_DATE_STRING)
        assertNotNull(Environment.OVER_DATE_STRING)

        // Checks that the environment variables are not empty
        assertFalse(Environment.BASELINE_DATE_STRING.isEmpty())
        assertFalse(Environment.TREATMENT_DATE_STRING.isEmpty())
        assertFalse(Environment.ENDLINE_DATE_STRING.isEmpty())
        assertFalse(Environment.OVER_DATE_STRING.isEmpty())
    }

    @Test
    fun checkConstantsAreNotNullOrEmpty() {
        assertNotNull(Constants.PERIOD_BASELINE)
        assertNotNull(Constants.PERIOD_EXPERIMENT)
        assertNotNull(Constants.PERIOD_ENDLINE)
        assertNotNull(Constants.PERIOD_OVER)

        assertNotNull(Constants.DEBUG_DATA)
        assertNotNull(Constants.GROUP_UNASSIGNED)
        assertNotNull(Constants.GROUP_CONTROL)
        assertNotNull(Constants.GROUP_INTERCEPT)
        assertNotNull(Constants.GROUP_AFFINE)
        assertNotNull(Constants.RECORD_AND_SUBMIT_WORK)
        assertNotNull(Constants.DAILY_SCHEDULER_WORK)
        assertNotNull(Constants.DAILY_SCHEDULER_BOUNCE_WORK)
        assertNotNull(Constants.FETCH_TEST_PARAMS_WORK)
        assertNotNull(Constants.PREFERENCES_NAME)
        assertNotNull(Constants.STUDY_PHASE_CHANNEL)

        assertNotNull(Constants.TREATMENT_START_NOTIFICATION)
        assertNotNull(Constants.TREATMENT_OVER_NOTIFICATION)

        assertFalse(Constants.PERIOD_BASELINE.isEmpty())
        assertFalse(Constants.PERIOD_EXPERIMENT.isEmpty())
        assertFalse(Constants.PERIOD_ENDLINE.isEmpty())
        assertFalse(Constants.PERIOD_OVER.isEmpty())
        assertFalse(Constants.DEBUG_DATA.isEmpty())
        assertFalse(Constants.RECORD_AND_SUBMIT_WORK.isEmpty())
        assertFalse(Constants.DAILY_SCHEDULER_WORK.isEmpty())
        assertFalse(Constants.DAILY_SCHEDULER_BOUNCE_WORK.isEmpty())
        assertFalse(Constants.FETCH_TEST_PARAMS_WORK.isEmpty())
        assertFalse(Constants.PREFERENCES_NAME.isEmpty())
        assertFalse(Constants.STUDY_PHASE_CHANNEL.isEmpty())
    }
}