package kr.gdu.shop3.dto;

import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import kr.gdu.shop3.entity.User;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class UserDto {
	//@Size : 입력한 값의 길이가 3자이상 10자이하만 가능
	@Size(min=3,max=10,message="아이디는 3자이상 10자이하로 입력하세요")
	private String userid;
	private String channel;     
	@Size(min=3,max=10,message="비밀번호는 3자이상 10자이하로 입력하세요")
	private String password;
	@NotEmpty(message="사용자이름은 필수 입니다.")
	private String username;
	private String phoneno;
	private String postcode;
	private String address;
	@NotEmpty(message="email을 입력하세요.")
	@Email(message="email 형식으로 입력하세요") //email 형식부분 검사
	private String email;
	@NotNull(message="생일을 입력하세요")
	@Past(message="생일은 과거 날짜만 가능합니다.")
	@DateTimeFormat(pattern="yyyy-MM-dd") //날짜의 형식 지정
	private Date birthday;
	public UserDto(User user) {
		this.userid = user.getUserid();
		this.password = user.getPassword();
		this.username = user.getUsername();
		this.phoneno = user.getPhoneno();
		this.postcode = user.getPostcode();
		this.address = user.getAddress();
		this.email = user.getEmail();
		this.birthday = user.getBirthday();
	}

}
