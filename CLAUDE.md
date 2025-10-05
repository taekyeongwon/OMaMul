# CLAUDE.md

이 파일은 이 저장소에서 작업할 때 Claude Code (claude.ai/code)에게 지침을 제공합니다.

**중요한 규칙: 모든 답변과 의사소통은 한국어로 작성해야 합니다.**

---

## 📊 전체 프로젝트 진행 상황 요약

### 현재 단계: Phase 2 완료 ✅

**Phase 1: XML + Fragment 디자인 변경 (물 앱 컨셉 적용)** ✅ 완료
- ✅ 1. feature-water:home - 메인 화면 개선
- ✅ 1.5. WaterActivity - 메인 액티비티 UI 재디자인
- ✅ 2. core:ui - 공통 UI 컴포넌트
- ✅ 3. feature-water:cup - 컵 관리 화면
- ✅ 4. feature-water:record - 로그 및 차트 화면
- ✅ 5. feature-water:alarm - 알람 설정 화면
- ✅ 6. feature-water:setting - 물 관련 설정
- ✅ 7. feature-common:init - 온보딩 화면
- ⏸️ 8. feature-common:setting - 앱 설정 (보류)

**진행률: 7/7 완료 (feature-common:setting 제외)**

**Phase 2: LiveData → StateFlow 마이그레이션** ✅ 완료
- ✅ ViewModel 클래스별 LiveData → StateFlow 변환 완료
- ✅ Fragment에서 observe → collect 방식으로 변경 완료
- ✅ 변수명 리팩토링 (livedata → flow) 완료
- ✅ feature-water 모듈 전체 마이그레이션 완료
- ✅ feature-common 모듈 전체 마이그레이션 완료

**주요 완료 커밋:**
- `6cffb96` Phase 2: LiveData → StateFlow 마이그레이션 완료
- `3accf6b` Fragment observe → Flow collect 마이그레이션 완료
- `8671f48` feature-water:setting LiveData → Flow 마이그레이션 완료
- `1858a19` refactor: LiveData 변수명을 Flow 네이밍으로 변경

**Phase 2.5: KAPT → KSP 마이그레이션** ✅ 완료
- ✅ libs.versions.toml에 KSP 플러그인 추가 (v2.0.20-1.0.25)
- ✅ 루트 build.gradle에 KSP 플러그인 선언
- ✅ 14개 모듈 Hilt compiler kapt → ksp 변환
- ✅ DataBinding 모듈에 kotlin-kapt 병행 사용 (9개 모듈)
- ✅ 전체 빌드 테스트 성공

**빌드 성능 개선 효과:**
- Hilt annotation processing: KAPT → KSP (2-4배 빠름)
- DataBinding: KAPT 유지 (KSP 미지원)
- 증분 빌드 성능 향상

**Phase 3: Compose UI 마이그레이션** (대기 중)
- 🔜 Navigation 구조 변경
- 🔜 Activity 구조 단순화
- 🔜 모듈별 Composable 구현
- 🔜 공통 Composable 구현

---

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
- **작업 완료 후 문서 업데이트**: 모듈 또는 주요 작업이 완료되면 반드시 이 CLAUDE.md 파일의 진행 상황을 업데이트해야 함. 완료된 작업은 체크 표시(✅)로 표시하고, 진행률 카운터를 업데이트하며, 주요 변경사항을 기록
- **ViewModel 인터페이스 보존**: 각 모듈별로 사용하고 있던 ViewModel의 모든 필드와 메서드는 반드시 동일하게 사용해야 함. 프레젠테이션 레이어 변경 시에도 기존 ViewModel의 public 인터페이스는 변경 금지
- **다국어 지원 규칙**:
  - **필수**: UI에 표시되는 모든 문구는 반드시 string 리소스로 관리해야 함
  - **레이아웃 작성 시**: `@string/xxx` 참조를 추가할 때는 반드시 해당 string이 존재하는지 확인
  - **string 리소스 추가 프로세스**:
    1. 레이아웃에 `android:text="@string/새로운_문구"` 작성
    2. 즉시 `core:ui/src/main/res/values/strings.xml` 확인
    3. 없으면 **반드시 5개 언어 모두에 추가** (values, values-ko-rKR, values-en-rUS, values-ja, values-zh-rCN)
    4. 추가 후 다음 작업 진행
  - **검증**: 레이아웃 작성 완료 후 누락된 string이 없는지 반드시 확인
