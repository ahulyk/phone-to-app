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
        const val TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2wiOnsicGF0aHMiOnsiLyovYXBwbGljYXRpb25zLyoqIjp7fSwiLyovY29udmVyc2F0aW9ucy8qKiI6e30sIi8qL2RldmljZXMvKioiOnt9LCIvKi9pbWFnZS8qKiI6e30sIi8qL2tub2NraW5nLyoqIjp7fSwiLyovbGVncy8qKiI6e30sIi8qL21lZGlhLyoqIjp7fSwiLyovcHVzaC8qKiI6e30sIi8qL3Nlc3Npb25zLyoqIjp7fSwiLyovdXNlcnMvKioiOnt9fX0sImFwcGxpY2F0aW9uX2lkIjoiMzkxNjc4ZDUtYjkzMy00N2Q3LWFkMTItZWNhMWVkZTNmMmVmIiwiZXhwIjoxNjY5MjE0MzcwLCJpYXQiOjE2NjkxMjc5NzAsImp0aSI6ImFjNWYxMGU1LTJkODItYmJlNi0wN2E2LThhOWY1ODAzOWE5YiIsInN1YiI6IkFuZHJpeTIifQ.OcqEpsSE75kHW2eM-z_sjpVO50nKzLbYGYl-tyHfKCHJYDWcyCJ_RKq-JU9mizYIp6vxBM-zx7yGYmtqvYxRdDRNuiO6MzJOi1N22RRhKK06pZQYWCWWxbfIV3nQbsrB2tKhjUDw4fhanD8wiXzDlhYlXzNO5s1DM2amNmfl1zRuJzMdEsj7LvrmIsCNXY_vwDv-AAZwlFyc4985spFXFn9sbayb3Uq098A53ny-3zbviL_z7mO5LZiSmgUPPYGyprjz92N7ji-2dK6vHnl3-_Y3xYIUcYY6TMpIHP1kAW4cyKjkQgtnX2uQVeivjbQCsrAgNX3XFZFbCRVyn2_5oQ"
        lateinit var clientManager: ClientManager
    }
}