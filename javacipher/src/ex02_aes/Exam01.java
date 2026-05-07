package ex02_aes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 * 1. usercipher 테이블의 email 컬럼의 크기를 2000으로 변경하기
 *    alter table usercipher1 modify column email VARCHAR(2000)
 * 2. key userid의 해쉬값(SHA-256)의 16자리로 설정하기
 * 
 * useraccount 테이블의 email을 읽어서 usercipher 테이블에 암호화 하여 저장하기
 */
public class Exam01 {
	public static void main(String[] args) throws Exception {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/springdb","gduser","1234");
		PreparedStatement pstmt = conn.prepareStatement("select * from useraccount");
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()) {
			String userid = rs.getString("userid");
			String email = rs.getString("email");
			String key = CipherUtil.makehash(userid);
			String cipherEmail = CipherUtil.encrypt(email,key); 
			pstmt = conn.prepareStatement("update usercipher1 set email=? where userid=?");
			pstmt.setString(1, cipherEmail);
			pstmt.setString(2, userid);
			pstmt.executeUpdate();			
		}
	}
}