- **그림자 효과 규칙**:
  - **필수**: `elevation` 또는 `translationZ`를 사용하여 그림자 효과를 적용할 때는 해당 뷰의 **상위 레이아웃에 반드시 `android:clipToPadding="false"` 속성을 설정**해야 함
  - 그림자가 패딩 영역을 넘어서 자연스럽게 표시되도록 보장
  - `cardElevation`, `translationZ`, `elevation` 속성을 조합하여 계층적 깊이감 표현
  - 패딩과 마진을 적절히 조합하여 그림자가 잘리지 않는 레이아웃 구성
  - **예시**: 버튼에 `elevation="4dp"`를 적용할 때 상위 Layout에 `android:clipToPadding="false"` 필수
- **아이콘 디자인 규칙**:
  - **필수**: 아이콘은 항상 **목적에 맞게 선택하거나 생성**해야 함
  - 기존 아이콘이 버튼의 기능이나 목적에 맞지 않으면 새로운 아이콘을 생성
  - 물 앱 컨셉에 맞는 아이콘 디자인 (물방울, 블루 계열 색상 활용)
  - Material Icons Extended 우선 사용, 없으면 커스텀 Vector Drawable 생성
  - **예시**: 삭제 버튼에는 휴지통 아이콘, 추가 버튼에는 플러스 아이콘 사용
- **Drawable 리소스 규칙**:
  - **필수**: XML 레이아웃에서 참조하는 모든 drawable은 **반드시 존재해야 함**
  - 레이아웃 작성 시 drawable이 없으면 즉시 생성
  - 공통 drawable은 `core:ui` 모듈에 생성
  - 모듈별 drawable은 해당 모듈의 `res/drawable` 폴더에 생성
  - **주요 공통 drawable**:
    - `gradient_background.xml`: 그라데이션 배경
    - `step_indicator_active.xml`: 활성 단계 표시 (40dp 원형, water_blue)
    - `step_indicator_inactive.xml`: 비활성 단계 표시 (40dp 원형, 회색)
    - `ripple_effect.xml`: 터치 효과 (물결 효과)
    - `radio_selector.xml`: 라디오 버튼 선택자
  - 없는 drawable을 참조하면 빌드 에러가 발생하므로 반드시 사전 생성 필요
