package edu.pnu.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import edu.pnu.domain.enums.Role;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Member {

	@Id
	@Pattern(regexp = "^[A-Za-z0-9]{5,10}$")
	String username;
	
	@Pattern(regexp = "^[A-Za-z0-9]{5,10}$")
	String password;
	
	private Role role;
	
	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
	String email;
}