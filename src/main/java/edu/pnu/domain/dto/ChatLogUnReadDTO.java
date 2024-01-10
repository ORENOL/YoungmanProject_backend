package edu.pnu.domain.dto;

import java.util.Date;

import edu.pnu.domain.enums.IsLooked;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatLogUnReadDTO {
	
	private String chatLogId;
	private String content;
	private String Sender;
	private String Receiver;
	private Date timeStamp;
	private String chatRoomId;
	private IsLooked isLooked;
	private int unReadMessage;

}
