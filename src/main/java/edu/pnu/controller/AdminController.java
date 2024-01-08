package edu.pnu.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.AssociationCode;
import edu.pnu.domain.Member;
import edu.pnu.domain.dto.SignMember;
import edu.pnu.persistence.AssociationCodeRepository;
import edu.pnu.persistence.MemberRepository;
import edu.pnu.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/admin/")
@Tag(name = "어드민 컨트롤러", description = "어드민 전용 모듈")
public class AdminController {
	
	private AdminService adminService;
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private AssociationCodeRepository assoRepo;
	
	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}
	
	private static Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Operation(summary = "코드테이블에 회사를 추가합니다.")
	@PostMapping("addAssociationCode")
	private ResponseEntity<?> addAssociationCode(@RequestBody AssociationCode associationCode) {
		adminService.addAssociationCode(associationCode);
		return ResponseEntity.ok().build();
		}
	
	@Operation(summary = "어드민의 회사에 소속된 회원 리스트를 불러옵니다.")
	@GetMapping("getOurMembers")
	private ResponseEntity<?> getOurMembers(Authentication auth, @RequestParam(required = false) String searchCriteria, @RequestParam(required = false) String searchValue) {
		List<Member> list = adminService.getOurMembers(auth, searchCriteria, searchValue);
		return ResponseEntity.ok(list);
	}
	
	@Operation(summary = "지정한 멤버의 정보를 수정합니다")
	@PutMapping("updateOurMember")
	private ResponseEntity<?> updateOurMember(@RequestBody SignMember member, Authentication auth) {
		adminService.updateOurMember(member, auth);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "지정한 멤버를 삭제합니다")
	@DeleteMapping("deleteOurMember")
	private ResponseEntity<?> deleteOurMember(@RequestBody Member member, Authentication auth) {
		adminService.deleteOurMember(member, auth);
		return ResponseEntity.ok().build();
	}
	
}
