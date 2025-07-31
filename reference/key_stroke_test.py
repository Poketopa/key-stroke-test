import requests
import base64
import os
import json
import hashlib
from typing import Dict, Any

def get_typing_pattern(text: str) -> str:
    """
    실제 타이핑 패턴을 시뮬레이션하는 함수
    실제 구현에서는 키보드 이벤트를 캡처해야 합니다.
    """
    # 실제로는 키보드 이벤트를 캡처하여 타이핑 패턴을 생성해야 함
    # 여기서는 예시로 간단한 패턴을 반환
    return "1234567890abcdef"  # 실제 타이핑 패턴으로 교체 필요

def save_typing_pattern(user_id: str, text: str, tp: str) -> Dict[str, Any]:
    """
    TypingDNA API를 사용하여 타이핑 패턴을 저장합니다.
    
    Args:
        user_id: 사용자 ID
        text: 타이핑된 텍스트
        tp: 타이핑 패턴
    
    Returns:
        API 응답 결과
    """
    # 올바른 API 키와 시크릿 (이미지에서 확인됨)
    api_key = os.getenv("TYPINGDNA_API_KEY", "3a07f4dae8484fb7f70d0d0613a3a3e4")
    api_secret = os.getenv("TYPINGDNA_API_SECRET", "d97ae110a884c19f47318d54373e27d9")  # 'd' 추가됨
    
    # Basic 인증 방식 사용
    auth_string = f"{api_key}:{api_secret}"
    auth_encoded = base64.b64encode(auth_string.encode('utf-8')).decode('utf-8')
    
    headers = {
        "Authorization": f"Basic {auth_encoded}",
        "Content-Type": "application/x-www-form-urlencoded"
    }
    
    # Endpoint
    url = "https://api.typingdna.com/save"
    
    # 요청 데이터
    payload = {
        "user_id": user_id,
        "text": text,
        "tp": tp
    }
    
    try:
        response = requests.post(url, headers=headers, data=payload, timeout=10)
        response.raise_for_status()  # HTTP 오류 발생 시 예외 발생
        
        return {
            "success": True,
            "status_code": response.status_code,
            "response": response.text
        }
        
    except requests.exceptions.RequestException as e:
        return {
            "success": False,
            "error": str(e),
            "status_code": getattr(e.response, 'status_code', None) if hasattr(e, 'response') else None
        }

def main():
    """메인 테스트 함수"""
    print("🔑 올바른 API 키와 시크릿으로 테스트합니다!")
    print("📝 API 키: 3a07f4dae8484fb7f70d0d0613a3a3e4")
    print("📝 API Secret: d97ae110a884c19f47318d54373e27d9")
    print("-" * 60)
    
    # user_id를 해시값으로 생성
    user_id = hashlib.md5("testuser".encode()).hexdigest()  # MD5 해시 사용
    text = "test"
    tp = get_typing_pattern(text)
    
    print(f"사용자 ID (해시): {user_id}")
    print(f"텍스트: {text}")
    print(f"타이핑 패턴: {tp}")
    print("-" * 50)
    
    result = save_typing_pattern(user_id, text, tp)
    
    if result["success"]:
        print("✅ API 호출 성공!")
        print(f"상태 코드: {result['status_code']}")
        print(f"응답: {result['response']}")
    else:
        print("❌ API 호출 실패!")
        print(f"오류: {result['error']}")
        if result.get('status_code'):
            print(f"상태 코드: {result['status_code']}")
        
        # user_id 관련 오류인 경우 안내
        if "Invalid user id" in result.get('error', ''):
            print("\n💡 해결 방법:")
            print("   - user_id는 이메일이나 전화번호가 아닌 해시값이나 ID여야 합니다")
            print("   - 6-256자 길이여야 합니다")
            print("   - 예: 'user_123', 'hash_abc123', 'id_456789'")
        
        # 다른 오류인 경우 디버깅 정보 출력
        else:
            print("\n🔍 디버깅 정보:")
            print(f"   - user_id 길이: {len(user_id)}")
            print(f"   - text 길이: {len(text)}")
            print(f"   - tp 길이: {len(tp)}")

if __name__ == "__main__":
    main()