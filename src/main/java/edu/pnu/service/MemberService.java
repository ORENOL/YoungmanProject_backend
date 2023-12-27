package edu.pnu.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.pnu.domain.Member;
import edu.pnu.persistence.MemberRepository;

@Service
public class MemberService {
	
	private MemberRepository memberRepo;
	
	private PasswordEncoder encoder;
	
	public MemberService(MemberRepository memberRepo, PasswordEncoder encoder) {
		this.memberRepo = memberRepo;
		this.encoder = encoder;
	}

	public void changePassword(Member member, Authentication authentication) {
		
		Optional<Member> existMember = memberRepo.findById(authentication.getName());
		Member tempMember = Member.builder()
				.username(authentication.getName())
				.password(encoder.encode(member.getPassword()))
				.role(existMember.get().getRole())
				.build();
		memberRepo.save(tempMember);
		return;

	}

	public void changeAuthority(Member member, Authentication authentication) {

		Optional<Member> existMember = memberRepo.findById(authentication.getName());
		Member tempMember = Member.builder()
				.username(member.getUsername())
				.password(existMember.get().getPassword())
				.role(member.getRole())
				.build();
		memberRepo.save(tempMember);
		return;


	}

}
