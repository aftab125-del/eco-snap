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
    var baseUrl: String = "http://10.0.2.2:8000"

    suspend fun checkBackendHealth(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/v1/health")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            conn.requestMethod = "GET"
            val responseCode = conn.responseCode
            conn.disconnect()
            responseCode == 200
        } catch (e: Exception) {
            false
        }
    }

    suspend fun signup(name: String, email: String, pass: String, lang: String): JSONObject? = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/v1/auth/signup")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 6000
            conn.readTimeout = 6000
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.doOutput = true

            val payload = JSONObject().apply {
                put("name", name)
                put("email", email)
                put("password", pass)
                put("language", lang)
            }

            OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(payload.toString()) }
            if (conn.responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                val res = JSONObject(reader.readText())
                reader.close()
                conn.disconnect()
                res
            } else {
                conn.disconnect()
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun login(email: String, pass: String): JSONObject? = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/v1/auth/login")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 6000
            conn.readTimeout = 6000
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.doOutput = true

            val payload = JSONObject().apply {
                put("email", email)
                put("password", pass)
            }

            OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(payload.toString()) }
            if (conn.responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                val res = JSONObject(reader.readText())
                reader.close()
                conn.disconnect()
                res
            } else {
                conn.disconnect()
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun sendChatMessage(userId: String, message: String, lang: String): JSONObject? = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/v1/chat/message")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 8000
            conn.readTimeout = 20000
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.doOutput = true

            val payload = JSONObject().apply {
                put("user_id", userId)
                put("message", message)
                put("language", lang)
            }

            OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(payload.toString()) }
            if (conn.responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                val res = JSONObject(reader.readText())
                reader.close()
                conn.disconnect()
                res
            } else {
                conn.disconnect()
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchScanHistory(userId: String): List<ScanHistoryItem> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/v1/history/scans?user_id=$userId")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.requestMethod = "GET"

            if (conn.responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                val res = JSONObject(reader.readText())
                reader.close()
                conn.disconnect()
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
                list
            } else {
                conn.disconnect()
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun logScanEvent(userId: String, itemHindi: String, itemEnglish: String, cat: String, jugaad: String, scrap: String) = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/v1/history/scan")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.doOutput = true

            val payload = JSONObject().apply {
                put("user_id", userId)
                put("item_name_hindi", itemHindi)
                put("item_name_english", itemEnglish)
                put("category", cat)
                put("jugaad_title", jugaad)
                put("scrap_rate", scrap)
            }

            OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(payload.toString()) }
            conn.responseCode
            conn.disconnect()
        } catch (e: Exception) {
            // Ignored
        }
    }
}
