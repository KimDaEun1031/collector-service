package org.daeun.sboot.repository;

import java.util.List;

import org.daeun.sboot.vo.PersonVO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonRepository extends MongoRepository<PersonVO, String>{
	List<PersonVO> deleteByNickname(String nickname);
	List<PersonVO> findByEmail(String email);
}
