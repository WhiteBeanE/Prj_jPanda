package com.kakao.jPanda.main.controller;


import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kakao.jPanda.common.annotation.NoLoginCheck;
import com.kakao.jPanda.main.domain.NoticeDto;
import com.kakao.jPanda.main.domain.PagerDto;
import com.kakao.jPanda.main.service.NoticeService;
import com.kakao.jPanda.talent.register.service.RegistTalentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	
	private final RegistTalentService registTalentService;
	private final NoticeService noticeService;
	
	// 메인 페이지 이동
	
	@GetMapping("/main")
	public String main(Model model) {
		model.addAttribute("bestSellerTalent", registTalentService.findBestSellerTalents());
		model.addAttribute("topRatedTalent", registTalentService.findTopRatedTalents());
		model.addAttribute("newTrendTalent", registTalentService.findNewTrendTalents());
		model.addAttribute("randomTalent", registTalentService.findRandomTalents());
		
		return "main/main";
	}	
	
	// 공지사항 페이지 이동
	
	@GetMapping("/main/notice") 
	public String notice() {
		return "main/notice";
	}
	
	// 공지사항 불러오기
	
	@ResponseBody
	@GetMapping("/notice/notices")
	public Map<String, Object> noticeListBySearchAndCurrentPage(PagerDto pager) {
		Map<String, Object> map = noticeService.findNoticesByPager(pager);
		
		return map;
	}
	
	// 공지사항 세부 페이지
	
	@GetMapping("/notices/{noticeNo}")
	public String noticeDetailByNoticeNo(@PathVariable Long noticeNo, Model model) {
		NoticeDto notice = noticeService.findNoticeByNoticeNo(noticeNo);
		model.addAttribute("notice", notice);
		return "main/noticeDetail";
	}
	
}
