<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    
<%-- /WEB-INF/view/user/password2.jsp --%>    
<!DOCTYPE html>
<html><head>
<meta charset="UTF-8">
<title>비밀번호 변경</title>
</head>
<body>
<h2>비밀번호 변경</h2>
<form:form modelAttribute="userPassword" method="post" action="password2" name="f">
<%-- global 오류  --%>
 <spring:hasBindErrors name="userPassword">
    <font color="red">
      <c:forEach items="${errors.globalErrors}" var="error">
        <spring:message code="${error.code}" /><br>
      </c:forEach>
    </font>
 </spring:hasBindErrors>

<table class="table"><tr><th>현재 비밀번호</th>
     <td><form:password path="password" class="form-control" />
         <font color="red"><form:errors path="password" /></font></td></tr>
  <tr><th>변경 비밀번호</th>
      <td><form:password path="chgpass" class="form-control" />
      <font color="red"><form:errors path="chgpass" /></font></td></tr>
  <tr><th>변경 비밀번호 재입력</th>
      <td><form:password path="chgpass2" class="form-control" />
      <font color="red"><form:errors path="chgpass2" /></font></td></tr>
  <tr><td colspan="2" class="text-center">
  	<button class="btn btn-primary">비밀번호변경</button></td></tr>
</table>
</form:form>

</body></html>