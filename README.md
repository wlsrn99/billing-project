# 정산 시스템 프로젝트

📅 **2024년 6월 ~ 2024년 7월** (4주)

<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"><img src="https://img.shields.io/badge/Spring%20Batch-6DB33F?style=for-the-badge&logo=spring&logoColor=white"><img src="https://img.shields.io/badge/Spring Cloud-6DB33F?style=for-the-badge&logo= &logoColor=white"><img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white"><img src="https://img.shields.io/badge/JPA-59666C?style=for-the-badge&logo=Hibernate&logoColor=white"><img src="https://img.shields.io/badge/QueryDSL-0769AD?style=for-the-badge&logo=Java&logoColor=white"><img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"><img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white"><img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=Prometheus&logoColor=white"><img src="https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=Grafana&logoColor=white"><img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=GitHub Actions&logoColor=white">


## 📌 프로젝트 소개

  <h2 align="center">
    <strong>🎥 대용량 시청기록에 대한 통계 및 정산 시스템 📊</strong>
  </h2>


---


### 🛠️ 기능

<table style="width: 100%;">
  <tr>
    <th style="width: 40%;">👥 User Service</th>
    <th style="width: 60%;">🎥 Streaming Service</th>
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
### 1.  1억 건의 데이터에 대한 배치 작업을 2분대로 처리
### 2.  멀티 스레드와 멀티 프로세스 환경에서 원할하게 서비스 동작

## 🏷️프로젝트 주요 경험 

### 1. 배치 작업 성능 개선 (97.42% 향상)
[📚 상세 개선 과정](https://www.notion.so/9e7b94b212764f31b2f76cc9dc8a7a8f)

<details>
<summary><strong>Quick Overview</strong></summary>

### 📊 최종 성능
**1억 건 기준 실측 결과: 2m3s895ms**

### 📈 성능 개선 추이

| 단계 | 데이터 규모 | 처리 시간 | 개선율 |
|------|------------|-----------|--------|
| 최적화 전 | 5천만 건 | 40분+ | - |
| 1차 최적화 | 5천만 건 | 37분 12초 | 7%+ ↓ |
| 2차 최적화 | 5천만 건 | 10분 40초 | 73.33% ↓ |
| 3차 최적화 | 5천만 건 | 1분 1초 (추정)* | 97.42% ↓ |

*3차 최적화 결과는 1억 건 기준 실측치를 바탕으로 5천만 건에 대해 선형적으로 추정한 값입니다.

### 🚀 주요 개선 포인트
1. **1차 최적화**: Spring Batch 파티셔닝 도입, Chunk 크기 최적화
2. **2차 최적화**: 데이터베이스 인덱싱, 쿼리 최적화
3. **3차 최적화**: JPA 제거, JDBC 직접 사용, 벌크 연산 적용

</details>



### 2. 통계 및 정산 최적화
- **플랫폼 스레드 활용**
- **Chunk 동시성 제어**
- **Spring Batch/DB Partitioning**

### 3. 부하 분산 및 서비스 매핑

<details>
<summary><strong>Spring Cloud Gateway</strong></summary>

- 중앙 집중식 인증 및 권한 부여, JWT 토큰 검증
- 로드 밸런싱: 라운드 로빈 방식으로 트래픽 분산

</details>

<details>
<summary><strong>Spring Cloud Eureka</strong></summary>

- Eureka 서비스 ID를 활용한 자동 서비스 매핑
   - Eureka에 등록된 서비스 ID를 활용하여 요청을 자동으로 해당 서비스로 매핑
   - streaming-service 멀티 프로세스를 동일한 serviceId로 매핑하여 효율적인 부하 분산
- Eureka Server를 통한 서비스 디스커버리
   - 서비스 자동 등록 및 검색
   - 서비스 헬스 체크 및 실시간 상태 모니터링

</details>

<details>
<summary><strong>Streaming Service CQRS</strong></summary>

- CQRS (Command Query Responsibility Segregation) 패턴 적용
   - 명령(쓰기 작업)과 조회(읽기 작업)의 책임 분리
- DB Main-Replica 구조 구현
   - Main DB: 쓰기 작업 전담, 데이터 일관성 보장
   - Replica DB: 읽기 작업 전담, 조회 성능 최적화
   - DB 간 ROW단위 실시간 동기화로 데이터 정합성 유지
- 트래픽 분산 및 가용성 향상
   - 읽기 작업의 부하를 Replica DB로 분산

</details>


## 🔍 아키텍처
![정산프로젝트 아키텍처3](https://github.com/user-attachments/assets/e8a2cd35-44b2-4e3d-aacc-69beb6342018)

## 📃 프로젝트 상세

### ⚙️️ 주요 기술 스택

![Java](https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.1-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud%20Gateway-4.1.4-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Spring Cloud Eureka](https://img.shields.io/badge/Spring%20Cloud%20Eureka-4.1.2-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8.8-02303A?style=flat-square&logo=gradle&logoColor=white)

### 📘 API 문서

[![Postman API Documentation](https://img.shields.io/badge/Postman-API%20Documentation-orange?style=for-the-badge&logo=postman)](https://documenter.getpostman.com/view/27591971/2sA3XWdKBy)

