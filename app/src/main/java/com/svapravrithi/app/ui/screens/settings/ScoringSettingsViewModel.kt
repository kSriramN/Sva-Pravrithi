package com.svapravrithi.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svapravrithi.app.data.repository.ScoringConfigRepository
import com.svapravrithi.app.domain.engine.ScoringConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScoringSettingsUiState(
    val baseScore: String = "",
    val wantsDeductionDivisor: String = "",
    val pleasureDeductionDivisor: String = "",
    val savingsBonusMultiplier: String = "",
    val savingsPenaltyMultiplier: String = "",
    val isSaving: Boolean = false,
    val saved: Boolean = false,
)

@HiltViewModel
class ScoringSettingsViewModel @Inject constructor(
    private val repository: ScoringConfigRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScoringSettingsUiState())
    val uiState: StateFlow<ScoringSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val config = repository.get()
            _uiState.value = ScoringSettingsUiState(
                baseScore = config.baseScore.toPlain(),
                wantsDeductionDivisor = config.wantsDeductionDivisor.toPlain(),
                pleasureDeductionDivisor = config.pleasureDeductionDivisor.toPlain(),
                savingsBonusMultiplier = config.savingsBonusMultiplier.toPlain(),
                savingsPenaltyMultiplier = config.savingsPenaltyMultiplier.toPlain(),
            )
        }
    }

    fun onBaseScoreChange(v: String) { _uiState.value = _uiState.value.copy(baseScore = v, saved = false) }
    fun onWantsDivisorChange(v: String) { _uiState.value = _uiState.value.copy(wantsDeductionDivisor = v, saved = false) }
    fun onPleasureDivisorChange(v: String) { _uiState.value = _uiState.value.copy(pleasureDeductionDivisor = v, saved = false) }
    fun onSavingsBonusChange(v: String) { _uiState.value = _uiState.value.copy(savingsBonusMultiplier = v, saved = false) }
    fun onSavingsPenaltyChange(v: String) { _uiState.value = _uiState.value.copy(savingsPenaltyMultiplier = v, saved = false) }

    fun resetToDefaults() {
        val d = ScoringConfig()
        _uiState.value = _uiState.value.copy(
            baseScore = d.baseScore.toPlain(),
            wantsDeductionDivisor = d.wantsDeductionDivisor.toPlain(),
            pleasureDeductionDivisor = d.pleasureDeductionDivisor.toPlain(),
            savingsBonusMultiplier = d.savingsBonusMultiplier.toPlain(),
            savingsPenaltyMultiplier = d.savingsPenaltyMultiplier.toPlain(),
            saved = false,
        )
    }

    fun save() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)
            repository.save(
                ScoringConfig(
                    baseScore = state.baseScore.toDoubleOrNull() ?: 50.0,
                    wantsDeductionDivisor = state.wantsDeductionDivisor.toDoubleOrNull() ?: 2.0,
                    pleasureDeductionDivisor = state.pleasureDeductionDivisor.toDoubleOrNull() ?: 2.0,
                    savingsBonusMultiplier = state.savingsBonusMultiplier.toDoubleOrNull() ?: 1.0,
                    savingsPenaltyMultiplier = state.savingsPenaltyMultiplier.toDoubleOrNull() ?: 1.0,
                ),
            )
            _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
        }
    }
}

private fun Double.toPlain(): String = if (this == this.toLong().toDouble()) this.toLong().toString() else this.toString()
