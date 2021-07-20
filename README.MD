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
│       │                         └─ scheduler
│       │                               └─ CovidVaccineStatScheduler.java
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

## 3. REST API Server

#### REST API
|METHOD|PATH + PARAMETER|DESCRIPTION|
|------|----|-----------|
|GET|/searchCovidVaccineStatTodayData?nowDate=?&sido=?|오늘 데이터 검색 후 반환|

#### Controller - Search
RestTemplate의 exchange 메소드를 이용해 당일 데이터를 Search Service에 반환한다.

+ Search TodayData Code
```
@GetMapping("/searchCovidVaccineStatTodayData")
public String searchCovidVaccineStatTodayData(@RequestParam String nowDate, @RequestParam String sido) {
	log.info("date = {}, sido = {}", nowDate, sido);

	String jsonInString = "";

	try {
		RestTemplate restTemplate = new RestTemplate();

		String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?"
				+ "&perPage=" +ODCLOUD_API_PERSIZE
				+ "&cond%5Bsido%3A%3AEQ%5D=" + URLEncoder.encode(sido, "UTF-8")
				+ "&cond%5BbaseDate%3A%3AEQ%5D="+ nowDate +"%2000%3A00%3A00"
				+ "&serviceKey=[서비스키]";

		log.info("url = {}",url);

		HttpHeaders header = new HttpHeaders();
		HttpEntity<?> entity = new HttpEntity<>(header);

		log.info("get TodayData");

		ResponseEntity<Map> resultMap = restTemplate.exchange(URI.create(url), HttpMethod.GET, entity, Map.class);

		Gson gson = new Gson();
		jsonInString = gson.toJson(resultMap.getBody());

	} catch (HttpClientErrorException | HttpServerErrorException e) {
		log.error(e.toString());

	} catch (Exception e) {
		log.error(e.toString());
	}

	return jsonInString;
}
```
**INFO**  
코로나 백신 예방접종 통계 API를 이용한 Restful API Server

**HOST**  
localhost:9090

**PATH(GET)**  
/searchCovidVaccineStatTodayData

**PARAMETERS**  
1. nowDate  
	- in : query  
	- description : 오늘 날짜(기본값-오늘)  
	- type : string  

2. sido
	- in : query  
	- description : 지역명칭(기본값-전국)
	- type : string  


## 4. Controller - Base
RestTemplate의 exchange 메소드를 이용해 API Data를 호출하였다.

#### RestTemplate란?
+ spring 3.0 부터 지원
+ 접근 가능한 외부 HTPP URL를 호출하는 방법 중에 하나로 SpringFramework에서 제공해주는 모듈
+ HTTP 서버와의 통신을 단순화하고 Restful 원칙을 지킴(json, xml을 쉽게 응답 받음 / 동기)
+ 다른 호출방법으로 AsyncRestTemplate(비동기)와 WebClient(동기/비동기)가 존재

