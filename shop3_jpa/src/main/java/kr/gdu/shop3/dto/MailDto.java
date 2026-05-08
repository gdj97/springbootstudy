package kr.gdu.shop3.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Setter
@Getter
@ToString
public class MailDto {
	@NotEmpty(message="수신자를 입력하세요")
	private String recipient;
	@NotEmpty(message="제목을 입력하세요")
	private String title;
	private String mtype;
	//file1 이름의 파일업로드가 여러개인 경우 List 객체로 전달함. 
	private List<MultipartFile> file1;
	@NotEmpty(message="내용을 입력하세요")
	private String contents;
}
