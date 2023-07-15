package com.kakao.jPanda.common.config.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.kakao.jPanda.member.domain.MemberDto;

import lombok.Getter;


@Getter
public class UserDetailsDto extends User{
	
	private static final long serialVersionUID = 2262860898085560566L;
	
	private MemberDto memberDto;
	Collection<GrantedAuthority> roles;
	
	public UserDetailsDto(MemberDto memberDto, Collection<GrantedAuthority> roles) {
		super(memberDto.getMemberId(), memberDto.getPassword(), roles);
		this.memberDto = memberDto;
		this.roles = roles;
	}
	
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return roles;
	}

	@Override
	public String getPassword() {
		return memberDto.getPassword();
	}

	@Override
	public String getUsername() {
		return memberDto.getMemberId();
	}
	
}
