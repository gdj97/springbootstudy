package ex02_aes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtil {
    private static byte[] randomKey; //암호화,복호화에 사용되는 키
    //초기화벡터
    private final static byte[] iv = new byte[] { //16바이트 => 128비트
    		(byte)0x8E,0x12,0x39,(byte)0x90,
    		0x07,0x72,0x6F,(byte)0x5A,
    		(byte)0x8E,0x12,0x39,(byte)0x90,
    		0x07,0x72,0x6F,(byte)0x5A};
    static Cipher cipher; //암호화/복호화 객체
    static {
    	try {
    		/*
    		 * AES : 암호화 알고리즘
    		 * CBC : 블럭모드 설정. 앞의 암호문을 뒤쪽에 암호문에 영향줌. IV 필요
    		 * PKCS5Padding : padding 방식 설정. 평문을 블럭화할때, 지정된 블럭 크기로 설정
    		 */
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    //키 자동 생성
	public static byte[] getRandomKey(String algo) 
			                       throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance(algo);
		keyGen.init(128); //128비트로 설정
		SecretKey key = keyGen.generateKey();
		return key.getEncoded(); 
	}    
	//평문 => 암호문 생성
	public static String encrypt(String plain) { //안녕하세요 김삿갓입니다
		byte[] cipherMsg = new byte[1024];
		try {
			randomKey = getRandomKey("AES"); //AES용 키값 생성
			Key key = new SecretKeySpec(randomKey, "AES");  //키 객체 생성
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv); //초기화블럭
			//Cipher.ENCRYPT_MODE : 암호 모드
			cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec); //암호객체 초기화
			cipherMsg = cipher.doFinal(plain.getBytes()); //암호화
		} catch (Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg).trim(); //암호문을 16진수 문자열로 리턴
	}
	// 암호문 => 평문 생성. 복호화
	public static String decrypt(String cipherMsg) {
		byte[] plainMsg = new byte[1024];
		try {
			Key key = new SecretKeySpec(randomKey, "AES"); //암호화에 사용된 키를 사용
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			//Cipher.DECRYPT_MODE : 복호화 모드
			cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			plainMsg = cipher.doFinal(hexToByte(cipherMsg.trim()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(plainMsg).trim(); //평문을 문자열로 리턴
	}
	// byte[] => 16진수 문자열로 변경
	private static String byteToHex(byte[] cipherMsg) {
		if (cipherMsg == null)	return null;
		String str = "";
		for (byte b : cipherMsg) {
			str += String.format("%02X", b);
		}
		return str;
	}
	// 16진수 문자열 => byte[] 변경
	private static byte[] hexToByte(String str) {
		if (str == null || str.length() < 2)	return null; 
		int len = str.length() / 2;
		byte[] buf = new byte[len];
		for (int i = 0; i < len; i++) {
			buf[i] = (byte)Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
		}
		return buf;
	}
//=================================================================
	//사용자가 지정한 key 사용. key값을 AES알고리즘에서 사용되는 키로 변경
	private static byte[] makeKey(String key) {
		int len = key.length();
		char ch='A';
		for(int i=len;i < 16; i++) {
			key += ch++;
		}
		return key.substring(0,16).getBytes();
	}	
	//주어진 key로 암호화
	public static String encrypt(String plain1, String key) {
		byte[] cipherMsg = new byte[1024];
		try {
			Key genKey = new SecretKeySpec(makeKey(key),"AES"); //128비트로 키를 변경하기
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, genKey, paramSpec);
			cipherMsg = cipher.doFinal(plain1.getBytes());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg);
	}
	//주어진 key로 복호화
	public static String decrypt(String cipher1, String key) {
		byte[] plainMsg = new byte[1024];
		try {
			Key genKey = new SecretKeySpec(makeKey(key),"AES");
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, genKey, paramSpec);
			plainMsg = cipher.doFinal(hexToByte(cipher1.trim()));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return new String(plainMsg).trim();
	}
//=========================================================================
	//평문이 저장된 파일을 읽어서, 암호문이 저장된 파일로 생성 
	public static void encryptFile(String plainFile, String cipherFile, String strkey) {
		//plainFile : 암호화 대상이 되는 파일의 이름
		//cipherFile : 결과 파일. 암호화된 파일의 이름
		try {
			getKey(strkey); //대칭키를 파일로 저장
			ObjectInputStream ois =new ObjectInputStream(new FileInputStream("key.ser"));
			Key key = (Key)ois.readObject(); //key 객체
			ois.close();
			
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec); //암호화 모드
			FileInputStream fis = new FileInputStream(plainFile); //평문 저장 파일 열기
			FileOutputStream fos = new FileOutputStream(cipherFile); //암호문 저장 파일 열기
			//CipherOutputStream : 암호화/복호화에 사용되는 출력스트림.
			CipherOutputStream cos = new CipherOutputStream(fos, cipher); //암호화되는 스트림
			byte[] buf = new byte[1024];
			int len;
			while ((len = fis.read(buf)) != -1) {
				cos.write(buf, 0, len); //평문을 암호화하여 저장
			}
			fis.close(); cos.flush();	fos.flush();
			cos.close(); fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void getKey(String key) throws Exception {
		Key genkey = new SecretKeySpec(makeKey(key), "AES");
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("key.ser"));
		out.writeObject(genkey); 
		out.flush();	out.close();
	}
	//암호문이 저장된 파일을 읽어서, 평문이 저장된 파일로 생성 
	public static void decryptFile(String cipherFile, String plainFile, String strkey) {
		try {
			//파일에 저장된 key 객체를 읽기
			ObjectInputStream ois =new ObjectInputStream(new FileInputStream("key.ser"));
			Key key = (Key) ois.readObject(); 
			ois.close();
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, key, paramSpec); // 복호화 모드
			FileInputStream fis = new FileInputStream(cipherFile);  //암호화된 파일 읽기
			FileOutputStream fos = new FileOutputStream(plainFile); //복호화된(평문) 파일에 열기.
			CipherOutputStream cos = new CipherOutputStream(fos, cipher); //복호화 하여 파일에 저장
			byte[] buf = new byte[1024];
			int len;
			while ((len = fis.read(buf)) != -1) {
				cos.write(buf, 0, len); //복호화하여 파일에 저장
			}
			fis.close();			cos.flush();
			fos.flush();			cos.close();			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//해쉬알고리즘 
	public static String makehash(String userid) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] plain = userid.getBytes();
		byte[] hash = md.digest(plain);
		return byteToHex(hash);
	}
}
