<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- /WEB-INF/view/user/login.jsp --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html><html><head><meta charset="UTF-8">
<title>로그인화면</title>
</head><body>
<h2>사용자 로그인</h2>
<form:form modelAttribute="userDto" method="post" action="login" name="loginform">
  <spring:hasBindErrors name="userDto">
    <font color="red"><c:forEach items="${errors.globalErrors}" var="error">
         <spring:message code="${error.code}" />
      </c:forEach></font></spring:hasBindErrors>
      
<table class="table">
<tr><td>아이디</td><td><form:input path="userid" class="form-control"/>
     <font color="red"><form:errors path="userid" /></font></td></tr>
<tr><td>비밀번호</td><td><form:password path="password" class="form-control"/>
     <font color="red"><form:errors path="password" /></font></td></tr>
<tr><td colspan="2" align="center">
<button class="btn btn-success">로그인</button>
<button type="button" onclick="location.href='join'" class="btn btn-primary">회원가입</button>
<button type="button" onclick="win_open('idsearch')" class="btn btn-secondary">아이디찾기</button>
<button type="button" onclick="win_open('pwsearch')" class="btn btn-secondary">비밀번호찾기</button>
</td></tr></table>
<p>
<a href="${apiURL}"><img height="30" src="http://static.nid.naver.com/oauth/small_g_in.PNG"></a>
</form:form>
<script type="text/javascript">
function win_open(page) {
	   var op = "width=500, height=350, left=50,top=150";
	   open(page ,"",op);
}
</script>
</body></html>