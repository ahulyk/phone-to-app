package com.vonage.tutorial.phonetoappabdul

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nexmo.client.NexmoClient
import com.nexmo.client.request_listener.NexmoApiError
import com.nexmo.client.request_listener.NexmoRequestListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class FirebaseMessagingService: FirebaseMessagingService() {

    companion object {
//        private val internalRemoteMessage = MutableLiveData<RemoteMessage>()
//        val firebaseRemoteMessage: LiveData<RemoteMessage>
//            get() = internalRemoteMessage
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        NexmoClient.get().enablePushNotifications(token, object: NexmoRequestListener<Void> {
            override fun onSuccess(result: Void?) { }
            override fun onError(error: NexmoApiError) { }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // CSDemo: When an incoming call comes in, notify the ClientManager
        Log.d("FirebaseMessagingService", remoteMessage.toString())
        App.clientManager.startIncomingCall(remoteMessage)
        //internalRemoteMessage.postValue(remoteMessage)
    }
}