package org.daeun.sboot.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@Slf4j
public class CovidVaccineStatController {

	@GetMapping("/covidVaccineStat")
	public String callAPI(@RequestParam String month, String day, String sido) {
		Map<String, Object> result = new HashMap<String, Object>();

		String test = "";
		try {
			RestTemplate restTemplate = new RestTemplate();

			//포맷으로 다시 정리
			//복사해서 만들기

			String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?"
					+ "page=1"
					+ "&perPage=18"
					+ "&cond%5BbaseDate%3A%3AEQ%5D=2021-"+ month +"-"+ day +"%2000%3A00%3A00"
					+ "&cond%5Bsido%3A%3AEQ%5D=" + URLEncoder.encode(sido, "UTF-8") + "&serviceKey=HGz5UDF80tY61L5yPZe3Ji96a0VZwzAzSwwlbvkRjxMAscm3dZybsbX2v4HlACe%2BBgRhZT2LpzY6VV9D6bjJyg%3D%3D";
			//로그 쓰기
			HttpHeaders header = new HttpHeaders();
			HttpEntity<?> entity = new HttpEntity<>(header);
			//로그로 데이터를 가져왔습니다라고 쓸듯
			ResponseEntity<Map> resultMap = restTemplate.exchange(URI.create(url), HttpMethod.GET, entity, Map.class);			
			
			result.put("statusCode", resultMap.getStatusCodeValue());
            result.put("header", resultMap.getHeaders());
            result.put("body", resultMap.getBody());

            ObjectMapper mapper = new ObjectMapper();
            test = mapper.writeValueAsString(resultMap.getBody());
            //스트링 로그로
           
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			result.put("statusCode", e.getRawStatusCode());
			result.put("body", e.getStatusText());

		} catch (Exception e) {
			result.put("statusCode", "999");
			result.put("body", "excpetion 오류");
			log.info(e.toString());
        }

		return test;
	}



}
