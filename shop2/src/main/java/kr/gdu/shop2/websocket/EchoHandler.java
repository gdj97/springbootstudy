package kr.gdu.shop2.websocket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import kr.gdu.shop2.dto.User;
//웹 채팅의 서버기능
@Component
public class EchoHandler extends  TextWebSocketHandler implements InitializingBean {
	private Set<WebSocketSession> clients; //현재 접속된 모든 브라우저(클라이언트)의 WebSocketSession 객체
	
	//Handler의 객체 준비되면 메서드가 한번 호출됨. 
	@Override
	public void afterPropertiesSet() throws Exception {
		//동기화기능을 추가한 형태로 변환
		clients = Collections.synchronizedSet(new HashSet<WebSocketSession>());
	}
	
	//브라우저에서 연결 시도시 성공한 경우 호출
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		System.out.println("클라이언트 접속 : " + session.getId());
		clients.add(session);
		//WebSocketConfig 클래스에서 .addInterceptors(new HttpSessionHandshakeInterceptor()) 설정이 필요함
		Map<String, Object> map = session.getAttributes();
//		User loginUser = (User)map.get("loginUser"); //HttpSession에 설정된 로그인정보 조회
//		System.out.println(loginUser.getUserid());   //로그인된 아이디정보 화면에 출력함
	}

	//브라우저에서 메시지를 전송한 경우. 메세지 서버가 수신함
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		String loadMessage = (String)message.getPayload(); //클라이언트가 전송한 메세지
		System.out.println(session.getId() + ":클라이언트 메세지:" + loadMessage);
		clients.add(session); //Set 같은 객체를 추가 안됨. 필요없는 로직
		for(WebSocketSession s : clients) {
			s.sendMessage(new TextMessage(loadMessage)); //모든 클라이언트에게 수신된 메세지 전송. 브로드캐스트(broadcast)
		}
	}

	//접속에 오류 발생 : 네크워크가 끊기거나, 브라우저를 닫은 경우
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		super.handleTransportError(session,exception); //TextWebSocketHandler 클래스의 메서드
		System.out.println("오류발생 : " + exception.getMessage());
	}

	//접속이 종료된 후 실행
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		super.afterConnectionClosed(session, closeStatus);
		System.out.println("클라이언트 접속 해제 : " + closeStatus.getReason());
		clients.remove(session); //클라이언트 목록에서 제거
	}

//	@Override   //TextWebSocketHandler 에서 이미 구현됨
//	public boolean supportsPartialMessages() { //수신된 메세지를 한번에 수신함. 
//		return false;  //기본값
//	}
}
