package com.kakao.jPanda.lhw.controller;

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

import com.kakao.jPanda.kyg.service.ChargeService;
import com.kakao.jPanda.lhw.domain.BambooUseDto;
import com.kakao.jPanda.lhw.domain.ReportDto;
import com.kakao.jPanda.lhw.domain.ReviewDto;
import com.kakao.jPanda.lhw.domain.TalentDto;
import com.kakao.jPanda.lhw.service.TalentService;

import lombok.RequiredArgsConstructor;
@Controller
@RequiredArgsConstructor
@RequestMapping("/talents")
public class TalentDetailController {

	private final TalentService talentService;
	private final ChargeService chargeService;
	
	
	// 재능 상세페이지 및 리뷰 리스트 불러오기
	@GetMapping("/talent/{talentNo}")
	public String talentDetails(@PathVariable Long talentNo, Model model, HttpSession session) {
		System.out.println("Controller talentDetails Start");
		TalentDto talent = talentService.findBoardTalentByTalentNo(talentNo);
		int viewCount = talentService.modifyTalentToViewCount(talentNo);
		List<ReviewDto> reviewList = talentService.findReviewListByTalentNo(talentNo);
		
		String memberId = (String)session.getAttribute("memberId");

		model.addAttribute("memberId", memberId);
		model.addAttribute("talent", talent);
		model.addAttribute("reviewList", reviewList);
		return "lhw/talent";
	}
	
	
	// 리뷰 인서트
	@ResponseBody
	@PostMapping("/talent/{talentNo}/reviews")
	public int reviewAdd(@PathVariable("talentNo") Long talentNo, @RequestBody ReviewDto review, HttpSession session, Model model) {
	    System.out.println("Controller reviewAdd Start");
	    List<BambooUseDto> bambooUseList = talentService.findBambooUseListByTalentNo(talentNo);
	    
	    String memberId = (String)session.getAttribute("memberId");
	    review.setTalentNo(talentNo); // talentNo를 review 객체에 설정
	    review.setReviewerId(memberId); // reviewerId를 review 객체에 설정

	    int result = -1;

	    for (BambooUseDto bambooUseDto : bambooUseList) {
	        if (memberId == null) {
	            // 로그인이 안되어 있을 때
	            result = -1;
	            break;
	        } else if (bambooUseDto.getBuyerId().equals(memberId)) {
	            // 구매자일 때
	            if (talentService.addReview(review) == 1) {
	                result = 1;
	                break;
	            }
	        } else {
	            // 해당 재능의 구매자가 아닐 때
	            result = 0;
	        }
	    }
	    model.addAttribute("memberId", memberId);
	    System.out.println("리뷰 인서트 완료시1 -> " + result);
	    return result;
	}
	
	
	// 리뷰 업데이트
	@ResponseBody
	@PutMapping("/talent/{talentNo}/reviews/{reviewNo}")
	public List<ReviewDto> reviewModify(@PathVariable("talentNo") Long talentNo, @PathVariable("reviewNo") Long reviewNo, 
							   @RequestBody ReviewDto review, HttpSession session) {
		System.out.println("Controller reviewModify Start");
		System.out.println("talentNo : " + talentNo);
		System.out.println("reviweNo : " + reviewNo);
		String memberId = (String)session.getAttribute("memberId");
		review.setReviewerId(memberId);
	    int modifyReview = talentService.modifyReview(review);
	    System.out.println("리뷰 수정 완료시 1 -> " + modifyReview);
	    
		return talentService.findReviewListByTalentNo(talentNo);
	}
	
	
	// 리뷰 딜리트
	@ResponseBody
	@DeleteMapping("/talent/{talentNo}/reviews/{reviewNo}")
	public List<ReviewDto> reviewRemove(@PathVariable("talentNo") Long talentNo, @PathVariable("reviewNo") Long reviewNo,
									@RequestBody ReviewDto review, HttpSession session) {
		System.out.println("Controller reviewRemove Start");
		int reviewRemove = talentService.removeReview(review);
		System.out.println("리뷰 삭제 완료시1 -> " + reviewRemove);
		return talentService.findReviewListByTalentNo(talentNo);
	}
	
	
	// 게시글 삭제 버튼 눌렀을때 게시글 상태 판매 종료로 전환
	@GetMapping("/talent/updateStatus/{talentNo}")
	public String talentStatusUpdate(@PathVariable Long talentNo) {
		talentService.updateTalentStatus(talentNo);
		return "redirect:/board";
	}
	
	
	// 구매하기 버튼 인서트
	@ResponseBody
	@PostMapping("/talent/purchase")
	public int purchaseAdd(@RequestBody BambooUseDto bambooUse, HttpSession session) {
		System.out.println("Controller purchaseAdd Start");
		// memberId 변수에 세션안에 있는 memberId 저장
		String memberId = (String)session.getAttribute("memberId");
		
		// bambooUse 객체 안에 memberId 저장 
		bambooUse.setBuyerId(memberId);
		System.out.println("구매 회원 정보 -> " + bambooUse);
		
		// 해당하는 memberId의 totalBamboo를 저장
		Long totalBamboo = chargeService.findTotalBambooByMemberId(memberId);
		System.out.println("totalBamboo -> " + totalBamboo);
		
		// 구매자 정보 검증 후에 인서트
		int bambooUseInsert = talentService.addBambooUse(bambooUse, totalBamboo);
		System.out.println("구매 완료시1 -> " + bambooUseInsert);

		return bambooUseInsert;
	}
	
	// 신고하기 조회
	@ResponseBody
	@PostMapping("/talent/report/{talentNo}")
	public String reportAdd(@PathVariable Long talentNo, @RequestParam("reportId")String reportId, @RequestParam("blackId")String blackId, 
					     @RequestParam("issue")String issue, @RequestParam("reportDate") Timestamp reportDate) {
		System.out.println("Controller reportAdd Start");
		ReportDto report = new ReportDto();
		report.setBlackId(blackId);
		report.setReportId(reportId);
		report.setIssue(issue);
		report.setReportDate(reportDate);
		report.setTalentNo(talentNo);
		System.out.println("신고 정보 -> " + report);
		
		String result = talentService.addReport(report);
		System.out.println("신고 최종 결과 -> " + result);
		
		return result;
	}
	

	
}
