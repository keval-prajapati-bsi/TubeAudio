package com.vibesync.app.localparty;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u000e\n\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0002"}, d2 = {"LOCAL_PARTY_SERVICE_TYPE", "", "app_debug"})
public final class LocalPartyHostKt {
    
    /**
     * `_vibesync._tcp.` -- Android NSD's mDNS/DNS-SD service type string, shared between
     * [LocalPartyHost] (registers it) and [LocalPartyClient] (browses for it). Must match exactly on
     * both sides -- this IS the "namespace" that makes a phone only discover other VibeSync local
     * rooms, not every other mDNS service on the same WiFi (printers, Chromecasts, etc).
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String LOCAL_PARTY_SERVICE_TYPE = "_vibesync._tcp.";
}