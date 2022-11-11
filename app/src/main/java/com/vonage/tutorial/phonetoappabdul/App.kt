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
        const val TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2wiOnsicGF0aHMiOnsiLyovYXBwbGljYXRpb25zLyoqIjp7fSwiLyovY29udmVyc2F0aW9ucy8qKiI6e30sIi8qL2RldmljZXMvKioiOnt9LCIvKi9pbWFnZS8qKiI6e30sIi8qL2tub2NraW5nLyoqIjp7fSwiLyovbGVncy8qKiI6e30sIi8qL21lZGlhLyoqIjp7fSwiLyovcHVzaC8qKiI6e30sIi8qL3Nlc3Npb25zLyoqIjp7fSwiLyovdXNlcnMvKioiOnt9fX0sImFwcGxpY2F0aW9uX2lkIjoiMzkxNjc4ZDUtYjkzMy00N2Q3LWFkMTItZWNhMWVkZTNmMmVmIiwiZXhwIjoxNjY4MjU4Njg3LCJpYXQiOjE2NjgxNzIyODcsImp0aSI6IjcxYzlkODdlLTE3ZDMtOGE2Zi1lZTIwLTA2ZTNhMGM4YmQ2YSIsInN1YiI6IkFuZHJpeTIifQ.RUucIz3Ad4fCnR0B1x_HOyJXy1UkPGUxwB8h9uBw9ASawTlOeye98RPNJQ3nTjq1crr3rQOOtHe0GGUTIwHbe2WcchEsBzxJxviBrzBsuqdLsgUEqB9hSQDjUuBF59zG3o0uQU-HNUQo5KAhX3wJwA6ITDWPlAM5urOjJNjz6P2Z-moZNFefPXWz4mtOzmyCOBjmJZpYtt_YWdnU-j6eSyVJLP4cVxO2hsOaoqXei4-IuSsiGoyNApGfauQ8FwtyR5pozMcc-7ofPh4JlAiLincAW9GiFCcW-OISAZXhrNt7WIsPJOZ_TQw9b6P_IfyJmz-AfnvgwCKTOYahRrUkwg"
        lateinit var clientManager: ClientManager
    }
}