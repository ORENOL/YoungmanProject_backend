	package edu.pnu.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.ChatLog;
import edu.pnu.domain.ChatMessage;
import edu.pnu.service.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/private/chat")
@Tag(name = "채팅컨트롤러", description = "WebSocket을 이용한 실시간 채팅 모듈")
public class ChatController {
	
	private ChatService chatService;
	
	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}
	
    private static Set<String> userList = new HashSet<>();

    // 전역 채널로 메세지를 보냅니다.
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/private")
    public ChatMessage sendMessageToGlobal(@Payload ChatMessage chatMessage) {
    	ChatMessage Message = chatService.sendMessageToGlobal(chatMessage);
        return Message;
    }
    
    // 1:1 채널로 유저를 구독시킵니다.
    @MessageMapping("/chat.inviteUser")
    public void inviteUser(@Payload ChatMessage chatMessage) {
    	chatService.inviteUser(chatMessage);
    }
    
    // 1:1 채널에 메세지를 보냅니다.
    @MessageMapping("/chat.sendMessageToRoom")
    public void sendMessageToRoom(@Payload ChatMessage chatMessage) {
    	chatService.sendMessageToRoom(chatMessage);
    }
    
    // 1:1 채널의 채팅 로그를 가져옵니다.
    @GetMapping("/getChatLogsByRoomId")
    public ResponseEntity<?> getChatLogsByRoomId(@RequestParam String chatRoomId, Authentication auth) {
    	List<ChatLog> logList = chatService.getChatLogsByRoomId(chatRoomId, auth);
    	return ResponseEntity.ok(logList);
    }
    
    // 사용자의 모든 채널의 마지막 채팅 로그를 가져옵니다.
    @GetMapping("/getLastChatLog")
    public ResponseEntity<?> getLasChatLog(Authentication auth) {
    	
    	// Receiver가 사용자인 마지막 로그를 가져오기.
    	List<ChatLog> logList = chatService.findLastMessagesForRoomId(auth);
    	return ResponseEntity.ok(logList);
    }
    
	/*
	 * @MessageMapping("/chat.addUser")
	 * 
	 * @SendTo("/topic/private") public ChatMessage addUser(@Payload ChatMessage
	 * chatMessage, SimpMessageHeaderAccessor headerAccessor) { // 사용자 이름을 WebSocket
	 * 세션에 추가 headerAccessor.getSessionAttributes().put("username",
	 * chatMessage.getSender()); return chatMessage; }
	 * 
	 * @MessageMapping("/chat/{id}") public void inviteUser(@Payload ChatMessage
	 * chatMessage, @DestinationVariable String id) {
	 * System.out.println("chatMessage: "+chatMessage.toString());
	 * System.out.println("id: "+id);
	 * messagingTemplate.convertAndSend("/queue/addChatToClient/"+id, chatMessage);
	 * }
	 * 
	 * @MessageMapping("/join") public void joinUser(@Payload String userId){
	 * userList.add(userId); userList.forEach(user-> System.out.println(user)); }
	 */

    

    
    

    		
}
