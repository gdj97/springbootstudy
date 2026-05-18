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
		
		//커스텀 설정한 로그인 
		http.formLogin((auth)->auth.loginPage("/login") //로그인 요청 페이지 
			 .loginProcessingUrl("/loginProc")   //로그인 form의 action 값
             .defaultSuccessUrl("/my", true)     //로그인 성공시 호출되는 페이지. 
                                              //true:무조건 /my , false : 요청페이지로 이동. 그외는 /my페이지 이동
			 .permitAll()                      //로그인 페이지는 누구나 접근 가능
 		 );		 
		
		//로그 아웃 설정
		http.logout(logout->logout
				 .logoutUrl("/logout") //로그아웃 요청 url
				 .logoutSuccessUrl("/login") // 로그아웃 후 요청되는 페이지
				 .invalidateHttpSession(true)//세션 무효화. 
				 .deleteCookies("JSESSIONID")//쿠키 삭제
				 .permitAll());
		 
	     http.sessionManagement((auth) -> auth
	    		 .sessionFixation().changeSessionId() //로그인시 세션ID 새로 발급
	             .maximumSessions(1)                  //아이디별 최대 세션수 1개로 제한. 중복 로그인 방지
	             .maxSessionsPreventsLogin(true));    //새로운 로그인 제한
	     
	     /*
	      * CSRF(Cross-Site Request Forgery) : 사이트 요청 위조
	      *  => 사용자가 의도하지 않은 요청을 수행하도록 하는 공격 방식
	      *  SpringSecurity는 기본적으로 POST,PUT,DELETE 요청시 CSRF 토큰을 요구함
	      */
	     http.csrf((auth)->auth.disable());  //csrf인증 해제. 권장하지 않음. 세션기반 인증시 활성화하는 것을 권장
		 
		 return http.build();		
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() { //비밀번호 암호화 객체
		return new BCryptPasswordEncoder();
	}	
}
