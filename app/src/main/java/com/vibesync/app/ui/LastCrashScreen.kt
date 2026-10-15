package com.vibesync.app.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibesync.app.ui.theme.CardBorder
import com.vibesync.app.ui.theme.ErrorRed
import com.vibesync.app.ui.theme.TextSecondary

/** Shown as the very first screen (before even the auth gate) whenever [com.vibesync.app.data.CrashLogger]
 *  finds a saved crash from the app's previous run -- a stand-in for a real crash-reporting
 *  service (Crashlytics, Play Console) that this dev environment has no way to pull from. Copy
 *  the text and send it back; Dismiss clears the saved log and continues into the app normally. */
@Composable
fun LastCrashScreen(crashText: String, onDismiss: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text(
            text = "VibeSync crashed last time",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ErrorRed
        )
        Text(
            text = "Copy this and send it back so the exact cause can be found.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = crashText,
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(crashText))
                    Toast.makeText(context, "Crash log copied", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Text("Copy")
            }
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp)) {
                Text("Dismiss")
            }
        }
    }
}
