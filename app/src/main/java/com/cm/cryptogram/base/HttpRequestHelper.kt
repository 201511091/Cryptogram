package com.cm.cryptogram.base

import android.content.ContentValues.TAG
import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.get
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HttpRequestHelper {
    private val client: HttpClient = HttpClient(CIO)
    private lateinit var targetUrl : String
    fun setTargetUrl(str : String ){ targetUrl = str }
    suspend fun requestKtorIo(): String = withContext(Dispatchers.IO) {
        val url = targetUrl
        val response: HttpResponse = client.get(url)
        val responseStatus = response.status
        Log.d(TAG, "requestKtorIo: $responseStatus")
        if (responseStatus == HttpStatusCode.OK) {
            response.readText()
        } else { "error: $responseStatus" }
    }
}