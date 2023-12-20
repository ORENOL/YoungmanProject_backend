package edu.pnu.domain;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.data.annotation.Id;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {
	
	@Id
	private long receiptId;
	private String companyName;
	private String vendorName;
	private String companyRegisterNumber;
	private int price;
	private LocalDateTime tradeDate;
	@Builder.Default
	private Date createDate = new Date();


}
