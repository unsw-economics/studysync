package au.edu.unsw.business.studysync.support

import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.text.toSpannable
import au.edu.unsw.business.studysync.R
import au.edu.unsw.business.studysync.constants.Environment

object MessageUtils {
    fun treatmentAffineDebrief(incentive: Double, limit: String, @ColorInt highlightColor: Int): Spannable {
        return SpannableStringBuilder("From now until the end of the study, you will be rewarded ")
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
            .append(".\n\nAs before, please keep this app installed until ")
            .bold {
                color(highlightColor) {
                    append(Environment.OVER_DATE.toString())
                }
            }
            .append(". When the study concludes, we will ask you for your payment details so that you can be compensated.\n\nThank you for your participation!")
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