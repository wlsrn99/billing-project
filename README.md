# 정산 시스템 프로젝트
**June 2024 ~ July 2024** 

<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"><img src="https://img.shields.io/badge/Spring%20Batch-6DB33F?style=for-the-badge&logo=spring&logoColor=white"><img src="https://img.shields.io/badge/Spring Cloud-6DB33F?style=for-the-badge&logo= &logoColor=white"><img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white"><img src="https://img.shields.io/badge/JPA-59666C?style=for-the-badge&logo=Hibernate&logoColor=white"><img src="https://img.shields.io/badge/QueryDSL-0769AD?style=for-the-badge&logo=Java&logoColor=white"><img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"><img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white"><img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=Prometheus&logoColor=white"><img src="https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=Grafana&logoColor=white"><img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=GitHub Actions&logoColor=white">


## 📌 프로젝트 소개
- 대량(1억 건)의 영상 시청 기록에 대한 통계 및 정산 Batch 작업
- 40분 이상에서 2m3s895ms로 97.42% 배치 성능 개선

## 🔥 프로젝트 목표
- 1억 건의 데이터에 대한 배치 작업을 2분대로 처리


## 🛠️ 주요 기능
1. 통계 및 정산 기능
 
    a. 플랫폼 스레드 활용
    
    b. [chunk read 동시성 제어]

    c. [DB Partitioning]



2. 부하 분산 및 서비스 매핑 기능 <details> <summary><b>Spring Cloud Gateway</b></summary> <ul> <li>중앙 집중식 인증 및 권한 부여, JWT 토큰 검증</li> <li>로드 밸런싱: 라운드 로빈 방식으로 트래픽 분산</li> </ul> </details> <details> <summary><b>Spring Cloud Eureka</b></summary> <ul> <li>Eureka 서비스 ID를 활용한 자동 서비스 매핑 <ul> <li>Eureka에 등록된 서비스 ID를 활용하여 요청을 자동으로 해당 서비스로 매핑</li> </ul> </li> <li>Eureka Server를 통한 서비스 디스커버리 <ul> <li>서비스 자동 등록 및 검색</li> <li>서비스 헬스 체크 및 실시간 상태 모니터링</li> </ul> </li> </ul> </details>


3. 📚 API 명세서

   [![Postman API Documentation](https://img.shields.io/badge/Postman-API%20Documentation-orange?style=for-the-badge&logo=postman)](https://documenter.getpostman.com/view/27591971/2sA3XWdKBy)

## 🔍 아키텍처
![정산프로젝트 아키텍처3](https://github.com/user-attachments/assets/e8a2cd35-44b2-4e3d-aacc-69beb6342018)

## ⚔️성능 개선

### 초기 상황
- 5천만 개 데이터 기준 통계 테이블 생성 시간: 약 40분
### 결과
- 1억 개 데이터 기준 통계+정산 테이블 생성 시간: 약 2분

<details>
<summary><b>1차 성능 개선</b></summary>

### 최적화 전략
1. Spring Batch 파티셔닝 도입
   - VideoId를 기준으로 데이터 파티셔닝
   - Chunk 크기 조정: 100 → 1,000

### 최적화 결과
| 작업 | 최적화 전 | 최적화 후 | 개선율 |
|------|-------|-----------|--------|
| 통계 작업 | 40분   | 22분 | 45% ↓ |
| 정산 작업 | 로그 에러 | 15분 | - |
| **총 소요 시간** | 40분+  | **37분 12초** | 7%+ ↓ |

</details>

<details>
<summary><b>2차 성능 개선</b></summary>

### 최적화 전략
1. 데이터베이스 레벨 최적화
   - 인덱스 생성: `CREATE INDEX idx_watch_history_date_video ON watch_history(created_at, video_id);`
   - 서브쿼리를 사용한 데이터 필터링 후 집계 수행

2. 쿼리 최적화
   ```sql
   SELECT new com.billing.entity.VideoStatistic(
       w.videoId, 
       w.createdAt, 
       COUNT(w.id), 
       SUM(w.adViewCount), 
       SUM(w.duration)
   ) 
   FROM (
       SELECT w.videoId, w.createdAt, w.id, w.adViewCount, w.duration
       FROM WatchHistory w 
       WHERE w.createdAt = :date 
         AND w.videoId BETWEEN :startVideoId AND :endVideoId
   ) w
   GROUP BY w.videoId, w.createdAt
   ```
   ### 최적화 결과
| 작업 | 최적화 전 | 1차 최적화 후 | 2차 최적화 후 | 최종 개선율 |
|------|-------|---------------|---------|-------------|
| 통계 + 정산 작업 (5천만 건) | 40분+  | 37분 12초 | 10분 40초 | 73.33% ↓ |
</details>

<details>
<summary><strong>3차 성능 개선</strong></summary>

### 최적화 전략

1. JPA 사용 제거
   - JDBC를 사용하는 방식으로 수정
   - chunkSize를 1000으로 설정
   - 스레드 안정성을 위해 JdbcPagingItemReader 사용

2. 파티션 프루닝 적용
   - SQL 쿼리에 파티션 지정: `FROM watch_history PARTITION(p:partitionDate) w`

3. 통계 로직 step의 processor 제거
   - 불필요한 메서드 호출 제거
   - 객체 생성 및 관리 비용 감소
   - 메모리 사용 감소

4. Writer에서 벌크 연산 사용
   - 여러 레코드를 하나의 SQL 문으로 삽입
   - 데이터베이스 호출 횟수 감소

### 성능 개선 결과
 ✅1억건 기준 2분 3초 895밀리초

| 작업                 | 최적화 전 | 1차 최적화 후 | 2차 최적화 후 | 3차 최적화 후  | 최종 개선율 |
|--------------------|-----------|---------------|---------------|-----------|-------------|
| 통계 + 정산 작업 (5천만 건) | 40분+ | 37분 12초 | 10분 40초 127밀리초 | 1분 1초(추정) | 97.42% ↓ |

3차 최적화 결과는 1억 건 기준 실측치를 바탕으로 5천만 건에 대해 선형적으로 추정한 값입니다.
최종 개선율은 최적화 전 시간(40분)과 3차 최적화 후 추정 시간을 비교하여 계산했습니다.

</details>

> 📚 **과정 상세 정보**  
> 자세한 내용은 [Notion](https://www.notion.so/9e7b94b212764f31b2f76cc9dc8a7a8f)에 있습니다