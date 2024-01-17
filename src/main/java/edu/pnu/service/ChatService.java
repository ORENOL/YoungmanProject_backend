package edu.pnu.service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import edu.pnu.domain.ChatLog;
import edu.pnu.domain.ChatMessage;
import edu.pnu.domain.dto.ChatLogUnReadDTO;
import edu.pnu.domain.enums.IsLooked;
import edu.pnu.domain.enums.MessageType;
import edu.pnu.exception.ResourceNotFoundException;
import edu.pnu.persistence.ChatLogRepository;

@Service
public class ChatService {
	
	private SimpMessagingTemplate messagingTemplate;
	private ChatLogRepository chatLogRepo;
	private MongoTemplate mongoTemplate;
	private NoticeService noticeService;
	
	public ChatService(SimpMessagingTemplate messagingTemplate, ChatLogRepository chatLogRepo, MongoTemplate mongoTemplate, NoticeService noticeService) {
		this.messagingTemplate = messagingTemplate;
		this.chatLogRepo = chatLogRepo;
		this.mongoTemplate = mongoTemplate;
		this.noticeService = noticeService;
	}
	
	private static Map<String, List<String>> existingRoomId = new HashMap<>();

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

	public void sendMessageToRoom(ChatMessage chatMessage, Authentication auth) {
    	ZonedDateTime sendTime = ZonedDateTime.now();
    	chatMessage.setTimeStamp(sendTime);
    	ChatLog log = saveChatLog(chatMessage);
    	System.out.println(chatMessage.toString());
    	messagingTemplate.convertAndSend("/topic/room/"+ chatMessage.getRoomId(), log);
    	messagingTemplate.convertAndSend("/topic/lobby/"+ chatMessage.getReceiver(), log);
    	System.out.println(existingRoomId.get(chatMessage.getRoomId()));
    	if(!existingRoomId.get(chatMessage.getRoomId()).contains(chatMessage.getReceiver())) {
        	ChatLogUnReadDTO temp = ChatLogUnReadDTO.builder()
					.chatRoomId(log.getChatRoomId())
					.content(log.getContent())
					.Sender(log.getSender())
					.timeStamp(log.getTimeStamp())
					.type(MessageType.CHAT)
					.unReadMessage(Integer.parseInt(getSumUnReadMessage(chatMessage.getReceiver())))
					.build();
        	messagingTemplate.convertAndSend("/topic/main/"+ chatMessage.getReceiver(), temp);
    	}

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
	
	public List<ChatLog> findLastMessagesForRoomId(Authentication auth, String searchValue) {
		Criteria criteria = new Criteria();
		
		if(searchValue == null) {
			criteria.orOperator(Criteria.where("Sender").is(auth.getName()), Criteria.where("Receiver").is(auth.getName()));
		} else {
			// 유저 및 상대방이 포함된 채팅방 찾기
			criteria.orOperator(Criteria.where("Sender").is(auth.getName()).and("Receiver").regex(searchValue, "i"), Criteria.where("Sender").regex(searchValue, "i").and("Receiver").is(auth.getName())); 

		}
		
	    Aggregation aggregation = Aggregation.newAggregation(
	            Aggregation.match(criteria), // 사용자가 포함된 채팅로그 필터링
	            Aggregation.sort(Sort.Direction.DESC, "timeStamp"), // 시간 내림차순 정렬
	            Aggregation.group("chatRoomId").first("content").as("content").first("timeStamp").as("timeStamp").first("chatRoomId").as("chatRoomId").first("Sender").as("Sender").first("isLooked").as("isLooked") // 각 그룹의 첫 번째 메시지 (가장 최근 메시지)
	        );
	    
        AggregationResults<ChatLog> results = mongoTemplate.aggregate(
            aggregation, "chatLog", ChatLog.class
        );
        
        return results.getMappedResults();

	}
	
	public List<Map> getCountUnReadMessage(Authentication auth, String searchValue) {
        Criteria criteria = new Criteria();

        if(searchValue == null) {
        	criteria.andOperator(Criteria.where("isLooked").is("FALSE"), Criteria.where("Receiver").is(auth.getName()));
        } else {
        	criteria.andOperator(Criteria.where("isLooked").is("FALSE"), Criteria.where("Receiver").is(auth.getName()), Criteria.where("Sender").regex(searchValue, "i"));
        }
        
        Aggregation aggregation = Aggregation.newAggregation(
	            Aggregation.match(criteria), // 사용자가 포함된 채팅로그 중 읽지 않은 메세지 필터링
	            Aggregation.group("chatRoomId").count().as("unreadMessages") // 각 그룹의 로그 갯수
	        );
        
        AggregationResults<Map> unreadCount = mongoTemplate.aggregate(
                aggregation, "chatLog", Map.class
            );

        return unreadCount.getMappedResults();
	}

	public String getSumUnReadMessage(String username) {
		Criteria criteria = new Criteria();

		criteria.andOperator(Criteria.where("isLooked").is("FALSE"), Criteria.where("Receiver").is(username));

        Aggregation aggregation = Aggregation.newAggregation(
	            Aggregation.match(criteria), // 사용자가 포함된 채팅로그 중 읽지 않은 메세지 필터링
	            Aggregation.count().as("unreadMessages") // 각 그룹의 로그 갯수
	        );
        
        AggregationResults<Map> unreadCount = mongoTemplate.aggregate(
                aggregation, "chatLog", Map.class
            );
        
        String sumCounts = "0";
        System.out.println("야:"+unreadCount.getMappedResults().toString().length());
        if(unreadCount != null && !unreadCount.getMappedResults().isEmpty()) {
        	sumCounts = unreadCount.getMappedResults().get(0).get("unreadMessages").toString();
        }
        
        return sumCounts;
	}

	public ChatLog updateIsLooked(ChatLog chatLog, Authentication auth) {
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
		return chatLogRepo.save(log);
		
	}

//	public void postGreeting(ChatMessage chatMessage, Authentication auth, SimpMessageHeaderAccessor headerAccessor) {
//		
//		String roomId = chatMessage.getRoomId();
//		
//    	ZonedDateTime sendTime = ZonedDateTime.now();
//		Date date = convertZonedDateTimeToDate(sendTime);
//		
//		ChatLog temp2 = ChatLog.builder()
//				.chatRoomId(roomId)
//				.content("유저가 존재하는 방입니다.")
//				.isLooked(IsLooked.FALSE)
//				.Sender(auth.getName())
//				.Receiver(roomId.replace(auth.getName(), "").replace("&", ""))
//				.timeStamp(date)
//				.type(MessageType.JOIN)
//				.build();
//		
//		// 내가 들어간 방이 존재하면
//		if (existingRoomId.containsKey(roomId)) {
//			// 들어간 방에 알림을 보내고
//			messagingTemplate.convertAndSend("/topic/room/"+ roomId, temp2);
//			// roomId딕셔너리에 자기 이름 추가하기
//			existingRoomId.get(roomId).add(auth.getName());
//			
//		// 내가 들어간 방이 존재하지 않으면
//		} else {
//			// roomId딕셔너리에 방을 추가하고 자기 이름 넣기
//			System.out.println("존재하지않으니 넣을게");
//			List<String> userList = new ArrayList<>();
//			userList.add(auth.getName());
//			existingRoomId.put(roomId, userList);
//		}
//		
//		System.out.println(existingRoomId.get(roomId).toString());
//		
//		
//		ChatLog temp = ChatLog.builder()
//				.chatRoomId(roomId)
//				.content(auth.getName() + "님이 입장했습니다.")
//				.isLooked(IsLooked.FALSE)
//				.Sender(auth.getName())
//				.Receiver(roomId.replace(auth.getName(), "").replace("&", ""))
//				.timeStamp(date)
//				.type(MessageType.JOIN)
//				.build();
////		messagingTemplate.convertAndSend("/topic/room/"+ roomId, temp);
//	}

	public void handleDisconnectEvent(SessionDisconnectEvent event) {
		
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Object roomIdObj = headerAccessor.getSessionAttributes().get("roomId");
        // 전체 알림 메세지의 경우 연결 해제 로직이 필요없으므로 메서드 종료
        if (roomIdObj == null) {
        	return;
        }
        String roomId = roomIdObj.toString();
        String userId = headerAccessor.getSessionAttributes().get("userId").toString();
        
        
		
    	ZonedDateTime sendTime = ZonedDateTime.now();
		Date date = convertZonedDateTimeToDate(sendTime);
		
		ChatLog temp = ChatLog.builder()
				.chatRoomId(roomId)
				.content(userId + "님이 퇴장했습니다.")
				.isLooked(IsLooked.FALSE)
				.Sender(userId)
				.Receiver(roomId.replace(userId, "").replace("&", ""))
				.timeStamp(date)
				.type(MessageType.LEAVE)
				.build();
		
		messagingTemplate.convertAndSend("/topic/room/"+ roomId, temp);
		
		existingRoomId.get(roomId).remove(userId);
		
		if(existingRoomId.get(roomId).isEmpty()) {
			existingRoomId.remove(roomId);
		}
		
		System.out.println(existingRoomId.get(roomId).toString());
		
	}

	public void sendInfoToSession(SimpMessageHeaderAccessor accessor, Authentication auth) {
		
        String roomId = accessor.getSessionAttributes().get("roomId").toString();
        String userId = accessor.getSessionAttributes().get("userId").toString();
        
    	ZonedDateTime sendTime = ZonedDateTime.now();
		Date date = convertZonedDateTimeToDate(sendTime);
        
		ChatLog temp2 = ChatLog.builder()
				.chatRoomId(roomId)
				.content("유저가 존재하는 방입니다.")
				.isLooked(IsLooked.FALSE)
				.Sender(userId)
				.Receiver(roomId.replace(userId, "").replace("&", ""))
				.timeStamp(date)
				.type(MessageType.JOIN)
				.build();
		
		// 내가 들어간 방이 존재하면
		if (existingRoomId.containsKey(roomId)) {
			// 들어간 방에 알림을 보내고
			messagingTemplate.convertAndSend("/topic/room/"+ roomId, temp2);
			// roomId딕셔너리에 자기 이름 추가하기
			existingRoomId.get(roomId).add(userId);
			
		// 내가 들어간 방이 존재하지 않으면
		} else {
			// roomId딕셔너리에 방을 추가하고 자기 이름 넣기
			System.out.println("존재하지않으니 넣을게");
			List<String> userList = new ArrayList<>();
			userList.add(userId);
			existingRoomId.put(roomId, userList);
		}
		
		System.out.println(existingRoomId.get(roomId).toString());
		
		messagingTemplate.convertAndSend("/topic/main/"+userId, getSumUnReadMessage(userId));
		
	}

	public void handleConnectEvent(SessionConnectEvent event) {
	       StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
	       String username = headerAccessor.getFirstNativeHeader("Authorization");
	        // Principal 객체에서 사용자 정보 추출
	       System.out.println("jwt: " + username);
	        if (username != null) {
	            // 사용자 이름을 사용하여 필요한 작업 수행
	            messagingTemplate.convertAndSend("/topic/main/"+username, "메세지: " + getSumUnReadMessage(username)+"\n알림: "+noticeService.getNoticeLogsCounts(username));
	        }
		
	}


	
}
