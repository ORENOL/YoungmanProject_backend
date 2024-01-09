package edu.pnu.domain;


import java.time.ZonedDateTime;

import edu.pnu.domain.enums.MessageType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessage {

	private MessageType type;
	private String content;
	private String sender;
	private String receiver;
	private String roomId;
	private ZonedDateTime timeStamp;
}


