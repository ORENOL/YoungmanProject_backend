package edu.pnu;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

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
	
	@Test
	public void InsertMongo() {
		Random random = new Random();
		for(int i=0; i<100; i++) {
			int quantity = random.nextInt(20)+1;
			int unitprice = (random.nextInt(10)+1)*10000;
			receiptRepo.save(Receipt.builder()
					.receiptId(i)
					.companyName("테스트기업"+ i )
					.item(generateRandomHangul(4))
					.quantity(quantity)
					.unitPrice(unitprice)
					.price(quantity * unitprice)
					.vendorName(generateRandomHangul(3))
					.tradeDate(LocalDateTime.parse("2023-11-23T15:30:00"))
					.createDate(new Date())
					.build());
		}

		
	}
	
	@Test
	public void findMongo() {
		Optional<Member> member = memberRepo.findById("test2");
		System.out.println(member.get().toString());
	}
}
