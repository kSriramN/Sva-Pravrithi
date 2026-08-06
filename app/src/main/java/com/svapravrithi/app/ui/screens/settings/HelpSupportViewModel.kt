package com.svapravrithi.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svapravrithi.app.data.local.entity.FaqEntity
import com.svapravrithi.app.data.repository.FaqRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

const val SUPPORT_EMAIL = "nagaraju.sagar007@gmail.com"

@HiltViewModel
class HelpSupportViewModel @Inject constructor(
    private val repository: FaqRepository,
) : ViewModel() {

    val faqs: StateFlow<List<FaqEntity>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { repository.ensureSeeded() }
    }

    fun addFaq(question: String, answer: String) {
        viewModelScope.launch { repository.add(question, answer) }
    }

    fun deleteFaq(faq: FaqEntity) {
        viewModelScope.launch { repository.delete(faq) }
    }
}
