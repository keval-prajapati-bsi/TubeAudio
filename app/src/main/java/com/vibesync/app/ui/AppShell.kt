package com.vibesync.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vibesync.app.data.CrashLogger
import com.vibesync.app.ui.components.NowPlayingBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TubeAudioApp(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    var lastCrash by remember { mutableStateOf(CrashLogger.readLastCrash(context)) }
    if (lastCrash != null) {
        LastCrashScreen(
            crashText = lastCrash ?: "",
            onDismiss = {
                CrashLogger.clear(context)
                lastCrash = null
            }
        )
        return
    }

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val showSettings by viewModel.showSettings.collectAsStateWithLifecycle()
    val showProfile by viewModel.showProfile.collectAsStateWithLifecycle()
    val showNowPlaying by viewModel.showNowPlaying.collectAsStateWithLifecycle()
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsStateWithLifecycle()
    val showPreferencesEditor by viewModel.showPreferencesEditor.collectAsStateWithLifecycle()
    val showEqualizer by viewModel.showEqualizer.collectAsStateWithLifecycle()
    val showLyrics by viewModel.showLyrics.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val showPartyHome by viewModel.showPartyHome.collectAsStateWithLifecycle()
    val showPartyRoom by viewModel.showPartyRoom.collectAsStateWithLifecycle()
    val activePartyCode by viewModel.activePartyCode.collectAsStateWithLifecycle()
    val showFriends by viewModel.showFriends.collectAsStateWithLifecycle()
    val showNotifications by viewModel.showNotifications.collectAsStateWithLifecycle()
    val showInviteFriends by viewModel.showInviteFriends.collectAsStateWithLifecycle()
    val socialMessage by viewModel.socialMessage.collectAsStateWithLifecycle()
    val videoPlayerRequest by viewModel.videoPlayerRequest.collectAsStateWithLifecycle()
    val showLocalRoomHome by viewModel.showLocalRoomHome.collectAsStateWithLifecycle()
    val showLocalRoom by viewModel.showLocalRoom.collectAsStateWithLifecycle()
    val localRoomActive by viewModel.isLocalRoomActive.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (currentUser == null) {
        AuthScreen(viewModel = viewModel)
        return
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissError()
        }
    }
    LaunchedEffect(playbackState.errorMessage) {
        playbackState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissError()
        }
    }
    LaunchedEffect(socialMessage) {
        socialMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSocialMessage()
        }
    }

    if (!onboardingCompleted) {
        OnboardingScreen(
            onSkip = { viewModel.skipOnboarding() },
            onDone = { languages, genres -> viewModel.completeOnboarding(languages, genres) }
        )
        return
    }

    if (videoPlayerRequest != null) {
        val request = videoPlayerRequest!!
        BackHandler { viewModel.closeVideoPlayer() }
        VideoPlayerScreen(videoUrl = request.url, title = request.title, onClose = { viewModel.closeVideoPlayer() })
        return
    }

    if (showPreferencesEditor) {
        val languages by viewModel.preferredLanguages.collectAsStateWithLifecycle()
        val genres by viewModel.preferredGenres.collectAsStateWithLifecycle()
        BackHandler { viewModel.closePreferencesEditor() }
        OnboardingScreen(
            initialLanguages = languages,
            initialGenres = genres,
            isEditMode = true,
            onBack = { viewModel.closePreferencesEditor() },
            onDone = { newLanguages, newGenres -> viewModel.savePreferencesFromEditor(newLanguages, newGenres) }
        )
        return
    }

    if (showEqualizer) {
        BackHandler { viewModel.closeEqualizer() }
        EqualizerScreen(viewModel = viewModel, onBack = { viewModel.closeEqualizer() })
        return
    }

    if (showLyrics) {
        BackHandler { viewModel.closeLyrics() }
        LyricsScreen(viewModel = viewModel, onBack = { viewModel.closeLyrics() })
        return
    }

    if (showInviteFriends) {
        // Checked before showPartyRoom -- reached from that screen's own menu, so it needs to
        // render "on top of" it (and back needs to land back there, not skip past it to Home).
        BackHandler { viewModel.closeInviteFriends() }
        InviteFriendsScreen(viewModel = viewModel, onBack = { viewModel.closeInviteFriends() })
        return
    }

    if (showPartyRoom) {
        // Matches the "Back" icon button's own behavior (see PartyRoomScreen) -- closing the
        // room view doesn't leave the party, sync keeps running, reachable again via Menu.
        BackHandler { viewModel.closePartyRoom() }
        PartyRoomScreen(viewModel = viewModel, onBack = { viewModel.closePartyRoom() })
        return
    }

    if (showPartyHome) {
        BackHandler { viewModel.closePartyHome() }
        PartyHomeScreen(viewModel = viewModel, onBack = { viewModel.closePartyHome() })
        return
    }

    if (showLocalRoom) {
        // Same "closing this screen doesn't leave the room" pattern as Party Room.
        BackHandler { viewModel.closeLocalRoom() }
        LocalRoomScreen(viewModel = viewModel, onBack = { viewModel.closeLocalRoom() })
        return
    }

    if (showLocalRoomHome) {
        BackHandler { viewModel.closeLocalRoomHome() }
        LocalRoomHomeScreen(viewModel = viewModel, onBack = { viewModel.closeLocalRoomHome() })
        return
    }

    if (showNowPlaying) {
        BackHandler { viewModel.closeNowPlaying() }
        NowPlayingScreen(viewModel = viewModel, onClose = { viewModel.closeNowPlaying() })
        return
    }

    if (showSettings) {
        BackHandler { viewModel.closeSettings() }
        SettingsScreen(viewModel = viewModel, onBack = { viewModel.closeSettings() })
        return
    }

    if (showFriends) {
        // Reached via Profile's "Friends" row, which closes Profile first (same convention as
        // its Equalizer/Music preferences rows) -- an independent top-level screen, not layered
        // on top of Profile, so back lands on Home like those do.
        BackHandler { viewModel.closeFriends() }
        FriendsScreen(viewModel = viewModel, onBack = { viewModel.closeFriends() })
        return
    }

    if (showNotifications) {
        BackHandler { viewModel.closeNotifications() }
        NotificationsScreen(viewModel = viewModel, onBack = { viewModel.closeNotifications() })
        return
    }

    if (showProfile) {
        BackHandler { viewModel.closeProfile() }
        ProfileScreen(viewModel = viewModel, onBack = { viewModel.closeProfile() })
        return
    }

    // Root tab view: back navigates to Home first (standard Android bottom-nav pattern) rather
    // than exiting straight from a non-Home tab; only exits the app from Home itself, which is
    // the default (correct) behavior for a root screen with no BackHandler registered.
    BackHandler(enabled = selectedTab != AppTab.HOME) { viewModel.selectTab(AppTab.HOME) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) { data -> Snackbar(snackbarData = data) } },
        bottomBar = {
            Column {
                if (playbackState.title != null) {
                    NowPlayingBar(
                        state = playbackState,
                        onTogglePlayPause = viewModel::togglePlayPause,
                        onSkipNext = viewModel::skipToNext,
                        onSkipPrevious = viewModel::skipToPrevious,
                        onExpand = { viewModel.openNowPlaying() }
                    )
                }
                // Floating glass pill, not an edge-to-edge bar: inset margins + a 32dp corner
                // radius (spec: "Bottom Navigation: 32dp") on an elevated-card surface at reduced
                // alpha. A true frosted blur needs RenderEffect (API 31+ only, see NowPlayingScreen's
                // gating elsewhere in this app), so this is the same flat-alpha-blend approximation
                // already used here -- works identically on every supported API level.
                androidx.compose.material3.Surface(
                    modifier = Modifier
                        .padding(horizontal = com.vibesync.app.ui.theme.Dimens.MediumGap, vertical = com.vibesync.app.ui.theme.Dimens.SmallGap),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(com.vibesync.app.ui.theme.Dimens.CornerBottomNav),
                    color = com.vibesync.app.ui.theme.ElevatedCardBackground.copy(alpha = 0.92f),
                    shadowElevation = 12.dp
                ) {
                // 5 tabs (Home/Explore/Party/Library/Profile), matching the newer of the two
                // bottom-nav patterns found across the Figma file's 31 screens -- the older
                // home-screen frame still showed a 4-tab Home/Explore/Library/Menu bar, but all 8
                // screens in the file's "expansion board" (Party Room, Local Room, Friends,
                // Profile, Settings, etc.) consistently show this 5-tab version with no Menu
                // entry, which reads as the more current intent. Party/Profile are transient
                // overlay screens (not part of the AppTab state machine), so they're always
                // unselected here -- the bar itself isn't even rendered while either is open,
                // since both return early above before reaching this Scaffold.
                NavigationBar(containerColor = androidx.compose.ui.graphics.Color.Transparent) {
                    NavigationBarItem(
                        selected = selectedTab == AppTab.HOME,
                        onClick = { viewModel.selectTab(AppTab.HOME) },
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                        label = { NavLabel("Home") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == AppTab.SEARCH,
                        onClick = { viewModel.selectTab(AppTab.SEARCH) },
                        icon = { Icon(Icons.Filled.Explore, contentDescription = null) },
                        label = { NavLabel("Explore") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { viewModel.openPartyHome() },
                        icon = { Icon(Icons.Filled.MusicNote, contentDescription = null) },
                        label = { NavLabel("Party") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == AppTab.LIBRARY,
                        onClick = { viewModel.selectTab(AppTab.LIBRARY) },
                        icon = { Icon(Icons.Filled.LibraryMusic, contentDescription = null) },
                        label = { NavLabel("Library") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { viewModel.openProfile() },
                        icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        label = { NavLabel("Profile") }
                    )
                }
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            AppTab.HOME -> DashboardScreen(viewModel = viewModel, modifier = Modifier.padding(padding))
            AppTab.SEARCH -> ExploreScreen(viewModel = viewModel, modifier = Modifier.padding(padding))
            AppTab.TRENDING -> TrendingScreen(viewModel = viewModel, modifier = Modifier.padding(padding))
            AppTab.LIBRARY -> LibraryScreen(viewModel = viewModel, modifier = Modifier.padding(padding))
            AppTab.DOWNLOADS -> DownloadsScreen(viewModel = viewModel, modifier = Modifier.padding(padding))
            AppTab.PODCASTS -> PodcastScreen(viewModel = viewModel, modifier = Modifier.padding(padding))
        }
    }
}

/** Single line, no wrap -- five nav items on one row leaves little width per label. */
@Composable
private fun NavLabel(text: String) {
    Text(
        text = text,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall
    )
}
