package edu.pnu.domain;

import edu.pnu.domain.enums.MessageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {

	private MessageType type;
	private String content;
	private String sender;
}


