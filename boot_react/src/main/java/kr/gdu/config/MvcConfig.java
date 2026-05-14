package kr.gdu.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MvcConfig implements WebMvcConfigurer{
	@Value("${board.upload.dir}")
	private String BOARD_UPLOAD_DIR;
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저에서 /img/board/** 로 시작하는 주소로 접근하면
        // 실제 로컬의 BOARD_UPLOAD_DIR 폴더에서 파일을 찾도록 설정
        registry.addResourceHandler("/img/board/**")
                .addResourceLocations("file:///" + BOARD_UPLOAD_DIR+"/img/board/");
    }	
}
