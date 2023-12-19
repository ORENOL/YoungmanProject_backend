package edu.pnu.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.pnu.domain.Member;

public interface MemberRepository extends MongoRepository<Member, String> {

}
