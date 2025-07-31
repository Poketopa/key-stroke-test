package com.hyunsung.key_stroke.data

object Constants {
    // API 관련
    const val TYPINGDNA_API_KEY = "3a07f4dae8484fb7f70d0d0613a3a3e4"
    const val TYPINGDNA_API_SECRET = "d97ae110a884c19f47318d54373e27d9"
    const val TYPINGDNA_BASE_URL = "https://api.typingdna.com"
    
    // 사용자 ID (TypingDNA API 요구사항: 6-256자, 해시/ID 형식)
    const val USER_A = "B6E3DC0FF5A19566DCA703F84A2C09BDD5DD22891E09B9FB3FC436C5BE9ECF9C"
    const val USER_B = "36D2620917D391C0700B144223FFAC4D7993AD2C8CB90BC072A37BB8372B5A6A"
    
    // Intent Extra
    const val EXTRA_USER_ID = "userId"
    
    // WebView 관련
    const val HTML_FILE_PATH = "file:///android_asset/typingdna_test.html"
    const val JAVASCRIPT_INTERFACE_NAME = "Android"
    
    // 메시지
    const val MSG_PATTERN_SAVED = "의 타이핑 패턴이 저장되었습니다."
    const val MSG_PATTERN_SAVE_FAILED = "의 패턴 저장에 실패했습니다."
    const val MSG_VERIFICATION_RESULT = " 검증 결과: %d%% 일치"
    
    // 동적 user_id 생성 함수
    fun generateUserId(base: String): String {
        val suffix = System.currentTimeMillis().toString().takeLast(6)
        return "${base}_$suffix"
    }
    
    // 기본 user_id 생성
    fun getUserA(): String = generateUserId("userA")
    fun getUserB(): String = generateUserId("userB")
} 