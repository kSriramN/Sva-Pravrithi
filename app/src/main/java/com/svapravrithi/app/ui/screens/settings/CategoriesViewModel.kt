package com.svapravrithi.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svapravrithi.app.data.local.entity.CategoryEntity
import com.svapravrithi.app.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repository: CategoryRepository,
) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { repository.ensureSeeded() }
    }

    fun add(name: String) {
        viewModelScope.launch { repository.add(name) }
    }

    fun delete(category: CategoryEntity) {
        viewModelScope.launch { repository.delete(category) }
    }
}
