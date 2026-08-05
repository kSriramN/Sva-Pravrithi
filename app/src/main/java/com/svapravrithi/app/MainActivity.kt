package com.svapravrithi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.svapravrithi.app.ui.navigation.SvaNavGraph
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
private fun SvaPravrithiRoot() {
    SvaPravrithiTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SvaNavGraph()
        }
    }
}
