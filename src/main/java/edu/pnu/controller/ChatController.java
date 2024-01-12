	package edu.pnu.controller;

import java.util.List;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import edu.pnu.domain.ChatLog;
import edu.pnu.domain.ChatMessage;
import edu.pnu.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/private/chat")
@Tag(name = "채팅컨트롤러", description = "WebSocket을 이용한 실시간 채팅 모듈")
public class ChatController {
	
	private ChatService chatService;
	
	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

    // 전역 채널로 메세지를 보냅니다.
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessageToGlobal(@Payload ChatMessage chatMessage) {
    	ChatMessage Message = chatService.sendMessageToGlobal(chatMessage);
        return Message;
    }
    
    // 받는이에게 알림을 보내서 1:1 채널로 받는이를 구독시킵니다.
    @MessageMapping("/chat.inviteUser")
    public void inviteUser(@Payload ChatMessage chatMessage) {
    	chatService.inviteUser(chatMessage);
    }
    
    // 1:1 채널에 메세지를 보냅니다.
    @MessageMapping("/chat.sendMessageToRoom")
    public void sendMessageToRoom(@Payload ChatMessage chatMessage) {
    	chatService.sendMessageToRoom(chatMessage);
    }	
    
    @MessageMapping("/chat.setRoomId")
    public void sendInfoToSession(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor accessor) {
    	System.out.println("hi");
    	accessor.getSessionAttributes().put("roomId", chatMessage.getRoomId());
    	accessor.getSessionAttributes().put("userId", chatMessage.getSender());
    	String test = accessor.getSessionAttributes().get("roomId").toString();
    	chatService.sendInfoToSession(accessor);
    	System.out.println(test);
    }
    
    
    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        chatService.handleDisconnectEvent(event);
    }
    
    @Operation(description = "1:1 채널의 채팅 로그를 가져옵니다.")
    @GetMapping("/getChatLogsByRoomId")
    public ResponseEntity<?> getChatLogsByRoomId(@RequestParam String chatRoomId, Authentication auth) {
    	long startTime = System.nanoTime();
    	List<ChatLog> logList = chatService.getChatLogsByRoomId(chatRoomId, auth);
    	long endTime = System.nanoTime();
    	System.out.println("실행 시간: " + (endTime-startTime)/1_000_000 + "ms");
    	return ResponseEntity.ok(logList);
    }

    @Operation(description = "사용자의 모든 채널의 마지막 채팅 로그를 가져옵니다.")
    @GetMapping("/getLastChatLog")
    public ResponseEntity<?> getLasChatLog(Authentication auth) {
    	List<ChatLog> logList = chatService.findLastMessagesForRoomId(auth);
    	return ResponseEntity.ok(logList);
    }
    
    @Operation(description = "사용자의 모든 채널의 읽지 않은 메세지 수를 가져옵니다.")
    @GetMapping("/getCountUnReadMessage")
    public ResponseEntity<?> findUnReadMessageForRoomIdd(Authentication auth) {
    	List<Map> logList = chatService.findUnReadMessageForRoomId(auth);
    	return ResponseEntity.ok(logList);
    }
    
    @Operation(description = "접속중인 사용자가 상대방의 메세지를 읽으면 메세지 상태를 읽음으로 업데이트합니다.")
    @PutMapping("/updateIsLooked")
	public ResponseEntity<?> updateIsLooked(@RequestBody ChatLog chatLog, Authentication auth) {
    	ChatLog log = chatService.updateIsLooked(chatLog, auth);
    	return ResponseEntity.ok(log);
    }
    
//    @Operation(description = "접속시 채팅창에 입장 메세지를 보냅니다.")
//    @PostMapping("/postGreeting")
//    public ResponseEntity<?> postGreeting(@RequestBody ChatMessage chatMessage, Authentication auth, SimpMessageHeaderAccessor headerAccessor) {
//    	chatService.postGreeting(chatMessage, auth, headerAccessor);
//    	return ResponseEntity.ok(null);
//    }
    
}
