# [User-service] 

## 📝 소개 (Overview)
User-service는 **사용자의 상세 프로필 정보를 관리**하고, **팔로우/팔로잉(소셜 그래프) 및 위스키 즐겨찾기** 기능을 담당하는 마이크로서비스입니다. 

회원가입 시 **Auth-service와 통신**하여 인증 정보를 생성하고, 사용자 활동(팔로우, 즐겨찾기) 발생 시 Kafka 이벤트를 발행하여 타 서비스(Board, Notification)에 비동기적으로 데이터 변경 사항을 전파합니다.

## 📌 핵심 기능 (Key Features)
- **회원가입 오케스트레이션** : 사용자 요청을 받아 Auth-service에 인증 정보(Email/Password) 생성을 요청하고, 성공 시 프로필 데이터(User Entity) 생성.

- **취향 분석** : 회원가입 직후에 사용자 선호도 설문 조사 저장 및 관리.
- **사용자 조회** : 사용자의 프로필(팔로잉/팔로워 수, 맞팔 여부, 인적사항) 조회.

- **소셜 네트워크** : 사용자 간 팔로우/언팔로우 기능 및 카운트 관리.

- **위스키 즐겨찾기** : Whisky-service와 통신하여 위스키 정보를 조회하고 내 보관함(즐겨찾기)에 추가/삭제.

- **이벤트 발행**: 팔로우 및 즐겨찾기 액션 발생 시 Kafka를 통해 이벤트 발행 (Notification, Board 서비스 연동).

## 🛠 기술 스택 (Tech Stack)
- **Language:** Java 21
- **Framework:** Spring Boot 3.5.5, Spring Cloud (OpenFeign), Querydsl
- **Database:** MySQL (JPA)
- **Messaging** : Kafka (Producer)

## 🔗 의존성 (Dependencies)
이 서비스가 정상적으로 구동되기 위해서는 아래 요소들이 필요합니다.
### 1. 인프라 (Infrastructure)
| 미들웨어  | 목적                  |
|:------|:--------------------|
| MySQL | 사용자 정보 및 프로필 데이터 저장 |
| Kafka | 팔로우 / 즐겨찾기 이벤트 발행   |


### 2. 연동 서비스 (Microservices)

| 통신 방식                        | 연동 서비스           | 목적 |
|:-----------------------------|:-----------------| :--- |
| Discovery Service (Eureka)   | `All Services`   | 각 서비스를 한 곳에서 찾기 위함 |
| Feign Client (Sync)          | `Auth-service`   | 회원 가입 시 계정 생성 위임 (Register User) |
| Feign Client (Sync)          | `Whisky-service` | 즐겨찾기 추가 시 위스키 상세 정보 조회 |
 | Kafka | `Notification-service`| 팔로잉 알림을 전송하기 위해 비동기적으로 이벤트 발행 |
 | Kafka | `Board-service`| 팔로잉한 사용자의 게시물 / 즐겨찾기한 위스키 변경사항 업데이트를 위해 이벤트 발행| 

## 📡 API 및 이벤트 명세
| Method | URI                            | 목적             |
| ------ | ------------------------------ | -------------- |
| POST   | /v1/api/users/sign-up          | 유저 회원가입        |
| GET    | /v1/api/users/{userId}         | 유저 상세 정보 조회    |
| PATCH  | /v1/api/users/preferences      | 유저 취향/선호 정보 수정 |
| POST   | /v1/api/users/follows          | 유저 팔로우         |
| DELETE | /v1/api/users/unfollows        | 유저 언팔로우        |
| POST   | /v1/api/users/whisky-favorites | 위스키 즐겨찾기 추가    |
| DELETE | /v1/api/users/whisky-favorites | 위스키 즐겨찾기 삭제    |


## ⚠️ 트러블 슈팅 (Troubleshooting)

