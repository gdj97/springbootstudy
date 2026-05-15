package kr.gdu.dto;

import java.util.Date;

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
	private String writer;
	private String pass;
	private String content;
	private Date regdate;
}
