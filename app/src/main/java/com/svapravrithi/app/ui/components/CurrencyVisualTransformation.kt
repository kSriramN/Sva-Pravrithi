package com.svapravrithi.app.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.svapravrithi.app.domain.model.Currency

/**
 * Formats an amount input field's DISPLAY with comma grouping (Indian lakh/crore for
 * INR, international 3-digit otherwise) as the user types, while the underlying
 * TextField state stays plain digits (e.g. "18500") - so parsing with toDoubleOrNull()
 * elsewhere is completely unaffected. Handles a single decimal point by grouping only
 * the integer portion. Cursor position is remapped correctly as commas shift while
 * typing (verified: typing "1" -> "1", "18" -> "18", "185" -> "185", "1850" -> "1,850",
 * "18500" -> "18,500", cursor always lands at the end of the newly-typed digit).
 */
class CurrencyVisualTransformation(private val currency: Currency) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val dotIndex = raw.indexOf('.')
        val intPart = if (dotIndex >= 0) raw.substring(0, dotIndex) else raw
        val rest = if (dotIndex >= 0) raw.substring(dotIndex) else "" // includes the '.' itself

        val groupedInt = groupDigits(intPart, currency.useIndianGrouping)
        val transformed = groupedInt + rest

        // prefixMap[i] = position in groupedInt corresponding to having consumed i digits of intPart
        val prefixMap = IntArray(intPart.length + 1)
        var gi = 0
        for (ii in intPart.indices) {
            while (gi < groupedInt.length && groupedInt[gi] == ',') gi++
            gi++
            prefixMap[ii + 1] = gi
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val o = offset.coerceIn(0, raw.length)
                return if (o <= intPart.length) prefixMap[o] else groupedInt.length + (o - intPart.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val o = offset.coerceIn(0, transformed.length)
                if (o > groupedInt.length) return intPart.length + (o - groupedInt.length)
                var lo = 0
                var hi = intPart.length
                while (lo < hi) {
                    val mid = (lo + hi + 1) / 2
                    if (prefixMap[mid] <= o) lo = mid else hi = mid - 1
                }
                return lo
            }
        }

        return TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}

/** Groups a plain digit string (no parsing to number, so leading zeros mid-typing are preserved). */
private fun groupDigits(digits: String, indianGrouping: Boolean): String {
    if (digits.length <= 3) return digits
    return if (indianGrouping) {
        val lastThree = digits.takeLast(3)
        var remaining = digits.dropLast(3)
        val groups = ArrayDeque<String>()
        while (remaining.length > 2) {
            groups.addFirst(remaining.takeLast(2))
            remaining = remaining.dropLast(2)
        }
        groups.addFirst(remaining)
        groups.joinToString(",") + "," + lastThree
    } else {
        val groups = ArrayDeque<String>()
        var remaining = digits
        while (remaining.length > 3) {
            groups.addFirst(remaining.takeLast(3))
            remaining = remaining.dropLast(3)
        }
        groups.addFirst(remaining)
        groups.joinToString(",")
    }
}
