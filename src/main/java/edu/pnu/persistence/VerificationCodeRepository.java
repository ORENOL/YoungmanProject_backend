package edu.pnu.persistence;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.pnu.domain.Code;


public interface CodeRepository extends MongoRepository<Code, String> {
	
	Optional<Code> findByCodeNumber(Integer codeNumber);

}
