package com.vibesync.app.localparty

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.vibesync.app.data.localparty.LocalMember
import com.vibesync.app.data.localparty.LocalQueueItem
import com.vibesync.app.data.localparty.LocalRoomState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.UUID

/** `_vibesync._tcp.` -- Android NSD's mDNS/DNS-SD service type string, shared between
 *  [LocalPartyHost] (registers it) and [LocalPartyClient] (browses for it). Must match exactly on
 *  both sides -- this IS the "namespace" that makes a phone only discover other VibeSync local
 *  rooms, not every other mDNS service on the same WiFi (printers, Chromecasts, etc). */
const val LOCAL_PARTY_SERVICE_TYPE = "_vibesync._tcp."

private data class ConnectedGuest(val id: String, val socket: Socket, val writer: OutputStreamWriter)

/**
 * Runs entirely on the host's own phone: a raw TCP [ServerSocket] on an OS-assigned free port,
 * advertised on the local network via NSD so guests can find it without typing an IP -- no
 * internet, no Firebase, nothing outside this WiFi network required at all. This is a genuinely
 * separate peer, not a scaled-down version of the Firebase Party feature.
 *
 * Protocol is newline-delimited JSON (one object per line -- plain JSON alone has no message
 * framing over a raw byte stream, so a delimiter is required): guests send `hello`/`requestSong`/
 * `leave`; the host only ever sends a `state` message (the FULL current [LocalRoomState] snapshot,
 * every time anything changes) or `kicked`. Sending the whole snapshot each time rather than diffs
 * is deliberately simple -- there's no Firestore-style targeted-write concept over a raw socket,
 * and a LAN party's member/queue lists are small enough that this costs nothing meaningful.
 *
 * **Known, disclosed simplification**: the local socket has no authentication/pairing step --
 * anyone who can see the mDNS advertisement on the same WiFi can connect and request songs. This
 * matches the trust model of the feature (a room you're hosting for people already in the same
 * physical space/network), not a public internet-facing service, but it's worth knowing this isn't
 * hardened the way the Firebase party (real Firebase Auth + security rules) is.
 */
class LocalPartyHost(context: Context, hostDisplayName: String, roomName: String) {
    val hostId: String = UUID.randomUUID().toString()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val stateLock = Mutex()
    private val guests = mutableMapOf<String, ConnectedGuest>()

    private var serverSocket: ServerSocket? = null
    private val nsdManager = context.applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager
    private var registrationListener: NsdManager.RegistrationListener? = null

    private val _state = MutableStateFlow(
        LocalRoomState(roomName = roomName, members = listOf(LocalMember(hostId, hostDisplayName, isHost = true)))
    )
    val state: StateFlow<LocalRoomState> = _state.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun start() {
        scope.launch {
            try {
                val server = ServerSocket(0)
                serverSocket = server
                registerNsd(server.localPort)
                acceptLoop(server)
            } catch (e: Exception) {
                _errorMessage.value = "Couldn't start the local room (${e.message ?: e::class.simpleName})."
            }
        }
    }

