package ex02_aes;
//파일을 읽어서 암호화, 복호화 하기
public class Main03 {
	public static void main(String[] args) {
		String key = "abc123456";
		//plain1.txt : 프로젝트폴더에 생성
		CipherUtil.encryptFile("plain1.txt", "cipher.sec", key);
	}
}
