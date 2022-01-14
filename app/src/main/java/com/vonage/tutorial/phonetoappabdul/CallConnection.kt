package com.vonage.tutorial.phonetoappabdul

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.telecom.Connection
import android.telecom.DisconnectCause
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.RemoteMessage
import com.nexmo.client.*
import com.nexmo.client.request_listener.NexmoApiError
import com.nexmo.client.request_listener.NexmoRequestListener
import kotlin.math.log

class CallConnection(private val context: Context,
                     private val pushInfo: RemoteMessage
) : Connection() {

    var activeCall: NexmoCall? = null
    var shouldAnswerCall = false

    @RequiresApi(Build.VERSION_CODES.O)
    fun processCall() {
        /*
        CSDemo: To process and answer a call, your user needs to be logged in.
        When the user is logged in, you can process the push. If the push processes
        correctly you will get an incoming call. If the user has clicked answer
        before the call comes in, run the answer call code again.
         */
        ClientManager(context).login()
        ClientManager.clientConnected.observeForever { connected ->
            if (connected) {
                NexmoClient.get().processNexmoPush(pushInfo.data, object : NexmoPushEventListener {
                    override fun onIncomingCall(call: NexmoCall?) {
                        activeCall = call
                        if (shouldAnswerCall) {
                            answerCall()
                        }
                    }
                    override fun onNewEvent(event: NexmoEvent?) {}
                    override fun onError(error: NexmoApiError?) {}
                })
            } else {
                setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
                destroy()
            }
        }
    }

    override fun onDisconnect() {
        endCall()
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }

    override fun onAnswer() {
        answerCall()
    }

    @SuppressLint("MissingPermission")
    private fun answerCall() {
        if (activeCall != null) {
            activeCall?.answer(object : NexmoRequestListener<NexmoCall> {
                override fun onError(error: NexmoApiError) {}
                override fun onSuccess(result: NexmoCall?) {
                    setInitialized()
                    setActive()
                }
            })
            activeCall?.addCallEventListener(object: NexmoCallEventListener {
                override fun onMemberStatusUpdated(newState: NexmoCallMemberStatus?, member: NexmoMember?) {
                    if (newState == NexmoCallMemberStatus.COMPLETED || newState == NexmoCallMemberStatus.CANCELLED) {
                        endCall()
                        setDisconnected(DisconnectCause(DisconnectCause.REMOTE))
                        destroy()
                    }
                }
                override fun onMuteChanged(newState: NexmoMediaActionState?, member: NexmoMember?) {}
                override fun onEarmuffChanged(newState: NexmoMediaActionState?, member: NexmoMember?) {}
                override fun onDTMF(dtmf: String?, member: NexmoMember?) {}
            })
        } else {
            shouldAnswerCall = true
            setRinging()
        }
    }

    private fun endCall() {
        activeCall?.hangup(object : NexmoRequestListener<NexmoCall> {
            override fun onError(error: NexmoApiError) {}
            override fun onSuccess(result: NexmoCall?) {}
        })
        shouldAnswerCall = false
        activeCall = null
    }

    override fun onReject() {
        if (activeCall != null) {
            endCall()
        }
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }
}