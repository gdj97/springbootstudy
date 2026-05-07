package ex01_hash;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

/*
 * 화면에서 아이디,비밀번호 입력받기
 * usercipher 테이블 조회하기
 *  - 아이디가 없으면 : 아이디 없음 출력
 *  - 비밀번호 오류  : 비밀번호 틀림 출력
 *  - 일치 : 반갑습니다. 이름님 출력
 */
public class Main02 {
	public static void main(String[] args) throws Exception {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/springdb","gduser","1234");
		PreparedStatement pstmt = 
				conn.prepareStatement("select password,username from usercipher1 where userid=?");
		Scanner scan = new Scanner(System.in);
		System.out.println("아이디와 비밀번호를 입력하세요");
		String id = scan.next();
		String pw = scan.next();
		pstmt.setString(1, id);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			StringBuilder hashpw = new StringBuilder();
			byte[] plain = pw.getBytes();
			byte[] hash = md.digest(plain);
			for(byte b : hash) hashpw.append(String.format("%02X", b));
			if(rs.getString("password").equals(hashpw.toString())) {
				System.out.println("반갑습니다. " + rs.getString("username") + "님");
			} else {
				System.out.println("비밀번호 틀림");
			}
		} else {
			System.out.println("아이디 없음");
		}

	}
}
