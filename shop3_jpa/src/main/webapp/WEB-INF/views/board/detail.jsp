<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- /WEB-INF/view/board/detail.jsp
  http://localhost:8080/board/detail?num=1 요청
    - Board BoardService.getBoard(num)
      Board BoardDao.selectOne(num) 
      num 파라미터에 해당 게시물을 정보를 db에서 읽어 Board 객체에 저장
    - 조회 수 증가
      BoardService.addReadcnt(num)
      BoardDao.addReadcnt(num)
      
    BoardController, BoardService, BoardDao, BoardMapper 구현하기   
    
   =============================================================
   2026-04-06 과제
   1. 댓글 등록,삭제시 조회수가 증가함. 목록에서 조회하는 경우만 조회수가 증가되도록 프로그램 수정
   2. 댓글 등록시 입력값검증에서 오류발생시 게시물내용이 보여지도록 수정  
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html><html><head><meta charset="UTF-8">
<title>게시물 상세보기</title>
<style type="text/css">
  .leftcol {	text-align: left;	vertical-align : top;  }
  .lefttoptable {  height : 250px;	    border-width: 0px;
	text-align: left;	vertical-align : top;	padding: 0px; }
</style></head><body>
<table class="table"><tr><td colspan="2" class="text-center h4">${board.boardName}</td></tr>
   <tr><td width="15%">글쓴이</td>
       <td width="85%" class="leftcol">${board.writer}</td></tr>
   <tr><td>제목</td><td class="leftcol">${board.title}</td></tr>
   <tr><td>내용</td><td class="leftcol">
     <table class="lefttoptable">
   <tr><td class="leftcol lefttoptable">${board.content}</td></tr></table></td></tr>
   <tr><td>첨부파일</td><td>&nbsp;
    <c:if test="${!empty board.fileurl}">
     <a href="file/${board.fileurl}">${board.fileurl}</a>
    </c:if></td></tr>
   <tr><td colspan="2" class="text-center">
     <a href="reply?num=${board.num}" class="btn btn-success">답변</a>
   <%-- 공지사항인 경우 관리자가 아니면 수정,삭제 버튼 없애기 --%>
   <c:if test="${board.boardid != 1 || loginUser.userid == 'admin' }">
     <a href="update?num=${board.num}&boardid=${board.boardid}" class="btn btn-primary">수정</a>
     <a href="delete?num=${board.num}&boardid=${board.boardid}" class="btn btn-danger">삭제</a>
   </c:if>  
     <a href="list?boardid=${board.boardid}" class="btn btn-primary">게시물목록</a>
   </td></tr></table>
   
   
  <%-- 댓글 등록,조회,삭제 --%> 
  <%--
    http://localhost:8080/shop1/board/detail?num=1#comment
    => detail.jsp 페이지에서 id=comment 인 영역을 보여줌.
    
    URL 명칭
    http : 프로토콜. 전문
    localhost : 호스트. ip 주소. 내컴퓨터
    8080 : 포트.  
    shop1/board/detail : Path. 웹어플리케이션이름 + 요청페이지
    ?num=1 : 쿼리. 파라미터값 표현
    
    #comment : 프래그먼트(Fragment). 서버에 전송되지 않는 영역. 브라우저내부에서 활용.
               id=comment인 영역으로 화면을 스크롤함
   --%>
  <span id="comment"></span> 
  <form:form modelAttribute="comment" action="comment"  method="post" name="commForm">
  <input type="hidden"  name="num"  value="${board.num}"/>
  <div class="row"> <%-- class="row" : bootstrap에서 한줄의미 table에서 tr에 해당하는 의미 --%>
    <div class="col text-center"> <%-- class="col" : 한칸의미. table에서는 td에 해당하는 의미 --%>
     <p><form:input path="writer" class="form-control"  placeholder="작성자"/>
       <form:errors path="writer" cssStyle="color:red;"/></p>
     </div>
    <div class="col text-center">
     <p><form:password path="pass" class="form-control"  placeholder="비밀번호"/>
       <form:errors path="pass"  cssStyle="color:red;" /></p>
     </div>
    <div class="col text-center">
     <p><form:input path="content" class="form-control" placeholder="댓글내용"/>
         <form:errors path="content" cssStyle="color:red;"/></p></div>
    <div class="col text-center">
    <p><button  class="btn btn-primary">댓글등록</button></p></div></div>
  </form:form>
  
  <%-- 등록된 댓글 목록 출력 --%>
  <div class="container"> <%-- bootstrap에서 사용되는 영역 지정. 자동으로 여백을 줌. 최대크기도 지정됨 --%>
  <%-- container-fluid : 여백없이 width:100%인 영역으로 지정됨 --%> 
  <table class="table table-bordered">
    <colgroup>
      <col style="width:5%">
      <col style="width:10%">
      <col style="width:40%">
      <col style="width:20%">
      <col style="width:20%">
    </colgroup>
    <c:forEach var="c" items="${commlist}" varStatus="stat">
    <tr><td>${c.seq}</td><td>${c.writer}</td><td>${c.content}</td>
        <td><fmt:formatDate value="${c.regdate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        <td class="text-right">
        <%-- name="commdel${stat.index}" : form의 이름을 유일하게 설정 --%>
    <form action="commdel" method="post" name="commdel${stat.index}" 
          class="d-flex justify-content-end align-items-center">
    <input type="hidden" name="num" value="${c.num}"> <%-- 게시물번호 --%>    
    <input type="hidden" name="seq" value="${c.seq}"> <%-- 게시물의 댓글 번호 --%>   
    <input type="password" name="pass" placeholder="비밀번호" class="form-control mr-1">
     <button class="btn btn-sm btn-outline-danger text-nowrap">삭제</button>
    </form></td>
    </tr></c:forEach></table></div>   
 </body></html>