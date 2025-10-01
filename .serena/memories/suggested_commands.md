# 권장 명령어

## 빌드 명령어

### 개발 빌드
```bash
# 디버그 빌드 (dev flavor)
./gradlew assembleDevDebug

# 릴리즈 빌드 (prod flavor)
./gradlew assembleProdRelease

# 모든 빌드 변형
./gradlew assemble

# 프로젝트 클린
./gradlew clean

# 의존성 새로고침 ⭐ Gradle 파일 수정 후 필수!
./gradlew --refresh-dependencies
```

### 설치 및 실행
```bash
# 개발 빌드를 기기에 설치
./gradlew installDevDebug

# 앱 제거
./gradlew uninstallDevDebug
```

## 테스트 명령어

### 단위 테스트
```bash
# 전체 단위 테스트
./gradlew test

# 특정 모듈 테스트
./gradlew :core:database:test
./gradlew :feature-water:home:test
```

### 계측 테스트
```bash
# 전체 계측 테스트 (기기 연결 필요)
./gradlew connectedAndroidTest

# 특정 모듈 계측 테스트
./gradlew :app:connectedAndroidTest
```

## 코드 품질

### Lint
```bash
# Lint 검사 및 리포트 생성
./gradlew lint

# 특정 모듈 Lint
./gradlew :app:lint
```

## 모듈별 빌드
```bash
# 특정 모듈만 빌드
./gradlew :feature-water:home:assembleDebug
./gradlew :core:ui:assembleDebug
```

## Git 명령어 (Windows)
```bash
# 상태 확인
git status

# 변경사항 확인
git diff

# 스테이징
git add .

# 커밋
git commit -m "메시지"

# 푸시
git push origin 브랜치명
```

## 유용한 Gradle 옵션
```bash
# 빌드 캐시 사용 안함
./gradlew --no-build-cache assembleDebug

# 오프라인 모드
./gradlew --offline assembleDebug

# 병렬 빌드
./gradlew --parallel assembleDebug

# 빌드 스캔
./gradlew --scan assembleDebug
```

## Windows 특이사항
- **실행**: `./gradlew` 대신 `gradlew.bat` 또는 `./gradlew` 모두 가능
- **경로**: 백슬래시(\) 사용, Git Bash는 슬래시(/) 지원
- **권한**: PowerShell에서 실행 시 `Set-ExecutionPolicy` 설정 필요할 수 있음

## 작업 완료 후 체크리스트
1. **빌드 테스트**: `./gradlew assembleDevDebug`
2. **Lint 검사**: `./gradlew lint`
3. **단위 테스트**: `./gradlew test`
4. **Git 커밋**: 변경사항 커밋
5. **의존성 변경 시**: `./gradlew --refresh-dependencies` 필수!
