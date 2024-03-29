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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import edu.pnu.domain.ChatLog;
import edu.pnu.domain.ChatMessage;
import edu.pnu.domain.dto.ApiResponse;
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
    
    // 채팅방에 메세지를 보냅니다.
    @MessageMapping("/chat.sendMessageToRoom")
    public void sendMessageToRoom(@Payload ChatMessage chatMessage, Authentication auth) {
    	chatService.sendMessageToRoom(chatMessage, auth);
    }	
    
    // 채팅방 입장시 웹소켓 세션에 채팅방Id 및 유저Id를 저장합니다.
    @MessageMapping("/chat.setRoomId")
    public void sendInfoToSession(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor accessor, Authentication auth) {
    	System.out.println("hi");
    	accessor.getSessionAttributes().put("roomId", chatMessage.getRoomId());
    	accessor.getSessionAttributes().put("userId", chatMessage.getSender());
    	String test = accessor.getSessionAttributes().get("roomId").toString();
    	chatService.sendInfoToSession(accessor, auth);
    	System.out.println(test);
    }
    
    // 웹소켓 연결시 유저Id를 저장합니다.
    @MessageMapping("/main.setUserId")
    public void sendInfoToSession(SimpMessageHeaderAccessor accessor, Authentication auth) {
    	accessor.getSessionAttributes().put("userId", auth.getName());
    }
    
    // 채팅방을 나가서 웹소켓 세션이 종료되면 상대방에게 채팅 종료 신호를 보냅니다.
    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        chatService.handleDisconnectEvent(event);
    }
    
    @Operation(summary = "채팅방의 모든 채팅 로그 가져오기", description = "1:1 채널의 채팅 로그를 가져옵니다.")
    @GetMapping("/getChatLogsByRoomId")
    public ResponseEntity<?> getChatLogsByRoomId(@RequestParam String chatRoomId, Authentication auth) {
    	long startTime = System.nanoTime();
    	List<ChatLog> logList = chatService.getChatLogsByRoomId(chatRoomId, auth);
    	long endTime = System.nanoTime();
    	System.out.println("실행 시간: " + (endTime-startTime)/1_000_000 + "ms");
    	return ResponseEntity.ok(logList);
    }

    @Operation(summary = "마지막 채팅 조회", description = "사용자의 모든 채널의 마지막 채팅 로그를 가져옵니다.")
    @GetMapping("/getLastChatLog")
    public ResponseEntity<?> getLasChatLog(Authentication auth, @RequestParam(required = false) String searchValue) {
    	List<ChatLog> logList = chatService.findLastMessagesForRoomId(auth, searchValue);
    	return ResponseEntity.ok(logList);
    }
    
    @SuppressWarnings("rawtypes")
	@Operation(summary = "읽지 않은 메세지 수 조회", description = "사용자의 모든 채널의 읽지 않은 메세지 수를 가져옵니다.")
    @GetMapping("/getCountUnReadMessage")
    public ResponseEntity<?> getCountUnReadMessage(Authentication auth, @RequestParam(required = false) String searchValue) {
    	List<Map> logList = chatService.getCountUnReadMessage(auth, searchValue);
    	return ResponseEntity.ok(logList);
    }
    
    @Operation(summary = "모든 읽지 않은 메세지 수 총합 조회", description = "사용자의 모든 채널의 읽지 않은 메세지 수의 총합을 가져옵니다.")
    @GetMapping("/getSumUnReadMessage")
    public ResponseEntity<?> getSumUnReadMessage(Authentication auth) {
    	String sumCounts = chatService.getSumUnReadMessage(auth.getName());
    	return ResponseEntity.ok(new ApiResponse(sumCounts));
    }
    
    @Operation(summary = "메세지 읽음 처리", description = "접속중인 사용자가 상대방의 메세지를 읽으면 메세지 상태를 읽음으로 업데이트합니다.")
    @PutMapping("/updateIsLooked")
	public ResponseEntity<?> updateIsLooked(@RequestBody ChatLog chatLog, Authentication auth) {
    	ChatLog log = chatService.updateIsLooked(chatLog, auth);
    	return ResponseEntity.ok(log);
    }
    
}
