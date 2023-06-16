package com.kakao.jPanda.talent.register.service.impl;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.kakao.jPanda.talent.register.dao.RegistTalentDao;
import com.kakao.jPanda.talent.register.domain.CategoryDto;
import com.kakao.jPanda.talent.register.domain.TalentDto;
import com.kakao.jPanda.talent.register.service.RegistTalentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistTalentServiceImpl implements RegistTalentService{
	private final RegistTalentDao registTalentDao;
	
	@Override
	public List<CategoryDto> findCategorys() {
		List<CategoryDto> categoryList = registTalentDao.selectCategorys();
		return categoryList;
	}
	
	@Override
	public String addTalent(TalentDto talent, HttpSession session) {
		talent.setSellerId((String) session.getAttribute("memberId"));
		int insertResult = registTalentDao.insertTalent(talent);
		if(insertResult > 0) {
			return "<script> " +
					"alert('재능 등록이 완료되었습니다.');" + 
					"location.href = '/trade';" + 
					"</script>";
		}else {
			return "<script> " +
					"alert('재능 등록이 완료되지 않았습니다. 다시 확인해주세요.');" + 
					"history.back();" + 
					"</script>";
		}
	}
	
	
	@Override
	public TalentDto findTalentByTalentNo(Long talentNo) {
		TalentDto talent = registTalentDao.selectTalentBytalentNo(talentNo);
		
		return talent;
	}

	@Override
	public String modifyTalent(String talentNo, TalentDto talent, HttpSession session) {
		String sellerId = findSellerIdByTalent(talent);
		if(!sellerId.equals((String) session.getAttribute("memberId"))) {
			return "<script>" +
					"alert('비정상적인 재능 수정입니다. 다시 로그인해 주세요.');" + 
					"location.href = '/login';" + 
					"</script>";
		}else {
			int updateResult = registTalentDao.updateTalent(talent);
			if(updateResult > 0) {
				return "<script> " +
						"alert('재능 수정이 완료되었습니다.');" + 
						"location.href = '/trade';" + 
						"</script>";
			}else {
				return "<script> " +
						"alert('재능 수정이 완료되지 않았습니다. 다시 확인해주세요.');" + 
						"history.back();" + 
						"</script>";
			}
		}
	}
	
	@Override
	public ModelAndView talentImageUpload(MultipartHttpServletRequest request) {
		
		// ckeditor는 이미지 업로드 후 이미지 표시하기 위해 uploaded 와 url을 json 형식으로 받아야 함
		// modelandview를 사용하여 json 형식으로 보내기위해 모델앤뷰 생성자 매개변수로 jsonView 라고 써줌
		// jsonView 라고 쓴다고 무조건 json 형식으로 가는건 아니고 @Configuration 어노테이션을 단 
		// WebConfig 파일에 MappingJackson2JsonView 객체를 리턴하는 jsonView 매서드를 만들어서 bean으로 등록해야 함 
		ModelAndView mav = new ModelAndView("jsonView");
		
		// ckeditor 에서 파일을 보낼 때 upload : [파일] 형식으로 해서 넘어오기 때문에 upload라는 키의 밸류를 받아서 uploadFile에 저장함
		MultipartFile uploadFile = request.getFile("upload");
		
		// 파일의 오리지널 네임
		String originalFileName = uploadFile.getOriginalFilename();
		
		// 파일의 확장자 추출
		String ext = originalFileName.substring(originalFileName.indexOf("."));
		
		// 서버에 저장될 때 중복된 파일 이름인 경우를 방지하기 위해 UUID에 확장자를 붙여 새로운 파일 이름을 생성
		String newFileName = UUID.randomUUID() + ext;
		
		// System.getProperty("user.dir")은 현재 프로젝트의 경로를 출력함
		// File.separator는 파일 구분자로 /로 대체가 가능하지만 여러 운영 체제에서 이식성을 위해 사용
//		String path = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "uploadImage").toString() + File.separator;
		int index = System.getProperty("user.dir").indexOf(File.separator + "jPanda");
		String path = System.getProperty("user.dir").substring(0, index) + File.separator + "uploadImage" + File.separator;
		// /static/uploadImage/업로드할 이미지 파일 이름
		String savePath = path + newFileName;
		
		
		// 해당 파일 경로에 폴더가 없을시 폴더 생성
		File fileDirectory = new File(savePath);
		if(!fileDirectory.exists()) {
			// 신규 폴더(Directory)생성
			fileDirectory.mkdirs();
		}
		
		// 반환할 경로
		// /uploadImage/업로드할 이미지 파일 이름으로 경로를 반환해주는 이유는
		// /uploadImage/**로 시작하는 경로를 요청하면 WebMvcConfig에서 /static/uploadImage 경로에서 찾도록 설정함
		String uploadPath = "/uploadImage/" + newFileName; 
		
		// 저장 경로로 파일 객체 생성
		File file = new File(savePath);
		
		// 파일 업로드
		try {
			uploadFile.transferTo(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// uploaded, url 값을 Modelandview를 통해 보냄
		mav.addObject("uploaded", true);
		mav.addObject("url", uploadPath);
		return mav;
	}
	
	@Override
	public List<TalentDto> findBestSellerTalents() {
		return registTalentDao.selectBestSellerTalents();
	}
	
	@Override
	public List<TalentDto> findTopRatedTalents() {
		return registTalentDao.selectTopRatedTalents();
	}
	
	@Override
	public List<TalentDto> findNewTrendTalents() {
		return registTalentDao.selectNewTrendTalents();
	}
	
	@Override
	public List<TalentDto> findRandomTalents() {
		return registTalentDao.selectRandomTalents();
	}

	private String findSellerIdByTalent(TalentDto talent) {
		return registTalentDao.selectSellerIdByTalent(talent);
	}

}
