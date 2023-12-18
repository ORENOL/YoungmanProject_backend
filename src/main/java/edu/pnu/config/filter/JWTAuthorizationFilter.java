package edu.pnu.config.filter;

import java.io.IOException;

import java.util.Optional;

import org.apache.catalina.User;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import edu.pnu.domain.Member;
import edu.pnu.persistence.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {

	private final MemberRepository memberRepository;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain chain) throws IOException, ServletException{
			String srcToken = request.getHeader("Authorization");
			if (srcToken == null || !srcToken.startsWith("Bearer ")) {
				chain.doFilter(request, response);
				return;
			}
			String jwtToken = srcToken.replace("Bearer ", "");
			
			String username = JWT.require(Algorithm.HMAC256("edu.pnu.jwt")).build().verify(jwtToken).getClaim("username").asString();
			
			Optional<Member> opt = memberRepository.findById(username);
			if (!opt.isPresent()) {
				chain.doFilter(request, response);
				return;
			}
			Member findmember = opt.get();
			
//			User user = new User(findmember.getUsername(), findmember.getPassword();
	}
}
