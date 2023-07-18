package com.kakao.jPanda.common.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		log.info("[handle] accessDeniedException : {}", accessDeniedException.getMessage());
		RequestCache requestCache = new HttpSessionRequestCache();
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		String defaultUrl = "/logout";
		
		if (savedRequest != null) {
			String prevUrl = savedRequest.getRedirectUrl();
			request.getSession().setAttribute("prevUrl", prevUrl);
		} else {
			request.getSession().setAttribute("prevUrl", defaultUrl);
		}
		
        if (isAjaxRequest(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        } else {
            response.sendRedirect(defaultUrl);
        }
		
	}

	private boolean isAjaxRequest(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

}
