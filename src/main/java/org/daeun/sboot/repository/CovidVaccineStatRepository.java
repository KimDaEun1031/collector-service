package org.daeun.sboot.repository;

import org.daeun.sboot.vo.CovidVaccineStatVO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CovidVaccineStatRepository extends MongoRepository<CovidVaccineStatVO, String>{

	List<CovidVaccineStatVO> findByBaseDate(String baseDate);
	List<CovidVaccineStatVO> findByBaseDateOrSido(String baseDate, String sido);
}
