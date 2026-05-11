package kr.gdu.shop3.service;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import jakarta.mail.Authenticator;
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
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kr.gdu.shop3.dto.UserDto;
import kr.gdu.shop3.entity.User;
import kr.gdu.shop3.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository dao;

	@Value("${resources.dir}")
	private String RESOURCES_DIR;

	@Value("${google.mail.id}")
	private String googleid;
	
	@Value("${google.mail.pw}")
	private String googlepw;

	public void userInsert(UserDto user) {
		dao.save(new User(user));
	}

	public UserDto getUser(String userid) {
		User u = dao.findById(userid).orElseGet(()->null);
		if (u != null)
  		    return new UserDto(dao.findById(userid).get());
		else
			return null;
	}

	public void userUpdate(UserDto user) {
		dao.save(new User(user));
	}

	public void userDelete(String userid) {
		dao.deleteById(userid);
	}

	@Transactional   //데이터베이스 변경시 처리 필수
	public void userChgPass(String userid, String chgpass) {
		dao.chgPass(userid,chgpass);
	}
	//아이디 찾기, 비밀번호찾기
	public String getSearch(UserDto user, String url) {
		if (url.equals("id")) {
			return  dao.searchByUserid(user.getEmail(),user.getPhoneno());
		} else {
		    return dao.searchByPassword(user.getUserid(),user.getEmail(),user.getPhoneno());
		}
	}

	public List<UserDto> userList() {
		//List<User> dao.findAll()
		return dao.findAll().stream().map(user->new UserDto(user)).toList();
	}
	public List<UserDto> getUserList(String[] idchks) {
		//findByUseridIn : List로 전달된 데이터를 이용하여 userid들을 in 연산자로 조회
		return dao.findByUseridIn(Arrays.asList(idchks)).stream().map(user->new UserDto(user)).toList();
	}
	/* 
	 * 메일전송시 필요한 환경변수
	 * 1. 구글 ID : application.properties 파일에서 값 조회. 멤버필드:googleid 선언한 상태
	 * 2. 구글 PW : application.properties 파일에서 값 조회. 멤버필드:googlepw 선언한 상태
	 * 3. mail.properties 파일 : 멤버필드 : RESOURCES_DIR 선언한 상태
	 */
	
	public boolean mailSend(UserDto user,String initpw,HttpServletRequest request) {
		Properties prop = new Properties();
		MyAuthenticator auth = new MyAuthenticator(googleid,googlepw);
		String path =RESOURCES_DIR + "mail.properties";
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			prop.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		prop.put("mail.smtp.user", googleid); //구글아이디로 메일 전송
		Session session = Session.getInstance(prop,auth);
		MimeMessage mailmsg = new MimeMessage(session);
		try {
			//보내는 사람 설정
			mailmsg.setFrom(new InternetAddress(googleid + "@gmail.com"));
			
//			mailmsg.setRecipient(Message.RecipientType.TO,new InternetAddress(user.getEmail()));
			mailmsg.setRecipient(Message.RecipientType.TO,new InternetAddress("myungshink67@gmail.com"));
			mailmsg.setSentDate(new Date());
			mailmsg.setSubject("비밀번호 초기화");
			MimeMultipart multipart =new MimeMultipart();
			MimeBodyPart message = new MimeBodyPart(); 
			//message type : "text/html; charset=utf-8" => 이메일에서 html 태그 인식
			//message type : "text/plain; charset=utf-8" => 이메일에서 html 태그 인식 안함
			message.setContent("<h1 style='color:blue;'>비밀번호 초기화 :" + initpw + "</h1>",
				               "text/html; charset=utf-8");  
			multipart.addBodyPart(message);
			mailmsg.setContent(multipart);
			Transport.send(mailmsg);
			return true;
		} catch(MessagingException me) {
			me.printStackTrace();
		}
		return false;
	}	
	public final class MyAuthenticator extends Authenticator {
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
