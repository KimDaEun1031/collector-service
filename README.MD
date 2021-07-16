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
![collector](https://user-images.githubusercontent.com/73865700/125903094-0fc1b3a4-1c0a-49bf-8afa-fa57fa1e8161.png)
> NOTE

> + Covid19 예방접종 통계 Data를 MSA 기반으로 구축해본다.
> + Service는 Spring boot Project로 구현된다.
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

Collector Service는 공공데이터 OpenAPI를 polling하여 Data를 수집한다.  
수집 방법은 호출(Get) 방법과 스케쥴링 기법(자정)으로 처리한다.  
> + 참고 사이트들을 블로그에 올려놓았다.  
> https://relaxed-it-study.tistory.com/category/JAVA/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8

## 1. Dependency
MongoDB와 Spring Web, Gson, Lombok를 추가한다.
Gson을 제외하고는 Spring boot를 설치할 때 나오는 dependency에서 추가할 수 있다.

#### pom.xml
```
       <parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.5.0</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
     
	<groupId>org.daeun</groupId>
	<artifactId>collector</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>collector</name>
	<description>Demo project for Spring Boot</description>
     
	<properties>
		<java.version>1.8</java.version>
	</properties>
     
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-mongodb</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	 	<dependency> 
		 <groupId>org.springframework.boot</groupId> 
		 <artifactId>spring-boot-starter-web</artifactId> 
	 	</dependency>
	 	<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web-services</artifactId>
		</dependency>
		<dependency>
			<groupId>com.google.code.gson</groupId>
			<artifactId>gson</artifactId>
			<version>2.8.5</version>
		</dependency>
		<dependency>
		    <groupId>org.projectlombok</groupId>
		    <artifactId>lombok</artifactId>
		    <version>1.18.2</version>
		    <scope>provided</scope>
		</dependency>
		<dependency>
		    <groupId>org.apache.httpcomponents</groupId>
		    <artifactId>httpclient</artifactId>
		    <version>4.5.13</version>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
```

## 2. Configuration
application.yml에서는 application의 port를 입력한다.
```
server.port=9090
```

## 3. REST API Server 구축

PARAMETER에는 서비스키를 추가해야한다. 

#### REST API
|METHOD|PATH + PARAMETER|DESCRIPTION|
|------|----|-----------|
|GET|/15077756/v1/vaccine-stat?cond%5BbaseDate%3A%3AEQ%5D="+ nowDate +"%2000%3A00%3A00|





