# 🚀 OMaMul 전체 프로젝트 Legacy Code 완전 제거 계획

## 📊 전체 프로젝트 현재 상태 분석

### ✅ 마이그레이션 완료된 모듈들
**Feature-Water 모듈 (95% 완료):**
- ✅ `feature-water:home` - HomeScreen.kt (완료)
- ✅ `feature-water:cup` - CupManageScreen.kt, CupCreateScreen.kt (완료)
- ✅ `feature-water:record` - WaterLogScreen.kt (완료)
- ✅ `feature-water:alarm` - WaterAlarmScreen.kt, AlarmModeScreen.kt (완료)
- ✅ `feature-water:setting` - WaterSettingScreen.kt (완료)

### 🔄 남아있는 Legacy Code (상세 분석)

#### 1. Fragment 파일들 (총 5개)

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

#### 2. DataBinding/ViewBinding 사용 파일들 (총 18개)

**App 모듈 (1개):**
- `app/src/main/java/com/tkw/omamul/WaterActivity.kt`

**Core 모듈 (9개):**
```
core/ui/
├── CustomDatePicker.kt       // 커스텀 날짜 선택기
├── CustomTimePicker.kt       // 커스텀 시간 선택기
├── ExpandableTextView.kt     // 확장 가능한 텍스트뷰
├── SwitchView.kt            // 커스텀 스위치
├── TextImageView.kt         // 텍스트+이미지 뷰
├── TextSwitchView.kt        // 텍스트+스위치 뷰
├── CustomBottomDialog.kt    // 바텀 다이얼로그
├── CustomDialog.kt          // 커스텀 다이얼로그
└── DialogResizeDelegation.kt // 다이얼로그 크기 조정
```

**Feature 모듈 (8개):**
```
feature-common/init/ (3개)
├── InitIntakeFragment.kt
├── InitLanguageFragment.kt
└── InitTimeFragment.kt

feature-common/setting/ (2개)
├── AccountFragment.kt
└── CommonSettingFragment.kt

feature-water/home/ (1개)
└── WaterIntakeDialog.kt     // 물 섭취 다이얼로그

feature-water/record/ (1개)
└── LogEditBottomDialog.kt   // 기록 편집 다이얼로그

feature-water/cup/ (1개)
└── (generated navigation args) // SafeArgs 생성 파일들
```

#### 3. XML Layout 파일들 (총 13개)

**Core 모듈 XML (10개):**
```
core/ui/res/layout/
├── custom_marker_month.xml      // 월별 차트 마커
├── custom_marker.xml            // 일반 차트 마커
├── custom_timepicker.xml        // 커스텀 시간 선택기
├── custom_switch.xml            // 커스텀 스위치
├── custom_bottom_dialog.xml     // 바텀 다이얼로그
├── custom_dialog_view.xml       // 다이얼로그 뷰
└── custom_text_image.xml        // 텍스트+이미지 레이아웃

core/alarmnoti/res/layout/
├── custom_notification.xml     // 알림 레이아웃
└── activity_alarm_screen.xml    // 알림 화면 액티비티
```

**App 모듈 XML (1개):**
- `app/res/layout/activity_water.xml`

**Feature 모듈 XML (2개):**
```
feature-common/init/res/layout/
├── fragment_init_time.xml       // 초기 시간 설정
├── fragment_init_language.xml   // 초기 언어 설정
└── fragment_init_intake.xml     // 초기 목표량 설정
```

#### 4. build.gradle DataBinding/ViewBinding 설정 (총 4개)

```
app/build.gradle                 // 메인 앱
core/navigation/build.gradle     // 네비게이션 (SafeArgs)
core/ui/build.gradle            // UI 컴포넌트
feature-common/init/build.gradle // 초기화 모듈
```

## 🎯 완전한 마이그레이션 전략

### Phase 2: Feature-Common 모듈 마이그레이션 (우선순위: 높음)

#### 2.1 Init 모듈 Compose 변환
**목표:** 앱 초기 설정 화면들을 Compose로 완전 마이그레이션

**변환 대상:**
- `InitIntakeFragment.kt` → `InitIntakeScreen.kt`
- `InitLanguageFragment.kt` → `InitLanguageScreen.kt`
- `InitTimeFragment.kt` → `InitTimeScreen.kt`

**작업 순서:**
1. Compose 의존성 추가 (`feature-common:init`)
2. Fragment → Compose Screen 변환
3. Navigation 구조 Compose로 변경
4. XML 레이아웃 파일 제거
5. build.gradle DataBinding/ViewBinding 제거

