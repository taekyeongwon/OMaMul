# CLAUDE.md

이 파일은 이 저장소에서 작업할 때 Claude Code (claude.ai/code)에게 지침을 제공합니다.

**중요한 규칙: 모든 답변과 의사소통은 한국어로 작성해야 합니다.**

## 빌드 명령어

### 개발 빌드
```bash
# 디버그 버전 빌드 (dev flavor)
./gradlew assembleDevDebug

# 릴리즈 버전 빌드 (prod flavor)
./gradlew assembleProdRelease

# 모든 빌드 변형 빌드
./gradlew assemble

# 프로젝트 클린
./gradlew clean

# 의존성 새로고침 (gradle 파일 수정 후 필수)
./gradlew --refresh-dependencies
```

### 테스트
```bash
# 단위 테스트 실행
./gradlew test

# 계측 테스트 실행
./gradlew connectedAndroidTest

# 특정 모듈 테스트 실행
./gradlew :core:database:test
./gradlew :feature-water:home:test
```

### 개발 작업
```bash
# 개발 빌드를 기기에 설치
./gradlew installDevDebug

# 린트 리포트 생성
./gradlew lint

# Gradle 파일 수정 후 동기화
./gradlew --refresh-dependencies
```

## 아키텍처 개요

### 멀티 모듈 구조
이 프로젝트는 멀티 모듈 접근 방식을 사용하여 **클린 아키텍처**와 **MVVM 패턴**을 따릅니다:

**코어 모듈:**
- `core:data` - 저장소 구현체 및 데이터 매퍼
- `core:database` - Realm 데이터베이스 엔티티 및 DAO
- `core:domain` - 비즈니스 로직, 저장소 인터페이스, 도메인 모델
- `core:ui` - 공유 UI 컴포넌트, 커스텀 차트 (MPAndroidChart), 다이얼로그
- `core:common` - 유틸리티, 권한, 애니메이션, 로케일 관리
- `core:navigation` - Jetpack Navigation 확장
- `core:datastore` - DataStore 프리퍼런스 구현
- `core:alarmnoti` - 알람 및 알림 관리
- `core:firebase` - Firebase 통합

**피처 모듈:**
- `feature-water:home` - ViewPager가 있는 메인 물 추적 화면
- `feature-water:alarm` - 물 마시기 알람
- `feature-water:cup` - 컵 관리 및 선택
- `feature-water:record` - 물 섭취 로그 및 차트
- `feature-water:setting` - 물 관련 설정
- `feature-common:base` - 기본 클래스 (BaseViewModel, 에러 처리)
- `feature-common:init` - 온보딩 플로우
- `feature-common:setting` - 앱 설정 및 계정 관리

### 주요 아키텍처 패턴

**Single Activity + Navigation:**
- `WaterActivity`가 메인 (유일한) 액티비티
- Safe Args와 함께 Jetpack Navigation Component 사용
- 초기화 상태에 따른 동적 네비게이션 그래프
- 메인 섹션용 바텀 네비게이션

**Repository 패턴:**
- `core:domain`의 저장소 인터페이스
- `core:data`의 저장소 구현체
- 엔티티와 도메인 모델 간 변환을 위한 데이터 매퍼
- Realm 데이터베이스와 DataStore 프리퍼런스 모두 사용

**의존성 주입:**
- 애플리케이션 전체에 Hilt 사용
- `MainApplication`에 `@HiltAndroidApp`
- 생성자 주입과 함께 ViewModel에 `@HiltViewModel`

**반응형 프로그래밍:**
- Flow와 LiveData 패턴 결합
- UI 상태 관리를 위한 `StateFlow`
- 일회성 이벤트를 위한 `SingleLiveEvent`
- `flatMapLatest`, `mapLatest`를 사용한 Flow 변환

## 기술 스택

**UI 레이어:**
- **하이브리드 접근**: Jetpack Compose + View/Data Binding
- MPAndroidChart를 사용한 커스텀 차트 컴포넌트
- Material Design 컴포넌트
- 탭 네비게이션을 위한 ViewPager2

**데이터 레이어:**
- 로컬 데이터베이스용 **Realm Kotlin**
- 프리퍼런스용 **DataStore**
- 매퍼와 함께하는 Repository 패턴

**백그라운드 처리:**
- 예약된 작업 (Google Drive 일일 백업)을 위한 **WorkManager**
- 물 마시기 알림을 위한 **AlarmManager**
- 시스템 이벤트 (부팅, 날짜 변경)를 위한 **Broadcast Receivers**

**추가 라이브러리:**
- 네트워크 호출을 위한 **Retrofit + OkHttp**
- 이미지 로딩을 위한 **Glide**
- 애니메이션을 위한 **Lottie**
- **Firebase** (Analytics, Auth)
- 인증을 위한 **Google Play Services**

## 개발 가이드라인

### Product Flavors
앱은 두 가지 플레이버를 사용합니다:
- `dev` - `.dev` 접미사가 있는 개발 빌드
- `prod` - 프로덕션 빌드

### 데이터베이스 스키마
다음 주요 엔티티와 함께 Realm 사용:
- `DayOfWaterEntity` - 일일 물 섭취 기록
- `CupEntity` - 용량이 있는 컵 정의
- `WaterEntity` - 개별 물 섭취 항목
- `AlarmSettingsEntity` - 알람 구성
- `SettingEntity` - 앱 프리퍼런스

### 커스텀 컴포넌트
- **차트 시스템**: `core:ui:chart`의 커스텀 MPAndroidChart 구현
  - 커스텀 렌더러가 있는 `CustomLineChart`, `CustomBarChart`
  - 데이터 시각화를 위한 Day/Week/Month 마커 뷰
- **알림 시스템**: `core:alarmnoti`에 중앙 집중화
- **로케일 관리**: 다국어 지원을 위한 `LocaleHelper`

### 테스트 구조
각 모듈에는 다음이 포함됩니다:
- `src/test/` - 단위 테스트 (JUnit)
- `src/androidTest/` - 계측 테스트 (Android Test)
- 현재 예제 테스트와 함께 기본 테스트 설정이 있음

