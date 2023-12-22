package edu.pnu;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import edu.pnu.domain.Member;
import edu.pnu.domain.Receipt;
import edu.pnu.persistence.MemberRepository;
import edu.pnu.persistence.ReceiptRepository;

@SpringBootTest
public class MongodbTest {

	@Autowired
	private ReceiptRepository receiptRepo;
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Test
	public void InsertMongo() {
		receiptRepo.save(Receipt.builder()
				.receiptId(1233231)
				.companyName("기참")
				.companyRegisterNumber("1234-3123")
				.vendorName("김공상")
				.tradeDate(LocalDateTime.parse("2023-11-23T15:30:00"))
				.build());
		
	}
	
	@Test
	public void findMongo() {
		Optional<Member> member = memberRepo.findById("test2");
		System.out.println(member.get().toString());
	}
}
