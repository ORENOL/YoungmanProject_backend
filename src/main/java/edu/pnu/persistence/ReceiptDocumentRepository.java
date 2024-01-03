package edu.pnu.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.pnu.domain.ReceiptDocument;

public interface ReceiptDocumentRepository extends MongoRepository<ReceiptDocument, String>  {
	
}