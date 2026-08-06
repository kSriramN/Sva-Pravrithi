package com.svapravrithi.app.ui.theme

import androidx.compose.runtime.compositionLocalOf
import com.svapravrithi.app.domain.model.Currency

/**
 * The app-wide selected currency (Profile > Currency), provided once at the root
 * (see MainActivity) and read anywhere a monetary amount is displayed via
 * `LocalCurrency.current`. Using compositionLocalOf (not staticCompositionLocalOf)
 * since the value does change at runtime and consumers need to recompose correctly
 * when it does.
 */
val LocalCurrency = compositionLocalOf { Currency.INR }
