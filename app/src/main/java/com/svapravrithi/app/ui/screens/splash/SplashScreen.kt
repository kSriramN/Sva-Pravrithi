package com.svapravrithi.app.ui.screens.splash

import android.content.SharedPreferences
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val prefs: SharedPreferences) : ViewModel() {
    fun hasSeenOnboarding(): Boolean = prefs.getBoolean(KEY_ONBOARDING_SEEN, false)
    companion object { const val KEY_ONBOARDING_SEEN = "onboarding_seen" }
}

@Composable
fun SplashScreen(
    onFinished: (onboardingSeen: Boolean) -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (visible) 1f else 0f, animationSpec = tween(600), label = "splashAlpha")

    LaunchedEffect(Unit) {
        visible = true
        delay(1100)
        onFinished(viewModel.hasSeenOnboarding())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Spa,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp),
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
        Text(
            "Sva-Pravrithi",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
        Text(
            "Spend with Awareness. Live with Balance.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
