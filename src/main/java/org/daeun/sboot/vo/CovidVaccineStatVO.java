package org.daeun.sboot.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "practiceCovid")
public class CovidVaccineStatVO {
	
	@Id
	private String id;
	
	private String baseDate;
	private String sido;
	private int firstCnt;
	private int secondCnt;
	private int totalFirstCnt;
	private int totalSecondCnt;
	private int accumulatedFirstCnt;
	private int accumulatedSecondCnt;


}
