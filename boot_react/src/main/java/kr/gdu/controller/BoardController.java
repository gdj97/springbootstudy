package kr.gdu.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.gdu.dto.BoardDto;
import kr.gdu.entity.BoardEntity;
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
		
		return	Map.of("board",board,
				"boardName",boardName);
	}	
	
}
