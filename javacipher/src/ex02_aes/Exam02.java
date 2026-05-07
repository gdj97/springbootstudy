package ex02_aes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

/*
 *  화면에서 userid를 입력받아 이메일을 출력하는 프로그램 작성하기
 */
public class Exam02 {
	public static void main(String[] args) throws Exception {
		System.out.println("userid를 입력하세요");
		Scanner scan = new Scanner(System.in);
		String userid = scan.next();
		Class.forName("org.mariadb.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/springdb","gduser","1234");
		PreparedStatement pstmt = conn.prepareStatement("select * from usercipher1 where userid=?");
		pstmt.setString(1, userid);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			String cipheremail = rs.getString("email"); //암호화 되어 저장된 이메일값 조회
			String key = CipherUtil.makehash(userid);   //key가져오기
			String plainemail = CipherUtil.decrypt(cipheremail,key); 
			System.out.println("이메일:" + plainemail);
		}else {
			System.out.println("아이디 없음");
		}		
	}
}
