package com.vibesync.app.extractor

import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import java.io.IOException
import okhttp3.Request as OkRequest

/** Shared with [com.vibesync.app.download.TrackDownloader] and the ExoPlayer HTTP data source
 *  in `PlaybackService` -- YouTube's CDN (googlevideo.com) can 403 requests for a resolved
 *  stream URL when the User-Agent doesn't look like a real browser/app, so every raw HTTP
 *  request against a resolved URL (streaming playback, downloading) needs to match this one. */
const val USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0 Safari/537.36"

/** Bridges NewPipeExtractor's HTTP abstraction onto our own OkHttp client. */
class OkHttpDownloader(private val client: OkHttpClient) : Downloader() {

    @Throws(IOException::class, ReCaptchaException::class)
    override fun execute(request: Request): Response {
        val requestBody = request.dataToSend()?.toRequestBody()

        val requestBuilder = OkRequest.Builder()
            .method(request.httpMethod(), requestBody)
            .url(request.url())
            .header("User-Agent", USER_AGENT)

        for ((name, values) in request.headers()) {
            when {
                values.size > 1 -> {
                    requestBuilder.removeHeader(name)
                    values.forEach { value -> requestBuilder.addHeader(name, value) }
                }
                values.size == 1 -> requestBuilder.header(name, values[0])
            }
        }

        val response = client.newCall(requestBuilder.build()).execute()

        if (response.code == 429) {
            response.close()
            throw ReCaptchaException("reCAPTCHA challenge requested", request.url())
        }

        val responseBody = response.body?.string()
        val latestUrl = response.request.url.toString()

        return Response(
            response.code,
            response.message,
            response.headers.toMultimap(),
            responseBody,
            latestUrl
        )
    }
}
