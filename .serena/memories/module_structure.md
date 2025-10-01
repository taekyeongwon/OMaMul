# 모듈 구조

## Core 모듈

### core:data
- Repository 구현체
- 데이터 매퍼 (Entity ↔ Domain Model 변환)
- 주요 구현: WaterRepositoryImpl, CupRepositoryImpl, AlarmRepositoryImpl, SettingRepositoryImpl, PrefDataRepositoryImpl

### core:database
- Realm 데이터베이스 엔티티 및 DAO
- 주요 엔티티:
  - DayOfWaterEntity: 일일 물 섭취 기록
  - CupEntity: 컵 정의
  - WaterEntity: 개별 물 섭취 항목
  - AlarmSettingsEntity: 알람 구성
  - SettingEntity: 앱 프리퍼런스

### core:domain
- 비즈니스 로직
- Repository 인터페이스
- 도메인 모델

### core:ui
- 공유 UI 컴포넌트
- 커스텀 차트 (MPAndroidChart):
  - CustomLineChart, CustomBarChart
  - Day/Week/Month 마커 뷰
- 다이얼로그
- 공통 Drawable 및 Color 리소스
- 다국어 String 리소스 (5개 언어)

### core:common
- 유틸리티
- 권한 관리
- 애니메이션
- LocaleHelper (다국어 지원)

### core:navigation
- Jetpack Navigation 확장

### core:datastore
- DataStore 프리퍼런스 구현

### core:alarmnoti
- 알람 및 알림 중앙 관리

### core:firebase
- Firebase Analytics, Auth 통합

## Feature 모듈

### feature-water:home
- 메인 물 추적 화면
- ViewPager2 기반
- 주요 ViewModel: WaterViewModel

### feature-water:alarm
- 물 마시기 알람 설정

### feature-water:cup
- 컵 관리 및 선택

### feature-water:record
- 물 섭취 로그 및 차트

### feature-water:setting
- 물 관련 설정

### feature-common:base
- BaseViewModel
- 공통 에러 처리

### feature-common:init
- 온보딩 플로우

### feature-common:setting
- 앱 설정 및 계정 관리

## 의존성 흐름
```
app
├── core:data
│   ├── core:domain
│   └── core:database
├── core:common
├── core:alarmnoti
├── core:firebase
└── feature-* 모듈들
    ├── core:ui
    ├── core:domain
    └── feature-common:base
```

## 특징
- Clean Architecture: 레이어 간 명확한 분리
- MVVM: ViewModel이 UI 로직 처리
- Repository Pattern: 데이터 소스 추상화
- 모듈화: 기능별 독립적 개발 가능
