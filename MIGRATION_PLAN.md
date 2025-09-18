# 🚀 OMaMul 전체 프로젝트 Legacy Code 제거 계획

## 📊 현재 상태 분석

### ✅ 완료된 모듈들 (Compose 마이그레이션 완료)
- `feature-water:home` - HomeScreen.kt
- `feature-water:cup` - CupManageScreen.kt, CupCreateScreen.kt  
- `feature-water:record` - WaterLogScreen.kt
- `feature-water:alarm` - WaterAlarmScreen.kt
- `feature-water:setting` - WaterSettingScreen.kt

### 🔄 남아있는 Legacy Code

#### 1. Fragment 파일들 (총 10개)

**Feature-Common 모듈 (5개):**
```
feature-common/init/
├── InitIntakeFragment.kt      // 초기 목표량 설정
├── InitLanguageFragment.kt    // 초기 언어 설정  
└── InitTimeFragment.kt        // 초기 시간 설정

feature-common/setting/
├── AccountFragment.kt         // 계정 설정
└── CommonSettingFragment.kt   // 공통 설정
```

**Feature-Water 모듈 (5개):**
```
feature-water/alarm/
├── AlarmModeCustomFragment.kt    // 커스텀 알림 설정
├── AlarmModeFragment.kt         // 알림 모드 선택
├── AlarmModePeriodFragment.kt   // 주기 알림 설정
└── WaterAlarmFragment.kt        // 알림 메인 (이미 WaterAlarmScreen.kt 생성됨)

feature-water/setting/
└── WaterSettingFragment.kt     // 물 설정 (이미 WaterSettingScreen.kt 생성됨)
```

#### 2. Adapter 클래스들 (총 4개)

```
feature-water/home/adapter/
└── CupPagerAdapter.kt          // 컵 선택 페이저

feature-water/record/
└── LogBindingAdapter.kt        // 기록 바인딩 어댑터

feature-water/cup/
└── CupBindingAdapter.kt        // 컵 바인딩 어댑터

feature-water/alarm/adapter/
└── AlarmListAdapter.kt         // 알림 리스트 어댑터
```

#### 3. XML Layout 파일들 (총 29개)

**Core 모듈 XML (8개):**
- core/ui: 커스텀 뷰, 다이얼로그, 마커 등
- core/alarmnoti: 알림 레이아웃, 알림 액티비티

**Feature 모듈 XML (21개):**
- feature-common/init: 초기화 화면 레이아웃 (3개)
- feature-water/alarm: 알림 관련 레이아웃 (13개)
- feature-water/setting: 설정 관련 레이아웃 (5개)

#### 4. DataBinding/ViewBinding 사용 파일들 (총 20개)

**Core UI 커스텀 뷰들:**
- CustomTimePicker.kt
- ExpandableTextView.kt
- SwitchView.kt
- TextImageView.kt
- TextSwitchView.kt
- CustomBottomDialog.kt
- CustomDialog.kt

**Fragment 파일들:**
- 모든 남아있는 Fragment 파일들 (10개)

**다이얼로그 파일들:**
- ExactAlarmDialog.kt
- WaterIntakeDialog.kt
- LanguageDialog.kt
- UnitDialog.kt

## 🎯 마이그레이션 전략

### Phase 1: Feature-Water 모듈 완전 정리 (우선순위: 높음)

#### 1.1 중복 파일 제거
```bash
# 이미 Compose Screen이 존재하는 Fragment 즉시 제거
rm feature-water/alarm/WaterAlarmFragment.kt
rm feature-water/setting/WaterSettingFragment.kt
```

#### 1.2 남은 Alarm Fragment들 Compose 변환
- `AlarmModeFragment.kt` → `AlarmModeScreen.kt`
- `AlarmModeCustomFragment.kt` → `AlarmModeCustomScreen.kt`  
- `AlarmModePeriodFragment.kt` → `AlarmModePeriodScreen.kt`

#### 1.3 Adapter 클래스 제거 및 Compose 변환
- `CupPagerAdapter.kt` → LazyRow in HomeScreen
- `LogBindingAdapter.kt` → LazyColumn in WaterLogScreen
- `CupBindingAdapter.kt` → LazyVerticalGrid in CupManageScreen
- `AlarmListAdapter.kt` → LazyColumn in AlarmScreen

### Phase 2: Feature-Common 모듈 마이그레이션 (우선순위: 중간)

#### 2.1 Init 모듈 Compose 변환
- `InitIntakeFragment.kt` → `InitIntakeScreen.kt`
- `InitLanguageFragment.kt` → `InitLanguageScreen.kt`
- `InitTimeFragment.kt` → `InitTimeScreen.kt`

