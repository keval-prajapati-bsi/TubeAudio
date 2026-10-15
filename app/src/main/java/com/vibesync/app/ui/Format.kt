package com.vibesync.app.ui

import java.util.Locale

/** "46m" / "1h 12m" -- a playlist header's total-length summary, distinct from [formatDuration]
 *  (which is for a single track's elapsed/total time as m:ss). */
fun formatTotalDuration(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}

fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 MB"
    val gb = bytes / 1_000_000_000.0
    if (gb >= 1.0) return String.format(Locale.US, "%.1f GB", gb)
    val mb = bytes / 1_000_000.0
    return String.format(Locale.US, "%.0f MB", mb)
}

fun formatDuration(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%d:%02d", minutes, seconds)
    }
}
