package edu.pnu.persistence;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import edu.pnu.domain.ChatLog;
import java.util.Date;


public interface ChatLogRepository extends MongoRepository<ChatLog, String> {

	List<ChatLog> findByChatRoomId(String chatRoomId);
	
    @Query("SELECT cl FROM ChatLog cl WHERE cl.Receiver = :userId GROUP BY cl.Sender HAVING MAX(cl.timeStamp)")
	List<ChatLog> findByReceiver(String receiver);
    
    void deleteByChatRoomId(String chatRoomId);
    
    List<ChatLog> findByChatRoomIdAndContent(String chatRoomId, String content);

    List<ChatLog> findBySenderAndReceiverAndTimeStamp(String sender, String receiver, Date timeStamp);
}
