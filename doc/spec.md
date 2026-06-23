# Implementation Notes

## 완료된 작업

* 프로젝트 전반의 구조, 테스트, API, 영속성, 문서화 규칙을 담은 루트 `AGENTS.md` 추가
* 로컬 개발 및 테스트를 위한 임시 H2 데이터소스 지원 추가
* Spring Security 기반 CORS 설정 추가
  * 허용 Origin은 `https://control-front-navy.vercel.app` 단일 도메인
  * 현재 MVP 단계에서는 모든 요청을 인증 없이 허용
* 공통 오류 응답 구조와 전역 예외 처리 추가
  * 오류 응답은 `{ code, message }` 형식으로 통일
  * 요청 값 오류는 `INVALID_REQUEST`, 존재하지 않는 버스는 `BUS_NOT_FOUND`, 미처리 서버 오류는 `INTERNAL_SERVER_ERROR` 사용
  * 스택트레이스와 내부 구현 정보는 API 응답에 노출하지 않음

## 데이터베이스 참고 사항

* 현재 데이터소스는 MySQL 호환 모드로 실행되는 H2 인메모리 데이터베이스
* H2는 임시 구성으로, 향후 영속성 작업 시 MySQL 마이그레이션 고려 필요
* Flyway 및 Liquibase는 아직 구성되지 않은 상태
