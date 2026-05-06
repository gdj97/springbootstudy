<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%>
<%-- /WEB-INF/view/board/reply.jsp : 답변등록 --%>
<!DOCTYPE html><html><head><meta charset="UTF-8">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<title>게시판 답글 쓰기</title></head><body>
<form:form modelAttribute="board" action="reply"   method="post" name="f">
  <form:hidden  path="num" /> <%-- 원글에 해당하는 정보 --%>
  <form:hidden  path="boardid" /> <%-- 원글에 해당하는 정보 --%>
  <form:hidden  path="grp" />  <%-- 원글에 해당하는 정보 --%>
  <form:hidden  path="grplevel" /> <%-- 원글에 해당하는 정보 --%>
  <form:hidden  path="grpstep" /> <%-- 원글에 해당하는 정보 --%>
  <h2>${board.boardName} 답글 등록</h2>
  <table class="table">
  <tr><td>글쓴이</td><td><input type="text" name="writer" class="form-control">
    <form:errors path="writer" cssStyle="color:red"/></td></tr>
  <tr><td>비밀번호</td><td><form:password path="pass" cssClass="form-control"/>
    <form:errors path="pass"  cssStyle="color:red"/></td></tr>
  <tr><td>제목</td><td><form:input path="title" value="RE:${board.title}" cssClass="form-control"/> 
  <form:errors path="title"  cssStyle="color:red"/></td></tr>
  <tr><td>내용</td><td><textarea name="content" rows="15" cols="80" id="summernote" class="form-control"></textarea>
   <form:errors path="content"  cssStyle="color:red"/></td></tr>
  <tr><td colspan="2" class="text-center"><button class="btn btn-primary">답변글등록</button></td></tr>    
  </table></form:form>
  <script type="text/javascript">
  $(function(){
  	  $("#summernote").summernote({
  		  height:300,
  		  callbacks : {
  			  onImageUpload : function(images) {
  				  for(let i=0;i < images.length;i++) {
  					  sendFile(images[i])
  				  }
  			  }
  		  }
  	  })
  })
  </script>
  </body></html>