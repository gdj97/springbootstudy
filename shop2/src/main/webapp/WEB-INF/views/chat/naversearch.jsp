<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- /WEB-INF/view/chat/naversearch.jsp --%>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>네이버 검색</title>
</head>
<body>
<select id="type" class="form-control">
  <option value="blog">블로그</option><option value="news">뉴스</option>
  <option value="book">책</option><option value="encyc">백과사전</option>
  <option value="cafearticle">카페글</option>
  <option value="kin">지식인</option><option value="local">지역</option>
  <option value="webkr">웹문서</option><option value="image">이미지</option>
  <option value="shop">쇼핑</option><option value="doc">전문자료</option>
</select>
<div class="row"><div class="col-4">
페이지 별 검색 갯수 :
  <select id="display"  class="form-control"><option>10</option><option>20</option><option>50</option></select>
</div>
<div class="col-8">검색어 :<input type="text" id="data"  placeholder="제시어"  class="form-control"></div>
</div>
<div class="text-center"><button class="btn btn-primary" onclick="naversearch(1)">검색</button></div>
<div id="result"></div>
<script type="text/javascript">
   function naversearch(start) {
	 $.ajax({
		 type:"POST",
		 url:"naversearch",
		 data: {"data":$("#data").val(),  //검색어  
			    "display":$("#display").val(), //한페이지에 출력할 글의 갯수
			    "start":start,     //시작페이지 
			    "type":$("#type").val()  //검색종류
	     },
		 success : function(json) {
			 let total = json.total;  //전체 검색 건수
			 let html = "";
			 let num = (start-1) * $("#display").val() + 1;  //검색 순서
			 let maxpage = Math.ceil(total / parseInt($("#display").val())) //최대 페이지
			 let startpage = (Math.ceil(start/10) -1 ) * 10 + 1; //시작 페이지 번호
			 let endpage = startpage + 9;
			 if(endpage > maxpage) endpage = maxpage;
			 html += "<table class='table table-bordered'><tr><td colspan='4' align='center'>"
			      + " 전체 조회 건수:" + total+",  현재페이지:"+start +"/"+maxpage +"</td></tr>";
	         $.each(json.items, function(i,item){
				 html+="<tr><td>"+num+"</td><td>"+item.title 
				 + "</td><td>"
				 if($("#type").val() == 'image') {
					html += "<a href='"+item.link + "'><img src='"+item.thumbnail +  "'></a><td>"
				 }	     
				 else {	 
					html += "<a href='"+item.link + "'>"+item.link
					     + "</a></td><td>"+ item.description
				 }	
			     html += "</td></tr>";
			     num++;
	         })
			 html +="<tr><td colspan='4' align='center'>";
			 if(start > 1) {
				html += "<button class='btn btn-success' onclick='naversearch("+(start-1)+")'>이전</button>"
			 }    
			 for(let i = startpage ; i<= endpage ;i++) {
			    if(i == start) {
			      html += "<button class='btn  btn-primary' onclick='naversearch("+i+")'>" + i + "</button>";
			    } else {
			      html += "<button class='btn  btn-secondary' onclick='naversearch("+i+")'>" + i + "</button>";
			    }
			 }
			 if(maxpage > start) {
			    html += "<button class='btn btn-success' onclick='naversearch("+(start+1)+")'>다음</button>"
			 }
			 html +="</td></tr></table>";
			 $("#result").html(html);
		 }
	 })	   
  }
</script></body></html>