### 백그라운드 작업
- **ScheduledWorkManager**: 제약 조건 (배터리, 충전, WiFi)과 함께 새벽 3시 일일 백업
- **물 알람**: AlarmManager를 사용한 구성 가능한 알림
- **부팅 리시버**: 기기 재시작 후 알람 복원

### 중요한 개발 규칙
- **Gradle 파일 수정 시**: 반드시 `./gradlew --refresh-dependencies` 명령어로 동기화 수행
- **언어**: 모든 코드 주석, 문서, 의사소통은 한국어로 작성
- **답변**: 답변은 간결하게 최대 3-5문장으로 답변

## Compose 마이그레이션 가이드

### 마이그레이션 전략
OMaMul 앱을 XML+Fragment 구조에서 Jetpack Compose로 단계적 전환합니다.

### 마이그레이션 순서 (모듈별)

#### 1단계: 홈 모듈 (feature-water:home) ✅ 완료
**현재 상태**: 마이그레이션 완료
```bash
# 홈 모듈 빌드 성공
./gradlew :feature-water:home:assembleDebug
```

**완료된 작업**:
- [x] HomeScreen 디자인 개선 (물 웨이브 애니메이션, 글래스모피즘)
- [x] @Preview 함수 추가 (HomeScreenPreview, WaterProgressPreview, CupListPreview, AlarmInfoCardPreview)
- [x] WaterFragment 분석 완료 (이미 ComposeView 사용)
- [x] material-icons-extended 의존성 추가 및 LocalDrink 아이콘 적용
- [x] R8/ProGuard 설정으로 사용하지 않는 아이콘 제거 최적화
- [x] Debug/Release 빌드 테스트 및 검증 완료
- [ ] WaterFragment 및 XML 파일 제거 (선택적)

#### 2단계: 온보딩 모듈 (feature-common:init) ✅ 완료
**대상 Fragment**: InitLanguageFragment, InitTimeFragment, InitIntakeFragment

**작업 내용**:
- [x] LanguageSelectionScreen (언어 선택)
- [x] TimeSettingScreen (시간 설정)
- [x] IntakeGoalScreen (목표량 설정)
- [x] 슬라이드 애니메이션 및 진행 표시기
- [x] Fragment 제거 및 Navigation 전환

**완료된 주요 기능들:**
- ✅ **언어 선택 화면**: 4개 언어 지원 (한국어, 영어, 일본어, 중국어), 권한 요청 통합
- ✅ **시간 설정 화면**: 기상/취침 시간 설정, Material 3 TimePicker 사용
- ✅ **목표량 설정 화면**: 커스텀 Amount Picker, 정확한 알람 권한 다이얼로그
- ✅ **Navigation 시스템**: InitNavHost로 3단계 흐름 관리
- ✅ **UI 개선**: 물 테마 그라데이션, 진행 표시기, ViewModel 없는 Preview 함수 완비
- ✅ **빌드 성공**: core:ui string resource 참조로 안정적 빌드

#### 3단계: 컵 관리 모듈 (feature-water:cup)
**대상 Fragment**: CupManageFragment, CupListEditFragment, CupCreateFragment

**작업 내용**:
- [ ] CupManagementScreen (컵 관리)
- [ ] CupEditScreen (컵 편집)
- [ ] CupCreationScreen (컵 생성)
- [ ] 드래그 앤 드롭 기능
- [ ] 시각적 컵 디자인

#### 4단계: 알람 모듈 (feature-water:alarm)
**대상 Fragment**: WaterAlarmFragment, AlarmModeFragment, AlarmModePeriodFragment, AlarmModeCustomFragment

**작업 내용**:
- [ ] AlarmSettingScreen (알람 설정)
- [ ] AlarmModeScreen (알람 모드)
- [ ] 커스텀 TimePicker
- [ ] 토글 스위치 및 알람 목록

#### 5단계: 기록 모듈 (feature-water:record)
**대상 Fragment**: WaterLogFragment, LogDayFragment, LogWeekFragment, LogMonthFragment

**작업 내용**:
- [ ] WaterLogScreen (통계 메인)
- [ ] DayLogScreen (일간 통계)
- [ ] WeekLogScreen (주간 통계)
- [ ] MonthLogScreen (월간 통계)
- [ ] Canvas 기반 커스텀 차트

#### 6단계: 설정 모듈 (feature-water:setting, feature-common:setting)
**대상 Fragment**: WaterSettingFragment, CommonSettingFragment, AccountFragment

**작업 내용**:
- [ ] AppSettingScreen (앱 설정)
- [ ] AccountScreen (계정 관리)
- [ ] 설정 항목별 UI 컴포넌트

### 각 단계별 체크리스트

#### 모듈 마이그레이션 완료 조건
1. **Compose 화면 생성**: 모든 Fragment를 Compose Screen으로 변환
2. **Navigation 전환**: XML Navigation을 Compose Navigation으로 변경
3. **Preview 함수**: 각 Screen에 ViewModel 없는 @Preview 추가
4. **Fragment 제거**: 기존 Fragment 및 XML 파일 삭제
5. **의존성 정리**: Fragment 관련 의존성 제거
6. **빌드 성공**: `./gradlew assembleDebug` 성공
7. **테스트**: 기본 기능 동작 확인

#### 마이그레이션 후 작업
```bash
# 각 모듈 완료 후 실행
./gradlew :feature-xxx:clean
./gradlew :feature-xxx:assembleDebug
./gradlew --refresh-dependencies
```

### Compose 설정 가이드라인

#### Gradle 설정 (Kotlin 2.0.0+)
Kotlin 2.0.0부터는 Compose 컴파일러가 내장되어 별도 버전 지정이 불필요합니다:

