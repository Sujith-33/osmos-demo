package com.example.osmos

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.ai.osmos.core.OsmosSDK
import com.ai.osmos.models.events.TrackingParams
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : FlutterActivity() {

    private val CHANNEL = "osmos"

    override fun configureFlutterEngine(
        flutterEngine: FlutterEngine
    ) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->

            when (call.method) {

                "loadAd" -> {
                    loadAd(result)
                }

                "trackImpression" -> {

                    val uclid =
                        call.argument<String>("uclid")

                    val position =
                        call.argument<Int>("position") ?: 1

                    trackImpression(
                        uclid = uclid,
                        position = position,
                        result = result
                    )
                }

                "trackClick" -> {

                    val uclid =
                        call.argument<String>("uclid")

                    trackClick(
                        uclid = uclid,
                        result = result
                    )
                }

                else -> result.notImplemented()
            }
        }
    }

    private fun trackImpression(
        uclid: String?,
        position: Int,
        result: MethodChannel.Result
    ) {

        lifecycleScope.launch {

            try {

                if (uclid.isNullOrEmpty()) {

                    result.error(
                        "IMPRESSION_ERROR",
                        "uclid is null or empty",
                        null
                    )

                    return@launch
                }

                val registerEvent =
                    OsmosSDK.globalInstance()
                        .registerEvent()

                val response =
                    registerEvent.registerAdImpressionEvent(
                        cliUbid = "Any",
                        uclid = uclid,
                        position = position,
                        trackingParams = null,
                        errorCallback = null
                    )

                Log.d(
                    "OSMOS_IMPRESSION",
                    "SUCCESS => $response"
                )

                result.success(true)

            } catch (e: Exception) {

                Log.e(
                    "OSMOS_IMPRESSION",
                    e.stackTraceToString()
                )

                result.error(
                    "IMPRESSION_ERROR",
                    e.message,
                    null
                )
            }
        }
    }

    private fun trackClick(
        uclid: String?,
        result: MethodChannel.Result
    ) {

        lifecycleScope.launch {

            try {

                if (uclid.isNullOrEmpty()) {

                    result.error(
                        "CLICK_ERROR",
                        "uclid is null or empty",
                        null
                    )

                    return@launch
                }

                val trackingParams =
                    TrackingParams.builder()
                        .build()

                val registerEvent =
                    OsmosSDK.globalInstance()
                        .registerEvent()

                val response =
                    registerEvent.registerAdClickEvent(
                        cliUbid = "Any",
                        uclid = uclid,
                        trackingParams = trackingParams,
                        errorCallback = null
                    )

                Log.d(
                    "OSMOS_CLICK",
                    "SUCCESS => $response"
                )

                result.success(true)

            } catch (e: Exception) {

                Log.e(
                    "OSMOS_CLICK",
                    e.stackTraceToString()
                )

                result.error(
                    "CLICK_ERROR",
                    e.message,
                    null
                )
            }
        }
    }

    private fun loadAd(
        result: MethodChannel.Result
    ) {

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val sdk =
                    OsmosSDK.globalInstance()

                val response =
                    sdk.adFetcherSDK()
                        .fetchDisplayAdsWithAu(
                            cliUbid = "Any",
                            pageType = "demo_page",
                            productCount = 1,
                            adUnits = listOf(
                                "banner_ads"
                            )
                        )

                Log.d(
                    "OSMOS_RESPONSE",
                    response?.toString() ?: "NULL"
                )

                if (response == null) {

                    withContext(Dispatchers.Main) {
                        result.success(
                            mapOf(
                                "error" to "Ad not available"
                            )
                        )
                    }

                    return@launch
                }

                val responseMap =
                    response as? Map<*, *>

                if (responseMap == null) {

                    withContext(Dispatchers.Main) {
                        result.success(
                            mapOf(
                                "error" to "Ad not available"
                            )
                        )
                    }

                    return@launch
                }

                val responseObj =
                    responseMap["response"]
                            as? Map<*, *>

                if (responseObj == null) {

                    withContext(Dispatchers.Main) {
                        result.success(
                            mapOf(
                                "error" to "Ad not available"
                            )
                        )
                    }

                    return@launch
                }

                val dataObj =
                    responseObj["data"]
                            as? String

                if (dataObj.isNullOrEmpty()) {

                    withContext(Dispatchers.Main) {
                        result.success(
                            mapOf(
                                "error" to "Ad not available"
                            )
                        )
                    }

                    return@launch
                }

                val imageRegex =
                    """"value":"(.*?)""""
                        .toRegex()

                val uclidRegex =
                    """"uclid":"(.*?)""""
                        .toRegex()

                val clickRegex =
                    """"click_tracking_url":"(.*?)""""
                        .toRegex()

                val imageUrl =
                    imageRegex
                        .find(dataObj)
                        ?.groupValues
                        ?.get(1)
                        ?: ""

                val uclid =
                    uclidRegex
                        .find(dataObj)
                        ?.groupValues
                        ?.get(1)
                        ?: ""

                val clickUrl =
                    clickRegex
                        .find(dataObj)
                        ?.groupValues
                        ?.get(1)
                        ?: ""

                if (
                    imageUrl.isEmpty() ||
                    uclid.isEmpty()
                ) {

                    withContext(Dispatchers.Main) {
                        result.success(
                            mapOf(
                                "error" to "Ad not available"
                            )
                        )
                    }

                    return@launch
                }

                Log.d(
                    "OSMOS_IMAGE",
                    imageUrl
                )

                Log.d(
                    "OSMOS_UCLID",
                    uclid
                )

                Log.d(
                    "OSMOS_CLICK_URL",
                    clickUrl
                )

                withContext(
                    Dispatchers.Main
                ) {

                    result.success(
                        mapOf(
                            "imageUrl" to imageUrl,
                            "destinationUrl" to clickUrl,
                            "uclid" to uclid
                        )
                    )
                }

            } catch (e: Exception) {

                Log.e(
                    "OSMOS_ERROR",
                    e.stackTraceToString()
                )

                withContext(
                    Dispatchers.Main
                ) {

                    result.success(
                        mapOf(
                            "error" to "Ad not available"
                        )
                    )
                }
            }
        }
    }
}
