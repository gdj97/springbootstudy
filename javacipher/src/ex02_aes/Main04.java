package ex02_aes;

public class Main04 {
	public static void main(String[] args) {
		String key = "abc123456";
		CipherUtil.decryptFile("cipher.sec","plain2.txt",  key);
	}
}
