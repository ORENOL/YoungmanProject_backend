package edu.pnu.specification;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import edu.pnu.domain.Receipt;

public class ReceiptSpecification {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public List<Receipt> findWithCustomQuery(String criteria, String criteriaValue) {
	    Query query = new Query();
	    query.addCriteria(Criteria.where(criteria).is(criteriaValue));
	    return mongoTemplate.find(query, Receipt.class);
	}

}
