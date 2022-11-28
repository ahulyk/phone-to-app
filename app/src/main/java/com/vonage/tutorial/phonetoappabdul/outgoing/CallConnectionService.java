package com.vonage.tutorial.phonetoappabdul.outgoing;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

@TargetApi(Build.VERSION_CODES.M)
public class CallConnectionService extends ConnectionService {

    private static final String TAG = CallConnectionService.class.getSimpleName();

    public CallConnectionService() {
        super();
    }

    @Override
    public void onCreateIncomingConnectionFailed(
            PhoneAccountHandle connectionManagerPhoneAccount,
            ConnectionRequest request
    ) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request);
        Log.i(TAG, "onCreateIncomingConnectionFailed");
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    @Override
    public Connection onCreateIncomingConnection(
            PhoneAccountHandle connectionManagerPhoneAccount,
            ConnectionRequest request
    ) {
        super.onCreateIncomingConnection(connectionManagerPhoneAccount, request);

        Log.i(TAG, "onCreateIncomingConnection");

        CallConnection conn = new CallConnection(this);
        conn.processCall("inbound");

        conn.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED);
        conn.setAddress(Uri.parse("tel:" + "+00000000000"), TelecomManager.PRESENTATION_ALLOWED);
        conn.setInitializing();
        return conn;
    }

    @Override
    public void onCreateOutgoingConnectionFailed(
            PhoneAccountHandle connectionManagerPhoneAccount,
            ConnectionRequest request
    ) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request);
        Log.i(TAG, "onCreateOutgoingConnectionFailed");
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public Connection onCreateOutgoingConnection(
            PhoneAccountHandle connectionManagerPhoneAccount,
            ConnectionRequest request
    ) {
        super.onCreateOutgoingConnection(connectionManagerPhoneAccount, request);

        Log.i(TAG, "onCreateOutgoingConnection");

        CallConnection conn = new CallConnection(this);
        conn.processCall("outbound");

        conn.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED);
        conn.setAddress(Uri.parse("sip:" + "andriy"), TelecomManager.PRESENTATION_ALLOWED);
        conn.setInitializing();
        return conn;
    }
}
