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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/private/receipt/")
@Tag(name = "영수증 컨트롤러", description = "영수증 관리 모듈")
public class ReceiptController {
	
	private ReceiptService receiptService;
	
	public ReceiptController(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	
	@Operation(summary = "페이지네이션된 영수증 정보를 가져옵니다.")
	@GetMapping("getPageReceipt")
	public ResponseEntity<?> getPageReceipt(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "createDate") String orderCriteria, @RequestParam(defaultValue = "companyName") String searchCriteria) {
		Page<Receipt> page = receiptService.getPageReceipt(pageNo, pageSize, orderCriteria);
		return ResponseEntity.ok(page);
	}
	
//	// 하나의 영수증을 가져옴
//	@GetMapping("getReceipt")
//	public ResponseEntity<?> getReceipt() {
//		Receipt receipt = receiptService.getReceipt();
//		return ResponseEntity.ok().build();
//	}
	
	@Operation(summary = "영수증 정보를 생성하거나 업데이트합니다.")
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
	

	@Operation(summary = "지정된 영수증 정보를 삭제합니다.")
	@DeleteMapping("deleteBoard")
	public ResponseEntity<?> deleteBoard(@RequestParam Long receiptId) {
		return receiptService.deleteBoard(receiptId);
	}

}
