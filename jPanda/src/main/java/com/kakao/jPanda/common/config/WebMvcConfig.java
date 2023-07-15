package com.kakao.jPanda.common.config;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kakao.jPanda.common.interceptor.AdminInterceptor;
import com.kakao.jPanda.common.interceptor.CommonInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	
//	@Autowired
//	private CommonInterceptor commonInterceptor;
//	@Autowired
//	private AdminInterceptor adminInterceptor;
//	
//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(commonInterceptor)
//				.addPathPatterns("/**")
//				.excludePathPatterns("");
//		
//		registry.addInterceptor(adminInterceptor)
//				.addPathPatterns("/admin/**");
//	}
	
	
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
    }
    
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String path = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "uploadImage").toString() + File.separator;
		// path = /static/uploadImage/
		registry.addResourceHandler("/uploadImage/**").addResourceLocations("file:" + path);
		// /uploadImage/**로 시작하는 경로를 요청할 시 외부 디텍토리에서 찾도록 설정
	}
	
}
