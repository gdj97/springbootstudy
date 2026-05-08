package kr.gdu.shop3.exception;

import lombok.Getter;

@Getter
public class LoginException extends RuntimeException {
	String url;
	public LoginException(String message,String url) {
		super(message);
		this.url = url;
	}	
}
