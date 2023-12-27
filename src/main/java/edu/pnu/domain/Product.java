package edu.pnu.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	    private int 금액;
	    private int 단가;
	    private int 수량;
	    private String 품목;
}
