package edu.pnu.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.pnu.domain.AssociationCode;
import edu.pnu.domain.Member;



public interface MemberRepository extends MongoRepository<Member, String> {
	Optional<Member> findByEmail(String email);
	boolean existsByEmail(String email);
	List<Member> findByAssociation(AssociationCode associationCode);

}
