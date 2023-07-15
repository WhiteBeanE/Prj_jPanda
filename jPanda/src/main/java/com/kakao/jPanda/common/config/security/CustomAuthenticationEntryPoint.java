package com.kakao.jPanda.common.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		log.info("[commence] authException : {}", authException.getMessage());
		int errorCode= HttpStatus.UNAUTHORIZED.value();
		String redirectUrl = "/login?error=";
		
		response.sendRedirect(redirectUrl + errorCode);
	}

}
