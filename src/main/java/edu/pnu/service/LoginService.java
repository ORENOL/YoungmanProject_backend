package edu.pnu.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import edu.pnu.domain.Member;
import edu.pnu.persistence.MemberRepository;

@Service
public class LoginService {

	@Autowired
	private MemberRepository memberRepo;
	
	public ResponseEntity<?> doubleCheck(Member member) {
		Optional<Member> existMember = memberRepo.findById(member.getUsername());
		if (existMember.isPresent()) {
			return ResponseEntity.status(226).build();
		} else {
			return ResponseEntity.ok().build();
		}
	}

}
