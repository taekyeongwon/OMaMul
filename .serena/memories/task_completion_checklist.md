# 작업 완료 시 체크리스트

## 필수 검증 단계

### 1. 코드 작성 후
- [ ] ViewModel public 인터페이스 유지 확인
- [ ] Repository 패턴 준수 확인
- [ ] Hilt 의존성 주입 적용 확인

### 2. UI 작성 후
- [ ] **String 리소스**: 5개 언어 모두 추가 확인
  - values/strings.xml
  - values-ko-rKR/strings.xml
  - values-en-rUS/strings.xml
  - values-ja/strings.xml
  - values-zh-rCN/strings.xml
- [ ] **Color 리소스**: colors.xml에 정의 확인
- [ ] **Drawable 리소스**: 모든 참조 drawable 존재 확인
- [ ] **Elevation 사용 시**: 상위 레이아웃에 clipToPadding="false" 확인
- [ ] **아이콘**: 목적에 맞는 아이콘 사용 확인

### 3. Gradle 파일 수정 후
- [ ] **필수**: `./gradlew --refresh-dependencies` 실행

### 4. 빌드 테스트
```bash
# 모듈별 빌드 (해당 모듈만)
./gradlew :feature-water:home:assembleDebug

# 전체 빌드
./gradlew assembleDevDebug
```

### 5. 코드 품질 검사
```bash
# Lint 검사
./gradlew lint
```

### 6. 테스트 실행
```bash
# 단위 테스트
./gradlew test

# 특정 모듈 테스트
./gradlew :모듈명:test
```

### 7. Git 커밋 (모듈 작업 완료 시 필수)
```bash
git add .
git commit -m "$(cat <<'EOF'
모듈명: 작업 내용 요약

## 주요 변경사항
- 변경 1
- 변경 2
- 변경 3

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
EOF
)"
```

## 디자인 변경 작업 시 추가 체크리스트

### Phase 1: XML 디자인 변경
- [ ] 물 앱 컨셉 적용 (블루 그라데이션, 글래스모피즘)
- [ ] MaterialCardView 사용
- [ ] 라운드 코너 (16dp) 적용
- [ ] 적절한 여백 (padding, margin)
- [ ] elevation 및 그림자 효과 확인
- [ ] 다국어 텍스트 두 줄 이하로 관리

### Phase 2: LiveData → StateFlow 마이그레이션
- [ ] LiveData → StateFlow 변환
- [ ] SingleLiveEvent → SharedFlow 변환
- [ ] 변수명 변경 (xxxLiveData → xxxFlow)
- [ ] Fragment의 collect 방식 변경
- [ ] 단위 테스트 업데이트

### Phase 3: Compose 마이그레이션
- [ ] Composable 구현
- [ ] Navigation 구조 변경
- [ ] ViewModel 인터페이스 유지
- [ ] MPAndroidChart AndroidView 래핑

## 빌드 에러 발생 시 체크포인트
1. **String not found**: strings.xml 5개 언어 모두 확인
2. **Color not found**: colors.xml에 정의 확인
3. **Drawable not found**: drawable 파일 존재 확인
4. **Gradle sync failed**: `./gradlew --refresh-dependencies` 실행
5. **Hilt error**: @HiltViewModel, @Inject 어노테이션 확인

## 최종 검증
- [ ] 앱 실행 및 기능 테스트
- [ ] UI 디자인 확인
- [ ] 다국어 전환 테스트
- [ ] 알람 및 백그라운드 작업 확인
