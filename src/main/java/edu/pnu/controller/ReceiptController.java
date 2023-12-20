package edu.pnu.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.service.ReceiptService;

@RestController
@RequestMapping("api/private/board/")
public class ReceiptController {
	
	private ReceiptService receiptService;
	
	public ReceiptController(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

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
