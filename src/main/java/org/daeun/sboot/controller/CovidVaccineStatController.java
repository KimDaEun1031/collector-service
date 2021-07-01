package org.daeun.sboot.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;
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
	public String covidVaccineStat(@RequestParam String month, String day, String sido) {
		Map<String, Object> result = new HashMap<String, Object>();

		String jsonInString = "";
		try {
			RestTemplate restTemplate = new RestTemplate();

			String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?"
					+ "page=1"
					+ "&perPage=18"
					+ "&cond%5BbaseDate%3A%3AEQ%5D=2021-"+ month +"-"+ day +"%2000%3A00%3A00"
					+ "&cond%5Bsido%3A%3AEQ%5D=" + URLEncoder.encode(sido, "UTF-8") + "&serviceKey=HGz5UDF80tY61L5yPZe3Ji96a0VZwzAzSwwlbvkRjxMAscm3dZybsbX2v4HlACe%2BBgRhZT2LpzY6VV9D6bjJyg%3D%3D";

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
			result.put("body", e.getStatusText());
			log.error(e.toString());

		} catch (Exception e) {
			result.put("statusCode", "999");
			result.put("body", "excpetion 오류");
			log.error(e.toString());
        }

		return jsonInString;
	}


	@GetMapping("/covidVaccineStatBatch")
	public String covidVaccineStatBatch(String total) {

		Map<String, Object> result = new HashMap<String, Object>();

		String jsonInString = "";
		try {
			RestTemplate restTemplate = new RestTemplate();

			String totalCount = covidVaccineStatTotalCount(total);
			log.info(totalCount);

			String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?perPage="+totalCount+"&serviceKey=HGz5UDF80tY61L5yPZe3Ji96a0VZwzAzSwwlbvkRjxMAscm3dZybsbX2v4HlACe%2BBgRhZT2LpzY6VV9D6bjJyg%3D%3D";
			log.info(url);

			HttpHeaders header = new HttpHeaders();
			HttpEntity<?> entity = new HttpEntity<>(header);

			log.info("get TotalData");

			ResponseEntity<Map> resultMap = restTemplate.exchange(URI.create(url), HttpMethod.GET, entity, Map.class);

			result.put("statusCode", resultMap.getStatusCodeValue());
			result.put("header", resultMap.getHeaders());
			result.put("body", resultMap.getBody());

			ObjectMapper mapper = new ObjectMapper();
			jsonInString = mapper.writeValueAsString(resultMap.getBody());

			log.info(jsonInString);

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			result.put("statusCode", e.getRawStatusCode());
			result.put("body", e.getStatusText());
			log.error(e.toString());

		} catch (Exception e) {
			result.put("statusCode", "999");
			result.put("body", "excpetion 오류");
			log.error(e.toString());
		}

		return jsonInString;
	}

	public String covidVaccineStatTotalCount(String total) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			RestTemplate restTemplate = new RestTemplate();

			String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?serviceKey=HGz5UDF80tY61L5yPZe3Ji96a0VZwzAzSwwlbvkRjxMAscm3dZybsbX2v4HlACe%2BBgRhZT2LpzY6VV9D6bjJyg%3D%3D";
			log.info(url);

			HttpHeaders header = new HttpHeaders();
			HttpEntity<?> entity = new HttpEntity<>(header);
			log.info("TotalCount getData");

			ResponseEntity<Map> resultMap = restTemplate.exchange(URI.create(url), HttpMethod.GET, entity, Map.class);

			result.put("statusCode", resultMap.getStatusCodeValue());
			result.put("header", resultMap.getHeaders());
			result.put("body", resultMap.getBody());

			Gson gson = new Gson();
			JsonParser jsonParser = new JsonParser();

			String jsonInString = gson.toJson(resultMap.getBody());
			JsonElement element = jsonParser.parse(jsonInString);
			JsonPrimitive primitive = (JsonPrimitive) element.getAsJsonObject().get("totalCount");
			total = gson.fromJson(primitive, String.class);
			log.info(total);

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			result.put("statusCode", e.getRawStatusCode());
			result.put("body", e.getStatusText());
			log.error(e.toString());

		} catch (Exception e) {
			result.put("statusCode", "999");
			result.put("body", "excpetion 오류");
			log.error(e.toString());
		}

		return total;
	}

}
