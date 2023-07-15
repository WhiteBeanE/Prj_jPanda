package com.kakao.jPanda.common.config.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SecurityPasswordEncoder implements PasswordEncoder {
	
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public SecurityPasswordEncoder() {
		this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
	}
	// 비밀번호 해시화
	@Override
	public String encode(CharSequence rawPassword) {
		return bCryptPasswordEncoder.encode(rawPassword);
	}
	// 비밀번호가 인치하는지 확인
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
	}

}
