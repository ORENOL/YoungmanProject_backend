package edu.pnu.persistence;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.pnu.domain.VerificationCode;


public interface VerificationCodeRepository extends MongoRepository<VerificationCode, String> {
	
	Optional<VerificationCode> findByCodeNumber(Integer codeNumber);

}
