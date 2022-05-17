package au.edu.unsw.business.studysync.support

import java.time.LocalDate

data class StudyDates(
    val baselineDate: LocalDate,
    val treatmentDate: LocalDate,
    val endlineDate: LocalDate,
    val overDate: LocalDate
)
