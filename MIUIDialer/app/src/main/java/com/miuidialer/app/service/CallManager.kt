package com.miuidialer.app.service

import android.telecom.Call
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CallManager {
    private val _currentCall = MutableStateFlow<Call?>(null)
    val currentCall: StateFlow<Call?> = _currentCall.asStateFlow()

    private val _callState = MutableStateFlow(Call.STATE_DISCONNECTED)
    val callState: StateFlow<Int> = _callState.asStateFlow()

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            _callState.value = state
        }
    }

    fun setCall(call: Call?) {
        _currentCall.value?.unregisterCallback(callCallback)
        _currentCall.value = call
        call?.registerCallback(callCallback)
        _callState.value = call?.state ?: Call.STATE_DISCONNECTED
    }

    fun answer() {
        _currentCall.value?.answer(0)
    }

    fun reject() {
        _currentCall.value?.reject(false, null)
    }

    fun disconnect() {
        _currentCall.value?.disconnect()
    }

    fun toggleMute(isMuted: Boolean) {
        _currentCall.value?.let { call ->
            val inCallService = call.details
        }
    }

    fun toggleHold() {
        val call = _currentCall.value ?: return
        if (call.state == Call.STATE_HOLDING) call.unhold() else call.hold()
    }

    fun getCallerName(): String? {
        return _currentCall.value?.details?.callerDisplayName
            ?.takeIf { it.isNotEmpty() }
    }

    fun getCallerNumber(): String {
        return _currentCall.value?.details?.handle?.schemeSpecificPart ?: ""
    }
}
