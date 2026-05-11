package kr.gdu.shop3.dto;

import java.util.Date;

import jakarta.validation.constraints.NotEmpty;
import kr.gdu.shop3.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class CommentDto {
	private int num;
	private int seq;
	@NotEmpty(message="작성자를 입력하세요")
	private String writer;
	@NotEmpty(message="비밀번호를 입력하세요")
	private String pass;
	@NotEmpty(message="내용를 입력하세요")
	private String content;
	private Date regdate;
	
	public CommentDto(Comment comm) {
		this.num = comm.getNum();
		this.seq = comm.getSeq();
		this.pass = comm.getPass();
		this.writer = comm.getWriter();
		this.content = comm.getContent();
		this.regdate = comm.getRegdate();		
	}
}
