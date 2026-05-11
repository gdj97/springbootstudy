package kr.gdu.shop3.dto;
import java.util.Date;
import jakarta.validation.constraints.NotEmpty;
import kr.gdu.shop3.entity.Board;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardDto {
	private int num;
	private String boardid;
	@NotEmpty(message="글쓴이를 입력하세요")
	private String writer;
	@NotEmpty(message="비밀번호를 입력하세요")
	private String pass;
	@NotEmpty(message="제목을 입력하세요")
	private String title;
	@NotEmpty(message="내용을 입력하세요")
	private String content;
	private MultipartFile file1;
	private String fileurl;
	private Date regdate;
	private int readcnt;
	private int grp;
	private int grplevel;
	private int grpstep;
	
	public BoardDto(Board board) {
		this.num = board.getNum();
		this.boardid = board.getBoardid();
		this.writer = board.getWriter();
		this.pass = board.getPass();
		this.title = board.getTitle();
		this.content = board.getContent();
		this.fileurl = board.getFile1();
		this.regdate = board.getRegdate();
		this.readcnt = board.getReadcnt();
		this.grp = board.getGrp();
		this.grplevel = board.getGrplevel();
		this.grpstep = board.getGrpstep();
	}
	
	public String getBoardName() {
		String boardName = null;
		switch(boardid) {
		   case "1" : boardName = "공지사항"; break;
		   case "2" : boardName = "자유게시판"; break;
		   case "3" : boardName = "QNA"; break;
		   default : boardName = "게시판번호오류";break;
		}
		return boardName;
	}
}
