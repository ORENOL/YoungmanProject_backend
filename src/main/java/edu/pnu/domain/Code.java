package edu.pnu.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

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
public class Code {
		@Id
		private String email;
	    private Integer codeNumber;
	    private LocalDateTime expiredTime;
}
