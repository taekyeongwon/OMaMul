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
*   **UI**: XML 기반 레이아웃
*   **비동기 처리**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
*   **의존성 주입**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
*   **데이터 저장**: [Realm](https.mongodb.com/docs/realm/sdk/kotlin/), [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
*   **네비게이션**: [Jetpack Navigation](https://developer.android.com/guide/navigation)

## 4. 모듈 구조

프로젝트는 기능별로 다음과 같이 모듈화되어 있습니다.

*   `app`: 최종 애플리케이션을 구성하고 모든 모듈을 통합하는 메인 모듈
*   `core`: 여러 모듈에서 공통으로 사용하는 핵심 기능 모음
    *   `core:common`: 공통 유틸리티, 확장 함수 등
    *   `core:data`: 데이터 소스(Repository) 구현체
    *   `core:database`: Realm DB 관련 코드
    *   `core:datastore`: DataStore 관련 코드
    *   `core:domain`: UseCase, Entity 등 비즈니스 로직
    *   `core:navigation`: 화면 이동(Navigation) 관련 로직
    *   `core:ui`: 공통 UI 컴포넌트 (테마, 스타일 등)
*   `feature-common`: 여러 기능에서 공통으로 사용되는 UI 및 기능 모듈
    *   `feature-common:base`: 기능 모듈의 기반이 되는 클래스
    *   `feature-common:init`: 앱 초기 설정 관련 기능
    *   `feature-common:setting`: 공통 설정 화면
*   `feature-water`: 물 마시기 핵심 기능 모듈
    *   `feature-water:home`: 메인 홈 화면
    *   `feature-water:record`: 섭취량 기록 화면
    *   `feature-water:alarm`: 알림 설정 화면
    *   `feature-water:cup`: 컵 관리 화면
    *   `feature-water:setting`: 물 마시기 관련 설정 화면
