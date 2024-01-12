package edu.pnu.controller;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.NoticeLog;
import edu.pnu.service.NoticeService;

@RestController
@RequestMapping("/api/private/notice")
public class NoticeController {
	
	private NoticeService noticeService;
	
	public NoticeController(NoticeService noticeService) {
		this.noticeService = noticeService;
	}

	@GetMapping("/getNoticeLogs")
	public ResponseEntity<?> getNoticeLogs() {
		List<NoticeLog> list = noticeService.getNoticeLogs();
		return ResponseEntity.ok(list);
	}
	
	@PutMapping("/updateUserHistoryInLog")
	public ResponseEntity<?> updateUserHistoryInLog(@RequestBody NoticeLog log, Authentication auth) {
		NoticeLog temp = noticeService.updateUserHistoryInLog(log, auth);
		return ResponseEntity.ok(temp);
	}
}
