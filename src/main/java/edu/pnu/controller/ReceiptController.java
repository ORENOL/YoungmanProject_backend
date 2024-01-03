package edu.pnu.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import edu.pnu.domain.Receipt;
import edu.pnu.domain.ReceiptDocument;
import edu.pnu.domain.dto.ApiResponse;
import edu.pnu.domain.dto.ReceiptPOJO;
import edu.pnu.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;

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
		return ResponseEntity.ok(receiptService.getAllReceipt());
	}

	
	@Operation(summary = "페이지네이션된 영수증 정보를 가져옵니다.", description = "기본값으로 0번째 페이지에 10개의 데이터를 가져오며, 날짜 내림차순으로 정렬됩니다.<br>정렬 조건값은 Receipt 도메인을 참고해주세요.")
	@GetMapping("getPageReceipt")
	public ResponseEntity<?> getPageReceipt(
			@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize, 
			@RequestParam(defaultValue = "tradeDate") String orderCriteria, @RequestParam(defaultValue = "companyName") String searchCriteria,
			@RequestParam(required = false) String searchWord) throws ParseException {
		Page<Receipt> page = receiptService.getPageReceipt(pageNo, pageSize, orderCriteria, searchCriteria, searchWord);
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
		String receiptid = receiptService.saveReceipt(receipt);
		return ResponseEntity.ok(receiptid);
	}
	
	@Operation(summary = "영수증 정보를 리스트로 받아 한번에 저장합니다.")
	@PostMapping("saveListReceipt")
	public ResponseEntity<?> saveListReceipt(@RequestBody List<Receipt> receipt) {
		String receiptid = receiptService.saveListReceipt(receipt);
		return ResponseEntity.ok(receiptid);
	}
	

	@Operation(summary = "지정된 영수증 정보를 삭제합니다.")
	@DeleteMapping("deleteReceipt")
	public ResponseEntity<?> deleteBoard(@RequestBody Receipt receiptId) {
		receiptService.deleteBoard(receiptId);
		ApiResponse response = new ApiResponse("delete success");
		return ResponseEntity.ok(response);
	}
	
	
	
	@Operation(summary = "Flask OCR API", description = "이미지를 업로드하면 서버로컬에 이미지를 저장하고 OCR Text를 가공한 Receipt JSON을 반환합니다.")
	@PostMapping("runReceiptOCR")
	public ResponseEntity<?> runReceiptOCR(@RequestParam MultipartFile image) throws IllegalStateException, IOException {
		List<ReceiptPOJO> receiptList = receiptService.runReceiptOCR(image);
		return ResponseEntity.ok(receiptList);
	}

}
