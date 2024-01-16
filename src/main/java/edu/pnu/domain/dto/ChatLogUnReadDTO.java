package edu.pnu.domain.dto;

import java.util.Date;

import edu.pnu.domain.enums.IsLooked;
import edu.pnu.domain.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatLogUnReadDTO {
	
	private String chatLogId;
	private String content;
	private String Sender;
	private String Receiver;
	private Date timeStamp;
	private String chatRoomId;
	private IsLooked isLooked;
	private MessageType type;
	private int unReadMessage;

}
