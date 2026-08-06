package com.svapravrithi.app.domain.model

import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Formats an amount for the given currency, rounded to the nearest whole unit.
 * INR uses Indian digit grouping (lakh/crore: 1,00,000); every other currency uses
 * standard international 3-digit grouping (1,000,000).
 *
 * Deliberately NOT using String.format("%,.0f", ...) or java.text.NumberFormat with a
 * locale: both depend on the device's runtime locale/ICU data to decide whether (and
 * how) to group digits, which is inconsistent across devices. This is a fully
 * self-contained algorithm with no external dependency, so behavior is identical on
 * every device.
 */
fun formatAmount(amount: Double, currency: Currency = Currency.INR): String {
    val rounded = amount.roundToLong()
    val isNegative = rounded < 0
    val absDigits = abs(rounded).toString()

    val grouped = if (absDigits.length <= 3) {
        absDigits
    } else if (currency.useIndianGrouping) {
        val lastThree = absDigits.takeLast(3)
        var remaining = absDigits.dropLast(3)
        val groups = ArrayDeque<String>()
        while (remaining.length > 2) {
            groups.addFirst(remaining.takeLast(2))
            remaining = remaining.dropLast(2)
        }
        groups.addFirst(remaining)
        groups.joinToString(",") + "," + lastThree
    } else {
        val groups = ArrayDeque<String>()
        var remaining = absDigits
        while (remaining.length > 3) {
            groups.addFirst(remaining.takeLast(3))
            remaining = remaining.dropLast(3)
        }
        groups.addFirst(remaining)
        groups.joinToString(",")
    }

    return if (isNegative) "-$grouped" else grouped
}
