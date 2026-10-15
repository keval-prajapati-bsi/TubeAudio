package com.vibesync.app.localparty

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.vibesync.app.data.localparty.DiscoveredRoom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Browses the local network for other phones' [LocalPartyHost]s via NSD -- the "here's a list of
 *  rooms available in your local network" step from the original ask, before actually joining
 *  one. [NsdManager.resolveService] (the classic, non-callback API) is deprecated on API 34 in
 *  favor of [NsdManager.registerServiceInfoCallback], but still fully functional -- kept here for
 *  simplicity since minSdk 26 needs to support it either way; a pre-existing style of deprecation
 *  warning this app already has plenty of elsewhere (AutoMirrored icons, etc), not a functional
 *  concern. */
class LocalPartyDiscovery(context: Context) {
    private val nsdManager = context.applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager
    private var discoveryListener: NsdManager.DiscoveryListener? = null

    private val _rooms = MutableStateFlow<List<DiscoveredRoom>>(emptyList())
    val rooms: StateFlow<List<DiscoveredRoom>> = _rooms.asStateFlow()

    private val _isDiscovering = MutableStateFlow(false)
    val isDiscovering: StateFlow<Boolean> = _isDiscovering.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun start() {
        if (discoveryListener != null) return
        _rooms.value = emptyList()
        _errorMessage.value = null
        val listener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String) {
                _isDiscovering.value = true
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                if (service.serviceType.trimEnd('.') != LOCAL_PARTY_SERVICE_TYPE.trimEnd('.')) return
                nsdManager.resolveService(service, object : NsdManager.ResolveListener {
                    override fun onServiceResolved(resolved: NsdServiceInfo) {
                        val host = resolved.host?.hostAddress ?: return
                        val room = DiscoveredRoom(name = resolved.serviceName, host = host, port = resolved.port)
                        _rooms.value = (_rooms.value.filterNot { it.name == room.name } + room)
                    }

                    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                        // Best-effort -- this one room just won't show up; other discoveries are unaffected.
                    }
                })
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                _rooms.value = _rooms.value.filterNot { it.name == service.serviceName }
            }

            override fun onDiscoveryStopped(serviceType: String) {
                _isDiscovering.value = false
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                _isDiscovering.value = false
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                _isDiscovering.value = false
            }
        }
        discoveryListener = listener
        try {
            nsdManager.discoverServices(LOCAL_PARTY_SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, listener)
        } catch (e: SecurityException) {
            discoveryListener = null
            _errorMessage.value = "Local room discovery needs the Nearby devices / Location permission -- check this app's permissions in system Settings."
        }
    }

    fun stop() {
        discoveryListener?.let {
            try { nsdManager.stopServiceDiscovery(it) } catch (_: Exception) {}
        }
        discoveryListener = null
        _isDiscovering.value = false
    }
}
