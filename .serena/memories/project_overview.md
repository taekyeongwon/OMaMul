# OMaMul 프로젝트 개요

## 프로젝트 목적
물 섭취 관리 Android 애플리케이션으로, 사용자가 일일 물 섭취량을 추적하고, 알림을 받으며, 기록을 차트로 시각화할 수 있는 앱입니다.

## 주요 기능
- **물 섭취 추적**: 컵 선택을 통한 간편한 물 섭취 기록
- **알람 및 알림**: 사용자 정의 가능한 물 마시기 알림
- **데이터 시각화**: MPAndroidChart를 사용한 일/주/월별 차트
- **다국어 지원**: 한국어, 영어, 일본어, 중국어
- **클라우드 백업**: Google Drive 일일 백업 (새벽 3시 자동)

## 기술 스택

### 아키텍처
- **패턴**: Clean Architecture + MVVM
- **구조**: Multi-Module (Core + Feature 모듈)
- **의존성 주입**: Hilt
- **Navigation**: Single Activity + Jetpack Navigation

### UI 레이어
- **하이브리드**: Jetpack Compose + XML (Data/View Binding)
- **Material Design**: Material Components
- **차트**: MPAndroidChart (커스텀 구현)
- **애니메이션**: Lottie

### 데이터 레이어
- **데이터베이스**: Realm Kotlin
- **프리퍼런스**: DataStore
- **네트워크**: Retrofit + OkHttp
- **패턴**: Repository Pattern + Mapper

### 백그라운드 처리
- **WorkManager**: 예약 작업 (일일 백업)
- **AlarmManager**: 물 마시기 알림
- **Broadcast Receivers**: 시스템 이벤트 처리

### 기타
- **Firebase**: Analytics, Auth
- **Google Play Services**: 인증
- **Glide**: 이미지 로딩

## 언어 및 버전
- **Kotlin**: Official code style
- **Java**: 17
- **Compile SDK**: 34
- **Min SDK**: 26

## Product Flavors
- `dev`: 개발 빌드 (applicationId.dev 접미사)
- `prod`: 프로덕션 빌드
