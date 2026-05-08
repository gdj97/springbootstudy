package kr.gdu.shop3.exception;

import lombok.Data;

@Data
public class CartException extends RuntimeException{
	private String url;
	public CartException(String msg, String url) {
		super(msg);
		this.url = url;
	}
}