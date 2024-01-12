package edu.pnu.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import edu.pnu.domain.NoticeLog;
import edu.pnu.domain.Receipt;
import edu.pnu.domain.enums.MessageType;
import edu.pnu.persistence.NoticeLogRepository;

@Service
public class NoticeService {
	
	private NoticeLogRepository noticeLogRepo;
	private SimpMessagingTemplate messagingTemplate;
	
	public NoticeService(NoticeLogRepository noticeLogRepo, SimpMessagingTemplate messagingTemplate) {
		this.noticeLogRepo = noticeLogRepo;
		this.messagingTemplate = messagingTemplate;
	}

	public List<NoticeLog> getNoticeLogs() {
		List<NoticeLog> list = noticeLogRepo.findAll();
		return list; 
	}
	
	public void saveAndNoticeLog(Receipt receipt, String status, Authentication auth) {
    	ZonedDateTime sendTime = ZonedDateTime.now();
		Date date = ChatService.convertZonedDateTimeToDate(sendTime);
		
		List<String> list = new ArrayList<>();
		list.add(auth.getName());
		
	    NoticeLog log = NoticeLog.builder()
	    					.content(auth.getName() + "님이 " + receipt.getCompanyName() + "의 영수증을 " + status + "했습니다.")
	    					.sender(auth.getName())
	    					.timeStamp(date)
	    					.userHistory(list)
	    					.type(MessageType.NOTICE)
	    					.build();
	    
		noticeLogRepo.save(log);
	    
	    messagingTemplate.convertAndSend("/topic/public", log);
		
		return;
	}

	public NoticeLog updateUserHistoryInLog(NoticeLog log, Authentication auth) {
		System.out.println(log.toString());
		List<String> list = log.getUserHistory();
		list.add(auth.getName());
		log.setUserHistory(list);
		return noticeLogRepo.save(log);
	}

}
