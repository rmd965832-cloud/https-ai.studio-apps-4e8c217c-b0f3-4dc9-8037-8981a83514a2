package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.SongItem
import com.example.ui.NavigationEvent
import com.example.ui.VoiceAssistantViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.WhatsAppHelper

sealed class ScreenDestination {
    data object Home : ScreenDestination()
    data class Search(val shareMode: Boolean = false) : ScreenDestination()
    data class Player(val song: SongItem) : ScreenDestination()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                VoiceAssistantApp()
            }
        }
    }
}

@Composable
fun VoiceAssistantApp(viewModel: VoiceAssistantViewModel = viewModel()) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf<ScreenDestination>(ScreenDestination.Home) }

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToSearch -> {
                    currentScreen = ScreenDestination.Search(shareMode = event.shareMode)
                }
                is NavigationEvent.NavigateToPlayer -> {
                    currentScreen = ScreenDestination.Player(song = event.song)
                }
                is NavigationEvent.TriggerWhatsAppShare -> {
                    WhatsAppHelper.shareTextToWhatsApp(context, event.song.youtubeShareText)
                }
            }
        }
    }

    BackHandler(enabled = currentScreen !is ScreenDestination.Home) {
        currentScreen = when (currentScreen) {
            is ScreenDestination.Player -> ScreenDestination.Search(shareMode = false)
            is ScreenDestination.Search -> ScreenDestination.Home
            ScreenDestination.Home -> ScreenDestination.Home
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                        (slideOutHorizontally { width -> -width } + fadeOut())
            },
            label = "screen_transition",
            modifier = Modifier.padding(innerPadding)
        ) { screen ->
            when (screen) {
                is ScreenDestination.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToSearch = { shareMode ->
                            currentScreen = ScreenDestination.Search(shareMode = shareMode)
                        }
                    )
                }
                is ScreenDestination.Search -> {
                    SearchScreen(
                        viewModel = viewModel,
                        initialShareMode = screen.shareMode,
                        onNavigateBack = {
                            currentScreen = ScreenDestination.Home
                        },
                        onPlaySong = { song ->
                            viewModel.playSong(song)
                            currentScreen = ScreenDestination.Player(song)
                        },
                        onShareSong = { song ->
                            WhatsAppHelper.shareTextToWhatsApp(context, song.youtubeShareText)
                        }
                    )
                }
                is ScreenDestination.Player -> {
                    PlayerScreen(
                        song = screen.song,
                        viewModel = viewModel,
                        onNavigateBack = {
                            currentScreen = ScreenDestination.Search(shareMode = false)
                        },
                        onShareToWhatsApp = { song ->
                            WhatsAppHelper.shareTextToWhatsApp(context, song.youtubeShareText)
                        },
                        onPlaySong = { newSong ->
                            viewModel.playSong(newSong)
                            currentScreen = ScreenDestination.Player(newSong)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
