<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<%-- /WEB-INF/view/chat/chat.jsp --%>    
<c:set var="port" value="${pageContext.request.localPort}" /><%-- 포트번호 :  8080 --%>
<c:set var="server" value="${pageContext.request.serverName}" /><%-- IP주소 : localhost --%>
<c:set var="path" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>websocket client</title>
</head>
<body>
<p><div id="chatStatus"></div>
<textarea name="chatMsg" rows="15" cols="40" class="form-control"></textarea><br>
메시지입력:<input type="text" name="chatInput" class="form-control">

<script type="text/javascript">
   $(function(){  //ws : websocket 프로토콜 서버에 접속
	   //클라이언트에서 소켓 생성 => 서버에 접속완료. 서버의 준비가 완료되어야 함
	   let ws = new WebSocket("ws://${server}:${port}${path}/chatting") //ws://172.16.2.12:8080/shop1/chatting
	   //서버와 접속 완료시 호출
	   ws.onopen = function() {
		  $("#chatStatus").text("info:connection opened") 
		  $("input[name=chatInput]").on("keydown",function(evt){  //키이벤트 등록
			  if(evt.keyCode == 13) { //enter 키가 눌려지면
				  let msg = $("input[name=chatInput]").val()
				  ws.send(msg)  //서버로 msg를 전송
				  $("input[name=chatInput]").val("")
			  }
		  })
	   }
	   //서버에서 메세지 수신한 경우
	   ws.onmessage = function(event) {
		   //event.data : 서버에서 수신된 메세지
		   $("textarea").eq(0).append(event.data+"\n")
	   }
	   //서버와 연결 종료된경우
	   ws.onclose = function(event){
		   $("#chatStatus").text("info:connection close")
	   }
   })
</script></body></html>