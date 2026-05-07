package kr.gdu.shop2;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import jakarta.annotation.PostConstruct;
import kr.gdu.shop2.sitemesh.SitemeshFilter;

@SpringBootApplication
public class Shop2Application {  //SpringBoot의 시작 프로그램.

	public static void main(String[] args) {
		SpringApplication.run(Shop2Application.class, args);
	}
	 @Bean
	 public FilterRegistrationBean<SitemeshFilter> sitemeshFilter() {
		 FilterRegistrationBean<SitemeshFilter> filter = new FilterRegistrationBean<SitemeshFilter>();
		 filter.setFilter(new SitemeshFilter());
		 return filter;
	 }
	 @PostConstruct   //객체 생성 후에 실행되는 메서드
	 void started(){
	     TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul")); //지역 설정
	 }
}
/*
 *    2026-05-06
 *    1. 게시판에 파일 업로드 시 resource 폴더로 업로드하기
 *    2. summernote 이미지 업로드시 resource 폴더로 업로드하기
 */



