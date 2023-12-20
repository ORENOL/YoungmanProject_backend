package edu.pnu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.Member;
import edu.pnu.persistence.MemberRepository;
import edu.pnu.service.LoginService;

@RestController
//@RequestMapping("/api/public")
public class LoginController {

	private MemberRepository memberRepository;
	
	private LoginService loginService;
	
	private PasswordEncoder encoder;
	
	public LoginController(MemberRepository memberRepository, LoginService loginService, PasswordEncoder encoder) {
		this.memberRepository = memberRepository;
		this.loginService = loginService;
		this.encoder = encoder;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(){
		return ResponseEntity.ok("ok");
	}
	
	@PostMapping("/api/public/signup")
	public ResponseEntity<?> signup(@RequestBody Member member){
		
		try {
			if(memberRepository.existsById(member.getUsername())) {
				return ResponseEntity.status(226).build();
			} else {
				memberRepository.save(Member.builder()
						.username(member.getUsername())
						.password(encoder.encode(member.getPassword()))
						.build());
				return ResponseEntity.ok().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.badRequest().build();
	}
	
	// 회원가입시 아이디 중복체크
	@PostMapping("/api/public/doubleCheck")
	public ResponseEntity<?> doubleCheck(@RequestBody Member member) {
		return loginService.doubleCheck(member);
	}
}
