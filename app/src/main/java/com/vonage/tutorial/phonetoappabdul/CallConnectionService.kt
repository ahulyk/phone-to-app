//package com.vonage.tutorial.phonetoappabdul
//
//import android.net.Uri
//import android.os.Build
//import android.telecom.*
//import androidx.annotation.RequiresApi
//import com.google.firebase.messaging.RemoteMessage
//import org.json.JSONObject
//
//class CallConnectionService : ConnectionService() {
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreateIncomingConnection(
//        connectionManagerPhoneAccount: PhoneAccountHandle?,
//        request: ConnectionRequest?
//    ): Connection {
//        /*
//        CSDemo: This gets the push info from the ClientManager and pulls out the from number as specified in your NCCO.
//        A CallConnection Object is also created. This is how the system tells you the user has initiated an action with the System UI.
//         */
//        val pushInfoBytes = request?.extras?.getByteArray("pushinfo")
//        val pushInfo = ParcelableUtil.unmarshall(pushInfoBytes!!, RemoteMessage.CREATOR)
//        val connection = CallConnection(this, pushInfo)
//        connection.processCall()
//        val from =
//            (((JSONObject(pushInfo.data["nexmo"]).get("body") as JSONObject).get("channel") as JSONObject).get(
//                "from"
//            ) as JSONObject).get("user") as String
//        connection.setCallerDisplayName(from, TelecomManager.PRESENTATION_ALLOWED)
//        connection.setAddress(Uri.parse("tel: Olx calling..."), TelecomManager.PRESENTATION_ALLOWED)
//        connection.connectionProperties = Connection.PROPERTY_SELF_MANAGED
//        connection.setInitializing()
//        return connection
//    }
//
//    override fun onCreateIncomingConnectionFailed(
//        connectionManagerPhoneAccount: PhoneAccountHandle?,
//        request: ConnectionRequest?
//    ) {
//        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
//    }
//}