package ex01_hash;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 * 1. usercipher 테이블 생성하기. useraccount와 같은 테이블로 생성하기
 *    create table usercipher select * from useraccount
 * 2. usercipher 테이블의 password 컬럼의 크기를 300으로 변경하기
 *    alter table usercipher modify column password varchar(300)
 * 3. usercipher 테이블의 userid를 기본키로 설정하기
 *    alter table usercipher add constraint primary key(userid)
 * 4. useraccount 테이블을 읽어서, usercipher 테이블의 password를 sha-256 알고리즘의 해쉬값으로 저장하기
 */
public class Exam01 {
	public static void main(String[] args) throws Exception {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/springdb","gduser","1234");
		PreparedStatement pstmt = conn.prepareStatement("select userid,password from useraccount");
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()) {
			String id = rs.getString("userid");
			String pw = rs.getString("password");
			if(pw == null) continue;
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] plain = pw.getBytes();
			byte[] hash = md.digest(plain);
			StringBuilder hashpw = new StringBuilder();
			for(byte b : hash) {
				hashpw.append(String.format("%02X", b));
			}
			pstmt = conn.prepareStatement("update usercipher1 set password=? where userid=?");
			pstmt.setString(1, hashpw.toString());
			pstmt.setString(2, id);
			pstmt.executeUpdate();
		}
	}
}
