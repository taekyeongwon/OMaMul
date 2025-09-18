# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# OMaMul (오늘 마실 물)

## 1. 프로젝트 개요

'OMaMul'은 사용자의 건강한 물 마시기 습관을 돕는 안드로이드 애플리케이션입니다. 일일 목표를 설정하고, 섭취량을 기록하며, 규칙적인 섭취를 위한 알림을 제공하여 사용자가 꾸준히 수분을 보충할 수 있도록 지원합니다.

## 2. 주요 기능

*   **일일 물 섭취량 기록**: 사용자가 마신 물의 양을 간편하게 기록합니다.
*   **목표 설정**: 개인의 필요에 맞는 일일 물 섭취 목표를 설정할 수 있습니다.
*   **섭취 통계 확인**: 일별, 주별, 월별 물 섭취 통계를 시각적으로 확인합니다.
*   **알림 기능**: 사용자가 설정한 시간에 물 마시기를 잊지 않도록 알림을 보냅니다.
*   **컵 사이즈 설정**: 자주 사용하는 컵의 용량을 미리 설정하여 편리하게 기록할 수 있습니다.

## 3. 기술 스택 및 아키텍처

본 프로젝트는 유지보수성과 확장성을 높이기 위해 다음과 같은 현대적인 안드로이드 아키텍처와 기술 스택을 채택했습니다.

