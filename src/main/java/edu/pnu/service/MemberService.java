package edu.pnu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.pnu.domain.Member;
import edu.pnu.exception.ResourceNotFoundException;
import edu.pnu.persistence.MemberRepository;

@Service
public class MemberService {
	
	private MemberRepository memberRepo;
	private PasswordEncoder encoder;
	private MongoTemplate mongoTemplate;
	
	public MemberService(MemberRepository memberRepo, PasswordEncoder encoder, MongoTemplate mongoTemplate) {
		this.memberRepo = memberRepo;
		this.encoder = encoder;
		this.mongoTemplate = mongoTemplate;
	}

	public void changePassword(Member member, Authentication authentication) {
		
		Optional<Member> existMember = memberRepo.findById(authentication.getName());
		Member tempMember = Member.builder()
				.username(authentication.getName())
				.password(encoder.encode(member.getPassword()))
				.email(existMember.get().getEmail())
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
				.email(existMember.get().getEmail())
				.role(member.getRole())
				.build();
		memberRepo.save(tempMember);
		return;


	}

		public List<Member> getOurMembers(Authentication auth, String searchCriteria, String searchValue) {
		List<Member> list;
		
		
		System.out.println(searchCriteria);
		System.out.println(searchValue);
		
		if (searchCriteria == null) {
			list = memberRepo.findByAssociation(memberRepo.findById(auth.getName()).get().getAssociation());
			return list;
		}

		Query query = new Query();

		if (searchCriteria.equals("username")) {
			query.addCriteria(Criteria.where("username").regex(searchValue, "i"));
			
		}
		
		if (searchCriteria.equals("role")) {
			query.addCriteria(Criteria.where("role").regex(searchValue, "i"));
		}
		
		Criteria criteria = new Criteria();
		
		if (searchCriteria.equals("username&role")) {
			String[] keyword = searchValue.split("&");
			criteria.andOperator(Criteria.where("username").regex(keyword[0]), Criteria.where("role").regex(keyword[1]));
			query.addCriteria(criteria);
		}
		
		list = mongoTemplate.find(query, Member.class);
		System.out.println(list);
		return list;	
	}

		public void deleteMember(Authentication auth) {
			Optional<Member> optionalExistMember = memberRepo.findById(auth.getName());
			if(optionalExistMember.isEmpty()) {
				throw new ResourceNotFoundException("not exist memberId");
			}
			Member existMember = optionalExistMember.get();
		}

}

