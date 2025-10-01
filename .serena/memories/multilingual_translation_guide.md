# 다국어 번역 가이드

## 지원 언어
1. **한국어** (values/, values-ko-rKR/)
2. **영어** (values-en-rUS/)
3. **일본어** (values-ja/)
4. **중국어 간체** (values-zh-rCN/)

## String 리소스 위치
**중요**: 모든 string 리소스는 `core:ui` 모듈에 관리
```
core/ui/src/main/res/
├── values/strings.xml
├── values-ko-rKR/strings.xml
├── values-en-rUS/strings.xml
├── values-ja/strings.xml
└── values-zh-rCN/strings.xml
```

## 문구 추가 프로세스

### 1. String 리소스 작성
레이아웃에 `@string/xxx` 참조 추가 시:
1. 즉시 core:ui/res/values/strings.xml 확인
2. 없으면 **5개 언어 모두 동시 추가** (필수!)
3. 추가 후 다음 작업 진행

### 2. 명명 규칙
- **접두사 사용**: 기능별 구분 (water_, alarm_, setting_, cup_, etc.)
- **스네이크 케이스**: `feature_action_description`
- **명확한 이름**: 용도가 분명하게
- **포맷 문자열**: 매개변수 순서와 타입 명시 (%d, %s, etc.)

### 3. 번역 품질 기준
- 각 언어의 자연스러운 표현
- 문화적 맥락 고려
- 일관된 톤앤매너
- 기술 용어 정확한 번역

## 언어별 번역 가이드

### 한국어 (values/, values-ko-rKR/)
- **톤**: 친근한 반말 (높임말 지양)
- **형태**: "~해요", "~에요" (정중한 표현)
- **이모티콘**: 적극 활용 (친근감)
- **예시**: "목표 달성! 잘 하셨어요! 🎉"

### 영어 (values-en-rUS/)
- **톤**: 간결하고 명확
- **문법**: 능동태 우선
- **스타일**: 사용자 친화적
- **예시**: "Goal achieved! Well done! 🎉"

### 일본어 (values-ja/)
- **경어**: 정중한 경어 사용
- **어순**: 자연스러운 일본어 어순
- **외래어**: 카타카나 적절히 활용
- **예시**: "目標達成！お疲れさまでした！ 🎉"

### 중국어 간체 (values-zh-rCN/)
- **문자**: 간체 사용
- **기준**: 대륙 중국어 표현
- **스타일**: 간결하고 이해하기 쉬움
- **예시**: "目标达成！做得好！ 🎉"

## 번역 예시

### 물 관련 문구
```xml
<!-- 한국어 -->
<string name="water_goal_achieved">목표 달성! 잘 하셨어요! 🎉</string>
<string name="water_goal_close">목표까지 %dml 남았어요! 거의 다 왔어요! 🔥</string>
<string name="water_add_intake">물 마시기</string>
<string name="water_remove_intake">되돌리기</string>

<!-- 영어 -->
<string name="water_goal_achieved">Goal achieved! Well done! 🎉</string>
<string name="water_goal_close">Only %dml left to goal! Almost there! 🔥</string>
<string name="water_add_intake">Drink Water</string>
<string name="water_remove_intake">Undo</string>

<!-- 일본어 -->
<string name="water_goal_achieved">目標達成！お疲れさまでした！ 🎉</string>
<string name="water_goal_close">目標まで%dml残っています！もうすぐです！ 🔥</string>
<string name="water_add_intake">水を飲む</string>
<string name="water_remove_intake">元に戻す</string>

<!-- 중국어 -->
<string name="water_goal_achieved">目标达成！做得好！ 🎉</string>
<string name="water_goal_close">距离目标还有%dml！快达成了！ 🔥</string>
<string name="water_add_intake">喝水</string>
<string name="water_remove_intake">撤销</string>
```

### 알람 관련 문구
```xml
<!-- 한국어 -->
<string name="alarm_settings">알람 설정</string>
<string name="alarm_time_format">%02d:%02d</string>

<!-- 영어 -->
<string name="alarm_settings">Alarm Settings</string>
<string name="alarm_time_format">%02d:%02d</string>

<!-- 일본語 -->
<string name="alarm_settings">アラーム設定</string>
<string name="alarm_time_format">%02d:%02d</string>

<!-- 中文 -->
<string name="alarm_settings">闹钟设置</string>
<string name="alarm_time_format">%02d:%02d</string>
```

## 검증 체크리스트
- [ ] 5개 언어 파일 모두 추가 확인
- [ ] 포맷 문자열 매개변수 일치 확인
- [ ] 텍스트 길이 2줄 이하 확인 (UI 레이아웃)
- [ ] 특수문자 및 이모티콘 일관성 확인
- [ ] 빌드 테스트로 누락 확인

## 빌드 에러 시
**String not found 에러 발생 시**: 5개 언어 파일 모두에 해당 string이 정의되었는지 확인!
