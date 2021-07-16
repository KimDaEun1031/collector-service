1차 프로젝트 
=====
![1차 프로젝트](https://user-images.githubusercontent.com/73865700/121571245-b86f4000-ca5d-11eb-9030-665f88a5e27c.jpg)



Maven
=====
# Maven compile

./mvnw clean install



RESTFul API
============
# Test tool
https://meetup.toast.com/posts/107

# Spring annotations 
https://www.java67.com/2019/04/top-10-spring-mvc-and-rest-annotations-examples-java.html



MongoDb
=======
# Cloud 이용하기
https://ndb796.tistory.com/302

# Cluster 만들기
https://tjddnjs.tistory.com/17

# MongoDB 설치 & 서버 접속
https://somjang.tistory.com/entry/WindowsMongodb%EC%84%A4%EC%B9%98%ED%95%98%EA%B8%B0ver-420

# 기본 명령어
https://freekim.tistory.com/13

# SpringBoot + MongoDB
https://mommoo.tistory.com/79

https://ckddn9496.tistory.com/100

# Spring Boot 기반 Microservice Architecture 구현
![projectImage](https://user-images.githubusercontent.com/73865700/125883268-2207087b-09d3-42a2-8ea3-3b8f16fde041.png)
> NOTE

> + Covid19 예방접종 통계 Data를 MSA 기반으로 구축해본다.
> + Collector Service에서는 공공데이터 OpenAPI를 polling하여 Data를 수집한다.
> + 수집 방법은 호출(Get) 방법과 스케쥴링 기법(자정)으로 처리한다.
> + Collector Service는 Spring boot Project로 구현된다.
> + JPA repository로 DB(MongoDB)에 접근한다.
> + MSA는 서비스 별로 형성관리를 분리함으로 이번 Study에서 분리 개발했다.

## Collector Service Description
#### Project directory tree
```
.
├─ mvnw
├─ mvnw.cmd
├─ pom.xml
├─ src/main/java/org/daeun/collector
│       │                         ├─ CollectorApplication.java
│       │                         ├─ constants
│       │                         │     └─ Constants.java
│       │                         ├─ controller
│       │                         │     └─ CovidVaccineStatController.java
│       │                         ├─ scheduler
│       │                         │     └─ CovidVaccineStatScheduler.java
│       │                         └─ vo
│       │                               └─ CovidVaccineStatVO.java
│       └─ resources
│           └─ application.properties
│  
└─ target
     ├─classes
     ├─generated-sources ...
```



