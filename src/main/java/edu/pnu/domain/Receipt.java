package edu.pnu.domain;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Receipt {
	
	@Id
	private String receiptId;
	private String companyName;
	private String item;
	private int quantity;
	private int unitPrice;
	private int price;
	private LocalDateTime tradeDate;
	@Builder.Default
	private Date createDate = new Date();


}
