<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- /WEB-INF/view/board/write.jsp  --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 작성</title></head>
<body>
<form:form modelAttribute="boardDto" action="write" enctype="multipart/form-data" name="f">
<input type="hidden" name="boardid" value="${param.boardid}" >           
<table class="table">
  <tr><th>글쓴이</th><td><form:input path="writer" class="form-control"/>
                  <font color="red"><form:errors path="writer" /></font></td></tr>
  <tr><th>비밀번호</th><td><form:password path="pass" class="form-control"/>
                  <font color="red"><form:errors path="pass" cssStyle="color:red;"/></font></td></tr>
  <tr><th>제목</th><td><form:input path="title" class="form-control"/>
                  <font color="red"><form:errors path="title" /></font></td></tr>
  <tr><th>내용</th>
      <td><form:textarea path="content" rows="15" cols="80"  id="summernote"/>
           <font color="red"><form:errors path="content" /></font></td></tr>
  <tr><th>첨부파일</th><td><input type="file" name="file1"></td></tr>
  <tr><td colspan="2" class="text-center">
       <a href="javascript:document.f.submit()" class="btn btn-primary">게시글등록</a>
       <a href="list?boardid=${param.boardid}" class="btn btn-success">게시글목록</a></td></tr>                        
</table></form:form>
<script type="text/javascript">
$(function(){
  $("#summernote").summernote({
	  height:300,
	  /*callbacks : 이벤트 처리
	    onImageUpload : 이미지 업로드시 처리
	    onInit : 에디터 로드시. 초기화면 설정...
	  */
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