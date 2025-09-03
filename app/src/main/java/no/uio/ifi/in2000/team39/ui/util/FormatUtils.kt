package no.uio.ifi.in2000.team39.ui.util


import java.text.NumberFormat
import java.util.Locale

// bedre nummer formatering med norske regler! f.eks. 27 000 istedenor 27000
fun formatNumber(number: Int): String {
    val format = NumberFormat.getInstance(Locale("no", "NO"))
    return format.format(number)
}
