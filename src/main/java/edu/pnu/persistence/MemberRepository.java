package edu.pnu.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.pnu.domain.Member;
import java.util.List;
import java.util.Optional;


public interface MemberRepository extends MongoRepository<Member, String> {
	Optional<Member> findByEmail(String email);

}
