<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   
<%-- /webapp/WEB-INF/view/item/list.jsp --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품 목록</title>
</head>
<body>
<a href="create">상품등록</a>
<a href="../cart/cartView" style="float:right">장바구니</a>
<table class="table">
	<tr><th>상품ID</th><th>상품명</th><th>가격</th><th>수정</th><th>삭제</th></tr>
	<c:forEach items="${itemList}" var="item">
	<tr><td>${item.id}</td>
	<td><a href="detail?id=${item.id}">${item.name}</a></td>
	<td>${item.price}</td>
	<td><a href="update?id=${item.id}">수정</a></td>
	<td><a href="delete?id=${item.id}">삭제</a></td></tr>
	</c:forEach>
</table>
</body>
</html>