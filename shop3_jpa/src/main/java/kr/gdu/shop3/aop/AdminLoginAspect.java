package kr.gdu.shop3.aop;

//springboot3부터는 Tomcat10환경임. (javax.servlet. => jakarta.servlet )
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import kr.gdu.shop3.dto.UserDto;
import kr.gdu.shop3.exception.ShopException;

@Component
@Aspect
public class AdminLoginAspect {
	@Around("execution(* kr.gdu.shop3.controller.AdminController.*(..))")
	public Object adminCheck(ProceedingJoinPoint joinPoint) throws Throwable {		
		HttpSession session = null;
		//RequestContextHolder : request,response 객체 전달
		ServletRequestAttributes attributes = 
				(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	    if (attributes != null) {
	        HttpServletRequest request = attributes.getRequest(); //request 객체 리턴
	        session = request.getSession(); //session  객체
	    }    
		UserDto loginUser = (UserDto)session.getAttribute("loginUser");
		if(loginUser == null || !(loginUser instanceof UserDto)) {
			throw new ShopException("[adminCheck]로그인 하세요","../user/login");
		}
		else if (!loginUser.getUserid().equals("admin")) {
			throw new ShopException
			("[adminCheck]관리자만 가능한 거래 입니다","../user/mypage?userid="+loginUser.getUserid());
		}
		return joinPoint.proceed();
	}
	
}
