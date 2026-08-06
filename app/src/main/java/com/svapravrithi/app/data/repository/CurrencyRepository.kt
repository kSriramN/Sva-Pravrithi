package com.svapravrithi.app.data.repository

import android.content.SharedPreferences
import com.svapravrithi.app.domain.model.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val KEY_CURRENCY_CODE = "selected_currency_code"

@Singleton
class CurrencyRepository @Inject constructor(private val prefs: SharedPreferences) {

    private val _currency = MutableStateFlow(loadFromPrefs())
    val currency: StateFlow<Currency> = _currency.asStateFlow()

    fun setCurrency(currency: Currency) {
        prefs.edit().putString(KEY_CURRENCY_CODE, currency.code).apply()
        _currency.value = currency
    }

    private fun loadFromPrefs(): Currency {
        val savedCode = prefs.getString(KEY_CURRENCY_CODE, null) ?: return Currency.INR
        return Currency.entries.find { it.code == savedCode } ?: Currency.INR
    }
}
