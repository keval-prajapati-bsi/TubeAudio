package com.vibesync.app.data

import android.content.Context
import android.os.StatFs
import java.io.File

/** [usedByAppBytes] is real (sum of downloaded audio files' sizes on disk); [deviceTotalBytes] is
 *  the whole partition [Context.getFilesDir] lives on, from [StatFs] -- shown next to it for
 *  scale, same as Android's own per-app storage screen, not a fabricated app-specific quota. */
data class StorageInfo(val usedByAppBytes: Long, val deviceTotalBytes: Long, val deviceFreeBytes: Long) {
    val usedFraction: Float
        get() = if (deviceTotalBytes <= 0) 0f else (usedByAppBytes.toFloat() / deviceTotalBytes.toFloat()).coerceIn(0f, 1f)
}

object StorageStats {
    fun compute(context: Context): StorageInfo {
        val audioDir = File(context.filesDir, "audio")
        val usedByApp = audioDir.listFiles()?.sumOf { it.length() } ?: 0L
        val statFs = StatFs(context.filesDir.path)
        return StorageInfo(
            usedByAppBytes = usedByApp,
            deviceTotalBytes = statFs.totalBytes,
            deviceFreeBytes = statFs.availableBytes
        )
    }
}
