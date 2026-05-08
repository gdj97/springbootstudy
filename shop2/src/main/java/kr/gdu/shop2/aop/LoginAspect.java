package kr.gdu.shop2.aop;

import jakarta.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import kr.gdu.shop2.dto.User;
import kr.gdu.shop2.exception.LoginException;

@Component
@Aspect
public class LoginAspect {
	/*
	 * advice : @Around : 필수메서드 실행 전,후 호출됨
	 * pointcut : controller 패키지에 User로 시작하는 클래스의 메서드 중 idCheck 이름으로 시작하는 메서드 이면서
	 *            매개변수목록이 String,HttpSession으로 끝나는 메서드 
	 */
	@Around("execution(* kr.gdu.shop2.controller.User*.idCheck*(..)) && args(..,userid,session)")
	public Object userIdCheck(ProceedingJoinPoint joinPoint,
								String userid,HttpSession session) throws Throwable {
	   User loginUser = (User)session.getAttribute("loginUser");	
	   if(loginUser == null || !(loginUser instanceof User)) { //로그아웃상태
		   throw new LoginException("[idCheck]로그인이 필요합니다.","../user/login");
	   }
	   if(!loginUser.getUserid().equals("admin") && !loginUser.getUserid().equals(userid)) {
		   throw new LoginException
		   ("[idCheck]본인만 거래 가능합니다.","../user/mypage?userid="+loginUser.getUserid());
	   }
	   return joinPoint.proceed();	
	}
	/*
	 *pointcut : UserController 클래스에서 메서드이름이 loginCheck로 시작하고, 매개변수의 마지막이 HttpSession인
	 *                   메서드로 설정
	 *advice : Around           
	 */
	@Around("execution(* kr.gdu.shop2.controller.User*.loginCheck*(..)) && args(..,session)")
	public Object loginCheck(ProceedingJoinPoint joinPoint,HttpSession session) throws Throwable {
	   User loginUser = (User)session.getAttribute("loginUser");	
	   if(loginUser == null || !(loginUser instanceof User)) { //로그아웃상태
		   throw new LoginException("[loginCheck]로그인이 필요합니다.","../user/login");
	   }
	   return joinPoint.proceed();	
	}
}
