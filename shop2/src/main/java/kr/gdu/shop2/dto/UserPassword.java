package kr.gdu.shop2.dto;

import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserPassword {
	private String userid;
	@NotEmpty(message = "비밀번호를 입력하세요")
	private String password;
	@NotEmpty(message = "변경비밀번호를 입력하세요")
	private String chgpass;
	@NotEmpty(message = "변경비밀번호 재입력을 입력하세요")
	private String chgpass2;
}
