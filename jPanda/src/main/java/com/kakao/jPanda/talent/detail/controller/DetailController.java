package com.kakao.jPanda.talent.detail.controller;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kakao.jPanda.common.annotation.NoLoginCheck;
import com.kakao.jPanda.talent.detail.domain.BambooUseDto;
import com.kakao.jPanda.talent.detail.domain.ReportDto;
import com.kakao.jPanda.talent.detail.domain.ReviewDto;
import com.kakao.jPanda.talent.detail.domain.TalentDto;
import com.kakao.jPanda.talent.detail.service.TalentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DetailController {

	private final TalentService talentService;
	
	// 재능 상세페이지 및 리뷰 리스트 불러오기
	@NoLoginCheck
	@GetMapping("/talents/{talentNo}/detail")
	public String talentDetails(@PathVariable Long talentNo, Model model, HttpSession session) {
		log.info("Controller talentDetails Start");
		// 조회수 업데이트
		talentService.modifyTalentToViewCount(talentNo);
		// 상세 페이지 탤런트 정보
		TalentDto talent = talentService.findBoardTalentByTalentNo(talentNo);
		// 리뷰 리스트 
		List<ReviewDto> reviewList = talentService.findReviewListByTalentNo(talentNo);
		// 상세 페이지에서 memberId 검증 필요
		String memberId = (String)session.getAttribute("memberId");

		model.addAttribute("memberId", memberId);
		model.addAttribute("talent", talent);
		model.addAttribute("reviewList", reviewList);
		
		return "talent/detail/talent";
	}
	
	
	// 리뷰 인서트
	@ResponseBody
	@PostMapping("/talents/{talentNo}/review")
	public int reviewAdd(@RequestBody ReviewDto review) {
		log.info("Controller reviewAdd Start");
		log.info("reviewInfo : " + review);
	    // 리뷰 구매 검증 완료 후 인서트
	    int reviewInsert = talentService.addReview(review);
	    log.info("Review Insert Success 1-> "+ reviewInsert );
	    
	    return reviewInsert;
	}
	
	
	// 리뷰 업데이트
	@ResponseBody
	@PutMapping("/talents/{talentNo}/reviews/{reviewNo}")
	public List<ReviewDto> reviewModify(@PathVariable("talentNo") Long talentNo, @PathVariable("reviewNo") Long reviewNo, 
							   @RequestBody ReviewDto review) {
		log.info("Controller reviewModify Start");
		
	    int reviewUpdate = talentService.modifyReview(review);
	    log.info("Review Update Success 1-> "+ reviewUpdate);
	    
		return talentService.findReviewListByTalentNo(talentNo);
	}
	
	
	// 리뷰 딜리트
	@ResponseBody
	@DeleteMapping("/talents/{talentNo}/reviews/{reviewNo}")
	public int reviewRemove(@RequestBody ReviewDto review) {
		log.info("Controller reviewRemove Start");
		
		int reviewRemove = talentService.removeReview(review);
		log.info("Review Delete Success 1-> "+ reviewRemove);
		
		return reviewRemove;
	}
	
	
	// 게시글 상태 판매 종료로 전환
	@ResponseBody
	@PutMapping("/talents/{talentNo}/status")
	public int talentStatusModify(@PathVariable Long talentNo) {
		log.info("Controller talentStatusModify Start");
		
		int talentUpdate = talentService.updateTalentStatus(talentNo);
		log.info("Talent Status Update Success 1-> " + talentUpdate);
		
		return talentUpdate;
	}
	
	
	// 구매하기 버튼 인서트              
	@ResponseBody
	@PostMapping("/talents/{talentNo}/buy")
	public int purchaseAdd(@RequestBody BambooUseDto bambooUse) {
		log.info("Controller purchaseAdd Start");
		
		log.info("구매 회원 정보 -> " + bambooUse);
		
		// 구매자 정보 검증 후에 인서트
		int bambooUseInsert = talentService.addBambooUse(bambooUse);
		log.info("BambooUse Insert Success 1-> " + bambooUseInsert);

		return bambooUseInsert;
	}
	
	
	// 신고하기 
	@ResponseBody
	@PostMapping("/talents/{talentNo}/report")
	public String reportAdd(@PathVariable Long talentNo, @RequestParam("reportId")String reportId, @RequestParam("blackId")String blackId, 
					     @RequestParam("issue")String issue, @RequestParam("reportDate") Timestamp reportDate) {
		log.info("Controller reportAdd Start");
		ReportDto report = new ReportDto();
		report.setBlackId(blackId);
		report.setReportId(reportId);
		report.setIssue(issue);
		report.setReportDate(reportDate);
		report.setTalentNo(talentNo);
		log.info("신고 정보 -> " + report);
		
		String result = talentService.addReport(report);
		log.info("신고 최종 결과 -> " + result);
		
		return result;
	}
	

	
}
