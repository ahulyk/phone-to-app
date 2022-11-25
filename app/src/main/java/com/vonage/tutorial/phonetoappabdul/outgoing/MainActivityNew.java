package com.vonage.tutorial.phonetoappabdul.outgoing;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import ua.slando.R;

public class MainActivityNew extends AppCompatActivity {
    private static final String TAG = MainActivityNew.class.getSimpleName();

    private TelecomManager mTelecomManager;
    private PhoneAccountHandle mPhoneAccountHandle;
    private PhoneAccount mPhoneAccount;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        // request permissions
        String[] callsPermissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MANAGE_OWN_CALLS,
                Manifest.permission.CALL_PHONE,
        };
        ActivityCompat.requestPermissions(this, callsPermissions, 123);

        // -----------------
        findViewById(R.id.registerVoip).setOnClickListener(v -> registerAccount());
        findViewById(R.id.startOutgoingCall).setOnClickListener(v -> startOutgoingCall());
        //
        mTelecomManager = (TelecomManager) this.getSystemService(Context.TELECOM_SERVICE);
        ComponentName componentName = new ComponentName(this, CallConnectionService.class);
        mPhoneAccountHandle = new PhoneAccountHandle(componentName, "VoIP Calling 1");
        mPhoneAccount = new PhoneAccount
                .Builder(mPhoneAccountHandle, "VoIP calling 1")
                .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
                .build();
        // -----------------

    }

    private void registerAccount() {
        Log.i(TAG, "registerAccount()");
        mTelecomManager.registerPhoneAccount(mPhoneAccount);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.server.telecom",
                "com.android.server.telecom.settings.EnableAccountPreferenceActivity"));
        startActivity(intent);
    }

    private void startOutgoingCall() {
        Log.i(TAG, "startOutgoingCall()");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Bundle extrasDetails = new Bundle();
        //
        Bundle extras = new Bundle();
        extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, mPhoneAccountHandle);
        extras.putParcelable(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, extrasDetails);
        //
        try {
            Log.i(TAG, "startOutgoingCall() calling");
            mTelecomManager.placeCall(Uri.parse("tel:" + "+00000000000"), extras);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}