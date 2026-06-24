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
* `doc/erd.md` 기준 JPA 엔티티와 Repository 추가
  * `routes`, `stops`, `route_stops`, `buses`, `bus_locations`, `bus_events` 테이블 대응
  * JPA 객체 연관관계는 두지 않고 `routeId`, `stopId`, `busId` 같은 FK ID 컬럼으로 참조
  * ERD의 unique 제약과 조회용 인덱스 후보를 엔티티 매핑에 반영
* QueryDSL 사용을 위한 의존성, annotation processor, `JPAQueryFactory` 설정 추가
* `doc/endpoint.md`에 정의된 공개 조회 API 4개 구현
  * `GET /v1/buses`: 상태, 노선 ID, 키워드, 페이지 조건을 지원하는 버스 목록 조회
  * `GET /v1/buses/{busId}`: 버스 상세, 노선 정보, 최신 위치 조회
  * `GET /v1/buses/{busId}/events`: 특정 버스의 최근 이벤트 조회
  * `GET /v1/events`: 전체 최근 이벤트 조회 및 이벤트 유형 필터 지원
  * `ONLINE` / `OFFLINE` 상태는 조회 시점의 UTC 기준 현재 시각과 `last_communicated_at` 차이로 계산
  * 목록 및 이벤트 조인 조회는 FK ID 기반 엔티티 설계를 유지하고 QueryDSL custom repository로 구현
  * `page`, `size`, `limit`, enum 파라미터 오류는 공통 `INVALID_REQUEST` 응답으로 처리
  * 존재하지 않는 버스 상세 또는 버스별 이벤트 조회는 `BUS_NOT_FOUND` 응답으로 처리
* `POST /v1/internal/telemetry` 텔레메트리 수신 API 구현
  * Simulator 또는 차량 단말이 전송한 위치, 속도, 기록 시각을 `bus_locations`에 저장
  * `buses.current_speed_kph`, `buses.last_communicated_at`, `buses.updated_at` 갱신
  * 이벤트가 포함된 요청은 `bus_events`에 함께 저장
  * 잘못된 요청 본문은 `INVALID_REQUEST`, 존재하지 않는 버스는 `BUS_NOT_FOUND` 응답으로 처리
* 텔레메트리 상세 API 명세는 `doc/endpoint.md`에 두고, `doc/backend.md`의 중복 요청 예시와 처리 규칙은 요약 참조로 정리
* Docker 및 프로파일 기반 실행 설정 추가
  * 애플리케이션 기본 포트를 `9090`으로 설정
  * 공통 설정은 `application.yaml`, 로컬 설정은 `application-local.yaml`, 배포 설정은 `application-prod.yaml`에 구성
  * 테스트 설정은 배포 산출물에 포함되지 않도록 `src/test/resources/application.yaml`에 별도 구성
  * 로컬 및 테스트 실행은 H2를 사용하고, 배포용 `prod` 프로파일은 MySQL 드라이버와 환경변수 기반 접속 정보를 사용
  * `docker-compose.yml`은 `control-api` 컨테이너를 `9090:9090`으로 노출하고, 기존 외부 네트워크 `my-network`, `control-network`에 연결
  * Dockerfile은 멀티 스테이지 빌드와 BuildKit Gradle 캐시 마운트를 사용

## 데이터베이스 참고 사항

* `local` 설정과 테스트 전용 설정은 MySQL 호환 모드로 실행되는 H2 인메모리 데이터베이스 사용
* `prod` 프로파일은 MySQL 접속을 사용하며, 현재 Docker Compose에는 더미 접속 정보가 들어 있음
* Flyway 및 Liquibase는 아직 구성되지 않은 상태
