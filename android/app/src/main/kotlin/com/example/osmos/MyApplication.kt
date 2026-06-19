package com.example.osmos

import android.app.Application
import com.ai.osmos.core.OsmosSDK

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        OsmosSDK.clientId("10088010")
            .displayAdsHost("demo-ba.o-s.io")
            .productAdsHost("demo.o-s.io")
            .debug(true)
            .buildGlobalInstance()
    }
}