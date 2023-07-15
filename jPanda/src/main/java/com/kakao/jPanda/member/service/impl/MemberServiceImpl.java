package com.kakao.jPanda.member.service.impl;

import java.util.List;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.kakao.jPanda.member.dao.MemberDao;
import com.kakao.jPanda.member.domain.BankDto;
import com.kakao.jPanda.member.domain.EmailVerifDto;
import com.kakao.jPanda.member.domain.MemberDto;
import com.kakao.jPanda.member.service.MemberService;
import com.kakao.jPanda.member.service.PasswordEncryptor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
	
	private final MemberDao memberDao;
    private final JavaMailSender mailSender;

	
	@Override
	public List<BankDto> findBankList() {
		
		return memberDao.selectBankList();
	}

	@Override
	public boolean joinMember(MemberDto memberInfo) {

	    String encryptedPassword = PasswordEncryptor.encrypt(memberInfo.getPassword());
	    memberInfo.setPassword(encryptedPassword);
	    boolean isJoinSuccessful = memberDao.insertMember(memberInfo);
	    return isJoinSuccessful;		
	}

	@Override
	public int checkId(String memberId) {
		
		return memberDao.checkId(memberId);
	}

	@Override
	public String findPwByIdAndEmail(String memberId, String email) {

		return memberDao.findPwByIdAndEmail(memberId, email);
	
	}

	@Override
	public String findIdByNameAndEmail(String name, String email) {
		
		return memberDao.findIdByNameAndEmail(name, email);
	
	}

	@Override
	public boolean login(MemberDto memberDto) {
		
	    String encryptedPassword = PasswordEncryptor.encrypt(memberDto.getPassword()); // 비밀번호 암호화
	    memberDto.setPassword(encryptedPassword);
		MemberDto loginMemberDto = memberDao.login(memberDto);
  
		System.out.println("loginMemberDto = "+loginMemberDto);
	    if (loginMemberDto != null) {
	        String status = loginMemberDto.getMemberStatus();
	        if (status.equals("Normal_role") || loginMemberDto.getMemberId().equals("admin")) { // "member_status"가 "normal_role"이거나 사용자 ID가 "admin"인 경우 로그인 허용
	            return true;
	        } else {
	            return false;
	        }
	    } else {
	        return false;
	    }
	}

	@Override
	public MemberDto findMember(String memberId) {

		
			return memberDao.selectMember(memberId);
		
	}

	@Override
	public void updatePasswordById(String memberId, String newPw) {

	    	String encryptedPassword = PasswordEncryptor.encrypt(newPw); // 비밀번호 암호화
			
	    	memberDao.updatePasswordById(memberId,encryptedPassword);
	}

	@Override
	public boolean withdrawal(String memberId, String password) {
		MemberDto member = memberDao.selectMember(memberId);
		String SavedencryptedPassword = member.getPassword(); 
		String encryptedPassword = PasswordEncryptor.encrypt(password); // 비밀번호 암호화하기
	    
	    if (encryptedPassword.equals(SavedencryptedPassword)) {
	        return memberDao.withdrawMemberById(memberId, encryptedPassword);
	    } else {
	        return false;
	    }
	}

	@Override
	public void editMemberInfo(MemberDto memberInfo) {
		
		memberDao.updateMemberInfo(memberInfo);
	}

	@Override
	public void insertVerificationCode(EmailVerifDto emailVerifDto) {

		memberDao.insertVerificationCode(emailVerifDto);
	}

	@Override
	public EmailVerifDto findEmailVerif(EmailVerifDto emailVerifDto) {
		EmailVerifDto emailVerif = memberDao.selectEmailVerif(emailVerifDto);
		return emailVerif;
	}
	
	@Override
	public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

	@Override
	public MemberDto findMemberByMemberId(String memberId) {
		return memberDao.selectMemberBtMemberId(memberId);
	}

	
}

