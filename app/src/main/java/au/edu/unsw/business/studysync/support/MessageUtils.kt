package au.edu.unsw.business.studysync.support

import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.annotation.ColorInt
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.text.toSpannable
import au.edu.unsw.business.studysync.constants.Environment
import java.time.LocalDate

object MessageUtils {
    fun baselineBody(endlineDate: LocalDate, @ColorInt highlightColor: Int): Spannable {
        return SpannableStringBuilder("Please keep this app installed for the duration of the study. You will be asked to complete an endline survey on ")
            .bold {
                color(highlightColor) {
                    append(endlineDate.toString())
                }
            }
            .append(", at which point you will be given instructions regarding deletion of the app.\n\nThank you for your participation!")
            .toSpannable()
    }

    fun treatmentInterceptDebrief(limit: String, @ColorInt highlightColor: Int): Spannable {
        return SpannableStringBuilder("Your target is to keep your smartphone usage under ")
            .bold {
                color(highlightColor) {
                    append(limit)
                }
            }
            .append(" each day. The app will keep a running tally of all the days for which you have met this target over the four week period starting on ")
            .bold {
                color(highlightColor) {
                    append(Environment.TREATMENT_DATE.toString())
                }
            }
            .append(". Note that you will not receive monetary compensation for hitting your target.\n\nPlease keep this app installed for the duration of the study and thank you for your participation!")
            .toSpannable()
    }

    fun treatmentAffineDebrief(incentive: Double, limit: String, @ColorInt highlightColor: Int): Spannable {
        return SpannableStringBuilder("You will be rewarded ")
            .bold {
                color(highlightColor) {
                    append(String.format("$%.2f", incentive))
                }
            }
            .append(" for every day that you keep your daily phone usage under ")
            .bold {
                color(highlightColor) {
                    append(limit)
                }
            }
            .append(" each day. The app will keep a running tally of the reward that you have earned over the four week period starting on ")
            .bold {
                color(highlightColor) {
                    append(Environment.TREATMENT_DATE.toString())
                }
            }
            .append(".\n\nPlease keep this app installed for the duration of the study and thank you for your participation!")
            .toSpannable()
    }

    fun successesMessage(successes: Int, @ColorInt highlightColor: Int): Spannable {
        val span = SpannableStringBuilder("Target met ")
            .bold {
                color(highlightColor) {
                    append(String.format("%d", successes))
                }
            }
            .append(" time")

        if (successes != 1) {
            span.append("s")
        }

        span.append(".")

        return span.toSpannable()
    }
}