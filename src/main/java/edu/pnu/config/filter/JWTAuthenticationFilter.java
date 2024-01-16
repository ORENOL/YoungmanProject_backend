package edu.pnu.config.filter;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.pnu.domain.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 인증 객체
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManager;
	
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
	
	@Value("${jwt_secret_key}")
	private String secretKey;
	
	// POST/login 요청이 왔을 때 인증을 시도하는 메서드
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
				throws AuthenticationException{
		
//			response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			ObjectMapper mapper = new ObjectMapper();
			Member member = null;
			try {
				member = mapper.readValue(request.getInputStream(), Member.class);
				System.out.println(member);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Authentication authToken = new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword());
			Authentication auth = authenticationManager.authenticate(authToken);
			System.out.println("auth: " + auth);
					
			return auth;
	}
	
	// 인증이 성공했을 때 실행되는 후처리 메서드
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
					Authentication authResult) throws IOException, ServletException{
		
	// 인증 결과 생성된 Authentication 객체에서 필요한 정보를 읽고 토큰을 만들어서 헤더에 추가함.
		User user = (User) authResult.getPrincipal();
		String token = JWT.create()
						.withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
						.withClaim("username", user.getUsername())
						.sign(Algorithm.HMAC256(secretKey));
				response.addHeader("Authorization", "Bearer " + token);
				response.addHeader("Username", user.getUsername());
				response.addHeader("Role", user.getAuthorities().toString());
				chain.doFilter(request, response);
	}
}
