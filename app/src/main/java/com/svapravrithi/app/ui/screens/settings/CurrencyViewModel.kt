package com.svapravrithi.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.svapravrithi.app.data.repository.CurrencyRepository
import com.svapravrithi.app.domain.model.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository,
) : ViewModel() {

    val currency: StateFlow<Currency> = repository.currency

    fun setCurrency(currency: Currency) {
        repository.setCurrency(currency)
    }
}
