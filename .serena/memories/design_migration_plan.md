# 디자인 변경 및 마이그레이션 계획

## Phase 1: XML + Fragment 디자인 변경 (물 앱 컨셉 적용)

### 진행 상황: 7/8 완료

#### ✅ 완료된 모듈
1. **feature-water:home** - 메인 화면 개선
2. **WaterActivity** - 메인 액티비티 UI 재디자인
3. **core:ui** - 공통 UI 컴포넌트
4. **feature-water:cup** - 컵 관리 화면
5. **feature-water:record** - 로그 및 차트 화면
6. **feature-water:alarm** - 알람 설정 화면
7. **feature-water:setting** - 물 관련 설정
8. **feature-common:init** - 온보딩 화면

#### 🔲 남은 모듈
- **feature-common:setting** - 앱 설정 및 계정 관리

### 디자인 컨셉 요소
- **색상 팔레트**:
  - Primary: #4A90E2 (블루)
  - Primary Variant: #2196F3
  - Secondary: #E3F2FD (라이트 블루)
  - Surface: #FFFFFF with 90% opacity (글래스모피즘)
- **효과**: 글래스모피즘 카드, 그라데이션 배경
- **애니메이션**: 물 웨이브, 부드러운 전환
- **아이콘**: Material Icons Extended, 물방울/컵 테마
- **레이아웃**: 라운드 코너(16dp), MaterialCardView

### 모듈별 작업 프로세스
```bash
# 1. 모듈 디자인 변경
# 2. 모듈 빌드 테스트
./gradlew :모듈명:assembleDebug

# 3. 전체 빌드 테스트
./gradlew assembleDevDebug

# 4. Git 커밋 (작업 완료 후 필수)
git add .
git commit -m "모듈명 물 앱 컨셉 적용..."
```

## Phase 2: LiveData → StateFlow 마이그레이션

### 목적
- Compose 마이그레이션 준비
- 반응형 프로그래밍 일관성

### 변환 규칙
- **LiveData → StateFlow**: 초기값 필요 (없으면 null)
- **MutableLiveData → MutableStateFlow**
- **SingleLiveEvent → SharedFlow**: replay=0, extraBufferCapacity=1
- **변수명**: `xxxLiveData` → `xxxFlow`

### 작업 순서
1. ViewModel 클래스별 변환
2. Fragment의 observe → collect 변경
3. DataBinding 확인
4. 단위 테스트 업데이트

### 주의사항
- ⚠️ **ViewModel public 인터페이스 절대 변경 금지!**
- 기존 기능 유지 확인
- Flow 수집은 lifecycleScope 사용

## Phase 3: Compose UI 마이그레이션

### 목적
- 전체 UI를 Jetpack Compose로 전환
- Fragment, XML, ViewBinding, DataBinding 제거

### 설정
```gradle
plugins {
    alias(libs.plugins.kotlin.compose)
}

android {
    buildFeatures {
        compose = true
        viewBinding = false
        dataBinding = false
    }
}
```

### 마이그레이션 순서
1. **Navigation**: Fragment 기반 → Composable 기반
2. **Activity**: WaterActivity → Compose 전용
3. **Screens**: 모듈별 Composable 구현
   - HomeScreen (WaterFragment)
   - CupManageScreen (CupManageFragment)
   - WaterLogScreen (WaterLogFragment)
   - AlarmScreen (WaterAlarmFragment)
   - SettingScreen (WaterSettingFragment)
4. **공통 컴포넌트**: core:ui Composable 구현
5. **차트**: MPAndroidChart를 AndroidView로 래핑

### Compose 디자인 시스템
```kotlin
// Color.kt
val WaterBlue = Color(0xFF4A90E2)
val WaterBlueVariant = Color(0xFF2196F3)
val WaterLightBlue = Color(0xFFE3F2FD)
val GlassSurface = Color(0xE6FFFFFF)

// Theme.kt
val WaterAppColorScheme = lightColorScheme(
    primary = WaterBlue,
    primaryContainer = WaterBlueVariant,
    secondary = WaterLightBlue,
    surface = GlassSurface
)
```

## 중요 원칙
1. **ViewModel 인터페이스 유지**: public 메서드/프로퍼티 변경 금지
2. **비즈니스 로직 보존**: Repository, Domain 레이어 변경 금지
3. **점진적 진행**: 모듈별 단계적 마이그레이션
4. **안정성 우선**: 각 단계마다 빌드 테스트
5. **테스트 커버리지**: 단위 테스트 및 UI 테스트 검증
