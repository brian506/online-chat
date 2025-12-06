# [Chat-service] 

## 📝 소개 (Overview)
Chat-service는 사용자 간의 실시간 1:1 대화를 지원하는 STOMP 기반의 웹소켓 메시징 서비스입니다. 

MongoDB를 활용하여 대용량 채팅 내역을 저장하며, Redis를 이용해 사용자의 **접속 상태(Presence)** 를 실시간으로 관리합니다. 특히, 상대방의 접속 여부에 따라 실시간 전송과 **오프라인 푸시 알림(Kafka)** 을 지능적으로 분기 처리하여 시스템 효율성을 극대화했습니다.

## 📌 핵심 기능 (Key Features)
- **실시간 양방향 통신** : STOMP 프로토콜과 WebSocket을 활용한 저지연 메시지 전송 및 채팅방 생성.

- **알림 시스템** :

  - 상대방이 채팅방에 접속 중일 때 ➔ WebSocket으로 즉시 메시지 전달.
  - 상대방이 오프라인일 때 ➔ Kafka 이벤트를 발행하여 Notification-service를 통해 알림 발송.

- **접속 상태 관리** : Redis를 활용하여 특정 채팅방에 입장한 사용자의 세션을 실시간으로 트래킹.

- **읽음 처리** : 메시지 확인 시 읽음 상태를 비동기로 동기화.

- **채팅 내역 관리** : 쓰기 작업이 빈번한 채팅 데이터 특성에 맞춰 MongoDB 도입 및 커서 기반 페이징(Cursor Pagination) 구현.

## 🛠 기술 스택 (Tech Stack)
- **Language:** Java 21
- **Framework:** Spring Boot 3.5.5, Spring WebSocket (STOMP)
- **Database:** 
  - **MongoDB** : 채팅 메시지 및 채팅방 정보 저장
  - **Redis** : 실시간 채팅방 접속자 관리
- **Messaging** : Kafka (Producer)

## 🔗 의존성 (Dependencies)
이 서비스가 정상적으로 구동되기 위해서는 아래 요소들이 필요합니다.
### 1. 인프라 (Infrastructure)
| 미들웨어    | 목적                         |
|:--------|:---------------------------|
| MongoDB | 채팅방 메시지, 채팅방, 채팅 참여자 정보 저장 |
| Kafka   |  채팅 메시지 내용 발행  |


### 2. 연동 서비스 (Microservices)

| 통신 방식                        | 연동 서비스                 | 목적                                     |
|:-----------------------------|:-----------------------|:---------------------------------------|
| Discovery Service (Eureka)   | `All Services`         | 각 서비스를 한 곳에서 찾기 위함                     |
| Feign Client (Sync)          | `User-service`         | 초기 채팅방 생성 시에 사용자 이름 조회 목적              |
 | Kafka | `Notification-service` | 오프라인 시에만 메시지 알림을 전송하기 위해 비동기적으로 이벤트 발행 |

## 📡 API 및 이벤트 명세
| Method | URI                                  | 목적          |
| ------ | ------------------------------------ | ----------- |
| POST   | /v1/api/chat/rooms                   | 채팅방 생성      |
| GET    | /v1/api/chat/rooms/search            | 채팅방 검색      |
| GET    | /v1/api/chat/rooms/my-rooms/{userId} | 내 채팅방 목록 조회 |
| DELETE | /v1/api/chat/rooms/{roomId}          | 채팅방 나가기/삭제  |
| GET | /v1/api/chat/message/{roomId} | 채팅방 메시지 내역 조회|

## 💡 기술적 의사결정 (Technical Decision)
### 1. RDB 대신 MongoDB를 선택한 이유
**문제** : 채팅 서비스는 데이터의 스키마가 변경될 가능성이 적으나, 쓰기(Write) 작업이 매우 빈번하고 시간이 지날수록 데이터 양이 기하급수적으로 증가합니다.

**해결** : Sharding을 통한 수평 확장이 용이하고, 고속 쓰기 처리에 최적화된 **MongoDB(NoSQL)**를 도입했습니다.

**결과** : 대량의 메시지 유입 시에도 RDB 대비 안정적인 성능을 확보했습니다.

### 2. Redis를 활용한 '스마트 알림' 전략 (Redis for Presence)
**문제** : 사용자가 채팅방을 보고 있는데도(Online), "새 메시지가 도착했습니다"라는 알림(Push)이 오면 사용자 경험(UX)을 해칩니다.

**해결** : StompEventListener를 통해 사용자가 채팅방에 입장(SUBSCRIBE)하면 Redis에 세션을 저장하고, 퇴장(DISCONNECT)하면 삭제했습니다. 메시지 전송 시 Redis를 조회하여 "사용자가 방에 없을 때만" Kafka 이벤트를 발행했습니다.

**결과** : 불필요한 알림 발송을 방지하고, Notification 서비스의 부하를 줄였습니다.

### 3. STOMP Interceptor를 이용한 보안 처리
**문제** : WebSocket 연결은 HTTP 핸드셰이크 이후 TCP 연결로 전환되므로, 기존 HTTP 요청 필터(OncePerRequestFilter)로는 메시지 단위의 인증을 처리하기 어렵습니다.

**해결** : ChannelInterceptor를 구현한 StompHandler를 등록하여, STOMP 프로토콜의 CONNECT 프레임이 전송될 때 헤더에 포함된 JWT를 검증하도록 구현했습니다.

## ⚠️ 트러블 슈팅 (Troubleshooting)

