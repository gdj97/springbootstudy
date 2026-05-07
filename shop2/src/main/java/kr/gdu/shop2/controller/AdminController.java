package kr.gdu.shop2.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import kr.gdu.shop2.dto.Mail;
import kr.gdu.shop2.dto.User;
import kr.gdu.shop2.exception.ShopException;
import kr.gdu.shop2.service.UserService;
/*
 * AdminController의 모든 메서드는 관리자 로그인된 경우만 실행됨
 * => AOP 설정 필요(AdminLoginAspect 클래스)
 */
@Controller
@RequestMapping("admin")
public class AdminController {
	@Autowired
	private UserService service;
	
	@Value("${resources.dir}")  
	private String RESOURCES_DIR;
	
	
	@RequestMapping("list")
	public ModelAndView list(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		//list : 모든 useraccount 테이블의 정보
		List<User> list = service.userList();
		mav.addObject("list",list);
		return mav;
	}
	/*
	 * String[] idchks : 화면에서 전송된 idchks 파라미터가 여러개인 경우. request.getParameterValues(파라미터이름)
	 * idchks : 사용자아이디값들 저장
	 */
	@PostMapping("mailform")
	public String mailform(String[] idchks, Model model) {
		if(idchks == null || idchks.length == 0) {
			throw new ShopException("메일을 보낼 대상자를 선택하세요","list");
		}
		//db에서 idchks내의 userid값에 해당하는 User 객체들 조회
		List<User> list = service.getUserList(idchks);
		Mail mail = new Mail();
		StringBuilder recipient = new StringBuilder();
		for (User u : list) {
			recipient.append(u.getUsername())  //테스트1<test1@aaa.bbb>,테스트2<test2@aaa.bbb>,
			         .append("<").append(u.getEmail()).append(">,");
		}
		mail.setRecipient(recipient.toString()); //수신자 정보
		mail.setGoogleid(""); //본인의 구글 id
		mail.setGooglepw(""); //본인의 앱비밀번호
		model.addAttribute("mail",mail);
		return "admin/mail";
	}
	   /*
	    * 구글 smtp 서버를 이용하여 메일 전송하기
	    * 1. 구글계정에접속하여 2단계 인증 설정하기
	    *     Google 계정관리 > 보안 및 로그인 > 2단계인증
	    * 2. 앱비밀번호 생성하기 
	    *     Google 계정관리 > Google 계정 검색 > 앱 비밀번호 검색 
	    * 3. 생성된 앱비밀번호를 메모장을 이용하여 저장하기 : momzgzwhcosuomnu
	    * 4. pom.xml에 mail 관련 설정 추가
	    * 5. mail.properties 파일 /resources/ 폴더에 생성하기
	    */
	
	@PostMapping("mail")
	public String mail(@Valid Mail mail,BindingResult bresult,Model model,HttpServletRequest request) {
		if(bresult.hasErrors()) {
			return null;
		}
		Properties prop = new Properties();
		try {
			String path = RESOURCES_DIR + "mail.properties";
			//fis : mail.properties 파일을 읽기 
			FileInputStream fis = new FileInputStream(path);
			prop.load(fis); //mail.properties 파일의 key=value 값으로 데이터 저장
			prop.put("mail.smtp.user", mail.getGoogleid()); //구글아이디로 메일 전송
		} catch(IOException e) {
			e.printStackTrace();
		}
		//mail : 화면에서 입력한 데이터. 
		//prop : 메일 전송을 위한 환경설정 데이터
		if(mailSend(mail,prop)) {
			model.addAttribute("message","메일 전송이 완료되었습니다.");
		} else {
			model.addAttribute("message","메일 전송을 실패했습니다.");
		}
		model.addAttribute("url","list");
		return "alert";  // /WEB-INF/view/alert.jsp
	}
	private boolean mailSend(Mail mail, Properties prop) {
		//Authenticator 객체 : 메일 인증 객체
		MyAuthenticator auth = new MyAuthenticator(mail.getGoogleid(),mail.getGooglepw());
		//session : 구글에서 메일전송을 할 수 있는 연결 객체. 
		Session session = Session.getInstance(prop,auth);
		//session 을 이용하여 메일객체 생성
		MimeMessage mailmsg = new MimeMessage(session);
		try {
			//보내는 사람 설정
			mailmsg.setFrom(new InternetAddress(mail.getGoogleid() + "@gmail.com"));
			
			List<InternetAddress> addrs = new ArrayList<InternetAddress>();
			String[] emails = mail.getRecipient().split(",");
			for(String email : emails) {
				try {
					addrs.add(new InternetAddress(new String(email.getBytes("utf-8"),"8859_1")));
				} catch (UnsupportedEncodingException ue) {
					ue.printStackTrace();
				}
			}
			InternetAddress[] arr = new InternetAddress[emails.length];
			for(int i=0;i<addrs.size();i++) {
				arr[i]=addrs.get(i);
			}
			//수신자 설정
			mailmsg.setRecipients(Message.RecipientType.TO,arr);
			//참조자 설정 : Message.RecipientType.CC
            //mailmsg.setRecipients(Message.RecipientType.CC,arr);
			mailmsg.setSentDate(new Date()); //보내는 일자
			mailmsg.setSubject(mail.getTitle());  //입력된 제목
			MimeMultipart multipart =new MimeMultipart();  //메일의 영역 : 내용, 첨부파일1,첨부파일2,...
			MimeBodyPart message = new MimeBodyPart();  //메일영역 구간
			message.setContent(mail.getContents(),mail.getMtype());  //내용 설정. 
			multipart.addBodyPart(message); //내용을 메일 저장
			for(MultipartFile mf : mail.getFile1()) {
				//mf : 첨부된 파일 한개
				if ((mf != null) && (!mf.isEmpty())) {
					multipart.addBodyPart(bodyPart(mf));  //첨부파일을 메일 저장
				}
			}
			mailmsg.setContent(multipart); //내용+첨부파일1+첨부파일2 => 메일의 내용으로 설정
			Transport.send(mailmsg);  //메일 전송
			return true;
		} catch(MessagingException me) {
			me.printStackTrace();
		}
		return false;
	}	
	private BodyPart bodyPart(MultipartFile mf) {
		//mf : 업로드된 파일의 내용
		MimeBodyPart body = new MimeBodyPart();
		String orgFile = mf.getOriginalFilename();
		String path = "c:/mailupload/"; //서버로 파일을 업로드 필요
		File f1 = new File(path);
		if(!f1.exists()) f1.mkdirs();
		File f2 = new File(path + orgFile);  //파일업로드폴더 + 파일이름
		try {
			mf.transferTo(f2);  //f2 파일에 mf 데이터를 저장. 업로드완료
			body.attachFile(f2); //메일에 파일을 추가. 첨부파일 설정
			body.setFileName(new String(orgFile.getBytes("UTF-8"),"8859_1"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body;
	}
	//인증객체. AdminController 클래스의 내부클래스로 구현함. 
	private final class MyAuthenticator extends Authenticator {
		private String id;
		private String pw;
		public MyAuthenticator(String id, String pw) {
			this.id = id;
			this.pw = pw;
		}
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(id,pw);
		}		
	}
}
