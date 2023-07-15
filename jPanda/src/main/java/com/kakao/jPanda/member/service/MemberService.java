package com.kakao.jPanda.member.service;


import java.util.List;

import com.kakao.jPanda.member.domain.BankDto;
import com.kakao.jPanda.member.domain.EmailVerifDto;
import com.kakao.jPanda.member.domain.MemberDto;


public interface MemberService {

	List<BankDto> findBankList(); 
	
	boolean joinMember(MemberDto memberInfo);
	
	int checkId(String memberId);
	
	String findPwByIdAndEmail(String memberId, String email);
	
	String findIdByNameAndEmail(String name, String email);
	
	boolean login(MemberDto memberDto);
	
	MemberDto findMember(String memberId);
	
	void updatePasswordById(String memberId, String newPw);
	
	boolean withdrawal(String memberId, String password);
	
	void editMemberInfo(MemberDto memberInfo);
	
	void insertVerificationCode(EmailVerifDto emailVerifDto);
	
	EmailVerifDto findEmailVerif(EmailVerifDto emailVerifDto);
	
	void sendEmail(String to, String subject, String body);

	MemberDto findMemberByMemberId(String memberId);

}
