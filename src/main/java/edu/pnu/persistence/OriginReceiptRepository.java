package edu.pnu.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.pnu.domain.OriginReceipt;

public interface OriginReceiptRepository extends MongoRepository<OriginReceipt, String>  {
}