package kr.gdu.shop3.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import kr.gdu.shop3.dto.SaleDto;
import kr.gdu.shop3.dto.UserDto;
import kr.gdu.shop3.dto.UserPasswordDto;
import kr.gdu.shop3.exception.ShopException;
import kr.gdu.shop3.service.ItemService;
import kr.gdu.shop3.service.UserService;
import kr.gdu.shop3.util.ShopUtil;


@Controller
@RequestMapping("user")
public class UserController {
	@Autowired
	private UserService service;
	@Autowired
	private ItemService itemService;
	
	//http://localhost:8080/shop1/user/join => /WEB-INF/view/user/join.jsp 
	@GetMapping("*") //Get 방식의 모든 요청
	public ModelAndView form() {
		ModelAndView mav = new ModelAndView();
		mav.addObject(new UserDto());
		return mav;//view: null => url과 같은 위치의 jsp 페이지 요청
	}
	/*
	 * OAuth2 이용 
	 *  1. 로그인 요청  
	 *  2. 인증코드발급 : redirectURL을 통해서
	 *  3. 로그인 재요청 : 토큰 발급
	 */
	@GetMapping("login") 
	public ModelAndView loginForm(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		String clientId="rGh0ITfXNHwLoTbWBKSN";
		String redirectURL = null;
		try {
			//네이버에 Callback URL 전달
			redirectURL = URLEncoder.encode("http://localhost:8080/shop1/user/naverlogin","UTF-8");
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		SecureRandom random = new SecureRandom(); //안전한 코드 생성
		//큰 랜덤숫자를 생성 : 요청시 전달하고, 네이버응답시 state 값을 비교
		String state = new BigInteger(130,random).toString();
		//네이버에 로그인 요청 url : 네이버 결정
		String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
		apiURL += "&client_id="+clientId;  //사용자정보
		apiURL += "&redirect_uri="+redirectURL; //네이버가 다시 요청할 url
		apiURL += "&state="+state;         //임의의 상태 코드
		mav.addObject("apiURL",apiURL);//네이버 로그인 접근
		mav.addObject(new UserDto());     //입력값 검증
		session.setAttribute("state", state); //세션에 임의의 상태 코드 등록
		System.out.println("1.session.id="+session.getId());
		return mav;
	}
	// 네이버에서 Callback url로 호출됨
	/*
	 * code : 네이버에서 전달받은 인증 코드(접근 가능 토큰발급용)
	 * state : callback url 요청시 전달한 상태 코드
	 */
	@RequestMapping("naverlogin")
	public String naverlogin(String code, String state, HttpSession session) {
		System.out.println("2.session.id="+session.getId());
		String clientId = ""; //네이버 개발자센터의 Client ID값
		String clientSecret = "";       //네이버 개발자센터의 Client Secret값
		String redirectURI=null;
		try {
			redirectURI = URLEncoder.encode("YOUR_CALLBACK_URL", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//로그인 접근 토큰 요청을 위한  url 생성
		//Access Token 발급을 위한 URL
		String apiURL;
		apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code";
		apiURL += "&client_id=" + clientId;
		apiURL += "&client_secret=" + clientSecret;
		apiURL += "&redirect_uri=" + redirectURI;
		apiURL += "&code=" + code;  //네이버 생성 코드값
		apiURL += "&state=" + state; //요청시 생성한 상태코드값
		System.out.println("code="+code+",state="+state);
		StringBuffer res = new StringBuffer();
		System.out.println("apiURL="+apiURL);
		try {
			/*
			 * URL : apiURL이 정상인지 판단가능
			 */
		      URL url = new URL(apiURL); 
		      HttpURLConnection con = (HttpURLConnection)url.openConnection(); //url 연결. 네이버에 접속
		      con.setRequestMethod("GET");
		      int responseCode = con.getResponseCode(); //응답 코드 제공
		      BufferedReader br;
		      System.out.print("responseCode="+responseCode);
		      if(responseCode==200) { // 정상처리 완료 : 토큰 생성
		    	  //con.getInputStream() : 네이버에서 전달한 내용 읽기 위한 입력 스트림
		        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		      } else {    //토큰 생성 실패
		    	  //con.getErrorStream() : 네이버에서 전달한 오류 내용을 읽기 위한 입력 스트림
		        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		      }
		      String inputLine;
		      while ((inputLine = br.readLine()) != null) {
		        res.append(inputLine);
		      }
		      //res : 네이버에서 제공한 정상메세지 또는 오류 메세지를 저장 StringBuffer 객체
		      br.close();
		      if(responseCode==200) {
		        System.out.println("res:" + res.toString());
		      }
		 } catch (Exception e) {
		      e.printStackTrace();
		 }
		//JSON 형식의 응답데이터에서 access token 추출하기 
		 JSONParser parser = new JSONParser();  //json-simple-1.1.1.jar 파일 설정 
		 JSONObject json=null;  //자바에서 사용할 수 있는 JSON 객체
		 try {
			json = (JSONObject)parser.parse(res.toString());
		 } catch (ParseException e) {
			e.printStackTrace();
		 }
		 //access-token을 이용하여 네이버에 로그인 하기
		 String token = (String)json.get("access_token");
		 String header = "Bearer " + token;  //로그인 권한 설정
		 try {
		    apiURL = "https://openapi.naver.com/v1/nid/me";  //로그인 url 
		    URL url = new URL(apiURL);
		    HttpURLConnection con = (HttpURLConnection)url.openConnection();
		    con.setRequestMethod("GET");
		    con.setRequestProperty("Authorization", header);
		    int responseCode = con.getResponseCode();
		    BufferedReader br;
		    res = new StringBuffer();  //정상 :로그인 사용자의 정보 저장, 오류발생:오류메세지
		    if(responseCode==200) {
		        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		    } else {
		        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		    }
		    String inputLine;
		    while ((inputLine = br.readLine()) != null) {
		        res.append(inputLine);
		    }
		    br.close();
		    System.out.println(res.toString());
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 try {
			json = (JSONObject)parser.parse(res.toString());
		 } catch (ParseException e) {
			e.printStackTrace();
		 }
		 JSONObject jsondetail = (JSONObject)json.get("response");
		 System.out.println(jsondetail);
		 System.out.println(jsondetail.get("id"));
		 System.out.println(jsondetail.get("name"));
		 System.out.println(jsondetail.get("email"));
		 String userid = jsondetail.get("id").toString();
		 UserDto user = service.getUser(userid);
		 if (user == null) { //첫번째 로그인 한경우 => useraccount 테이블에 저장. 회원가입
			 user = new UserDto();
			 user.setUserid(userid);
			 user.setUsername(jsondetail.get("name").toString());
			 String email = jsondetail.get("email").toString();
			 user.setEmail(email);
			 user.setChannel("naver");
			 service.userInsert(user);
		 }
		 session.setAttribute("loginUser", user);
		 return "redirect:mypage?userid="+user.getUserid();
	}
	
	@PostMapping("join")
	public ModelAndView join(@Valid UserDto user,BindingResult bresult) {
		ModelAndView mav = new ModelAndView();
		if(bresult.hasErrors()) { //@Valid에서 검증한 오류 존재?
			//messages.properties 파일에서 코드를 찾아서 메세지를 출력
			bresult.reject("error.input.user"); //글로벌 오류로 등록
			bresult.reject("error.input.check");
			return mav;
		}
		//db에 등록
		try {
		    service.userInsert(user);
		} catch (DataIntegrityViolationException e) {//키값 중복된 경우. 
			e.printStackTrace();
			bresult.reject("error.duplicate.user");
			return mav;
		} catch (Exception e) { //중복 예외의 예외 
			e.printStackTrace();
			return mav;
		}
		mav.setViewName("redirect:login"); //http://localhost:8080/shop1/user/login 페이지를 재요청(redirect)
		return mav;		
	}
	/*
	 * @Valid 어노테이션으로 유효성검증시 User 클래스의 userid,password 외에 name, email,birthday 등의 입력되어야 함
	 * 직접 @Valid 역할 구현해야 함
	 */
	@PostMapping("login")
	public ModelAndView login(UserDto user,BindingResult bresult,HttpSession session) {
		ModelAndView mav = new ModelAndView();
		// 직접 입력값 검증구현하기
		if(user.getUserid()==null || 
			(user.getUserid().trim().length() < 3 || user.getUserid().trim().length() > 10)) {
			bresult.rejectValue("userid", "error.required"); //(프로퍼티,오류코드)
		}
		if(user.getPassword()==null ||
			(user.getPassword().trim().length() < 3 || user.getPassword().trim().length() > 10)) {
			bresult.rejectValue("password", "error.required");
		}		
		if(bresult.hasErrors()) {
			bresult.reject("error.login.check");
			return mav;
		}
		/*
		 * 아이디와 비밀번호가 정상적으로 입력된 경우
		 * 1. userid에 맞는 정보를 db에서 조회하기
		 *    userid가 없으면 아이디가 없습니다. (error.login.userid)
		 * 2. db에 등록된 비밀번호와, 입력된 비밀번호를 비교.
		 *    일치 : session 객체에 loginUser이름으로 User 객체를 속성으로 등록
		 *          페이지를 mypage로 페이지 이동
		 *    불일치 : 비밀번호를 확인하세요. (error.login.password)      
		 */
		UserDto dbUser = service.getUser(user.getUserid());
		if(dbUser == null) {
			bresult.reject("error.login.userid");
			return mav;
		}
		if(dbUser.getPassword().equals(user.getPassword())) {
			session.setAttribute("loginUser", dbUser);
			mav.setViewName("redirect:mypage?userid="+user.getUserid());
		} else {
			bresult.reject("error.login.password");
			return mav;
		}
		return mav;
	}
	/* AOP 클래스로 설정 
	 * 1. 로그인 상태
	 * 2. 관리자만 제외하고 본인 정보만 조회가능
	 */
	@RequestMapping("mypage")
	public ModelAndView idCheckMypage(String userid,HttpSession session) {
		ModelAndView mav = new ModelAndView();
		UserDto user = service.getUser(userid);
		//salelist : 사용자가 주문한 주문데이터 정보 목록. //sale 테이블의 정보 + saleItem 테이블 정보 + item 테이블의 정보
		List<SaleDto> salelist = itemService.saleList(userid);
		mav.addObject("user", user);
		mav.addObject("salelist",salelist);
		return mav;
	}	
	//AOP 설정되도록 메서드의 선언부 구현 필요
	@GetMapping({"update","delete"})
	public ModelAndView idCheckUser(String userid,HttpSession session) {
		ModelAndView mav = new ModelAndView();
		UserDto user = service.getUser(userid);
		mav.addObject("user",user);
		return mav;
	}
	@RequestMapping("logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:login";
	}
	@PostMapping("update")
	public String update(@Valid UserDto user,BindingResult bresult,HttpSession session ) {
		//입력값 검증
		if(bresult.hasErrors()) {
			bresult.reject("error.update.user");
			return null;
		}
		//비밀번호 검증
		UserDto loginUser = (UserDto)session.getAttribute("loginUser");
		if(!loginUser.getPassword().equals(user.getPassword())) {
			bresult.reject("error.update.password");
			return null;
		}
		//비밀번호 일치
		try {
			service.userUpdate(user);
			if(loginUser.getUserid().equals(user.getUserid())) { //본인 정보 수정하는 경우
			   session.setAttribute("loginUser", user);          //로그인 정보 변경
			}
			return "redirect:mypage?userid="+user.getUserid();
		} catch(Exception e) {
			e.printStackTrace();
			throw new ShopException("고객 수정시 오류 발생","update?userid=" + user.getUserid());
		}
	}	
	/*
	 * LoginAspect.userIdCheck() 메서드 실행 설정
	 * 탈퇴 검증
	 * 1. 관리자인 경우 탈퇴 불가
	 * 2. 비밀번호 검증 => 로그인된 비밀번호와 비교
	 *     본인탈퇴시 : 본인 비밀번호로 검증
	 *     관리자타인탈퇴 : 관리자 비밀번호로 검증
	 * 3. 비밀번호 불일치
	 *    메세지 출력 후 delete 페이지로 이동
	 * 4. 비밀번호 일치
	 *    db에서 사용자정보 삭제
	 *    본인탈퇴 : 로그아웃. login페이지로 이동
	 *    관리자 타인 탈퇴 : admin/list 페이지 이동    
	 */
	@PostMapping("delete")
	public String idCheckDelete(String password,String userid,HttpSession session) {
		if(userid.equals("admin")) {
			throw new ShopException("관리자는 탈퇴 불가입니다","mypage?userid=admin");
		}
		//비밀번호 검증. => 로그인 정보로 비교
		UserDto loginUser = (UserDto)session.getAttribute("loginUser");
		//password : 입력된 비밀번호
		//loginUser.getPassword() : db에 등록된 비밀번호
		if(!password.equals(loginUser.getPassword())) {
			throw new ShopException("비밀번호를 확인하세요","delete?userid="+userid);
		}
		//비밀번호가 일치 : db에서 userid에 해당하는 정보 삭제
		try {
			service.userDelete(userid);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ShopException("탈퇴 거래시 오류가 발생했습니다.","delete?userid="+userid);
		}
		//회원 정보 삭제 성공
		if(loginUser.getUserid().equals("admin")) {
			return "redirect:../admin/list";
		} else {
			session.invalidate();
			return "redirect:login";
		}
	}
	/*
	 * 비밀번호 변경 화면 출력
	 * 1. login 검증 => AOP 클래스
	 *    LoginAspect.loginCheck()
	 *     => pointcut : UserController 클래스에서 메서드이름이 loginCheck로 시작하고, 매개변수의 마지막이 HttpSession인
	 *                   메서드로 설정
	 *        advice : Around           
	 */
	@GetMapping({"password","password2"})
	public String loginCheckForm(HttpSession session,Model model) {
		model.addAttribute(new UserPasswordDto());
		return null;
	}
	/*
	 * 1. login 검증 => AOP 클래스
	 * 2. 현재비밀번호와 입력비밀번호 비교
	 *    일치 : db 수정. 로그인정보 수정. mypage로 페이지 이동
	 *    불일치 : 오류메세지 출력. password 페이지 이동
	 */
	@PostMapping("password")
	public String loginCheckPassword(String password, String chgpass,HttpSession session) {
		UserDto loginUser = (UserDto)session.getAttribute("loginUser");
		if(!password.equals(loginUser.getPassword())) {
			throw new ShopException("비밀번호 오류입니다.","password");
		}
		try {
			service.userChgPass(loginUser.getUserid(),chgpass);
			loginUser.setPassword(chgpass);
		} catch(Exception e) {  //db 수정시 오류 발생
			e.printStackTrace();
			throw new ShopException("비밀번호 변경시 db 오류입니다.","password");
		}
		return "redirect:mypage?userid=" + loginUser.getUserid();
	}
	/*
	 * @PathVariable : {url} 값을 매개변수로 전달. url에 해당하는 값을  String url 매개변수로 전달
	 *   idsearch 요청 : url=id
	 *   pwsearch 요청 : url=pw
	 */
	@PostMapping("{url}search") // xxsearch 요청시 호출되는 메서드(idsearch, pwsearch 요청)
	public ModelAndView search(UserDto user, BindingResult bresult, @PathVariable String url,HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		String code = "error.userid.search"; //아이디를 찾을 수 없습니다.
		if(url.equals("pw")) { //pwsearch 요청인 경우
			code = "error.password.search"; //비밀번호를 찾을 수 없습니다.
			if(user.getUserid() == null || user.getUserid().trim().equals("")) {
				bresult.rejectValue("userid", "error.required");
			}
		}
		if(user.getEmail() == null || user.getEmail().trim().equals("")) {
			bresult.rejectValue("email", "error.required");
		}
		if(user.getPhoneno() == null || user.getPhoneno().trim().equals("")) {
			bresult.rejectValue("phoneno", "error.required");
		}
		if(bresult.hasErrors()) {
			bresult.reject("error.input.check");
			return mav;
		}
		//입력값이 정상인 경우
		//result : db에서 조회한 아이디값 또는 비밀번호값
		String result = service.getSearch(user,url);
		if(result==null) { //아이디 또는 비밀번호를 찾지 못함
			bresult.reject(code);
			return mav;
		}
		//비밀번호 검색인 경우 비밀번호를 임의의 문자로 변경
		if(url.equals("pw")) {
			result = ShopUtil.getRandomString(6, false, true);
			service.userChgPass(user.getUserid(), result); //비밀번호 변경
			
			if (service.mailSend(user,result,request)) {
				mav.addObject("msg","비밀번호를 초기화 하여 메일로 전송했습니다.");
			} else {
				mav.addObject("msg","초기화된 비밀번호의 메일전송을 실패 했습니다:" + result);
			}
		}
		mav.addObject("result",result);
		mav.addObject("title",((url.equals("pw")?"비밀번호":"아이디")));
		mav.setViewName("search"); //뷰이름. /WEB-INF/view/search.jsp 페이지 선택
		return mav;
	}	
	//==================================
	@PostMapping("password2")
	public String loginCheckPassword2(UserPasswordDto userpass,BindingResult bresult,HttpSession session) {
		if(userpass.getPassword() == null || userpass.getPassword().trim().equals("")) {
			bresult.rejectValue("password", "error.required");
		}
		if(userpass.getChgpass() == null || userpass.getChgpass().trim().equals("")) {
			bresult.rejectValue("chgpass", "error.required");
		}
		if(userpass.getChgpass2() == null || userpass.getChgpass2().trim().equals("")) {
			bresult.rejectValue("chgpass2", "error.required");
		}
		if(bresult.hasErrors()) {
			bresult.reject("error.input.check"); //global 오류등록
			return null;
		}
		//변경비밀번호와 변경비밀번호 재입력값이 같은지 검증
		if(!userpass.getChgpass().equals(userpass.getChgpass2())) {
			bresult.reject("error.password.equals");
			return null;
		}
		UserDto loginUser = (UserDto)session.getAttribute("loginUser");
		if(!userpass.getPassword().equals(loginUser.getPassword())) {
			throw new ShopException("비밀번호 오류입니다.","password2");
		}
		try {
			service.userChgPass(loginUser.getUserid(),userpass.getChgpass()); //비밀번호를 db에서 변경
			loginUser.setPassword(userpass.getChgpass());   //세션정보의 비밀번호 변경
		} catch(Exception e) {  //db 수정시 오류 발생
			e.printStackTrace();
			throw new ShopException("비밀번호 변경시 db 오류입니다.","password2");
		}
		return "redirect:mypage?userid=" + loginUser.getUserid();
	}
	
}