package org.daeun.sboot;

import java.util.Arrays;
import java.util.List;

import org.daeun.sboot.repository.PersonRepository;
import org.daeun.sboot.vo.PersonVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


@SpringBootApplication
public class SbootApplication implements CommandLineRunner{
	
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private PersonRepository personRepository;
	
	

	public static void main(String[] args) {
		SpringApplication.run(SbootApplication.class, args);
	}
	

	@Override
	public void run(String... args) throws Exception {
		PersonVO person1 = new PersonVO("kimdaeun", "kim@do.com");
		PersonVO person2 = new PersonVO("kimheungtae", "kim@do.com");

		//mongoTemplate 사용한 방법
		//insert
		//mongoTemplate.insert(Arrays.asList(person1,person2), PersonVO.class);
		//get
//		Query query = new Query();
//		mongoTemplate.save(person1);
//		List<PersonVO> result = mongoTemplate.find(Query.query(Criteria.where(PersonVO.Email).is("kim@do.com")),  PersonVO.class);		
//		System.out.println(result);
		
		//레포지토리 사용한 방법 -> 간단함
		//삽입
		//personRepository.insert(Arrays.asList(person1,person2));
		//삭제
		//personRepository.deleteByNickname("kimdaeun");
		//조회
		List<PersonVO> result = personRepository.findByEmail("kim@do.com");
		System.out.println(result);
		//save
		//personRepository.saveAll(Arrays.asList(person1,person2));
		
	
		
	}
	
	
}
