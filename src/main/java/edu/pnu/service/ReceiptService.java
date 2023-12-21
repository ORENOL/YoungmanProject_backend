package edu.pnu.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import edu.pnu.domain.Receipt;
import edu.pnu.persistence.ReceiptRepository;

@Service
public class ReceiptService {

	private ReceiptRepository receiptRepo;
	
	public ReceiptService(ReceiptRepository receiptRepo) {
		this.receiptRepo = receiptRepo;
	}

	public Page<Receipt> getPageReceipt(int pageNo, int pageSize, String orderCriteria) {
		Sort sort = Sort.by(Sort.Order.desc(orderCriteria));
		Page<Receipt> page = receiptRepo.findAll(PageRequest.of(pageNo, pageSize, sort));
		return page;
	}

	public void saveReceipt(Receipt receipt) {
		receiptRepo.save(receipt);
	}

	public ResponseEntity<?> deleteBoard(Long receiptId) {
		
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

	
}
