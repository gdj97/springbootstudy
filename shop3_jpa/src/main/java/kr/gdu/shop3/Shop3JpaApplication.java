package kr.gdu.shop3;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import jakarta.annotation.PostConstruct;
import kr.gdu.shop3.sitemesh.SitemeshFilter;

@SpringBootApplication
public class Shop3JpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(Shop3JpaApplication.class, args);
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
