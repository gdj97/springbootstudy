<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%-- /WEB-INF/view/board/delete.jsp --%>
<!DOCTYPE html><html><head><meta charset="UTF-8">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<title>게시판 삭제 화면</title></head>
<body>
<form action="delete"  method="post"  name="f">
<spring:hasBindErrors name="board">
	<font color="red"><c:forEach items="${errors.globalErrors}"
	var="error"><spring:message code="${error.code }" /></c:forEach>
	</font></spring:hasBindErrors>
<input type="hidden" name="num" value="${board.num}">
<input type="hidden" name="boardid" value="${board.boardid}">
<input type="hidden" name="title" value="${board.title}">
<h4>${board.boardName}글 삭제 화면</h4>
<table class="table">
    <tr><td>제목</td><td>${board.title}</td></tr>
	<tr><td>게시글비밀번호</td>
		<td><input type="password" name="pass" class="form-control" /></td></tr>
	<tr><td colspan="2" class="text-right">
<button class="btn btn-danger">게시글삭제</button></td></tr>
</table></form></body></html>