#### 2.2 Setting 모듈 Compose 변환  
- `AccountFragment.kt` → `AccountScreen.kt`
- `CommonSettingFragment.kt` → `CommonSettingScreen.kt`

### Phase 3: Core UI 모듈 마이그레이션 (우선순위: 낮음)

#### 3.1 커스텀 뷰 Compose 변환
- `CustomTimePicker.kt` → Compose TimePicker
- `SwitchView.kt` → Compose Switch
- `TextImageView.kt` → Compose Row+Icon+Text
- `TextSwitchView.kt` → Compose Row+Text+Switch

#### 3.2 다이얼로그 Compose 변환
- `CustomDialog.kt` → Compose AlertDialog
- `CustomBottomDialog.kt` → Compose BottomSheet
- `ExactAlarmDialog.kt` → Compose AlertDialog
- `WaterIntakeDialog.kt` → Compose Dialog
- `LanguageDialog.kt` → Compose Dialog
- `UnitDialog.kt` → Compose Dialog

### Phase 4: 정리 및 최적화 (우선순위: 높음)

#### 4.1 XML 파일 제거
```bash
# 모든 layout XML 파일 제거
rm -rf */src/main/res/layout/
rm -rf */src/main/res/navigation/
```

#### 4.2 build.gradle 정리
```kotlin
// 모든 모듈에서 제거
- dataBinding = true
- viewBinding = true
- implementation libs.androidx.navigation.fragment
- implementation libs.androidx.navigation.ui
- implementation libs.androidx.fragment.ktx
```

#### 4.3 의존성 정리
- Fragment 관련 import 모두 제거
- DataBinding 관련 import 모두 제거
- ViewBinding 관련 import 모두 제거

## 📅 실행 일정

### Week 1: Feature-Water 모듈 완전 정리
- [ ] 중복 Fragment 파일 즉시 제거
- [ ] 남은 Alarm Fragment들 Compose 변환
- [ ] Adapter 클래스들 제거 및 Compose 리스트로 변환
- [ ] feature-water 모듈 XML 파일 모두 제거

### Week 2: Feature-Common 모듈 마이그레이션  
- [ ] Init 모듈 3개 Fragment → Compose Screen 변환
- [ ] Setting 모듈 2개 Fragment → Compose Screen 변환
- [ ] feature-common 모듈 XML 파일 모두 제거

### Week 3: Core UI 모듈 마이그레이션
- [ ] 커스텀 뷰들 Compose 변환
- [ ] 다이얼로그들 Compose 변환
- [ ] core/ui 모듈 XML 파일 모두 제거

### Week 4: 정리 및 최적화
- [ ] 모든 XML navigation 파일 제거
- [ ] 모든 build.gradle에서 DataBinding/ViewBinding 의존성 제거
- [ ] 전체 프로젝트 빌드 검증 및 테스트
- [ ] 문서 업데이트

## 🎯 성공 기준

### 기술적 목표
- [ ] Fragment 파일 0개
- [ ] Adapter 클래스 0개  
- [ ] XML layout 파일 0개 (필수 시스템 파일 제외)
- [ ] DataBinding/ViewBinding 사용 0개
- [ ] 100% Compose UI 달성

### 성능 목표
- [ ] 앱 시작 시간 20% 개선
- [ ] 메모리 사용량 15% 감소
- [ ] 빌드 시간 30% 단축

### 품질 목표
- [ ] 모든 화면 정상 동작
- [ ] 모든 기능 테스트 통과
- [ ] 접근성 가이드라인 준수
- [ ] Material 3 디자인 시스템 완전 적용

## 🚨 주의사항

### 호환성 유지
- 기존 ViewModel 로직은 최대한 보존
- 데이터 모델 변경 최소화
- Navigation 흐름 동일하게 유지

### 점진적 마이그레이션
- 각 모듈별로 독립적으로 진행
- 매 단계마다 빌드 검증 필수
- 기능 테스트 후 다음 단계 진행

### 백업 및 복구
- 각 단계 시작 전 브랜치 생성
- 중요 파일 백업 보관
- 롤백 계획 수립

## 📈 예상 효과

### 개발 생산성
- XML 레이아웃 작업 시간 단축
- Preview 기능으로 빠른 UI 검증
- 컴포넌트 재사용성 증대

### 성능 개선
- View inflation 시간 단축
- 메모리 사용량 최적화
- 렌더링 성능 향상

### 유지보수성
- 코드 일관성 향상
- 타입 안전성 증대
- 테스트 용이성 개선