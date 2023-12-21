package edu.pnu.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.pnu.domain.Member;
import edu.pnu.persistence.MemberRepository;

@Service
public class UserDetailService implements UserDetailsService {
	
	
	private MemberRepository memberRepository;
	
	public UserDetailService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//memberRepository에서 사용자 정보를 검색
		Optional<Member> option = memberRepository.findById(username);
		if (!option.isPresent()) {
			throw new UsernameNotFoundException("사용자가 없습니다.");
		}
		Member member = memberRepository.findById(username).orElseThrow(()->
		new UsernameNotFoundException("Not Found!"));
		
		// UserDetails 타입의 객체를 생성해서 리턴
		return new User(member.getUsername(), member.getPassword(),
						AuthorityUtils.createAuthorityList("ROLE_" + member.getRole().toString()));
	}
}
