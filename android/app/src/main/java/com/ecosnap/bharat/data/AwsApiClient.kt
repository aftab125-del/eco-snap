package com.ecosnap.bharat.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object AwsApiClient {
    // Candidates for emulator and local Wi-Fi physical device
    private val candidateUrls = listOf(
        "http://10.0.2.2:8000",
        "http://192.168.1.156:8000",
        "http://localhost:8000"
    )
    var baseUrl: String = "http://10.0.2.2:8000"

    private suspend fun executePost(endpoint: String, payload: JSONObject, timeoutMs: Int = 5000): JSONObject? = withContext(Dispatchers.IO) {
        val targets = listOf(baseUrl) + candidateUrls.filter { it != baseUrl }
        for (target in targets) {
            try {
                val url = URL("$target$endpoint")
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = timeoutMs
                conn.readTimeout = timeoutMs + 10000
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                conn.doOutput = true

                OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(payload.toString()) }
                if (conn.responseCode in 200..299) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                    val res = JSONObject(reader.readText())
                    reader.close()
                    conn.disconnect()
                    baseUrl = target // Cache working target for future calls
                    return@withContext res
                } else {
                    conn.disconnect()
                }
            } catch (e: Exception) {
                // Try next candidate
            }
        }
        null
    }

    private suspend fun executeGet(endpoint: String, timeoutMs: Int = 4000): JSONObject? = withContext(Dispatchers.IO) {
        val targets = listOf(baseUrl) + candidateUrls.filter { it != baseUrl }
        for (target in targets) {
            try {
                val url = URL("$target$endpoint")
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = timeoutMs
                conn.readTimeout = timeoutMs
                conn.requestMethod = "GET"

                if (conn.responseCode in 200..299) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                    val res = JSONObject(reader.readText())
                    reader.close()
                    conn.disconnect()
                    baseUrl = target
                    return@withContext res
                } else {
                    conn.disconnect()
                }
            } catch (e: Exception) {
                // Try next candidate
            }
        }
        null
    }

    suspend fun checkBackendHealth(): Boolean = withContext(Dispatchers.IO) {
        val res = executeGet("/api/v1/health", timeoutMs = 2000)
        res != null && res.optString("status") == "healthy"
    }

    suspend fun signup(name: String, email: String, pass: String, lang: String): JSONObject? {
        val payload = JSONObject().apply {
            put("name", name)
            put("email", email)
            put("password", pass)
            put("language", lang)
        }
        return executePost("/api/v1/auth/signup", payload, timeoutMs = 4000)
    }

    suspend fun login(email: String, pass: String): JSONObject? {
        val payload = JSONObject().apply {
            put("email", email)
            put("password", pass)
        }
        return executePost("/api/v1/auth/login", payload, timeoutMs = 4000)
    }

    suspend fun sendChatMessage(userId: String, message: String, lang: String): JSONObject? {
        val payload = JSONObject().apply {
            put("user_id", userId)
            put("message", message)
            put("language", lang)
        }
        return executePost("/api/v1/chat/message", payload, timeoutMs = 8000)
    }

    suspend fun fetchScanHistory(userId: String): List<ScanHistoryItem> {
        val res = executeGet("/api/v1/history/scans?user_id=$userId", timeoutMs = 3000)
        if (res != null) {
            val array = res.optJSONArray("scans") ?: JSONArray()
            val list = mutableListOf<ScanHistoryItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ScanHistoryItem(
                        scanId = obj.optString("scan_id"),
                        itemNameHindi = obj.optString("item_name_hindi"),
                        itemNameEnglish = obj.optString("item_name_english"),
                        category = obj.optString("category"),
                        jugaadTitle = obj.optString("jugaad_title"),
                        scrapRate = obj.optString("scrap_rate"),
                        timestamp = obj.optLong("timestamp")
                    )
                )
            }
            return list
        }
        return emptyList()
    }

    suspend fun logScanEvent(userId: String, itemHindi: String, itemEnglish: String, cat: String, jugaad: String, scrap: String) {
        val payload = JSONObject().apply {
            put("user_id", userId)
            put("item_name_hindi", itemHindi)
            put("item_name_english", itemEnglish)
            put("category", cat)
            put("jugaad_title", jugaad)
            put("scrap_rate", scrap)
        }
        executePost("/api/v1/history/scan", payload, timeoutMs = 3000)
    }
}
