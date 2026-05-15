package kr.gdu.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import kr.gdu.dto.CommentDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@IdClass(CommentId.class)
@Data
@NoArgsConstructor
public class CommentEntity { //comment_entity 
	@Id
	private int num;
	@Id
	private int seq;
	private String writer;
	private String pass;
	private String content;
	@Temporal(TemporalType.TIMESTAMP)
	private Date regdate;
	public CommentEntity(CommentDto dto) {
		this.num = dto.getNum();
		this.seq = dto.getSeq();
		this.pass = dto.getPass();
		this.writer = dto.getWriter();
		this.content = dto.getContent();
		this.regdate = dto.getRegdate();
	}
	@PrePersist
	public void onPrePersist() {
		this.regdate = new Date();
	}
}
