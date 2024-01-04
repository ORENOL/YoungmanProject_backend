package edu.pnu.domain;

import org.springframework.data.annotation.Id;

public class AssociationCode {
	
	@Id
	private int code;
	private String association;

}