    private fun registerNsd(port: Int) {
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = _state.value.roomName
            serviceType = LOCAL_PARTY_SERVICE_TYPE
            this.port = port
        }
        val listener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(info: NsdServiceInfo) {}
            override fun onRegistrationFailed(info: NsdServiceInfo, errorCode: Int) {
                _errorMessage.value = "Couldn't advertise the room on this network (error $errorCode)."
            }
            override fun onServiceUnregistered(info: NsdServiceInfo) {}
            override fun onUnregistrationFailed(info: NsdServiceInfo, errorCode: Int) {}
        }
        registrationListener = listener
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, listener)
    }

    private suspend fun acceptLoop(server: ServerSocket) {
        while (true) {
            val socket = try {
                server.accept()
            } catch (e: Exception) {
                break // serverSocket.close() (see stop()) lands here -- expected, not an error
            }
            scope.launch { handleGuest(socket) }
        }
    }

    private suspend fun handleGuest(socket: Socket) {
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        val writer = OutputStreamWriter(socket.getOutputStream())
        var guestId: String? = null
        try {
            while (true) {
                val line = reader.readLine() ?: break
                val json = JSONObject(line)
                when (json.optString("type")) {
                    "hello" -> {
                        val id = json.getString("id")
                        val name = json.getString("displayName")
                        guestId = id
                        stateLock.withLock {
                            guests[id] = ConnectedGuest(id, socket, writer)
                            _state.value = _state.value.copy(members = _state.value.members + LocalMember(id, name, isHost = false))
                        }
                        broadcastState()
                    }
                    "requestSong" -> {
                        stateLock.withLock {
                            val current = _state.value
                            val videoId = json.getString("videoId")
                            if (current.queue.none { it.videoId == videoId } && current.currentVideoId != videoId) {
                                val item = LocalQueueItem(
                                    id = UUID.randomUUID().toString(),
                                    videoId = videoId,
                                    title = json.getString("title"),
                                    thumbnailUrl = json.optString("thumbnailUrl").takeIf { it.isNotEmpty() },
                                    url = json.getString("url"),
                                    durationSeconds = json.getLong("durationSeconds"),
                                    addedByName = json.optString("addedByName", "Someone")
                                )
                                _state.value = current.copy(queue = current.queue + item)
                            }
                        }
                        broadcastState()
                    }
                    "leave" -> break
                }
            }
        } catch (_: Exception) {
            // Connection dropped -- treated the same as an explicit "leave" below.
        } finally {
            guestId?.let { removeGuest(it) }
            try { socket.close() } catch (_: Exception) {}
        }
    }

    private suspend fun removeGuest(id: String) {
        stateLock.withLock {
            guests.remove(id)
            _state.value = _state.value.copy(members = _state.value.members.filterNot { it.id == id })
        }
        broadcastState()
    }

    private suspend fun broadcastState() {
        val json = _state.value.toJson().put("type", "state")
        val snapshot = stateLock.withLock { guests.values.toList() }
        for (guest in snapshot) {
            try {
                sendTo(guest.writer, json)
            } catch (_: Exception) {
                removeGuest(guest.id)
            }
        }
    }

    private fun sendTo(writer: OutputStreamWriter, json: JSONObject) {
        writer.write(json.toString())
        writer.write("\n")
        writer.flush()
    }

    private fun updateState(transform: (LocalRoomState) -> LocalRoomState) {
        scope.launch {
            stateLock.withLock { _state.value = transform(_state.value) }
            broadcastState()
        }
    }

    /** Sets what's playing now, freezing a fresh start timestamp as the sync anchor -- same
     *  formula every guest's [LocalPartyClient] uses to compute its own expected position (see
     *  [LocalRoomState]'s doc comment). Removes the item from the queue if it came from there
     *  (an ad-hoc host play passes null). */
    fun setCurrentTrack(videoId: String, title: String, thumbnailUrl: String?, url: String, durationSeconds: Long, fromQueueItemId: String?) {
        updateState {
            it.copy(
                currentVideoId = videoId,
                currentTitle = title,
                currentThumbnailUrl = thumbnailUrl,
                currentUrl = url,
                currentDurationSeconds = durationSeconds,
                isPlaying = true,
                startPositionMs = 0L,
                playbackStartedAtMillis = System.currentTimeMillis(),
                queue = if (fromQueueItemId != null) it.queue.filterNot { q -> q.id == fromQueueItemId } else it.queue
            )
        }
    }

    fun setPlaybackState(isPlaying: Boolean, positionMs: Long) {
        updateState { it.copy(isPlaying = isPlaying, startPositionMs = positionMs, playbackStartedAtMillis = System.currentTimeMillis()) }
    }

    fun addToQueue(item: LocalQueueItem) {
        updateState { if (it.queue.any { q -> q.videoId == item.videoId } || it.currentVideoId == item.videoId) it else it.copy(queue = it.queue + item) }
    }

    fun reorderQueue(newOrder: List<LocalQueueItem>) {
        updateState { it.copy(queue = newOrder) }
    }

    fun removeFromQueue(itemId: String) {
        updateState { it.copy(queue = it.queue.filterNot { q -> q.id == itemId }) }
    }

    fun kick(memberId: String) {
        scope.launch {
            val guest = stateLock.withLock { guests[memberId] }
            if (guest != null) {
                try { sendTo(guest.writer, JSONObject().put("type", "kicked")) } catch (_: Exception) {}
                try { guest.socket.close() } catch (_: Exception) {}
            }
            removeGuest(memberId)
        }
    }

    fun stop() {
        registrationListener?.let { try { nsdManager.unregisterService(it) } catch (_: Exception) {} }
        try { serverSocket?.close() } catch (_: Exception) {}
        scope.launch {
            val snapshot = stateLock.withLock { guests.values.toList().also { guests.clear() } }
            snapshot.forEach { try { it.socket.close() } catch (_: Exception) {} }
        }
    }
}
