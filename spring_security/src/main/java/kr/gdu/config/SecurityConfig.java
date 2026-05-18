package kr.gdu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public HttpSessionEventPublisher httpSessionEventPhblisher() {
		return new HttpSessionEventPublisher();
	}
	/*
	 * HttpSecurity : spring Security의 보안 설정을 담당하는 객체
	 *                http 요청시 인증, 권한을 정의할 수 있는 객체
	 * authorizeHttpRequests() : url에 따라 권한(authorization) 설정
	 * requestMatchers(요청url) : 권한 설정
	 * permitAll() : 모두 허용. 로그아웃 상태도 접근 허용
	 * anyRequest() : 그외의 요청
	 * authenticated() : 인증 필요
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http.authorizeHttpRequests((auth)->auth
				 .requestMatchers("/","/login","/home","/join","/joinProc").permitAll()  //모두 허용
				 .requestMatchers("/admin").hasRole("ADMIN") //ADMIN 권한의 사용자만 접근 허용
				 .requestMatchers("/my/*").hasAnyRole("ADMIN","USER") //ADMIN,USER 권한의 사용자 접근 허용
				 .anyRequest().authenticated());   //그외 url을 인증을 받은 사용자만 접근 허용
		
		http.formLogin((auth)->auth.loginPage("/login")
			 .loginProcessingUrl("/loginProc")
             .defaultSuccessUrl("/my", true)  
			 .permitAll()
 		 );		 
		 http.logout(logout->logout
				 .logoutUrl("/logout") 
				 .logoutSuccessUrl("/login")
				 .invalidateHttpSession(true)
				 .deleteCookies("JSESSIONID")
				 .permitAll());
		 
	     http.sessionManagement((auth) -> auth
	    		 .sessionFixation().changeSessionId()
	             .maximumSessions(1) 
	             .maxSessionsPreventsLogin(true));
		 http.csrf((auth)->auth.disable());
		 return http.build();		
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}	
}
