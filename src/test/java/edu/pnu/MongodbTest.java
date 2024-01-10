package edu.pnu;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.pnu.domain.ChatLog;
import edu.pnu.domain.Member;
import edu.pnu.domain.Receipt;
import edu.pnu.domain.enums.IsLooked;
import edu.pnu.persistence.AssociationCodeRepository;
import edu.pnu.persistence.ChatLogRepository;
import edu.pnu.persistence.MemberRepository;
import edu.pnu.persistence.ReceiptRepository;
import edu.pnu.service.ChatService;

@SpringBootTest
public class MongodbTest {

	@Autowired
	private ReceiptRepository receiptRepo;
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private AssociationCodeRepository assoRepo;
	
	@Autowired
	private ChatLogRepository chatLogRepo;
	
    public static String generateRandomHangul(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        // Range of Hangul Syllables
        int start = 0xAC00;
        int end = 0xD7A3;

        for (int i = 0; i < length; i++) {
            int codePoint = start + random.nextInt(end - start + 1);
            sb.append((char) codePoint);
        }

        return sb.toString();
    }
    
    public static LocalDateTime generateRandomDateTime(LocalDateTime start, LocalDateTime end) {
        long secondsBetween = ChronoUnit.SECONDS.between(start, end);
        long randomSeconds = ThreadLocalRandom.current().nextLong(secondsBetween + 1);

        return start.plusSeconds(randomSeconds);
    }
	
	@Test
	public void InsertMongo() {
		Random random = new Random();
        LocalDateTime startDate = LocalDateTime.of(2020, 1, 1, 0, 0); // 시작 날짜 및 시간
        LocalDateTime endDate = LocalDateTime.now(); // 현재 날짜 및 시간

       

		for(int i=0; i<100; i++) {
			int quantity = random.nextInt(20)+1;
			int unitprice = (random.nextInt(10)+1)*10000;
	        LocalDateTime randomDateTime = generateRandomDateTime(startDate, endDate);
			receiptRepo.save(Receipt.builder()
					.companyName("테스트기업"+ i )
					.item(generateRandomHangul(4))
					.quantity(quantity)
					.unitPrice(unitprice)
					.price(quantity * unitprice)
					.tradeDate(randomDateTime)
					.createDate(new Date())
					.build());
		}

		
	}
	
	@Test
	public void findMongo() {
		Optional<Member> member = memberRepo.findById("test2");
		System.out.println(member.get().toString());
	}
	
	@Test
	public void deleteMongo() {
		receiptRepo.deleteByCompanyNameContaining("테스트기업");
	}

	@Test
	public void changeValue() {

		String id = "0";
		List<Member> list = memberRepo.findAll();
		for (Member member : list) {
			Member temp = Member.builder().username(member.getUsername()).password(member.getPassword())
					.email(member.getEmail()).role(member.getRole()).association(assoRepo.findById(id).get()).build();
			memberRepo.save(temp);
		}
	}
	
	@Test
	public void addChatLog() {

		List<ChatLog> list = new ArrayList<>();
		
		for (int i=0; i<3000; i++) {
	    	ZonedDateTime sendTime = ZonedDateTime.now();
			Date date = ChatService.convertZonedDateTimeToDate(sendTime);

		ChatLog log = ChatLog.builder()
				.chatRoomId("OREN&ORENOL")
				.content("아무 메세지"+i)
				.isLooked(IsLooked.FALSE)
				.Sender("OREN")
				.Receiver("ORENOL")
				.timeStamp(date)
				.build();
		
		list.add(log);
		}
		
		chatLogRepo.saveAll(list);
	}
	
	@Test
	public void deleteChatLog() {
		chatLogRepo.deleteByChatRoomId("OREN&ORENOL");
	}
}
