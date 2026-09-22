package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.room.AppDatabase
import com.example.data.room.FavoriteSongEntity
import com.example.data.room.RecentSearchEntity
import com.example.data.room.VoiceAssistantRepository
import com.example.model.IntentAction
import com.example.model.SongItem
import com.example.model.VoiceIntent
import com.example.model.VoiceLanguage
import com.example.network.SearchResult
import com.example.network.YouTubeSearchRepository
import com.example.util.VoiceIntentParser
import com.example.voice.TtsManager
import com.example.voice.VoiceRecognitionManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class NavigationEvent {
    data class NavigateToSearch(val autoQuery: String, val shareMode: Boolean) : NavigationEvent()
    data class NavigateToPlayer(val song: SongItem) : NavigationEvent()
    data class TriggerWhatsAppShare(val song: SongItem) : NavigationEvent()
}

class VoiceAssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val voiceManager = VoiceRecognitionManager(application)
    private val ttsManager = TtsManager(application)
    private val searchRepository = YouTubeSearchRepository(application)
    private val db = AppDatabase.getDatabase(application)
    private val assistantRepository = VoiceAssistantRepository(db.voiceAssistantDao())

    val isListening: StateFlow<Boolean> = voiceManager.isListening
    val transcript: StateFlow<String> = voiceManager.transcript
    val rmsLevel: StateFlow<Float> = voiceManager.rmsDb
    val voiceError: StateFlow<String?> = voiceManager.errorMessage

    val isSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking
    val isTtsEnabled: StateFlow<Boolean> = ttsManager.isTtsEnabled
    val speechRate: StateFlow<Float> = ttsManager.speechRate
    val lastSpokenText: StateFlow<String> = ttsManager.lastSpokenText

    private val _selectedLanguage = MutableStateFlow(VoiceLanguage.ASSAMESE)
    val selectedLanguage: StateFlow<VoiceLanguage> = _selectedLanguage.asStateFlow()

    private val _detectedIntent = MutableStateFlow<VoiceIntent?>(null)
    val detectedIntent: StateFlow<VoiceIntent?> = _detectedIntent.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SongItem>>(emptyList())
    val searchResults: StateFlow<List<SongItem>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isShareMode = MutableStateFlow(false)
    val isShareMode: StateFlow<Boolean> = _isShareMode.asStateFlow()

    private val _isFromLiveApi = MutableStateFlow(false)
    val isFromLiveApi: StateFlow<Boolean> = _isFromLiveApi.asStateFlow()

    private val _currentPlayingSong = MutableStateFlow<SongItem?>(null)
    val currentPlayingSong: StateFlow<SongItem?> = _currentPlayingSong.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    val recentSearches: StateFlow<List<RecentSearchEntity>> = assistantRepository.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteSongs: StateFlow<List<FavoriteSongEntity>> = assistantRepository.favoriteSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Load initial curated songs so screen is lively
        searchSongs(query = "", shareMode = false)

        // Listen for voice transcript updates
        viewModelScope.launch {
            voiceManager.transcript.collect { text ->
                if (text.isNotBlank()) {
                    handleVoiceTranscript(text)
                }
            }
        }
    }

    fun setLanguage(language: VoiceLanguage) {
        _selectedLanguage.value = language
    }

    fun startListening() {
        voiceManager.startListening(_selectedLanguage.value)
    }

    fun stopListening() {
        voiceManager.stopListening()
    }

    fun clearVoiceError() {
        voiceManager.clearError()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setShareMode(enabled: Boolean) {
        _isShareMode.value = enabled
    }

    fun handleVoiceTranscript(rawText: String) {
        val intent = VoiceIntentParser.parse(rawText)
        _detectedIntent.value = intent

        val query = intent.query.ifBlank { rawText }
        _searchQuery.value = query

        // Persist to Room
        viewModelScope.launch {
            assistantRepository.saveSearch(
                query = query,
                intentAction = intent.action.name,
                languageCode = _selectedLanguage.value.localeTag
            )
        }

        // Speak the assistant's voice response back to the user
        ttsManager.speakIntentResponse(intent, _selectedLanguage.value)

        when (intent.action) {
            IntentAction.SEND_WHATSAPP -> {
                _isShareMode.value = true
                viewModelScope.launch {
                    searchSongs(query, shareMode = true)
                    _navigationEvents.emit(NavigationEvent.NavigateToSearch(autoQuery = query, shareMode = true))
                }
            }
            IntentAction.PLAY_SONG -> {
                _isShareMode.value = false
                viewModelScope.launch {
                    searchSongs(query, shareMode = false)
                    // If results exist, can play top song directly or navigate to search
                    _navigationEvents.emit(NavigationEvent.NavigateToSearch(autoQuery = query, shareMode = false))
                }
            }
            IntentAction.SEARCH_ONLY -> {
                viewModelScope.launch {
                    searchSongs(query, shareMode = _isShareMode.value)
                    _navigationEvents.emit(NavigationEvent.NavigateToSearch(autoQuery = query, shareMode = _isShareMode.value))
                }
            }
        }
    }

    fun searchSongs(query: String = _searchQuery.value, shareMode: Boolean = _isShareMode.value) {
        _isShareMode.value = shareMode
        _isLoading.value = true
        viewModelScope.launch {
            val result = searchRepository.searchSongs(query)
            when (result) {
                is SearchResult.Success -> {
                    _searchResults.value = result.songs
                    _isFromLiveApi.value = result.isFromLiveApi
                }
                is SearchResult.Error -> {
                    _searchResults.value = YouTubeSearchRepository.ALL_CURATED_SONGS
                    _isFromLiveApi.value = false
                }
            }
            _isLoading.value = false
        }
    }

    fun selectSong(song: SongItem) {
        if (_isShareMode.value) {
            viewModelScope.launch {
                _navigationEvents.emit(NavigationEvent.TriggerWhatsAppShare(song))
            }
        } else {
            playSong(song)
        }
    }

    fun playSong(song: SongItem) {
        _currentPlayingSong.value = song
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToPlayer(song))
        }
    }

    fun toggleFavorite(song: SongItem) {
        viewModelScope.launch {
            val isFav = favoriteSongs.value.any { it.videoId == song.videoId }
            assistantRepository.toggleFavorite(song, isFav)
        }
    }

    fun isSongFavorite(videoId: String): Boolean {
        return favoriteSongs.value.any { it.videoId == videoId }
    }

    fun deleteRecentSearch(id: Long) {
        viewModelScope.launch {
            assistantRepository.deleteSearch(id)
        }
    }

    fun clearAllSearches() {
        viewModelScope.launch {
            assistantRepository.clearAllSearches()
        }
    }

    fun getCustomApiKey(): String = searchRepository.getCustomApiKey()

    fun saveCustomApiKey(key: String) {
        searchRepository.saveCustomApiKey(key)
    }

    fun speakText(text: String) {
        ttsManager.speak(text, _selectedLanguage.value)
    }

    fun speakGreeting() {
        ttsManager.speakGreeting(_selectedLanguage.value)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun setTtsEnabled(enabled: Boolean) {
        ttsManager.setTtsEnabled(enabled)
    }

    fun setSpeechRate(rate: Float) {
        ttsManager.setSpeechRate(rate)
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.stopListening()
        ttsManager.shutdown()
    }
}
