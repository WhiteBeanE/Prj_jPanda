package com.kakao.jPanda.talent.register.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.kakao.jPanda.talent.register.domain.CategoryDto;
import com.kakao.jPanda.talent.register.domain.TalentDto;

public interface RegistTalentService {

	List<CategoryDto> 	findCategorys();
	String 			addTalent(TalentDto talent, HttpSession session);
	ModelAndView 	talentImageUpload(MultipartHttpServletRequest request);
	TalentDto 		findTalentByTalentNo(Long talentNo);
	String 			modifyTalent(String talentNo, TalentDto talent, HttpSession session);
	List<TalentDto> findBestSellerTalents();
	List<TalentDto> findTopRatedTalents();
	List<TalentDto> findNewTrendTalents();
	List<TalentDto> findRandomTalents();
	
}
