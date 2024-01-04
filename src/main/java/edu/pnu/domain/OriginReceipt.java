package edu.pnu.domain;

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
public class OriginReceipt {
	
	@Id
	private String receiptId;
	private String companyName;
	private String item;
	private String quantity;
	private String unitPrice;
	private String price;
	private String tradeDate;
	@Builder.Default
	private Date createDate = new Date();
	private String receiptDocumentId;


}
