package com.svapravrithi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.ui.navigation.SvaNavGraph
import com.svapravrithi.app.ui.screens.settings.CurrencyViewModel
import com.svapravrithi.app.ui.theme.LocalCurrency
import com.svapravrithi.app.ui.theme.SvaPravrithiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SvaPravrithiRoot()
        }
    }
}

@Composable
private fun SvaPravrithiRoot(currencyViewModel: CurrencyViewModel = hiltViewModel()) {
    val currency by currencyViewModel.currency.collectAsState()

    SvaPravrithiTheme {
        CompositionLocalProvider(LocalCurrency provides currency) {
            Surface(modifier = Modifier.fillMaxSize()) {
                SvaNavGraph()
            }
        }
    }
}
