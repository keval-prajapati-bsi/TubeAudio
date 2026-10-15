package com.vibesync.app

import android.app.Application
import com.vibesync.app.data.CrashLogger
import com.vibesync.app.extractor.OkHttpDownloader
import okhttp3.OkHttpClient
import org.schabi.newpipe.extractor.NewPipe
import java.util.concurrent.TimeUnit

class TubeAudioApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        // Installed first, before anything else, so even a crash during startup (NewPipe init,
        // AppContainer construction) gets caught -- see CrashLogger's doc for why this exists.
        CrashLogger.install(this)

        val httpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
        NewPipe.init(OkHttpDownloader(httpClient))

        container = AppContainer(this)
    }
}
