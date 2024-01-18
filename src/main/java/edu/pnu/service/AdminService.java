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
			criteria.andOperator(Criteria.where("username").regex(keyword[0], "i"), Criteria.where("role").regex(keyword[1], "i"));
			query.addCriteria(criteria);
		}
		
		list = mongoTemplate.find(query, Member.class);

		System.out.println(searchCriteria);
		System.out.println(searchValue);
		System.out.println(list);
		return list;
	}

	public void updateOurMember(SignMember member, Authentication auth) {
		
		Optional<Member> optionalExistMember = memberRepo.findById(auth.getName());
		Optional<Member> optionalChangeMember = memberRepo.findById(member.getUsername());
		if (!(optionalExistMember.isPresent() & optionalChangeMember.isPresent())) {
			throw new ResourceNotFoundException("not exist member");
		}
		Member existMember = optionalExistMember.get();
		Member changeMember = optionalChangeMember.get();
		
		AssociationCode adminAssociation = existMember.getAssociation();
		AssociationCode memberAssociation = changeMember.getAssociation();
		
		if(!memberAssociation.getAssociation().equals(adminAssociation.getAssociation())) {
			throw new NotAcceptableStatusException("not your member");
		}
		
		Member temp = Member.builder()
					.username(changeMember.getUsername())
					.password(changeMember.getPassword())
					.email(changeMember.getEmail())
					.role(member.getRole())
					.association(changeMember.getAssociation())
					.build();
		 
		memberRepo.save(temp);
		return;
	}

	public void deleteOurMember(Member member, Authentication auth) {
		
		Optional<Member> OptionalExistMember = memberRepo.findById(member.getUsername());
		Optional<Member> OptionalAdmin = memberRepo.findById(auth.getName());
		
		if (OptionalExistMember.isEmpty() && OptionalAdmin.isEmpty()) {
			throw new ResourceNotFoundException("not exist member");
		}
		
		Member existMember = OptionalExistMember.get();
		Member admin = OptionalAdmin.get();
		
		if(!existMember.getAssociation().getCode().equals(admin.getAssociation().getCode())) {
			throw new NotAcceptableStatusException("not your member");
		}
		
		memberRepo.delete(existMember);
		return;
	}
}
