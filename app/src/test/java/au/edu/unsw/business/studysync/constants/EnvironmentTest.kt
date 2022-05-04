package au.edu.unsw.business.studysync.constants

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class EnvironmentTest {
    @Test
    fun checkLogicalDates() {
        // Checks that the dates are in the correct order
        assertTrue(Environment.BASELINE_DATE_STRING < Environment.TREATMENT_DATE_STRING)
        assertTrue(Environment.TREATMENT_DATE_STRING < Environment.ENDLINE_DATE_STRING)
        assertTrue(Environment.ENDLINE_DATE_STRING < Environment.OVER_DATE_STRING)
    }
}