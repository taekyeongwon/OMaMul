# 🎨 Figma 디자인 가이드

이 문서는 Figma 디자인을 XML 레이아웃으로 변환할 때 따라야 할 규칙과 절차를 정의합니다.

---

## 📌 Figma 디자인 정보

**Figma 디자인 소스:**
- 링크: https://www.figma.com/design/Dgp8drNVKW1c6qEqqXcAvy/Readdy--%EC%BB%A4%EB%AE%A4%EB%8B%88%ED%8B%B0-
- File Key: `Dgp8drNVKW1c6qEqqXcAvy`

---

## 🔄 작업 프로세스 (필수 순서)

### 1단계: Figma 디자인 분석
1. `mcp__Framelink_Figma_MCP__get_figma_data` 도구로 Figma node 데이터 조회 (depth=6~8)
2. `globalVars.styles`에서 모든 스타일 정보 정확히 추출:
   - layout (width, height, padding, margin)
   - fill (색상, 그라데이션, 투명도)
   - stroke (테두리 색상, 두께)
   - effect (그림자, 블러)
   - textStyle (폰트, 크기, 정렬)
3. 각 요소의 dimensions, spacing 정확히 계산
4. 색상 값 (#RRGGBB, rgba), 투명도, 그라데이션 정확히 변환
5. borderRadius, stroke, shadow 등 시각 효과 정확히 적용

### 2단계: 공통 리소스 생성 (우선)
**반드시 레이아웃 작성 전에 완료해야 함**

#### 2-1. Color 리소스 추가
- 파일: `core:ui/src/main/res/values/colors.xml`
- Figma에서 추출한 모든 색상을 colors.xml에 추가
- 색상 이름은 의미론적으로 명명 (예: `figma_primary_blue`, `figma_text_secondary`)
- **검증**: 레이아웃에서 참조할 모든 색상이 정의되어 있는지 확인

#### 2-2. String 리소스 추가
- 파일: `core:ui/src/main/res/values*/strings.xml`
- Figma에서 추출한 모든 텍스트를 string 리소스로 추가
- **반드시 5개 언어 모두 추가** (values, values-ko-rKR, values-en-rUS, values-ja, values-zh-rCN)
- **검증**: 레이아웃에서 참조할 모든 string이 존재하는지 확인

#### 2-3. Drawable 리소스 생성
- 경로: `core:ui/src/main/res/drawable/`
- Figma에서 추출한 공통 drawable 생성:
  - 그라데이션 배경 (gradient)
  - 카드 배경 (shape with corner radius)
  - 버튼 배경 (selector with states)
  - 아이콘 (vector drawable)
- **검증**: 레이아웃에서 참조할 모든 drawable이 존재하는지 확인

### 3단계: 공통 컴포넌트 생성
**재사용 가능한 레이아웃 컴포넌트를 먼저 생성**

#### 3-1. 공통 카드 레이아웃
- 파일: `core:ui/src/main/res/layout/component_glass_card.xml`
- MaterialCardView 기반 글래스모피즘 카드
- 속성:
  - `cardCornerRadius="16dp"`
  - `cardElevation="4dp"`
  - `cardBackgroundColor="@color/glass_surface"`

예시:
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardCornerRadius="16dp"
    app:cardElevation="4dp"
    app:cardBackgroundColor="@color/glass_surface"
    android:layout_margin="16dp">

    <!-- 내부 콘텐츠 -->

</com.google.android.material.card.MaterialCardView>
```

#### 3-2. 공통 라디오 버튼
- 파일: `core:ui/src/main/res/drawable/figma_radio_selector.xml`
- selector drawable로 checked/unchecked 상태 정의
- 관련 drawable:
  - `figma_radio_checked.xml`
  - `figma_radio_unchecked.xml`

예시:
```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_checked="true" android:drawable="@drawable/figma_radio_checked" />
    <item android:state_checked="false" android:drawable="@drawable/figma_radio_unchecked" />
</selector>
```

#### 3-3. 공통 버튼 스타일
- 파일: `core:ui/src/main/res/values/styles.xml`
- MaterialButton 기반 스타일 정의

예시:
```xml
<style name="FigmaButton" parent="Widget.MaterialComponents.Button">
    <item name="cornerRadius">24dp</item>
    <item name="backgroundTint">@color/water_blue</item>
    <item name="android:textColor">@color/white</item>
    <item name="android:textSize">16sp</item>
    <item name="android:paddingStart">32dp</item>
    <item name="android:paddingEnd">32dp</item>
    <item name="android:paddingTop">12dp</item>
    <item name="android:paddingBottom">12dp</item>
</style>
```

### 4단계: XML 레이아웃 작성
**반드시 Figma 디자인을 100% 동일하게 재현**

#### 재작성 원칙
- 기존 레이아웃을 수정하는 것이 아니라 Figma 디자인에 맞게 **완전히 새로 작성**
- Fragment 코드와의 연결을 위해 기존 ID는 유지 (예: `rg_language`, `rb_ko`, `btn_next`)
- 공통 컴포넌트(카드, 버튼 등)는 3단계에서 생성한 것을 재사용
- 모든 리소스 참조는 2단계에서 생성한 것을 사용

#### 검증 사항
- 인디케이터 개수 및 스타일 (활성/비활성 색상, 크기, 간격)
- 각 UI 요소의 크기 (width, height)
- 간격 (padding, margin, spacing between elements)
- 카드 구조 (개별 카드 vs 단일 카드 내 여러 항목)
- 버튼 위치 (좌측/우측/중앙)
- 텍스트 정렬 및 크기
- 그라데이션 방향 및 색상
- 그림자 효과 (elevation, translationZ)

### 5단계: 빌드 테스트
```bash
# 모듈 단위 빌드 테스트
./gradlew :feature-xxx:assembleDebug

# 전체 빌드 테스트
./gradlew assembleDevDebug
```

### 6단계: 동작 테스트 (사용자 수동)
- 사용자가 직접 앱 실행하여 디자인 확인
- Figma 디자인과 비교하여 차이점 확인

### 7단계: 오류 수정 (필요 시)
- 사용자가 "디자인이 다르다"고 지적하면 Figma 데이터를 다시 분석하여 정확히 수정
- **추측하지 말고 Figma JSON 데이터를 정확히 읽어서 적용**

---

## 🎨 공통 리소스 정의

### Color 리소스 (`core:ui/src/main/res/values/colors.xml`)

#### 주요 공통 색상
```xml
<!-- 메인 색상 -->
<color name="water_blue">#4A90E2</color>
<color name="water_blue_variant">#2196F3</color>
<color name="water_light_blue">#E3F2FD</color>

<!-- 표면 색상 -->
<color name="glass_surface">#E6FFFFFF</color> <!-- 90% 불투명도 -->
<color name="white">#FFFFFFFF</color>

<!-- 텍스트 색상 -->
<color name="text_primary">#1B1B1F</color>
<color name="text_secondary">#6C757D</color>

<!-- 구분선 색상 -->
<color name="divider_light">#E0E0E0</color>

<!-- 그라데이션 색상 -->
<color name="gradient_start">#4A90E2</color>
<color name="gradient_middle">#5BA3F5</color>
<color name="gradient_end">#E3F2FD</color>
```

### Drawable 리소스 (`core:ui/src/main/res/drawable/`)

#### 주요 공통 drawable
- `figma_gradient_background.xml`: 그라데이션 배경
- `figma_glass_card.xml`: 글래스모피즘 카드 배경
- `figma_progress_bar.xml`: 진행 바 (활성)
- `figma_indicator_inactive.xml`: 비활성 단계 표시
- `figma_radio_checked.xml`: 라디오 버튼 선택 상태
- `figma_radio_unchecked.xml`: 라디오 버튼 비선택 상태
- `figma_radio_selector.xml`: 라디오 버튼 선택자
- `ripple_circle.xml`: 원형 터치 효과 (물결 효과)
- `ico_arrow_right.xml`: 우측 화살표 아이콘
- `ico_close.xml`: 닫기 아이콘
- `ico_water_drop.xml`: 물방울 아이콘

### String 리소스 (`core:ui/src/main/res/values*/strings.xml`)

#### String 추가 프로세스
1. Figma에서 텍스트 추출
2. 의미론적 이름으로 string 리소스 생성 (예: `init_language_title`)
3. 5개 언어 모두 번역 추가:
   - `values/strings.xml` (기본, 한국어)
   - `values-ko-rKR/strings.xml` (한국어)
   - `values-en-rUS/strings.xml` (영어)
   - `values-ja/strings.xml` (일본어)
   - `values-zh-rCN/strings.xml` (중국어 간체)

#### 번역 예시
```xml
<!-- 한국어 -->
<string name="init_language_title">언어를 선택해주세요</string>

<!-- 영어 -->
<string name="init_language_title">Select your language</string>

<!-- 일본어 -->
<string name="init_language_title">言語を選択してください</string>

<!-- 중국어 -->
<string name="init_language_title">请选择语言</string>
```

---

## 📏 Figma 디자인 정확도 규칙

### 필수 원칙
Figma 디자인을 XML 레이아웃으로 변환할 때 **100% 동일하게** 재현해야 함

### 분석 프로세스
1. `mcp__Framelink_Figma_MCP__get_figma_data` 도구로 Figma node 데이터 조회 (depth=6~8)
2. `globalVars.styles`에서 layout, fill, stroke, effect, textStyle 등 모든 스타일 정보 정확히 추출
3. 각 요소의 dimensions (width, height), padding, margin, spacing 정확히 계산
4. 색상 값 (#RRGGBB, rgba), 투명도, 그라데이션 정확히 변환
5. borderRadius, stroke, shadow 등 시각 효과 정확히 적용

### 검증 사항
- 인디케이터 개수 및 스타일 (활성/비활성 색상, 크기, 간격)
- 각 UI 요소의 크기 (width, height)
- 간격 (padding, margin, spacing between elements)
- 카드 구조 (개별 카드 vs 단일 카드 내 여러 항목)
- 버튼 위치 (좌측/우측/중앙)
- 텍스트 정렬 및 크기

### 오류 방지
- Figma 디자인과 다른 부분이 발견되면 즉시 수정
- 사용자가 "디자인이 다르다"고 지적하면 Figma 데이터를 다시 분석하여 정확히 수정
- **추측하지 말고 Figma JSON 데이터를 정확히 읽어서 적용**

---

## 🎯 Figma 컴포넌트 → XML 변환 가이드

### 1. Frame → Layout
- **Frame (vertical)** → `LinearLayout` (android:orientation="vertical")
- **Frame (horizontal)** → `LinearLayout` (android:orientation="horizontal")
- **Frame (auto layout)** → `ConstraintLayout` or `LinearLayout`

### 2. Text → TextView
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/xxx"
    android:textSize="[Figma fontSize]sp"
    android:textColor="@color/[Figma color]"
    android:fontFamily="[Figma fontFamily]"
    android:textStyle="[Figma fontWeight]" />
```

### 3. Rectangle → Shape Drawable
Figma의 Rectangle을 XML shape drawable로 변환:

```xml
<!-- drawable/figma_card_background.xml -->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/[Figma fill]" />
    <corners android:radius="[Figma borderRadius]dp" />
    <stroke
        android:width="[Figma strokeWeight]dp"
        android:color="@color/[Figma stroke]" />
</shape>
```

### 4. Button → MaterialButton
```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="[Figma width]dp"
    android:layout_height="[Figma height]dp"
    android:text="@string/xxx"
    app:cornerRadius="[Figma borderRadius]dp"
    app:backgroundTint="@color/[Figma fill]"
    android:textColor="@color/[Figma textColor]" />
```

### 5. Card → MaterialCardView
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardCornerRadius="[Figma borderRadius]dp"
    app:cardElevation="[Figma shadow]dp"
    app:cardBackgroundColor="@color/[Figma fill]">

    <!-- 내부 콘텐츠 -->

</com.google.android.material.card.MaterialCardView>
```

### 6. Gradient → Gradient Drawable
```xml
<!-- drawable/figma_gradient_background.xml -->
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <gradient
        android:type="linear"
        android:angle="[Figma angle]"
        android:startColor="@color/[Figma color1]"
        android:centerColor="@color/[Figma color2]"
        android:endColor="@color/[Figma color3]" />
</shape>
```

### 7. Shadow → Elevation
```xml
<!-- elevation 사용 -->
<View
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:elevation="[Figma shadow blur]dp"
    android:translationZ="[Figma shadow spread]dp" />

<!-- 상위 레이아웃에 반드시 추가 -->
<LinearLayout
    android:clipToPadding="false"
    android:clipChildren="false">
```

---

## ⚠️ 주의사항

### 1. 리소스 누락 방지
- 레이아웃 작성 전 반드시 색상, drawable, string 리소스를 먼저 생성
- 레이아웃에서 참조하는 모든 리소스가 존재하는지 검증
- 빌드 에러가 발생하면 즉시 누락된 리소스 추가

### 2. 그림자 효과 주의
- `elevation` 또는 `translationZ` 사용 시 상위 레이아웃에 `android:clipToPadding="false"` 필수
- 그림자가 잘리지 않도록 충분한 패딩과 마진 확보

### 3. 다국어 지원 검증
- 모든 텍스트는 string 리소스로 관리
- 5개 언어 모두 번역 추가 확인
- 긴 텍스트의 경우 레이아웃이 깨지지 않는지 확인

### 4. Fragment ID 유지
- 기존 Fragment 코드와의 연결을 위해 View ID는 반드시 유지
- 예: `rg_language`, `rb_ko`, `btn_next` 등

---

## 📝 작업 체크리스트

작업 시작 전 반드시 확인:

- [ ] Figma node 데이터 조회 완료 (depth=6~8)
- [ ] 모든 색상을 colors.xml에 추가 완료
- [ ] 모든 텍스트를 5개 언어 string 리소스에 추가 완료
- [ ] 필요한 모든 drawable 생성 완료
- [ ] 공통 컴포넌트(카드, 버튼) 생성 완료
- [ ] 레이아웃 작성 완료 (Figma 디자인 100% 재현)
- [ ] Fragment ID 유지 확인
- [ ] 빌드 테스트 성공
- [ ] 동작 테스트 요청 (사용자)
- [ ] 사용자 피드백 반영 완료

모든 체크리스트 완료 후 커밋 진행!
