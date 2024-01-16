package edu.pnu.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.pnu.domain.NoticeLog;

public interface NoticeLogRepository extends MongoRepository<NoticeLog, String> {
	String countByUserHistoryNot(String username);
	Optional<List<NoticeLog>> findByUserHistoryNot(String username);
}
