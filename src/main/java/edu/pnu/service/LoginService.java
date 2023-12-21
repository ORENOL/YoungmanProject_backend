package edu.pnu.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
						.role(Role.USER)
						.build());
				return ResponseEntity.ok().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("unexpected error occurs: " + e.getMessage());
		}
		

	}

}
