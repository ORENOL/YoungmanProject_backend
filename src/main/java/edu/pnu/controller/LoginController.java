package edu.pnu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import edu.pnu.domain.VerificationCode;
import edu.pnu.domain.Member;
import edu.pnu.domain.dto.ApiResponse;
import edu.pnu.domain.dto.SignMember;
import edu.pnu.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "로그인 컨트롤러", description = "회원 인증 모듈")
public class LoginController {

	
	private LoginService loginService;
	
	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}
	
	@Operation(summary = "로그인 시 404 Not Found 방지를 위한 API")
	@PostMapping("/login")
	public ResponseEntity<?> login(){
		return ResponseEntity.ok().build();
	}
	
	// 개발 전용 api입니다.
	@Operation(summary = "Swagger 테스트용 JWT 발급 API", description = "Try it out -> Execute 이후 발급된 JWT코드를 우측 상단의 Authorize에 입력하세요.")
	@PutMapping("/provideJWT")
	public ResponseEntity<?> provideJWT() {

		String token = JWT.create()
				.withClaim("username", "OREN")
				.sign(Algorithm.HMAC256("edu.pnu.jwt"));
		
		return ResponseEntity.ok("Bearer " + token);
	}

	@Operation(summary = "사용자 회원가입 기능", description = "Member 객체에는 username, password, email 프로퍼티만 입력하면 됩니다.")
	@PostMapping("/api/public/signup")
	public ResponseEntity<?> signup(@RequestBody @Valid SignMember member){
		loginService.signup(member);
	    ApiResponse response = new ApiResponse("signUp success");
		return ResponseEntity.ok(response);
	}
	
	@Operation(summary = "회원가입시 아이디 중복체크 기능", description = "Member 객체에는 username 프로퍼티만 입력하면 됩니다.") 
	@PostMapping("/api/public/doubleCheck")
	public ResponseEntity<?> doubleCheck(@RequestBody Member member) {
		loginService.doubleCheck(member);
		ApiResponse response = new ApiResponse("good id");
		return ResponseEntity.ok(response);
	}
	
	@Operation(summary = "이메일을 이용한 아이디 찾기 기능", description = "Member 객체에는 email 프로퍼티만 입력하면 됩니다.")
	@PostMapping("/api/public/findId")
	public ResponseEntity<?> findId(@RequestBody Member member) {
		String memberId = loginService.findId(member);
		return ResponseEntity.ok(memberId);
	}
	
	@Operation(summary = "비밀번호 변경 기능", description = "Member 객체에는 password 프로퍼티만 입력하면 됩니다.")
	@PutMapping("/api/public/findPassword")
	public ResponseEntity<?> findPassword(@RequestBody Member member) {
		loginService.findPassword(member);
		ApiResponse response = new ApiResponse("password changed");
		return ResponseEntity.ok(response);
	}
	
	@Operation(summary = "이메일 검증 API")
	@PostMapping("/api/public/verifyEmail")
	public ResponseEntity<?> verifyEmail(@RequestBody Member member) {
		loginService.verifyEmail(member);
		return ResponseEntity.ok(null);
	}
	
	/*
	 * @Operation(summary = "검증코드 발송 API")
	 * 
	 * @PostMapping("/api/public/sendCodeToMail") public ResponseEntity<?>
	 * sendCodeToMail(@RequestBody Member member) {
	 * loginService.sendCodeToMail(member); return ResponseEntity.ok(null); }
	 */
	
	@Operation(summary = "검증코드 유효성 검사 API", description = "Code 객체에는 codeNumber 프로퍼티만 입력하면 됩니다.")
	@PostMapping("/api/public/verifyCode")
	public ResponseEntity<?> verifyCode(@RequestBody VerificationCode code) {
		loginService.verifyCode(code);
		return ResponseEntity.ok(null);
	}
}
