package edu.pnu.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.pnu.domain.Receipt;

public interface ReceiptRepository extends MongoRepository<Receipt, Long> {
}