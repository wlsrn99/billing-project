# 정산 프로젝트 ( BillingProject )
**June 2024 ~ July 2024** 

<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white">
<img src="https://img.shields.io/badge/Spring%20Batch-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/Spring Cloud-6DB33F?style=for-the-badge&logo= &logoColor=white"> 
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white">
<img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=Prometheus&logoColor=white">
<img src="https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=Grafana&logoColor=white">
<img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=GitHub Actions&logoColor=white">
<br>

## 📌 프로젝트 소개
- 대량(1억건)의 영상 시청기록에 대한 통계 및 정산 Batch 작업


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

