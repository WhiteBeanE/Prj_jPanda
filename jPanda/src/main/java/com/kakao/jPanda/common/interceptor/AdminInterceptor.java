package com.kakao.jPanda.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class AdminInterceptor implements HandlerInterceptor, AsyncHandlerInterceptor{
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String memberId = (String)request.getSession().getAttribute("memberId");
        String contentTypeHeader = request.getHeader("Content-Type");
        boolean isAjaxRequest = contentTypeHeader != null && contentTypeHeader.startsWith("application/json");
		
		if (memberId.equals("admin")) {
			return true;
		} else {
			if (isAjaxRequest) {
				log.info("CommonInterceptor False : UNAUTHORIZED");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write("{\"redirectUrl\": \"/login\"}");
			} else {
				response.sendRedirect("/login");
			}
			
			return false;
		}
		
	}

}
