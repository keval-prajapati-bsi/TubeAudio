package com.vibesync.app.playback;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\n\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0004"}, d2 = {"LOCAL_CHECK_INTERVAL_MS", "", "LOCAL_DRIFT_TOLERANCE_MS", "LOCAL_PARTY_QUEUE_ID", "app_debug"})
public final class LocalPartySyncControllerKt {
    
    /**
     * A local room plays through the same single-track "queue of one" mechanism the Firebase party
     * used before this app's gapless-prefetch fix (see [PARTY_QUEUE_ID]'s sibling constant here) --
     * a deliberate v1 simplification, not an oversight: porting the full prefetch/gapless mechanism
     * to a second, socket-driven sync engine in the same round as building the socket layer itself
     * was more risk than this round's budget could absorb. A short poll-driven gap between songs is
     * the known, disclosed trade-off -- see version27/CHANGES.txt. Worth revisiting the same way the
     * Firebase party's gap was fixed, once this is confirmed working end-to-end on real devices.
     */
    public static final long LOCAL_PARTY_QUEUE_ID = -5L;
    private static final long LOCAL_DRIFT_TOLERANCE_MS = 700L;
    private static final long LOCAL_CHECK_INTERVAL_MS = 2000L;
}