- **Color 리소스 규칙**:
  - **필수**: XML 레이아웃에서 참조하는 모든 색상은 **반드시 colors.xml에 정의되어야 함**
  - 레이아웃 작성 시 색상이 없으면 즉시 `core:ui/src/main/res/values/colors.xml`에 추가
  - 하드코딩된 색상 값(예: #FFFFFF) 사용 금지, 반드시 color 리소스로 관리
  - **주요 공통 색상**:
    - `water_blue` (#4A90E2): 메인 블루 색상
    - `water_blue_variant` (#2196F3): 블루 변형 색상
    - `water_light_blue` (#E3F2FD): 라이트 블루
    - `glass_surface` (#E6FFFFFF): 글래스모피즘 표면 (90% 불투명도)
    - `text_primary` (#1B1B1F): 주요 텍스트 색상
    - `text_secondary` (#6C757D): 보조 텍스트 색상
    - `divider_light` (#E0E0E0): 구분선 색상
    - `white` (#FFFFFFFF): 흰색
  - 그라데이션 색상: `gradient_start`, `gradient_middle`, `gradient_end`
  - 없는 색상을 참조하면 빌드 에러가 발생하므로 반드시 사전 정의 필요

### 다국어 번역 규칙

#### 문구 추가 프로세스
새로운 UI 문구가 추가될 때는 다음 절차를 따라야 합니다:

1. **core:ui 모듈의 strings.xml 파일들에 번역 추가**
   - `values/strings.xml` (기본, 한국어)
   - `values-en-rUS/strings.xml` (영어)
   - `values-ja/strings.xml` (일본어)
   - `values-ko-rKR/strings.xml` (한국어)
   - `values-zh-rCN/strings.xml` (중국어 간체)

2. **번역 품질 기준**
   - 각 언어의 자연스러운 표현 사용
   - 문화적 맥락을 고려한 번역
   - 일관된 톤앤매너 유지
   - 기술 용어의 정확한 번역

3. **string 리소스 명명 규칙**
   - 기능별 접두사 사용 (예: `water_`, `alarm_`, `setting_`)
   - 명확하고 직관적인 이름
   - 스네이크 케이스 사용
   - 포맷 문자열의 경우 매개변수 순서와 타입 명시

#### 언어별 번역 가이드라인

**한국어 (values/, values-ko-rKR/)**
- 높임말 사용 지양, 친근한 반말 톤
- "~해요", "~에요" 형태의 정중한 표현
- 이모티콘 활용으로 친근감 표현

**영어 (values-en-rUS/)**
- 간결하고 명확한 표현
- 능동태 우선 사용
- 사용자 친화적인 톤

**일본어 (values-ja/)**
- 정중한 경어 사용
- 자연스러운 일본어 어순
- 카타카나 외래어 적절히 활용

**중국어 간체 (values-zh-rCN/)**
- 간체 문자 사용
- 대륙 중국어 표현 기준
- 간결하고 이해하기 쉬운 표현

#### 예시: 물 관련 문구
```xml
<!-- 한국어 -->
<string name="water_goal_achieved">목표 달성! 잘 하셨어요! 🎉</string>
<string name="water_goal_close">목표까지 %dml 남았어요! 거의 다 왔어요! 🔥</string>

<!-- 영어 -->
<string name="water_goal_achieved">Goal achieved! Well done! 🎉</string>
<string name="water_goal_close">Only %dml left to goal! Almost there! 🔥</string>

<!-- 일본어 -->
<string name="water_goal_achieved">目標達成！お疲れさまでした！ 🎉</string>
<string name="water_goal_close">目標まで%dml残っています！もうすぐです！ 🔥</string>

<!-- 중국어 -->
<string name="water_goal_achieved">目标达成！做得好！ 🎉</string>
<string name="water_goal_close">距离目标还有%dml！快达成了！ 🔥</string>
```

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

## 디자인 변경 및 마이그레이션 계획

### Phase 1: XML + Fragment 디자인 변경 (물 앱 컨셉 적용)

#### 작업 목표
- 기존 XML 레이아웃을 물 앱 컨셉에 맞게 디자인 변경
- 프레젠테이션 로직, 비즈니스 로직은 유지
- ViewModel의 모든 필드 및 메서드 참조 유지

#### 디자인 컨셉 요소
- **색상 팔레트**:
  - Primary: #4A90E2 (블루)
  - Primary Variant: #2196F3
  - Secondary: #E3F2FD (라이트 블루)
  - Surface: #FFFFFF with 90% opacity (글래스모피즘)
- **효과**: 글래스모피즘 카드, 그라데이션 배경
- **애니메이션**: 물 웨이브, 부드러운 전환, 물 채우기 효과
- **아이콘**: Material Icons Extended 사용, 물방울/컵 테마
- **레이아웃**: 라운드 코너(16dp), 카드 기반, 적절한 여백

#### 모듈별 작업 우선순위

**✅ 1. feature-water:home** - 메인 화면 개선 (완료)
- FAB 버튼 가시성 향상
- 롱클릭 되돌리기 버튼 아이콘을 목적에 맞게 수정
- 중앙 애니메이션 영역 외곽선을 컵 모양으로 변경하여 담겨있는 느낌 제공
- 애니메이션 영역 하단 텍스트가 두줄이 되어도 애니메이션 영역이 줄어들지 않도록 두줄 기준으로 높이 고정
- 다국어 텍스트가 두줄을 넘어가지 않도록 관리

**✅ 1.5. WaterActivity** - 메인 액티비티 UI 재디자인 (완료)
- Bottom Navigation 영역을 앱 컨셉에 맞게 재디자인
- ActionBar 영역 재디자인
- 우측 상단 더보기 버튼 제거
- 홈 모듈의 목표 영역 클릭 시 더보기 버튼에서 호출하던 팝업 연결
- 홈 모듈의 다음 알람 영역 클릭 시 더보기 버튼에서 호출하던 화면 이동 연결

**✅ 2. core:ui** - 공통 UI 컴포넌트, 다른 모듈에서 재사용 (완료)
**✅ 3. feature-water:cup** - 컵 관리 화면 (완료)
**✅ 4. feature-water:record** - 로그 및 차트 화면 (완료)
**✅ 5. feature-water:alarm** - 알람 설정 화면 (완료)
- 알람 설정 화면 물 앱 컨셉 적용 (MaterialCardView, 글래스모피즘)
- 알람 모드 관련 화면 디자인 개선 (fragment_alarm_mode.xml, fragment_alarm_mode_period.xml, fragment_alarm_mode_custom.xml)
- AlarmActivity 기상 알람 스타일 UI (드래그 투 디스미스, 70% 임계값)
- item_alarm.xml, item_alarm_edit.xml MaterialCardView 적용
- core:alarmnoti에 core:ui 의존성 추가
- 다국어 지원 (한국어, 영어, 일본어, 중국어)

**진행률: 6/8 완료**

**✅ 6. feature-water:setting** - 물 관련 설정 (완료)
- fragment_setting.xml 그라데이션 배경 및 섹션 스타일 통일
- setting_info.xml MaterialCardView 적용, 계정 정보 및 통계 디자인 개선
- setting_water.xml MaterialCardView 적용, 구분선 추가, right_arrow 아이콘 적용
- setting_alarm.xml MaterialCardView 적용, 알람 설정 세부 항목 디자인 개선
- setting_etc.xml MaterialCardView 적용, 언어 설정 디자인 개선
- dialog_language.xml 터치 영역 확보 및 텍스트 스타일 개선

**진행률: 7/8 완료**

**✅ 7. feature-common:init** - 온보딩 화면 (완료)
- fragment_init_language.xml 언어 선택 화면 물 앱 컨셉 적용
- fragment_init_time.xml 시간 설정 화면 물 앱 컨셉 적용
- fragment_init_intake.xml 섭취량 설정 화면 물 앱 컨셉 적용
- Progress Indicator 개선 (40dp 원형, active/inactive 상태, elevation)
- MaterialCardView 및 글래스모피즘 효과 적용
- 그라데이션 배경 및 MaterialButton 적용

**진행률: 7/7 완료 (Phase 1 완료, feature-common:setting 보류)**

**⏸️ 8. feature-common:setting** - 앱 설정 (보류)
- 추후 작업 지시 전까지 보류

**Phase 1 완료! 다음은 Phase 2: LiveData → StateFlow 마이그레이션 진행**

#### 각 모듈별 작업 프로세스
```bash
# 1. 모듈 디자인 변경
# 2. 모듈 빌드 테스트
./gradlew :feature-water:home:assembleDebug

# 3. 전체 빌드 테스트
./gradlew assembleDevDebug

# 4. CLAUDE.md 진행 상황 업데이트 (작업 완료 후 반드시 수행)
#    - 완료된 모듈 체크 표시(✅) 업데이트
#    - 진행률 카운터 업데이트
#    - 주요 변경사항 기록

# 5. 깃 커밋 (진행 상황 업데이트 후 반드시 수행)
git add .
git commit -m "$(cat <<'EOF'
feature-water:home 물 앱 컨셉 적용

## 주요 변경사항
- 작업 내용 요약
- 디자인 개선 사항
- 기능 추가/수정 사항

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
EOF
)"
```

#### Git 커밋 가이드라인
각 모듈 작업 완료 시 반드시 다음 순서를 따릅니다:

**작업 순서:**
1. 모듈 작업 완료
2. 빌드 테스트 성공 확인
3. **CLAUDE.md 진행 상황 업데이트** (✅ 표시, 진행률, 변경사항)
4. **Git 커밋 수행**
5. 다음 모듈 작업 시작

**커밋 메시지 형식:**
- 제목: 모듈명과 주요 작업 내용
- 본문: 상세 변경사항을 카테고리별로 정리
- 푸터: Claude Code 생성 표시

### Phase 2: LiveData → StateFlow 마이그레이션

#### 작업 목표
- Compose 마이그레이션 준비를 위한 반응형 프로그래밍 업데이트
- LiveData → StateFlow 변환
- SingleLiveEvent → SharedFlow 변환
- 변수명 리팩토링 (livedata → flow)

#### 변환 규칙
- **LiveData → StateFlow**: 초기값 없는 경우 null로 초기화
- **SingleLiveEvent → SharedFlow**: replay=0, extraBufferCapacity=1
- **MutableLiveData → MutableStateFlow**
- **변수명 변경**: `xxxLiveData` → `xxxFlow`

#### 작업 순서
1. ViewModel 클래스별 변환
2. Fragment에서 collect 방식으로 변경
3. DataBinding에서 Flow 처리 확인
4. 단위 테스트 업데이트

### Phase 3: Compose UI 마이그레이션

#### 작업 목표
- 전체 UI를 Jetpack Compose로 마이그레이션
- Fragment, XML, ViewBinding, DataBinding 제거
- MPAndroidChart는 AndroidView로 래핑하여 사용

#### Kotlin 2.0 Compose 설정
```gradle
plugins {
    alias(libs.plugins.kotlin.compose) // Kotlin 2.0.0+ 필수
}

android {
    buildFeatures {
        compose = true
        viewBinding = false
        dataBinding = false
    }

    buildTypes {
        release {
            minifyEnabled = true // material-icons-extended 최적화
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    // Compose BOM으로 버전 통합 관리
    implementation platform(libs.androidx.compose.bom)
    implementation libs.bundles.compose
    debugImplementation libs.androidx.compose.ui.tooling
    debugImplementation libs.androidx.compose.ui.test.manifest
}
```

#### Compose 마이그레이션 순서
1. **Navigation 구조 변경**: Fragment 기반 → Composable 기반
2. **Activity 구조 단순화**: WaterActivity → Compose 전용
3. **모듈별 Composable 구현**:
   - HomeScreen (기존 WaterFragment)
   - CupManageScreen (기존 CupManageFragment)
   - WaterLogScreen (기존 WaterLogFragment)
   - AlarmScreen (기존 WaterAlarmFragment)
   - SettingScreen (기존 WaterSettingFragment)
4. **공통 Composable 구현**: core:ui 모듈의 커스텀 컴포넌트들
5. **MPAndroidChart 래핑**: AndroidView로 기존 차트 시스템 재사용

#### Compose 디자인 시스템
```kotlin
// Color.kt
val WaterBlue = Color(0xFF4A90E2)
val WaterBlueVariant = Color(0xFF2196F3)
val WaterLightBlue = Color(0xFFE3F2FD)
val GlassSurface = Color(0xE6FFFFFF) // 90% 불투명도

// Theme.kt
val WaterAppColorScheme = lightColorScheme(
    primary = WaterBlue,
    primaryContainer = WaterBlueVariant,
    secondary = WaterLightBlue,
    surface = GlassSurface
)
```

### 주의사항
- **ViewModel 인터페이스 유지**: 모든 public 메서드와 프로퍼티는 반드시 유지
- **비즈니스 로직 보존**: Repository, UseCase, Domain 레이어는 변경 금지
- **점진적 마이그레이션**: 모듈별로 단계적 진행, 빌드 안정성 확보
- **테스트 커버리지**: 각 단계별 단위 테스트 및 UI 테스트 검증