package org.daeun.sboot.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.print.DocFlavor.STRING;

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
public class RestTestController {

	@GetMapping("/covid_vaccine_stat")
	public String callAPI(@RequestParam String month, String day) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		String jsonInString = "";

		try {
			RestTemplate restTemplate = new RestTemplate();
			
			String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?"
					+ "page=1"
					+ "&perPage=18"
					+ "&cond%5BbaseDate%3A%3AEQ%5D=2021-"+ month +"-"+ day +"%2000%3A00%3A00"
					+ "&serviceKey=HGz5UDF80tY61L5yPZe3Ji96a0VZwzAzSwwlbvkRjxMAscm3dZybsbX2v4HlACe%2BBgRhZT2LpzY6VV9D6bjJyg%3D%3D";
			
			HttpHeaders header = new HttpHeaders();
			HttpEntity<?> entity = new HttpEntity<>(header);

			ResponseEntity<Map> resultMap = restTemplate.exchange(URI.create(url), HttpMethod.GET, entity, Map.class);			
			
			result.put("statusCode", resultMap.getStatusCodeValue());
            result.put("header", resultMap.getHeaders());
            result.put("body", resultMap.getBody());
            
            ObjectMapper mapper = new ObjectMapper();
            jsonInString = mapper.writeValueAsString(resultMap.getBody());
           
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			result.put("statusCode", e.getRawStatusCode());
            result.put("body"  , e.getStatusText());
            System.out.println(result);
	 
		} catch (Exception e) {
            result.put("statusCode", "999");
            result.put("body"  , "excpetion오류");
            System.out.println(e.toString());
        }
 
        return jsonInString;
	}
	

}
