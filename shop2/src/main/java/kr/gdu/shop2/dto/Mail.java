package kr.gdu.shop2.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Setter
@Getter
@ToString
public class Mail {
	@NotEmpty(message="구글 아이디를 입력하세요")
	private String googleid;
	@NotEmpty(message="구글 비밀번호를 입력하세요")
	private String googlepw;
	private String recipient;
	@NotEmpty(message="제목을 입력하세요")
	private String title;
	private String mtype;
	//file1 이름의 파일업로드가 여러개인 경우 List 객체로 전달함. 
	private List<MultipartFile> file1;
	@NotEmpty(message="내용을 입력하세요")
	private String contents;
}
