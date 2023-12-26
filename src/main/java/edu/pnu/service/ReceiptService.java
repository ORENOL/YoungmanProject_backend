package edu.pnu.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import edu.pnu.domain.Receipt;
import edu.pnu.persistence.ReceiptRepository;

@Service
public class ReceiptService {
	
	private MongoTemplate mongoTemplate;

	private ReceiptRepository receiptRepo;
	
	public ReceiptService(ReceiptRepository receiptRepo, MongoTemplate mongoTemplate) {
		this.receiptRepo = receiptRepo;
		this.mongoTemplate = mongoTemplate;
	}

	public ResponseEntity<?> getPageReceipt(int pageNo, int pageSize, String orderCriteria, String searchCriteria, String searchWord) {
		
		Sort sort = Sort.by(Sort.Order.desc(orderCriteria));
		PageRequest pageable = PageRequest.of(pageNo, pageSize, sort);
		System.out.println(searchWord);
		if (searchWord == null) {
			Page<Receipt> page = receiptRepo.findAll(pageable);
			return ResponseEntity.ok(page);
		}
		
		Query query = new Query();
		query.with(pageable);
		long total = mongoTemplate.count(query, Receipt.class);
		
		if (searchCriteria.equals("companyName") || searchCriteria.equals("item")) {
		    query.addCriteria(Criteria.where(searchCriteria).regex(searchWord, "i"));
	        List<Receipt> list = mongoTemplate.find(query, Receipt.class);
		    return ResponseEntity.ok(new PageImpl<>(list, pageable, total)); 
		}
		
		if (searchCriteria.equals("quantity") || searchCriteria.equals("unitPrice") || searchCriteria.equals("price")) {
			int number;
			
			try {
				number = Integer.parseInt(searchWord);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.internalServerError().body("error occurs: " + e.getMessage());
			}
			
			query.addCriteria(Criteria.where(searchCriteria).gte(number));
	        List<Receipt> list = mongoTemplate.find(query, Receipt.class);
		    return ResponseEntity.ok(new PageImpl<>(list, pageable, total));
		}
		
		if (searchCriteria.equals("tradeDate") || searchCriteria.equals("createDate")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate;
			Date endDate;
			
			try {
				String[] parts = searchWord.split("~");
			    startDate = format.parse(parts[0]);
			    endDate = format.parse(parts[1]);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.internalServerError().body("error occurs: " + e.getMessage());
			}
	        query.addCriteria(Criteria.where(searchCriteria).gte(startDate).lte(endDate));
	        List<Receipt> list = mongoTemplate.find(query, Receipt.class);
		    return ResponseEntity.ok(new PageImpl<>(list, pageable, total));
		}
		
		
		return null;
		
		
	}

	public ResponseEntity<?> saveReceipt(Receipt receipt) {
		try {
			System.out.println(receipt.toString());
			receiptRepo.save(receipt);
			return ResponseEntity.ok("save success");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("error occurs: " + e.getMessage());
		}
		
	}

	public ResponseEntity<?> deleteBoard(String receiptId) {
		
		try {
			Optional<Receipt> receipt = receiptRepo.findById(receiptId);
			
			if (receipt.isPresent()) {
				receiptRepo.deleteById(receiptId);
				return ResponseEntity.ok("delete success");
			} else {
				return ResponseEntity.unprocessableEntity().body("not exist receipt");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.internalServerError().body("unexpected Error");
		
	}

	public ResponseEntity<?> getAllReceipt() {
		return ResponseEntity.ok(receiptRepo.findAll());
	}

	public ResponseEntity<?> searchByStringReceipt(String criteria, String value) {
	    Query query = new Query();
	    query.addCriteria(Criteria.where(criteria).regex(value, "i"));
	    return ResponseEntity.ok(mongoTemplate.find(query, Receipt.class)); 
	}
	
}
