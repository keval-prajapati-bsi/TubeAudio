package com.vibesync.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.vibesync.app.ui.theme.ErrorRed

/** Shared confirmation prompt for every destructive action app-wide (delete a playlist/track,
 *  leave/close a party or local room, kick/remove a member, and anything else that reaches for
 *  this dialog) -- the confirm button is always styled red, and the haptic tap on confirm here is
 *  a single edit that covers every one of those call sites at once, one of the highest-leverage
 *  spots in the whole app to add feedback for "you just did something that can't be undone". */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String = "Delete",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onConfirm()
                onDismiss()
            }) {
                Text(confirmLabel, color = ErrorRed)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
