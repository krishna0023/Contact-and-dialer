package com.miuidialer.app.service

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService
import com.miuidialer.app.ui.screens.InCallActivity

class InCallServiceImpl : InCallService() {

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        CallManager.setCall(call)
        val intent = Intent(this, InCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        CallManager.setCall(null)
    }
}
