package edu.pnu.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.pnu.domain.Code;
import edu.pnu.domain.Member;
import edu.pnu.domain.enums.Role;
import edu.pnu.exception.DuplicatedIdException;
import edu.pnu.exception.ExpiredCodeException;
import edu.pnu.exception.ResourceNotFoundException;
import edu.pnu.persistence.CodeRepository;
import edu.pnu.persistence.MemberRepository;

@Service
public class LoginService {


	private MemberRepository memberRepo;
	private PasswordEncoder encoder;
	private JavaMailSender mailSender;
	private CodeRepository codeRepo;
	
	public LoginService(MemberRepository memberRepo, PasswordEncoder encoder, JavaMailSender mailSender, CodeRepository codeRepo) {
		this.memberRepo = memberRepo;
		this.encoder = encoder;
		this.mailSender = mailSender;
		this.codeRepo = codeRepo;
	}
	
	Random random = new Random();
	
	public String doubleCheck(Member member) {
		Optional<Member> existMember = memberRepo.findById(member.getUsername());
		if (existMember.isPresent()) {
			throw new DuplicatedIdException("duplicated id");
		} 
		return null;
		
	}

	public void signup(Member member) {	
		
		
		if (memberRepo.existsById(member.getUsername()) || memberRepo.existsByEmail(member.getEmail())) {
			throw new DuplicatedIdException("duplicated id/email");
		} 
		
		if (member.getUsername().isEmpty() || member.getEmail().isEmpty() || member.getPassword().isEmpty()) {
			throw new ResourceNotFoundException("not fill in field");
		}
		
		memberRepo.save(Member.builder()
				.username(member.getUsername())
				.password(encoder.encode(member.getPassword()))
				.email(member.getEmail())
				.role(Role.USER)
				.build());
		return;
		
		
	}

	public String findId(Member member) {
		
		Optional<Member> existMember = memberRepo.findByEmail(member.getEmail());
		
		if(!existMember.isPresent()) {
			throw new ResourceNotFoundException("not exist member");
		}
		
		String userId = existMember.get().getUsername();
		return userId;
	}

	public void findPassword(Member member) {
		
		Optional<Member> existMember = memberRepo.findByEmail(member.getEmail());
		Member oldMember = existMember.get();
		
		memberRepo.save(Member.builder()
				.username(oldMember.getUsername())
				.password(encoder.encode(member.getPassword()))
				.role(oldMember.getRole())
				.email(oldMember.getEmail())
				.build());
		return;
	}

	public void verifyEmail(Member member) {

		Optional<Member> existMember = memberRepo.findByEmail(member.getEmail());
		
		if(!existMember.isPresent()) {
			throw new ResourceNotFoundException("not exist member");
		}
		
		return;
		
	}

	public void verifyCode(Code code) {
		
		Optional<Code> existCode = codeRepo.findByCodeNumber(code.getCodeNumber());
		
		if(!existCode.isPresent()) {
			throw new ResourceNotFoundException("not exist code");
		}
		
		if(existCode.get().getExpiredTime().isBefore(LocalDateTime.now())) {
			throw new ExpiredCodeException("expired code");
		}
		
		codeRepo.delete(existCode.get());
		
		return;
		
	}

	public void sendCodeToMail(Member member) {
		Optional<Member> existMember = memberRepo.findByEmail(member.getEmail());
		
		if(!existMember.isPresent()) {
			throw new ResourceNotFoundException("not exist member");
		}
		
		Member tempMember = existMember.get();
		
		// 인증코드 난수 생성 (digit = 자릿수)
		int digit = 6;
		digit = (int) Math.pow(10, digit-1);
		int code = random.nextInt(9 * digit) + digit;
		SimpleMailMessage mail = new SimpleMailMessage();
		
		// 인증코드 생성 (timeToExpired = 만료기간[분])
		int timeToExpired = 15;
		codeRepo.save(Code.builder()
				.email(tempMember.getEmail())
				.codeNumber(code)
				.expiredTime(LocalDateTime.now().plusMinutes(timeToExpired))	
				.build());
		
		mail.setTo(tempMember.getEmail());
		mail.setSubject("Youngman프로젝트 비밀번호 인증코드입니다.");
		mail.setText("인증코드: " + code);
		mailSender.send(mail);
	}

}
