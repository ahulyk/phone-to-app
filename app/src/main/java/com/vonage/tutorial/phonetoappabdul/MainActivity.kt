package com.vonage.tutorial.phonetoappabdul

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.nexmo.client.NexmoCall
import com.nexmo.client.NexmoClient
import com.nexmo.client.request_listener.NexmoApiError
import com.nexmo.client.request_listener.NexmoConnectionListener
import com.nexmo.client.request_listener.NexmoRequestListener
import com.nexmo.utils.logger.ILogger

class MainActivity : AppCompatActivity() {

    private lateinit var connectionStatusTextView: TextView
    private lateinit var loginButton: Button
    private lateinit var telecomPermissionButton: Button
    private lateinit var answerCallButton: Button
    private lateinit var rejectCallButton: Button
    private lateinit var endCallButton: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val clientManager = ClientManager(applicationContext)

        // request permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 123);
        }

        // init views
        connectionStatusTextView = findViewById(R.id.connectionStatusTextView)
        loginButton = findViewById(R.id.loginButton)
        telecomPermissionButton = findViewById(R.id.telecomPermissionButton)
        answerCallButton = findViewById(R.id.answerCallButton)
        rejectCallButton = findViewById(R.id.rejectCallButton)
        endCallButton = findViewById(R.id.endCallButton)

        loginButton.setOnClickListener {
            clientManager.login()
            clientManager.setClientCallListener {
            answerCallButton.visibility = View.VISIBLE
            rejectCallButton.visibility = View.VISIBLE
            endCallButton.visibility = View.GONE
            }
        }

        answerCallButton.setOnClickListener {
            clientManager.answerCall(object : NexmoRequestListener<NexmoCall> {
                override fun onError(p0: NexmoApiError) {
                }

                override fun onSuccess(p0: NexmoCall?) {
                    answerCallButton.visibility = View.GONE
                    rejectCallButton.visibility = View.GONE
                    endCallButton.visibility = View.VISIBLE
                }
            })
        }

        val callTerminateListener = object : NexmoRequestListener<NexmoCall> {
            override fun onError(p0: NexmoApiError) {
            }

            override fun onSuccess(p0: NexmoCall?) {
                answerCallButton.visibility = View.GONE
                rejectCallButton.visibility = View.GONE
                endCallButton.visibility = View.GONE
            }
        }
        rejectCallButton.setOnClickListener { clientManager.rejectCall(callTerminateListener)  }
        endCallButton.setOnClickListener { clientManager.endCall(callTerminateListener) }
        // CSDemo: Your app needs to get the user's permission to use the system calling
        telecomPermissionButton.setOnClickListener { enableTelecomPermission() }


        clientManager.setClientConnectionListener { connectionStatus, _ ->
            runOnUiThread {
                connectionStatusTextView.text = connectionStatus.toString()
                loginButton.visibility = View.GONE
                telecomPermissionButton.visibility = View.VISIBLE
            }

            if (connectionStatus == NexmoConnectionListener.ConnectionStatus.CONNECTED) {
                // CSDemo: Get the token from firebase and register it with Vonage.
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let { token ->
                            clientManager.enablePush(token, object : NexmoRequestListener<Void>{
                                override fun onError(error: NexmoApiError) {
                                    connectionStatusTextView.text = error.toString()
                                }

                                override fun onSuccess(void: Void?) {
                                    connectionStatusTextView.text = "push enabled"
                                }
                            })
                        }
                    }
                }
            }
        }
    }

    private fun enableTelecomPermission() {
        val intent = Intent()
        intent.setClassName(
            "com.android.server.telecom",
            "com.android.server.telecom.settings.EnableAccountPreferenceActivity"
        )
        startActivity(intent)
    }
}