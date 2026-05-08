/*
 * 1. summernote를 이용하여 이미지를 게시물에 등록하기
   2. Ajax을 이용하여 파일업로드하기
 */
function sendFile(file) {
	let data = new FormData(); //파일업로드를 위한 컨테이너 객체 생성
	data.append("image",file); //file : 업로드되는 데이터 한개
	$.ajax({
		  url : "/ajax/uploadImage",
		  type : "post",    //post 방식의 요청
		  data : data,      //FormData 객체를 서버로 전달
		  processData : false,  //문자열 전송아님. 파일업로드시 사용함
		  contentType : false,  //컨텐트타입 설정안함. 파일업로드시 사용함
		  success : function(src) {
			  console.log(src);  //업로드된 파일의 이름
			  $("#summernote").summernote("insertImage",src);
		  },
		  error : function(e) { 
			  alert("이미지 업로드 실패:" + e.status)
		  }
	})	
}

let randomColorFactor = function(){  //0 ~ 255 사이의 임의의 수 리턴
 return Math.round(Math.random()*255)
}
let randomColor = function(opacity) {  //rgba(red,green,blue,투명도)
 return "rgba("+ randomColorFactor() + ","
		 + randomColorFactor() + ","
		 + randomColorFactor() + ","
		 +(opacity || '.3') +")"
}
