package edu.pnu.controller;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import edu.pnu.domain.Member;
import edu.pnu.persistence.MemberRepository;
import edu.pnu.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "로그인 컨트롤러", description = "회원 인증 모듈")
public class LoginController {

	private MemberRepository memberRepository;
	
	private LoginService loginService;
	
	private PasswordEncoder encoder;
	
	public LoginController(MemberRepository memberRepository, LoginService loginService, PasswordEncoder encoder) {
		this.memberRepository = memberRepository;
		this.loginService = loginService;
		this.encoder = encoder;
	}
	
	@Operation(summary = "로그인 시 404 Not Found 방지를 위한 API")
	@PostMapping("/login")
	public ResponseEntity<?> login(){
		return ResponseEntity.ok("ok");
	}
	
	// 개발 전용 api입니다.
	@Operation(summary = "Swagger 테스트용 JWT 발급 API", description = "Try it out -> Execute 이후 발급된 JWT코드를 우측 상단의 Authorize에 입력하세요.")
	@PutMapping("/provideJWT")
	public ResponseEntity<?> provideJWT() {

		String token = JWT.create()
				.withClaim("username", "test2")
				.sign(Algorithm.HMAC256("edu.pnu.jwt"));
		
		return ResponseEntity.ok("Bearer " + token);
	}
	
	
	@Operation(summary = "사용자 회원가입 기능")
	@PostMapping("/api/public/signup")
	public ResponseEntity<?> signup(@RequestBody Member member){
		return loginService.signup(member);
	}
	
	@Operation(summary = "회원가입시 아이디 중복체크 기능") 
	@PostMapping("/api/public/doubleCheck")
	public ResponseEntity<?> doubleCheck(@RequestBody Member member) {
		return loginService.doubleCheck(member);
	}
}
