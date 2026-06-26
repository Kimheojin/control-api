# MongoDB Telemetry Storage Consideration

이 문서는 텔레메트리 이력 데이터를 MongoDB로 분리하는 방안을 나중에 검토하기 위해 정리한 참고 문서다.
현재 구현을 변경한 내용이 아니며, 실제 적용 시에는 별도 구현 및 검증이 필요하다.

## 1. 배경

현재 `POST /v1/internal/telemetry`로 수신한 텔레메트리는 RDBMS에 저장된다.

* 위치 이력은 `bus_locations`에 계속 누적된다.
* 이벤트 이력은 `bus_events`에 계속 누적된다.
* `buses.current_speed_kph`, `buses.last_communicated_at`은 현재 상태 조회를 위해 갱신된다.

위치와 이벤트 이력은 append-only 성격이 강하고 시간이 지날수록 빠르게 증가할 수 있다.
운영 관제의 최근 조회 중심 요구에는 MongoDB 같은 문서 저장소와 TTL 기반 보관 정책이 더 적합할 수 있다.

## 2. 목표 방향

MongoDB 도입 시 기본 방향은 다음과 같다.

* RDBMS는 기준 데이터와 현재 상태를 계속 담당한다.
  * 버스, 노선, 정류장 기준 데이터
  * `buses.current_speed_kph`
  * `buses.last_communicated_at`
* MongoDB는 텔레메트리 이력 저장을 담당한다.
  * 위치 이력
  * 이벤트 이력
* 공개 API의 요청/응답 형태는 변경하지 않는다.
* 프론트엔드와 시뮬레이터 연동 계약은 유지한다.
* MongoDB 텔레메트리 이력은 TTL 인덱스로 기본 7일 보관한다.

## 3. 저장 구조 초안

### `bus_locations` 컬렉션

```json
{
  "id": "ObjectId",
  "busId": 1,
  "latitude": 37.5665,
  "longitude": 126.9780,
  "speedKph": 42,
  "recordedAt": "2026-06-23T01:20:00Z",
  "createdAt": "2026-06-23T01:20:01Z"
}
```

권장 인덱스:

* `{ "busId": 1, "recordedAt": -1 }`
* `{ "createdAt": 1 }` TTL 7일

### `bus_events` 컬렉션

```json
{
  "id": "ObjectId",
  "busId": 1,
  "type": "SUDDEN_BRAKE",
  "latitude": 37.5658,
  "longitude": 126.9772,
  "occurredAt": "2026-06-23T01:18:12Z",
  "description": "급정거 감지",
  "createdAt": "2026-06-23T01:18:13Z"
}
```

권장 인덱스:

* `{ "busId": 1, "occurredAt": -1 }`
* `{ "type": 1, "occurredAt": -1 }`
* `{ "createdAt": 1 }` TTL 7일

## 4. 처리 흐름 변경안

### 텔레메트리 수신

`POST /v1/internal/telemetry` 처리 흐름은 다음처럼 변경할 수 있다.

1. RDBMS에서 버스 존재 여부를 확인한다.
2. MongoDB `bus_locations`에 위치 이력을 저장한다.
3. RDBMS `buses.current_speed_kph`, `buses.last_communicated_at`, `buses.updated_at`을 갱신한다.
4. 요청에 이벤트가 있으면 MongoDB `bus_events`에 이벤트 이력을 저장한다.
5. 기존과 동일하게 `{ "busId": ..., "received": true }`를 반환한다.

### 버스 상세 조회

`GET /v1/buses/{busId}`는 RDBMS에서 버스와 노선 정보를 조회하고, MongoDB에서 해당 버스의 최신 위치 1건을 조회해 `currentLocation`에 매핑한다.

위치 이력이 없으면 기존과 동일하게 `currentLocation`은 `null`로 둔다.

### 이벤트 조회

`GET /v1/buses/{busId}/events`는 MongoDB에서 해당 버스의 최근 이벤트를 조회한다.

`GET /v1/events`는 MongoDB에서 전체 최근 이벤트를 조회하고, `type` 파라미터가 있으면 MongoDB 조회 조건으로 적용한다.

이벤트 응답에 필요한 `busNumber`, `routeName`은 RDBMS의 버스/노선 기준 데이터와 조합한다.

## 5. 구현 시 고려 사항

* Spring Data MongoDB 의존성을 추가해야 한다.
* local/prod 프로파일에 MongoDB 접속 설정을 추가해야 한다.
* Docker 환경에서는 `MONGO_HOST`, `MONGO_PORT`, `MONGO_DATABASE` 같은 환경변수를 받을 수 있게 해야 한다.
* MongoDB 장애 시 텔레메트리 수신 API를 실패 처리할지, RDB 현재 상태 갱신만 허용할지 정책을 정해야 한다.
* RDB와 MongoDB를 동시에 쓰므로 완전한 단일 트랜잭션으로 묶기는 어렵다.
* MVP에서는 수신 실패 시 명확히 실패 응답을 주는 방향이 단순하다.
* 기존 RDB `bus_locations`, `bus_events` 데이터 마이그레이션 여부는 별도 결정이 필요하다.

## 6. 테스트 방향

적용 시 최소한 다음 테스트를 추가하거나 수정한다.

* 텔레메트리 수신 시 MongoDB에 위치 이력이 저장되고 RDB 버스 현재 상태가 갱신되는지 검증
* 이벤트 포함 요청 시 MongoDB에 이벤트 이력이 저장되는지 검증
* 존재하지 않는 버스 ID는 기존처럼 `BUS_NOT_FOUND`로 실패하는지 검증
* 버스 상세 조회가 MongoDB 최신 위치를 `currentLocation`으로 반환하는지 검증
* 위치 이력이 없으면 `currentLocation`이 `null`인지 검증
* 버스별 이벤트 조회가 MongoDB 이벤트를 기존 응답 형태로 반환하는지 검증
* 전체 이벤트 조회와 이벤트 타입 필터가 기존 응답 형태를 유지하는지 검증

## 7. 현재 결정 상태

이 문서는 구현 지시가 아니라 향후 검토용 설계 메모다.

현재 결정된 기본 가정:

* 텔레메트리 이력만 MongoDB로 분리한다.
* 기준 데이터와 현재 상태는 RDBMS에 유지한다.
* 이력 보관 기간은 기본 7일로 검토한다.
* 공개 API 계약은 변경하지 않는다.

아직 결정되지 않은 사항:

* MongoDB 장애 시 수신 API 실패 정책
* 기존 RDB 이력 데이터 마이그레이션 여부
* local Docker Compose에 MongoDB 컨테이너를 포함할지 여부
* 운영 환경 MongoDB 인증, replica set, 백업 정책
