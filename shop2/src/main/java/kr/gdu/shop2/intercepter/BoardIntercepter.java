package kr.gdu.shop2.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

import kr.gdu.shop2.dto.User;
import kr.gdu.shop2.exception.ShopException;
/*
 * Intercepter : Spring 기능
 *   DispatcherServlet과 Controller 사이에서 동작
 *   요청 URL을 기준으로 중간에 동작함.
 *   
 * Filter와 차이 : Wep Application의 기능. Servlet 보다 먼저 실행
 * 
 * 주요 메서드
 *   preHandle : Controller 호출 전
 *   postHandle : Controller 호출 후
 *   afterCompletion : 뷰 호출 완료 후
 */
public class BoardIntercepter implements HandlerInterceptor{
	// /board/write, /board/update, /board/delete 요청시 호출
	// preHandle : BoardController 메서드 실행 전 호출
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String boardid = request.getParameter("boardid"); //파라미터 조회
		HttpSession session = request.getSession();       //session 객체 
		User login = (User)session.getAttribute("loginUser"); //로그인 정보
		if(boardid == null || boardid.equals("1")) {  //공지사항인 경우
			if(login == null || !login.getUserid().equals("admin")) { //로그아웃상태 또는 일반사용자 로그인인 경우
				throw new ShopException("관리자만 거래 가능합니다.",
						                request.getContextPath()+"/board/list?boardid=1");
			}
		}
		return true;
	}

}
