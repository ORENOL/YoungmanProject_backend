package edu.pnu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.Member;
import edu.pnu.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/private/member")
@Tag(name = "멤버 컨트롤러", description = "계정 관리 모듈")
public class MemberController {
	
	private MemberService memberService;
	
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}
	
	@Operation(summary = "회원 비밀번호 변경", description = "member 객체에는 password 프로퍼티만 입력하면 됩니다.")
	@PutMapping("changePassword")
	public ResponseEntity<?> changePassword(@RequestBody Member member, Authentication authentication) {
		memberService.changePassword(member, authentication);
		return ResponseEntity.ok("password changed");
	}
	
	@Secured("ROLE_ADMIN")
	@Operation(summary = "타회원 권한 변경", description = "해당 api는 admin 권한을 필요로 합니다.<br>member 객체에는 username 프로퍼티만 입력하면 됩니다.")
	@PutMapping("changeAuthority")
	public ResponseEntity<?> changeAuthority(@RequestBody Member member, Authentication authentication) {
		memberService.changeAuthority(member, authentication);
		return  ResponseEntity.ok("authority changed");
	}
	
	@Operation(summary = "회원 탈퇴 (미구현)", description = "")
	@DeleteMapping("deleteMember")
	public ResponseEntity<?> deleteMember() {
		return null;
	}
	

}
