package com.vibesync.app.localparty

import com.vibesync.app.data.localparty.DiscoveredRoom
import com.vibesync.app.data.localparty.LocalRoomState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.UUID

/** A guest's live connection to someone else's [LocalPartyHost] -- the client-side half of the
 *  same newline-delimited-JSON protocol (see that class's doc comment). Read loop runs for the
 *  whole life of the connection; [state] mirrors whatever the host last broadcast. */
class LocalPartyClient(private val displayName: String) {
    val myId: String = UUID.randomUUID().toString()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var socket: Socket? = null
    private var writer: OutputStreamWriter? = null

    private val _state = MutableStateFlow<LocalRoomState?>(null)
    val state: StateFlow<LocalRoomState?> = _state.asStateFlow()

    private val _connectionError = MutableStateFlow<String?>(null)
    val connectionError: StateFlow<String?> = _connectionError.asStateFlow()

    private val _wasKicked = MutableStateFlow(false)
    val wasKicked: StateFlow<Boolean> = _wasKicked.asStateFlow()

    fun connect(room: DiscoveredRoom) {
        scope.launch {
            try {
                val s = Socket(room.host, room.port)
                socket = s
                val w = OutputStreamWriter(s.getOutputStream())
                writer = w
                send(JSONObject().put("type", "hello").put("id", myId).put("displayName", displayName))

                val reader = BufferedReader(InputStreamReader(s.getInputStream()))
                while (true) {
                    val line = reader.readLine() ?: break
                    val json = JSONObject(line)
                    when (json.optString("type")) {
                        "state" -> _state.value = LocalRoomState.fromJson(json)
                        "kicked" -> {
                            _wasKicked.value = true
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                if (!_wasKicked.value) {
                    _connectionError.value = "Lost connection to the room (${e.message ?: e::class.simpleName})."
                }
            } finally {
                _state.value = null
            }
        }
    }

    private fun send(json: JSONObject) {
        try {
            val w = writer ?: return
            w.write(json.toString())
            w.write("\n")
            w.flush()
        } catch (_: Exception) {
            // The read loop above will notice the same broken connection and surface an error.
        }
    }

    fun requestSong(videoId: String, title: String, thumbnailUrl: String?, url: String, durationSeconds: Long) {
        send(
            JSONObject().put("type", "requestSong")
                .put("videoId", videoId)
                .put("title", title)
                .put("thumbnailUrl", thumbnailUrl)
                .put("url", url)
                .put("durationSeconds", durationSeconds)
                .put("addedByName", displayName)
        )
    }

    fun disconnect() {
        try { send(JSONObject().put("type", "leave")) } catch (_: Exception) {}
        try { socket?.close() } catch (_: Exception) {}
    }
}
