package com.kakao.jPanda.common.config.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kakao.jPanda.member.domain.MemberDto;
import com.kakao.jPanda.member.service.MemberService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class MemberDetailsServiceImpl implements UserDetailsService {
	
//	private final AMService amService;
	private final MemberService memberService;
	
	@Override
	public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
		log.info("[loadUserByUsername] memberId : {}", memberId);
		
//		MemberDto memberDto = amService.findMemberByMemberId(memberId);
		MemberDto memberDto = memberService.findMemberByMemberId(memberId);
		
		if (memberDto != null) {
			String roles = "user";
			if(memberDto.getMemberId().equals("admin")) {
				roles = "ADMIN";
			}
			
			UserDetailsDto userDetailsDto = new UserDetailsDto(memberDto, getRoles(roles));
			log.info("[loadUserByUsername] userDetailsDto : {}", userDetailsDto);
			
			return userDetailsDto;
		}
		
		throw new BadCredentialsException("No such id or wrong password");
	}
	
	private Collection<GrantedAuthority> getRoles(String role) {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(new SimpleGrantedAuthority("ROLE_" + role));
		
		return roles;
	}
	
}//end class
