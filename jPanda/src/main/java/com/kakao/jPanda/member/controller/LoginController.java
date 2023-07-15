package com.kakao.jPanda.member.controller;

import java.security.SecureRandom;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kakao.jPanda.common.annotation.NoLoginCheck;
import com.kakao.jPanda.member.domain.MemberDto;
import com.kakao.jPanda.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
	
	private final MemberService memberservice;

	
    @GetMapping("/login")
    public String login(Model model) {
        return "login/login";
    }
	
//    
//    @PostMapping("/login")
//    public String login(@ModelAttribute MemberDto memberDto, HttpSession session,RedirectAttributes redirectAttributes) {
//    	boolean loginResult = memberservice.login(memberDto);
//    	if(loginResult) {
//    		session.setAttribute("memberId", memberDto.getMemberId());
//    		String memberId = (String) session.getAttribute("memberId");   		
//            MemberDto member = memberservice.findMember(memberId);
//            log.info("[login] member {}", member);
//            if(memberId.equals("admin")) {
//            	return "redirect:/admin";
//            } 
//           
//            redirectAttributes.addFlashAttribute("memberInfo", member);
//    		return "redirect:/main";
//    	}else {
//    		redirectAttributes.addFlashAttribute("alertMsg", "로그인 할 수 없습니다.");
//    		return "redirect:/login";
//    	}
//    }
    
    
    @ResponseBody
    @DeleteMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Logout success";
    }
	
	
    @GetMapping("/find")
    public String find(Model model) {
    	return "login/find";
    }

	
    @GetMapping("/findid")
    public ResponseEntity<String> findIdByNameAndEmail(@RequestParam String name, @RequestParam String email) {
        String memberId = memberservice.findIdByNameAndEmail(name, email);
        log.info("[findIdByNameAndEmail] memberId {}", memberId);
        if(memberId == null) {
            return new ResponseEntity<String>("해당하는 정보가 없습니다.", HttpStatus.OK);
        } else {
        	String hiddenId = memberId.substring(0, memberId.length()-3) + "***"; // 마지막 3글자를 ***로 대체
            return new ResponseEntity<String>("아이디 : "+ "[ "+hiddenId+" ]" , HttpStatus.OK);
        }
    }
    
    @GetMapping("/findpw")
    @ResponseBody
    public ResponseEntity<String> findPwByIdAndEmail(@RequestParam String memberId, @RequestParam String email) {
        String pw = memberservice.findPwByIdAndEmail(memberId, email);
        log.info("[findPwByIdAndEmail] pw {}", pw);
        if(pw == null || pw.isEmpty()) {
            return new ResponseEntity<String>("해당하는 정보가 없습니다.", HttpStatus.OK);
        } else {
            String newPw = generateNewPassword();
            memberservice.updatePasswordById(memberId, newPw);
            String to = email; // 수신자 이메일 주소
            String subject = "비밀번호 찾기 결과"; // 이메일 제목
            String body = "비밀번호 : " + "[ " + newPw + " ]"; // 이메일 본문
            memberservice.sendEmail(to, subject, body); // 이메일 발송
            return new ResponseEntity<String>("비밀번호를 초기화 했습니다. 이메일을 확인해주세요.", HttpStatus.OK);
        }
    }
    
    public String generateNewPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase(Locale.ROOT);
        String digits = "0123456789";
        String alphanum = upper + lower + digits;

        SecureRandom random = new SecureRandom();
        char[] password = new char[random.nextInt(8) + 1];
        for (int i = 0; i < password.length; i++) {
            password[i] = alphanum.charAt(random.nextInt(alphanum.length()));
        }
        return new String(password);
    }

}
