package edu.pnu.persistence;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import edu.pnu.domain.ChatLog;

public interface ChatLogRepository extends MongoRepository<ChatLog, String> {

	List<ChatLog> findByChatRoomId(String chatRoomId);
	
    @Query("SELECT cl FROM ChatLog cl WHERE cl.Receiver = :userId GROUP BY cl.Sender HAVING MAX(cl.timeStamp)")
	List<ChatLog> findByReceiver(String receiver);

}
