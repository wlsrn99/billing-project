# 정산 시스템 프로젝트

📅 **2024년 6월 ~ 2024년 7월**

<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"><img src="https://img.shields.io/badge/Spring%20Batch-6DB33F?style=for-the-badge&logo=spring&logoColor=white"><img src="https://img.shields.io/badge/Spring Cloud-6DB33F?style=for-the-badge&logo= &logoColor=white"><img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white"><img src="https://img.shields.io/badge/JPA-59666C?style=for-the-badge&logo=Hibernate&logoColor=white"><img src="https://img.shields.io/badge/QueryDSL-0769AD?style=for-the-badge&logo=Java&logoColor=white"><img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"><img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white"><img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=Prometheus&logoColor=white"><img src="https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=Grafana&logoColor=white"><img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=GitHub Actions&logoColor=white">

## 📌 프로젝트 소개

<div align="center">

# 대용량 시청기록에 대한 통계 및 정산 시스템

</div>

### 🛠️ 기능

<table style="width: 100%;">
  <tr>
    <th style="width: 40%;">User Service</th>
    <th style="width: 60%;">Streaming Service</th>
  </tr>
  <tr>
    <td>👤 회원 가입</td>
    <td>📹 동영상 관리: 등록, 재생, 정지</td>
  </tr>
  <tr>
    <td>🔐 로그인</td>
    <td>📊 동영상 통계 조회: 일간/주간/월간 Top 5 (조회수, 재생시간)</td>
  </tr>
  <tr>
    <td>🚪 로그아웃</td>
    <td>💰 동영상 정산 조회: 일간/주간/월간 조회</td>
  </tr>
</table>

## 🔥 프로젝트 목표
#### 1. 1억 건의 데이터에 대한 배치 작업을 2분대로 처리
#### 2. 멀티 스레드와 멀티 프로세스 환경에서 원활하게 서비스 동작

## 🏷️ 프로젝트 주요 경험
<div align="center">

### 1. 배치 작업 성능 개선 (97.42% 향상)

</div>


#### 1.1 최종 성능 📊
- #### 1억 건 기준 실측 결과: 2m3s895ms

#### 1.2  성능 개선 추이 📈

| 단계 | 데이터 규모 | 처리 시간 | 개선율 |
|------|------------|-----------|--------|
| 최적화 전 | 5천만 건 | 40분+ | - |
| 1차 최적화 | 5천만 건 | 37분 12초 | 7%+ ↓ |
| 2차 최적화 | 5천만 건 | 10분 40초 | 73.33% ↓ |
| 3차 최적화 | 5천만 건 | 1분 1초 (추정)* | 97.42% ↓ |

*3차 최적화 결과는 1억 건 기준 실측치를 바탕으로 5천만 건에 대해 선형적으로 추정한 값입니다.

#### 1.3  주요 개선 포인트 🚀
- **1차 최적화**: Spring Batch 파티셔닝 도입, Chunk 크기 최적화
- **2차 최적화**: 데이터베이스 인덱싱, 쿼리 최적화
- **3차 최적화**: JPA 제거, JDBC 직접 사용, 벌크 연산 적용

#### 1.4 [📚 성능 테스트 과정 상세 내용](https://delightful-rotate-bfd.notion.site/Spring-Batch-63fe19c8c43443669b11a500f1944703)

<br>
<div align="center">

### 2. 통계 및 정산 최적화

</div>

#### 2.1 플랫폼 스레드 활용
- 고성능 처리를 위한 최신 Java 플랫폼 스레드 기술 적용
#### 2.2 Chunk 동시성 제어
- 데이터 처리의 효율성 증대를 위한 Chunk 기반 동시성 관리
#### 2.3 Spring Batch Partitioning/DB Partitioning
- 대규모 데이터 처리를 위한 분산 처리 기법 도입

<br>

<div align="center">

### 3. 부하 분산 및 서비스 매핑

</div>

#### 3.1 Spring Cloud Gateway

- **중앙 집중식 인증 및 권한 부여**: JWT 토큰 기반의 보안 체계 구축
- **로드 밸런싱**: 라운드 로빈 방식을 통한 효율적인 트래픽 분산

#### 3.2 Spring Cloud Eureka

- **자동 서비스 매핑**
  - Eureka 서비스 ID 기반 동적 라우팅
  - 멀티 프로세스 환경에서의 효율적 부하 분산
- **서비스 디스커버리**
  - 자동화된 서비스 등록 및 검색 메커니즘
  - 실시간 헬스 체크 및 상태 모니터링

#### 3.3 Streaming Service CQRS
- **CQRS 패턴 적용**
  - 명령(쓰기)과 조회(읽기) 책임의 명확한 분리
- **DB Main-Replica 구조**

  | 구분 | 역할 | 특징 |
  |------|------|------|
  | Main DB | 쓰기 작업 전담 | 데이터 일관성 보장 |
  | Replica DB | 읽기 작업 전담 | 조회 성능 최적화 |

- **데이터 동기화**: ROW 단위 실시간 동기화로 정합성 유지
- **트래픽 분산**: Replica DB를 통한 읽기 작업 부하 분산 및 가용성 향상


### 🚨 트러블 슈팅
- [Spring Batch 멀티스레드 환경에서 메타데이터 충돌 문제](https://delightful-rotate-bfd.notion.site/Spring-Batch-8f58955119d6488089a039dc17f4e52a)
- [Spring Batch 메타데이터 테이블 초기화 문제](https://delightful-rotate-bfd.notion.site/Spring-Batch-42eb0fca09814619891d276b617ee056)
- [Spring Batch 데이터베이스 연결 풀 부족 문제](https://delightful-rotate-bfd.notion.site/Spring-Batch-f3771808430e401e8ef8ff29d238ae5b)
- [멀티모듈 CI MySQL 접속 불가 문제](https://delightful-rotate-bfd.notion.site/CI-a0f15147ac54407493c2f6fb8cfa4615)
- [Docker 컨테이너 간 MySQL 접속 불가 문제](https://delightful-rotate-bfd.notion.site/Docker-MySQL-d7d65a68b0b84f7199763f227db6849b)

### 📝기술적 의사 결정
- [Query DSL 도입](https://delightful-rotate-bfd.notion.site/QueryDSL-0744c34dbe304c72b37ba6b3ff2122d2)
- [인증 및 토큰 발급 로직 분리](https://delightful-rotate-bfd.notion.site/14f76b750a6045d4bcfa501a512d2fab)
- [비관적락 VS 낙관적락 VS 원자적 연산](https://delightful-rotate-bfd.notion.site/VS-VS-2281bac695a94c978bc3d00984b5076c)
## 🔍 아키텍처
![정산프로젝트 아키텍처3](https://github.com/user-attachments/assets/e8a2cd35-44b2-4e3d-aacc-69beb6342018)

## 📃 프로젝트 상세

### ⚙️️ 주요 기술 스택

![Java](https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.1-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud%20Gateway-4.1.4-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Spring Cloud Eureka](https://img.shields.io/badge/Spring%20Cloud%20Eureka-4.1.2-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8.8-02303A?style=flat-square&logo=gradle&logoColor=white)

### 📘 API 문서

[![Postman API Documentation](https://img.shields.io/badge/Postman-API%20Documentation-orange?style=for-the-badge&logo=postman)](https://documenter.getpostman.com/view/27591971/2sA3XWdKBy)

### 📙ERD
<img width="599" alt="스크린샷 2024-07-20 오후 5 15 10" src="https://github.com/user-attachments/assets/fcf36423-03c0-4d35-8691-2f3e9c0b79ec">
