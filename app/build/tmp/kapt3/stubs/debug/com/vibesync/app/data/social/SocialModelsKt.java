package com.vibesync.app.data.social;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\b\n\u0000\n\u0002\u0010\t\n\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0002"}, d2 = {"ONLINE_WINDOW_MILLIS", "", "app_debug"})
public final class SocialModelsKt {
    
    /**
     * Online/offline is approximated from a heartbeat, not real presence -- this app has no Realtime
     * Database (the product Firebase's `onDisconnect()` actually needs), only Firestore, so there's
     * no reliable signal for "this device just lost its connection." Instead every signed-in device
     * periodically bumps its own `users/{uid}.lastSeenAt`, and anyone reading it treats "seen within
     * the last [ONLINE_WINDOW_MILLIS]" as online -- a disclosed approximation, same honesty pattern
     * as this app's other "no real backend for X" caveats (Trending's canned queries, Popular
     * artists' fixed list). A killed app / lost connection shows as online for up to that window,
     * then quietly goes stale.
     */
    public static final long ONLINE_WINDOW_MILLIS = 90000L;
}