package edu.pnu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

import edu.pnu.config.filter.JWTAuthenticationFilter;
import edu.pnu.config.filter.JWTAuthorizationFilter;
import edu.pnu.persistence.MemberRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {


	private MemberRepository memberRepository;

	private AuthenticationConfiguration authenticationConfiguration;
	
	public SecurityConfig(MemberRepository memberRepository, AuthenticationConfiguration authenticationConfiguration) {
		this.authenticationConfiguration = authenticationConfiguration;
		this.memberRepository = memberRepository;
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf->csrf.disable());
		http.cors(cors->cors.configurationSource(corsFilter()));
		http.formLogin(frmLogin->frmLogin.disable());
		http.authorizeHttpRequests(auth->auth
				.requestMatchers(new AntPathRequestMatcher("/api/private/**")).authenticated()
				.anyRequest().permitAll());
		http.sessionManagement(ssmn->ssmn.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.addFilterBefore(new JWTAuthorizationFilter(memberRepository), AuthorizationFilter.class);
		http.addFilter(new JWTAuthenticationFilter(
						authenticationConfiguration.getAuthenticationManager()));
		return http.build();
	}
	
	@Bean
	CorsConfigurationSource corsFilter() {
		
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin("http://10.125.121.214:3000, http://10.125.121.213:3000, http://localhost:3000");
		config.addAllowedOriginPattern("*");
		config.addAllowedMethod(CorsConfiguration.ALL);
		config.addAllowedHeader(CorsConfiguration.ALL);
		config.setAllowCredentials(true);
		config.addExposedHeader("Authorization");
		config.addExposedHeader("Username");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

    @Bean
    WebClient webClient(WebClient.Builder builder) {
		return builder.build();
	}
	
}
