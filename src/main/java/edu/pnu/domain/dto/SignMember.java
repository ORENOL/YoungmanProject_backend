package edu.pnu.domain.dto;

import org.springframework.data.annotation.Id;

import edu.pnu.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SignMember {
	
		@Id
		@Pattern(regexp = "^[A-Za-z0-9]{5,10}$", message = "알파벳 대소문자, 숫자로 이루어진 5~10크기 이내의 아이디여야 합니다.")
		private String username;
		
		@Pattern(regexp = "^[A-Za-z0-9]{5,10}$", message = "알파벳 대소문자, 숫자로 이루어진 5~10크기 이내의 비밀번호여야 합니다.")
		String password;
		
		private Role role;
		
		@Email(message = "올바른 형식의 이메일 주소여야 합니다.")
		private String email;
		
		private String association;
}

