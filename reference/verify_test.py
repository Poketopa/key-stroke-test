import requests
import base64
import json

def verify_user_typing():
    """TypingDNA 사용자 검증 테스트"""
    
    # Authentication API 키와 시크릿
    api_key = "3a07f4dae8484fb7f70d0d0613a3a3e4"
    api_secret = "d97ae110a884c19f47318d54373e27d9"
    
    # Basic 인증 헤더
    auth_string = f"{api_key}:{api_secret}"
    auth_encoded = base64.b64encode(auth_string.encode('utf-8')).decode('utf-8')
    
    headers = {
        "Authorization": f"Basic {auth_encoded}",
        "Content-Type": "application/x-www-form-urlencoded"
    }
    
    print("🔍 TypingDNA 사용자 검증 테스트")
    print("=" * 50)
    
    # 검증 패턴 입력 받기
    print("\n📝 검증 패턴을 입력해주세요:")
    print("(HTML에서 생성한 패턴을 복사해서 붙여넣으세요)")
    verification_pattern = input("패턴: ").strip()
    
    if not verification_pattern:
        print("❌ 패턴이 입력되지 않았습니다.")
        return
    
    print(f"\n✅ 입력된 패턴: {verification_pattern[:50]}...")
    
    # 사용자 A 검증
    print("\n🧪 사용자 A 검증 중...")
    result_a = verify_user("user_a", verification_pattern, headers)
    
    # 사용자 B 검증
    print("\n🧪 사용자 B 검증 중...")
    result_b = verify_user("user_b", verification_pattern, headers)
    
    # 결과 분석
    print("\n📊 검증 결과 분석")
    print("=" * 50)
    
    if result_a and not result_b:
        print("✅ 성공: 사용자 A로 정확히 인식됨")
        print("🎯 결과: 이 타이핑은 사용자 A의 패턴과 일치합니다.")
    elif result_b and not result_a:
        print("✅ 성공: 사용자 B로 정확히 인식됨")
        print("🎯 결과: 이 타이핑은 사용자 B의 패턴과 일치합니다.")
    elif result_a and result_b:
        print("⚠️ 경고: 두 사용자 모두 인증됨 (모호함)")
        print("🎯 결과: 이 타이핑이 두 사용자 모두와 일치합니다.")
    else:
        print("❌ 실패: 어떤 사용자도 인증되지 않음")
        print("🎯 결과: 이 타이핑은 등록된 사용자 패턴과 일치하지 않습니다.")
    
    return result_a, result_b

def verify_user(user_id, pattern, headers):
    """사용자 검증"""
    url = f"https://api.typingdna.com/auto/{user_id}"
    payload = {"tp": pattern}
    
    try:
        response = requests.post(url, headers=headers, data=payload, timeout=10)
        
        if response.status_code == 200:
            result = response.json()
            result_value = result.get('result', 'N/A')
            high_confidence = result.get('high_confidence', 'N/A')
            action = result.get('action', 'N/A')
            
            print(f"Status: {response.status_code}")
            print(f"Action: {action}")
            print(f"Result: {result_value}")
            print(f"High Confidence: {high_confidence}")
            
            if result_value == 1:
                print("🎉 사용자 인증 성공!")
                return True
            else:
                print("❌ 사용자 인증 실패!")
                return False
        else:
            print(f"❌ 검증 요청 실패: {response.status_code}")
            print(f"Response: {response.text}")
            return None
            
    except Exception as e:
        print(f"❌ 오류: {e}")
        return None

def show_usage_guide():
    """사용 가이드 표시"""
    print("\n📋 사용 가이드")
    print("=" * 50)
    print("1. HTML에서 'helloworld'를 타이핑하여 검증 패턴 생성")
    print("2. 생성된 패턴을 복사")
    print("3. 이 프로그램을 실행하고 패턴을 붙여넣기")
    print("4. 결과 확인")
    print("\n💡 팁: 각 타이핑마다 다른 속도로 입력하면 더 정확한 결과를 얻을 수 있습니다!")

if __name__ == "__main__":
    show_usage_guide()
    verify_user_typing() 