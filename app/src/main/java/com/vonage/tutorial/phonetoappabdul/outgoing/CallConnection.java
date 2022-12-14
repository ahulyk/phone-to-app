package com.vonage.tutorial.phonetoappabdul.outgoing;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.nexmo.client.NexmoCall;
import com.nexmo.client.NexmoCallEventListener;
import com.nexmo.client.NexmoCallMemberStatus;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoLegTransferEvent;
import com.nexmo.client.NexmoMediaActionState;
import com.nexmo.client.NexmoMember;
import com.nexmo.client.request_listener.NexmoApiError;
import com.nexmo.client.request_listener.NexmoConnectionListener;
import com.nexmo.client.request_listener.NexmoRequestListener;
import com.nexmo.utils.ILogger;

import org.jetbrains.annotations.Nullable;

@TargetApi(Build.VERSION_CODES.M)
public class CallConnection extends Connection {

    private static final String TAG = CallConnection.class.getSimpleName();

    private final Context mContext;

    // something should be handled in clientManager
    private String otherUser = "Andriy";
    private String yourJWT = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2wiOnsicGF0aHMiOnsiLyovYXBwbGljYXRpb25zLyoqIjp7fSwiLyovY29udmVyc2F0aW9ucy8qKiI6e30sIi8qL2RldmljZXMvKioiOnt9LCIvKi9pbWFnZS8qKiI6e30sIi8qL2tub2NraW5nLyoqIjp7fSwiLyovbGVncy8qKiI6e30sIi8qL21lZGlhLyoqIjp7fSwiLyovcHVzaC8qKiI6e30sIi8qL3Nlc3Npb25zLyoqIjp7fSwiLyovdXNlcnMvKioiOnt9fX0sImFwcGxpY2F0aW9uX2lkIjoiMzkxNjc4ZDUtYjkzMy00N2Q3LWFkMTItZWNhMWVkZTNmMmVmIiwiZXhwIjoxNjY5NzI5MTUzLCJpYXQiOjE2Njk2NDI3NTMsImp0aSI6ImJlZmE4NWQzLWZhZmUtYzI4OS1hODA3LWEwYWUwNTBkMWNkNSIsInN1YiI6IkFuZHJpeTIifQ.nq8QcfALUlJtrupES896zxjpUjiT9Zadu46ZWJvAW_fugqs6JavTgU56Ji0W_TGgI4WUzVAqs7-w64A5YUjaeusuB7RMdV1yHoHSwFcTGdEH92K0eopbiI6UO6PJrA_X-jm-doziVYZsWbeWLx4slzHFaaYV1o-rEC8MtPm-hYN6Op8ml-KevqpmasD8V4KT8615x5qOyjllA5xJR5guV4rsVhPWST1Ii_7lZp-c7DT8Rw5D0Mq0MWU_5M7kJfuZJelLjnAHyK03FfdoHAINuVhpy1AgUHKirhbbJwf5TObd0sLPcpFZGACNT_rP3Zyb5PZiZXzFnPaNUXexBcCTmw";

    public Boolean shouldAnswerCall = Boolean.FALSE;

    private NexmoCall activeCall;

    private NexmoClient client;

    private NexmoCallEventListener callEventListener;

    public CallConnection(Context context) {
        super();
        Log.i(TAG, "CallConnection()");
        this.mContext = context;

        // initialize NexmoCallEventListener
        callEventListener = new NexmoCallEventListener() {
            @Override
            public void onMemberStatusUpdated(NexmoCallMemberStatus callStatus, NexmoMember member) {
                Log.d(TAG,
                        "NexmoCallEventListener:onMemberStatusUpdated"
                                + "NexmoMember:"
                                + member.getChannel()
                                + " NexmoCallMemberStatus:"
                                + callStatus.toString()
                );
                if (callStatus == NexmoCallMemberStatus.COMPLETED || callStatus == NexmoCallMemberStatus.CANCELLED) {
                    endCall();
                    setDisconnected(new DisconnectCause(DisconnectCause.REMOTE));
                    destroy();
                }
                if (callStatus == NexmoCallMemberStatus.ANSWERED && !member.getUser().getName().equals("Andriy2")) {
                    setInitialized();
                    setActive();
                }
            }

            @Override
            public void onMuteChanged(NexmoMediaActionState newState, NexmoMember member) {
            }

            @Override
            public void onEarmuffChanged(NexmoMediaActionState newState, NexmoMember member) {
            }

            @Override
            public void onDTMF(String dtmf, NexmoMember member) {
            }

            @Override
            public void onLegTransfer(NexmoLegTransferEvent event, NexmoMember member) {
            }
        };
    }

