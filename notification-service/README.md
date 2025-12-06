# [Notification-service] 

## 📝 소개 (Overview)
Notification-service는 시스템 내 발생하는 **모든 주요 이벤트(팔로우, 좋아요, 댓글, 채팅)를 수집**하여 사용자에게 **실시간 푸시 알림(FCM)** 을 발송하고, 알림 히스토리를 관리하는 중앙 집중형 서비스입니다. 

Kafka Consumer를 통해 비동기적으로 이벤트를 처리함으로써, 이벤트 발행 주체(User, Board, Chat Service)의 응답 속도에 영향을 주지 않으면서 안정적인 알림 발송을 보장합니다.

## 📌 핵심 기능 (Key Features)
- **이벤트 통합 수신** : Kafka를 통해 분산된 서비스들의 이벤트(팔로우, 댓글, 좋아요, 채팅)를 중앙에서 수집.

- **FCM 푸시 알림** : FCM을 연동하여 모바일 기기로 실시간 알림 전송.

- **알림 히스토리 관리** : 발송된 알림을 DB에 영구 저장하여 지난 알림 목록 조회 가능.

- **알림 상태 관리** : 알림 읽음 처리, 삭제, 읽지 않은 알림 개수 조회 기능.

- **커서 기반 페이징** : 대량의 알림 데이터 조회 시 성능 저하 방지를 위한 No-Offset 페이징 구현.

## 🛠 기술 스택 (Tech Stack)
- **Language:** Java 21
- **Framework:** Spring Boot 3.5.5, Querydsl
- **Database:** 
  - **RDB** : MySQL (알림 내역 저장)
  - **NoSQL** : Redis (FCM 기기 토큰 조회)
- **Messaging** : Kafka (Consumer)
- **Push Notification** : FCM

## 🔗 의존성 (Dependencies)
이 서비스가 정상적으로 구동되기 위해서는 아래 요소들이 필요합니다.
### 1. 인프라 (Infrastructure)
| 미들웨어  | 목적                         |
|:------|:---------------------------|
| MySQL | 알림 히스토리 저장                 |
| Redis | FCM 토큰 조회 |
| Kafka | FCM 알림 전송을 위한 Kafka 이벤트 처리 |
 

## 📡 API 및 이벤트 명세
### 1. API 명세
| Method | URI                                         | 목적       |
| ------ | ------------------------------------------- | -------- |
| GET    | /v1/api/notifications                       | 알림 목록 조회 |
| PATCH  | /v1/api/notifications/{notificationId}/read | 알림 읽음 처리 |
| DELETE | /v1/api/notifications/{notificationId}      | 알림 삭제    |

### 2. 이벤트 명세
| Topic Name | Group ID | Event Source    | 설명                                |
| :--- | :--- |:----------------|:----------------------------------|
| `following-topic` | `following-group` | *User-service*  | 누군가 나를 팔로우했을 때 알림                 |
| `comment-topic` | `comment-group` | *Board-service* | 내 게시글에 댓글이 달렸을 때 알림               |
| `like-topic` | `like-group` | *Board-service* | 내 게시글에 좋아요가 눌렸을 때 알림              |
| `chatting-topic` | `chatting-group` | *Chat-service*  |  상대방이 오프라인 상태여서 실시간 메시지를 못 받을 때 알림|

## 💡 기술적 의사결정 (Technical Decision)
### 1. 알림 서비스를 분리한 이유
**문제** : 만약 Board-service에서 댓글 작성 후 직접 FCM을 발송한다면, 구글 서버와의 통신 지연 시 댓글 작성 API 응답도 함께 느려집니다. 또한, 알림 로직 변경 시 모든 서비스를 수정해야 합니다.

**해결** : 알림 발송의 책임을 Notification-service로 위임하고 Kafka를 통해 비동기 처리했습니다.

**결과** : 댓글 작성 API는 0.1초 내에 응답하며, 알림은 백그라운드에서 별도로 발송되어 시스템 전체의 응답성(Latency)과 결합도를 개선했습니다.

### 2. 채팅 알림의 처리 전략
**전략** : 채팅 서비스(Chat-service)는 실시간성이 중요하므로 WebSocket을 우선 사용합니다.

**역할** : Notification-service는 채팅 서비스가 판단하기에 **"사용자가 현재 접속해 있지 않다"**고 판단하여 Kafka로 보낸 메시지만을 처리합니다. 이를 통해 중복 알림(앱을 보고 있는데 푸시가 오는 현상)을 방지했습니다.

## ⚠️ 트러블 슈팅 (Troubleshooting)

