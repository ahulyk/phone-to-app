package com.vonage.tutorial.phonetoappabdul

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.nexmo.client.NexmoClient
import com.nexmo.utils.logger.ILogger

class App: Application() {



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        NexmoClient.Builder().logLevel(ILogger.eLogLevel.SENSITIVE).build(this)
        clientManager = ClientManager(this)
    }

    companion object {
        lateinit var clientManager: ClientManager
        const val TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2wiOnsicGF0aHMiOnsiLyovYXBwbGljYXRpb25zLyoqIjp7fSwiLyovY29udmVyc2F0aW9ucy8qKiI6e30sIi8qL2RldmljZXMvKioiOnt9LCIvKi9pbWFnZS8qKiI6e30sIi8qL2tub2NraW5nLyoqIjp7fSwiLyovbGVncy8qKiI6e30sIi8qL21lZGlhLyoqIjp7fSwiLyovcHVzaC8qKiI6e30sIi8qL3Nlc3Npb25zLyoqIjp7fSwiLyovdXNlcnMvKioiOnt9fX0sImFwcGxpY2F0aW9uX2lkIjoiMzkxNjc4ZDUtYjkzMy00N2Q3LWFkMTItZWNhMWVkZTNmMmVmIiwiZXhwIjoxNjY5Mjg3ODE2LCJpYXQiOjE2NjkyMDE0MTYsImp0aSI6ImYwODhhNDIyLTBmMGYtMzczZi0xMzE3LWEzZjJkYjM1NDQ4ZiIsInN1YiI6IkFuZHJpeSJ9.jwCwsquHoER0XhJwUgvsNnOCmlNlgkmih1yTA5tZv51UNWiAHasZ2OhPHpbR12Jj2-8le8Gf6xA1KIu-W6ttynwl5LTjzOualoUgveY1CYK_bW3uxUER5JuErFYrxILF2pa-4vN99fVxQAC5DgMtvejUHjoJSGbL2KxAnb67eix-gum9HlmxRk2t8H_dDe9YpoV7C6bYD4pQZ7g0NKJSuPN4qhDBaYNwSibBBSEx1FqFPmyeFFuTCHYJyZsD-wL3-eb7h8dTicyM6xj5PRCNPxXS04FEFiu5vl59NgFT4rA_PK2xO_Mhn-j3fC9LQCrOLTcech9Zevpxu4mukkhhUg"
    }
}