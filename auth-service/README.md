# [Auth-service] 

## 📝 소개
이 모듈은 사용자 **회원가입, 로그인, 토큰 발급/갱신**을 담당하는 **인증(Authentication) 및 인가(Authorization)** 마이크로서비스입니다. Spring Security와 JWT를 기반으로 보안을 처리하며, Redis를 활용하여 토큰과 FCM 정보를 관리합니다.
## 📌 핵심 기능
- **회원가입**: 이메일 및 닉네임 중복 검증 후 사용자 생성 (Password Encoded)

- **로그인**: 사용자 검증 및 JWT(Access/Refresh) 토큰 발급

- **토큰 관리**: Redis를 이용한 Refresh Token 캐싱 및 만료 처리

- **FCM 토큰 관리**: 로그인 시 모바일 기기의 FCM 토큰 저장/갱신 (Redis)

- **로그아웃**: Redis에서 해당 유저의 Refresh Token 삭제

## 🛠 기술 스택 (Tech Stack)
- **Language:** Java 21
- **Framework:** Spring Boot 3.5.5
- **Database:** 
  - **RDB** : MySQL (사용자 정보)
  - **NoSQL** : Redis(RefreshToken, FCMToken 캐싱)
- **Security** : Spring Security, JWT

## 🔗 의존성 (Dependencies)

### 1. 인프라 (Infrastructure)
| 미들웨어  | 목적                             |
|:------|:-------------------------------|
| MySQL | 회원가입 및 로그인에 필요한 정보 저장          |
| Redis | RefreshToken, FCMToken 저장 및 관리 |


### 2. 연동 서비스 (Microservices)

| 통신 방식                        | 대상 서비스 | 목적 |
|:-----------------------------| :--- | :--- |
| Discovery Service (Eureka)   | `All Services` | 각 서비스를 한 곳에서 찾기 위함 |





## 📡 API 및 이벤트 명세
| Method | URI                                    | 목적        |
| ------ | -------------------------------------- | --------- |
| POST   | /v1/api/auth/sign-up                   | 회원가입      |
| POST   | /v1/api/auth/logout/{userId}           | 로그아웃      |
| GET    | /v1/api/auth/login                     | 로그인       |
| GET    | /v1/api/auth/check-nickname/{nickname} | 닉네임 중복 확인 |


## 💡 기술적 의사결정 (Technical Decision)
**Q. MSA 간 통신 효율성을 높인 방법**
- **문제:** 각 마이크로서비스에서 사용자 정보를 알기 위해 매번 `Auth-DB`를 조회하면 네트워크 지연과 DB 부하가 발생.
- **해결:** JWT Access Token의 Payload에 `userId`, `nickname` 등 자주 쓰는 데이터를 포함하여 발급.

## ⚠️ 트러블 슈팅 (Troubleshooting