    @Override
    public void onDisconnect() {
        super.onDisconnect();
        Log.i(TAG, "onDisconnect()");
        endCall();
        setDisconnected(new DisconnectCause(DisconnectCause.LOCAL));
        destroy();
    }

    @Override
    public void onReject() {
        super.onReject();
        Log.i(TAG, "onReject()");
        if (activeCall != null) {
            endCall();
        }
        setDisconnected(new DisconnectCause(DisconnectCause.REJECTED));
        destroy();
    }

    @Override
    public void onAbort() {
        super.onAbort();
        Log.i(TAG, "onAbort()");
        setDisconnected(new DisconnectCause(DisconnectCause.CANCELED));
        destroy();
    }

    @Override
    public void onAnswer(int videoState) {
        answerCall();
    }

    @Override
    public void onStateChanged(int state) {
        super.onStateChanged(state);
        Log.i(TAG, "onStateChanged() state = " + state);
    }

    public void processCall(String direction) {

        // if not login, login the user, something should be handled in clientManager
        // initialize NexmoClient
        client = NexmoClient.get();
        if (client == null) {
            client = new NexmoClient
                    .Builder()
                    .restEnvironmentHost("https://api-us-1.nexmo.com")
                    .environmentHost("https://ws-us-1.nexmo.com")
                    .imageProcessingServiceUrl("https://api-us-1.nexmo.com/v1/image")
                    .logLevel(
                            ILogger.eLogLevel.SENSITIVE
                    )
                    .logKey(0x0L)
                    .build(mContext);
        }
        // add connection listener to monitor connection state
        client.setConnectionListener((connectionStatus, connectionStatusReason) -> {
            if (connectionStatus == NexmoConnectionListener.ConnectionStatus.CONNECTED) {
                if (direction == "outbound") {
                    makeCall();
                }
            }
        });
        if (client.isConnected()) {
            if (direction == "outbound") {
                makeCall();
            }
        }

        // add incoming call listener for incoming calls
        client.addIncomingCallListener(it -> {
            activeCall = it;
        });
        //
        client.login(yourJWT);
    }

    public void endCall() {
        if (activeCall == null) {
            shouldAnswerCall = Boolean.FALSE;
        } else {

            activeCall.hangup(new NexmoRequestListener<NexmoCall>() {
                @Override
                public void onError(@NonNull NexmoApiError nexmoApiError) {
                }

                @Override
                public void onSuccess(@Nullable NexmoCall nexmoCall) {
                }
            });

            shouldAnswerCall = Boolean.FALSE;
            activeCall = null;
        }
    }

    public void answerCall() {
        if (activeCall == null) {
            shouldAnswerCall = Boolean.TRUE;
            setRinging();
        } else {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED
            ) {
                return;
            }

            activeCall.answer(new NexmoRequestListener<NexmoCall>() {
                @Override
                public void onError(@NonNull NexmoApiError nexmoApiError) {
                }

                @Override
                public void onSuccess(@Nullable NexmoCall nexmoCall) {
                    setInitialized();
                    setActive();
                }
            });

            activeCall.addCallEventListener(callEventListener);
        }
    }

    public void makeCall() {
        client.serverCall(otherUser, null, new NexmoRequestListener<NexmoCall>() {
            @Override
            public void onError(@NonNull NexmoApiError nexmoApiError) {
                nexmoApiError.toString();
            }

            @Override
            public void onSuccess(@Nullable NexmoCall call) {
                activeCall = call;
                activeCall.addCallEventListener(callEventListener);
            }
        });
    }
}
