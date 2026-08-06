package com.svapravrithi.app.ui.screens.backup

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.svapravrithi.app.data.backup.BackupJsonMapper
import com.svapravrithi.app.data.backup.BackupSnapshot
import com.svapravrithi.app.data.backup.GoogleDriveBackupService
import com.svapravrithi.app.data.local.entity.ScoringConfigEntity
import com.svapravrithi.app.data.repository.DeclarationRepository
import com.svapravrithi.app.data.repository.ExpenseRepository
import com.svapravrithi.app.data.repository.PlanRepository
import com.svapravrithi.app.data.repository.ScoringConfigRepository
import com.svapravrithi.app.domain.engine.ScoringConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class BackupStatus { IDLE, WORKING, SUCCESS, ERROR }

data class BackupUiState(
    val account: GoogleSignInAccount? = null,
    val status: BackupStatus = BackupStatus.IDLE,
    val message: String? = null,
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val driveService: GoogleDriveBackupService,
    private val expenseRepository: ExpenseRepository,
    private val planRepository: PlanRepository,
    private val declarationRepository: DeclarationRepository,
    private val scoringConfigRepository: ScoringConfigRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState(account = driveService.lastSignedInAccount()))
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    /** Launch this Intent via `rememberLauncherForActivityResult` in the screen. */
    fun signInIntent(): Intent = driveService.signInClient.signInIntent

    fun onSignInResult(account: GoogleSignInAccount?) {
        _uiState.value = _uiState.value.copy(account = account)
    }

    fun onSignInError(e: com.google.android.gms.common.api.ApiException) {
        val friendly = when (e.statusCode) {
            10 -> "Google sign-in isn't configured for this app build yet. See the README's " +
                "\"Enable Google Drive backup\" section \u2014 it needs a one-time Google Cloud " +
                "OAuth setup (status: DEVELOPER_ERROR)."
            12501 -> null // user cancelled the sign-in dialog - not an error worth showing
            else -> "Google sign-in failed (status ${e.statusCode}). ${e.message ?: ""}".trim()
        }
        if (friendly != null) {
            _uiState.value = _uiState.value.copy(status = BackupStatus.ERROR, message = friendly)
        }
    }

    fun signOut() {
        driveService.signInClient.signOut()
        _uiState.value = BackupUiState()
    }

    fun exportNow() {
        val account = _uiState.value.account ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = BackupStatus.WORKING, message = null)
            val config = scoringConfigRepository.get()
            val snapshot = BackupSnapshot(
                exportedAtMillis = System.currentTimeMillis(),
                expenses = expenseRepository.getAllOnce(),
                plans = planRepository.getAllOnce(),
                declarations = declarationRepository.getAllOnce(),
                scoringConfig = ScoringConfigEntity(
                    baseScore = config.baseScore,
                    wantsDeductionDivisor = config.wantsDeductionDivisor,
                    pleasureDeductionDivisor = config.pleasureDeductionDivisor,
                    savingsBonusMultiplier = config.savingsBonusMultiplier,
                    savingsPenaltyMultiplier = config.savingsPenaltyMultiplier,
                ),
            )
            val json = BackupJsonMapper.toJson(snapshot)
            val result = driveService.upload(account, json)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(status = BackupStatus.SUCCESS, message = "Backed up to Google Drive.")
            } else {
                _uiState.value.copy(status = BackupStatus.ERROR, message = result.exceptionOrNull()?.message ?: "Export failed.")
            }
        }
    }

    fun importNow() {
        val account = _uiState.value.account ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = BackupStatus.WORKING, message = null)
            val result = driveService.download(account)
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(status = BackupStatus.ERROR, message = result.exceptionOrNull()?.message ?: "Import failed.")
                return@launch
            }
            val jsonText = result.getOrNull()
            if (jsonText == null) {
                _uiState.value = _uiState.value.copy(status = BackupStatus.ERROR, message = "No backup found on Google Drive yet.")
                return@launch
            }
            val snapshot = BackupJsonMapper.fromJson(jsonText)
            expenseRepository.replaceAll(snapshot.expenses)
            planRepository.replaceAll(snapshot.plans)
            declarationRepository.replaceAll(snapshot.declarations)
            snapshot.scoringConfig?.let {
                scoringConfigRepository.save(
                    ScoringConfig(
                        baseScore = it.baseScore,
                        wantsDeductionDivisor = it.wantsDeductionDivisor,
                        pleasureDeductionDivisor = it.pleasureDeductionDivisor,
                        savingsBonusMultiplier = it.savingsBonusMultiplier,
                        savingsPenaltyMultiplier = it.savingsPenaltyMultiplier,
                    ),
                )
            }
            _uiState.value = _uiState.value.copy(status = BackupStatus.SUCCESS, message = "Restored from Google Drive backup.")
        }
    }
}
