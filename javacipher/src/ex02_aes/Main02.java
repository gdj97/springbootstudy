package ex02_aes;

public class Main02 {
	public static void main(String[] args) {
		String plain1 = "안녕하세요 김삿갓입니다";
		String key = "abc1234567";
		String cipher1 = CipherUtil.encrypt(plain1,key);
		System.out.println("암호문:"+cipher1);
		String plain2 = CipherUtil.decrypt(cipher1,key); 
		System.out.println("복호문:"+plain2);
		System.out.println("====================");
		key = "abc12345";
		cipher1 = CipherUtil.encrypt(plain1,key);
		System.out.println("암호문:"+cipher1);
		plain2 = CipherUtil.decrypt(cipher1,key); 
		System.out.println("복호문:"+plain2);
	}
}
