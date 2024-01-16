package edu.pnu.controller;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.NoticeLog;
import edu.pnu.domain.dto.ApiResponse;
import edu.pnu.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/private/notice")
@Tag(name = "로그컨트롤러", description = "영수증 업데이트 로그 모듈")
public class NoticeController {
	
	private NoticeService noticeService;
	
	public NoticeController(NoticeService noticeService) {
		this.noticeService = noticeService;
	}

    @Operation(summary = "모든 영수증 로그를 조회", description = "모든 영수증 업데이트 로그를 조회합니다.")
	@GetMapping("/getNoticeLogs")
	public ResponseEntity<?> getNoticeLogs() {
		List<NoticeLog> list = noticeService.getNoticeLogs();
		return ResponseEntity.ok(list);
	}
    
    @Operation(summary = "모든 영수증 로그의 총갯수 조회",description = "모든 영수증 업데이트 로그의 총 갯수를 가져옵니다.")
	@GetMapping("/getNoticeLogsCounts")
	public ResponseEntity<?> getNoticeLogsCounts(Authentication auth) {
		String sumCounts = noticeService.getNoticeLogsCounts(auth);
		return ResponseEntity.ok(new ApiResponse(sumCounts));
	}
	
    @Operation(summary = "영수증 읽음 처리", description = "영수증 업데이트 로그에 참조한 유저를 추가합니다. (로그 하나 보기)")
	@PutMapping("/updateUserHistoryInLog")
	public ResponseEntity<?> updateUserHistoryInLog(@RequestBody NoticeLog log, Authentication auth) {
		NoticeLog noticeLog = noticeService.updateUserHistoryInLog(log, auth);
		return ResponseEntity.ok(noticeLog);
	}
    
    @Operation(summary = "모든 영수증 읽음 처리", description = "모든 영수증 업데이트 로그에 참조한 유저를 추가합니다. (로그 모두 보기)")
	@PutMapping("/updateUserHistoryInAllLog")
	public ResponseEntity<?> updateUserHistoryInAllLog(Authentication auth) {
		List<NoticeLog> logList = noticeService.updateUserHistoryInAllLog(auth);
		return ResponseEntity.ok(logList);
	}
}
