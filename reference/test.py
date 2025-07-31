import requests
import base64
import json

def test_typingdna_verification():
    """TypingDNA 사용자 구분 테스트"""
    
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
    
    # 사용자 A의 등록 패턴들 (helloworld)
    user_a_patterns = [
        "0,3.2,0,1,10,530340096,0,-1,-1,0,-1,-1,0,-1,-1,3,116,67,3,169,80,1,0,0,1,2,1,4210384742,1,1,0,0,0,1,2560,1440,2,1015,138,0,2343085426|104,3995,33,72|101,115,43,69|108,153,56,76|108,162,42,76|111,192,43,79|119,123,87,87|111,44,59,79|114,106,69,82|108,87,85,76|100,123,72,68",
        "0,3.2,0,1,10,530340096,0,-1,-1,0,-1,-1,0,-1,-1,3,120,60,3,23,13,1,0,0,1,2,1,4210384742,1,1,0,0,0,1,2560,1440,2,1015,138,0,2343085426|104,2385,65,72|101,72,102,69|108,87,71,76|108,119,71,76|111,225,25,79|119,15,55,87|111,61,57,79|114,43,87,82|108,105,74,76|100,93,71,68",
        "0,3.2,0,1,10,530340096,0,-1,-1,0,-1,-1,0,-1,-1,2,250,199,2,56,16,1,0,0,1,2,1,4210384742,1,1,0,0,0,1,2560,1440,2,1015,138,0,2343085426|104,1569,57,72|101,75,93,69|108,77,55,76|108,100,73,76|111,183,43,79|119,88,87,87|111,44,74,79|114,63,103,82|108,87,59,76|100,76,104,68"
    ]
    
    # 사용자 B의 등록 패턴들 (helloworld)
    user_b_patterns = [
        "0,3.2,0,1,10,530340096,0,-1,-1,0,-1,-1,0,-1,-1,2,167,125,2,66,52,1,0,0,1,2,1,4210384742,1,1,0,0,0,1,2560,1440,2,1015,138,0,2343085426|104,2465,73,72|101,395,101,69|108,300,101,76|108,266,74,76|111,304,73,79|119,331,70,87|111,254,86,79|114,254,73,82|108,299,42,76|100,212,69,68",
        "0,3.2,0,1,10,530340096,0,-1,-1,0,-1,-1,0,-1,-1,2,198,124,2,23,12,1,0,0,1,2,1,4210384742,1,1,0,0,0,1,2560,1440,2,1015,138,0,2343085426|104,2059,71,72|101,616,99,69|108,298,72,76|108,180,71,76|111,210,72,79|119,180,102,87|111,123,87,79|114,105,70,82|108,119,70,76|100,88,101,68",
        "0,3.2,0,1,10,530340096,0,-1,-1,0,-1,-1,0,-1,-1,3,192,105,3,94,106,1,0,0,1,2,1,4210384742,1,1,0,0,0,1,2560,1440,2,1015,138,0,2343085426|104,2641,67,72|101,296,41,69|108,298,88,76|108,254,77,76|111,245,71,79|119,241,71,87|111,239,41,79|114,179,72,82|108,210,40,76|100,256,99,68"
    ]
    
    # 검증 패턴 (helloworld)
    verification_pattern = "0,3.2,0,1,10,530340096,0,-1,-1,0,-1,-1,0,-1,-1,2,162,100,2,26,6,1,0,0,1,2,1,4210384742,1,1,0,0,0,1,2560,1440,2,1015,138,0,2343085426|104,2765,52,72|101,39,143,69|108,122,29,76|108,109,41,76|111,132,41,79|119,106,58,87|111,43,43,79|114,34,82,82|108,102,42,76|100,47,70,68"
    
    print("🚀 TypingDNA 사용자 구분 테스트 (helloworld)")
    print("=" * 60)
    
    # 1단계: 사용자 A 등록
    print("\n📝 1단계: 사용자 A 등록")
    enroll_user("user_a", user_a_patterns, headers)
    
    # 2단계: 사용자 B 등록
    print("\n📝 2단계: 사용자 B 등록")
    enroll_user("user_b", user_b_patterns, headers)
    
    # 3단계: 검증 테스트
    print("\n🔍 3단계: 검증 테스트")
    print("=" * 60)
    
    # 테스트 1: 검증 패턴으로 사용자 A 인증
    print("\n🧪 테스트 1: 검증 패턴으로 사용자 A 인증")
    result_a = verify_user("user_a", verification_pattern, headers)
    
    # 테스트 2: 검증 패턴으로 사용자 B 인증
    print("\n🧪 테스트 2: 검증 패턴으로 사용자 B 인증")
    result_b = verify_user("user_b", verification_pattern, headers)
    
    # 결과 분석
    print("\n📊 결과 분석")
    print("=" * 60)
    
    if result_a and not result_b:
        print("✅ 성공: 사용자 A로 정확히 인식됨")
    elif result_b and not result_a:
        print("✅ 성공: 사용자 B로 정확히 인식됨")
    elif result_a and result_b:
        print("⚠️ 경고: 두 사용자 모두 인증됨 (모호함)")
    else:
        print("❌ 실패: 어떤 사용자도 인증되지 않음")
    
    return result_a, result_b

def enroll_user(user_id, patterns, headers):
    """사용자 등록"""
    url = f"https://api.typingdna.com/auto/{user_id}"
    
    for i, pattern in enumerate(patterns, 1):
        print(f"📝 패턴 {i} 등록 중...")
        
        payload = {"tp": pattern}
        
        try:
            response = requests.post(url, headers=headers, data=payload, timeout=10)
            
            if response.status_code == 200:
                result = response.json()
                print(f"✅ 패턴 {i} 등록 성공!")
                print(f"   Action: {result.get('action', 'N/A')}")
                print(f"   Enrollment: {result.get('enrollment', 'N/A')}")
            else:
                print(f"❌ 패턴 {i} 등록 실패: {response.status_code}")
                
        except Exception as e:
            print(f"❌ 오류: {e}")

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
            
            print(f"Status: {response.status_code}")
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
            return None
            
    except Exception as e:
        print(f"❌ 오류: {e}")
        return None

if __name__ == "__main__":
    test_typingdna_verification()