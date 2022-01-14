package com.vonage.tutorial.phonetoappabdul

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.RemoteMessage
import com.nexmo.client.NexmoCall
import com.nexmo.client.NexmoClient
import com.nexmo.client.NexmoIncomingCallListener
import com.nexmo.client.request_listener.NexmoApiError
import com.nexmo.client.request_listener.NexmoConnectionListener
import com.nexmo.client.request_listener.NexmoRequestListener
import com.nexmo.utils.logger.ILogger

@RequiresApi(Build.VERSION_CODES.O)
class ClientManager(private val context: Context) {

    companion object {
        private val internalClientConnected = MutableLiveData<Boolean>()
        val clientConnected: LiveData<Boolean>
            get() = internalClientConnected
    }

    private var call: NexmoCall? = null
    private lateinit var client: NexmoClient
    private lateinit var telecomManager: TelecomManager
    private lateinit var phoneAccountHandle: PhoneAccountHandle

    init {
        client = NexmoClient.Builder().logLevel(ILogger.eLogLevel.SENSITIVE).build(context)
        val componentName = ComponentName(context, CallConnectionService::class.java)
        telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        phoneAccountHandle = PhoneAccountHandle(componentName, "Vonage Voip Calling")
        val phoneAccount = PhoneAccount.builder(phoneAccountHandle, "Vonage Voip Calling")
            .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER).build()
        telecomManager.registerPhoneAccount(phoneAccount)

        // CSDemo: Start an incoming call ConnectionService Call when there is an incoming call
        FirebaseMessagingService.firebaseRemoteMessage.observeForever {
            startIncomingCall(it)
        }
    }

    fun login() {
        client.login("userJWT")
    }

    fun setClientCallListener(listener: NexmoIncomingCallListener) {
        client.addIncomingCallListener { it ->
            call = it
            listener.onIncomingCall(call)
        }
    }

    fun setClientConnectionListener(listener: NexmoConnectionListener) {
        client.setConnectionListener { status, reason ->
            if (status == NexmoConnectionListener.ConnectionStatus.CONNECTED) {
                internalClientConnected.postValue(true)
            }
            listener.onConnectionStatusChange(status, reason)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startIncomingCall(remoteMessage: RemoteMessage) {
        // CSDemo: If the client is connected, the Client SDK expects your own app code to handle the call
        if (context.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS) == PackageManager.PERMISSION_GRANTED &&
        !client.isConnected) {
            val bytes = ParcelableUtil.marshall(remoteMessage)
            val extras = Bundle()
            extras.putByteArray("pushinfo", bytes)
            telecomManager.isIncomingCallPermitted(phoneAccountHandle)
            // CSDemo: This calls the onCreateIncomingConnection function in the CallConnectionService class
            telecomManager.addNewIncomingCall(phoneAccountHandle, extras)
        }
    }
    @SuppressLint("MissingPermission")
    fun answerCall(listener: NexmoRequestListener<NexmoCall>) {
        call?.answer(listener)
    }

    fun rejectCall(listener: NexmoRequestListener<NexmoCall>) {
        call?.hangup(listener)
        call = null
    }

    fun endCall(listener: NexmoRequestListener<NexmoCall>) {
        call?.hangup(listener)
        call = null
    }

    fun enablePush(token: String, listener: NexmoRequestListener<Void>) {
        client.enablePushNotifications(token, listener)
    }
}