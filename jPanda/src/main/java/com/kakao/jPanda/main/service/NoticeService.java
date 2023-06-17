package com.kakao.jPanda.main.service;

import java.util.Map;

import com.kakao.jPanda.main.domain.NoticeDto;
import com.kakao.jPanda.main.domain.PagerDto;

public interface NoticeService {
	Map<String, Object> findNoticesByPager(PagerDto pager);
	NoticeDto 					findNoticeByNoticeNo(Long noticeNo);
}
