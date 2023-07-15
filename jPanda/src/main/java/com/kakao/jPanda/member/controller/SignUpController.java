package com.kakao.jPanda.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kakao.jPanda.common.annotation.NoLoginCheck;
import com.kakao.jPanda.member.domain.BankDto;
import com.kakao.jPanda.member.domain.EmailVerifDto;
import com.kakao.jPanda.member.domain.MemberDto;
import com.kakao.jPanda.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SignUpController {
	
	private final MemberService memberservice;
	
	
    @GetMapping("/signup")
    public String signupForm(Model model) {
 	    List<BankDto> foundBankList = memberservice.findBankList();
 	    model.addAttribute("bankList", foundBankList);
    	return "signup/signup";
    }

	
	@PostMapping("/members/signup")
	public String join(MemberDto memberInfo) {
	    boolean isJoinSuccessful = memberservice.joinMember(memberInfo);

	    if (isJoinSuccessful) {
	        // 회원가입 성공
	    	log.info("[join] 회원가입 성공 여부 - {}",isJoinSuccessful);
	        return "redirect:/login";
	    } else {
	        // 회원가입 실패
	    	log.info("[join] 회원가입 성공 여부 - {}",isJoinSuccessful);
	        return "redirect:/signup";
	    }
	}
	
	
    @GetMapping("/members/id/id")
    @ResponseBody
    public Map<String, String> checkId(@RequestParam String memberId) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            int count = memberservice.checkId(memberId);

            if (count == 0) {
                resultMap.put("result", "success");
            } else {
                resultMap.put("result", "fail");
            }
        } catch (Exception e) {
            resultMap.put("result", "error");
            resultMap.put("errorMessage", e.getMessage());
        }
        return resultMap;
    }
    
	private String generateVerificationCode() {
	    int length = 5; // 생성할 랜덤 문자열의 길이
	    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	    StringBuilder code = new StringBuilder();
	
	    Random random = new Random();
	    for (int i = 0; i < length; i++) {
	        int index = random.nextInt(characters.length());
	        code.append(characters.charAt(index));
	    }
	
	    return code.toString();
	
	}
	
    
    @PostMapping("/sendVerificationCode")
    @ResponseBody
    public ResponseEntity<String> sendVerificationCode(@RequestParam("email") String email) {
        String verificationCode = generateVerificationCode();
        log.info("[sendVerificationCode] verificationCode {}",verificationCode);
        EmailVerifDto emailVerifDto = new EmailVerifDto();
        emailVerifDto.setEmail(email);
        emailVerifDto.setVerifCode(verificationCode);
        
        memberservice.insertVerificationCode(emailVerifDto);
        
        String to =email;
        String subject = "인증번호";
        String body = "인증번호 : " + verificationCode;
        memberservice.sendEmail(to, subject, body);
        
        return ResponseEntity.ok("success");
        
    }
    
    
    @GetMapping("/verifyCode")
    @ResponseBody
    public ResponseEntity<String> verifyCode(EmailVerifDto emailVerifDto) {
    	
    	EmailVerifDto foundedEmailVerif = memberservice.findEmailVerif(emailVerifDto);

        if (foundedEmailVerif != null) {
            return ResponseEntity.ok("일치");
        } else {
            return ResponseEntity.ok("불일치");
        }
    	
    }

}
