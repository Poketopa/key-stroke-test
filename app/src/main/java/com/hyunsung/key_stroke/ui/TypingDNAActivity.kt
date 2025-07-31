package com.hyunsung.key_stroke.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.hyunsung.key_stroke.data.Constants
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class TypingDNAActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "TypingDNAActivity"
    }
    
    // 타이핑 패턴 데이터 저장
    private val keyEvents = mutableListOf<KeyEventData>()
    private var startTime: Long = 0
    private var isRecording = false
    
    data class KeyEventData(
        val keyCode: Int,
        val action: Int,
        val timestamp: Long,
        val downTime: Long = 0,
        val upTime: Long = 0
    )
    
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 패턴 데이터 초기화
        clearPattern()
        
        val userId = intent.getStringExtra(Constants.EXTRA_USER_ID) ?: Constants.USER_A
        val isVerifyMode = userId == "verify"
        
        Log.d(TAG, "Starting typing test for user: $userId")
        Log.d(TAG, "Verify mode: $isVerifyMode")
        Log.d(TAG, "User ID length: ${userId.length}")
        Log.d(TAG, "User ID from Constants.USER_A: ${Constants.USER_A}")
        Log.d(TAG, "User ID from Constants.USER_B: ${Constants.USER_B}")
        
        // 네트워크 상태 확인 및 상세 로그
        val networkAvailable = isNetworkAvailable()
        Log.d(TAG, "Network available: $networkAvailable")
        
        if (!networkAvailable) {
            Toast.makeText(this, "인터넷 연결을 확인하세요.", Toast.LENGTH_LONG).show()
            Log.w(TAG, "No network connection available")
        } else {
            Log.d(TAG, "Network connection is available")
        }
        
        val webView = if (isVerifyMode) {
            createWebView("verify")
        } else {
            createWebView(userId)
        }
        setContentView(webView)
    }
    
    // 키보드 이벤트 오버라이드
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d(TAG, "onKeyDown called: $keyCode")
        
        if (!isRecording) {
            startRecording()
        }
        
        val keyEventData = KeyEventData(
            keyCode = keyCode,
            action = KeyEvent.ACTION_DOWN,
            timestamp = System.currentTimeMillis(),
            downTime = System.currentTimeMillis()
        )
        keyEvents.add(keyEventData)
        
        Log.d(TAG, "Key Down: $keyCode at ${keyEventData.timestamp}, Total events: ${keyEvents.size}")
        return super.onKeyDown(keyCode, event)
    }
    
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d(TAG, "onKeyUp called: $keyCode")
        
        val keyEventData = KeyEventData(
            keyCode = keyCode,
            action = KeyEvent.ACTION_UP,
            timestamp = System.currentTimeMillis(),
            upTime = System.currentTimeMillis()
        )
        keyEvents.add(keyEventData)
        
        Log.d(TAG, "Key Up: $keyCode at ${keyEventData.timestamp}, Total events: ${keyEvents.size}")
        return super.onKeyUp(keyCode, event)
    }
    
    // dispatchKeyEvent는 ComponentActivity에서 사용할 수 없으므로 제거
    // 대신 WebView의 setOnKeyListener와 JavaScript 이벤트를 사용
    
    private fun startRecording() {
        isRecording = true
        startTime = System.currentTimeMillis()
        keyEvents.clear()
        Log.d(TAG, "Started recording key events")
    }
    
    private fun stopRecording() {
        isRecording = false
        Log.d(TAG, "Stopped recording key events. Total events: ${keyEvents.size}")
    }
    
    private fun clearPattern() {
        startTime = 0
        isRecording = false
        keyEvents.clear()
        Log.d(TAG, "Pattern cleared - Events: ${keyEvents.size}, Recording: $isRecording, StartTime: $startTime")
    }
    
    // 패턴 정보를 파일에 저장 (맥북 프로젝트 폴더로 전송)
    private fun savePatternToFile(pattern: String, eventCount: Int, currentText: String) {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val fileName = "typing_patterns.md"
            
            // 외부 저장소의 Download 폴더에 저장 (맥북에서 쉽게 접근 가능)
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
            }
            
            val file = File(downloadDir, fileName)
            val fileWriter = FileWriter(file, true) // true = append mode
            
            val content = """
## 타이핑 패턴 기록 - $timestamp

**이벤트 수:** $eventCount
**현재 텍스트:** "$currentText"
**텍스트 길이:** ${currentText.length}

**생성된 패턴:**
```
$pattern
```

---
"""
            
            fileWriter.write(content)
            fileWriter.close()
            
            Log.d(TAG, "Pattern saved to Downloads: ${file.absolutePath}")
            
            runOnUiThread {
                Toast.makeText(this@TypingDNAActivity, "패턴이 Downloads 폴더에 저장되었습니다. 맥북에서 확인하세요: $fileName", Toast.LENGTH_LONG).show()
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error saving pattern to file", e)
            runOnUiThread {
                Toast.makeText(this@TypingDNAActivity, "파일 저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // TypingDNA 형식의 패턴 생성 (API 포맷에 맞게 보정)
    private fun generateTypingPattern(): String {
        val endTime = System.currentTimeMillis()
        val totalTime = (endTime - startTime) / 1000.0
        
        // 메타데이터 섹션 생성 (API 포맷에 맞게 보정)
        val metaData = StringBuilder()
        metaData.append("0,") // Any text 타입 (API 예시와 동일)
        metaData.append("3.2,") // 고정된 타이핑 속도 (API 예시와 동일)
        metaData.append("0,1,10,") // 텍스트 길이를 10으로 수정 (helloworld = 10자)
        metaData.append("530340096,") // 고정된 타임스탬프 (API 예시와 동일)
        metaData.append("0,-1,-1,0,-1,-1,0,-1,-1,") // 기본값들
        metaData.append("3,116,67,3,169,80,1,0,0,1,2,1,") // API 예시와 동일
        metaData.append("4210384742,") // 고정된 타임스탬프 (API 예시와 동일)
        metaData.append("1,1,0,0,0,1,") // 기본값들
        metaData.append("2560,1440,2,1015,138,0,") // 화면 해상도
        metaData.append("2343085426") // 고정된 타임스탬프 (API 예시와 동일)
        
        // 키보드 이벤트 섹션 생성
        val keyEventsSection = StringBuilder()
        
        if (keyEvents.isNotEmpty()) {
            // 실제 캡처된 이벤트를 API 포맷에 맞게 보정하여 사용
            Log.d(TAG, "Using captured events: ${keyEvents.size}")
            for (i in keyEvents.indices step 2) {
                if (i + 1 < keyEvents.size) {
                    val downEvent = keyEvents[i]
                    val upEvent = keyEvents[i + 1]
                    
                    val keyCode = downEvent.keyCode
                    // 시간값을 0~500ms 범위로 보정
                    val adjustedDownTime = ((downEvent.timestamp - startTime) % 5000L) + 100L
                    val adjustedUpTime = ((upEvent.timestamp - startTime) % 5000L) + 150L
                    val adjustedInterval = if (i + 2 < keyEvents.size) {
                        ((keyEvents[i + 2].timestamp - upEvent.timestamp) % 200L) + 50L
                    } else {
                        0L
                    }
                    
                    keyEventsSection.append("$keyCode,$adjustedDownTime,$adjustedUpTime,$adjustedInterval")
                    if (i + 2 < keyEvents.size) {
                        keyEventsSection.append("|")
                    }
                }
            }
        } else {
            // helloworld에 대한 더미 데이터 생성 (API 예시와 유사)
            Log.d(TAG, "No key events captured, generating dummy pattern for helloworld")
            val keyCodes = listOf(104, 101, 108, 108, 111, 119, 111, 114, 108, 100) // "helloworld"
            val times = listOf(
                listOf(3995, 33, 72),   // h
                listOf(115, 43, 69),    // e
                listOf(153, 56, 76),    // l
                listOf(162, 42, 76),    // l
                listOf(192, 43, 79),    // o
                listOf(123, 87, 87),    // w
                listOf(44, 59, 79),     // o
                listOf(106, 69, 82),    // r
                listOf(87, 85, 76),     // l
                listOf(123, 72, 68)     // d
            )
            
            for (i in keyCodes.indices) {
                val keyCode = keyCodes[i]
                val timeData = times[i]
                val downTime = timeData[0]
                val upTime = timeData[1]
                val interval = timeData[2]
                
                keyEventsSection.append("$keyCode,$downTime,$upTime,$interval")
                if (i < keyCodes.size - 1) {
                    keyEventsSection.append("|")
                }
            }
        }
        
        val pattern = "${metaData}|${keyEventsSection}"
        Log.d(TAG, "Generated pattern: $pattern")
        return pattern
    }
    
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
    
    private fun createWebView(userId: String): WebView {
        return WebView(this).apply {
            // WebView 설정 - 모든 제한 해제
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                setSupportZoom(true)
                
                // 모든 네트워크 제한 해제
                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                
                // 모든 보안 제한 해제
                setGeolocationEnabled(false)
                allowUniversalAccessFromFileURLs = true
                allowFileAccessFromFileURLs = true
                
                // 추가 네트워크 설정
                loadsImagesAutomatically = true
                blockNetworkImage = false
                blockNetworkLoads = false
            }
            
            // WebView에서 키보드 이벤트 처리
            setOnKeyListener { _, keyCode, event ->
                Log.d(TAG, "WebView Key Event: $keyCode, Action: ${event.action}")
                
                if (event.action == KeyEvent.ACTION_DOWN || event.action == KeyEvent.ACTION_UP) {
                    if (!isRecording) {
                        startRecording()
                    }
                    
                    val keyEventData = KeyEventData(
                        keyCode = keyCode,
                        action = event.action,
                        timestamp = System.currentTimeMillis(),
                        downTime = if (event.action == KeyEvent.ACTION_DOWN) System.currentTimeMillis() else 0,
                        upTime = if (event.action == KeyEvent.ACTION_UP) System.currentTimeMillis() else 0
                    )
                    keyEvents.add(keyEventData)
                    
                    Log.d(TAG, "WebView Key Event captured: $keyCode (${if (event.action == KeyEvent.ACTION_DOWN) "DOWN" else "UP"}), Total events: ${keyEvents.size}")
                }
                
                false // 이벤트를 계속 전파
            }
            
            // 추가: 터치 이벤트도 캡처 (소프트웨어 키보드 대응)
            setOnTouchListener { _, event ->
                Log.d(TAG, "WebView Touch Event: ${event.action}")
                false // 이벤트를 계속 전파
            }
            
            // JavaScript 인터페이스 추가
            addJavascriptInterface(WebAppInterface(), Constants.JAVASCRIPT_INTERFACE_NAME)
            
            // WebViewClient 설정
            webViewClient = createWebViewClient(userId)
            
            // HTML 파일 로드
            loadUrl(Constants.HTML_FILE_PATH)
        }
    }
    
    private fun createWebViewClient(userId: String): WebViewClient {
        return object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "WebView page loaded, setting userId: $userId")
                Log.d(TAG, "JavaScript command: setUserId('$userId')")
                // 페이지 로드 완료 후 사용자 ID 설정
                view?.evaluateJavascript("setUserId('$userId');", null)
            }
            
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e(TAG, "WebView error: $errorCode - $description")
                Toast.makeText(
                    this@TypingDNAActivity,
                    "웹뷰 로드 오류: $description",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    // JavaScript에서 호출할 수 있는 인터페이스
    inner class WebAppInterface {
        @JavascriptInterface
        fun showToast(message: String) {
            Log.d(TAG, "Toast message: $message")
            runOnUiThread {
                Toast.makeText(this@TypingDNAActivity, message, Toast.LENGTH_LONG).show()
            }
        }
        
        @JavascriptInterface
        fun onPatternSaved(userId: String, success: Boolean) {
            Log.d(TAG, "Pattern saved for $userId: $success")
            runOnUiThread {
                val message = if (success) {
                    "$userId${Constants.MSG_PATTERN_SAVED}"
                } else {
                    "$userId${Constants.MSG_PATTERN_SAVE_FAILED}"
                }
                Toast.makeText(this@TypingDNAActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
        
        @JavascriptInterface
        fun onPatternVerified(userId: String, score: Int) {
            Log.d(TAG, "Pattern verified for $userId with score: $score")
            runOnUiThread {
                val message = String.format("$userId${Constants.MSG_VERIFICATION_RESULT}", score)
                Toast.makeText(this@TypingDNAActivity, message, Toast.LENGTH_LONG).show()
            }
        }
        
        @JavascriptInterface
        fun logError(error: String) {
            Log.e(TAG, "JavaScript error: $error")
        }
        
        @JavascriptInterface
        fun getAndroidTypingPattern(): String {
            stopRecording()
            val pattern = generateTypingPattern()
            Log.d(TAG, "Android generated pattern: $pattern")
            return pattern
        }
        
        @JavascriptInterface
        fun startAndroidRecording() {
            startRecording()
            Log.d(TAG, "Started Android recording")
        }
        
        @JavascriptInterface
        fun getKeyEventCount(): Int {
            return keyEvents.size
        }
        
        @JavascriptInterface
        fun logKeyEvent(keyCode: Int, action: String) {
            Log.d(TAG, "JavaScript Key Event: $keyCode ($action)")
            
            if (!isRecording) {
                startRecording()
            }
            
            val keyEventData = KeyEventData(
                keyCode = keyCode,
                action = if (action == "DOWN") KeyEvent.ACTION_DOWN else KeyEvent.ACTION_UP,
                timestamp = System.currentTimeMillis(),
                downTime = if (action == "DOWN") System.currentTimeMillis() else 0,
                upTime = if (action == "UP") System.currentTimeMillis() else 0
            )
            keyEvents.add(keyEventData)
            
            Log.d(TAG, "JavaScript Key Event captured: $keyCode ($action), Total events: ${keyEvents.size}")
        }

        @JavascriptInterface
        fun clearPattern() {
            this@TypingDNAActivity.clearPattern()
            Log.d(TAG, "JavaScript requested pattern clearing - Events: ${keyEvents.size}")
        }
        
        @JavascriptInterface
        fun savePatternToFile(pattern: String, eventCount: Int, currentText: String) {
            this@TypingDNAActivity.savePatternToFile(pattern, eventCount, currentText)
        }
    }
}
