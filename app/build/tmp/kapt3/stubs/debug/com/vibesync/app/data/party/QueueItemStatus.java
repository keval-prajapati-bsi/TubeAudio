package com.vibesync.app.data.party;

/**
 * No separate "playing" status -- whether an APPROVED item is "up next" or "now playing" is
 * determined by comparing its id against [PartyDoc.currentQueueItemId], not by its own status
 * field. This keeps "go to next" / "go back" / "jump to this song" all boil down to the same two
 * writes (retire the old current item to PLAYED, if any; point the party doc at the new one)
 * instead of needing a 4-state machine.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/vibesync/app/data/party/QueueItemStatus;", "", "(Ljava/lang/String;I)V", "PENDING", "APPROVED", "PLAYED", "app_debug"})
public enum QueueItemStatus {
    /*public static final*/ PENDING /* = new PENDING() */,
    /*public static final*/ APPROVED /* = new APPROVED() */,
    /*public static final*/ PLAYED /* = new PLAYED() */;
    
    QueueItemStatus() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.vibesync.app.data.party.QueueItemStatus> getEntries() {
        return null;
    }
}