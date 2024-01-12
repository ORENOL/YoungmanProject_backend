package edu.pnu.domain;

import java.util.Date;
import java.util.List;

import edu.pnu.domain.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class NoticeLog {
	
	private String id;
	private String content;
	private String sender;
	private Date timeStamp;
	private List<String> userHistory;
	private MessageType type;

}