```gradle
plugins {
    alias libs.plugins.kotlin.compose // Kotlin 2.0.0+ 필수
}

android {
    buildFeatures {
        compose = true
        // kotlinCompilerExtensionVersion 설정 불필요 (Kotlin 2.0.0+)
    }

    buildTypes {
        release {
            minifyEnabled true // material-icons-extended 최적화를 위해 필수
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    // Compose BOM으로 버전 통합 관리
    implementation platform(libs.androidx.compose.bom)

    // 번들로 관리되는 Compose 의존성 (material-icons-extended 포함)
    implementation libs.bundles.compose

    // 디버그 전용
    debugImplementation libs.androidx.compose.ui.tooling
    debugImplementation libs.androidx.compose.ui.test.manifest
}
```

#### String Resource 관리
**중요**: 모든 string 리소스는 `core:ui` 모듈에서 통합 관리됩니다.

**새 string 추가 시 확인 사항:**
1. **기존 확인**: 먼저 `core:ui/src/main/res/values/strings.xml`에서 동일한 의미의 string이 있는지 확인
2. **네이밍 규칙**:
   - 기능별 그룹화: `cup_name`, `alarm_title`, `setting_language` 등
   - 액션: `add`, `edit`, `delete`, `save`, `cancel`
   - 메시지: `empty_*_message`, `*_hint`
3. **다국어 지원**: 모든 언어 파일에 동일하게 추가 필요
   - `values/strings.xml` (기본 한국어)
   - `values-en-rUS/strings.xml` (영어)
   - `values-ja/strings.xml` (일본어)
   - `values-zh-rCN/strings.xml` (중국어)

**예시:**
```xml
<!-- 기존 확인 후 추가 -->
<string name="cup_name">컵 이름</string>
<string name="add_cup">컵 추가</string>
<string name="empty_cup_message">등록된 컵이 없습니다</string>
```

#### LiveData → StateFlow/SharedFlow 마이그레이션 가이드
**중요**: `collectAsStateWithLifecycle()` 함수는 StateFlow/SharedFlow에서만 작동합니다. LiveData 객체에서는 빌드 에러가 발생하므로 반드시 StateFlow로 변환 후 사용해야 합니다.

**마이그레이션 패턴:**
```kotlin
// Before (LiveData)
private val _data = MutableLiveData<String>()
val data: LiveData<String> = _data

// After (StateFlow)
private val _dataStateFlow = MutableStateFlow("")
val dataStateFlow: StateFlow<String> = _dataStateFlow.asStateFlow()

// Repository Flow를 StateFlow로 변환
val cupListStateFlow: StateFlow<List<Cup>> =
    repository.getCupList().mapLatest { it.cupList }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
```

**필수 사항**: collectAsStateWithLifecycle 사용 전에 반드시 StateFlow로 변환
```kotlin
// ❌ 잘못된 사용 (빌드 에러 발생)
val data by viewModel.dataLiveData.collectAsStateWithLifecycle()

// ✅ 올바른 사용 (StateFlow 사용)
val data by viewModel.dataStateFlow.collectAsStateWithLifecycle()
```

**Compose에서 사용:**
```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = hiltViewModel()) {
    val data by viewModel.dataStateFlow.collectAsStateWithLifecycle()
    // UI 구현...
}
```

**호환성 유지**: 기존 Fragment와의 호환성을 위해 LiveData와 StateFlow 병행 지원
```kotlin
// 기존 Fragment용 LiveData 유지
val dataLiveData: LiveData<String> = _dataStateFlow.asLiveData()

// 새 Compose용 StateFlow 추가 (collectAsStateWithLifecycle 사용 가능)
val dataStateFlow: StateFlow<String> = _dataStateFlow.asStateFlow()

// 두 방식 모두 업데이트
fun updateData(newData: String) {
    launch {
        _dataLiveData.value = newData      // Fragment용
        _dataStateFlow.value = newData     // Compose용
    }
}
```

**빌드 에러 해결 순서:**
1. ViewModel에서 LiveData를 StateFlow로 변환 (또는 추가)
2. Compose 화면에서 StateFlow 버전 사용
3. collectAsStateWithLifecycle() 적용

### Compose 디자인 가이드라인