#### 2.2 Setting 모듈 Compose 변환
**목표:** 공통 설정 화면들을 Compose로 완전 마이그레이션

**변환 대상:**
- `AccountFragment.kt` → `AccountScreen.kt`
- `CommonSettingFragment.kt` → `CommonSettingScreen.kt`

### Phase 3: Core 모듈 마이그레이션 (우선순위: 중간)

#### 3.1 Core UI 커스텀 뷰 Compose 변환
**목표:** 모든 커스텀 뷰를 Compose 컴포넌트로 전환

**변환 계획:**
```kotlin
// 커스텀 뷰 → Compose 컴포넌트 매핑
CustomTimePicker.kt     → @Composable TimePickerDialog
CustomDatePicker.kt     → @Composable DatePickerDialog  
SwitchView.kt          → @Composable CustomSwitch
TextImageView.kt       → @Composable TextWithIcon
TextSwitchView.kt      → @Composable TextWithSwitch
ExpandableTextView.kt  → @Composable ExpandableText
CustomBottomDialog.kt  → @Composable BottomSheetDialog
CustomDialog.kt        → @Composable AlertDialog (Material 3)
```

#### 3.2 Core AlarmNoti 모듈 처리
**목표:** 알림 관련 XML을 필요에 따라 유지 또는 Compose 변환

**처리 방안:**
- `custom_notification.xml` → 시스템 알림이므로 유지 검토
- `activity_alarm_screen.xml` → Compose 액티비티로 변환

### Phase 4: App 모듈 마이그레이션 (우선순위: 높음)

#### 4.1 메인 액티비티 Compose 전환
**목표:** WaterActivity를 완전한 Compose 기반으로 전환

**변환 대상:**
- `WaterActivity.kt` - DataBinding → Compose
- `activity_water.xml` → 제거

**새로운 구조:**
```kotlin
@Composable
fun WaterApp() {
    val navController = rememberNavController()
    
    WaterNavHost(
        navController = navController,
        startDestination = determineStartDestination()
    )
}
```

### Phase 5: 남은 Dialog와 세부 컴포넌트 (우선순위: 낮음)

#### 5.1 Feature 모듈 Dialog 변환
**변환 대상:**
- `WaterIntakeDialog.kt` → `@Composable WaterIntakeDialog`
- `LogEditBottomDialog.kt` → `@Composable LogEditBottomSheet`

#### 5.2 Chart Marker 컴포넌트
**처리 방안:**
- `custom_marker.xml`, `custom_marker_month.xml` → Compose Canvas 또는 유지

## 📅 상세 실행 계획

### Week 1: Feature-Common 모듈 완전 마이그레이션

**Day 1-2: Init 모듈**
- [ ] `feature-common:init` build.gradle Compose 의존성 추가
- [ ] `InitIntakeScreen.kt` 구현 (목표량 설정 UI)
- [ ] `InitLanguageScreen.kt` 구현 (언어 선택 UI)
- [ ] `InitTimeScreen.kt` 구현 (시간 설정 UI)

**Day 3: Init 모듈 정리**
- [ ] Fragment 파일들 제거
- [ ] XML 레이아웃 파일들 제거
- [ ] build.gradle DataBinding/ViewBinding 제거
- [ ] Navigation 구조 Compose로 전환

**Day 4-5: Setting 모듈**
- [ ] `AccountScreen.kt` 구현 (계정 관리 UI)
- [ ] `CommonSettingScreen.kt` 구현 (공통 설정 UI)
- [ ] Fragment 파일들 제거
- [ ] build.gradle 정리

### Week 2: Core UI 모듈 마이그레이션

**Day 1-3: 커스텀 뷰 Compose 변환**
- [ ] `@Composable TimePickerDialog` 구현
- [ ] `@Composable DatePickerDialog` 구현
- [ ] `@Composable CustomSwitch` 구현
- [ ] `@Composable TextWithIcon` 구현
- [ ] `@Composable TextWithSwitch` 구현

**Day 4-5: 다이얼로그 Compose 변환**
- [ ] `@Composable ExpandableText` 구현
- [ ] `@Composable BottomSheetDialog` 구현
- [ ] `@Composable AlertDialog` (Material 3) 구현
- [ ] 기존 DataBinding 클래스들 제거

### Week 3: App 모듈 및 최종 정리

**Day 1-2: WaterActivity Compose 전환**
- [ ] `WaterActivity.kt` Compose 기반으로 재작성
- [ ] `activity_water.xml` 제거
- [ ] 메인 Navigation 구조 통합

