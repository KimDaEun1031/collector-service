package org.daeun.collector.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.daeun.collector.constants.Constants.ODCLOUD_API_PERSIZE;

@RestController
@Slf4j
public class CovidVaccineStatController {

	@GetMapping("/covidVaccineStat")
	public String covidVaccineStat(@RequestParam int month, int day, String sido) {
		String jsonInString = "";
		try {
			RestTemplate restTemplate = new RestTemplate();

			String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?"
					+ "page=1"
					+ "&perPage=18"
					+ "&cond%5BbaseDate%3A%3AEQ%5D=2021-0"+ month +"-"+ day +"%2000%3A00%3A00"
					+ "&cond%5Bsido%3A%3AEQ%5D=" + sido + "&serviceKey=HGz5UDF80tY61L5yPZe3Ji96a0VZwzAzSwwlbvkRjxMAscm3dZybsbX2v4HlACe%2BBgRhZT2LpzY6VV9D6bjJyg%3D%3D";
			log.info("url = {}",url);

			HttpHeaders header = new HttpHeaders();
			HttpEntity<?> entity = new HttpEntity<>(header);

			ResponseEntity<Map> resultMap = restTemplate.exchange(URI.create(url), HttpMethod.GET, entity, Map.class);

            ObjectMapper mapper = new ObjectMapper();
			jsonInString = mapper.writeValueAsString(resultMap.getBody());

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error(e.toString());

		} catch (Exception e) {
			log.error(e.toString());
        }

		return jsonInString;
	}


	@GetMapping("/covidVaccineStatBatch")
	public String covidVaccineStatBatch() {

		String jsonInString = "";
		try {
			RestTemplate restTemplate = new RestTemplate();

			int total = 0;
			String totalCount = Integer.toString(covidVaccineStatTotalCount(total));
			log.info("totalCount = {}",totalCount);

			String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?perPage="+totalCount+"&serviceKey=HGz5UDF80tY61L5yPZe3Ji96a0VZwzAzSwwlbvkRjxMAscm3dZybsbX2v4HlACe%2BBgRhZT2LpzY6VV9D6bjJyg%3D%3D";
			log.info("url = {}",url);

			HttpHeaders header = new HttpHeaders();
			HttpEntity<?> entity = new HttpEntity<>(header);

			log.info("get TotalData");

			ResponseEntity<Map> resultMap = restTemplate.exchange(URI.create(url), HttpMethod.GET, entity, Map.class);

			Gson gson = new Gson();
			JsonParser jsonParser = new JsonParser();

			jsonInString = gson.toJson(resultMap.getBody());
			JsonElement element = jsonParser.parse(jsonInString);
			JsonArray arrayData = (JsonArray) element.getAsJsonObject().get("data");

			log.info("arrayData = {}",arrayData);

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error(e.toString());

		} catch (Exception e) {
			log.error(e.toString());
		}

		return jsonInString;
	}

	public int covidVaccineStatTotalCount(int total) {
		try {
			RestTemplate restTemplate = new RestTemplate();

			String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?serviceKey=HGz5UDF80tY61L5yPZe3Ji96a0VZwzAzSwwlbvkRjxMAscm3dZybsbX2v4HlACe%2BBgRhZT2LpzY6VV9D6bjJyg%3D%3D";
			log.info("url = {}",url);

			HttpHeaders header = new HttpHeaders();
			HttpEntity<?> entity = new HttpEntity<>(header);
			log.info("TotalCount getData");

			ResponseEntity<Map> resultMap = restTemplate.exchange(URI.create(url), HttpMethod.GET, entity, Map.class);

			Gson gson = new Gson();
			JsonParser jsonParser = new JsonParser();

			String jsonInString = gson.toJson(resultMap.getBody());
			JsonElement element = jsonParser.parse(jsonInString);
			JsonPrimitive primitive = (JsonPrimitive) element.getAsJsonObject().get("totalCount");
			total = Integer.parseInt(gson.fromJson(primitive, String.class));
			log.info("total = {}",total);


		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error(e.toString());

		} catch (Exception e) {
			log.error(e.toString());
		}

		return total;
	}


	public String readCovidVaccineStatFullData() {
		String jsonInString = "";

		try {
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
						+ "&serviceKey=HGz5UDF80tY61L5yPZe3Ji96a0VZwzAzSwwlbvkRjxMAscm3dZybsbX2v4HlACe%2BBgRhZT2LpzY6VV9D6bjJyg%3D%3D";

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

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error(e.toString());

		} catch (Exception e) {
			log.error(e.toString());
		}

		return jsonInString;
	}

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

	}

	public String readCovidVaccineStatTodayData() {

		String jsonInString = "";

		try {
			RestTemplate restTemplate = new RestTemplate();

			LocalDate nowDate = LocalDate.now();

			String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?"
					+ "&perPage=" +ODCLOUD_API_PERSIZE //상수
					+ "&cond%5BbaseDate%3A%3AEQ%5D="+ nowDate +"%2000%3A00%3A00"
					+ "&serviceKey=HGz5UDF80tY61L5yPZe3Ji96a0VZwzAzSwwlbvkRjxMAscm3dZybsbX2v4HlACe%2BBgRhZT2LpzY6VV9D6bjJyg%3D%3D";

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

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error(e.toString());

		} catch (Exception e) {
			log.error(e.toString());
		}

		return jsonInString;
	}

	@GetMapping("/searchCovidVaccineStatTodayData")
	public String searchCovidVaccineStatTodayData(@RequestParam String nowDate, @RequestParam String sido) {
		log.info("date = {}, sido = {}", nowDate, sido);

		String jsonInString = "";

		try {
			RestTemplate restTemplate = new RestTemplate();

			String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?"
					+ "&perPage=" +ODCLOUD_API_PERSIZE //상수
					+ "&cond%5Bsido%3A%3AEQ%5D=" + URLEncoder.encode(sido, "UTF-8")
					+ "&cond%5BbaseDate%3A%3AEQ%5D="+ nowDate +"%2000%3A00%3A00"
					+ "&serviceKey=HGz5UDF80tY61L5yPZe3Ji96a0VZwzAzSwwlbvkRjxMAscm3dZybsbX2v4HlACe%2BBgRhZT2LpzY6VV9D6bjJyg%3D%3D";

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

}
