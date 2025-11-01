# 📊 프로젝트 진행 상황

이 문서는 OMaMul 프로젝트의 전체 작업 진행사항을 추적합니다.

---

## 현재 단계: Phase 3 - Figma 디자인 적용 🚀

**Figma 디자인 소스:**
- 링크: https://www.figma.com/design/Dgp8drNVKW1c6qEqqXcAvy/Readdy--%EC%BB%A4%EB%AE%A4%EB%8B%88%ED%8B%B0-
- File Key: `Dgp8drNVKW1c6qEqqXcAvy`

---

## Phase 3: Figma Readdy 디자인 기반 XML 레이아웃 재디자인

### 작업 프로세스
1. 사용자가 XML 파일과 Figma node-id 매핑 제공
2. **docs/DESIGN_GUIDE.md** 참고하여 Figma 디자인 분석
3. 공통 리소스 먼저 생성 (colors, drawables, strings)
4. 공통 컴포넌트 생성 (카드 레이아웃, 라디오 버튼 등)
5. XML 레이아웃 정확히 재작성
6. 모듈 단위 빌드 테스트
7. 모듈 작업 완료 후 동작 테스트 (사용자 수동)
8. 정상 동작 확인 후 커밋

### 진행 상황

#### ✅ feature-common:init - 온보딩 화면 (완료)
- ✅ fragment_init_language.xml (node-id=2-2) - 언어 선택 화면
- ✅ fragment_init_time.xml - 시간 설정 화면
- ✅ fragment_init_intake.xml - 섭취량 설정 화면
- ✅ Progress Indicator 개선 (40dp 원형, active/inactive 상태, elevation)
- ✅ MaterialCardView 및 글래스모피즘 효과 적용
- ✅ 그라데이션 배경 및 MaterialButton 적용
- ✅ 다국어 지원 (한국어, 영어, 일본어, 중국어)

**완료 커밋:** `a364bfa - feat: feature-common:init 모듈 Figma 디자인 적용 완료`

#### ⏳ feature-water:home - 메인 화면 (대기)
- ⏳ Figma 디자인 node-id 매핑 대기
- ⏳ 메인 물 추적 화면 재디자인

#### ⏳ feature-water:alarm - 알람 화면 (대기)
- ⏳ Figma 디자인 node-id 매핑 대기

#### ⏳ feature-water:cup - 컵 관리 화면 (대기)
- ⏳ Figma 디자인 node-id 매핑 대기

#### ⏳ feature-water:record - 로그 및 차트 화면 (대기)
- ⏳ Figma 디자인 node-id 매핑 대기

#### ⏳ feature-water:setting - 물 관련 설정 (대기)
- ⏳ Figma 디자인 node-id 매핑 대기

#### ⏸️ feature-common:setting - 앱 설정 (보류)
- 추후 작업 지시 전까지 보류

---

## 완료된 Phase

### ✅ Phase 1: XML + Fragment 디자인 변경 (물 앱 컨셉 적용)

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

#### 모듈별 작업 완료 내역

**✅ 1. feature-water:home** - 메인 화면 개선
- FAB 버튼 가시성 향상
- 롱클릭 되돌리기 버튼 아이콘을 목적에 맞게 수정
- 중앙 애니메이션 영역 외곽선을 컵 모양으로 변경하여 담겨있는 느낌 제공
- 애니메이션 영역 하단 텍스트가 두줄이 되어도 애니메이션 영역이 줄어들지 않도록 두줄 기준으로 높이 고정
- 다국어 텍스트가 두줄을 넘어가지 않도록 관리

**✅ 1.5. WaterActivity** - 메인 액티비티 UI 재디자인
- Bottom Navigation 영역을 앱 컨셉에 맞게 재디자인
- ActionBar 영역 재디자인
- 우측 상단 더보기 버튼 제거
- 홈 모듈의 목표 영역 클릭 시 더보기 버튼에서 호출하던 팝업 연결
- 홈 모듈의 다음 알람 영역 클릭 시 더보기 버튼에서 호출하던 화면 이동 연결

**✅ 2. core:ui** - 공통 UI 컴포넌트
- 다른 모듈에서 재사용 가능한 공통 컴포넌트 개발

**✅ 3. feature-water:cup** - 컵 관리 화면

**✅ 4. feature-water:record** - 로그 및 차트 화면

**✅ 5. feature-water:alarm** - 알람 설정 화면
- 알람 설정 화면 물 앱 컨셉 적용 (MaterialCardView, 글래스모피즘)
- 알람 모드 관련 화면 디자인 개선 (fragment_alarm_mode.xml, fragment_alarm_mode_period.xml, fragment_alarm_mode_custom.xml)
- AlarmActivity 기상 알람 스타일 UI (드래그 투 디스미스, 70% 임계값)
- item_alarm.xml, item_alarm_edit.xml MaterialCardView 적용
- core:alarmnoti에 core:ui 의존성 추가
- 다국어 지원 (한국어, 영어, 일본어, 중국어)

**✅ 6. feature-water:setting** - 물 관련 설정
- fragment_setting.xml 그라데이션 배경 및 섹션 스타일 통일
- setting_info.xml MaterialCardView 적용, 계정 정보 및 통계 디자인 개선
- setting_water.xml MaterialCardView 적용, 구분선 추가, right_arrow 아이콘 적용
- setting_alarm.xml MaterialCardView 적용, 알람 설정 세부 항목 디자인 개선
- setting_etc.xml MaterialCardView 적용, 언어 설정 디자인 개선
- dialog_language.xml 터치 영역 확보 및 텍스트 스타일 개선

**✅ 7. feature-common:init** - 온보딩 화면
- fragment_init_language.xml 언어 선택 화면 물 앱 컨셉 적용
- fragment_init_time.xml 시간 설정 화면 물 앱 컨셉 적용
- fragment_init_intake.xml 섭취량 설정 화면 물 앱 컨셉 적용
- Progress Indicator 개선 (40dp 원형, active/inactive 상태, elevation)
- MaterialCardView 및 글래스모피즘 효과 적용
- 그라데이션 배경 및 MaterialButton 적용

**진행률: 7/7 완료 (feature-common:setting 보류)**

---

### ✅ Phase 2: LiveData → StateFlow 마이그레이션

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

**완료 커밋:** `95c3d0e - fix: WaterActivity LiveData → Flow 마이그레이션 누락 수정`

---

### ✅ Phase 2.5: KAPT → KSP 마이그레이션

#### 작업 목표
- 빌드 속도 향상을 위한 KAPT → KSP 마이그레이션
- Hilt, Room, Moshi 등 KSP 지원 라이브러리 마이그레이션

**완료 커밋:** `a88f436 - refactor: KAPT → KSP 마이그레이션 및 다국어 지원 개선`

---

### ✅ Phase 2.6: Skills 및 토큰 최적화

#### 작업 목표
- Claude Code Skills 기반 워크플로우 적용
- 토큰 사용량 최적화

**완료 커밋:** `16d7741 - chore: Skills 기반 워크플로우 적용 및 토큰 최적화`

---

## 향후 계획

### Phase 4: Compose UI 마이그레이션 (예정)

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

---

## 주의사항

- **ViewModel 인터페이스 유지**: 모든 public 메서드와 프로퍼티는 반드시 유지
- **비즈니스 로직 보존**: Repository, UseCase, Domain 레이어는 변경 금지
- **점진적 마이그레이션**: 모듈별로 단계적 진행, 빌드 안정성 확보
- **테스트 커버리지**: 각 단계별 단위 테스트 및 UI 테스트 검증
