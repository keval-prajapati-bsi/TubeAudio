package com.vibesync.app.playback;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\n\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0004"}, d2 = {"DRIFT_CHECK_INTERVAL_MS", "", "DRIFT_TOLERANCE_MS", "PARTY_QUEUE_ID", "app_debug"})
public final class PartySyncControllerKt {
    
    /**
     * A party queue plays through the same single-track "queue of one" mechanism the app already
     * uses for ad-hoc direct-play (see [com.vibesync.app.data.ADHOC_QUEUE_ID]) -- party tracks are
     * never written to the local Room library.
     */
    public static final long PARTY_QUEUE_ID = -4L;
    
    /**
     * Snap back onto the shared position if a device has drifted more than this far from where it
     * should be -- small natural jitter (buffering, decode timing) shouldn't cause visible re-seeks.
     * Tightened from 1200ms/8s to 700ms/5s: with more people in a party, more devices are
     * independently buffering/decoding at slightly different rates, so catching drift sooner keeps
     * everyone closer together as the party scales past a couple of phones.
     */
    private static final long DRIFT_TOLERANCE_MS = 700L;
    private static final long DRIFT_CHECK_INTERVAL_MS = 5000L;
}