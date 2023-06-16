package com.kakao.jPanda.talent.register.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.kakao.jPanda.talent.register.domain.CategoryDto;
import com.kakao.jPanda.talent.register.domain.TalentDto;
import com.kakao.jPanda.talent.register.service.RegistTalentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TalentController {
	private final RegistTalentService registTalentService;
	
	// 재능 등록 페이지 이동
	@GetMapping("/talents/write-form")
	public String talentWriteForm(Model model) {
		return "talent/register/talent-write-form";
	}
	
	// 재능 수정 페이지 이동
	@GetMapping("/talents/{talentNo}/edit-form")
	public String talentUpdateFrom(@PathVariable Long talentNo, Model model, HttpSession session) {
		TalentDto talent = registTalentService.findTalentByTalentNo(talentNo);
		model.addAttribute("talent", talent);
		return "talent/register/talent-edit-form";
	}
	
	// 이미지  저장 후 경로 반환
	@ResponseBody
	@PostMapping("/talents/image-upload")
	public ModelAndView talentImageUpload(MultipartHttpServletRequest request) {
		ModelAndView modelAndView = registTalentService.talentImageUpload(request);
		
		return modelAndView;
	}
	
	// 카테고리 불러오기
	@ResponseBody
	@GetMapping("/categorys")
	public List<CategoryDto> findCategoryList(){
		return registTalentService.findCategorys();
	}
	
	// 재능 DB Insert
	@ResponseBody
	@PostMapping("/talents")
	public String talentAdd(TalentDto talent, HttpSession session) {
		return registTalentService.addTalent(talent,session);
	}
	
	// 재능 DB Update
	@ResponseBody
	@PutMapping("/talents/{talentNo}")
	public String talentModify(@PathVariable String talentNo, TalentDto talentDto, HttpSession session) {
		return registTalentService.modifyTalent(talentNo, talentDto, session);
	}
	
}
