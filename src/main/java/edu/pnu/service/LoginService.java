package edu.pnu.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.pnu.domain.Member;
import edu.pnu.domain.Role;
import edu.pnu.exception.DuplicatedIdException;
import edu.pnu.exception.ResourceNotFoundException;
import edu.pnu.persistence.MemberRepository;

@Service
public class LoginService {


	private MemberRepository memberRepo;
	private PasswordEncoder encoder;
	
	public LoginService(MemberRepository memberRepo, PasswordEncoder encoder) {
		this.memberRepo = memberRepo;
		this.encoder = encoder;
	}
	
	public String doubleCheck(Member member) {
		Optional<Member> existMember = memberRepo.findById(member.getUsername());
		if (existMember.isPresent()) {
			throw new DuplicatedIdException("duplicated id");
		} 
		return null;
		
	}

	public void signup(Member member) {	
		
		if (memberRepo.existsById(member.getUsername())) {
			throw new DuplicatedIdException("duplicated id");
		} 
		
		memberRepo.save(Member.builder().username(member.getUsername())
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
		
		if(!existMember.isPresent()) {
			throw new ResourceNotFoundException("not exist member");
		}
		
		Member oldMember = existMember.get();
		
		memberRepo.save(Member.builder()
				.username(oldMember.getUsername())
				.password(member.getPassword())
				.role(oldMember.getRole())
				.email(oldMember.getEmail())
				.build());
		
		return;
	}

}
