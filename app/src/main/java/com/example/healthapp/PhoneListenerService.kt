package com.example.healthapp

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class PhoneListenerService: WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "onMessageReceived(): $messageEvent")
        Log.d(TAG, String(messageEvent.data))
        if (messageEvent.path == MESSAGE_PATH) {
            // This is where we handle the received message data
            handleReceivedMessage(messageEvent.data)
        }
    }

    private fun handleReceivedMessage(data: ByteArray) {
        val message = String(data)
        Log.d(TAG, "Processing received message in background: $message")
    }


    companion object{
        private const val TAG = "PhoneListenerService"
        private const val MESSAGE_PATH = "/deploy"
    }
}