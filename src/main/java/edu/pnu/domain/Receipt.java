package edu.pnu.domain;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document
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
	private String receiptDocumentId;
	private String originReceiptId;

}
