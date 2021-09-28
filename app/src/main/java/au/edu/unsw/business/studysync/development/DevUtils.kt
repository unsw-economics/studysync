package au.edu.unsw.business.studysync.development

object DevUtils {
    fun<T> printList(list: List<T>): String {
        var s = ""
        var first = true

        for (x in list) {
            if (first) {
                s += "$x"
                first = false
            } else {
                s += "\n$x"
            }
        }

        return s
    }
}