#### RestTemplate Method
![image](https://user-images.githubusercontent.com/73865700/126104985-c4a7fddd-8cc6-44c8-b0b4-6b15a98d96d3.png)

#### @RequestParam
+ @RequestParam은 1개의 HTTP 요청 파라미터를 받기 위해서 사용한다. @RequestParam은 필수 여부가 true이기 때문에 기본적으로 반드시 해당 파라미터가 전송되어야 한다.해당 파라미터가 전송되지 않으면 400 Error를 유발하게 된다. 그렇기 때문에 반드시 필요한 변수가 아니라면 required의 값을 false로 설정해둘 수 있으며 해당 Parameter를 사용하지 않고 요청을 보낼 경우에 default로 받을 값을 defaultValue 옵션을 통해 설정할 수도 있다.
+ @PathVariable과 병행하여 사용해도 상관 없다. REST API에서 값을 호출할 때 주로 많이 사용한다.

#### ResponseEntity
+ ResponseEntity는 HttpEntity 클래스를 상속받아 구현되었으며 사용자의 HttpRequest에 대한 응답 데이터를 포함하는 클래스이다. 따라서 HttpStatus, HttpHeaders, HttpBody를 포함한다.

#### Jackson(ObjectMapper) vs Gson
+ Jackson과 Gson 모두 Java에 대한 JSON 데이터 바인딩 지원을 제공하는 완전한 라이브러리이다. 
+ Jackson은 SpringFramework에 내장되어있지만 Gson은 pom.xml에 따로 추가해 줘야 한다.
![DFSF](https://user-images.githubusercontent.com/73865700/126108131-08ac158b-01c2-42a9-8aff-ef7a3d26574c.png)
+ 적은 데이터는 GSON, 대량 데이터는 JACKSON이 우수하다.
+ 참고 사이트 : https://cchoimin.tistory.com/entry/JAVA-JSON-%EB%8B%A4%EB%A3%A8%EA%B8%B0-%EC%A0%95%EB%A6%AC-JACKSON-ObjectMapper

#### Base Code
```
@GetMapping("/covidVaccineStat")
public String covidVaccineStat(@RequestParam int month, int day, String sido) {
	String jsonInString = "";
	try {
		RestTemplate restTemplate = new RestTemplate();

		String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?" 
				+ "page=1"
				+ "&perPage=18"
				+ "&cond%5BbaseDate%3A%3AEQ%5D=2021-0"+ month +"-"+ day +"%2000%3A00%3A00"
				+ "&cond%5Bsido%3A%3AEQ%5D=" + URLEncoder.encode(sido, "UTF-8") + "&serviceKey=[서비스키]"

		log.info("url = {}",url);

		HttpHeaders header = new HttpHeaders();
		HttpEntity<?> entity = new HttpEntity<>(header);

		ResponseEntity<Map> resultMap = restTemplate.exchange(URI.create(url), HttpMethod.GET, entity, Map.class);			

		ObjectMapper mapper = new ObjectMapper();
		jsonInString = mapper.writeValueAsString(resultMap.getBody());
		
		Gson gson = new Gson();
		jsonInString = gson.toJson(resultMap.getBody());

	} catch (HttpClientErrorException | HttpServerErrorException e) {
		log.error(e.toString());

	} catch (Exception e) {
		log.error(e.toString());
}

	return jsonInString;
}
```
+ 예시 URL : http://localhost:9090/covidVaccineStat?month=4&day=11&sido=서울특별시

#### URL
```
+ "page=1" + "&perPage=18"
```
page 첫번째부터 perpage는 18개를 호출한다.  
```
+ "&cond%5BbaseDate%3A%3AEQ%5D=2021-0"+ month +"-"+ day +"%2000%3A00%3A00"
```
url 창에서 입력한 month와 day에 맞는 값을 호출한다.
```
+ "&cond%5Bsido%3A%3AEQ%5D=" + URLEncoder.encode(sido, "UTF-8")
```
url 창에서 입력한 sido에 맞는 값을 인코딩하여 호출한다.

#### Http
```
HttpHeaders header = new HttpHeaders();
HttpEntity<?> entity = new HttpEntity<>(header);
```
호출 결과로 http status code, header 정보, body 정보를 확인하기 위해서 header와 httpEntity 객체를 생성한다.
httpEntity에는 header 정보를 포함한다.  

#### Change Code
+ 처음 코드
```
@Override
public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables)throws RestClientException {
	RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
	ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
	return nonNull(execute(url, method, requestCallback, responseExtractor, uriVariables));
}

UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString, HttpMethod.GET, entity, Map.class);
```
+ 바뀐 코드
```
@Override
public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
	RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
	ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
	return nonNull(execute(url, method, requestCallback, responseExtractor));
}

ResponseEntity<Map> resultMap = restTemplate.exchange(URI.create(url), HttpMethod.GET, entity, Map.class);
```
API URL이 이미 인코딩된 상태였다. UriComponents 클래스를 사용했을 때 String 타입인 url을 URI로 build시키는 과정에서 encode 해서 오류가 났다. 그래서 UriComponents 클래스를 사용하지 않고 실행을 해도 오류가 나서 찾아보니 RestTemplate은 기본적으로 org.springframework.web.util.DefaultUriTemplateHandler을 사용해서 String url을 자동으로 Encoding 하는 것을 알았다.  
변경 : uri.toString -> URI.create(url)
+ 오류 해결 사이트 : https://goni9071.tistory.com/296

## 4-1. Controller - Push
RestTemplate의 exchange 메소드를 이용해 API Data를 호출하고 DB로 Push한다.

#### Push FullData Code
```
RestTemplate restTemplate = new RestTemplate();

LocalDate startDate = LocalDate.of(2021, 03, 11);
LocalDate nowDate = LocalDate.now();

long betweenDays = ChronoUnit.DAYS.between(startDate, nowDate);

for (int i=0; i<betweenDays; i++) {
	LocalDate periodDate = startDate;
	startDate = startDate.plusDays(1);
	String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?"
			+ "&perPage=" +ODCLOUD_API_PERSIZE
			+ "&cond%5BbaseDate%3A%3AEQ%5D="+ periodDate +"%2000%3A00%3A00"
			+ "&serviceKey=[]";

	log.info("url = {}",url);

	HttpHeaders header = new HttpHeaders();
	HttpEntity<?> entity = new HttpEntity<>(header);

	log.info("get TodayData");

	ResponseEntity<Map> resultMap = restTemplate.exchange(URI.create(url), HttpMethod.GET, entity, Map.class);

	Gson gson = new Gson();
	JsonParser jsonParser = new JsonParser();

	jsonInString = gson.toJson(resultMap.getBody());
	JsonElement element = jsonParser.parse(jsonInString);
	JsonArray arrayData = (JsonArray) element.getAsJsonObject().get("data");
	log.info("arrayData = {} ", arrayData);

	sendCovidStat(arrayData);
}	
```
데이터가 들어간 처음 날짜와 오늘 날짜를 대조해 그 사이 day 수를 뽑아내서 for문을 돌려서 sendCovidStat 메소드에 값을 전달한다.

#### Push TodayData Code
```
RestTemplate restTemplate = new RestTemplate();

LocalDate nowDate = LocalDate.now();

String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?"
		+ "&perPage=" +ODCLOUD_API_PERSIZE //상수
		+ "&cond%5BbaseDate%3A%3AEQ%5D="+ nowDate +"%2000%3A00%3A00"
		+ "&serviceKey=[서비스키]";

log.info("url = {}",url);

HttpHeaders header = new HttpHeaders();
HttpEntity<?> entity = new HttpEntity<>(header);

log.info("get TodayData");

ResponseEntity<Map> resultMap = restTemplate.exchange(URI.create(url), HttpMethod.GET, entity, Map.class);

Gson gson = new Gson();
JsonParser jsonParser = new JsonParser();

jsonInString = gson.toJson(resultMap.getBody());
JsonElement element = jsonParser.parse(jsonInString);
JsonArray arrayData = (JsonArray) element.getAsJsonObject().get("data");
log.info("arrayData = {} ", arrayData);

sendCovidStat(arrayData);
```
오늘 날짜의 데이터를 sendCovidStat 메소드에 전달한다.

#### Send Code
```
public Object sendCovidStat(@RequestBody JsonArray arrayData) {

	log.info(String.valueOf(arrayData));
	try {
		RestTemplate restTemplate = new RestTemplate();

		String url = "http://localhost:9091/saveCovidVaccineStat";
		log.info("url = {}",url);

		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(arrayData.toString(),header);

		ResponseEntity<Object> response = restTemplate.postForEntity(url, entity, Object.class);

	} catch (HttpClientErrorException | HttpServerErrorException e) {
		log.error(e.toString());

	} catch (Exception e) {
		log.error(e.toString());
	}
	return arrayData;
```
FullData와 TodayData 메소드에서 받은 arrayData를 POST 방식으로 DB Service에 Push한다.

