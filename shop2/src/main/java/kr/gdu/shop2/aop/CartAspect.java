package kr.gdu.shop2.aop;


import jakarta.servlet.http.HttpSession;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import kr.gdu.shop2.dto.Cart;
import kr.gdu.shop2.dto.User;
import kr.gdu.shop2.exception.CartException;
import kr.gdu.shop2.exception.LoginException;


@Component
@Aspect
public class CartAspect {
	/*
	 * pointcut : 필수메서드
	 *    * : 접근제한자,리턴타입 상관없음
	 *    controller.Cart* : controller 패키지의 Cart로 시작하는 클래스
	 *    check*(..) : 매개변수 목록과 상관없는 메서드의 이름이 check로 시작하는 메서드
	 *    args(..,session) : 매개변수 목록의 마지막 매개변수값이  HttpSession 타입인 메서드
	 *    
	 * advice : @Before.  필수메서드(pointcut으로 지정된 메서드) 실행 전  
	 */
	/*
	 * 1. 로그인 필수
	 * 2. 관리자로 로그인된 경우는 주문 불가
	 */
	@Before("execution(* controller.Cart*.check*(..)) && args(..,session)")
	public void cartCheck(HttpSession session) throws Throwable {
	   Cart cart =(Cart)session.getAttribute("CART"); //등록된 장바구니 객체
	   if(cart == null || cart.getItemSetList().size() == 0) { //장바구니에 상품이 없는 경우
		   throw new CartException("장바구니에 상품을 추가하세요","../item/list");
			//예외 발생시 정상적인 수행을 멈추고, 예외처리 알고리즘 실행
		}
	   User loginUser = (User)session.getAttribute("loginUser");	
	   if(loginUser == null || !(loginUser instanceof User)) { //로그아웃상태
		   throw new LoginException("로그인이 필요합니다.","../user/login");
	   }
	   if(loginUser.getUserid().equals("admin")) {
		   throw new LoginException("관리자는 주문할 수 없습니다","../user/mypage?userid="+ loginUser.getUserid());
	   }
	}
}
