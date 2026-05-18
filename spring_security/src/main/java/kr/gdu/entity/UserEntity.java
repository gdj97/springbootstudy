package kr.gdu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class UserEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id; //자동 증가 설정
	@Column(unique=true)   //unique 제약조건 설정
	private String username;   //아이디
	private String password;   //비밀번호
	private String role;	   //권한
}
