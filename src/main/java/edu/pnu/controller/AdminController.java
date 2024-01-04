package edu.pnu.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.AssociationCode;
import edu.pnu.persistence.AssociationCodeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("api/admin/")
public class AdminController {
	
	private AssociationCodeRepository associationCodeRepo;
	
	public AdminController(AssociationCodeRepository associationCodeRepo) {
		this.associationCodeRepo = associationCodeRepo;
	}
	
	
	
	@Operation(summary = "코드테이블에 회사를 추가합니다.")
	@PostMapping("/addAssociationCode")
	private void addAssociationCode(@RequestBody AssociationCode associationCode) {
		associationCodeRepo.save(associationCode);
	}

}
