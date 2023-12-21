package edu.pnu.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import edu.pnu.domain.Receipt;
import edu.pnu.persistence.ReceiptRepository;

@Service
public class ReceiptService {

	private ReceiptRepository receiptRepo;
	
	public ReceiptService(ReceiptRepository receiptRepo) {
		this.receiptRepo = receiptRepo;
	}

	public Page<Receipt> getOnePageReceipt(int pageNo, int pageSize, String orderCriteria) {
		Sort sort = Sort.by(Sort.Order.desc(orderCriteria));
		Page<Receipt> page = receiptRepo.findAll(PageRequest.of(pageNo, pageSize, sort));
		return page;
	}

	public void saveReceipt(Receipt receipt) {
		receiptRepo.save(receipt);
	}

	public void deleteBoard(Receipt receipt) {
		receiptRepo.delete(receipt);
	}

	
}
