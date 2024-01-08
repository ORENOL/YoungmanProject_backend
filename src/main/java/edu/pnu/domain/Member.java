package edu.pnu.domain;

import org.springframework.data.annotation.Id;

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
public class Member {

	@Pattern(regexp = "^[A-Za-z0-9]{5,10}$")
	@Id
	private String username;
	
	@Pattern(regexp = "^[A-Za-z0-9]{5,10}$")
	String password;
	
	private Role role;
	
	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
	private String email;
	
	private AssociationCode association;
}