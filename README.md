# 정산 시스템 프로젝트

📅 **2024년 6월 ~ 2024년 7월** (4주)

<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"><img src="https://img.shields.io/badge/Spring%20Batch-6DB33F?style=for-the-badge&logo=spring&logoColor=white"><img src="https://img.shields.io/badge/Spring Cloud-6DB33F?style=for-the-badge&logo= &logoColor=white"><img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white"><img src="https://img.shields.io/badge/JPA-59666C?style=for-the-badge&logo=Hibernate&logoColor=white"><img src="https://img.shields.io/badge/QueryDSL-0769AD?style=for-the-badge&logo=Java&logoColor=white"><img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"><img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white"><img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=Prometheus&logoColor=white"><img src="https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=Grafana&logoColor=white"><img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=GitHub Actions&logoColor=white">


## 📌 프로젝트 소개
- 대량(1억 건)의 영상 시청 기록에 대한 통계 및 정산 Batch 작업
- ### [**97.42%** 배치 작업 성능 개선](#performance-improvement)

## 🔥 프로젝트 목표
-  1억 건의 데이터에 대한 배치 작업을 2분대로 처리


## ⚒️기능 소개

### 1. 통계 및 정산 기능
- **플랫폼 스레드 활용**
- **chunk 동시성 제어**
- **Spring Batch/DB Partitioning**

### 2. 부하 분산 및 서비스 매핑 기능
- <details> <summary><b>Spring Cloud Gateway</b></summary> <ul> <li>중앙 집중식 인증 및 권한 부여, JWT 토큰 검증</li> <li>로드 밸런싱: 라운드 로빈 방식으로 트래픽 분산</li> </ul> </details> <details> <summary><b>Spring Cloud Eureka</b></summary> <ul> <li>Eureka 서비스 ID를 활용한 자동 서비스 매핑 <ul> <li>Eureka에 등록된 서비스 ID를 활용하여 요청을 자동으로 해당 서비스로 매핑</li> <li>streaming-service 멀티 프로세스를 동일한 serviceId로 매핑하여 효율적인 부하 분산</li> </ul> </li> <li>Eureka Server를 통한 서비스 디스커버리 <ul> <li>서비스 자동 등록 및 검색</li> <li>서비스 헬스 체크 및 실시간 상태 모니터링</li> </ul> </li> </ul> </details> <details> <summary><b>Streaming Service CQRS </b></summary> <ul> <li>CQRS (Command Query Responsibility Segregation) 패턴 적용 <ul> <li>명령(쓰기 작업)과 조회(읽기 작업)의 책임 분리</li>  </ul> </li> <li>DB Main-Replica 구조 구현 <ul> <li>Main DB: 쓰기 작업 전담, 데이터 일관성 보장</li> <li>Replica DB: 읽기 작업 전담, 조회 성능 최적화</li> <li>DB 간 ROW단위 실시간 동기화로 데이터 정합성 유지</li> </ul> </li> <li>트래픽 분산 및 가용성 향상 <ul> <li>읽기 작업의 부하를 Replica DB로 분산</li>  </ul> </li> </ul> </details>


3. 📚 API 명세서

   [![Postman API Documentation](https://img.shields.io/badge/Postman-API%20Documentation-orange?style=for-the-badge&logo=postman)](https://documenter.getpostman.com/view/27591971/2sA3XWdKBy)

## 🔍 아키텍처
![정산프로젝트 아키텍처3](https://github.com/user-attachments/assets/e8a2cd35-44b2-4e3d-aacc-69beb6342018)

<h2 id="performance-improvement">⚔️성능 개선</h2>

### 📊 최종 성능
✅ **1억 건 기준 실측 결과: 2m3s895ms**

### 📈 성능 개선 추이

| 단계 | 데이터 규모 | 처리 시간 | 개선율 |
|------|------------|-----------|--------|
| 최적화 전 | 5천만 건 | 40분+ | - |
| 1차 최적화 | 5천만 건 | 37분 12초 | 7%+ ↓ |
| 2차 최적화 | 5천만 건 | 10분 40초 | 73.33% ↓ |
| 3차 최적화 | 5천만 건 | 1분 1초 (추정)* | 97.42% ↓ |

- 3차 최적화 결과는 1억 건 기준 실측치를 바탕으로 5천만 건에 대해 선형적으로 추정한 값입니다.

### 🚀 주요 개선 포인트
1. **1차 최적화**: Spring Batch 파티셔닝 도입, Chunk 크기 최적화
2. **2차 최적화**: 데이터베이스 인덱싱, 쿼리 최적화
3. **3차 최적화**: JPA 제거, JDBC 직접 사용, 벌크 연산 적용

> 📚 **상세 개선 과정**  
> 자세한 내용은 [Notion](https://www.notion.so/9e7b94b212764f31b2f76cc9dc8a7a8f)에 있습니다