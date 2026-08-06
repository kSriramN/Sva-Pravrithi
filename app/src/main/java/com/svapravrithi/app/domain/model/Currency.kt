package com.svapravrithi.app.domain.model

/**
 * Supported currencies. INR is the default (matches the app's original design), with
 * Indian lakh/crore digit grouping; everything else uses standard international
 * 3-digit grouping.
 */
enum class Currency(val code: String, val symbol: String, val displayName: String, val useIndianGrouping: Boolean) {
    INR("INR", "\u20b9", "Indian Rupee", true),
    USD("USD", "$", "US Dollar", false),
    EUR("EUR", "\u20ac", "Euro", false),
    GBP("GBP", "\u00a3", "British Pound", false),
    JPY("JPY", "\u00a5", "Japanese Yen", false),
    AUD("AUD", "A$", "Australian Dollar", false),
    CAD("CAD", "C$", "Canadian Dollar", false),
    SGD("SGD", "S$", "Singapore Dollar", false),
}
