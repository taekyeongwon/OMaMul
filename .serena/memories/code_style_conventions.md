# 코드 스타일 및 컨벤션

## Kotlin 스타일
- **공식 스타일**: kotlin.code.style=official
- **명명 규칙**: camelCase (변수, 함수), PascalCase (클래스)
- **프로퍼티 접두사**:
  - `_xxx`: private MutableStateFlow/MutableLiveData
  - `xxxFlow`: public StateFlow
  - `xxxLiveData`: public LiveData (마이그레이션 예정)

## 아키텍처 컨벤션

### ViewModel
- **어노테이션**: @HiltViewModel
- **의존성 주입**: 생성자 주입
- **반응형**: Flow + LiveData 혼합 사용
  - StateFlow: UI 상태
  - LiveData: 기존 호환성
  - SingleLiveEvent: 일회성 이벤트
- **중요**: ViewModel의 public 인터페이스는 절대 변경 금지!

### Repository
- **패턴**: 인터페이스(core:domain) + 구현체(core:data)
- **명명**: XxxRepositoryImpl
- **데이터 변환**: Mapper 사용 (Entity ↔ Domain)

### 데이터베이스
- **Realm Kotlin**: 모든 엔티티
- **명명**: XxxEntity
- **위치**: core:database/model

## UI 리소스 규칙

### String 리소스
- **필수**: 모든 UI 문구는 string 리소스로 관리
- **위치**: core:ui/res/values/strings.xml
- **다국어**: 5개 언어 모두 동시 추가 필수
  - values/ (기본, 한국어)
  - values-ko-rKR/
  - values-en-rUS/
  - values-ja/
  - values-zh-rCN/
- **명명**: 접두사 사용 (water_, alarm_, setting_)

### Color 리소스
- **필수**: 하드코딩 금지, colors.xml에 정의
- **위치**: core:ui/res/values/colors.xml
- **주요 색상**:
  - water_blue (#4A90E2)
  - water_blue_variant (#2196F3)
  - water_light_blue (#E3F2FD)
  - glass_surface (#E6FFFFFF)

### Drawable 리소스
- **필수**: 레이아웃에서 참조 시 반드시 존재
- **공통**: core:ui/res/drawable
- **모듈별**: 해당 모듈/res/drawable

## 디자인 컨셉
- **테마**: 물 앱 (블루 그라데이션)
- **효과**: 글래스모피즘
- **레이아웃**: MaterialCardView, 라운드 코너(16dp)
- **아이콘**: Material Icons Extended 우선
- **그림자**: elevation 사용 시 상위에 clipToPadding="false" 필수

## 파일 구조
```
모듈/src/main/java/com/tkw/모듈명/
├── XxxViewModel.kt
├── XxxFragment.kt
└── adapter/
    └── XxxAdapter.kt

모듈/src/main/res/
├── layout/
│   ├── fragment_xxx.xml
│   └── item_xxx.xml
└── drawable/
    └── xxx.xml
```

## 주석 및 문서
- **언어**: 한국어
- **형식**: KDoc 스타일
- **필수 주석**: public API, 복잡한 로직
