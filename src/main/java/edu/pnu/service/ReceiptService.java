package edu.pnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.pnu.persistence.ReceiptRepository;

@Service
public class ReceiptService {

	@Autowired
	private ReceiptRepository boardRepo;

//	public Page<Board> getOnePageBoards(int pageNo, String criteria) {
//		Page<Board> page = boardRepo.findAll(Pageable pageable);
//		return page;
//	}
	
	
}