package com.example.partware

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.partware.ui.theme.PartWareTheme
import com.example.partware.ui.parts.PartsRoute

/** App entry point. Hosts the Compose UI; inventory logic lives in PartsViewModel. */
class MainActivity : ComponentActivity() {
    /** Sets up edge-to-edge rendering and opens the parts screen using the app theme. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PartWareTheme {
                PartsRoute()
            }
        }
    }
}
