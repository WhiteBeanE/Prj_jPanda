package com.kakao.jPanda.trade.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TradeWebSocketInterceptor extends HttpSessionHandshakeInterceptor{
	
	@Override
	// WebSocket 연결을 설정하기 전에 실행되는 로직
	// 로그인한 유저의 ID를 저장하는 하여 afterConnectionEstablished에서 세션을 저장하기 위한 용도
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		log.info("beforeHandshake");
		ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
		HttpServletRequest httpServletRequest = serverHttpRequest.getServletRequest();
		HttpSession httpSession = httpServletRequest.getSession();
		attributes.put("memberId", httpSession.getAttribute("memberId"));
		
		return true;
	}
	
}
