package edu.pnu.service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import edu.pnu.domain.ChatLog;
import edu.pnu.domain.ChatMessage;
import edu.pnu.domain.enums.IsLooked;
import edu.pnu.persistence.ChatLogRepository;

@Service
public class ChatService {
	
	private SimpMessagingTemplate messagingTemplate;
	private ChatLogRepository chatLogRepo;
	private MongoTemplate mongoTemplate;
	
	public ChatService(SimpMessagingTemplate messagingTemplate, ChatLogRepository chatLogRepo, MongoTemplate mongoTemplate) {
		this.messagingTemplate = messagingTemplate;
		this.chatLogRepo = chatLogRepo;
		this.mongoTemplate = mongoTemplate;
	}

	public ChatMessage sendMessageToGlobal(ChatMessage chatMessage) {
    	ZonedDateTime sendTime = ZonedDateTime.now();
    	chatMessage.setTimeStamp(sendTime);
		return chatMessage;
	}

	public void inviteUser(ChatMessage chatMessage) {
    	messagingTemplate.convertAndSend("/topic/room/"+ chatMessage.getReceiver(), chatMessage);
	}

	public void sendMessageToRoom(ChatMessage chatMessage) {
    	ZonedDateTime sendTime = ZonedDateTime.now();
    	chatMessage.setTimeStamp(sendTime);
    	saveChatLog(chatMessage);
    	System.out.println(chatMessage.toString());
    	messagingTemplate.convertAndSend("/topic/room/"+ chatMessage.getRoomId(), chatMessage);
	}
	
	public void saveChatLog(ChatMessage chatMessage) {
		Date date = convertZonedDateTimeToDate(chatMessage.getTimeStamp());
		System.out.println(date);
		ChatLog log = ChatLog.builder()
							.chatRoomId(chatMessage.getRoomId())
							.content(chatMessage.getContent())
							.isLooked(IsLooked.FALSE)
							.Sender(chatMessage.getSender())
							.Receiver(chatMessage.getReceiver())
							.timeStamp(date)
							.build();
		
		chatLogRepo.save(log);
	}
	
    public Date convertZonedDateTimeToDate(ZonedDateTime zonedDateTime) {
        // Step 1: ZonedDateTime을 Instant로 변환
        Instant instant = zonedDateTime.toInstant();
        
        // Step 2: Instant를 Date로 변환
        Date date = Date.from(instant);
        
        return date;
    }

	public List<ChatLog> getChatLogsByRoomId(String chatRoomId) {
		List<ChatLog> logList = chatLogRepo.findByChatRoomId(chatRoomId);
//		if (!OptionalLogList.isPresent()) {
//			throw new ResourceNotFoundException("not exist chatlog");
//		}
		
//		List<ChatLog> LogList = OptionalLogList.get();
		
		return logList;
	}

	public List<ChatLog> getLasChatLog(Authentication auth) {
		List<ChatLog> logList = chatLogRepo.findByReceiver(auth.getName());
		return logList;
	}
	
	public List<ChatLog> findLastMessagesForReceiver(Authentication auth) {
	    Aggregation aggregation = Aggregation.newAggregation(
	        Aggregation.match(Criteria.where("Receiver").is(auth.getName())), // 특정 수신자 필터링
	        Aggregation.sort(Sort.Direction.DESC, "timestamp"), // 시간 내림차순 정렬
	        Aggregation.group("Sender") // 발신자별 그룹화
	            .first("$$ROOT").as("lastMessage") // 각 그룹의 첫 번째 메시지 (가장 최근 메시지)
	    );

	    AggregationResults<ChatLog> results = mongoTemplate.aggregate(
	        aggregation, ChatLog.class, ChatLog.class
	    );

	    return results.getMappedResults();
	}


}
