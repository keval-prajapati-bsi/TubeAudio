package com.vibesync.app.data

import android.content.Context
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** A minimal, dependency-free crash reporter: installs a global uncaught-exception handler that
 *  writes the full stack trace to a local file before letting the crash proceed exactly as it
 *  normally would (the process still dies -- this only makes sure the *reason* survives the
 *  crash instead of vanishing with it). Built because there's no way to pull a real `adb logcat`
 *  or Play Console crash report for this app right now -- the fallback is [ui.LastCrashScreen],
 *  which reads this file back on next launch so the exact stack trace can be read/copied directly
 *  off the phone, no computer needed. */
object CrashLogger {
    private const val FILE_NAME = "last_crash.txt"

    fun install(context: Context) {
        val appContext = context.applicationContext
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                writeCrash(appContext, thread, throwable)
            } catch (_: Exception) {
                // Never let a failure in the logger itself block the real crash from proceeding.
            }
            previousHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun writeCrash(context: Context, thread: Thread, throwable: Throwable) {
        val writer = StringWriter()
        throwable.printStackTrace(PrintWriter(writer))
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val text = "Crash at $timestamp on thread \"${thread.name}\"\n\n$writer"
        File(context.filesDir, FILE_NAME).writeText(text)
    }

    fun readLastCrash(context: Context): String? {
        val file = File(context.applicationContext.filesDir, FILE_NAME)
        return if (file.exists()) file.readText() else null
    }

    fun clear(context: Context) {
        File(context.applicationContext.filesDir, FILE_NAME).delete()
    }
}
