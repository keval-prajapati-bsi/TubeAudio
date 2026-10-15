package com.vibesync.app.extractor;

/**
 * Bridges NewPipeExtractor's HTTP abstraction onto our own OkHttp client.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/vibesync/app/extractor/OkHttpDownloader;", "Lorg/schabi/newpipe/extractor/downloader/Downloader;", "client", "Lokhttp3/OkHttpClient;", "(Lokhttp3/OkHttpClient;)V", "execute", "Lorg/schabi/newpipe/extractor/downloader/Response;", "request", "Lorg/schabi/newpipe/extractor/downloader/Request;", "app_debug"})
public final class OkHttpDownloader extends org.schabi.newpipe.extractor.downloader.Downloader {
    @org.jetbrains.annotations.NotNull()
    private final okhttp3.OkHttpClient client = null;
    
    public OkHttpDownloader(@org.jetbrains.annotations.NotNull()
    okhttp3.OkHttpClient client) {
        super();
    }
    
    @java.lang.Override()
    @kotlin.jvm.Throws(exceptionClasses = {java.io.IOException.class, org.schabi.newpipe.extractor.exceptions.ReCaptchaException.class})
    @org.jetbrains.annotations.NotNull()
    public org.schabi.newpipe.extractor.downloader.Response execute(@org.jetbrains.annotations.NotNull()
    org.schabi.newpipe.extractor.downloader.Request request) throws java.io.IOException, org.schabi.newpipe.extractor.exceptions.ReCaptchaException {
        return null;
    }
}