**Day 3-4: 남은 Dialog들 처리**
- [ ] `WaterIntakeDialog.kt` → Compose 변환
- [ ] `LogEditBottomDialog.kt` → Compose 변환
- [ ] Chart Marker 컴포넌트 결정 및 처리

**Day 5: 전체 정리 및 검증**
- [ ] 모든 XML layout 파일 제거
- [ ] 모든 build.gradle에서 DataBinding/ViewBinding 제거
- [ ] SafeArgs 의존성 제거 (core:navigation)
- [ ] 전체 프로젝트 빌드 검증

## 🎯 모듈별 상세 작업 리스트

### 📱 feature-common:init 모듈

#### 제거할 파일들:
```bash
rm feature-common/init/src/main/java/com/tkw/init/InitIntakeFragment.kt
rm feature-common/init/src/main/java/com/tkw/init/InitLanguageFragment.kt
rm feature-common/init/src/main/java/com/tkw/init/InitTimeFragment.kt
rm -rf feature-common/init/src/main/res/layout/
rm -rf feature-common/init/src/main/res/navigation/
```

#### 생성할 파일들:
```kotlin
// feature-common/init/src/main/java/com/tkw/init/
InitIntakeScreen.kt      // 목표량 설정 Compose 화면
InitLanguageScreen.kt    // 언어 선택 Compose 화면
InitTimeScreen.kt        // 시간 설정 Compose 화면
InitNavigation.kt        // Compose Navigation 정의
```

#### build.gradle 수정:
```kotlin
// 제거
- dataBinding = true
- viewBinding = true
- implementation libs.androidx.navigation.fragment
- implementation libs.androidx.navigation.ui
- implementation libs.androidx.fragment.ktx

// 추가
+ alias libs.plugins.kotlin.compose
+ buildFeatures { compose = true }
+ implementation platform(libs.androidx.compose.bom)
+ implementation libs.bundles.compose
+ implementation libs.androidx.hilt.navigation.compose
```

### 📱 feature-common:setting 모듈

#### 제거할 파일들:
```bash
rm feature-common/setting/src/main/java/com/tkw/setting/AccountFragment.kt
rm feature-common/setting/src/main/java/com/tkw/setting/CommonSettingFragment.kt
```

#### 생성할 파일들:
```kotlin
// feature-common/setting/src/main/java/com/tkw/setting/
AccountScreen.kt         // 계정 관리 Compose 화면
CommonSettingScreen.kt   // 공통 설정 Compose 화면
SettingNavigation.kt     // Compose Navigation 정의
```

### 🎨 core:ui 모듈

#### 제거할 파일들:
```bash
rm core/ui/src/main/java/com/tkw/ui/custom/CustomTimePicker.kt
rm core/ui/src/main/java/com/tkw/ui/custom/CustomDatePicker.kt
rm core/ui/src/main/java/com/tkw/ui/custom/SwitchView.kt
rm core/ui/src/main/java/com/tkw/ui/custom/TextImageView.kt
rm core/ui/src/main/java/com/tkw/ui/custom/TextSwitchView.kt
rm core/ui/src/main/java/com/tkw/ui/custom/ExpandableTextView.kt
rm core/ui/src/main/java/com/tkw/ui/dialog/CustomBottomDialog.kt
rm core/ui/src/main/java/com/tkw/ui/dialog/CustomDialog.kt
rm -rf core/ui/src/main/res/layout/
```

#### 생성할 파일들:
```kotlin
// core/ui/src/main/java/com/tkw/ui/compose/
TimePickerDialog.kt      // Compose 시간 선택 다이얼로그
DatePickerDialog.kt      // Compose 날짜 선택 다이얼로그
CustomSwitch.kt          // Compose 커스텀 스위치
TextWithIcon.kt          // Compose 텍스트+아이콘
TextWithSwitch.kt        // Compose 텍스트+스위치
ExpandableText.kt        // Compose 확장 가능한 텍스트
BottomSheetDialog.kt     // Compose 바텀시트
AlertDialog.kt           // Material 3 AlertDialog
```

#### build.gradle 수정:
```kotlin
// 제거
- dataBinding = true
- viewBinding = true

// Compose는 이미 적용됨
```

### 📱 app 모듈

#### 수정할 파일들:
- `WaterActivity.kt` - DataBinding → Compose 전환

#### 제거할 파일들:
```bash
rm app/src/main/res/layout/activity_water.xml
```

#### build.gradle 수정:
```kotlin
// 제거
- dataBinding = true
- viewBinding = true
```

