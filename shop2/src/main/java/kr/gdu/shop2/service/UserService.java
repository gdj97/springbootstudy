package kr.gdu.shop2.service;


import java.io.FileInputStream;
import java.io.IOException;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.gdu.shop2.dao.UserDao;
import kr.gdu.shop2.dto.User;

@Service
public class UserService {
	@Autowired
	private UserDao dao;

	public void userInsert(User user) {
		dao.insert(user);
	}

	public User getUser(String userid) {
		return dao.selectOne(userid);
	}

	public void userUpdate(User user) {
		dao.update(user);
	}

	public void userDelete(String userid) {
		dao.delete(userid);
	}

	public void userChgPass(String userid, String chgpass) {
		dao.chgPass(userid,chgpass);
	}

	public String getSearch(User user, String url) {
		return dao.search(user,url);
	}

	public List<User> userList() {
		return dao.list();
	}

	public List<User> getUserList(String[] idchks) {
		return dao.list(idchks);  //오버로딩된 메서드
	}
	
	public boolean mailSend(User user,String initpw,HttpServletRequest request) {
		String googleid = "구글아이디";
		String googlepw = "앱비밀번호";
		Properties prop = new Properties();
		MyAuthenticator auth = new MyAuthenticator(googleid,googlepw);
		String path = request.getServletContext().getRealPath("/") + "/WEB-INF/classes/mail.properties";
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
				               "text/plain; charset=utf-8");  
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
