package org.daeun.sboot.controller;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.daeun.sboot.repository.CovidVaccineStatRepository;
import org.daeun.sboot.vo.CovidVaccineStatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResizableByteArrayOutputStream;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@Slf4j
public class CovidVaccineStatController {

	@Autowired
	private CovidVaccineStatRepository repository;

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

			Gson gson = new Gson();
			JsonParser jsonParser = new JsonParser();

			jsonInString = gson.toJson(resultMap.getBody());
			JsonElement element = jsonParser.parse(jsonInString);
			JsonArray arrayData = (JsonArray) element.getAsJsonObject().get("data");


			log.info(String.valueOf(arrayData));

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


	@GetMapping("/covidVaccineStatRow")
	public String covidVaccineStatRow(@RequestParam(required = false, defaultValue = "18") int perSize,
									  @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDate).now()}") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate baseDate) {

		Map<String, Object> result = new HashMap<String, Object>();

		String jsonInString = "";
		try {
			RestTemplate restTemplate = new RestTemplate();

			LocalDate startDate = LocalDate.of(2021, 03, 11);
			LocalDate nowDate = LocalDate.now();

			long betweenDays = ChronoUnit.DAYS.between(startDate, nowDate);

			for (int i=0; i<1; i++) {
				baseDate = startDate;
				startDate = startDate.plusDays(1);
				String url = "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?"
						+ "&perPage=" +perSize
						+ "&cond%5BbaseDate%3A%3AEQ%5D="+ baseDate +"%2000%3A00%3A00"
						+ "&serviceKey=HGz5UDF80tY61L5yPZe3Ji96a0VZwzAzSwwlbvkRjxMAscm3dZybsbX2v4HlACe%2BBgRhZT2LpzY6VV9D6bjJyg%3D%3D";

				log.info(url);

				HttpHeaders header = new HttpHeaders();
				HttpEntity<?> entity = new HttpEntity<>(header);

				log.info("get TodayData");

				ResponseEntity<Map> resultMap = restTemplate.exchange(URI.create(url), HttpMethod.GET, entity, Map.class);

				result.put("statusCode", resultMap.getStatusCodeValue());
				result.put("header", resultMap.getHeaders());
				result.put("body", resultMap.getBody());

				Gson gson = new Gson();
				JsonParser jsonParser = new JsonParser();

				jsonInString = gson.toJson(resultMap.getBody());
				JsonElement element = jsonParser.parse(jsonInString);
				JsonArray arrayData = (JsonArray) element.getAsJsonObject().get("data");

				sendCovidStat(arrayData);

				/*List<CovidVaccineStatVO> rowList = new ArrayList<>();

				for(int j=0; j<arrayData.size(); j++) {
					JsonObject row = (JsonObject) arrayData.get(j);

					CovidVaccineStatVO covidVO = gson.fromJson(row, CovidVaccineStatVO.class);
					rowList.add(covidVO);

				}

				repository.insert(rowList);*/

			}

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

//	practice send json
	public Object sendCovidStat(@RequestBody JsonArray arrayData) {
		MultiValueMap<String, Object> result = new LinkedMultiValueMap<String, Object>();
		result.add("arrayData", arrayData.toString());

		Gson gson = new Gson();
		JsonParser jsonParser = new JsonParser();

		log.info(String.valueOf(arrayData));
		try {
			RestTemplate restTemplate = new RestTemplate();

			String url = "http://localhost:9091/saveCovidVaccineStat";
			log.info(url);

			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<?> entity = new HttpEntity<>(result,header);
			log.info(entity.toString());

			ResponseEntity<Response> response = restTemplate.postForEntity(url, entity, Response.class);
//			ResponseEntity<MultiValueMap> resultMap = restTemplate.exchange(URI.create(url), HttpMethod.POST, entity, MultiValueMap.class);

			result.add("body", response.getBody());

//			result.put("statusCode", resultMap.getStatusCodeValue());
//			result.put("header", resultMap.getHeaders());
//			result.put("body", resultMap.getBody());

			log.info(String.valueOf(result));







		} catch (HttpClientErrorException | HttpServerErrorException e) {
//			result.put("statusCode", e.getRawStatusCode());
//			result.put("body", e.getStatusText());
			log.error(e.toString());

		} catch (Exception e) {
			log.error(e.toString());
		}
		return arrayData;

	}

}
