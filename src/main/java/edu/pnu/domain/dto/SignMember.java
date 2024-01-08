package edu.pnu.domain.dto;

import org.springframework.data.annotation.Id;

import edu.pnu.domain.enums.Role;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SignMember {
	
		@Pattern(regexp = "^[A-Za-z0-9]{5,10}$")
		@Id
		private String username;
		
		@Pattern(regexp = "^[A-Za-z0-9]{5,10}$")
		String password;
		
		private Role role;
		
		@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
		private String email;
		
		private String association;
}

