package kr.gdu.shop3.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import kr.gdu.shop3.dto.CommentDto;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
 * 복합키 : 2개컬럼 이상의 결합으로 키를 정의
 *   @Id 만 사용 불가함. 
 */
@Entity
@Table(name="comment")
@IdClass(CommentId.class)  //복합키인 경우 필수. 키의 구조 설정
@Data
@NoArgsConstructor
public class Comment {
	@Id
	private int num;
	@Id
	private int seq;
	private String writer;
	private String pass;
	private String content;
	@Temporal(TemporalType.TIMESTAMP)
	private Date regdate;
	public Comment(CommentDto dto) {
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
