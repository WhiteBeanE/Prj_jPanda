package com.kakao.jPanda.main.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kakao.jPanda.main.domain.NoticeDto;
import com.kakao.jPanda.main.domain.PagerDto;
import com.kakao.jPanda.main.service.NoticeService;
import com.kakao.jPanda.talent.register.dao.NoticeDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
	private final NoticeDao noticeDao;
	
	@Override
	public Map<String, Object> findNoticesByPager(PagerDto pager) {
		Map<String, Object> map = new HashMap<String, Object>();
		int totalCount = noticeDao.selectNoticeCountByPager(pager);
		pagerSetting(pager, totalCount);
		List<NoticeDto> noticeList = findNoticeListByPager(pager);
		
		map.put("noticeList", noticeList);
		map.put("pager", pager);
		
		return map;
	}
	
	private void pagerSetting(PagerDto pager, int totalCount) {
		int currentPage = pager.getCurrentPage();
		int perPage = pager.getPerPage();
		int pageBlock = pager.getPageBlock();
		int startRow = (currentPage - 1) * perPage + 1;
		int endRow = startRow + perPage - 1;
		int totalPage = (int)Math.ceil((double)totalCount / perPage);
		int startNum = currentPage - (currentPage - 1) % pageBlock;
		int lastNum = startNum + pageBlock -1;
		if(lastNum > totalPage) lastNum = totalPage;
		
		pager.setStartRow(startRow);
		pager.setEndRow(endRow);
		pager.setTotalPage(totalPage);
		pager.setStartNum(startNum);
		pager.setLastNum(lastNum);
	}

	private List<NoticeDto> findNoticeListByPager(PagerDto pager) {
		return noticeDao.selectNoticesByPager(pager);
	}

	@Override
	public NoticeDto findNoticeByNoticeNo(Long noticeNo) {
		
		return noticeDao.selectNoticeByNoticeNo(noticeNo);
	}
}
