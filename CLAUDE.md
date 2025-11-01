# CLAUDE.md

이 파일은 이 저장소에서 작업할 때 Claude Code (claude.ai/code)에게 지침을 제공합니다.

**중요한 규칙: 모든 답변과 의사소통은 한국어로 작성해야 합니다.**

---

## 📋 문서 구조

이 프로젝트의 문서는 다음과 같이 구성됩니다:
- **CLAUDE.md** (현재 파일) - 핵심 개발 규칙 및 아키텍처
- **docs/PROGRESS.md** - 작업 진행사항 및 Phase 관리
- **docs/DESIGN_GUIDE.md** - Figma 디자인 가이드 및 리소스 규칙

작업 시작 전에는 **docs/PROGRESS.md**를 확인하고, 디자인 작업 시에는 **docs/DESIGN_GUIDE.md**를 참고하세요.

---

## 🏗️ 아키텍처 개요

### 멀티 모듈 구조
이 프로젝트는 **클린 아키텍처**와 **MVVM 패턴**을 따르는 멀티 모듈 구조입니다:

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

### 주요 패턴
- **Single Activity + Navigation**: WaterActivity + Jetpack Navigation
- **Repository 패턴**: Domain 레이어의 인터페이스, Data 레이어의 구현체
- **의존성 주입**: Hilt 사용 (@HiltAndroidApp, @HiltViewModel)
- **반응형 프로그래밍**: StateFlow + SingleLiveEvent

---

## 🔧 기술 스택

**UI 레이어:**
- XML Layouts + ViewBinding/DataBinding (현재)
- MPAndroidChart (커스텀 차트)
- Material Design Components

**데이터 레이어:**
- Realm Kotlin (로컬 데이터베이스)
- DataStore (프리퍼런스)

**백그라운드 처리:**
- WorkManager (예약된 작업)
- AlarmManager (물 마시기 알림)
- Broadcast Receivers (시스템 이벤트)

**주요 라이브러리:**
- Retrofit + OkHttp (네트워크)
- Glide (이미지 로딩)
- Lottie (애니메이션)
- Firebase (Analytics, Auth)

---

## 📜 핵심 개발 규칙

### 1. 빌드 및 동기화
```bash
# 개발 빌드
./gradlew assembleDevDebug

# 프로덕션 빌드
./gradlew assembleProdRelease

# Gradle 파일 수정 후 필수
./gradlew --refresh-dependencies
```

**중요**: Gradle 파일 수정 시 반드시 `--refresh-dependencies` 실행

### 2. 언어 및 답변 스타일
- 모든 코드 주석, 문서, 의사소통은 **한국어**로 작성
- 답변은 간결하게 **최대 3-5문장**으로 작성

### 3. 작업 완료 후 커밋
- 모듈 작업 완료 및 빌드 테스트 성공 시 `/git-commit` skill 사용하여 자동 커밋

### 4. ViewModel 인터페이스 보존
- **절대 규칙**: 각 모듈의 ViewModel은 모든 필드와 메서드를 반드시 동일하게 유지
- 프레젠테이션 레이어 변경 시에도 기존 ViewModel의 public 인터페이스는 변경 금지
- 비즈니스 로직 보존: Repository, UseCase, Domain 레이어는 변경 금지

### 5. 다국어 지원 규칙
- **필수**: UI에 표시되는 모든 문구는 반드시 string 리소스로 관리
- **레이아웃 작성 시**: `@string/xxx` 참조를 추가할 때는 반드시 해당 string이 존재하는지 확인
- **string 리소스 추가 프로세스**:
  1. 레이아웃에 `android:text="@string/새로운_문구"` 작성
  2. 즉시 `core:ui/src/main/res/values/strings.xml` 확인
  3. 없으면 **반드시 5개 언어 모두에 추가** (values, values-ko-rKR, values-en-rUS, values-ja, values-zh-rCN)
  4. 추가 후 다음 작업 진행
- **검증**: 레이아웃 작성 완료 후 누락된 string이 없는지 반드시 확인

#### 언어별 번역 가이드라인
**한국어**: 높임말 지양, 친근한 반말 톤 ("~해요", "~에요")
**영어**: 간결하고 명확한 표현, 능동태 우선
**일본어**: 정중한 경어 사용, 카타카나 외래어 활용
**중국어**: 간체 문자 사용, 간결하고 이해하기 쉬운 표현

### 6. 리소스 관리 규칙

#### Color 리소스
- **필수**: XML 레이아웃에서 참조하는 모든 색상은 **반드시 colors.xml에 정의**
- 하드코딩된 색상 값(예: #FFFFFF) 사용 금지
- 레이아웃 작성 시 색상이 없으면 즉시 `core:ui/src/main/res/values/colors.xml`에 추가

#### Drawable 리소스
- **필수**: XML 레이아웃에서 참조하는 모든 drawable은 **반드시 존재해야 함**
- 레이아웃 작성 시 drawable이 없으면 즉시 생성
- 공통 drawable은 `core:ui` 모듈에 생성
- 모듈별 drawable은 해당 모듈의 `res/drawable` 폴더에 생성

#### 아이콘 디자인
- 아이콘은 항상 **목적에 맞게 선택하거나 생성**
- 물 앱 컨셉에 맞는 아이콘 디자인 (물방울, 블루 계열 색상 활용)
- Material Icons Extended 우선 사용, 없으면 커스텀 Vector Drawable 생성

### 7. 그림자 효과 규칙
- **필수**: `elevation` 또는 `translationZ` 사용 시 상위 레이아웃에 **반드시 `android:clipToPadding="false"` 설정**
- 그림자가 패딩 영역을 넘어서 자연스럽게 표시되도록 보장
- `cardElevation`, `translationZ`, `elevation` 속성을 조합하여 계층적 깊이감 표현

---

## 🎨 물 앱 컨셉 디자인 요소

- **색상**: 블루 그라데이션 (#4A90E2 → #E3F2FD)
- **효과**: 글래스모피즘, 물 웨이브 애니메이션
- **아이콘**: 물방울, 컵, 웨이브 모션
- **레이아웃**: 카드 기반, 라운드 코너(16dp)
- **애니메이션**: 부드러운 전환, 물 채우기 효과

---

## 📁 데이터베이스 스키마

다음 주요 엔티티와 함께 Realm 사용:
- `DayOfWaterEntity` - 일일 물 섭취 기록
- `CupEntity` - 용량이 있는 컵 정의
- `WaterEntity` - 개별 물 섭취 항목
- `AlarmSettingsEntity` - 알람 구성
- `SettingEntity` - 앱 프리퍼런스

---

## 🧪 테스트 구조

각 모듈에는 다음이 포함됩니다:
- `src/test/` - 단위 테스트 (JUnit)
- `src/androidTest/` - 계측 테스트 (Android Test)

```bash
# 단위 테스트 실행
./gradlew test

# 계측 테스트 실행
./gradlew connectedAndroidTest

# 특정 모듈 테스트
./gradlew :core:database:test
```

---

## 🔗 참고 문서

작업 시작 전 반드시 확인:
- **docs/PROGRESS.md** - 현재 작업 진행사항 및 Phase 확인
- **docs/DESIGN_GUIDE.md** - Figma 디자인 가이드 및 리소스 규칙
