package edu.pnu.service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import edu.pnu.domain.dto.ChatLogUnReadDTO;
import edu.pnu.domain.enums.IsLooked;
import edu.pnu.exception.ResourceNotFoundException;
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
	
    public static Date convertZonedDateTimeToDate(ZonedDateTime zonedDateTime) {
        // Step 1: ZonedDateTime을 Instant로 변환
        Instant instant = zonedDateTime.toInstant();
        
        // Step 2: Instant를 Date로 변환
        Date date = Date.from(instant);
        
        return date;
    }

	public void inviteUser(ChatMessage chatMessage) {
    	messagingTemplate.convertAndSend("/topic/room/"+ chatMessage.getReceiver(), chatMessage);
	}

	public void sendMessageToRoom(ChatMessage chatMessage) {
    	ZonedDateTime sendTime = ZonedDateTime.now();
    	chatMessage.setTimeStamp(sendTime);
    	ChatLog log = saveChatLog(chatMessage);
    	System.out.println(chatMessage.toString());
    	messagingTemplate.convertAndSend("/topic/room/"+ chatMessage.getRoomId(), log);
	}
	
	public ChatLog saveChatLog(ChatMessage chatMessage) {
		Date date = convertZonedDateTimeToDate(chatMessage.getTimeStamp());
		System.out.println(date);
		ChatLog log = ChatLog.builder()
							.chatRoomId(chatMessage.getRoomId())
							.content(chatMessage.getContent())
							.isLooked(IsLooked.FALSE)
							.Sender(chatMessage.getSender())
							.Receiver(chatMessage.getReceiver())
							.timeStamp(date)
							.type(chatMessage.getType())
							.build();
		
		ChatLog saveLog = chatLogRepo.save(log);
		return saveLog;
	}
	

	public List<ChatLog> getChatLogsByRoomId(String chatRoomId, Authentication auth) {
		List<ChatLog> logList = chatLogRepo.findByChatRoomId(chatRoomId);
		
		List<ChatLog> list = new ArrayList<>();
		
		
		for (ChatLog log : logList) {
	
			if (!auth.getName().equals(log.getSender())) {
				log.setIsLooked(IsLooked.TRUE);
			}
			list.add(log);
		}
		
		chatLogRepo.saveAll(list);
		
    	ZonedDateTime sendTime = ZonedDateTime.now();
		Date date = convertZonedDateTimeToDate(sendTime);

		
		ChatLog temp = ChatLog.builder()
				.chatRoomId(chatRoomId)
				.content(auth.getName() + "이(가) 입장했습니다.")
				.isLooked(IsLooked.FALSE)
				.Sender(auth.getName())
				.Receiver(chatRoomId.replace(auth.getName(), "").replace("&", ""))
				.timeStamp(date)
				.build();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		messagingTemplate.convertAndSend("/topic/room/"+ chatRoomId, temp);
		
		
		return list;
	}

	public List<ChatLog> findLastMessagesForReceiver(Authentication auth) {
	    Aggregation aggregation = Aggregation.newAggregation(
	            Aggregation.match(Criteria.where("Receiver").is(auth.getName())), // 특정 수신자 필터링
	            Aggregation.sort(Sort.Direction.DESC, "timeStamp"), // 시간 내림차순 정렬
	            Aggregation.group("Sender").first("content").as("content").first("timeStamp").as("timeStamp").first("chatRoomId").as("chatRoomId").first("Sender").as("Sender") // 각 그룹의 첫 번째 메시지 (가장 최근 메시지)
	        );

	        AggregationResults<ChatLog> results = mongoTemplate.aggregate(
	            aggregation, "chatLog", ChatLog.class
	        );
	       
	        return results.getMappedResults();
	    }
	
	public List<ChatLogUnReadDTO> findLastMessagesForRoomId(Authentication auth) {
		Criteria criteria = new Criteria();
		criteria.orOperator(Criteria.where("Sender").is(auth.getName()), Criteria.where("Receiver").is(auth.getName()));
		
	    Aggregation aggregation = Aggregation.newAggregation(
	            Aggregation.match(criteria), // 사용자가 포함된 채팅로그 필터링
	            Aggregation.sort(Sort.Direction.DESC, "timeStamp"), // 시간 내림차순 정렬
	            Aggregation.group("chatRoomId").first("content").as("content").first("timeStamp").as("timeStamp").first("chatRoomId").as("chatRoomId").first("Sender").as("Sender").first("isLooked").as("isLooked") // 각 그룹의 첫 번째 메시지 (가장 최근 메시지)
	        );
	    
        AggregationResults<ChatLogUnReadDTO> results = mongoTemplate.aggregate(
            aggregation, "chatLog", ChatLogUnReadDTO.class
        );
        // 마지막 메세지 쿼리
        
//		criteria.andOperator(Criteria.where("isLooked").is("FALSE"));
//        Aggregation aggregation2 = Aggregation.newAggregation(
//	            Aggregation.match(criteria), // 사용자가 포함된 채팅로그 중 읽지 않은 메세지 필터링
//	            Aggregation.group("chatRoomId").count().as("unReadMessages") // 각 그룹의 로그 갯수
//	        );
//        
//        AggregationResults<ChatLogUnReadDTO> unreadCount = mongoTemplate.aggregate(
//                aggregation2, "chatLog", ChatLogUnReadDTO.class
//            );
//        // 읽지 않은 메세지 수 쿼리
//        
//        List<ChatLogUnReadDTO> list = new ArrayList<>();
//        
//       	long startTime = System.nanoTime();
//        for (ChatLogUnReadDTO log : results.getMappedResults()) {
//        	for (ChatLogUnReadDTO unreadLog : unreadCount.getMappedResults()) {
//        		if (log.getChatRoomId().equals(unreadLog.getChatRoomId())) {
//        			log.setUnReadMessage(unreadLog.getUnReadMessage());
//        			list.add(log);
//        		}
//        	}
//        }
//        long endTime = System.nanoTime();
//        
//    	System.out.println("실행 시간: " + (endTime-startTime)/1_000_000 + "ms");

        return results.getMappedResults();

	}
	
	public List<Map> findUnReadMessageForRoomId(Authentication auth) {
        Criteria criteria = new Criteria();
//		criteria.orOperator(Criteria.where("Sender").is(auth.getName()), );
		criteria.andOperator(Criteria.where("isLooked").is("FALSE"), Criteria.where("Receiver").is(auth.getName()));

        Aggregation aggregation = Aggregation.newAggregation(
	            Aggregation.match(criteria), // 사용자가 포함된 채팅로그 중 읽지 않은 메세지 필터링
	            Aggregation.group("chatRoomId").count().as("unreadMessages") // 각 그룹의 로그 갯수
	        );
        
        AggregationResults<Map> unreadCount = mongoTemplate.aggregate(
                aggregation, "chatLog", Map.class
            );

        return unreadCount.getMappedResults();
	}

	public void updateIsLooked(ChatLog chatLog, Authentication auth) {
		// 신호를 받으면 읽지않은 메세지를 조회하고 읽음처리로 할까?

		// 메세지를 서버로 전송해서 해당 메세지만 읽음 처리로 할까? (받아야할 메세지의 정보는?)
		// 메세지의 방번호와 내용?, 수신자와 송신자? --> 아이디 가져오는데 성공함
		System.out.println();
		Optional<ChatLog> OptionalLog = chatLogRepo.findById(chatLog.getChatLogId());
		
		if (!OptionalLog.isPresent()) {
			throw new ResourceNotFoundException("not exist logId");
		}
		
		ChatLog log = OptionalLog.get();
		
		log.setIsLooked(IsLooked.TRUE);
		chatLogRepo.save(log);
		
	}
}
