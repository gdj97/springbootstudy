package kr.gdu.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.gdu.dto.BoardDto;
import kr.gdu.dto.CommentDto;
import kr.gdu.entity.BoardEntity;
import kr.gdu.entity.CommentEntity;
import kr.gdu.service.BoardService;
/*
 * @CrossOrigin : CORS(Cross-Origin Resource Sharing) 설정
 *    - SpringBoot환경에서 다른 도메인과 자원 공유 허용
 * origins="http://localhost:5173" : 주소의 요청만 허용  
 *                                   origins="*" : 모든 도메인과 자원 공유
 * allowCredentials="true" : 민감 정보 허용 (인증정보등)
 *                           origins="*"인 경우 true는 불가능함
 */
@RestController
@RequestMapping("/board/")
@CrossOrigin(origins="http://localhost:5173", allowCredentials="true")
public class BoardController {
	@Autowired
	BoardService service;
	
	@Value("${board.upload.dir}")
	private String UPLOAD_PATH;
	
	@GetMapping("boardList")
	public Map<String,Object> boardList(@RequestParam Map<String,String> param) {
		Integer pageInt = null;
		for(String key : param.keySet()) {
			if(param.get(key) == null || param.get(key).trim().equals("")) {
				param.put(key, null);
			}
		}
		if(param.get("page") != null) {
			pageInt = Integer.parseInt(param.get("page"));
		} else {
			pageInt = 1;
		}
		String boardid = param.get("boardid");
		if(boardid==null) boardid="1";
		String boardName = null;
		switch(boardid) {
		case "1" : boardName="공지사항";break;
		case "2" : boardName="자유게시판";break;
		case "3" : boardName="QNA";break;
		}
		int limit = 10;
		int listcount =service.boardCount(boardid);
		List<BoardEntity> blist = service.boardList(pageInt,limit,boardid).getContent();

		int bottomLine = 10;   //한화면에 출력될 페이지 갯수
		int start = (pageInt - 1) / bottomLine * bottomLine + 1;  //시작페이지번호
		int end = start + bottomLine - 1;   //끝 페이지번호
		int maxpage = (listcount / limit) + (listcount % limit == 0 ? 0 : 1);  //최대페이지번호
		if (end > maxpage) end = maxpage;  //
		/* -- Map 객체 생성 방법
		 * 1. Map map = new HashMap<>();
		 *    map.put("boardid",boardid);
		 *    .....
		 *    
		 * 2. Map.of(k1,v1,k2,v2....)
		 *    Java9에 추가됨
		 *    변경불가 객체임. 
		 *    최대 10개만 지원
		*/  
		System.out.println(start + "," + end + "," + maxpage);
		return Map.of(
				"boardid",boardid,
				"boardName",boardName,
				"pageInt",pageInt,
				"maxpage",maxpage,
				"start",start,
				"end",end,
				"listcount",listcount,
				"blist",blist,
				"bottomLine",bottomLine);		
	}
	@PostMapping("boardPro")
	public BoardEntity boardPro(@RequestParam(value="file2", required = false) 
	MultipartFile multipartFile, BoardDto boardDto)  {
		String path = UPLOAD_PATH+"img/board/";
		File dir = new File(path);
		if(!dir.exists()) dir.mkdirs();
		String filename="";
		if (multipartFile!=null && !multipartFile.isEmpty()) {
			File file = new File(path, multipartFile.getOriginalFilename());
			filename=multipartFile.getOriginalFilename();
			try {
				multipartFile.transferTo(file);
			} catch (Exception e) {
				e.printStackTrace();
		    }
		}
		boardDto.setBoardid(boardDto.getBoardid());
		boardDto.setFile1(filename);  //첨부파일 이름
		BoardEntity entity = service.insertBoard(new BoardEntity(boardDto));
		return entity;
	  }
	@GetMapping("boardInfo")
	public Map<String, Object> boardInfo(int num)  {
		BoardEntity board = service.getBoard(num);
		service.addReadcnt(num);
		String boardName = null;
		if(board.getBoardid() == null || board.getBoardid().equals("1"))
			boardName = "공지사항";
		else if(board.getBoardid().equals("2"))
			boardName = "자유게시판";
		else if(board.getBoardid().equals("3"))
			boardName = "QNA";
		
		List<CommentEntity> commlist = service.commentList(num); //num:게시물번호. 게시물번호에 해당하는 댓글목록 조회
		
		return	Map.of("board",board,
				"boardName",boardName,
				"clist",commlist);
	}	
	@GetMapping("boardUpdateForm")
	public Map<String, Object> boardUpdateForm(int num)  {
		BoardEntity board = service.getBoard(num);
		String boardName = null;
		if(board.getBoardid() == null || board.getBoardid().equals("1"))
			boardName = "공지사항";
		else if(board.getBoardid().equals("2"))
			boardName = "자유게시판";
		else if(board.getBoardid().equals("3"))
			boardName = "QNA";
		
		return	Map.of("board",board,
				"boardName",boardName);
	}
	@PostMapping("boardUpdatePro")
	public Map<String,Object> boardUpdatePro(
	@RequestParam(value="file2", required = false) MultipartFile multipartFile, 
	                BoardDto boardDto) throws IllegalStateException, IOException {
		//비밀번호 검증
	    BoardEntity dbBoard = service.getBoard(boardDto.getNum());
		Map<String,Object> map = new HashMap<>(); //react 전송할 결과 데이터 => react는 json 형식으로 전달됨
		//boardDto.getPass() : 입력된 비밀번호
		//dbBoard.getPass() : db에 저장된 비밀번호
		if(!boardDto.getPass().equals(dbBoard.getPass())) { //비밀번호 오류
			map.put("msg", "비밀번호 오류");
			map.put("code", 100);
			return map;
		}
		String path =UPLOAD_PATH+"img/board/";
		File dir = new File(path);
		/*
		 * mkdirs()  : 폴더의 깊이가 여러개인 경우 생성. 한개인 경우도 사용 가능
		 * mkdir() : 폴더의 깊이가 한개인 경우
		 */
		if(!dir.exists()) dir.mkdirs();  //폴더가 없으면 폴더 생성		
		String filename="";
		if (multipartFile != null && !multipartFile.isEmpty()) { //첨부파일 업로드 상태
			File file = new File(path, multipartFile.getOriginalFilename());
			filename=multipartFile.getOriginalFilename();
			multipartFile.transferTo(file);	//업로드 실행
			boardDto.setFile1(filename); //수정전 파일이름을 업로드된 파일의 이름 변경
		} else {
			boardDto.setFile1(dbBoard.getFile1()); //수정전 파일이름 
		}
		try {
		    boardDto.setRegdate(dbBoard.getRegdate()); //게시물 생성 일자 유지.
		    boardDto.setReadcnt(dbBoard.getReadcnt());
			service.boardUpdate(new BoardEntity(boardDto));  //save(BoardEntity)=>모든 컬럼이 변경됨. 
			map.put("msg", "게시글 수정완료");
			map.put("code", 0);
		} catch (Exception e) {
			map.put("msg", "게시글 수정에 실패 했습니다");
			map.put("code", 200);
			e.printStackTrace();
		}
		return map;  
	}
	@PostMapping("boardDeletePro")
	public Map<String,Object> boardDeletePro(
	@RequestParam("num") Integer num, @RequestParam("pass") String pass) {
	    BoardEntity dbBoard = service.getBoard(num);
		Map<String,Object> map = new HashMap<>();
		map.put("boardid", dbBoard.getBoardid());
		if(!pass.equals(dbBoard.getPass())) {
			map.put("msg", "비밀번호 오류");
			map.put("code", 100);
			return map;
		}
		try {
			service.boardDelete(num);
			map.put("msg", "게시글 삭제완료");
			map.put("code", 0);
		} catch (Exception e) {
			map.put("msg", "게시글 삭제에 실패 했습니다");
			map.put("code", 200);
			e.printStackTrace();
		}
		return map;
	}
	@PostMapping("CommentPro")
	public Map<String,Object> comment(CommentDto comm) {
		Map<String,Object> map = new HashMap<>();
		int seq = service.commmaxseq(comm.getNum()); //seq의 최대값
		comm.setSeq(++seq);
		service.commInsert(new CommentEntity(comm));  //댓글을 db에 등록
		map.put("msg", "댓글 등록완료");
		map.put("code", 0);
		return map;		
	}
	@PostMapping("CommentDelete")
	public Map<String,Object> commdel(CommentDto comm) {
		// num,seq,pass 파라미터값이 저장됨
		Map<String,Object> map = new HashMap<>();
		CommentEntity dbComm = service.getComment(comm.getNum(),comm.getSeq()); //삭제 대상 댓글 레코드 조회
		//비밀번호 검증
		if(comm.getPass().equals(dbComm.getPass())) {   //일치
			service.commentDel(comm.getNum(),comm.getSeq());  //삭제 대상 레코드 삭제
			map.put("msg", "댓글 삭제완료");
			map.put("code", 0);
			return map;
		} else {   //비밀번호 불일치
			map.put("msg", "비밀번호 오류");
			map.put("code", 100);
			return map;
		}
	}	
}
