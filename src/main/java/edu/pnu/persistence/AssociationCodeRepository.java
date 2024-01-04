package edu.pnu.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.pnu.domain.AssociationCode;

public interface AssociationCodeRepository extends MongoRepository<AssociationCode, String>  {
	
}