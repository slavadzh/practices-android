package com.example.practices_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.practices_android.ui.navigation.AppRoot
import com.example.practices_android.ui.theme.PracticesandroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticesandroidTheme {
                AppRoot()
            }
        }
    }
}
