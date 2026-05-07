package kr.gdu.shop2.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import kr.gdu.shop2.intercepter.BoardIntercepter;

@Configuration
@EnableAspectJAutoProxy //AOP 관련 설정 
//@EnableWebMvc  //기본 제공되는 web처리 기능 유지
public class MvcConfig implements WebMvcConfigurer{
	@Value("${board.upload.dir}")
	private String BOARD_UPLOAD_DIR;
	
	//예외처리 객체 : 예외발생시 예외 처리해 주는 객체
	@Bean
	public SimpleMappingExceptionResolver exceptionHandler() {
		SimpleMappingExceptionResolver ser = new SimpleMappingExceptionResolver();
		Properties pr = new Properties(); //Hashtable의 하위클래스
		/*
		 * exception.CartException 예외가 발생하면,/WEB-INF/view/exception.jsp를 호출
		 */
		pr.put("exception.CartException", "exception");
		pr.put("exception.LoginException", "exception");
		pr.put("exception.ShopException", "exception");
		ser.setExceptionMappings(pr);
		return ser;
	}
	//인터셉터관련 설정
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new BoardIntercepter())  //인테셉터 객체 설정
		.addPathPatterns("/board/write")   //url 정보 추가
		.addPathPatterns("/board/update")
		.addPathPatterns("/board/delete");		
	}	
	@Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/WEB-INF/views/", ".jsp");
    }
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저에서 /board/** 로 시작하는 주소로 접근하면
        // 실제 로컬의 BOARD_UPLOAD_DIR 폴더에서 파일을 찾도록 설정
        registry.addResourceHandler("/board/**")
                .addResourceLocations("file:///" + BOARD_UPLOAD_DIR);
    }	
}
