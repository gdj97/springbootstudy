<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- /WEB-INF/view/user/mypage.jsp --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>mypage</title>
<style type="text/css">
  .select {  
      padding:3px; 
      background-color: #0000ff;  
  }
  .select>a { 
       color : #ffffff;   
       text-decoration: none; 
       font-weight: bold;
  }
  .title { text-decoration: none; }
</style>
</head>
<body>
<table class="table">
 <tr><td id="tab1" class="tab text-center">
 <a href="javascript:disp_div('minfo','tab1')" class="title">회원정보</a></td>
   <c:if test="${param.userid != 'admin'}">
     <td id="tab2" class="tab text-center">
 <a href="javascript:disp_div('oinfo','tab2')"  class="title">주문정보</a></td>
   </c:if></tr></table>

<div id="oinfo" class="info" style="display:none;">
<table class="table">
<tr><th>주문번호</th><th>주문일자</th><th>주문금액</th></tr>
<c:forEach items="${salelist}" var="sale" varStatus="stat">
<tr><td align="center">
<a href="javascript:list_disp('saleLine${stat.index}')">${sale.saleid}</a></td>
<td align="center"><fmt:formatDate value="${sale.saledate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
<td align="right">
<fmt:formatNumber value="${sale.total}" pattern="###,###" />원</td></tr>
<tr id="saleLine${stat.index}" class="saleLine">
 <td colspan="3" align="center">
 <table><tr><th>상품명</th><th>상품가격</th><th>주문수량</th><th>상품총액</th></tr>
   <c:forEach items="${sale.itemList }" var="saleItem">
   <%-- ${saleItem.item.name} : saleItem.getItem().getName() => 상품명 
        ${saleItem.item.price}: saleItem.getItem().getPrice() => 상품가격
   --%>
   <tr><td class="title">${saleItem.item.name}</td>
       <td>
  <fmt:formatNumber value="${saleItem.item.price}" pattern="###,###"/>원</td>
       <td>${saleItem.quantity}</td>
       <td><fmt:formatNumber value="${saleItem.item.price * saleItem.quantity}" pattern="###,###"/></td></tr>
   </c:forEach></table>
 </td></tr></c:forEach></table></div>

 <div id="minfo" class="info">
 <table class="table">
   <tr><td>아이디</td><td>${user.userid}</td></tr>
   <tr><td>이름</td><td>${user.username}</td></tr>   
   <tr><td>우편번호</td><td>${user.postcode}</td></tr>   
   <tr><td>전화번호</td><td>${user.phoneno}</td></tr>   
   <tr><td>이메일</td><td>${user.email}</td></tr>   
   <tr><td>생년월일</td>
   <td><fmt:formatDate value="${user.birthday}" pattern="yyyy-MM-dd"/></td></tr>   
 </table><br>
 <a href="logout" class="btn btn-secondary">로그아웃</a>&nbsp;
 <a href="update?userid=${user.userid}" class="btn btn-primary">회원정보수정</a>&nbsp;
 <a href="password" class="btn btn-primary">비밀번호수정</a>&nbsp;
 <a href="password2" class="btn btn-primary">비밀번호수정2</a>&nbsp;
 <c:if test="${loginUser.userid != 'admin'}">
 <a href="delete?userid=${user.userid}" class="btn btn-danger">회원탈퇴</a>&nbsp;
 </c:if>
 <c:if test="${loginUser.userid == 'admin'}">
 <a href="../admin/list" class="btn btn-success">회원목록</a>&nbsp;
 </c:if> 
 </div>
 <script type="text/javascript">
   $(function(){ //준비되면
	   $("#minfo").show()  //회원정보 보여줌
	   $("#oinfo").hide()  //주문정보 숨김
	   $(".saleLine").each(function(){   //주문상품 숨김  
		   $(this).hide()
	   })
	   $("#tab1").addClass("select") // 회원정보 제목을 select 속성 주기. 파랑바탕에 흰글씨 
   })
   function disp_div(id,tab) { //tab 선택시 호출되는 함수
	   //id : oinfo, tab:tab2
	   $(".info").each(function() {  //회원정보,주문정보 데이터 숨김
		   $(this).hide()
	   })
	   $(".tab").each(function() {    //회원정보,주문정보 제목 select 속성 제거
		   $(this).removeClass("select")
	   })
	   $("#"+id).show() //선택한 정보를 보여주기
	   $("#"+tab).addClass("select") //tab에 class 속성을 select 값 주기
   }
   function list_disp(id) { //주문상품을 볼수 있도록 선택. 현재 안보이는 경우 보이도록, 현재 보이는 경우는 안보이도록 
	   $("#"+id).toggle()
   }
</script>
 </body></html>