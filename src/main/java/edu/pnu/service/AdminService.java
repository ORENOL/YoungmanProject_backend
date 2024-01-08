package edu.pnu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;

import edu.pnu.domain.AssociationCode;
import edu.pnu.domain.Member;
import edu.pnu.domain.dto.SignMember;
import edu.pnu.exception.ResourceNotFoundException;
import edu.pnu.persistence.AssociationCodeRepository;
import edu.pnu.persistence.MemberRepository;

@Service
public class AdminService {

	private AssociationCodeRepository associationCodeRepo;
	private MemberRepository memberRepo;
	private MongoTemplate mongoTemplate;
	
	public AdminService(AssociationCodeRepository associationCodeRepo, MemberRepository memberRepo, MongoTemplate mongoTemplate) {
		this.associationCodeRepo = associationCodeRepo;
		this.memberRepo = memberRepo;
		this.mongoTemplate = mongoTemplate;
	}

	public void addAssociationCode(AssociationCode associationCode) {
		associationCodeRepo.save(associationCode);
		return;
	}

	// 회원명, 권한, 승인여부
	public List<Member> getOurMembers(Authentication auth, String searchCriteria, String searchValue) {
		List<Member> list;
		
		

		System.out.println(auth.getPrincipal());
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

		return list;
	}

	public void updateOurMember(SignMember member, Authentication auth) {
		
		Optional<Member> existingMember = memberRepo.findById(auth.getName());
		
		if (!existingMember.isPresent()) {
			throw new ResourceNotFoundException("not exist member");
		}
		Member existMember = existingMember.get();
		
		AssociationCode adminAssociation = existMember.getAssociation();
		AssociationCode memberAssociation = associationCodeRepo.findById(member.getAssociation()).get();
		
		if(!memberAssociation.getAssociation().equals(adminAssociation.getAssociation())) {
			throw new NotAcceptableStatusException("not your member");
		}
		
		
		Member temp = Member.builder()
					.username(existMember.getUsername())
					.password(existMember.getPassword())
					.email(existMember.getEmail())
					.role(member.getRole())
					.association(existMember.getAssociation())
					.build();
		 
		memberRepo.save(temp);
		return;
	}

	public void deleteOurMember(Member member, Authentication auth) {
		
		Optional<Member> existingMember = memberRepo.findById(auth.getName());
		
		if (!existingMember.isPresent()) {
			throw new ResourceNotFoundException("not exist member");
		}
		
		Member existMember = existingMember.get();
		
		AssociationCode adminAssociation = member.getAssociation();
		
		if(!existMember.getAssociation().equals(adminAssociation)) {
			throw new NotAcceptableStatusException("not your member");
		}
		
		memberRepo.delete(existMember);
		return;
	}
}
