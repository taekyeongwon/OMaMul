# Git Commit Skill

이 스킬은 작업 완료 후 자동으로 Git 커밋을 생성합니다.

## 실행 조건
- 모듈 작업이 완료되었을 때
- 빌드 테스트가 성공했을 때
- CLAUDE.md 진행 상황이 업데이트되었을 때

## 작업 순서

1. **Git 상태 확인**
   - `git status`로 변경된 파일 확인
   - 스테이징되지 않은 파일 확인

2. **CLAUDE.md 업데이트 검증**
   - 진행 상황 체크박스(✅) 업데이트 확인
   - 진행률 카운터 업데이트 확인
   - 주요 변경사항 기록 확인

3. **커밋 메시지 생성**
   - 제목: 모듈명과 주요 작업 내용 (50자 이내)
   - 본문: 상세 변경사항을 카테고리별로 정리
     - ## 주요 변경사항
     - 작업 내용 요약 (불릿 포인트)
   - 푸터: Claude Code 생성 표시

4. **Git 커밋 실행**
   ```bash
   git add .
   git commit -m "$(cat <<'EOF'
   [제목]

   ## 주요 변경사항
   - [변경사항 1]
   - [변경사항 2]

   🤖 Generated with [Claude Code](https://claude.ai/code)

   Co-Authored-By: Claude <noreply@anthropic.com>
   EOF
   )"
   ```

5. **커밋 결과 확인**
   - 커밋 해시 및 파일 변경 통계 확인
   - `git log -1 --oneline`으로 최근 커밋 확인

## 서브에이전트 활용
복잡하지 않은 커밋 작업은 Haiku 모델을 사용한 서브에이전트가 처리하여 토큰 효율성을 높입니다.

## 주의사항
- **CLAUDE.md 업데이트 필수**: 커밋 전 반드시 진행 상황이 업데이트되어야 함
- **빌드 테스트 필수**: 커밋 전 빌드가 성공해야 함
- **의미 있는 커밋**: 작업 단위별로 커밋을 분리
- **Gradle 파일 수정 시**: `./gradlew --refresh-dependencies` 실행 후 커밋

## Git Safety Protocol
- NEVER update git config
- NEVER run destructive commands (push --force, hard reset)
- NEVER skip hooks (--no-verify, --no-gpg-sign)
- NEVER force push to main/master
- Avoid `git commit --amend` unless explicitly requested
- DO NOT push to remote unless user explicitly requests
