package edu.pnu.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.pnu.domain.NoticeLog;

public interface NoticeLogRepository extends MongoRepository<NoticeLog, String> {

}
