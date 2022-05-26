package au.edu.unsw.business.studysync.support

import au.edu.unsw.business.studysync.constants.Environment
import io.kotlintest.specs.AbstractAnnotationSpec
import io.kotlintest.specs.AbstractAnnotationSpec.After
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDate

internal class TimeUtilsTest {

    @Test
    fun `Duration of 1 hour to a readable format`() {
        val d: Duration = Duration.ofHours(1)
        assertEquals("1 hour", TimeUtils.humanizeTimeHm(d))
    }

    @Test
    fun `Duration of 1 hour and 1 minute to a readable format`() {
        val d: Duration = Duration.ofMinutes(61)
        assertEquals("1 hour 1 minute", TimeUtils.humanizeTimeHm(d))
    }

    @Test
    fun `Duration of 59 minute to a readable format`() {
        val d: Duration = Duration.ofMinutes(59)
        assertEquals("59 minutes", TimeUtils.humanizeTimeHm(d))
    }

    @Test
    fun `Duration of 1 minute to a readable format`() {
        val d: Duration = Duration.ofMinutes(1)
        assertEquals("1 minute", TimeUtils.humanizeTimeHm(d))
    }

    @Test
    fun `Duration of 1 hour and 9 minutes to a readable format`() {
        val d: Duration = Duration.ofMinutes(69)
        assertEquals("1 hour 9 minutes", TimeUtils.humanizeTimeHm(d))
    }

    @Test
    fun `Duration of 1 hour and 10 minutes to a readable format`() {
        val d: Duration = Duration.ofMinutes(70)
        assertEquals("1 hour 10 minutes", TimeUtils.humanizeTimeHm(d))
    }

    @Test
    fun `Duration of 2 hour and 2 minutes to a readable format`() {
        val d: Duration = Duration.ofMinutes(122)
        assertEquals("2 hours 2 minutes", TimeUtils.humanizeTimeHm(d))
    }

    @Test
    fun `Duration of 0 minutes to a readable format`() {
        val d: Duration = Duration.ofMinutes(0)
        assertEquals("0 minutes", TimeUtils.humanizeTimeHm(d))
    }

    @AbstractAnnotationSpec.Before
    fun before() {
        mockkObject(Environment)
        every { Environment.BASELINE_DATE } returns LocalDate.parse("2022-05-27")
        every { Environment.TREATMENT_DATE } returns LocalDate.parse("2022-06-10")
        every { Environment.ENDLINE_DATE } returns LocalDate.parse("2022-07-08")
    }

    @Test
    fun `start of baseline period`() {
        val date = LocalDate.parse("2022-05-27")
        val (studyStage, days) = TimeUtils.getStudyPeriodAndDay(date)
        assertEquals("baseline", studyStage)
        assertEquals(0, days)
    }

    @Test
    fun `end of baseline period`() {
        val date = LocalDate.parse("2022-06-09")
        val (studyStage, days) = TimeUtils.getStudyPeriodAndDay(date)
        assertEquals("baseline", studyStage)
        assertEquals(13, days)
    }

    @Test
    fun `start of experiment period`() {
        val date = LocalDate.parse("2022-06-10")
        val (studyStage, days) = TimeUtils.getStudyPeriodAndDay(date)
        assertEquals("experiment", studyStage)
        assertEquals(0, days)
    }

    @Test
    fun `one day after experiment period`() {
        val date = LocalDate.parse("2022-06-11")
        val (studyStage, days) = TimeUtils.getStudyPeriodAndDay(date)
        assertEquals("experiment", studyStage)
        assertEquals(1, days)
    }

    @Test
    fun `middle of experiment period`() {
        val date = LocalDate.parse("2022-06-25")
        val (studyStage, days) = TimeUtils.getStudyPeriodAndDay(date)
        assertEquals("experiment", studyStage)
        assertEquals(15, days)
    }

    @Test
    fun `end of experiment period`() {
        val date = LocalDate.parse("2022-07-07")
        val (studyStage, days) = TimeUtils.getStudyPeriodAndDay(date)
        assertEquals("experiment", studyStage)
        assertEquals(27, days)
    }

    @Test
    fun `start of endline period`() {
        val date = LocalDate.parse("2022-07-08")
        val (studyStage, days) = TimeUtils.getStudyPeriodAndDay(date)
        assertEquals("endline", studyStage)
        assertEquals(0, days)
    }

    @Test
    fun `one day after endline period`() {
        val date = LocalDate.parse("2022-07-09")
        val (studyStage, days) = TimeUtils.getStudyPeriodAndDay(date)
        assertEquals("endline", studyStage)
        assertEquals(1, days)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}