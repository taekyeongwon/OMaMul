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

### Compose 디자인 가이드라인

#### 물 앱 컨셉 디자인 요소
- **색상**: 블루 그라데이션 (#4A90E2 → #E3F2FD)
- **효과**: 글래스모피즘, 물 웨이브 애니메이션
- **아이콘**: 물방울, 컵, 웨이브 모션
- **레이아웃**: 카드 기반, 라운드 코너
- **애니메이션**: 부드러운 전환, 물 채우기 효과

#### Material Icons Extended 최적화 설정
```gradle
// build.gradle
implementation 'androidx.compose.material:material-icons-extended'

buildTypes {
    release {
        minifyEnabled true // R8로 사용하지 않는 아이콘 제거
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```