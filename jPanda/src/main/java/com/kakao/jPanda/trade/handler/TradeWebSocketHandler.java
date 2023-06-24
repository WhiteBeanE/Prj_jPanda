package com.kakao.jPanda.trade.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.jPanda.trade.domain.TradeDto;
import com.kakao.jPanda.trade.service.TradeService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class TradeWebSocketHandler extends TextWebSocketHandler{
	
	private final TradeService tradeService;
	
	@Autowired
	public TradeWebSocketHandler(TradeService tradeService) {
		this.tradeService = tradeService;
	}
	
	//Session 관리를 위한 Map
	private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
	
	//ObjectWrapper
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// TradeWebSocketInterceptor에서 세션에 저장한 "memberId" 값을 사용
		String memberId = (String) session.getAttributes().get("memberId");
		log.info("afterConnectionEstablished memberId : " +  memberId);
		if (memberId != null) {
			sessionMap.put(memberId, session);
		}
		
		sessionMap.forEach((key, value)->{
			log.info("session map>> id : {} session : {}", key, value);
		});
		
	}
	
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		// admin client에서 Ajax통신 성공을 하면 이쪽으로 JSON형식으로 message에 데이터를 보내줌
		
		// message.getPayload()를 통해 웹소켓 메시지의 내용을 가져오고, 메시지는 문자열로 전송되고 있으므로 String으로 형변환
		String payload = (String)message.getPayload();
		//tradeDto와 필드명 일치
		// 필드명 변경
		String modifiedMessage = payload.replace("purchaseNo", "refundPurchaseNo");
		
		// objectMapper.readValue() 메소드를 사용하여 JSON 문자열을 Java 객체로 변환 
		// modifiedMessage를 List<TradeDto>로 변환
		List<TradeDto> messageTradeDtoList = objectMapper.readValue(modifiedMessage, new TypeReference<List<TradeDto>>() {});
		log.info("handleMessage before messageTradeDtoList : " + messageTradeDtoList);
		log.info("handleMessage before messageTradeDtoList : " + messageTradeDtoList.size());
		
		List<TradeDto> tradeDtoList = messageTradeDtoList.stream().map(tradeDto -> {
																	TradeDto resultTradeDto = findTradeDto(tradeDto);
																	combineTradeDtoValues(resultTradeDto);
																	return resultTradeDto;
																   }).collect(Collectors.toList());
		
		log.info("handleMessage after tradeDtoList : " + tradeDtoList);
		
		// Collectors.groupingBy(TradeDto::getMemberId)를 사용하여 TradeDto 객체들을 memberId를 기준으로 그룹화
		// TradeDto 객체의 getMemberId() 메서드를 사용하여 그룹화를 수행
		// Map<memberId, List<TradeDto>>
		Map<String, List<TradeDto>> groupedTradeDtoMap = tradeDtoList.stream().collect(Collectors.groupingBy(TradeDto::getMemberId));
		groupedTradeDtoMap.forEach((memberId, trades) -> {
			// sessionMap.get(memberId)를 통해 memberId에 해당하는 웹소켓 세션(WebSocketSession)을 가져옴
			WebSocketSession receiverSession = sessionMap.get(memberId);
			// 세션을 찾았다면, 해당 세션으로 데이터를 전송
			if (receiverSession != null) {
				try {
					// trades 리스트를 JSON 문자열로 변환하고, 해당 문자열을 웹소켓 세션으로 전송
					// objectMapper.writeValueAsString(trades)는 trades 리스트를 JSON 문자열로 변환하는 역할을 수행
					receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(trades)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.info("afterConnectionClosed removed memberId : {} session : {}", (String) session.getAttributes().get("memberId"),sessionMap.values().remove(session));
		sessionMap.values().remove(session);
		
	}
	
	/**
	 * param을 통해 talent TB, exchange TB, talent_refund TB의 PK로 row 조회
	 * @param tradeDto
	 * @return 조회된 TradeDto
	 */
	private TradeDto findTradeDto(TradeDto tradeDto) {
		if (tradeDto == null) {
			log.info("findTradeDto : tradeDto is Null");
			return new TradeDto();
		}
		
		if (tradeDto.getTalentNo() != null) {
	        return tradeService.findTradeByTalentNo(tradeDto.getTalentNo());
	        
		} else if(tradeDto.getExchangeNo() != null) {
			return tradeService.findExchangeByExchangeNo(tradeDto.getExchangeNo());
			
		} else if(tradeDto.getRefundPurchaseNo() != null) {
			return tradeService.findRefundByRefundPurchaseNo(tradeDto.getRefundPurchaseNo());
			
		} else {
			return new TradeDto();
		}
		
	}
	
	/**
	 * TradeDto의 sellerId, getBuyerId, getExchangeId를 memberId로 combine
	 * 해당 dto의 type에 따라 listType, approveDate 세팅
	 * @param tradeDto
	 */
	private void combineTradeDtoValues(TradeDto tradeDto) {
		if (tradeDto == null) {
			tradeDto = new TradeDto();
		}
		
		if (tradeDto.getSellerId() != null) {
			tradeDto.setListType("sell");
			tradeDto.setMemberId(tradeDto.getSellerId());
		} else if (tradeDto.getBuyerId() != null) {
			tradeDto.setListType("refund");
			tradeDto.setMemberId(tradeDto.getBuyerId());
			tradeDto.setRefundApproveDate(tradeDto.getApproveDate());
		} else if (tradeDto.getExchangeId() != null) {
			tradeDto.setListType("sell");
			tradeDto.setMemberId(tradeDto.getExchangeId());
			tradeDto.setTalentNo(tradeDto.getExchangeTalentNo());
			tradeDto.setExchangeApproveDate(tradeDto.getApproveDate());
		}
		
	}
	
}//end class