<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%-- /WEB-INF/views/board/update.jsp --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html><html><head>
<meta charset="UTF-8">
<title>게시글 수정</title>
</head><body>
<form:form modelAttribute="board" action="update"  enctype="multipart/form-data" name="f">
   <form:hidden path="num" />
   <form:hidden path="boardid" />
   <form:hidden path="fileurl"/> <%-- 기존에 등록된 업로드파일 이름 --%>
   <h4 class="text-center">${board.boardName}수정</h4>
   <table class="table">
   <tr><td>글쓴이</td><td><form:input path="writer" cssClass="form-control"/>
   <form:errors path="writer" cssStyle="color:red;"/></td></tr>
   <tr><td>비밀번호</td><td><form:password path="pass"  cssClass="form-control"/>
   <font color="red"><form:errors path="pass" /></font></td></tr>
   <tr><td>제목</td><td><form:input path="title"  cssClass="form-control"/>
   <font color="red"><form:errors path="title" /></font></td></tr>
   <tr><td>내용</td>
   <td><form:textarea path="content" rows="15" cols="80" id="summernote" cssClass="form-control"/>
   <font color="red"><form:errors path="content" /></font></td></tr>
   <tr><td>첨부파일</td>
       <td><c:if test="${!empty board.fileurl}">
     <div id="file_desc">
       <a href="file/${board.fileurl}">${board.fileurl}</a>
       <a href="javascript:file_delete()">[첨부파일삭제]</a>
     </div></c:if>
   <input type="file" name="file1"></td></tr>
   <tr><td colspan="2" class="text-center">
       <a href="javascript:document.f.submit()" class="btn btn-primary">게시글수정</a>
       <a href="list" class="btn btn-success">게시글목록</a></td></tr></table></form:form>
<script type="text/javascript">
    function file_delete() {
    	document.f.fileurl.value = ""
	    document.getElementById("file_desc").style.display = "none";
    }
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
    
</script></body></html>