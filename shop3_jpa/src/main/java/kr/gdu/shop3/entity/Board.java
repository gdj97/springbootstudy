package kr.gdu.shop3.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import kr.gdu.shop3.dto.BoardDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity  //db의 테이블로 인식
@Table(name="board") //물리적으로 db에 생성된 테이블이름. name=board 부분이 없으면 명명규칙에 의해서 table생성됨
@Data
@NoArgsConstructor
public class Board {
	@Id   //기본키로 설정함. 모든 엔티티는 @Id어노테이션 필수
	private int num;
	private String boardid,writer,pass,title,content,file1;
	@Temporal(TemporalType.TIMESTAMP)  //날짜 형식 설정 : 날짜+시간, DATE : 날짜, TIME:시간
	private Date regdate;
	private int readcnt,grp,grplevel,grpstep;

	public Board(BoardDto dto) {
		this.num = dto.getNum();
		this.boardid = dto.getBoardid();
		this.writer = dto.getWriter();
		this.pass = dto.getPass();
		this.title = dto.getTitle();
		this.content = dto.getContent();
		this.file1 = dto.getFileurl();
		this.regdate = dto.getRegdate();
		this.readcnt = dto.getReadcnt();
		this.grp = dto.getGrp();
		this.grplevel = dto.getGrplevel();
		this.grpstep = dto.getGrpstep();
	}
	@PrePersist
	public void onPrePersist() {
		this.regdate = new Date(); //현재 시간 설정
	}
}
