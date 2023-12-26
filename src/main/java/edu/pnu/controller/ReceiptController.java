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
	
	@Operation(summary = "영수증 전부 가져옵니다. 개발용 API")
	@GetMapping("getAllReceipt")
	public ResponseEntity<?> getAllReceipt() {
		return receiptService.getAllReceipt();
	}

	
	@Operation(summary = "페이지네이션된 영수증 정보를 가져옵니다.", description = "기본값으로 0번째 페이지에 10개의 데이터를 가져오며, 날짜 내림차순으로 정렬됩니다.<br>정렬 조건값은 Receipt 도메인을 참고해주세요.")
	@GetMapping("getPageReceipt")
	public ResponseEntity<?> getPageReceipt(
			@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize, 
			@RequestParam(defaultValue = "tradeDate") String orderCriteria, @RequestParam(defaultValue = "companyName") String searchCriteria,
			@RequestParam(required = false) String searchWord) {
		return receiptService.getPageReceipt(pageNo, pageSize, orderCriteria, searchCriteria, searchWord);
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
		return receiptService.saveReceipt(receipt);
	}
	

	@Operation(summary = "지정된 영수증 정보를 삭제합니다.")
	@DeleteMapping("deleteReceipt")
	public ResponseEntity<?> deleteBoard(@RequestParam String receiptId) {
		return receiptService.deleteBoard(receiptId);
	}
	
	@Operation(summary = "검색기능 테스트")
	@GetMapping("searchByStringReceipt")
	public ResponseEntity<?> searchByStringReceipt(@RequestParam(defaultValue = "companyName") String criteria,@RequestParam String value){
		return receiptService.searchByStringReceipt(criteria, value);
	}

}
