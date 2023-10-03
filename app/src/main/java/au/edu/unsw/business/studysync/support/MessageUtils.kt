package au.edu.unsw.business.studysync.support

import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.annotation.ColorInt
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.text.toSpannable

object MessageUtils {
    fun baselineBody(@ColorInt highlightColor: Int): Spannable {
        return SpannableStringBuilder("You do not need to do anything else at the moment. Please keep this app installed for the duration of the study. You will be asked to complete an endline survey around week 10.")
            .toSpannable()
        /*
        return SpannableStringBuilder("You do not need to do anything else at the moment. Please keep this app installed for the duration of the study. You will be asked to complete an endline survey on ")
            .bold {
                color(highlightColor) {
                    append(TimeUtils.studyDates.endlineDate.toString())
                }
            }
            .append(".\n\nThank you for your participation!")
            .toSpannable()
         */
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
                    append(TimeUtils.studyDates.treatmentDate.toString())
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
                    append(TimeUtils.studyDates.treatmentDate.toString())
                }
            }
            .append(".\n\nPlease keep this app installed for the duration of the study and thank you for your participation!")
            .toSpannable()
    }

    fun endlineBody(@ColorInt highlightColor: Int): Spannable {
        return SpannableStringBuilder("Please check your email inbox and spam folder for the invitation to the Endline Survey.  Upon completion of the Endline Survey, your total earnings from this portion of the study (if any) along with \$25 for completing the surveys will be paid to your nominated PayID account within a week.  If you have any questions, please contact the research team at unswsmartphoneproject@gmail.com.\n\nPlease keep the app installed until Week 12")
//            .bold {
//                color(highlightColor) {
//                    append(TimeUtils.studyDates.overDate.toString())
//                }
//            }
            .append(", at which point you will be given instructions regarding deletion of the app. If you do so, you will also be entered into a lottery to win $100.\n\nThank you for your participation!")
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