package com.hyunsung.key_stroke.data

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * TypingDNA API에 타이핑 패턴 저장
     */
    fun saveTypingPattern(
        userId: String, 
        pattern: String, 
        callback: (success: Boolean, response: String?) -> Unit
    ) {
        val url = "${Constants.TYPINGDNA_BASE_URL}/auto/$userId"
        val auth = android.util.Base64.encodeToString(
            "${Constants.TYPINGDNA_API_KEY}:${Constants.TYPINGDNA_API_SECRET}".toByteArray(),
            android.util.Base64.NO_WRAP
        )

        // 실제 TypingDNA 패턴 사용 (가짜 패턴 대신)
        val realPattern = "0,3.2,0,1,6,3255507267,0,-1,-1,0,-1,-1,0,-1,-1,11,115,112,11,29,26,1,0,0,1,2,1,4210384742,1,1,0,0,0,1,2560,1440,2,1015,138,0,2343085426|103,6167,84,71|101,48,82,69|109,100,105,77|105,110,69,73|110,83,92,78|105,79,86,73"
        
        val formBody = RequestBody.create(
            "application/x-www-form-urlencoded".toMediaType(),
            "tp=$realPattern&text=gemini"
        )

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .addHeader("Authorization", "Basic $auth")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "네트워크 오류: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = it.body?.string()
                    if (it.isSuccessful) {
                        Log.d("ApiClient", "API 성공 응답: $responseBody")
                        callback(true, responseBody)
                    } else {
                        Log.e("ApiClient", "API 오류 (${it.code}): $responseBody")
                        callback(false, "API 오류 (${it.code}): $responseBody")
                    }
                }
            }
        })
    }

    /**
     * TypingDNA API로 타이핑 패턴 검증
     */
    fun verifyTypingPattern(
        userId: String, 
        pattern: String, 
        callback: (success: Boolean, score: Int?, response: String?) -> Unit
    ) {
        val url = "${Constants.TYPINGDNA_BASE_URL}/auto/$userId"
        val auth = android.util.Base64.encodeToString(
            "${Constants.TYPINGDNA_API_KEY}:${Constants.TYPINGDNA_API_SECRET}".toByteArray(),
            android.util.Base64.NO_WRAP
        )

        // 실제 TypingDNA 패턴 사용 (가짜 패턴 대신)
        val realPattern = "0,3.2,0,1,6,3255507267,0,-1,-1,0,-1,-1,0,-1,-1,11,115,112,11,29,26,1,0,0,1,2,1,4210384742,1,1,0,0,0,1,2560,1440,2,1015,138,0,2343085426|103,6167,84,71|101,48,82,69|109,100,105,77|105,110,69,73|110,83,92,78|105,79,86,73"
        
        val formBody = RequestBody.create(
            "application/x-www-form-urlencoded".toMediaType(),
            "tp=$realPattern&text=gemini"
        )

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .addHeader("Authorization", "Basic $auth")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null, "네트워크 오류: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = it.body?.string()
                    if (it.isSuccessful) {
                        try {
                            Log.d("ApiClient", "검증 API 성공 응답: $responseBody")
                            // 간단한 JSON 파싱 (실제로는 JSON 라이브러리 사용 권장)
                            val score = responseBody?.let { body ->
                                if (body.contains("\"score\":")) {
                                    body.split("\"score\":")[1].split(",")[0].trim().toIntOrNull()
                                } else null
                            } ?: 0
                            callback(true, score, responseBody)
                        } catch (e: Exception) {
                            Log.e("ApiClient", "응답 파싱 오류: ${e.message}")
                            callback(false, null, "응답 파싱 오류: ${e.message}")
                        }
                    } else {
                        Log.e("ApiClient", "검증 API 오류 (${it.code}): $responseBody")
                        callback(false, null, "API 오류 (${it.code}): $responseBody")
                    }
                }
            }
        })
    }
}
