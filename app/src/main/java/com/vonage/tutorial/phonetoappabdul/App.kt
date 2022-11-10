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
        const val TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2wiOnsicGF0aHMiOnsiLyovYXBwbGljYXRpb25zLyoqIjp7fSwiLyovY29udmVyc2F0aW9ucy8qKiI6e30sIi8qL2RldmljZXMvKioiOnt9LCIvKi9pbWFnZS8qKiI6e30sIi8qL2tub2NraW5nLyoqIjp7fSwiLyovbGVncy8qKiI6e30sIi8qL21lZGlhLyoqIjp7fSwiLyovcHVzaC8qKiI6e30sIi8qL3Nlc3Npb25zLyoqIjp7fSwiLyovdXNlcnMvKioiOnt9fX0sImFwcGxpY2F0aW9uX2lkIjoiMzkxNjc4ZDUtYjkzMy00N2Q3LWFkMTItZWNhMWVkZTNmMmVmIiwiZXhwIjoxNjY4MTczMTY1LCJpYXQiOjE2NjgwODY3NjUsImp0aSI6IjRkMDg3YmM3LTExOTctY2U2OS05ODUyLWY0N2M5MDk2MThiNiIsInN1YiI6IkFuZHJpeTIifQ.i5aDKWSX5Sg8QXn_eWBpbFnTD6jzSDN5Czqw_Pn0qIQV6mUKfSBGvbPeOZzoC1ykxkSZYJ4PMP4pgGrjzHYCw-Wcd3AUpC676h-YETp67QmkuGKTeu_qybQQfTLtUoqy_XiYkj-Ur-Uub3tPtBwukwO5d4Jphn2SWXqWGseMH483FP3BdYJiPqeXKnlc9k9J-7GOh14fiexm-sz2DACoUFQolDJ3_pmAA8vTo_pSeP4CowPyb552US-MjD4-frDhx6pHPSiVO3-KAb2OxBatdNFf9W0ITOer-1f8ewsMPBdNnTicOuLaYdeU42cRPtxh_FZLXM9j8jzczzEIq566pw"
        lateinit var clientManager: ClientManager
    }
}