<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>   
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%-- /WEB-INF/view/admin/mail.jsp --%>
<!DOCTYPE html><html><head><meta charset="UTF-8">
<title>메일 보내기</title>
</head>
<body><h2>메일보내기</h2>
<form:form modelAttribute="mail" name="mailform"  action="mail" enctype="multipart/form-data" >
   본인구글ID : <form:input  path="googleid" class="form-control" />
   <form:errors path="googleid" class="text-danger"/><br>   
   본인구글비밀번호 : <form:password path="googlepw" class="form-control" />
   <form:errors path="googlepw" class="text-danger"/>
<table class="table">
 <tr><td>보내는사람</td><td>${loginUser.email}</td></tr>
 <tr><td>받는사람</td>
 <td><form:input path="recipient" class="form-control" /></td></tr>
 <tr><td>제목</td><td><form:input path="title"  class="form-control" />
 <form:errors path="title" class="text-danger"/></td></tr>
 <tr><td>메시지형식</td><td><select name="mtype" class="form-control">
  <option value="text/html; charset=utf-8">HTML</option>
  <option value="text/plain; charset=utf-8">TEXT</option></select></td></tr>
<tr><td>첨부파일1</td><td><input type="file" name="file1"></td></tr>
<tr><td>첨부파일2</td><td><input type="file" name="file1"></td></tr>
<tr><td colspan="2"><form:textarea path="contents" cols="120" rows="10" class="form-control" id="summernote"/>
<form:errors path="contents" class="text-danger"/>
 </td></tr>
<tr><td colspan="2" class="text-center"><button class="btn btn-primary">메일보내기</button></td></tr>
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