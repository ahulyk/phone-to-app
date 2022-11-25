package com.vonage.tutorial.phonetoappabdul

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.RemoteMessage
import com.nexmo.client.*
import com.nexmo.client.request_listener.NexmoApiError
import com.nexmo.client.request_listener.NexmoConnectionListener
import com.nexmo.client.request_listener.NexmoRequestListener


@RequiresApi(Build.VERSION_CODES.O)
class ClientManager(private val context: Context) {

    companion object {
        private val internalClientConnected = MutableLiveData<Boolean>()
        val clientConnected: LiveData<Boolean>
            get() = internalClientConnected
    }

    var call: NexmoCall? = null
    private lateinit var client: NexmoClient
    private lateinit var telecomManager: TelecomManager
    private lateinit var phoneAccountHandle: PhoneAccountHandle

    val callStatus = MutableLiveData(NexmoCallMemberStatus.UNKNOWN)

    val callStatusLister = object : NexmoCallEventListener {
        override fun onMemberStatusUpdated(
            newState: NexmoCallMemberStatus?,
            member: NexmoMember?
        ) {
            callStatus.postValue(newState)
        }

        override fun onMuteChanged(
            newState: NexmoMediaActionState?,
            member: NexmoMember?
        ) {
        }

        override fun onEarmuffChanged(
            newState: NexmoMediaActionState?,
            member: NexmoMember?
        ) {
        }

        override fun onDTMF(dtmf: String?, member: NexmoMember?) {
        }

        override fun onLegTransfer(
            event: NexmoLegTransferEvent?,
            member: NexmoMember?
        ) {
        }

    }

    init {
        client = NexmoClient.get()
        val componentName = ComponentName(context, CallConnectionService::class.java)
        telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        phoneAccountHandle = PhoneAccountHandle(componentName, PhoneAccount.SCHEME_SIP)
        val phoneAccount = PhoneAccount.builder(phoneAccountHandle, PhoneAccount.SCHEME_SIP)
//            .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
//            .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER or PhoneAccount.CAPABILITY_CONNECTION_MANAGER)
            .build()
        telecomManager.registerPhoneAccount(phoneAccount)


//        // CSDemo: Start an incoming call ConnectionService Call when there is an incoming call
//        FirebaseMessagingService.firebaseRemoteMessage.observeForever {
//            startIncomingCall(it)
//        }
    }

    fun login() {
        client.login(App.TOKEN)
    }

    fun setClientCallListener(listener: NexmoIncomingCallListener) {
        client.addIncomingCallListener { result ->
            call = result
            call?.addCallEventListener(callStatusLister)
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
        if (context.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS) == PackageManager.PERMISSION_GRANTED
            && call == null
        //&&
        //!client.isConnected
        ) {
            val bytes = ParcelableUtil.marshall(remoteMessage)
            val extras = Bundle()
            extras.putByteArray("pushinfo", bytes)

//            val connection = CallConnection(context, extras as RemoteMessage)
//            connection.processCall()

//             CSDemo: This calls the onCreateIncomingConnection function in the CallConnectionService class
            telecomManager.isIncomingCallPermitted(phoneAccountHandle)
            telecomManager.addNewIncomingCall(phoneAccountHandle, extras)
        }


//        val action = NotificationCompat.Action.Builder(
//            IconCompat.createWithResource(context, R.drawable.ic_launcher_background),
//            "Accept call",
//            PenInt
//        ).build()

//
//        val notificationManager = NotificationManagerCompat.from(context)
//        val INCOMING_CALL_CHANNEL_ID = "incoming_call"
//        val INCOMING_CALL_CHANNEL_NAME = "in app call"
//
//        val channel = NotificationChannel(
//            INCOMING_CALL_CHANNEL_ID,
//            INCOMING_CALL_CHANNEL_NAME,
//            NotificationManager.IMPORTANCE_DEFAULT
//        )
//        notificationManager.createNotificationChannel(channel)
//
//        val notificationBuilder = NotificationCompat.Builder(
//            context,
//            INCOMING_CALL_CHANNEL_ID
//        )
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setContentTitle("Incoming call")
//            .setContentText("OLX")
//            .setOngoing(true)
//
//        val notification = notificationBuilder.build()
//
//        notificationManager.notify(43424, notification)
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

    fun placeOutgoingCall() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        //  telecomManager.placeCall(Uri.fromParts(PhoneAccount.SCHEME_SIP, "Andriy", null), bundleOf())
//        telecomManager.placeCall(Uri.parse("olx:Andriy"), bundleOf())

        val outgoingUri =
            Uri.fromParts(PhoneAccount.SCHEME_SIP, "Andriy", null)
        val outgoingExtras = Bundle()
        outgoingExtras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
        outgoingExtras.putBundle(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, bundleOf())
        telecomManager.placeCall(outgoingUri, outgoingExtras)
    }

    fun doCall(userName: String, listener: NexmoRequestListener<NexmoCall>) {

        client.serverCall(
            userName, null, object : NexmoRequestListener<NexmoCall> {
                override fun onError(error: NexmoApiError) {
                    listener.onError(error)
                }

                override fun onSuccess(result: NexmoCall?) {
                    call = result
                    call?.addCallEventListener(callStatusLister)
                    listener.onSuccess(result)

                }
            })
    }

    fun enablePush(token: String, listener: NexmoRequestListener<Void>) {
        client.enablePushNotifications(token, listener)
    }
}