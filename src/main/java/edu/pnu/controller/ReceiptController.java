package edu.pnu.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.Receipt;
import edu.pnu.service.ReceiptService;

@RestController
@RequestMapping("api/private/receipt/")
public class ReceiptController {
	
	private ReceiptService receiptService;
	
	public ReceiptController(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	
	// 한 페이지 분량의 영수증 목록을 가져옴
	@GetMapping("getPageReceipt")
	public ResponseEntity<?> getOnePage(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "createDate") String orderCriteria, @RequestParam(defaultValue = "companyName") String searchCriteria) {
		Page<Receipt> page = receiptService.getOnePageReceipt(pageNo, pageSize, orderCriteria);
		return ResponseEntity.ok(page);
	}
	
//	// 하나의 영수증을 가져옴
//	@GetMapping("getReceipt")
//	public ResponseEntity<?> getReceipt() {
//		Receipt receipt = receiptService.getReceipt();
//		return ResponseEntity.ok().build();
//	}
	
	// 영수증을 삽입 혹은 업데이트함
	@PostMapping("saveReceipt")
	public ResponseEntity<?> saveReceipt(@RequestBody Receipt receipt) {
		
		try {
			receiptService.saveReceipt(receipt);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
		
		return ResponseEntity.ok("save success");
	}
	
	// 영수증을 삭제함
	@DeleteMapping("deleteBoard")
	public ResponseEntity<?> deleteBoard(@RequestBody Receipt receipt) {
		
		try {
			receiptService.deleteBoard(receipt);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	
		return ResponseEntity.ok("delete success");
	}

}
