package com.vibesync.app.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vibesync.app.TubeAudioApplication
import com.vibesync.app.data.ADHOC_QUEUE_ID
import com.vibesync.app.data.DEFAULT_PLAYLIST_NAME
import com.vibesync.app.data.DOWNLOADS_QUEUE_ID
import com.vibesync.app.data.LIKED_QUEUE_ID
import com.vibesync.app.data.PlaylistPortability
import com.vibesync.app.data.StorageInfo
import com.vibesync.app.data.StorageStats
import com.vibesync.app.data.auth.AppUser
import com.vibesync.app.data.auth.AuthResult
import com.vibesync.app.data.db.PlaylistEntity
import com.vibesync.app.data.db.TrackEntity
import com.vibesync.app.data.party.CreatePartyResult
import com.vibesync.app.data.party.JoinPartyResult
import com.vibesync.app.data.party.PartyActionResult
import com.vibesync.app.data.party.PartyDoc
import com.vibesync.app.data.party.PartyMember
import com.vibesync.app.data.party.PartyQueueItem
import com.vibesync.app.data.party.QueueItemStatus
import com.vibesync.app.data.localparty.DiscoveredRoom
import com.vibesync.app.data.localparty.LocalQueueItem
import com.vibesync.app.data.localparty.LocalRoomState
import com.vibesync.app.data.social.Friend
import com.vibesync.app.data.social.PartyInvite
import com.vibesync.app.data.social.SocialActionResult
import com.vibesync.app.data.social.UserProfile
import com.vibesync.app.download.DownloadState
import com.vibesync.app.localparty.LocalPartyClient
import com.vibesync.app.localparty.LocalPartyDiscovery
import com.vibesync.app.localparty.LocalPartyHost
import com.vibesync.app.extractor.AudioResolutionException
import com.vibesync.app.extractor.AudioStreamResolver
import com.vibesync.app.extractor.SearchException
import com.vibesync.app.extractor.SearchResult
import com.vibesync.app.extractor.TrendingFilter
import com.vibesync.app.extractor.TrendingFilters
import com.vibesync.app.extractor.YouTubeSearcher
import com.vibesync.app.extractor.toTransientTrackEntity
import com.vibesync.app.lyrics.LyricsResult
import com.vibesync.app.playback.EqualizerPreset
import com.vibesync.app.playback.EqualizerUiState
import com.vibesync.app.playback.PlaybackUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as TubeAudioApplication).container
    private val libraryRepository = container.libraryRepository
    private val playerController = container.playerController
    private val trackDownloader = container.trackDownloader
    private val searchHistory = container.searchHistoryPreferences

    val playbackState: StateFlow<PlaybackUiState> = playerController.uiState

    val playlists: StateFlow<List<PlaylistEntity>> = libraryRepository.playlists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recentPlaylists: StateFlow<List<PlaylistEntity>> = libraryRepository.recentPlaylists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val downloadStates: StateFlow<Map<String, DownloadState>> = trackDownloader.downloadStates

    val downloadedTracks: StateFlow<List<TrackEntity>> = libraryRepository.downloadedTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val likedTracks: StateFlow<List<TrackEntity>> = libraryRepository.likedTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val likedVideoIds: StateFlow<Set<String>> = likedTracks
        .map { list -> list.mapTo(mutableSetOf()) { it.videoId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    fun toggleLike(videoId: String) {
        viewModelScope.launch { libraryRepository.toggleLike(videoId) }
    }

    fun toggleLikeSearchResult(result: SearchResult) {
        viewModelScope.launch { libraryRepository.toggleLikeFromSearchResult(result) }
    }

    /** Downloads tab content: completed downloads plus anything still downloading/paused (which
     *  isn't marked `isDownloaded` in the DB yet), so an in-progress item shows up immediately
     *  with its live progress ring instead of only appearing once it finishes. */
    val downloadsScreenTracks: StateFlow<List<TrackEntity>> = combine(
        libraryRepository.downloadedTracks,
        trackDownloader.activeDownloadTracks
    ) { downloaded, active ->
        val downloadedIds = downloaded.mapTo(mutableSetOf()) { it.videoId }
        downloaded + active.values.filter { it.videoId !in downloadedIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Null when nothing in the playlist is actively downloading (so the UI shows a plain
     *  download icon); otherwise the fraction of the playlist that's downloaded/in-progress. */
    fun playlistDownloadProgress(playlistId: Long): Flow<Float?> = combine(
        libraryRepository.observeTracksForPlaylist(playlistId),
        trackDownloader.downloadStates
    ) { tracks, states ->
        if (tracks.isEmpty() || tracks.none { states.containsKey(it.videoId) }) return@combine null
        val progressSum = tracks.sumOf { track ->
            when {
                track.isDownloaded -> 1.0
                else -> when (val state = states[track.videoId]) {
                    is DownloadState.Downloading -> state.fraction.toDouble()
                    is DownloadState.Paused -> state.fraction.toDouble()
                    null -> 0.0
                }
            }
        }
        (progressSum / tracks.size).toFloat()
    }

    val defaultPlaylistId: StateFlow<Long?> = playlists
        .map { list -> list.firstOrNull { it.isDefault }?.id }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _selectedPlaylistId = MutableStateFlow<Long?>(null)
    val selectedPlaylistId: StateFlow<Long?> = _selectedPlaylistId.asStateFlow()

    /** Which playlist (if any) the Library tab has pushed open into its detail screen -- lifted
     *  up here (rather than local Compose state in LibraryScreen) so other tabs, like the
     *  Dashboard's "Your Playlists" rows, can deep-link straight into a playlist's detail view. */
    private val _openPlaylistId = MutableStateFlow<Long?>(null)
    val openPlaylistId: StateFlow<Long?> = _openPlaylistId.asStateFlow()

    fun openPlaylist(playlistId: Long?) {
        _openPlaylistId.value = playlistId
    }

    private val _showLikedSongs = MutableStateFlow(false)
    val showLikedSongs: StateFlow<Boolean> = _showLikedSongs.asStateFlow()

    fun openLikedSongs() {
        _showLikedSongs.value = true
    }

    fun closeLikedSongs() {
        _showLikedSongs.value = false
    }

    val selectedPlaylistTracks: StateFlow<List<TrackEntity>> = _selectedPlaylistId
        .filterNotNull()
        .flatMapLatest { libraryRepository.observeTracksForPlaylist(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _linkInput = MutableStateFlow("")
    val linkInput: StateFlow<String> = _linkInput.asStateFlow()

    private val _isResolving = MutableStateFlow(false)
    val isResolving: StateFlow<Boolean> = _isResolving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<TrackEntity>> = _searchQuery
        .debounce(200)
        .flatMapLatest { query -> if (query.isBlank()) flowOf(emptyList()) else libraryRepository.searchTracks(query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onSearchQueryChanged(value: String) {
        _searchQuery.value = value
    }

    /** Plays a track in the context of the Default playlist (every track lives there) --
     *  used anywhere a track is shown outside its "home" playlist context: local library
     *  search results and the Downloads tab, so skip-next/autoplay still behave normally. */
    fun playFromDefault(track: TrackEntity) {
        viewModelScope.launch {
            val defaultId = libraryRepository.ensureDefaultPlaylist().id
            val tracks = libraryRepository.getTracksForPlaylistOnce(defaultId)
            val index = tracks.indexOfFirst { it.videoId == track.videoId }
            if (index >= 0) startQueue(defaultId, tracks, index, DEFAULT_PLAYLIST_NAME)
        }
    }

    // -- YouTube search (discover new songs by title, distinct from the library search above) --

    private val _youtubeSearchQuery = MutableStateFlow("")
    val youtubeSearchQuery: StateFlow<String> = _youtubeSearchQuery.asStateFlow()

    private val _youtubeSearchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val youtubeSearchResults: StateFlow<List<SearchResult>> = _youtubeSearchResults.asStateFlow()

    private val _isSearchingYoutube = MutableStateFlow(false)
    val isSearchingYoutube: StateFlow<Boolean> = _isSearchingYoutube.asStateFlow()

    /** Video ids added to the library during the current search session, so a result's button
     *  can flip from "+" to a checkmark without a round trip through the playlists Flow. */
    private val _addedVideoIds = MutableStateFlow<Set<String>>(emptySet())
    val addedVideoIds: StateFlow<Set<String>> = _addedVideoIds.asStateFlow()

    val recentSearches: StateFlow<List<String>> = searchHistory.recentQueries

    private var youtubeLiveSearchJob: Job? = null

    /** Live search: fires automatically ~400ms after typing stops, same debounce-then-fetch shape
     *  as the app's local-library search (`searchResults`) already uses, just done manually here
     *  since YouTube search results are populated imperatively (network call + history-write side
     *  effects) rather than as a pure `flatMapLatest` chain. [searchFor]/[submitYoutubeSearch]
     *  (deliberate taps -- a recent-search chip, an artist chip, the IME search action) stay
     *  immediate and don't wait for this debounce. */
    fun onYoutubeSearchQueryChanged(value: String) {
        _youtubeSearchQuery.value = value
        youtubeLiveSearchJob?.cancel()
        if (value.isBlank()) return
        youtubeLiveSearchJob = viewModelScope.launch {
            delay(400)
            runSearch(value)
        }
    }

    fun submitYoutubeSearch() {
        val query = _youtubeSearchQuery.value
        if (query.isBlank() || _isSearchingYoutube.value) return
        runSearch(query)
    }

    fun searchFor(query: String) {
        _youtubeSearchQuery.value = query
        runSearch(query)
    }

    fun removeRecentSearch(query: String) = searchHistory.removeQuery(query)

    private fun runSearch(query: String) {
        if (_isSearchingYoutube.value) return
        _isSearchingYoutube.value = true
        _errorMessage.value = null
        _addedVideoIds.value = emptySet()
        viewModelScope.launch {
            try {
                val results = withContext(Dispatchers.IO) { YouTubeSearcher.search(query) }
                _youtubeSearchResults.value = results
                searchHistory.addQuery(query)
                if (results.isEmpty()) _errorMessage.value = "No songs found for \"$query\"."
            } catch (e: SearchException) {
                _errorMessage.value = e.message
            } catch (e: Exception) {
                _errorMessage.value = "Search failed: ${e.message ?: "unknown error"}"
            } finally {
                _isSearchingYoutube.value = false
            }
        }
    }

    /** Saves the song to the library (it always lands in "All songs") and returns the resulting
     *  track so the caller can immediately open the "Add to playlist" picker on it -- tapping
     *  "+" on a search/trending result asks which playlist, rather than silently only adding to
     *  the default one. */
    suspend fun addSearchResultAndReturnTrack(result: SearchResult): TrackEntity? {
        if (_addedVideoIds.value.contains(result.videoId)) return null
        return try {
            val track = libraryRepository.addSearchResultToDefault(result)
            _addedVideoIds.update { it + result.videoId }
            track
        } catch (e: Exception) {
            _errorMessage.value = "Couldn't add \"${result.title}\": ${e.message ?: "unknown error"}"
            null
        }
    }

    /** Plays a search/trending result immediately, without saving it to the library first --
     *  the track only gets an ad-hoc, non-resumable one-item queue (see [ADHOC_QUEUE_ID]).
     *  Liking or "+"-adding it separately is what actually saves it. */
    fun playSearchResultDirectly(result: SearchResult) {
        val transientTrack = TrackEntity(
            videoId = result.videoId,
            url = result.url,
            title = result.title,
            thumbnailUrl = result.thumbnailUrl,
            durationSeconds = result.durationSeconds,
            addedAtEpochMillis = System.currentTimeMillis()
        )
        playerController.playQueue(ADHOC_QUEUE_ID, listOf(transientTrack), 0, "Search")
    }

    /** "Add to queue" from a search/trending result row -- appends after whatever's currently
     *  playing without saving the track or disturbing playback. */
    fun addSearchResultToQueue(result: SearchResult) {
        if (queuedVideoIds.value.contains(result.videoId)) return
        playerController.addToQueue(result.toTransientTrackEntity())
        _errorMessage.value = "Added \"${result.title}\" to queue"
    }

    /** Starts playing every currently-loaded search result, in order, from the top -- the
     *  Search tab's "Play all" action. */
    fun playSearchResultsFromStart() {
        val results = _youtubeSearchResults.value
        if (results.isEmpty()) return
        playerController.playQueue(ADHOC_QUEUE_ID, results.map { it.toTransientTrackEntity() }, 0, "Search results")
    }

    // -- Connectivity: gates the launch-time Recommended/Trending fetches so offline shows a
    // friendly inline state instead of a generic network-error snackbar. --

    val isOnline: StateFlow<Boolean> = container.networkMonitor.isOnline

    // -- Trending (main "Trending songs" list, always unfiltered) --

    private val _trendingResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val trendingResults: StateFlow<List<SearchResult>> = _trendingResults.asStateFlow()

    private val _isLoadingTrending = MutableStateFlow(false)
    val isLoadingTrending: StateFlow<Boolean> = _isLoadingTrending.asStateFlow()

    fun loadTrendingIfNeeded() {
        if (_trendingResults.value.isNotEmpty() || _isLoadingTrending.value || !isOnline.value) return
        _isLoadingTrending.value = true
        viewModelScope.launch {
            try {
                _trendingResults.value = withContext(Dispatchers.IO) { YouTubeSearcher.allTrending() }
            } catch (e: SearchException) {
                _errorMessage.value = e.message
            } catch (e: Exception) {
                _errorMessage.value = "Couldn't load trending: ${e.message ?: "unknown error"}"
            } finally {
                _isLoadingTrending.value = false
            }
        }
    }

    /** Plays every currently-loaded "Trending songs" result in order -- Trending tab's "Play
     *  all" action. */
    fun playTrendingResultsFromStart() {
        val results = _trendingResults.value
        if (results.isEmpty()) return
        playerController.playQueue(ADHOC_QUEUE_ID, results.map { it.toTransientTrackEntity() }, 0, "Trending")
    }

    // -- Podcasts tab: reuses the same YouTube-search machinery as everything else, just pointed
    // at the general "Videos" filter and sorted by upload date instead of the "Songs" filter --
    // see YouTubeSearcher.podcastSearch's doc for why. Own state (not shared with the main Search
    // tab or party add-song search) so typing here doesn't disturb either of those. --

    private val _podcastQuery = MutableStateFlow("")
    val podcastQuery: StateFlow<String> = _podcastQuery.asStateFlow()

    private val _podcastResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val podcastResults: StateFlow<List<SearchResult>> = _podcastResults.asStateFlow()

    private val _isLoadingPodcasts = MutableStateFlow(false)
    val isLoadingPodcasts: StateFlow<Boolean> = _isLoadingPodcasts.asStateFlow()

    fun loadLatestPodcastsIfNeeded() {
        if (_podcastResults.value.isNotEmpty() || _isLoadingPodcasts.value || !isOnline.value) return
        runPodcastSearch("")
    }

    private var podcastLiveSearchJob: Job? = null

    fun onPodcastQueryChanged(value: String) {
        _podcastQuery.value = value
        podcastLiveSearchJob?.cancel()
        podcastLiveSearchJob = viewModelScope.launch {
            delay(400)
            runPodcastSearch(value)
        }
    }

    fun submitPodcastSearch() {
        podcastLiveSearchJob?.cancel()
        runPodcastSearch(_podcastQuery.value)
    }

    private fun runPodcastSearch(query: String) {
        if (_isLoadingPodcasts.value) return
        _isLoadingPodcasts.value = true
        viewModelScope.launch {
            try {
                _podcastResults.value = withContext(Dispatchers.IO) { YouTubeSearcher.podcastSearch(query) }
            } catch (e: SearchException) {
                _errorMessage.value = e.message
            } catch (e: Exception) {
                _errorMessage.value = "Couldn't load podcasts: ${e.message ?: "unknown error"}"
            } finally {
                _isLoadingPodcasts.value = false
            }
        }
    }

    // -- Trending "Top charts" category sub-screen (a filter card's own filtered list, separate
    // from the always-on "Trending songs" list above) --

    private val _trendingCategory = MutableStateFlow<TrendingFilter?>(null)
    val trendingCategory: StateFlow<TrendingFilter?> = _trendingCategory.asStateFlow()

    private val _categoryResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val categoryResults: StateFlow<List<SearchResult>> = _categoryResults.asStateFlow()

    private val _isLoadingCategory = MutableStateFlow(false)
    val isLoadingCategory: StateFlow<Boolean> = _isLoadingCategory.asStateFlow()

    fun openTrendingCategory(filter: TrendingFilter) {
        _trendingCategory.value = filter
        _categoryResults.value = emptyList()
        _isLoadingCategory.value = true
        viewModelScope.launch {
            try {
                _categoryResults.value = withContext(Dispatchers.IO) { YouTubeSearcher.trending(filter) }
            } catch (e: SearchException) {
                _errorMessage.value = e.message
            } catch (e: Exception) {
                _errorMessage.value = "Couldn't load \"${filter.label}\": ${e.message ?: "unknown error"}"
            } finally {
                _isLoadingCategory.value = false
            }
        }
    }

    fun closeTrendingCategory() {
        _trendingCategory.value = null
    }

    /** Plays every result in the currently-open filtered category, in order -- "respects the
     *  filter you applied" (as opposed to [playTrendingResultsFromStart], which always plays the
     *  unfiltered list). */
    fun playCategoryResultsFromStart() {
        val results = _categoryResults.value
        if (results.isEmpty()) return
        val label = _trendingCategory.value?.label ?: "Trending"
        playerController.playQueue(ADHOC_QUEUE_ID, results.map { it.toTransientTrackEntity() }, 0, label)
    }

    // -- Home "Recommended" section, sourced from onboarding language/genre picks --

    private val _recommendedResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val recommendedResults: StateFlow<List<SearchResult>> = _recommendedResults.asStateFlow()

    private val _isLoadingRecommended = MutableStateFlow(false)
    val isLoadingRecommended: StateFlow<Boolean> = _isLoadingRecommended.asStateFlow()

    fun loadRecommendedIfNeeded() {
        if (_recommendedResults.value.isNotEmpty() || _isLoadingRecommended.value || !isOnline.value) return
        _isLoadingRecommended.value = true
        viewModelScope.launch {
            try {
                val query = container.recommendationPreferences.buildRecommendedQuery()
                _recommendedResults.value = withContext(Dispatchers.IO) { YouTubeSearcher.search(query) }
            } catch (e: SearchException) {
                _errorMessage.value = e.message
            } catch (e: Exception) {
                _errorMessage.value = "Couldn't load recommendations: ${e.message ?: "unknown error"}"
            } finally {
                _isLoadingRecommended.value = false
            }
        }
    }

    /** Jumps to the Search tab with the recommended query pre-run -- "See all" on the Home
     *  screen's Recommended section. */
    fun openFullRecommendedSearch() {
        searchFor(container.recommendationPreferences.buildRecommendedQuery())
        selectTab(AppTab.SEARCH)
    }

    // -- Onboarding (first-launch language/genre picker) --

    val onboardingCompleted: StateFlow<Boolean> = container.recommendationPreferences.onboardingCompleted
    val preferredLanguages: StateFlow<Set<String>> = container.recommendationPreferences.selectedLanguages
    val preferredGenres: StateFlow<Set<String>> = container.recommendationPreferences.selectedGenres

    fun completeOnboarding(languages: Set<String>, genres: Set<String>) {
        container.recommendationPreferences.savePreferences(languages, genres)
        _recommendedResults.value = emptyList()
        loadRecommendedIfNeeded()
    }

    fun skipOnboarding() {
        container.recommendationPreferences.skipOnboarding()
    }

    private val _showPreferencesEditor = MutableStateFlow(false)
    val showPreferencesEditor: StateFlow<Boolean> = _showPreferencesEditor.asStateFlow()

    fun openPreferencesEditor() {
        _showPreferencesEditor.value = true
    }

    fun closePreferencesEditor() {
        _showPreferencesEditor.value = false
    }

    fun savePreferencesFromEditor(languages: Set<String>, genres: Set<String>) {
        completeOnboarding(languages, genres)
        _showPreferencesEditor.value = false
    }

    private val _showAllCategories = MutableStateFlow(false)
    val showAllCategories: StateFlow<Boolean> = _showAllCategories.asStateFlow()

    fun openAllCategories() {
        _showAllCategories.value = true
    }

    fun closeAllCategories() {
        _showAllCategories.value = false
    }

    private val _selectedTab = MutableStateFlow(AppTab.HOME)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
    }

    val themeMode: StateFlow<com.vibesync.app.data.ThemeMode> = container.themePreferences.themeMode
    val accentColorArgb: StateFlow<Int?> = container.themePreferences.accentColorArgb
    val secondaryColorArgb: StateFlow<Int?> = container.themePreferences.secondaryColorArgb

    fun setThemeMode(mode: com.vibesync.app.data.ThemeMode) = container.themePreferences.setThemeMode(mode)
    fun setAccent(primaryArgb: Int?, secondaryArgb: Int? = null) = container.themePreferences.setAccent(primaryArgb, secondaryArgb)

    private val _showSettings = MutableStateFlow(false)
    val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()

    fun openSettings() {
        _showProfile.value = false
        _showSettings.value = true
    }

    fun closeSettings() {
        _showSettings.value = false
    }

    // -- Profile screen (no accounts in this app -- just a locally-editable display name/subtitle) --

    val profileName: StateFlow<String> = container.profilePreferences.displayName
    val profileSubtitle: StateFlow<String> = container.profilePreferences.subtitle

    fun setProfile(name: String, subtitle: String) = container.profilePreferences.setProfile(name, subtitle)

    private val _showProfile = MutableStateFlow(false)
    val showProfile: StateFlow<Boolean> = _showProfile.asStateFlow()

    fun openProfile() {
        _showProfile.value = true
    }

    fun closeProfile() {
        _showProfile.value = false
    }

    // -- Custom download location (Profile -> Download location) --

    val downloadTreeUri: StateFlow<String?> = container.downloadLocationPreferences.customTreeUri

    fun setDownloadLocation(uriString: String?) = container.downloadLocationPreferences.setCustomTreeUri(uriString)

    private val _storageInfo = MutableStateFlow<StorageInfo?>(null)
    val storageInfo: StateFlow<StorageInfo?> = _storageInfo.asStateFlow()

    /** Recomputed on demand (not a Flow that recomposes live) since it's a filesystem stat call --
     *  called when the Profile or Downloads screen opens. */
    fun refreshStorageInfo() {
        viewModelScope.launch {
            _storageInfo.value = withContext(Dispatchers.IO) { StorageStats.compute(getApplication()) }
        }
    }

    /** Playlists where every track is downloaded -- backs both the Downloads screen's
     *  "Downloaded playlists" count card and (tapping it) the list itself. Recomputed whenever
     *  the playlist list or any playlist's tracks change; fine at personal-app scale (a handful
     *  of playlists, not thousands). */
    val fullyDownloadedPlaylists: StateFlow<List<PlaylistEntity>> = playlists
        .flatMapLatest { list ->
            if (list.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(list.map { playlist -> libraryRepository.observeTracksForPlaylist(playlist.id).map { playlist to it } }) { pairs ->
                    pairs.filter { (_, tracks) -> tracks.isNotEmpty() && tracks.all { it.isDownloaded } }.map { it.first }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val fullyDownloadedPlaylistCount: StateFlow<Int> = fullyDownloadedPlaylists
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    /** Whether every track in this playlist is downloaded -- swaps a [PlaylistRow]'s download
     *  button for a plain "already offline" indicator instead of an actionable one. */
    fun isPlaylistFullyDownloaded(playlistId: Long): Flow<Boolean> =
        fullyDownloadedPlaylists.map { list -> list.any { it.id == playlistId } }

    init {
        viewModelScope.launch {
            val default = libraryRepository.ensureDefaultPlaylist()
            if (_selectedPlaylistId.value == null) _selectedPlaylistId.value = default.id
        }
        // Piggyback download failures onto the same app-wide snackbar as everything else.
        viewModelScope.launch {
            trackDownloader.downloadErrors.collect { message ->
                if (message != null) {
                    _errorMessage.value = message
                    trackDownloader.dismissError()
                }
            }
        }
    }

    fun onLinkInputChanged(value: String) {
        _linkInput.value = value
    }

    fun submitPastedLink() {
        val rawUrl = _linkInput.value
        if (rawUrl.isBlank() || _isResolving.value) return
        _isResolving.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val resolved = withContext(Dispatchers.IO) { AudioStreamResolver.resolve(rawUrl) }
                libraryRepository.addToDefault(resolved)
                val defaultId = libraryRepository.ensureDefaultPlaylist().id
                val tracks = libraryRepository.getTracksForPlaylistOnce(defaultId)
                startQueue(defaultId, tracks, 0)
                _linkInput.value = ""
            } catch (e: AudioResolutionException) {
                _errorMessage.value = e.message
            } catch (e: Exception) {
                _errorMessage.value = "Something went wrong: ${e.message ?: "unknown error"}"
            } finally {
                _isResolving.value = false
            }
        }
    }

    fun selectPlaylist(playlistId: Long) {
        _selectedPlaylistId.value = playlistId
    }

    fun playFromSelectedPlaylist(track: TrackEntity) {
        val playlistId = _selectedPlaylistId.value ?: return
        val tracks = selectedPlaylistTracks.value
        val index = tracks.indexOfFirst { it.videoId == track.videoId }.takeIf { it >= 0 } ?: return
        val label = playlists.value.firstOrNull { it.id == playlistId }?.name ?: ""
        startQueue(playlistId, tracks, index, label)
    }

    /** Starts a playlist from the top -- used by the Home screen's "Recently played"
     *  cards and the playlist detail screen's "Play all" button. */
    fun playPlaylistFromStart(playlistId: Long) {
        viewModelScope.launch {
            val tracks = libraryRepository.getTracksForPlaylistOnce(playlistId)
            val label = playlists.value.firstOrNull { it.id == playlistId }?.name ?: ""
            if (tracks.isNotEmpty()) startQueue(playlistId, tracks, 0, label)
        }
    }

    /** Same as [playPlaylistFromStart] but immediately shuffled -- the playlist detail screen's
     *  dedicated shuffle button. Plays normally first (so a real track loads without racing the
     *  shuffle toggle) then flips shuffle on right away. */
    fun shufflePlayPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val tracks = libraryRepository.getTracksForPlaylistOnce(playlistId)
            val label = playlists.value.firstOrNull { it.id == playlistId }?.name ?: ""
            if (tracks.isEmpty()) return@launch
            startQueue(playlistId, tracks, 0, label)
            if (!playerController.uiState.value.isShuffleEnabled) playerController.toggleShuffle()
        }
    }

    fun playSelectedPlaylistFromStart() {
        val playlistId = _selectedPlaylistId.value ?: return
        playPlaylistFromStart(playlistId)
    }

    /** Plays the Downloads tab's own queue -- scoped to *only* downloaded tracks, so shuffle/
     *  repeat/skip-next never hand off to a track that needs a network resolve. This is what
     *  makes offline listening (airplane mode) actually work end-to-end, not just single-track
     *  playback. */
    fun playDownloadedTrack(track: TrackEntity) {
        viewModelScope.launch {
            val tracks = libraryRepository.getDownloadedTracksOnce()
            val index = tracks.indexOfFirst { it.videoId == track.videoId }
            if (index >= 0) startQueue(DOWNLOADS_QUEUE_ID, tracks, index, "Downloads")
        }
    }

    /** Same idea as [playDownloadedTrack], scoped to just your liked songs. */
    fun playLikedTrack(track: TrackEntity) {
        viewModelScope.launch {
            val tracks = libraryRepository.getLikedTracksOnce()
            val index = tracks.indexOfFirst { it.videoId == track.videoId }
            if (index >= 0) startQueue(LIKED_QUEUE_ID, tracks, index, "Liked Songs")
        }
    }

    private fun startQueue(playlistId: Long, tracks: List<TrackEntity>, index: Int, label: String = "") {
        playerController.playQueue(playlistId, tracks, index, label)
        if (playlistId > 0) {
            viewModelScope.launch { libraryRepository.touchPlaylist(playlistId) }
        }
    }

    fun createPlaylist(
        name: String,
        description: String? = null,
        coverImageUri: String? = null,
        themeColorArgb: Int? = null
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val playlist = libraryRepository.createPlaylist(name.trim(), description, coverImageUri, themeColorArgb)
            _selectedPlaylistId.value = playlist.id
        }
    }

    fun renamePlaylist(playlistId: Long, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { libraryRepository.renamePlaylist(playlistId, name.trim()) }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            libraryRepository.deletePlaylist(playlistId)
            if (_selectedPlaylistId.value == playlistId) {
                _selectedPlaylistId.value = defaultPlaylistId.value
            }
        }
    }

    suspend fun playlistIdsForTrack(videoId: String): List<Long> = libraryRepository.playlistIdsForTrack(videoId)

    fun reorderPlaylist(playlistId: Long, orderedVideoIds: List<String>) {
        viewModelScope.launch { libraryRepository.reorderPlaylist(playlistId, orderedVideoIds) }
    }

    /** Used by the "Add to playlist" dialog's inline "new playlist" flow -- creates the
     *  playlist and immediately adds the track, returning the new id so the dialog can
     *  update its checkbox state without waiting for the playlists Flow to recompose. */
    suspend fun createPlaylistAndAddTrack(name: String, videoId: String): Long? {
        if (name.isBlank()) return null
        val playlist = libraryRepository.createPlaylist(name.trim())
        libraryRepository.addTrackToPlaylist(playlist.id, videoId)
        return playlist.id
    }

    /** Same as [createPlaylistAndAddTrack] but for the full [com.vibesync.app.ui.components.CreatePlaylistDialog]
     *  flow (cover photo/description/theme color) -- used when "New playlist" is tapped from
     *  inside the "Add to playlist" picker. */
    suspend fun createPlaylistWithDetailsAndAddTrack(
        name: String,
        description: String?,
        coverImageUri: String?,
        themeColorArgb: Int?,
        videoId: String
    ): Long? {
        if (name.isBlank()) return null
        val playlist = libraryRepository.createPlaylist(name.trim(), description, coverImageUri, themeColorArgb)
        libraryRepository.addTrackToPlaylist(playlist.id, videoId)
        return playlist.id
    }

    fun addTrackToPlaylist(playlistId: Long, videoId: String) {
        viewModelScope.launch { libraryRepository.addTrackToPlaylist(playlistId, videoId) }
    }

    fun removeTrackFromPlaylist(playlistId: Long, videoId: String) {
        viewModelScope.launch { libraryRepository.removeTrackFromPlaylist(playlistId, videoId) }
    }

    /** Deleting a track everywhere must also clean up its downloaded file, if any --
     *  otherwise the audio would linger orphaned in app-private storage forever. */
    fun deleteTrack(videoId: String) {
        viewModelScope.launch {
            val track = libraryRepository.getTrack(videoId)
            if (track?.isDownloaded == true) trackDownloader.removeDownload(track)
            libraryRepository.deleteTrack(videoId)
        }
    }

    val queuedVideoIds: StateFlow<Set<String>> = playerController.queuedVideoIds

    /** "Add to queue" from any already-saved track row (Library/Downloads/Liked/Playlist rows).
     *  Piggybacks the confirmation onto the same app-wide snackbar as everything else -- both
     *  the visible slide-in and the message itself are the "you know it worked" feedback. */
    fun addTrackToQueue(track: TrackEntity) {
        if (queuedVideoIds.value.contains(track.videoId)) return
        playerController.addToQueue(track)
        _errorMessage.value = "Added \"${track.title}\" to queue"
    }

    fun coverThumbnail(playlistId: Long): Flow<String?> = libraryRepository.observeCoverThumbnail(playlistId)

    fun trackCount(playlistId: Long): Flow<Int> = libraryRepository.observeTrackCount(playlistId)

    fun downloadTrack(track: TrackEntity) {
        viewModelScope.launch { trackDownloader.downloadTrack(track) }
    }

    fun removeDownload(track: TrackEntity) {
        viewModelScope.launch { trackDownloader.removeDownload(track) }
    }

    fun downloadPlaylist(tracks: List<TrackEntity>) {
        viewModelScope.launch { trackDownloader.downloadPlaylist(tracks) }
    }

    /** Same as [downloadPlaylist] but for call sites (playlist list rows) that only have the
     *  playlist id on hand, not its already-loaded track list. */
    fun downloadPlaylistById(playlistId: Long) {
        viewModelScope.launch {
            val tracks = libraryRepository.getTracksForPlaylistOnce(playlistId)
            trackDownloader.downloadPlaylist(tracks)
        }
    }

    fun pauseDownload(videoId: String) = trackDownloader.pauseDownload(videoId)

    fun resumeDownload(track: TrackEntity) = trackDownloader.resumeDownload(track)

    fun cancelDownload(videoId: String) = trackDownloader.cancelDownload(videoId)

    fun exportPlaylist(playlistId: Long, playlistName: String, onReady: (Uri) -> Unit) {
        viewModelScope.launch {
            val tracks = libraryRepository.getTracksForPlaylistOnce(playlistId)
            val uri = withContext(Dispatchers.IO) {
                PlaylistPortability.exportToShareableUri(getApplication(), playlistName, tracks)
            }
            onReady(uri)
        }
    }

    fun exportTracks(tracks: List<TrackEntity>, exportName: String, onReady: (Uri) -> Unit) {
        viewModelScope.launch {
            val uri = withContext(Dispatchers.IO) {
                PlaylistPortability.exportToShareableUri(getApplication(), exportName, tracks)
            }
            onReady(uri)
        }
    }

    /** Result is surfaced through the same app-wide snackbar as [errorMessage] --
     *  simplest place for a one-off "Imported X" / "Import failed" toast to land. */
    fun importPlaylist(uri: Uri) {
        viewModelScope.launch {
            try {
                val imported = withContext(Dispatchers.IO) { PlaylistPortability.importFromUri(getApplication(), uri) }
                libraryRepository.importPlaylist(imported.name, imported.tracks)
                _errorMessage.value = "Imported \"${imported.name}\" (${imported.tracks.size} songs)."
            } catch (e: Exception) {
                _errorMessage.value = "Import failed: ${e.message ?: "unknown error"}"
            }
        }
    }

    // -- Expanded Now Playing screen + Queue --

    private val _showNowPlaying = MutableStateFlow(false)
    val showNowPlaying: StateFlow<Boolean> = _showNowPlaying.asStateFlow()

    fun openNowPlaying() {
        _showNowPlaying.value = true
    }

    fun closeNowPlaying() {
        _showNowPlaying.value = false
    }

    private val _showQueue = MutableStateFlow(false)
    val showQueue: StateFlow<Boolean> = _showQueue.asStateFlow()

    // -- Video playback (Podcasts' "Watch video" action) -- a self-contained screen backed by its
    // own VideoPlayerController, deliberately separate from the audio PlayerController above (see
    // that class's doc comment). --

    data class VideoPlayerRequest(val url: String, val title: String)

    private val _videoPlayerRequest = MutableStateFlow<VideoPlayerRequest?>(null)
    val videoPlayerRequest: StateFlow<VideoPlayerRequest?> = _videoPlayerRequest.asStateFlow()

    /** Pauses the shared audio player first -- watching a video while a background song is also
     *  still playing would just be two overlapping audio tracks, since the video screen has its
     *  own independent player rather than taking over the app's single shared one. */
    fun openVideoPlayer(url: String, title: String) {
        if (playerController.uiState.value.isPlaying) playerController.togglePlayPause()
        _videoPlayerRequest.value = VideoPlayerRequest(url, title)
    }

    fun closeVideoPlayer() {
        _videoPlayerRequest.value = null
    }

    fun openQueue() {
        _showQueue.value = true
    }

    fun closeQueue() {
        _showQueue.value = false
    }

    val upNext: StateFlow<List<TrackEntity>> = playerController.upNext

    fun playFromUpNext(track: TrackEntity) = playerController.playFromUpNext(track)

    fun reorderQueue(newUpNextOrder: List<TrackEntity>) = playerController.reorderUpNext(newUpNextOrder)

    val playbackHistory: StateFlow<List<TrackEntity>> = container.playbackHistoryPreferences.history

    /** Replays a History-tab row -- an ad-hoc single-item queue, same idea as
     *  [playSearchResultDirectly], since a history entry may not be in the live queue (or even
     *  saved to the library) anymore. */
    fun playTrackDirectly(track: TrackEntity) {
        playerController.playQueue(ADHOC_QUEUE_ID, listOf(track), 0, "History")
    }

    /** For the Now Playing screen's heart icon / "Add to playlist" action -- the currently
     *  playing track may not be saved yet (an ad-hoc direct play), so this looks it up in the
     *  library first and falls back to building a transient one from the live playback state,
     *  same pattern as [playSearchResultDirectly]. */
    suspend fun getOrBuildCurrentTrack(): TrackEntity? {
        val state = playbackState.value
        val videoId = state.videoId ?: return null
        libraryRepository.getTrack(videoId)?.let { return it }
        val url = state.url ?: return null
        return TrackEntity(
            videoId = videoId,
            url = url,
            title = state.title ?: "",
            thumbnailUrl = state.thumbnailUrl,
            durationSeconds = state.durationMs / 1000,
            addedAtEpochMillis = System.currentTimeMillis()
        )
    }

    // -- Equalizer (real android.media.audiofx.Equalizer, see EqualizerController) --

    val equalizerState: StateFlow<EqualizerUiState> = container.equalizerController.state

    fun setEqualizerEnabled(enabled: Boolean) = container.equalizerController.setEnabled(enabled)
    fun setEqualizerBandLevel(band: Short, level: Short) = container.equalizerController.setBandLevel(band, level)
    fun applyEqualizerPreset(preset: EqualizerPreset) = container.equalizerController.applyPreset(preset)
    fun resetEqualizer() = container.equalizerController.reset()
    fun setBassBoostStrength(strength: Short) = container.equalizerController.setBassBoostStrength(strength)

    private val _showEqualizer = MutableStateFlow(false)
    val showEqualizer: StateFlow<Boolean> = _showEqualizer.asStateFlow()

    fun openEqualizer() {
        _showEqualizer.value = true
    }

    fun closeEqualizer() {
        _showEqualizer.value = false
    }

    // -- Lyrics (best-effort lookup via lrclib.net, see LyricsRepository) --

    private val _lyricsResult = MutableStateFlow<LyricsResult?>(null)
    val lyricsResult: StateFlow<LyricsResult?> = _lyricsResult.asStateFlow()

    private val _isLoadingLyrics = MutableStateFlow(false)
    val isLoadingLyrics: StateFlow<Boolean> = _isLoadingLyrics.asStateFlow()

    private val _showLyrics = MutableStateFlow(false)
    val showLyrics: StateFlow<Boolean> = _showLyrics.asStateFlow()

    fun openLyrics() {
        _showLyrics.value = true
        _lyricsResult.value = null
        viewModelScope.launch {
            val track = getOrBuildCurrentTrack()
            if (track == null) {
                _lyricsResult.value = LyricsResult.NotFound
                return@launch
            }
            _isLoadingLyrics.value = true
            _lyricsResult.value = container.lyricsRepository.fetchLyrics(track)
            _isLoadingLyrics.value = false
        }
    }

    fun closeLyrics() {
        _showLyrics.value = false
    }

    fun toggleLikeCurrentTrack() {
        viewModelScope.launch {
            val track = getOrBuildCurrentTrack() ?: return@launch
            libraryRepository.toggleLikeTrack(track)
        }
    }

    /** Saves the currently-playing track (if it isn't already saved) and returns it, so the
     *  caller can open [com.vibesync.app.ui.components.AddToPlaylistDialog] on it -- that
     *  dialog assumes the track already exists in the library. */
    suspend fun ensureCurrentTrackSaved(): TrackEntity? {
        val track = getOrBuildCurrentTrack() ?: return null
        return libraryRepository.ensureTrackSaved(track)
    }

    /** Party-aware: the host's play/pause controls the whole party (routes to
     *  [togglePartyPlayPause]); a non-host member's play/pause only affects their own device --
     *  but *resuming* first catches them back up to wherever the party actually is right now, so
     *  pausing locally and pressing play again doesn't just resume from the stale spot they left
     *  off at (see `PartySyncController.currentExpectedPositionMs`). */
    fun togglePlayPause() {
        if (_activePartyCode.value != null) {
            if (isPartyHost.value) {
                togglePartyPlayPause()
                return
            }
            if (!playbackState.value.isPlaying) {
                container.partySyncController.currentExpectedPositionMs()?.let { playerController.seekTo(it) }
            }
        }
        playerController.togglePlayPause()
    }

    fun skipToNext() = playerController.skipToNext()

    fun skipToPrevious() = playerController.skipToPrevious()

    fun toggleShuffle() = playerController.toggleShuffle()

    fun toggleRepeat() = playerController.toggleRepeat()

    /** Party-aware: the host's seek moves the timeline for everyone (routes to
     *  [seekPartyTo]); a non-host member can't drag the timeline at all -- their position is
     *  fully sync-driven, so a local seek would just get corrected back on the next sync tick. */
    fun seekTo(positionMs: Long) {
        if (_activePartyCode.value != null) {
            if (isPartyHost.value) {
                seekPartyTo(positionMs)
            }
            return
        }
        playerController.seekTo(positionMs)
    }

    fun dismissError() {
        _errorMessage.value = null
        playerController.dismissError()
    }

    // -- Auth (Firebase, email/password) --

    val currentUser: StateFlow<AppUser?> = container.authRepository.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val result = container.authRepository.signUp(email, password, displayName)
            if (result is AuthResult.Error) _authError.value = result.message
            _isAuthLoading.value = false
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val result = container.authRepository.signIn(email, password)
            if (result is AuthResult.Error) _authError.value = result.message
            _isAuthLoading.value = false
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val result = container.authRepository.signInWithGoogle(idToken)
            if (result is AuthResult.Error) _authError.value = result.message
            _isAuthLoading.value = false
        }
    }

    fun signOut() {
        leaveCurrentParty()
        container.authRepository.signOut()
    }

    fun dismissAuthError() {
        _authError.value = null
    }

    // -- Social: usernames, friends, party invites (Firestore; see SocialRepository). Adding a
    // friend is immediate/mutual, no approval step -- party invites *do* have a real accept/reject
    // step, since accepting one means actually joining someone's live party. --

    val myProfile: StateFlow<UserProfile?> = currentUser
        .flatMapLatest { user -> if (user == null) flowOf(null) else container.socialRepository.observeMyProfile(user.uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _usernameError = MutableStateFlow<String?>(null)
    val usernameError: StateFlow<String?> = _usernameError.asStateFlow()

    private val _isSavingUsername = MutableStateFlow(false)
    val isSavingUsername: StateFlow<Boolean> = _isSavingUsername.asStateFlow()

    fun setUsername(desired: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            _isSavingUsername.value = true
            _usernameError.value = null
            val result = container.socialRepository.setUsername(user.uid, desired)
            if (result is SocialActionResult.Error) _usernameError.value = result.message
            _isSavingUsername.value = false
        }
    }

    fun dismissUsernameError() {
        _usernameError.value = null
    }

    val friends: StateFlow<List<Friend>> = currentUser
        .flatMapLatest { user -> if (user == null) flowOf(emptyList()) else container.socialRepository.observeFriends(user.uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _friendSearchQuery = MutableStateFlow("")
    val friendSearchQuery: StateFlow<String> = _friendSearchQuery.asStateFlow()

    private val _friendSearchResults = MutableStateFlow<List<UserProfile>>(emptyList())
    val friendSearchResults: StateFlow<List<UserProfile>> = _friendSearchResults.asStateFlow()

    private val _isSearchingFriends = MutableStateFlow(false)
    val isSearchingFriends: StateFlow<Boolean> = _isSearchingFriends.asStateFlow()

    private var friendSearchJob: Job? = null

    /** Live search, same debounce-then-fetch shape as [onYoutubeSearchQueryChanged]. */
    fun onFriendSearchQueryChanged(value: String) {
        _friendSearchQuery.value = value
        friendSearchJob?.cancel()
        if (value.isBlank()) {
            _friendSearchResults.value = emptyList()
            return
        }
        friendSearchJob = viewModelScope.launch {
            delay(400)
            submitFriendSearch()
        }
    }

    fun submitFriendSearch() {
        val query = _friendSearchQuery.value.trim()
        val myUid = currentUser.value?.uid
        if (query.isBlank()) return
        viewModelScope.launch {
            _isSearchingFriends.value = true
            val results = container.socialRepository.searchUsersByUsername(query)
            _friendSearchResults.value = results.filter { it.uid != myUid }
            _isSearchingFriends.value = false
        }
    }

    fun addFriend(profile: UserProfile) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = container.socialRepository.addFriend(user.uid, user.displayName, myProfile.value?.username, profile)
            if (result is SocialActionResult.Error) _usernameError.value = result.message
        }
    }

    fun removeFriend(friend: Friend) {
        val user = currentUser.value ?: return
        viewModelScope.launch { container.socialRepository.removeFriend(user.uid, friend.uid) }
    }

    private val _showFriends = MutableStateFlow(false)
    val showFriends: StateFlow<Boolean> = _showFriends.asStateFlow()

    fun openFriends() {
        _showFriends.value = true
    }

    fun closeFriends() {
        _showFriends.value = false
        _friendSearchQuery.value = ""
        _friendSearchResults.value = emptyList()
    }

    // -- Party invites: a friend-only, host-only "invite them straight into my live party" flow.
    // Notifications are in-app only (a badge count + a one-shot banner while the app happens to be
    // open) -- not a true push delivered while the app's fully closed; see
    // SocialRepository.observeMyPendingInvites's doc comment for why. --

    val pendingInvites: StateFlow<List<PartyInvite>> = currentUser
        .flatMapLatest { user -> if (user == null) flowOf(emptyList()) else container.socialRepository.observeMyPendingInvites(user.uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val pendingInviteCount: StateFlow<Int> = pendingInvites.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val _socialMessage = MutableStateFlow<String?>(null)
    val socialMessage: StateFlow<String?> = _socialMessage.asStateFlow()

    fun dismissSocialMessage() {
        _socialMessage.value = null
    }

    private val _showNotifications = MutableStateFlow(false)
    val showNotifications: StateFlow<Boolean> = _showNotifications.asStateFlow()

    fun openNotifications() {
        _showNotifications.value = true
    }

    fun closeNotifications() {
        _showNotifications.value = false
    }

    fun respondToInvite(invite: PartyInvite, accept: Boolean) {
        viewModelScope.launch {
            container.socialRepository.respondToInvite(invite.id, accept)
            if (accept) {
                closeNotifications()
                joinPartyByCode(invite.partyCode)
            }
        }
    }

    private val _showInviteFriends = MutableStateFlow(false)
    val showInviteFriends: StateFlow<Boolean> = _showInviteFriends.asStateFlow()

    fun openInviteFriends() {
        _invitedFriendUids.value = emptySet()
        _showInviteFriends.value = true
    }

    fun closeInviteFriends() {
        _showInviteFriends.value = false
    }

    private val _invitedFriendUids = MutableStateFlow<Set<String>>(emptySet())
    /** Local-only "I just tapped invite on this friend" feedback (an icon swap, like every other
     *  add-style action in this app) -- not a reflection of whether they've actually responded
     *  yet, that's what the queued invite doc itself (and eventually [pendingInvites] on their
     *  side) is for. */
    val invitedFriendUids: StateFlow<Set<String>> = _invitedFriendUids.asStateFlow()

    /** Host-only: invite a friend straight into the party currently open -- enforced both here
     *  (needs an active code + host status) and for real by security rules (see
     *  FIRESTORE_RULES.md's partyInvites create rule, which checks the party doc's own hostUid). */
    fun inviteFriendToParty(friend: Friend) {
        val user = currentUser.value ?: return
        val code = _activePartyCode.value ?: return
        if (!isPartyHost.value) return
        val partyName = partyState.value?.name ?: "Party"
        viewModelScope.launch {
            when (val result = container.socialRepository.sendPartyInvite(user.uid, user.displayName, friend.uid, code, partyName)) {
                is SocialActionResult.Success -> _invitedFriendUids.update { it + friend.uid }
                is SocialActionResult.Error -> _partyError.value = result.message
            }
        }
    }

    /** Best-effort "online" presence -- see [com.vibesync.app.data.social.ONLINE_WINDOW_MILLIS]'s
     *  doc comment for why this is a heartbeat approximation, not real presence. Restarts (via
     *  [flatMapLatest] cancelling the previous loop) whenever [currentUser] changes, so signing out
     *  stops the heartbeat instead of it quietly continuing under whoever's uid it captured. */
    init {
        viewModelScope.launch {
            currentUser.flatMapLatest { user ->
                if (user == null) return@flatMapLatest flowOf(Unit)
                flow {
                    while (true) {
                        container.socialRepository.sendHeartbeat(user.uid)
                        emit(Unit)
                        delay(30_000)
                    }
                }
            }.collect { }
        }
    }

    private var seenInviteIds = setOf<String>()
    private var hasSeenFirstInviteSnapshot = false

    /** A lightweight in-app banner the moment a genuinely new invite arrives while the app happens
     *  to be running -- [hasSeenFirstInviteSnapshot] guards against every pre-existing pending
     *  invite banner-spamming the instant this collector first subscribes (e.g. right after
     *  sign-in), same guard shape as the kicked-from-party detector below. */
    init {
        viewModelScope.launch {
            pendingInvites.collect { invites ->
                val ids = invites.map { it.id }.toSet()
                if (hasSeenFirstInviteSnapshot) {
                    invites.firstOrNull { it.id !in seenInviteIds }?.let { invite ->
                        _socialMessage.value = "${invite.fromDisplayName.ifBlank { "Someone" }} invited you to \"${invite.partyName}\" -- check Notifications."
                    }
                } else {
                    hasSeenFirstInviteSnapshot = true
                }
                seenInviteIds = ids
            }
        }
    }

    // -- Party rooms (Firebase Firestore; see PartyRepository/PartySyncController for the sync
    // design). A "party" isn't a bottom-nav tab -- it's reached via the new hamburger Menu item
    // (replacing the old direct Downloads nav button) alongside Downloads. --

    private val _showPartyHome = MutableStateFlow(false)
    val showPartyHome: StateFlow<Boolean> = _showPartyHome.asStateFlow()

    private val _showPartyRoom = MutableStateFlow(false)
    val showPartyRoom: StateFlow<Boolean> = _showPartyRoom.asStateFlow()

    private val _activePartyCode = MutableStateFlow<String?>(null)
    val activePartyCode: StateFlow<String?> = _activePartyCode.asStateFlow()

    private val _isPartyActionLoading = MutableStateFlow(false)
    val isPartyActionLoading: StateFlow<Boolean> = _isPartyActionLoading.asStateFlow()

    private val _partyError = MutableStateFlow<String?>(null)
    val partyError: StateFlow<String?> = _partyError.asStateFlow()

    val partyState: StateFlow<PartyDoc?> = _activePartyCode
        .flatMapLatest { code -> if (code == null) flowOf(null) else container.partyRepository.observeParty(code) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val partyQueue: StateFlow<List<PartyQueueItem>> = _activePartyCode
        .flatMapLatest { code -> if (code == null) flowOf(emptyList()) else container.partyRepository.observeQueue(code) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val partyMembers: StateFlow<List<PartyMember>> = _activePartyCode
        .flatMapLatest { code -> if (code == null) flowOf(emptyList()) else container.partyRepository.observeMembers(code) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Every videoId currently pending/approved in the party queue -- lets a search/playlist row
     *  show "already added" (a checkmark) instead of "+", matching the same pattern the app
     *  already uses for the local up-next queue (`PlayerController.queuedVideoIds`). A PLAYED song
     *  isn't "in the queue" anymore, so it's excluded -- re-requesting it is fine. */
    val partyQueuedVideoIds: StateFlow<Set<String>> = partyQueue
        .map { items -> items.filter { it.status != QueueItemStatus.PLAYED }.mapTo(mutableSetOf()) { it.videoId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    /** How many songs *this* user has requested that haven't played yet -- the ask was specifically
     *  "a normal user can't see how many songs they've requested"; counting pending+approved (not
     *  PLAYED, which is history at that point) is what "how many are still mine, waiting" means. */
    val myPartyRequestCount: StateFlow<Int> = combine(partyQueue, currentUser) { items, user ->
        if (user == null) 0 else items.count { it.addedByUid == user.uid && it.status != QueueItemStatus.PLAYED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val isPartyHost: StateFlow<Boolean> = combine(partyState, currentUser) { party, user ->
        party != null && user != null && party.hostUid == user.uid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    /** True while a just-joined/just-created party's audio is still catching up to the shared
     *  position -- drives a "Joining party..." screen so nobody hears an out-of-sync moment while
     *  the stream is still resolving. See `PartySyncController.isSyncing`. */
    val isJoiningParty: StateFlow<Boolean> = container.partySyncController.isSyncing

    /** Detects being kicked: if we're supposedly in a party but our own uid stops showing up in
     *  its live member list, the host removed us. [hasSeenOwnMembership] guards against the very
     *  first (possibly still-empty) snapshot right after joining/creating being mistaken for a
     *  kick before the real member list has actually loaded. */
    private var hasSeenOwnMembership = false

    init {
        viewModelScope.launch {
            combine(_activePartyCode, partyMembers, currentUser) { code, members, user -> Triple(code, members, user) }
                .collect { (code, members, user) ->
                    if (code == null || user == null) {
                        hasSeenOwnMembership = false
                        return@collect
                    }
                    val stillAMember = members.any { it.uid == user.uid }
                    if (stillAMember) {
                        hasSeenOwnMembership = true
                    } else if (hasSeenOwnMembership) {
                        _partyError.value = "You were removed from the party."
                        container.partySyncController.stop()
                        playerController.isLockedToParty = false
                        _activePartyCode.value = null
                        _showPartyRoom.value = false
                        hasSeenOwnMembership = false
                    }
                }
        }
    }

    fun openPartyHome() {
        _showPartyHome.value = true
        _partyError.value = null
    }

    fun closePartyHome() {
        _showPartyHome.value = false
    }

    /** Re-opens the room UI for an already-active party (e.g. navigating back in via Menu)
     *  without creating/joining again. */
    fun openActivePartyRoom() {
        if (_activePartyCode.value != null) _showPartyRoom.value = true
    }

    fun closePartyRoom() {
        _showPartyRoom.value = false
    }

    fun createParty(partyName: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            _isPartyActionLoading.value = true
            _partyError.value = null
            when (val result = container.partyRepository.createParty(user.uid, user.displayName, partyName)) {
                is CreatePartyResult.Success -> enterParty(result.code, isHost = true)
                is CreatePartyResult.Error -> _partyError.value = result.message
            }
            _isPartyActionLoading.value = false
        }
    }

    fun joinPartyByCode(code: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            _isPartyActionLoading.value = true
            _partyError.value = null
            when (val result = container.partyRepository.joinParty(code, user.uid, user.displayName)) {
                JoinPartyResult.Success -> enterParty(code.trim().uppercase(), isHost = false)
                JoinPartyResult.NotFound -> _partyError.value = "No party found with that code."
                is JoinPartyResult.Error -> _partyError.value = result.message
            }
            _isPartyActionLoading.value = false
        }
    }

    private fun enterParty(code: String, isHost: Boolean) {
        // Mutual exclusivity with the local (LAN) room -- see MainViewModel's Local Room section
        // -- both drive the same shared PlayerController via the same PARTY_QUEUE_ID-style
        // sentinel + isLockedToParty flag, so being "in" both at once would just have them fight
        // over local playback state.
        leaveLocalRoom()
        _activePartyCode.value = code
        _showPartyHome.value = false
        _showPartyRoom.value = true
        // isHost here is the explicit parameter, not the reactive isPartyHost StateFlow -- that
        // flow is derived from partyState, which won't have caught up to this brand-new party yet
        // at the exact moment we're entering it, so reading it here would race and likely see the
        // stale "not in any party" value.
        playerController.isLockedToParty = !isHost
        container.partySyncController.start(code, isHost, viewModelScope)
    }

    fun leaveCurrentParty() {
        val code = _activePartyCode.value
        val uid = currentUser.value?.uid
        if (code != null && uid != null) container.partyRepository.leaveParty(code, uid)
        container.partySyncController.stop()
        playerController.isLockedToParty = false
        _activePartyCode.value = null
        _showPartyRoom.value = false
    }

    /** Any member can request a song -- lands as "pending" unless the requester is the host
     *  (host actions auto-approve; also re-checked server-side by the security rules, this flag
     *  alone is not what makes it trustworthy). Checked against the locally-cached queue first for
     *  instant feedback (no round trip needed for the common case); [PartyRepository.addToQueue]
     *  re-checks against a fresh server read too, so two people can't both sneak the same song in
     *  by requesting within the same instant. */
    fun requestSongForParty(track: TrackEntity) {
        val code = _activePartyCode.value ?: return
        val user = currentUser.value ?: return
        val alreadyQueued = partyQueue.value.any { it.videoId == track.videoId && it.status != QueueItemStatus.PLAYED }
        if (alreadyQueued) {
            _partyError.value = "\"${track.title}\" is already in the queue."
            return
        }
        val host = isPartyHost.value
        viewModelScope.launch {
            val result = container.partyRepository.addToQueue(code, track, user.uid, user.displayName, autoApprove = host)
            if (result is PartyActionResult.Error) _partyError.value = result.message
        }
    }

    fun requestPlaylistForParty(tracks: List<TrackEntity>) {
        val code = _activePartyCode.value ?: return
        val user = currentUser.value ?: return
        val host = isPartyHost.value
        viewModelScope.launch {
            val nextPosition = nextApprovedPosition()
            val result = container.partyRepository.addPlaylistToQueue(code, tracks, user.uid, user.displayName, autoApprove = host, startPosition = nextPosition)
            if (result is PartyActionResult.Error) _partyError.value = result.message
        }
    }

    /** Host-only: play something immediately, replacing whatever's currently playing -- an ad-hoc
     *  play never added to the queue (queueItemId null), distinct from [playApprovedQueueItemNow]
     *  which jumps to something already sitting in "Up next". */
    fun playSongNowInParty(track: TrackEntity) {
        val code = _activePartyCode.value ?: return
        if (!isPartyHost.value) return
        viewModelScope.launch {
            val result = container.partyRepository.setCurrentTrack(code, track, queueItemId = null, startPositionMs = 0L)
            if (result is PartyActionResult.Error) _partyError.value = result.message
        }
    }

    /** Host-only: tap any song in "Up next" to jump straight to it, skipping whatever order it
     *  was queued in. */
    fun playApprovedQueueItemNow(item: PartyQueueItem) {
        val code = _activePartyCode.value ?: return
        if (!isPartyHost.value) return
        viewModelScope.launch { container.partySyncController.playQueueItem(code, item) }
    }

    fun approvePartyQueueItem(item: PartyQueueItem) {
        val code = _activePartyCode.value ?: return
        if (!isPartyHost.value) return
        viewModelScope.launch {
            container.partyRepository.approveQueueItem(code, item.id, nextApprovedPosition())
        }
    }

    fun rejectPartyQueueItem(item: PartyQueueItem) {
        val code = _activePartyCode.value ?: return
        if (!isPartyHost.value) return
        viewModelScope.launch { container.partyRepository.rejectQueueItem(code, item.id) }
    }

    fun reorderPartyQueue(newOrder: List<PartyQueueItem>) {
        val code = _activePartyCode.value ?: return
        if (!isPartyHost.value) return
        viewModelScope.launch { container.partyRepository.reorderQueue(code, newOrder.map { it.id }) }
    }

    /** Host-only: drop an upcoming (not-yet-played) song from the queue entirely. */
    fun removePartyQueueItem(item: PartyQueueItem) {
        val code = _activePartyCode.value ?: return
        if (!isPartyHost.value) return
        viewModelScope.launch {
            val result = container.partyRepository.removeQueueItem(code, item.id)
            if (result is PartyActionResult.Error) _partyError.value = result.message
        }
    }

    fun togglePartyPlayPause() {
        val code = _activePartyCode.value ?: return
        if (!isPartyHost.value) return
        val party = partyState.value ?: return
        val state = playbackState.value
        viewModelScope.launch {
            container.partyRepository.setPlaybackState(code, isPlaying = !party.isPlaying, positionMs = state.positionMs)
        }
    }

    /** Host-only: drag the timeline for everyone -- writes straight to the party doc (host's own
     *  device re-syncs to it the same way every other device does, via `PartySyncController`,
     *  rather than seeking locally first) so there's exactly one source of truth for "where is
     *  this track right now" instead of the host's local seek and the party doc's position
     *  potentially disagreeing. */
    fun seekPartyTo(positionMs: Long) {
        val code = _activePartyCode.value ?: return
        if (!isPartyHost.value) return
        val party = partyState.value ?: return
        viewModelScope.launch {
            container.partyRepository.setPlaybackState(code, isPlaying = party.isPlaying, positionMs = positionMs)
        }
    }

    fun skipPartyToNext() {
        val code = _activePartyCode.value ?: return
        if (!isPartyHost.value) return
        viewModelScope.launch {
            if (!container.partySyncController.playNext(code)) {
                container.partyRepository.setPlaybackState(code, isPlaying = false, positionMs = playbackState.value.durationMs)
            }
        }
    }

    fun skipPartyToPrevious() {
        val code = _activePartyCode.value ?: return
        if (!isPartyHost.value) return
        viewModelScope.launch {
            if (!container.partySyncController.playPrevious(code)) {
                _partyError.value = "Nothing played before this yet."
            }
        }
    }

    /** Host-only: remove another member from the party. The removed member's own device detects
     *  this on its own (see the membership-watch in `init {}`) and exits locally -- this call only
     *  needs to delete their membership doc. */
    fun kickPartyMember(uid: String) {
        val code = _activePartyCode.value ?: return
        if (!isPartyHost.value) return
        if (uid == currentUser.value?.uid) return // use Leave for yourself
        viewModelScope.launch {
            val result = container.partyRepository.kickMember(code, uid)
            if (result is PartyActionResult.Error) _partyError.value = result.message
        }
    }

    private fun nextApprovedPosition(): Long =
        (partyQueue.value.filter { it.status == QueueItemStatus.APPROVED }.maxOfOrNull { it.position } ?: -1L) + 1L

    fun dismissPartyError() {
        _partyError.value = null
    }

    // -- Party "add a song" search -- deliberately separate state from the main Search tab's
    // (_youtubeSearchQuery/youtubeSearchResults) so typing in the party room's add-song dialog
    // doesn't disturb whatever's shown on the Search tab. --

    private val _partyAddQuery = MutableStateFlow("")
    val partyAddQuery: StateFlow<String> = _partyAddQuery.asStateFlow()

    private val _partyAddResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val partyAddResults: StateFlow<List<SearchResult>> = _partyAddResults.asStateFlow()

    private val _isPartyAddSearching = MutableStateFlow(false)
    val isPartyAddSearching: StateFlow<Boolean> = _isPartyAddSearching.asStateFlow()

    private var partyAddLiveSearchJob: Job? = null

    /** Live search, same debounce-then-fetch shape as [onYoutubeSearchQueryChanged]. */
    fun onPartyAddQueryChanged(value: String) {
        _partyAddQuery.value = value
        partyAddLiveSearchJob?.cancel()
        if (value.isBlank()) {
            _partyAddResults.value = emptyList()
            return
        }
        partyAddLiveSearchJob = viewModelScope.launch {
            delay(400)
            submitPartyAddSearch()
        }
    }

    fun submitPartyAddSearch() {
        val query = _partyAddQuery.value.trim()
        if (query.isBlank() || _isPartyAddSearching.value) return
        viewModelScope.launch {
            _isPartyAddSearching.value = true
            try {
                _partyAddResults.value = withContext(Dispatchers.IO) { YouTubeSearcher.search(query) }
            } catch (e: SearchException) {
                _partyError.value = e.message
            } catch (e: Exception) {
                _partyError.value = "Search failed: ${e.message ?: "unknown error"}"
            } finally {
                _isPartyAddSearching.value = false
            }
        }
    }

    fun requestSearchResultForParty(result: SearchResult) {
        requestSongForParty(result.toTransientTrackEntity())
    }

    /** Fetches a playlist's tracks once (not a live subscription -- this is a one-shot "grab the
     *  contents to queue them up" action, not something that needs to stay reactive). */
    fun requestPlaylistForPartyById(playlistId: Long) {
        viewModelScope.launch {
            val tracks = libraryRepository.observeTracksForPlaylist(playlistId).first()
            if (tracks.isNotEmpty()) requestPlaylistForParty(tracks)
        }
    }

    // -- Local Party Room (LAN, no Firebase/internet -- see localparty/LocalPartyHost.kt,
    // LocalPartyClient.kt, LocalPartyDiscovery.kt, playback/LocalPartySyncController.kt). A
    // genuinely separate mode from the Firebase Party above, not a variant of it -- mutually
    // exclusive with it (see enterParty's leaveLocalRoom() call and hostLocalRoom/joinLocalRoom's
    // leaveCurrentParty() calls below), since both ultimately drive the same shared
    // PlayerController and can't both be "in control" at once. --

    private val localDiscovery = LocalPartyDiscovery(getApplication())
    val localDiscoveredRooms: StateFlow<List<DiscoveredRoom>> = localDiscovery.rooms
    val isDiscoveringLocalRooms: StateFlow<Boolean> = localDiscovery.isDiscovering

    init {
        viewModelScope.launch {
            localDiscovery.errorMessage.collect { if (it != null) _localRoomError.value = it }
        }
    }

    private var localHost: LocalPartyHost? = null
    private var localClient: LocalPartyClient? = null
    private var localStateCollectJob: Job? = null

    private val _localRoomState = MutableStateFlow<LocalRoomState?>(null)
    val localRoomState: StateFlow<LocalRoomState?> = _localRoomState.asStateFlow()

    private val _isLocalHost = MutableStateFlow(false)
    val isLocalHost: StateFlow<Boolean> = _isLocalHost.asStateFlow()

    /** Identifies "which member row is me" in the room's member list -- the host's own stable id,
     *  or a guest's freshly-generated connection id ([LocalPartyClient.myId]). */
    private val _myLocalId = MutableStateFlow<String?>(null)
    val myLocalId: StateFlow<String?> = _myLocalId.asStateFlow()

    private val _isConnectingLocalRoom = MutableStateFlow(false)
    val isConnectingLocalRoom: StateFlow<Boolean> = _isConnectingLocalRoom.asStateFlow()

    private val _localRoomError = MutableStateFlow<String?>(null)
    val localRoomError: StateFlow<String?> = _localRoomError.asStateFlow()

    /** Whether this device is currently part of a local room at all (host or guest, connected or
     *  still connecting) -- drives Menu's "Local Room" entry re-opening the active room instead of
     *  the host/browse home screen, same as [activePartyCode] does for the Firebase party. */
    val isLocalRoomActive: StateFlow<Boolean> = combine(_localRoomState, _isConnectingLocalRoom) { state, connecting ->
        state != null || connecting
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun dismissLocalRoomError() {
        _localRoomError.value = null
    }

    private val _showLocalRoomHome = MutableStateFlow(false)
    val showLocalRoomHome: StateFlow<Boolean> = _showLocalRoomHome.asStateFlow()

    /** Doesn't start discovery itself -- [LocalRoomHomeScreen] requests the runtime permission
     *  NSD needs first (see its own comment) and calls [startLocalDiscovery] once that's resolved,
     *  so the very first visit to this screen doesn't race a permission prompt against discovery
     *  actually starting. */
    fun openLocalRoomHome() {
        _localRoomError.value = null
        _showLocalRoomHome.value = true
    }

    /** Stopped again in [closeLocalRoomHome] (or implicitly once a join actually starts, see
     *  [joinLocalRoom]) so this app isn't broadcasting mDNS discovery queries any longer than the
     *  user is actually looking. */
    fun startLocalDiscovery() {
        localDiscovery.start()
    }

    fun closeLocalRoomHome() {
        localDiscovery.stop()
        _showLocalRoomHome.value = false
    }

    private val _showLocalRoom = MutableStateFlow(false)
    val showLocalRoom: StateFlow<Boolean> = _showLocalRoom.asStateFlow()

    /** Re-opens the room UI for an already-active local room (Menu -> Local Room), same pattern
     *  as [openActivePartyRoom]. */
    fun openActiveLocalRoom() {
        if (localHost != null || localClient != null) _showLocalRoom.value = true
    }

    fun closeLocalRoom() {
        _showLocalRoom.value = false
    }

    fun hostLocalRoom(roomName: String) {
        val user = currentUser.value ?: return
        leaveCurrentParty()
        val host = LocalPartyHost(getApplication(), user.displayName, roomName.ifBlank { "${user.displayName}'s Room" })
        localHost = host
        localClient = null
        _isLocalHost.value = true
        _myLocalId.value = host.hostId
        _localRoomError.value = null
        host.start()

        localStateCollectJob?.cancel()
        localStateCollectJob = viewModelScope.launch {
            launch { host.state.collect { _localRoomState.value = it } }
            launch { host.errorMessage.collect { if (it != null) _localRoomError.value = it } }
        }

        playerController.isLockedToParty = false
        container.localPartySyncController.startAsHost(host, viewModelScope)
        _showLocalRoomHome.value = false
        _showLocalRoom.value = true
    }

    fun joinLocalRoom(room: DiscoveredRoom) {
        val user = currentUser.value ?: return
        leaveCurrentParty()
        localDiscovery.stop() // no need to keep browsing once actually connecting to one
        val client = LocalPartyClient(user.displayName)
        localClient = client
        localHost = null
        _isLocalHost.value = false
        _myLocalId.value = client.myId
        _localRoomError.value = null
        _isConnectingLocalRoom.value = true
        client.connect(room)

        localStateCollectJob?.cancel()
        localStateCollectJob = viewModelScope.launch {
            launch {
                client.state.collect { state ->
                    _localRoomState.value = state
                    if (state != null) _isConnectingLocalRoom.value = false
                }
            }
            launch {
                client.connectionError.collect { err ->
                    if (err != null) {
                        _localRoomError.value = err
                        exitLocalRoomLocally()
                    }
                }
            }
            launch {
                client.wasKicked.collect { kicked ->
                    if (kicked) {
                        _localRoomError.value = "You were removed from the room."
                        exitLocalRoomLocally()
                    }
                }
            }
        }

        playerController.isLockedToParty = true
        container.localPartySyncController.startAsGuest(client, viewModelScope)
        _showLocalRoomHome.value = false
        _showLocalRoom.value = true
    }

    /** Resets local state without notifying the host -- for when the connection is already gone
     *  (kicked, dropped) rather than a deliberate [leaveLocalRoom]. */
    private fun exitLocalRoomLocally() {
        localStateCollectJob?.cancel()
        localStateCollectJob = null
        container.localPartySyncController.stop()
        playerController.isLockedToParty = false
        localHost = null
        localClient = null
        _localRoomState.value = null
        _isLocalHost.value = false
        _myLocalId.value = null
        _isConnectingLocalRoom.value = false
        _showLocalRoom.value = false
    }

    fun leaveLocalRoom() {
        localHost?.stop()
        localClient?.disconnect()
        exitLocalRoomLocally()
    }

    private fun localQueueItemFromTrack(track: TrackEntity, addedByName: String) = LocalQueueItem(
        id = java.util.UUID.randomUUID().toString(),
        videoId = track.videoId,
        title = track.title,
        thumbnailUrl = track.thumbnailUrl,
        url = track.url,
        durationSeconds = track.durationSeconds,
        addedByName = addedByName
    )

    /** Any member (host included) can request a song -- unlike the Firebase party there's no
     *  pending/approve step here (see [LocalRoomState]'s doc comment), it lands straight in the
     *  queue either way. */
    fun requestSongForLocalRoom(track: TrackEntity) {
        val user = currentUser.value ?: return
        val host = localHost
        if (host != null) {
            host.addToQueue(localQueueItemFromTrack(track, user.displayName))
        } else {
            localClient?.requestSong(track.videoId, track.title, track.thumbnailUrl, track.url, track.durationSeconds)
        }
    }

    fun requestSearchResultForLocalRoom(result: SearchResult) {
        requestSongForLocalRoom(result.toTransientTrackEntity())
    }

    fun localTogglePlayPause() {
        val host = localHost ?: return
        val state = _localRoomState.value ?: return
        host.setPlaybackState(isPlaying = !state.isPlaying, positionMs = playbackState.value.positionMs)
    }

    fun localSeekTo(positionMs: Long) {
        val host = localHost ?: return
        val state = _localRoomState.value ?: return
        host.setPlaybackState(isPlaying = state.isPlaying, positionMs = positionMs)
    }

    fun localSkipNext() {
        val host = localHost ?: return
        val next = (_localRoomState.value ?: return).queue.firstOrNull()
        if (next != null) {
            host.setCurrentTrack(next.videoId, next.title, next.thumbnailUrl, next.url, next.durationSeconds, fromQueueItemId = next.id)
        } else {
            host.setPlaybackState(isPlaying = false, positionMs = playbackState.value.durationMs)
        }
    }

    fun localPlayQueueItemNow(item: LocalQueueItem) {
        localHost?.setCurrentTrack(item.videoId, item.title, item.thumbnailUrl, item.url, item.durationSeconds, fromQueueItemId = item.id)
    }

    fun localReorderQueue(newOrder: List<LocalQueueItem>) {
        localHost?.reorderQueue(newOrder)
    }

    fun localRemoveQueueItem(item: LocalQueueItem) {
        localHost?.removeFromQueue(item.id)
    }

    fun localKickMember(memberId: String) {
        localHost?.kick(memberId)
    }

    // Intentionally no player teardown in onCleared(): the PlayerController is
    // Application-scoped and must keep the audio session alive after this
    // ViewModel (and its Activity) are destroyed.
}