#### 물 앱 컨셉 디자인 요소
- **색상**: 블루 그라데이션 (#4A90E2 → #E3F2FD)
- **효과**: 글래스모피즘, 물 웨이브 애니메이션
- **아이콘**: 물방울, 컵, 웨이브 모션
- **레이아웃**: 카드 기반, 라운드 코너
- **애니메이션**: 부드러운 전환, 물 채우기 효과

#### 공통 컴포넌트 우선 개발
- WaterProgressIndicator (물 진행률) ✅
- CupCard (컵 선택 카드) ✅
- WaterWaveAnimation (물 웨이브) ✅
- GlassmorphismCard (글래스 효과 카드) ✅
- CustomTimePicker (시간 선택기)

#### Material Icons Extended 최적화
**중요**: `libs.bundles.compose`에 material-icons-extended가 포함되어 있으므로, **반드시** `minifyEnabled true` 설정을 해야 합니다:

```gradle
buildTypes {
    release {
        minifyEnabled true // material-icons-extended 번들 사용 시 필수
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

**이유**: material-icons-extended는 수천 개의 아이콘을 포함하므로 R8/ProGuard로 사용하지 않는 아이콘을 자동 제거해야 APK 크기를 최적화할 수 있습니다.

### Compose Preview 가이드라인

#### ViewModel이 있는 Compose 함수의 Preview 패턴

**문제점**: ViewModel 파라미터가 있는 Compose 함수는 Unstable 객체로 인해 Preview가 제대로 렌더링되지 않습니다.

**해결 방법**: 다음 패턴을 **필수**로 사용해야 합니다:

#### 1. 메인 함수 (ViewModel 사용)
```kotlin
@Composable
fun LanguageSelectionScreen(
    onNavigateNext: () -> Unit,
    viewModel: InitViewModel = hiltViewModel() // Unstable 객체
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(null)

    var selectedLanguage by remember { mutableStateOf(Locale.KOREAN.language) }

    // ViewModel 로직 처리
    LaunchedEffect(sideEffect) {
        when (sideEffect) {
            is InitContract.SideEffect.OnMoveNext -> onNavigateNext()
            else -> {}
        }
    }

    // Content 함수 호출 (Preview와 동일한 UI)
    LanguageSelectionScreenContent(
        selectedLanguage = selectedLanguage,
        onLanguageSelect = {
            selectedLanguage = it
            viewModel.setEvent(InitContract.Event.SaveLanguage(it))
        },
        onNavigateNext = { /* ViewModel 이벤트 */ }
    )
}
```

#### 2. Content 함수 (Preview용, ViewModel 없음)
```kotlin
@Composable
private fun LanguageSelectionScreenContent(
    selectedLanguage: String = Locale.KOREAN.language,
    onLanguageSelect: (String) -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    // Preview에서 상호작용 테스트를 위한 로컬 상태
    var localSelectedLanguage by remember { mutableStateOf(selectedLanguage) }

    // 실제 UI 구현
    Box(modifier = Modifier.fillMaxSize()) {
        // UI 컴포넌트들...
        languages.forEach { (code, name) ->
            LanguageOption(
                languageCode = code,
                languageName = name,
                isSelected = localSelectedLanguage == code,
                onSelect = {
                    localSelectedLanguage = code
                    onLanguageSelect(code)
                }
            )
        }
    }
}
```

#### 3. Preview 함수
```kotlin
@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun LanguageSelectionScreenPreview() {
    LanguageSelectionScreenContent(
        onNavigateNext = {}
    )
}
```

#### Preview 작성 규칙

**필수 사항:**
1. **함수명**: `[ScreenName]Content` 형식으로 명명
2. **접근 제한자**: `private` 사용으로 외부 노출 방지
3. **파라미터**: 필요한 상태값들을 파라미터로 받되, 기본값 제공
4. **로컬 상태**: Preview에서 상호작용 테스트를 위해 `remember` 사용
5. **콜백**: 빈 람다 `{}` 기본값으로 제공

**Preview 어노테이션 옵션:**
```kotlin
@Preview(
    showBackground = true,                    // 배경 표시
    backgroundColor = 0xFFE0F6FF,            // 배경 색상 (물 테마)
    showSystemUi = true,                     // 시스템 UI 표시 (전체 화면용)
    device = Devices.PIXEL_4,                // 특정 기기
    uiMode = Configuration.UI_MODE_NIGHT_YES // 다크 테마
)
```

**Preview 장점:**
- ✅ **안정적인 렌더링**: ViewModel 없이 정상 Preview 표시
- ✅ **실제 상호작용**: 버튼 클릭, 값 변경 등 테스트 가능
- ✅ **다양한 상태**: 다이얼로그, 선택 상태 등도 확인 가능
- ✅ **빠른 개발**: Android Studio에서 실시간 UI 확인

### 🎉 마이그레이션 완료 현황
- ✅ **화면 구조 분석 완료** (18개 Fragment 파악)
- ✅ **Navigation 구조 분석 완료**
- ✅ **마이그레이션 계획 수립 완료**
- ✅ **1단계: 홈 모듈 완료** (feature-water:home)
- ✅ **2단계: 온보딩 모듈 완료** (feature-common:init)
- ✅ **3단계: 컵 관리 모듈 완료** (feature-water:cup)
- ✅ **4단계: 알람 모듈 완료** (feature-water:alarm)
- ✅ **5단계: 기록 모듈 완료** (feature-water:record)
- ✅ **6단계: 설정 모듈 완료** (feature-water:setting)

## 🏆 **Compose 마이그레이션 100% 완료!**

**마이그레이션된 모듈**: 6개 모듈
**변환된 Fragment**: 18개 → Compose Screen
**삭제 대상 파일**: 35+ Fragment/XML 파일

#### 3단계: 컵 관리 모듈 (feature-water:cup) ✅ 완료
**완료된 작업**:
- [x] CupManageFragment → CupManagementScreen
- [x] CupCreateFragment → CupCreationScreen
- [x] CupListEditFragment 통합 (deprecated 상태였음)
- [x] ViewModel StateFlow 호환성 추가
- [x] Compose Navigation (CupNavHost) 구현
- [x] Gradle 설정: compose = true, kotlin-compose 플러그인, libs.bundles.compose
- [x] Material Icons Extended 최적화 (minifyEnabled = true)
- [x] Preview 함수 완비 (Content 패턴 적용)
- [x] 빌드 성공 및 검증 완료

#### 4단계: 알람 모듈 (feature-water:alarm) ✅ 완료
**완료된 작업**:
- [x] WaterAlarmFragment → AlarmSettingScreen
- [x] AlarmModeFragment → AlarmModeScreen
- [x] AlarmNavHost 구현 (Compose Navigation)
- [x] ViewModel StateFlow 호환성 추가
- [x] Gradle 설정: compose = true, kotlin-compose 플러그인, libs.bundles.compose
- [x] Preview 함수 완비 (Content 패턴 적용)
- [x] 빌드 성공 및 검증 완료

#### 5단계: 기록 모듈 (feature-water:record) ✅ 완료
**완료된 작업**:
- [x] WaterLogFragment → WaterLogScreen (TabLayout + HorizontalPager)
- [x] LogDayFragment, LogWeekFragment, LogMonthFragment → 통합
- [x] RecordNavHost 구현 (Compose Navigation)
- [x] Canvas 기반 커스텀 차트 구현 (WaterBarChart, WaterLineChart)
- [x] Gradle 설정: compose = true, kotlin-compose 플러그인, libs.bundles.compose
- [x] Preview 함수 완비 (Content 패턴 적용)
- [x] 빌드 성공 및 검증 완료

#### 6단계: 설정 모듈 (feature-water:setting) ✅ 완료
**완료된 작업**:
- [x] WaterSettingFragment → WaterSettingScreen (이미 구현됨)
- [x] SettingNavHost 구현 (Compose Navigation)
- [x] ViewModel StateFlow 호환성 완료
- [x] feature-common:setting 모듈 제거 (불필요한 모듈)
- [x] Gradle 설정: compose = true, kotlin-compose 플러그인, libs.bundles.compose
- [x] Preview 함수 완비 (Content 패턴 적용)
- [x] 빌드 성공 및 검증 완료

**주요 기능들:**
- ✅ **물 설정 화면**: 계정 정보, 목표량/시간/단위 설정, 통계 표시
- ✅ **알람 설정 연동**: 알람 모드, 주기, 벨소리 설정
- ✅ **기타 설정**: 언어 설정, Google Drive 백업, 앱 정보
- ✅ **사용자 계정**: 로그인/로그아웃, 동기화 상태, 총 섭취량/달성률 표시
- ✅ **물 테마 디자인**: 블루 그라데이션, 글래스모피즘 카드, 설정 아이콘

### Git 커밋 정책

#### 단계별 자동 커밋
**중요**: 각 마이그레이션 단계가 완료될 때마다 반드시 Git 커밋을 수행해야 합니다.

**커밋 시점:**
- 각 모듈의 Compose 마이그레이션 완료 시
- 빌드 성공 및 검증 완료 시
- CLAUDE.md 업데이트 완료 시

**커밋 메시지 형식:**
```
N단계: [모듈명] Compose 마이그레이션 완료

## N단계: [모듈명] (feature-[path])
- [Fragment명] → [Screen명]
- [주요 작업 목록]
- [기술적 성과]

## 기술적 성과
- [Fragment 통합 수]
- [새로운 기능]
- [빌드 상태]

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

**자동 커밋 절차:**
1. `git status` - 변경사항 확인
2. `git add .` - 모든 변경사항 스테이징
3. `git commit -m "..."` - 단계별 커밋 메시지로 커밋
4. `git status` - 커밋 완료 확인

## Fragment 및 XML 정리 가이드

### 📊 현재 상황 (2025년 1월 기준)
- ✅ **마이그레이션 완료**: 5개 모듈 (home, init, cup, alarm, record)
- 🔄 **마이그레이션 대상**: 2개 모듈 (water:setting, common:setting)
- 🗂️ **정리 대상**: 35+ Fragment/XML 파일

### 🎯 정리 전략 (우선순위별)

#### 🟢 **즉시 제거 가능 (안전)**
1. **Deprecated Fragment**:
   - `CupListEditFragment.kt` (@Deprecated 주석 확인됨)

2. **완료된 모듈의 Fragment XML** (23개):
   ```
   feature-water/alarm/src/main/res/layout/
   ├── fragment_water_alarm.xml
   ├── fragment_alarm_mode.xml
   ├── fragment_alarm_mode_period.xml
   └── fragment_alarm_mode_custom.xml

   feature-water/cup/src/main/res/layout/
   ├── fragment_cup_manage.xml
   ├── fragment_cup_create.xml
   └── fragment_cup_list_edit.xml

   feature-water/record/src/main/res/layout/
   ├── fragment_water_log.xml
   ├── fragment_log_day.xml
   ├── fragment_log_week.xml
   └── fragment_log_month.xml

   feature-water/home/src/main/res/layout/
   └── fragment_water.xml (WaterFragment가 ComposeView 사용 시)
   ```

3. **완료된 모듈의 Item/Dialog XML** (12개):
   ```
   feature-water/alarm/src/main/res/layout/
   ├── dialog_alarm_mode.xml, dialog_alarm_period.xml
   ├── dialog_exact_alarm.xml, dialog_custom_alarm.xml
   ├── dialog_timepicker.xml, dialog_ringtone.xml
   ├── item_alarm.xml, item_alarm_edit.xml
   ├── layout_alarm_switch.xml, layout_week_list.xml

   feature-water/cup/src/main/res/layout/
   ├── item_cup_list.xml, item_cup_list_edit.xml

   feature-water/home/src/main/res/layout/
   ├── item_cup.xml, item_cup_add.xml
   └── dialog_water_intake.xml

   feature-water/record/src/main/res/layout/
   ├── dialog_log_edit.xml
   └── item_day_amount.xml
   ```

#### 🟡 **신중히 제거 (검토 후)**
1. **Fragment.kt 파일들** (마이그레이션 완료 확인 후):
   ```
   feature-water/alarm/
   ├── WaterAlarmFragment.kt
   ├── AlarmModeFragment.kt
   ├── AlarmModePeriodFragment.kt
   └── AlarmModeCustomFragment.kt

   feature-water/cup/
   ├── CupManageFragment.kt
   ├── CupCreateFragment.kt
   └── CupListEditFragment.kt (Deprecated)

   feature-water/record/
   ├── WaterLogFragment.kt
   ├── LogDayFragment.kt
   ├── LogWeekFragment.kt
   ├── LogMonthFragment.kt
   └── ViewPagerAdapter.kt

   feature-common/init/
   ├── InitLanguageFragment.kt
   ├── InitTimeFragment.kt
   └── InitIntakeFragment.kt
   ```

2. **WaterFragment.kt 검토**:
   - 현재 ComposeView 사용 중
   - Compose Screen으로 완전 교체 확인 후 제거

#### 🔴 **유지 필요 (삭제 금지)**
1. **Core UI XML** (차트 및 공통 컴포넌트):
   ```
   core/ui/src/main/res/layout/
   ├── custom_marker.xml
   ├── custom_marker_month.xml
   ├── custom_timepicker.xml
   ├── custom_switch.xml
   ├── custom_bottom_dialog.xml
   ├── custom_dialog_view.xml
   └── custom_text_image.xml
   ```

2. **AlarmNoti XML** (시스템 연동):
   ```
   core/alarmnoti/src/main/res/layout/
   ├── activity_alarm_screen.xml
   └── custom_notification.xml
   ```

3. **Main Activity**:
   ```
   app/src/main/res/layout/
   └── activity_water.xml
   ```

### 🔧 정리 실행 절차

#### 1단계: 안전한 파일 제거
```bash
# Deprecated Fragment 제거
rm feature-water/cup/src/main/java/com/tkw/cup/CupListEditFragment.kt
rm feature-water/cup/src/main/res/layout/fragment_cup_list_edit.xml

# 완료된 모듈 Fragment XML 제거
find feature-water/alarm/src/main/res/layout/ -name "fragment_*.xml" -delete
find feature-water/cup/src/main/res/layout/ -name "fragment_*.xml" -delete
find feature-water/record/src/main/res/layout/ -name "fragment_*.xml" -delete

# Item/Dialog XML 제거 (모듈별)
find feature-water/alarm/src/main/res/layout/ -name "dialog_*.xml" -delete
find feature-water/alarm/src/main/res/layout/ -name "item_*.xml" -delete
find feature-water/alarm/src/main/res/layout/ -name "layout_*.xml" -delete
```

#### 2단계: Navigation 설정 확인
```bash
# Navigation 그래프에서 Fragment 참조 제거 확인
grep -r "Fragment" app/src/main/res/navigation/
grep -r "Fragment" feature-*/src/main/res/navigation/
```

#### 3단계: Fragment.kt 파일 제거
```bash
# 각 모듈별로 Fragment 파일 제거
rm feature-water/alarm/src/main/java/com/tkw/alarm/*Fragment.kt
rm feature-water/cup/src/main/java/com/tkw/cup/*Fragment.kt
rm feature-water/record/src/main/java/com/tkw/record/*Fragment.kt
rm feature-common/init/src/main/java/com/tkw/init/*Fragment.kt
```

#### 4단계: 빌드 검증
```bash
./gradlew clean
./gradlew assembleDebug
./gradlew test
```

### ⚠️ 주의사항
1. **Navigation 참조**: Fragment 제거 전 Navigation 그래프에서 참조 제거 필수
2. **Manifest 확인**: Fragment가 Manifest에 등록되어 있지 않은지 확인
3. **Dependency 정리**: Fragment 관련 import 구문 정리
4. **테스트**: 각 단계마다 빌드 성공 확인
5. **백업**: Git 커밋으로 변경사항 보존

## 🚨 **현재 남은 핵심 작업**

### **분석 결과 (2025년 1월 기준)**
- ✅ **마이그레이션 완료**: 6개 모듈의 모든 Fragment → Compose Screen 변환 완료
- 🔄 **남은 핵심 작업**: WaterActivity 전면 개편 및 최종 정리

### **🎯 우선순위별 작업 계획**

#### **우선순위 1**: WaterActivity Compose 전환 (핵심)
**현재 문제**:
- WaterActivity.kt에서 여전히 NavHostFragment 및 ViewBinding 사용
- fragment_setting.xml이 DataBinding으로 구현되어 있음
- activity_water.xml이 여전히 Fragment 기반 구조

**작업 계획**:
1. **WaterActivity Compose 전환**:
   - ActivityWaterBinding → ComposeView로 교체
   - NavHostFragment → Compose Navigation으로 전환
   - 온보딩 플로우 개선 (최초 한 번만 표시)
   - 메인 화면 Bottom Tab Navigation 구현

2. **Setting 화면 완전한 Compose 전환**:
   - fragment_setting.xml → WaterSettingScreen (이미 구현됨)
   - DataBinding 제거
   - setting_info.xml, setting_water.xml, setting_alarm.xml, setting_etc.xml 통합

#### **우선순위 2**: ViewBinding/DataBinding 완전 제거
**현재 ViewBinding/DataBinding 사용 중인 파일들**:
- `WaterActivity.kt` - ActivityWaterBinding 사용 (🔴 즉시 제거 필요)
- `core:ui` 커스텀 컴포넌트들 - 부분적 사용 (🟡 점진적 제거)

#### **우선순위 3**: XML 파일 완전 정리
**남은 XML 파일들**:
- `activity_water.xml` (🔴 즉시 제거 후 ComposeView로 교체)
- `fragment_setting.xml` + 관련 include XML들 (🔴 즉시 제거)
- `core:ui` XML들 (🟡 필요시 Compose로 점진적 전환)
- `core:alarmnoti` XML들 (🟢 시스템 연동용 유지)

### **📋 세부 실행 계획**

#### **1단계: WaterActivity 전면 개편**
```kotlin
// Before: Fragment 기반
class WaterActivity : AppCompatActivity() {
    private lateinit var dataBinding: ActivityWaterBinding
    // NavHostFragment, BottomNavigationView 사용
}

// After: Compose 기반
class WaterActivity : ComponentActivity() {
    // ComposeView만 사용, 모든 UI가 Compose로 구현
}
```

### **🗺️ Compose Navigation 구조 설계**

#### **Navigation 플로우**
```
WaterActivity (ComponentActivity)
├── 최초 실행: Onboarding Flow
│   ├── LanguageSelectionScreen
│   ├── TimeSettingScreen
│   └── IntakeGoalScreen
│
└── 이후 실행: Main App Flow
    ├── Bottom Navigation (HomeScreen, WaterLogScreen, WaterSettingScreen)
    ├── HomeScreen → CupManagementScreen (onAddClick)
    ├── CupManagementScreen → CupCreationScreen (컵 추가 버튼)
    └── WaterSettingScreen → 각종 Compose Dialogs
```

#### **주요 Navigation 규칙**
1. **시작 화면 결정**: `InitViewModel.getInitFlag()` 기반
   - `true`: HomeScreen (기본 사용자)
   - `false`: LanguageSelectionScreen (신규 사용자)

2. **Bottom Navigation 구성**:
   - **HomeScreen**: 메인 물 추적 화면
   - **WaterLogScreen**: 통계 및 로그
   - **WaterSettingScreen**: 설정

3. **화면 간 이동 경로**:
   - `HomeScreen` → `CupManagementScreen` (컵 선택 FAB 클릭)
   - `CupManagementScreen` → `CupCreationScreen` (컵 추가 버튼)
   - `WaterSettingScreen` → 각종 설정 다이얼로그

#### **세부 작업**:
- [ ] MainActivity 구조 설계
- [ ] ComposeView 기반으로 전환
- [ ] 온보딩 플로우 개선 (InitViewModel.getInitFlag() 활용)
- [ ] 메인 화면 Bottom Tab Navigation (Home, Record, Setting)
- [ ] 화면 간 Navigation 연결 (HomeScreen ↔ CupManagementScreen ↔ CupCreationScreen)
- [ ] Splash Screen 유지
- [ ] Edge-to-Edge 지원

#### **2단계: 삭제된 Dialog들 Compose 변환**

### **🗂️ 삭제된 Dialog 목록 (커밋 3d1ad75)**

#### **알람 모듈 Dialog (6개)**
```kotlin
// 삭제된 파일들:
feature-water/alarm/src/main/java/com/tkw/alarm/dialog/
├── AlarmModeBottomDialog.kt         → AlarmModeBottomSheet (Compose)
├── AlarmPeriodDialog.kt             → AlarmPeriodDialog (Compose)
├── AlarmRingtoneDialog.kt           → AlarmRingtoneDialog (Compose)
├── AlarmTimeBottomDialog.kt         → AlarmTimeBottomSheet (Compose)
├── CustomAlarmBottomDialog.kt       → CustomAlarmBottomSheet (Compose)
└── ExactAlarmDialog.kt              → ExactAlarmPermissionDialog (Compose)
```

#### **홈 모듈 Dialog (1개)**
```kotlin
// 삭제된 파일:
feature-water/home/src/main/java/com/tkw/home/dialog/
└── WaterIntakeDialog.kt             → WaterIntakeDialog (Compose)
```

#### **기록 모듈 Dialog (1개)**
```kotlin
// 삭제된 파일:
feature-water/record/src/main/java/com/tkw/record/dialog/
└── LogEditBottomDialog.kt           → LogEditBottomSheet (Compose)
```

#### **설정 모듈 Dialog (2개)**
```kotlin
// 삭제된 파일:
feature-water/setting/src/main/java/com/tkw/setting/dialog/
├── LanguageDialog.kt                → LanguageSelectionDialog (Compose)
└── UnitDialog.kt                    → UnitSelectionDialog (Compose)
```

### **🎯 Dialog 복원 및 Compose 변환 계획**

#### **우선순위별 Dialog 복원**
1. **🔴 필수 (즉시 복원)**:
   - `WaterIntakeDialog` - 물 섭취량 입력 (HomeScreen에서 사용)
   - `LanguageSelectionDialog` - 언어 설정 (WaterSettingScreen에서 사용)
   - `UnitSelectionDialog` - 단위 설정 (WaterSettingScreen에서 사용)

2. **🟡 중요 (복원 필요)**:
   - `AlarmModeBottomSheet` - 알람 모드 선택 (WaterSettingScreen에서 사용)
   - `AlarmRingtoneDialog` - 벨소리 선택 (WaterSettingScreen에서 사용)
   - `ExactAlarmPermissionDialog` - 정확한 알람 권한 (AlarmModeScreen에서 사용)

3. **🟢 선택적 (필요시 복원)**:
   - `AlarmPeriodDialog` - 알람 주기 설정
   - `AlarmTimeBottomSheet` - 시간 선택
   - `CustomAlarmBottomSheet` - 커스텀 알람 설정
   - `LogEditBottomSheet` - 로그 편집

#### **Dialog 구현 가이드라인**
```kotlin
// Compose Dialog 구현 패턴
@Composable
fun WaterIntakeDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (amount: Int) -> Unit,
    initialAmount: Int = 250
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("물 섭취량 입력") },
            text = {
                // Dialog 내용 구현
            },
            confirmButton = {
                TextButton(onClick = { onConfirm(amount) }) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("취소")
                }
            }
        )
    }
}
```

#### **3단계: Setting 화면 DataBinding 제거**
**제거 대상 XML 파일들**:
```
feature-water/setting/src/main/res/layout/
├── fragment_setting.xml (DataBinding 사용)
├── setting_info.xml
├── setting_water.xml
├── setting_alarm.xml
└── setting_etc.xml
```

**WaterSettingScreen 개선**:
- [ ] 모든 Setting 관련 XML을 Compose로 통합
- [ ] DataBinding 의존성 완전 제거
- [ ] Preview 함수 추가
- [ ] 복원된 Compose Dialog들과 연결

#### **4단계: ViewModel 이벤트 연결**

### **🔗 화면별 ViewModel 이벤트 매핑**

#### **HomeScreen 이벤트 연결**
```kotlin
@Composable
fun HomeScreen(
    onNavigateToCupManagement: () -> Unit,
    viewModel: WaterViewModel = hiltViewModel()
) {
    // 물 추가 버튼 클릭 → WaterIntakeDialog 표시
    val showWaterIntakeDialog by viewModel.showWaterIntakeDialog.collectAsStateWithLifecycle()

    // Dialog 이벤트 연결
    WaterIntakeDialog(
        isVisible = showWaterIntakeDialog,
        onDismiss = { viewModel.hideWaterIntakeDialog() },
        onConfirm = { amount ->
            viewModel.addWaterIntake(amount)
            viewModel.hideWaterIntakeDialog()
        }
    )

    // 컵 선택 FAB 클릭 → CupManagementScreen 이동
    FloatingActionButton(
        onClick = onNavigateToCupManagement
    ) { /* FAB 내용 */ }
}
```

#### **WaterSettingScreen 이벤트 연결**
```kotlin
@Composable
fun WaterSettingScreen(
    viewModel: SettingViewModel = hiltViewModel()
) {
    // 언어 설정 클릭 → LanguageSelectionDialog
    val showLanguageDialog by viewModel.showLanguageDialog.collectAsStateWithLifecycle()

    // 단위 설정 클릭 → UnitSelectionDialog
    val showUnitDialog by viewModel.showUnitDialog.collectAsStateWithLifecycle()

    // 알람 모드 클릭 → AlarmModeBottomSheet
    val showAlarmModeDialog by viewModel.showAlarmModeDialog.collectAsStateWithLifecycle()

    // Dialog들 이벤트 연결
    LanguageSelectionDialog(
        isVisible = showLanguageDialog,
        onDismiss = { viewModel.hideLanguageDialog() },
        onLanguageSelect = { language ->
            viewModel.setLanguage(language)
            viewModel.hideLanguageDialog()
        }
    )

    UnitSelectionDialog(
        isVisible = showUnitDialog,
        onDismiss = { viewModel.hideUnitDialog() },
        onUnitSelect = { unit ->
            viewModel.setUnit(unit)
            viewModel.hideUnitDialog()
        }
    )
}
```

#### **CupManagementScreen 이벤트 연결**
```kotlin
@Composable
fun CupManagementScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCupCreation: () -> Unit,
    viewModel: CupViewModel = hiltViewModel()
) {
    // 컵 추가 버튼 클릭 → CupCreationScreen 이동
    Button(
        onClick = onNavigateToCupCreation
    ) { Text("컵 추가") }

    // 컵 선택 이벤트
    LazyColumn {
        items(cupList) { cup ->
            CupCard(
                cup = cup,
                onCupSelect = { selectedCup ->
                    viewModel.selectCup(selectedCup)
                    onNavigateBack()
                }
            )
        }
    }
}
```

#### **AlarmModeScreen 이벤트 연결**
```kotlin
@Composable
fun AlarmModeScreen(
    viewModel: WaterAlarmViewModel = hiltViewModel()
) {
    // 정확한 알람 권한 요청 → ExactAlarmPermissionDialog
    val showExactAlarmDialog by viewModel.showExactAlarmDialog.collectAsStateWithLifecycle()

    ExactAlarmPermissionDialog(
        isVisible = showExactAlarmDialog,
        onDismiss = { viewModel.hideExactAlarmDialog() },
        onConfirm = {
            viewModel.requestExactAlarmPermission()
            viewModel.hideExactAlarmDialog()
        }
    )
}
```

### **🎯 ViewModel 상태 확장 필요**

#### **각 ViewModel에 추가할 Dialog 상태들**
```kotlin
// WaterViewModel 확장
class WaterViewModel : BaseViewModel() {
    private val _showWaterIntakeDialog = MutableStateFlow(false)
    val showWaterIntakeDialog = _showWaterIntakeDialog.asStateFlow()

    fun showWaterIntakeDialog() { _showWaterIntakeDialog.value = true }
    fun hideWaterIntakeDialog() { _showWaterIntakeDialog.value = false }
}

// SettingViewModel 확장
class SettingViewModel : BaseViewModel() {
    private val _showLanguageDialog = MutableStateFlow(false)
    val showLanguageDialog = _showLanguageDialog.asStateFlow()

    private val _showUnitDialog = MutableStateFlow(false)
    val showUnitDialog = _showUnitDialog.asStateFlow()

    private val _showAlarmModeDialog = MutableStateFlow(false)
    val showAlarmModeDialog = _showAlarmModeDialog.asStateFlow()
}
```

#### **5단계: Core UI 컴포넌트 정리**
**ViewBinding 사용 중인 컴포넌트들**:
- `ExpandableTextView`, `TextSwitchView`, `CustomTimePicker` 등
- 선택적으로 Compose 전환 또는 사용하지 않는 경우 제거

#### **6단계: 최종 정리 및 검증**
- [ ] Fragment 관련 import 문 정리
- [ ] Navigation XML 파일 정리
- [ ] ViewBinding/DataBinding 의존성 제거
- [ ] 빌드 최적화 (미사용 리소스 제거)
- [ ] 전체 빌드 및 테스트 검증

### **🎉 완료 목표**
**최종 달성 상태**:
- ✅ **100% Compose**: 모든 UI가 Jetpack Compose로 구현
- ✅ **Fragment 제거**: Fragment/XML 완전 제거
- ✅ **Single Activity**: WaterActivity만 Compose 기반으로 존재
- ✅ **Navigation**: Compose Navigation 완전 전환
- ✅ **현대적 아키텍처**: MVVM + Compose + StateFlow

### **📊 진행률**
- **UI 마이그레이션**: 95% 완료 (WaterActivity 남음)
- **ViewBinding 제거**: 10% 완료 (대부분 남음)
- **XML 정리**: 85% 완료 (Setting XML 남음)
- **Dialog 복원**: 0% 완료 (10개 Dialog 복원 필요)
- **Navigation 전환**: 0% 완료 (Fragment → Compose Navigation)
- **ViewModel 연결**: 20% 완료 (Dialog 상태 추가 필요)
- **전체 진행률**: 70% 완료

### **📝 핵심 작업 요약**

#### **🎯 3대 핵심 작업**
1. **Navigation 화면 이동 로직 적용**:
   - 온보딩 플로우: LanguageSelectionScreen → TimeSettingScreen → IntakeGoalScreen
   - 메인 앱 플로우: Bottom Navigation (Home, Log, Setting)
   - 화면 이동: HomeScreen → CupManagementScreen → CupCreationScreen

2. **삭제된 Dialog들 Compose 변환**:
   - 필수: WaterIntakeDialog, LanguageSelectionDialog, UnitSelectionDialog
   - 중요: AlarmModeBottomSheet, AlarmRingtoneDialog, ExactAlarmPermissionDialog
   - 선택적: AlarmPeriodDialog, CustomAlarmBottomSheet, LogEditBottomSheet

3. **ViewModel 이벤트 연결**:
   - 각 화면의 클릭 이벤트 → 해당 ViewModel 이벤트 연결
   - Dialog 상태 관리 (show/hide) StateFlow 추가
   - 기존 LiveData와 호환성 유지

---

## 🎯 기존 정리 작업 (낮은 우선순위)

### **Fragment/XML 정리 작업**
위의 핵심 작업 완료 후 진행:

## 📊 **최종 마이그레이션 통계**

| 구분 | 이전 (Fragment) | 이후 (Compose) | 개선 효과 |
|------|-----------------|----------------|-----------|
| **UI 기술** | XML + Fragment | Jetpack Compose | 선언적 UI |
| **Navigation** | XML Navigation | Compose Navigation | 타입 안전성 |
| **상태 관리** | LiveData | StateFlow | 반응형 프로그래밍 |
| **Preview** | 없음 | @Preview 함수 | 실시간 미리보기 |
| **코드량** | ~35 XML 파일 | Compose Screen | 코드 통합 |
| **유지보수성** | 분산된 파일 | 단일 Screen | 개선됨 |