### 🔄 core:navigation 모듈

#### build.gradle 수정:
```kotlin
// SafeArgs 관련 모든 의존성 제거
- dataBinding = true
- viewBinding = true
- alias libs.plugins.navigation.safeargs
```

## 🚨 주의사항 및 고려사항

### 호환성 유지
- **ViewModel**: 기존 ViewModel 로직 최대한 보존
- **데이터 모델**: Domain 모델 변경 최소화
- **비즈니스 로직**: Repository, UseCase 패턴 유지

### 단계적 마이그레이션
- **모듈별 독립**: 각 모듈을 독립적으로 마이그레이션
- **빌드 검증**: 매 단계마다 전체 빌드 성공 확인
- **기능 테스트**: 각 화면별 기능 정상 동작 확인

### 백업 및 복구
- **브랜치 관리**: 각 모듈별 별도 브랜치 생성
- **중요 파일 백업**: 복원 가능한 백업 체계
- **롤백 계획**: 문제 발생 시 빠른 복구 방안

### 성능 최적화
- **메모리 사용량**: DataBinding 제거로 메모리 절약
- **빌드 시간**: XML inflation 제거로 빌드 속도 향상
- **렌더링 성능**: Compose 최적화 기법 적용

## 📈 예상 효과

### 기술적 효과
- **100% Compose UI**: Fragment+XML 완전 제거
- **코드 일관성**: 단일 UI 프레임워크 사용
- **타입 안전성**: Compose의 강력한 타입 시스템
- **개발 생산성**: Preview, Hot Reload 활용

### 성능 효과
- **앱 시작 시간**: 20-30% 개선 예상
- **메모리 사용량**: 15-25% 감소 예상
- **빌드 시간**: 30-40% 단축 예상
- **APK 크기**: DataBinding 제거로 크기 감소

### 유지보수 효과
- **코드 복잡도**: XML+Kotlin → Pure Kotlin
- **테스트 용이성**: Compose 테스팅 활용
- **디버깅**: 단일 언어로 디버깅 효율성
- **팀 생산성**: 일관된 개발 패턴

## 🏁 성공 기준

### 기술적 목표 (100% 달성)
- [ ] Fragment 파일 0개
- [ ] XML layout 파일 0개 (시스템 필수 제외)
- [ ] DataBinding/ViewBinding 사용 0개
- [ ] 100% Compose UI 달성
- [ ] 모든 기능 정상 동작

### 성능 목표
- [ ] 앱 시작 시간 20% 이상 개선
- [ ] 메모리 사용량 15% 이상 감소
- [ ] 빌드 시간 30% 이상 단축
- [ ] 전체 테스트 통과율 100%

### 품질 목표
- [ ] 모든 화면 Material 3 디자인 적용
- [ ] 접근성 가이드라인 100% 준수
- [ ] Preview 함수 100% 커버리지
- [ ] 코드 리뷰 통과율 100%

---

## 📋 실행 체크리스트

### Phase 2: Feature-Common (Week 1)
- [ ] Init 모듈 Compose 의존성 추가
- [ ] InitIntakeScreen.kt 구현
- [ ] InitLanguageScreen.kt 구현  
- [ ] InitTimeScreen.kt 구현
- [ ] Init 모듈 Fragment/XML 제거
- [ ] Setting 모듈 AccountScreen.kt 구현
- [ ] Setting 모듈 CommonSettingScreen.kt 구현
- [ ] Setting 모듈 Fragment 제거

### Phase 3: Core UI (Week 2)
- [ ] TimePickerDialog.kt 구현
- [ ] DatePickerDialog.kt 구현
- [ ] CustomSwitch.kt 구현
- [ ] TextWithIcon.kt 구현
- [ ] TextWithSwitch.kt 구현
- [ ] ExpandableText.kt 구현
- [ ] BottomSheetDialog.kt 구현
- [ ] AlertDialog.kt 구현
- [ ] 기존 커스텀 뷰 제거
- [ ] XML 레이아웃 제거

### Phase 4: App & Final (Week 3)
- [ ] WaterActivity.kt Compose 전환
- [ ] activity_water.xml 제거
- [ ] WaterIntakeDialog.kt Compose 변환
- [ ] LogEditBottomDialog.kt Compose 변환
- [ ] 모든 build.gradle DataBinding 제거
- [ ] 전체 프로젝트 빌드 검증
- [ ] 모든 기능 테스트 완료

**🎯 최종 목표: Fragment+XML+DataBinding 0개, 100% Pure Compose Android App 달성!**