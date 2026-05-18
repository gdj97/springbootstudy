package kr.gdu.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kr.gdu.dto.MemberDto;
import kr.gdu.entity.MemberEntity;
import kr.gdu.service.MemberService;

@RestController
@RequestMapping("/member/")
@CrossOrigin(origins="http://localhost:5173", allowCredentials="true")
public class MemberController {
	@Autowired
	MemberService service;
	
	@Value("${member.upload.dir}")
	private String UPLOAD_PATH;
	
	
	@PostMapping("joinPro")
	public void joinPro(MemberDto dto) {
		service.memberInsert(new MemberEntity(dto));
	}	
	@PostMapping("picture")
	public Map<String, Object> picture(@RequestParam("picture") MultipartFile picture) {
		String path = UPLOAD_PATH+"/member/picture/";
		File dir = new File(path);
		if(!dir.exists()) dir.mkdirs();
		String filename="";
		if (picture!=null && !picture.isEmpty()) {
			File file = new File(path, picture.getOriginalFilename());
			filename=picture.getOriginalFilename();
			try {
				picture.transferTo(file);
			} catch (Exception e) {
				e.printStackTrace();
		    }
		}
		return	Map.of("picture",filename,"code",0);
	}	
	@PostMapping("loginPro")
	public ResponseEntity loginPro(String id, String pass, String key, HttpServletResponse response,
	    @CookieValue(value = "id", required = false) Cookie cookie) throws Exception {
		if (cookie==null) {
			cookie = new Cookie("id", "");
			response.addCookie(cookie);
		}
		Map<String, String> responseBody = new HashMap<>();
		MemberEntity mem = service.getMember(id); //회원 정보
		if (mem != null) {  //회원이 존재
			if (mem.getPass().equals(pass)) { //비밀번호 검증
				cookie.setValue(id); //아이디값
				cookie.setDomain("localhost");  //쿠키에서 유효한 도메일 설정
				cookie.setPath("/");   //전체 경로에서 쿠키 접근 가능
				cookie.setMaxAge(30); // 쿠키 유효시간 30초. 정하지 않으면 브라우저가 종료시 삭제
				//보안쿠키. : 암호화하여 저장. 
				cookie.setSecure(true); // SSL 서버연결 시에만 쿠키를 전송하도록 설정. 단 localhost는 상관없음
				response.addCookie(cookie);	//응답 객체에 저장			
				responseBody.put("message", "login_success");
				responseBody.put("token", "1");
				//HttpStatus.OK : http의 코드 200. => 정상처리
				return new ResponseEntity<Map<String, String>>(responseBody, HttpStatus.OK);
			} else {
				responseBody.put("message", "비밀번호가 틀립니다");
				responseBody.put("token", "0");
				return new ResponseEntity<Map<String, String>>(responseBody, HttpStatus.UNAUTHORIZED);
			}
		} else {  // 회원이 없는 경우
			responseBody.put("message", "id 가 없습니다");
			responseBody.put("token", "0");
			//HttpStatus.UNAUTHORIZED : 401번 http 오류 코드
			return new ResponseEntity<Map<String, String>>
			(responseBody, HttpStatus.UNAUTHORIZED);
		}
	}	
}
