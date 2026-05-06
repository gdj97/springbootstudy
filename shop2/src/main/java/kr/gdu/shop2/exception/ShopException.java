package kr.gdu.shop2.exception;

import lombok.Getter;

@Getter
public class ShopException extends RuntimeException {
	String url;
	public ShopException(String message,String url) {
		super(message);
		this.url = url;
	}
}
