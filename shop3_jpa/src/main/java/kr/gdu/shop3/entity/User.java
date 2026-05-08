package kr.gdu.shop3.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.gdu.shop3.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="useraccount")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	@Id
	private String userid;
	private String channel;
	private String password;
	private String username;
	private String phoneno;
	private String postcode;
	private String address;
	private String email;
	private Date birthday;
	public User(UserDto user) {
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
