package edu.pnu.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.ChatMessage;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "채팅컨트롤러", description = "WebSocket을 이용한 실시간 채팅 모듈")
public class ChatController {
	
	private SimpMessagingTemplate messagingTemplate;
	
	public ChatController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/private")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
    	System.out.println(chatMessage.getContent());
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/private")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // 사용자 이름을 WebSocket 세션에 추가
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
    
    @MessageMapping("/chat.sendMessageToRoom")
    public void sendMessageToRoom(@Payload ChatMessage chatMessage) {
    	messagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoomId(), chatMessage);
    }
    
    @MessageMapping("/chat.inviteUser")
    public void sendUser(@Payload ChatMessage chatMessage) {
    	messagingTemplate.convertAndSendToUser(chatMessage.getReceiver(), "queue/invites", chatMessage);
    }

}