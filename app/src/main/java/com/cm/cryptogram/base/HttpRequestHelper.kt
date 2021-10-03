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
    suspend fun requestKtorIo(): String = withContext(Dispatchers.IO) {
        val url = "https://ctrytogram-default-rtdb.asia-southeast1.firebasedatabase.app/.json"
        val response: HttpResponse = client.get(url)
        val responseStatus = response.status
        Log.d(TAG, "requestKtorIo: $responseStatus")
        if (responseStatus == HttpStatusCode.OK) {
            response.readText()
        } else { "error: $responseStatus" }
    }
}