package edu.pnu.controller;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.Receipt;
import edu.pnu.service.ReceiptService;

@RestController
@RequestMapping("api/private/board/")
public class ReceiptController {
	
	@Autowired
	private ReceiptService boardService;

/*	
	// 한 페이지 분량의 게시글을 가져옴
	@GetMapping("getOnePageBoards")
	public ResponseEntity<?> getOnePageBoards(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNo, @RequestParam(required = false, defaultValue = "createdAt", value = "criteria") String criteria) {
		Page<Board> page = boardService.getOnePageBoards(pageNo, criteria);
		return ResponseEntity.ok(page);
	}
	
	// 하나의 게시글을 가져옴
	@GetMapping("getBoard")
	public ResponseEntity<?> getBoard() {
		Board board = boardService.getBoard();
		return ResponseEntity.ok(board);
	}
	
	// 게시글을 수정함
	@PostMapping("updateBoard")
	public ResponseEntity<?> updateBoard() {
		boardService.updateBoard();
		return ResponseEntity.ok().build();
	}
	
	// 게시글을 생성함
	@PostMapping("createBoard")
	public ResponseEntity<?> createBoard() {
		boardService.createBoard();
		return ResponseEntity.ok().build();
	}
	
	// 게시글을 삭제함
	@DeleteMapping("deleteBoard")
	public ResponseEntity<?> deleteBoard() {
		boardService.deleteBoard();
		return ResponseEntity.ok().build();
	}
*/
}
