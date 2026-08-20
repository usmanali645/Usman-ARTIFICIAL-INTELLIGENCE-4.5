package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.ui.screens.ChatScreen
import com.example.ui.theme.UsmanAiTheme
import com.example.ui.viewmodel.ChatViewModel
import com.example.ui.viewmodel.ChatViewModelFactory

class MainActivity : ComponentActivity() {
    private val chatViewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UsmanAiTheme {
                ChatScreen(
                    viewModel = chatViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
