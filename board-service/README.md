# [Board-service] 

## 📝 소개
Board-service는 사용자가 위스키에 대한 경험을 공유하는 게시판 기능과, 개인화된 **타임라인(Feed)** 을 제공하는 마이크로서비스입니다.

게시글/댓글 작성과 같은 기본적인 기능뿐만 아니라, Kafka Consumer를 통해 사용자 간의 팔로우 관계와 즐겨찾기 데이터를 실시간으로 동기화하여 "내가 팔로우한 사람들의 게시글" 등을 조회할 때 성능을 최적화했습니다.
## 📌 핵심 기능
- **게시글 관리** : 위스키 관련 게시글 작성(이미지 업로드 포함), 조회, 삭제.

- **커서 기반 페이징 (Cursor Pagination)** : 대용량 데이터 환경에서도 빠른 속도를 보장하기 위해 No-Offset 방식의 무한 스크롤 구현.

- **개인화 피드** :
  - **팔로잉 피드** : 내가 팔로우한 사용자들의 게시글 모아보기.
  - **즐겨찾기 피드** : 내가 즐겨찾기한 위스키와 관련된 게시글 모아보기.

- **상호작용** : 게시글 좋아요 및 댓글 기능.

- **이벤트 기반 통신** : 댓글/좋아요 발생 시 Kafka 이벤트를 발행하여 알림 서비스(Notification)로 전파.

## 🛠 기술 스택 (Tech Stack)
- **Language:** Java 21
- **Framework:** Spring Boot 3.5.5
- **Database:** : MySQL 
- **Messaging** : Kafka (Producer & Consumer) 
- **Storage** : AWS S3

## 🔗 의존성 (Dependencies)

### 1. 인프라 (Infrastructure)
| 미들웨어  | 목적                                    |
|:------|:--------------------------------------|
| MySQL | 회원가입 및 로그인에 필요한 정보 저장                 |
| Kafka | 팔로우,즐겨찾기 이벤트 변경사항 감지 / 댓글 및 좋아요 알림 발행 |


### 2. 연동 서비스 (Microservices)
 통신 방식                        | 연동 서비스                 | 목적                 |
|:-----------------------------|:-----------------------|:-------------------|
| Discovery Service (Eureka)   | `All Services`         | 각 서비스를 한 곳에서 찾기 위함 |
| Kafka | `Notification-service` | 댓글 및 좋아요 알림 발행     |
| Kafka | `User-service`         | 팔로우 및 즐겨찾기 정보 수신   |





## 📡 API 및 이벤트 명세
### 1. API 명세
#### - Boards
| Method | URI                              | 목적                 |
| ------ | -------------------------------- | ------------------ |
| POST   | /v1/api/boards                   | 게시글 생성             |
| POST   | /v1/api/boards/{boardId}         | 게시글 수정 및 관련 작업     |
| GET    | /v1/api/boards/{boardId}         | 게시글 상세 조회          |
| GET    | /v1/api/boards/followings        | 팔로잉한 유저 게시글 목록 조회  |
| GET    | /v1/api/boards/favorite-whiskies | 즐겨찾는 위스키 관련 게시글 조회 |

#### - Comments
| Method | URI              | 목적    |
| ------ | ---------------- | ----- |
| POST   | /v1/api/comments | 댓글 작성 |

### 2. 이벤트 명세
#### - 구독
| Topic           | Group ID        | Event Class              | 설명                                           |
| --------------- | --------------- | ------------------------ | -------------------------------------------- |
| following-topic | following-group | FollowEvent              | 팔로우/언팔로우 발생 시 `BoardUserFollow` 테이블에 동기화     |
| favorites-topic | favorites-group | UserFavoritesWhiskyEvent | 위스키 즐겨찾기 변경 시 `UserWhiskyFavorites` 테이블에 동기화 |

#### - 발행
| Topic         | Event Class  | 설명               |
| ------------- | ------------ | ---------------- |
| comment-topic | CommentEvent | 게시글에 댓글 작성 시 발행  |
| like-topic    | LikeEvent    | 게시글에 좋아요 클릭 시 발행 |


## 💡 기술적 의사결정 (Technical Decision)
**Q. MSA 간 통신 효율성을 높인 방법**
- **문제:** 각 마이크로서비스에서 사용자 정보를 알기 위해 매번 `Auth-DB`를 조회하면 네트워크 지연과 DB 부하가 발생.
- **해결:** JWT Access Token의 Payload에 `userId`, `nickname` 등 자주 쓰는 데이터를 포함하여 발급.

## ⚠️ 트러블 슈팅 (Troubleshooting
