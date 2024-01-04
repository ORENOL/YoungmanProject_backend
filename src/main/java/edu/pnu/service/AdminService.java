package edu.pnu.service;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;

import edu.pnu.domain.AssociationCode;
import edu.pnu.domain.Member;
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
		
		list = mongoTemplate.find(query, Member.class);

		return list;
	}

	public void updateOurMember(Member member, Authentication auth) {
		
		AssociationCode adminAssociation = memberRepo.findById(auth.getName()).get().getAssociation();
		
		if(!member.getAssociation().equals(adminAssociation)) {
			throw new NotAcceptableStatusException("not your member");
		}
		
		Member existMember = memberRepo.findById(member.getUsername()).get();
		
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
		
		AssociationCode adminAssociation = memberRepo.findById(auth.getName()).get().getAssociation();
		
		if(!member.getAssociation().equals(adminAssociation)) {
			throw new NotAcceptableStatusException("not your member");
		}
		
		memberRepo.delete(member);
		return;
	}
}
