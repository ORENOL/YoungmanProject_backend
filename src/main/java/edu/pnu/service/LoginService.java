package edu.pnu.service;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.pnu.domain.Member;
import edu.pnu.domain.Role;
import edu.pnu.persistence.MemberRepository;

@Service
public class LoginService {


	private MemberRepository memberRepo;
	private PasswordEncoder encoder;
	
	public LoginService(MemberRepository memberRepo, PasswordEncoder encoder) {
		this.memberRepo = memberRepo;
		this.encoder = encoder;
	}
	
	public ResponseEntity<?> doubleCheck(Member member) {
		Optional<Member> existMember = memberRepo.findById(member.getUsername());
		if (existMember.isPresent()) {
			return ResponseEntity.status(226).body("id duplication");
		} else {
			return ResponseEntity.ok("enable id");
		}
	}

	public ResponseEntity<?> signup(Member member) {
		try {
			if(memberRepo.existsById(member.getUsername())) {
				return ResponseEntity.status(226).build();
			} else {
				memberRepo.save(Member.builder()
						.username(member.getUsername())
						.password(encoder.encode(member.getPassword()))
						.email(member.getEmail())
						.role(Role.USER)
						.build());
				return ResponseEntity.ok().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("unexpected error occurs: " + e.getMessage());
		}
		

	}

	public ResponseEntity<?> findId(Member member) {
		
		Optional<Member> existMember = memberRepo.findByEmail(member.getEmail());
		
		if(!existMember.isPresent()) {
			return ResponseEntity.unprocessableEntity().body("not exist member");
		}
		
		String userId = existMember.get().getUsername();
		
		return ResponseEntity.ok(userId);
	}

	public ResponseEntity<?> findPassword(Member member) {
		
		Optional<Member> existMember = memberRepo.findByEmail(member.getEmail());
		
		if(!existMember.isPresent()) {
			return ResponseEntity.unprocessableEntity().body("not exist member");
		}
		
		Member oldMember = existMember.get();
		
		memberRepo.save(Member.builder()
				.username(oldMember.getUsername())
				.password(member.getPassword())
				.role(oldMember.getRole())
				.email(oldMember.getEmail())
				.build());
		
		return ResponseEntity.ok("password changed");
	}

}