*   **언어**: [Kotlin](https://kotlinlang.org/)
*   **아키텍처**:
    *   **클린 아키텍처 (Clean Architecture)**: UI, 비즈니스 로직, 데이터 계층을 명확하게 분리하여 의존성을 낮추고 테스트 용이성을 높입니다.
    *   **멀티 모듈 (Multi-module)**: 기능별로 코드를 모듈화하여 빌드 속도를 개선하고 코드의 재사용성을 극대화합니다.
    *   **Single Activity Architecture + Compose Navigation**: 모든 화면이 Composable로 구성되어 Compose Navigation으로 관리됩니다.
*   **UI**: Jetpack Compose (Material 3 디자인 시스템)
*   **비동기 처리**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html), Flow, LiveData
*   **의존성 주입**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
*   **데이터 저장**: [Realm](https.mongodb.com/docs/realm/sdk/kotlin/), [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
*   **네비게이션**: [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)

## 4. 개발 환경 및 빌드 명령어

### 환경 설정
- **Android Studio**: Android Studio Narwhal Feature Drop | 2025.1.2
- **Compile SDK**: 34
- **Min SDK**: 26
- **Target SDK**: 34
- **Gradle JDK**: jbr-17
- **Gradle Version**: 8.6
- **AGP Version**: 8.4.2
- **Kotlin**: 2.0.20
- **Compose BOM**: 최신 안정 버전

### 빌드 명령어
```bash
# 프로젝트 빌드 (dev 빌드)
./gradlew assembleDebug

# 프로젝트 빌드 (prod 빌드)
./gradlew assembleProdDebug

# 테스트 실행
./gradlew test

# 특정 모듈 테스트
./gradlew :app:test

# lint 검사
./gradlew lint

# 모든 모듈 빌드
./gradlew build

# 클린 빌드
./gradlew clean build
```

### 프로젝트 빌드 종류
프로젝트는 두 가지 Product Flavor를 제공합니다:
- **prod**: 프로덕션 버전 (applicationId: com.tkw.omamul)
- **dev**: 개발 버전 (applicationId: com.tkw.omamul.dev)

## 5. 모듈 구조

프로젝트는 기능별로 다음과 같이 모듈화되어 있습니다.

### Core 모듈 (공통 기능)
*   `core:common`: 공통 유틸리티, 확장 함수 등
*   `core:data`: 데이터 소스(Repository) 구현체
*   `core:database`: Realm DB 관련 코드
*   `core:datastore`: DataStore 관련 코드
*   `core:domain`: UseCase, Entity 등 비즈니스 로직
*   `core:navigation`: 화면 이동(Navigation) 관련 로직
*   `core:ui`: 공통 UI 컴포넌트 (테마, 스타일 등)
*   `core:alarmnoti`: 알림 및 푸시 기능
*   `core:firebase`: Firebase 관련 기능

### Feature 모듈 (기능별)
*   `feature-common:base`: 기능 모듈의 기반이 되는 클래스
*   `feature-common:init`: 앱 초기 설정 관련 기능
*   `feature-common:setting`: 공통 설정 화면(언어 설정, 광고 제거 등)
*   `feature-water:home`: 메인 홈 화면
*   `feature-water:record`: 섭취량 기록 화면
*   `feature-water:alarm`: 알림 설정 화면
*   `feature-water:cup`: 컵 관리 화면
*   `feature-water:setting`: 물 마시기 관련 설정 화면

### App 모듈
*   `app`: 최종 애플리케이션을 구성하고 모든 모듈을 통합하는 메인 모듈

## 6. 주요 기술 스택 세부사항

### 데이터바인딩 및 뷰바인딩
- DataBinding과 ViewBinding이 모두 활성화되어 있습니다
- 양방향 데이터바인딩이 일부 화면에 적용되어 있습니다

### 의존성 주입 (Hilt)
- 모든 모듈에서 Hilt를 사용하여 의존성 주입을 관리합니다
- KAPT를 사용하여 Hilt 어노테이션을 처리합니다

### 네트워킹
- Retrofit을 사용하여 HTTP 통신을 처리합니다
- OkHttp가 기본 HTTP 클라이언트로 사용됩니다

### 인증
- Google Play Services Auth를 사용하여 사용자 인증을 처리합니다

### 백그라운드 작업
- WorkManager를 사용하여 백그라운드 작업을 관리합니다

## 7. 코딩 컨벤션

### 응답 관련
- 모든 질문에 대한 답변은 한글로 제공해야 합니다
- 간결한 답변을 제공하며, 핵심 내용만 포함하여 최대 3-5문장으로 답변합니다

### 파일 생성 원칙
- 기존 파일을 편집하는 것을 새 파일 생성보다 선호합니다
- 명시적으로 요청되지 않는 한 문서 파일(*.md)이나 README 파일을 미리 생성하지 않습니다

## 8. Compose 마이그레이션 계획 (Fragment+XML → Compose+Navigation)

### 8.1 현재 구조 분석

#### Feature-Water 모듈 마이그레이션 완료 상태:

**Home 모듈 (feature-water:home)**
- ✅ **Compose 완료**: `HomeScreen.kt` - 메인 물 섭취 화면

**Cup 모듈 (feature-water:cup)**
- ✅ **Compose 완료**: `CupManageScreen.kt` - 컵 관리 메인 화면
- ✅ **Compose 완료**: `CupCreateScreen.kt` - 컵 추가/수정 화면

**Record 모듈 (feature-water:record)**
- ✅ **Compose 완료**: `WaterLogScreen.kt` - 기록 메인 (탭 레이아웃)

**Alarm 모듈 (feature-water:alarm)**
- ✅ **Compose 완료**: `WaterAlarmScreen.kt` - 알림 메인 화면

**Setting 모듈 (feature-water:setting)**
- ✅ **Compose 완료**: `WaterSettingScreen.kt` - 물 관련 설정

### 8.2 마이그레이션 완료 상태

#### ✅ 마이그레이션 완료:

**1단계: Navigation 구조 변경**
- ✅ Fragment+XML → Compose+Navigation 완료
- ✅ 각 모듈의 Fragment 파일들 Compose Screen으로 변환
- ✅ ViewModel에 StateFlow 지원 추가

**2단계: 화면별 Compose 변환**
1. ✅ **Home** - `HomeScreen.kt` (완료)
2. ✅ **Cup** - 컵 관리 화면들 (완료)
   - ✅ `CupManageScreen.kt` (컵 관리 메인)
   - ✅ `CupCreateScreen.kt` (컵 추가/수정)
3. ✅ **Record** - 기록 조회 화면들 (완료)
   - ✅ `WaterLogScreen.kt` (탭 기반 메인)
4. ✅ **Alarm** - 알림 설정 화면들 (완료)
   - ✅ `WaterAlarmScreen.kt` (알림 메인)
5. ✅ **Setting** - 설정 화면 (완료)
   - ✅ `WaterSettingScreen.kt` (물 관련 설정)

**3단계: 의존성 정리**
- ✅ Compose 의존성 추가 완료
- ✅ ViewModel StateFlow 지원 완료
- ✅ 커스텀 아이콘 시스템 구현 (`WaterIcons`)

### 8.3 마이그레이션 주요 성과

#### 완료된 주요 기능들:
- ✅ **Modern UI**: Material 3 디자인 시스템 적용
- ✅ **Gradient Background**: 물 테마의 그라데이션 배경
- ✅ **Card-based Layout**: 모든 화면에 일관된 카드 기반 레이아웃
- ✅ **Custom Icons**: `WaterIcons.LocalDrink`, `WaterIcons.Timeline` 구현
- ✅ **Preview System**: 모든 화면과 컴포넌트에 Preview 함수 구현
- ✅ **StateFlow Integration**: ViewModel에서 Compose 친화적 StateFlow 지원
- ✅ **Responsive Design**: 다양한 화면 크기 대응

#### 주요 UI 개선사항:
- **애니메이션**: 진행률 표시기에 부드러운 애니메이션 적용
- **접근성**: 모든 버튼과 아이콘에 contentDescription 추가
- **일관성**: 모든 화면에서 동일한 색상 팔레트와 스타일링 사용
- **사용성**: 직관적인 네비게이션과 명확한 정보 계층 구조

### 8.4 새로운 UI/UX 디자인 사양

#### 디자인 시스템

**색상 팔레트:**
```kotlin
// Primary Colors (물 테마)
val Primary = Color(0xFF2196F3)      // 밝은 파란색
val PrimaryVariant = Color(0xFF1976D2) // 진한 파란색
val Secondary = Color(0xFF4CAF50)    // 성공/완료 녹색

// Background Gradients
val BackgroundStart = Color(0xFF87CEEB)  // 하늘색
val BackgroundEnd = Color(0xFFE0F6FF)    // 연한 파란색

// Surface Colors
val SurfaceLight = Color(0xFFF3F9FF)     // 연한 파란 배경
val SurfaceWhite = Color.White
val SurfaceGray = Color(0xFFF5F5F5)

// Text Colors  
val TextPrimary = Color(0xFF212121)
val TextSecondary = Color(0xFF757575)
```

**컴포넌트 스타일:**
- **Card 디자인**: 둥근 모서리 (16-24dp), 그림자 효과
- **버튼**: Material 3 스타일, 둥근 모서리
- **Progress Indicator**: 원형 진행률 표시기 (물방울 테마)
- **Typography**: 한글 최적화 폰트

#### 화면별 디자인 사양

**1. HomeScreen (메인 화면)**
```
┌─────────────────────────────────┐
│ Header (날짜, 인사말, 설정)        │
│ ┌─────────────────────────────┐ │
│ │   원형 진행률 표시기          │ │
│ │   (현재 섭취량/목표량)        │ │
│ │   애니메이션 효과             │ │
│ └─────────────────────────────┘ │
│ ┌──────────┐ ┌──────────┐      │
│ │ 기록보기  │ │ 알림설정  │      │
│ └──────────┘ └──────────┘      │
│ ┌─────────────────────────────┐ │
│ │ 컵 선택 (가로 스크롤)         │ │
│ │ [250ml] [500ml] [+추가]     │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ 오늘의 요약                  │ │
│ │ 횟수│총량│마지막섭취          │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
```

**2. CupScreen (컵 관리)**
```
┌─────────────────────────────────┐
│ [← 뒤로] 컵 관리          [편집] │
│ ┌─────────────────────────────┐ │
│ │ 내 컵 목록                   │ │
│ │ ┌─────┐ ┌─────┐ ┌─────┐    │ │
│ │ │250ml│ │500ml│ │750ml│    │ │
│ │ └─────┘ └─────┘ └─────┘    │ │
│ │                             │ │
│ │ [+ 새 컵 추가]               │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ 추천 컵 사이즈               │ │
│ │ • 물병 (500ml)              │ │
│ │ • 머그컵 (250ml)            │ │
│ │ • 텀블러 (400ml)            │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
```

**3. RecordScreen (기록 조회)**
```
┌─────────────────────────────────┐
│ [← 뒤로] 물 마시기 기록           │
│ ┌─────┬─────┬─────────────────┐ │
│ │ 일별 │ 주별 │ 월별              │ │
│ └─────┴─────┴─────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ 📊 차트 영역                 │ │
│ │   (선택된 기간별 그래프)      │ │
│ │                             │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ 📋 상세 기록 리스트          │ │
│ │ 12:30 - 250ml (물병)        │ │
│ │ 15:45 - 500ml (텀블러)      │ │
│ │ 18:20 - 250ml (컵)          │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
```

**4. AlarmScreen (알림 설정)**
```
┌─────────────────────────────────┐
│ [← 뒤로] 알림 설정               │
│ ┌─────────────────────────────┐ │
│ │ 🔔 알림 활성화     [ON/OFF] │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ ⏰ 알림 시간 설정            │ │
│ │ 09:00  12:00  15:00  18:00 │ │
│ │ [+ 시간 추가]               │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ 🎵 알림음 설정               │ │
│ │ • 기본 알림음               │ │
│ │ • 물방울 소리               │ │
│ │ • 사용자 지정               │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ 📅 반복 설정                │ │
│ │ 월 화 수 목 금 토 일         │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
```

**5. SettingScreen (설정)**
```
┌─────────────────────────────────┐
│ [← 뒤로] 설정                   │
│ ┌─────────────────────────────┐ │
│ │ 🎯 목표 설정                │ │
│ │ 일일 목표: 2000ml           │ │
│ │ [수정]                      │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ 🌍 언어 설정                │ │
│ │ • 한국어                   │ │
│ │ • English                  │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ ☁️ 백업 & 동기화            │ │
│ │ Google 계정 연동            │ │
│ │ [로그인]                    │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ ℹ️ 앱 정보                  │ │
│ │ 버전 1.0.0                 │ │
│ │ 개발자 정보                │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
```

### 8.4 구현 가이드라인

#### Compose Navigation 구조:
```kotlin
// WaterNavigation.kt
sealed class WaterDestination(val route: String) {
    object Home : WaterDestination("home")
    object Record : WaterDestination("record")
    object Cup : WaterDestination("cup") 
    object Alarm : WaterDestination("alarm")
    object Setting : WaterDestination("setting")
}
```

#### 모듈별 build.gradle 수정사항:
```kotlin
// 제거할 의존성
- implementation libs.androidx.navigation.fragment
- implementation libs.androidx.navigation.ui
- dataBinding = true
- viewBinding = true

// 추가할 의존성  
+ implementation platform(libs.androidx.compose.bom)
+ implementation libs.bundles.compose
+ implementation libs.androidx.hilt.navigation.compose
```

// 추가할 플러그인
+ alias libs.plugins.kotlin.compose

### 8.6 모듈별 상세 마이그레이션 가이드

#### Cup 모듈 마이그레이션
**제거할 파일들:**
- `fragment_cup_manage.xml`
- `fragment_cup_create.xml` 
- `fragment_cup_list_edit.xml`
- `CupManageFragment.kt`
- `CupCreateFragment.kt`
- `CupListEditFragment.kt`
- `cup_nav_graph.xml`

**새로 생성할 파일들:**
- `CupManageScreen.kt`
- `CupCreateScreen.kt` 
- `CupListEditScreen.kt`
- `CupNavigation.kt`

#### Record 모듈 마이그레이션
**제거할 파일들:**
- `fragment_water_log.xml`
- `fragment_log_day.xml`
- `fragment_log_week.xml` 
- `fragment_log_month.xml`
- `WaterLogFragment.kt`
- `LogDayFragment.kt`
- `LogWeekFragment.kt`
- `LogMonthFragment.kt`
- `record_nav_graph.xml`

**새로 생성할 파일들:**
- `WaterLogScreen.kt` (TabRow 포함)
- `LogDayScreen.kt`
- `LogWeekScreen.kt`
- `LogMonthScreen.kt`
- `RecordNavigation.kt`

#### Alarm 모듈 마이그레이션
**제거할 파일들:**
- `fragment_water_alarm.xml`
- `fragment_alarm_mode.xml`
- `fragment_alarm_mode_custom.xml`
- `fragment_alarm_mode_period.xml`
- 모든 다이얼로그 XML 파일들
- 모든 Fragment 클래스들
- `alarm_nav_graph.xml`

**새로 생성할 파일들:**
- `WaterAlarmScreen.kt`
- `AlarmModeScreen.kt`
- `AlarmModeCustomScreen.kt`
- `AlarmModePeriodScreen.kt`
- Compose 다이얼로그들
- `AlarmNavigation.kt`

#### Setting 모듈 마이그레이션
**제거할 파일들:**
- `fragment_setting.xml`
- `WaterSettingFragment.kt`
- 모든 다이얼로그 XML 파일들

**새로 생성할 파일들:**
- `WaterSettingScreen.kt`
- Compose 다이얼로그들
- `SettingNavigation.kt`

### 8.7 중앙 집중식 Navigation 구현

#### WaterNavHost 구조:
```kotlin
// core:navigation/WaterNavHost.kt
@Composable
fun WaterNavHost(
    navController: NavHostController,
    startDestination: String = WaterDestination.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Home
        composable(WaterDestination.Home.route) {
            HomeScreen(
                onNavigateToRecord = {
                    navController.navigate(WaterDestination.Record.route)
                },
                onNavigateToAlarm = {
                    navController.navigate(WaterDestination.Alarm.route)
                },
                onNavigateToCup = {
                    navController.navigate(WaterDestination.Cup.route)
                },
                onNavigateToSetting = {
                    navController.navigate(WaterDestination.Setting.route)
                }
            )
        }
        
        // Cup Management
        composable(WaterDestination.Cup.route) {
            CupManageScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreate = {
                    navController.navigate("${WaterDestination.CupCreate.route}?cupId=null")
                },
                onNavigateToEdit = { cup ->
                    navController.navigate("${WaterDestination.CupCreate.route}?cupId=${cup.id}")
                }
            )
        }
        
        composable(
            route = "${WaterDestination.CupCreate.route}?cupId={cupId}",
            arguments = listOf(
                navArgument("cupId") { 
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val cupId = backStackEntry.arguments?.getString("cupId")
            CupCreateScreen(
                cupId = cupId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Record
        composable(WaterDestination.Record.route) {
            WaterLogScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Alarm
        composable(WaterDestination.Alarm.route) {
            WaterAlarmScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToMode = {
                    navController.navigate(WaterDestination.AlarmMode.route)
                }
            )
        }
        
        composable(WaterDestination.AlarmMode.route) {
            AlarmModeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Setting
        composable(WaterDestination.Setting.route) {
            WaterSettingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
```

#### Navigation Destinations:
```kotlin
// core:navigation/WaterDestination.kt
sealed class WaterDestination(val route: String) {
    object Home : WaterDestination("home")
    object Record : WaterDestination("record")
    object Cup : WaterDestination("cup")
    object CupCreate : WaterDestination("cup_create")
    object CupEdit : WaterDestination("cup_edit")
    object Alarm : WaterDestination("alarm")
    object AlarmMode : WaterDestination("alarm_mode")
    object Setting : WaterDestination("setting")
}
```

### 8.8 각 모듈별 build.gradle 수정 체크리스트

**모든 feature-water 모듈에서 제거해야 할 의존성:**
```kotlin
// 제거
- implementation libs.androidx.navigation.fragment
- implementation libs.androidx.navigation.ui
- implementation libs.androidx.fragment.ktx
- dataBinding = true
- viewBinding = true
```

**모든 feature-water 모듈에서 추가해야 할 의존성:**
```kotlin
// 추가
+ implementation libs.androidx.hilt.navigation.compose
+ implementation platform(libs.androidx.compose.bom)
+ implementation libs.bundles.compose
+ debugImplementation libs.androidx.compose.ui.tooling
+ debugImplementation libs.androidx.compose.ui.test.manifest
```

**모든 feature-water 모듈에서 추가해야 할 플러그인:**
```kotlin
// 추가
+ alias libs.plugins.kotlin.compose
```

### 8.9 마이그레이션 검증 체크리스트

#### 기능 검증:
- [ ] 모든 화면 정상 렌더링
- [ ] 네비게이션 흐름 정상 동작
- [ ] ViewModel과 상태 관리 정상
- [ ] 다이얼로그 정상 동작
- [ ] 백 버튼 및 업 네비게이션 정상
- [ ] Deep Link 정상 동작
- [ ] 화면 회전 대응
- [ ] 다크 테마 지원

#### 성능 검증:
- [ ] 메모리 누수 없음
- [ ] 불필요한 리컴포지션 없음
- [ ] 애니메이션 부드러움
- [ ] 빌드 시간 개선

#### 코드 품질:
- [ ] 모든 Fragment 및 XML 파일 제거
- [ ] 사용하지 않는 의존성 제거
- [ ] Compose 모범 사례 준수
- [ ] 접근성 지원
- [ ] 단위 테스트 업데이트

#### 컴포넌트 재사용성:
- 공통 UI 컴포넌트는 `core:ui` 모듈에 구현
- 테마, 색상, 타이포그래피 중앙 관리
- 애니메이션 효과 표준화

### 8.5 성능 최적화

#### Compose 최적화 사항:
- `remember`와 `mutableStateOf` 적절한 사용
- `LazyColumn`/`LazyRow`로 리스트 최적화  
- `derivedStateOf`로 불필요한 리컴포지션 방지
- `Stable`과 `Immutable` 어노테이션 활용

#### 메모리 관리:
- ViewModel의 적절한 스코프 관리
- 이미지 리소스 최적화
- 불필요한 상태 보유 방지

### 8.10 Compose Preview 가이드라인

#### Preview 작성 규칙:
모든 Compose 화면과 주요 컴포넌트에는 Preview 함수를 작성해야 합니다.

**필수 Import:**
```kotlin
import androidx.compose.ui.tooling.preview.Preview
```

#### Preview 작성 패턴:

**1. 메인 화면 Preview (전체 화면)**
```kotlin
@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun HomeScreenPreview() {
    // ViewModel 없이 실행 가능한 Preview 구현
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF87CEEB),
                        Color(0xFFE0F6FF)
                    )
                )
            )
    ) {
        // 화면 컴포넌트들을 목 데이터로 구성
    }
}
```

**2. 개별 컴포넌트 Preview**
```kotlin
@Preview(showBackground = true)
@Composable
fun WaterProgressCardPreview() {
    WaterProgressCard(currentIntake = 1500, goal = 2000, progress = 0.75f)
}
```

**3. 상태별 Preview (다양한 상태 표시)**
```kotlin
@Preview(showBackground = true)
@Composable
fun CupItemPreview() {
    CupItem(
        cup = Cup(cupId = "1", cupName = "물병", cupAmount = 500),
        isEditMode = false,
        onEditClick = {},
        onDeleteClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun CupItemEditModePreview() {
    CupItem(
        cup = Cup(cupId = "1", cupName = "물병", cupAmount = 500),
        isEditMode = true,
        onEditClick = {},
        onDeleteClick = {}
    )
}
```

**4. 리스트/그리드 컴포넌트 Preview**
```kotlin
@Preview(showBackground = true)
@Composable
fun CupSelectionCardPreview() {
    CupSelectionCard(
        cupList = listOf(
            Cup(cupId = "1", cupName = "물병", cupAmount = 500),
            Cup(cupId = "2", cupName = "머그컵", cupAmount = 250)
        ),
        onAddClick = {},
        onCupClick = {}
    )
}
```

#### Preview 명명 규칙:
- 화면 Preview: `[ScreenName]Preview`
- 컴포넌트 Preview: `[ComponentName]Preview`
- 상태별 Preview: `[ComponentName][State]Preview`

#### Preview 위치:
- 모든 Preview 함수는 파일 맨 하단에 배치
- `// Preview Functions` 주석으로 구분
- 메인 화면 Preview를 첫 번째로 작성
- 개별 컴포넌트 Preview를 그 다음에 작성

#### Preview 데이터:
- 실제 도메인 모델을 사용하여 현실적인 데이터로 구성
- 다양한 케이스를 보여줄 수 있는 의미있는 샘플 데이터 사용
- 빈 상태, 로딩 상태, 에러 상태 등도 Preview로 제공

#### Preview 어노테이션 옵션:
```kotlin
@Preview(
    showBackground = true,                    // 배경 표시
    backgroundColor = 0xFFE0F6FF,            // 배경 색상
    showSystemUi = true,                     // 시스템 UI 표시 (전체 화면용)
    device = Devices.PIXEL_4,                // 특정 기기
    uiMode = Configuration.UI_MODE_NIGHT_YES // 다크 테마
)
```

## 9. 마이그레이션 실행 순서

### 9.1 준비 단계
1. **백업 생성**: 현재 코드 상태 백업
2. **브랜치 생성**: `feature/compose-migration` 브랜치 생성
3. **의존성 업데이트**: 모든 모듈의 build.gradle 업데이트

### 9.2 실행 단계
1. **Cup 모듈** 완전 마이그레이션
2. **Record 모듈** 완전 마이그레이션
3. **Alarm 모듈** 완전 마이그레이션  
4. **Setting 모듈** 완전 마이그레이션
5. **통합 테스트** 및 버그 수정

### 9.3 완료 단계
1. **전체 빌드 검증**
2. **기능 테스트 완료**
3. **성능 테스트 완료**
4. **코드 정리** (미사용 파일 제거)
5. **문서화 업데이트**