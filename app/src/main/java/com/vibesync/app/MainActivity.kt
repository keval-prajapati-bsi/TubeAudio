package com.vibesync.app

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vibesync.app.ui.AppTab
import com.vibesync.app.ui.MainViewModel
import com.vibesync.app.ui.SplashScreen
import com.vibesync.app.ui.TubeAudioApp
import com.vibesync.app.ui.theme.TubeAudioTheme

/** Survives Activity recreation (a plain object, not Activity/ViewModel state) -- see
 *  [MainActivity.onCreate]'s splash-gating for why this exists: without it, rotating the phone
 *  while the app was already past the splash would still replay it, since `remember{}` state
 *  inside `setContent{}` normally resets whenever the Activity is recreated. */
private object SplashState {
    var hasShown = false
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* notification is a nice-to-have, not required for playback */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        handleIncomingIntent(intent)

        setContent {
            // Real bug fix: this used to be `remember { mutableStateOf(true) }`, which reset to
            // true (replaying the splash animation) every time the Activity was recreated --
            // including on a plain rotation, since MainActivity wasn't declared to handle that
            // config change itself. Two layers of fix: MainActivity now declares
            // orientation/screenSize/uiMode in its manifest entry so it isn't recreated for those
            // changes at all (the more robust fix -- also stops anything else in the Compose tree
            // from silently losing `remember{}` state on rotation, not just this one flag); and
            // this reads from the process-lifetime SplashState object rather than resetting
            // unconditionally, as a second line of defense for recreations from other causes.
            var showSplash by remember { mutableStateOf(!SplashState.hasShown) }

            if (showSplash) {
                SplashScreen(onFinished = { SplashState.hasShown = true; showSplash = false })
            } else {
                val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
                val accentColorArgb by viewModel.accentColorArgb.collectAsStateWithLifecycle()
                val secondaryColorArgb by viewModel.secondaryColorArgb.collectAsStateWithLifecycle()
                TubeAudioTheme(
                    themeMode = themeMode,
                    accentColorArgb = accentColorArgb,
                    secondaryColorArgb = secondaryColorArgb
                ) {
                    TubeAudioApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    /** Lets a YouTube link be shared into the app via the system share sheet, not just pasted. */
    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)?.trim()
            if (!sharedText.isNullOrBlank()) {
                viewModel.selectTab(AppTab.HOME)
                viewModel.onLinkInputChanged(sharedText)
                viewModel.submitPastedLink()
            }
        }
    }
}
