package com.kakao.jPanda.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kakao.jPanda.common.annotation.NoLoginCheck;

import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class CommonInterceptor implements HandlerInterceptor, AsyncHandlerInterceptor{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String memberId = (String)request.getSession().getAttribute("memberId");
		//view 관련 요청 handler들은 모두 통과시킴
		//로직 없을 시 Class Casting Error
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        NoLoginCheck noLoginCheck = ((HandlerMethod) handler).getMethodAnnotation(NoLoginCheck.class);
        boolean isAjaxRequest = "XMLHttpRequest".equals(request.getHeader("x-requested-with"));
        
		if (noLoginCheck == null) {
			
			if (memberId != null) {
				return true;
			} else {
				log.info("CommonInterceptor False : memberId null");
				if (isAjaxRequest) {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write("{\"redirectUrl\": \"/login\"}");
				} else {
					response.sendRedirect("/login");
				}
				
	            return false;
			}
			
		} else {
			return true;
		}
		
	}
	
}
