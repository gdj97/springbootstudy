<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- /WEB-INF/view/cart/checkout.jsp--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html><html><head><meta charset="UTF-8">
<title>주문 전 상품 목록 보기</title></head>
<body><h2>배송지 정보</h2>
<table class="table table-bordered">
  <tr><td>주문아이디</td><td>${loginUser.userid}</td></tr>
  <tr><td>이름</td><td>${loginUser.username}</td></tr>
  <tr><td>우편번호</td><td>${loginUser.postcode}</td></tr>
  <tr><td>주소</td><td>${loginUser.address}</td></tr>
  <tr><td>전화번호</td><td>${loginUser.phoneno}</td></tr>
</table>
<h2>구매 상품 </h2>
<fmt:setLocale value="ko_KR"/>
<table class="table table-bordered"><tr><th>상품명</th><th>가격</th><th>수량</th><th>합계</th></tr>
  <c:forEach items="${sessionScope.CART.itemSetList}" var="itemSet" varStatus="stat">
   <tr><td>${itemSet.item.name}</td>
   <td><fmt:formatNumber value="${itemSet.item.price}" type="currency" currencySymbol="￦"/></td>
   <td><fmt:formatNumber value="${itemSet.quantity}" pattern="###,###" /></td>
   <td><fmt:formatNumber value="${itemSet.item.price *itemSet.quantity}" type="currency" /></td></tr>
  </c:forEach>
  <tr><td colspan="4" align="right">총 구입 금액 : 
     <fmt:formatNumber value="${sessionScope.CART.total}" type="currency" /> 원</td></tr>
  <tr><td colspan="4" class="text-center">
     <a href="javascript:kakaopay()" class="btn btn-primary">확정하기</a>&nbsp;
     <a href="../item/list" class="btn btn-success">상품 목록</a>&nbsp;
  </td></tr></table>
<%-- 결제하기 모듈 : 개발자 문서
   https://developers.portone.io/docs/ko/ready/readme
   https://admin.portone.io/auth/signin
 --%>
<%--  
<script type="text/javascript" src="https://cdn.iamport.kr/js/iamport.payment-1.1.8.js"></script>
 --%>
<script src="https://cdn.iamport.kr/v1/iamport.js"></script>
<script type="text/javascript">
//   let IMP = window.IMP
   IMP.init("imp24373841") //가맹점 식별코드
   
   function kakaopay() {
	   $.ajax("kakao",{
		   success : function(json) {
			   iamPay(json)
		   }
	   })
   }
   function iamPay(json) {
	   IMP.request_pay({
		   channelKey : "channel-key-5b247c32-21a5-4a1c-bb55-bc70b389cc9a",
		   pg : "payco",     //상점구분. 카카오페이
		   pay_method : "card", //카드 결제
		   merchant_uid : json.merchant_uid,  //주문번호:주문별로 유일한값이 필요. userid-session id 값
		   name : json.name,    //주문상품명. 사과외3건     
		   amount : json.amount,//전체주문금액
		   buyer_email : "myungshink@naver.com",   //주문자이메일. 테스트. 
		   buyer_name : json.buyer_name,    //주문자성명
		   buyer_tel : json.buyer_tel,      //주문자전화번호
		   buyer_addr : json.buyer_addr,    //주문자 주소
		   buyer_postcode : json.buyer_postcode  //주문자 우편번호
	   },function(rsp){
		   if (rsp.success) {
			   let msg = "결제가 완료 되었습니다."
			   msg += "\n:고유ID : " + rsp.imp_uid
			   msg += "\n:상점ID : " + rsp.merchant_uid
			   msg += "\n:결제금액 : " + rsp.paid_amount
			   alert(msg)
			   location.href="end"
		   } else {
			   alert("결제에 실패 했습니다.:" + rsp.error_msg)
		   }
	   })
   }
</script>  
  </body></html>