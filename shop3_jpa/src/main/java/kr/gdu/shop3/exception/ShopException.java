package kr.gdu.shop3.exception;

import lombok.Getter;

@Getter
public class ShopException extends RuntimeException {
	String url;
	public ShopException(String message,String url) {
		super(message);
		this.url = url;
	}
}
