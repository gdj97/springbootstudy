package kr.gdu.shop2.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import kr.gdu.shop2.dto.Board;
import kr.gdu.shop2.dto.Comment;
import kr.gdu.shop2.exception.ShopException;
import kr.gdu.shop2.service.BoardService;

@Controller
@RequestMapping("board")
public class BoardController {
	@Autowired
	private BoardService service;
	
	@GetMapping("*")
	public String getForm(Model model) {
		model.addAttribute(new Board());
		return null;
	}
	@PostMapping("write")
	public String write(@Valid Board board,BindingResult bresult, HttpServletRequest request) {
		if(bresult.hasErrors()) {
			return null;
		}
		
		if(board.getBoardid() == null || board.getBoardid().trim().equals("")) board.setBoardid("1");
		service.boardWrite(board,request);
		return "redirect:list?boardid=" + board.getBoardid();
	}
	
	@RequestMapping("list")
	public ModelAndView list(@RequestParam Map<String,String> param, HttpSession session) {
		//@RequestParam : 파라미터값을 Map객체로 파라미터이름=파라미터값의 형태로 전달
		System.out.println(param); //pageNum : 2
		Integer pageNum = null;
		//param.keySet() : 파라미터이름 목록
		for(String key : param.keySet()) {
			if(param.get(key) == null || param.get(key).trim().equals("")) {
			   param.put(key, null);	
			}
		}
		if (param.get("pageNum") != null) { //pageNum 파라미터가 존재하는 경우
			   pageNum = Integer.parseInt(param.get("pageNum"));
		} else {   //pageNum 파라미터가 없는 경우
			pageNum = 1;
		}
		String boardid = param.get("boardid");
		if (boardid == null) boardid = "1";
		String searchtype = param.get("searchtype");
		String searchcontent = param.get("searchcontent");
		
		ModelAndView mav = new ModelAndView();
		String boardName = null;
		switch(boardid) {
		   case "1" : boardName = "공지사항"; break;
		   case "2" : boardName = "자유게시판"; break;
		   case "3" : boardName = "QNA"; break;
		}
		int limit = 10;  //화면에 출력될 게시물 건수. 
		int listcount = service.boardcount(boardid,searchtype,searchcontent); //게시판 종류별,검색내용으로 전체 등록된 게시물 건수
		List<Board> boardlist = service.boardlist(pageNum,limit,boardid,searchtype,searchcontent); //화면에 출력할 게시글 목록
		int maxpage = (int)((double)listcount/limit + 0.95); //최대페이지
		/*  listcount : 3
		 *   (int)((double)3/10 + 0.95) => (int)(1.25) > 1 
		 *  listcount : 31
		 *   (double)31/10 => 3.1 + 0.95 => 4.05 => (int)(4.05) > 4
		 *  listcount : 40
		 *   (double)40/10 => 4.0 + 0.95 => 4.95 => (int)(4.95) > 4
		 *  listcount : 501
		 *   (double)501/10 => 50.1 + 0.95 => 51.05 => (int)(51.05) > 51 
		 */
		int startpage = (int)((pageNum/10.0 + 0.9) - 1) * 10 + 1;
		/*
		 *  현재 페이지 : 1  : 1 ~ 10
		 *    1/10.0 => 0.1 => 0.1 + 0.9 => 1.0 - 1 => (int)(0.0) => 0 * 10 => 0 + 1 => 1 
		 *  현재 페이지 : 5  : 1 ~ 10
		 *    5/10.0 => 0.5 => 0.5 + 0.9 => 1.4 - 1 => (int)(0.4) => 0 * 10 => 0 + 1 => 1 
		 *  현재 페이지 : 10  : 1 ~ 10
		 *    10/10.0 => 1.0 => 1.0 + 0.9 => 1.9 - 1 => (int)(0.9) => 0 * 10 => 0 + 1 => 1 
		 *  현재 페이지 : 11 : 11 ~ 20
		 *    11/10.0 => 1.1 => 1.1 + 0.9 => 2.0 - 1 => (int)(1.0) => 1 * 10 => 10 + 1 => 11 
		 *  현재 페이지 : 15 : 11 ~ 20
		 *    15/10.0 => 1.5 => 1.5 + 0.9 => 2.4 - 1 => (int)(1.4) => 1 * 10 => 10 + 1 => 11 
		 */
		int endpage = startpage + 9;
		if(endpage > maxpage) endpage = maxpage; //화면에 출력할 페이지는 maxpage값을 넘지 못함
		mav.addObject("boardid",boardid);      //게시판 종류
		mav.addObject("boardName", boardName); //게시판 종류 이름
		mav.addObject("pageNum", pageNum);     //현재 페이지번호
		mav.addObject("maxpage", maxpage);     //최대 페이지 
		mav.addObject("startpage", startpage); //화면에 출력된 시작 페이지
		mav.addObject("endpage", endpage);     //화면에 출력된 종료 페이지
		mav.addObject("listcount", listcount); //전체 등록된 게시물 건수
		mav.addObject("boardlist", boardlist); //출력할 게시물 목록
		int boardno = listcount - (pageNum - 1) * limit;
		/*
		 * 현재페이지 1. 게시물건수 : 21
		 *    21 - 0 * 10 : 21
		 * 현재페이지 2. 게시물건수 : 21
		 *    21 - (2-1) * 10 :  11
		 * 현재페이지 3. 게시물건수 : 21
		 *    21 - (3-1) * 10 :  21
		 *   
		 */
		mav.addObject("boardno", boardno);     //화면에 보여질 게시물 번호의 시작값
		mav.addObject("today", new SimpleDateFormat("yyyyMMdd").format(new Date()));
		return mav;
	}
	@GetMapping("detail")
	public String detail(Integer num,Boolean countable, Model model) {
		if (countable == null) countable = false;
		Board board = service.getBoard(num); //num의 게시물 조회
		if(countable) {
			service.addReadcnt(num);   //조회수 증가
		}
		model.addAttribute("board",board); 
		model.addAttribute("comment",new Comment());
		List<Comment> commlist = service.commentList(num); //num:게시물번호. 게시물번호에 해당하는 댓글목록 조회
		model.addAttribute("commlist",commlist);
		return null; // /WEB-INF/view/board/detail.jsp 요청
	}
	
	@GetMapping({"reply","update","delete"})
	public String getBoard(Integer num,Model model) {
		Board board = service.getBoard(num);
		model.addAttribute("board",board);
		return null;
	}
	/*
	 * order by grp desc, grpstep asc
	 * 
	 *      num  grp grplevel grpstep 
	 * 원글   3    3      0       0  
	 * 원글   2    2      0       0  
	 * 답글   6    2      1       1  
	 * 답글   7    2      2       2  
	 * 답글   4    2      1       3  
	 * 원글   1    1      0       0  
	 * 답글   5    1      1       1  
	 * 
	 */
	/*
	 * 1. 유효성 검사하기-파라미터값 저장. 
	 *     - 원글정보 : num,grp,grplevel,grpstep,boardid
	 *     - 답글정보 : writer,pass,title,content
	 * 2. db에 insert => BoardService.boardReply()
	 *     - 원글의 grpstep 보다 큰 기존 등록된 답글의 grpstep 값을 +1 수정 
	 *       => BoardDao.grpStepAdd()
	 *       
	 *     - num : maxNum() + 1  
	 *     - db에 insert  => BoardDao.insert()
	 *       grp : 원글과 동일
	 *       grplevel : 원글의 grplevel + 1    
	 *       grpstep : 원글의 grpstep + 1
	 *       
	 * 3. 등록 성공 : list로 페이지 이동
	 *    등록 실패 : "답변 등록시 오류 발생" reply 페이지 이동           
	 */		
	@PostMapping("reply")
	public String reply(@Valid Board board,BindingResult bresult,Model model ) {
		if(bresult.hasErrors()) {
    		Map<String,Object> map = bresult.getModel();
    		Board b = (Board)map.get("board"); //화면에서 입력받은 값을 저장한 Board 객체
    		b.setTitle(board.getTitle().substring(3));//원글의 제목으로 변경
			model.addAllAttributes(bresult.getModel());
			return null;
		}
		try {
			service.boardReply(board);
			return "redirect:list?boardid=" + board.getBoardid();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ShopException("답변등록시 오류 발생","reply?num="+board.getNum());
		}
	}
	/*
	 * 1. 유효성 검사하기-파라미터값 저장.
	 * 2. 비밀번호 검증
	 * 3. db에 update => BoardService.boardReply()
	 *     - db에 update  => BoardDao.update()
	 * 4. 변경 성공 : list로 페이지 이동
	 *    변경 실패 : "게시글 수정시 오류 발생" update 페이지 이동           
	 */		
	@PostMapping("update")
	public String update(@Valid Board board, BindingResult bresult, HttpServletRequest request) {
		if(bresult.hasErrors()) {
			return null;
		}
		Board dbBoard = service.getBoard(board.getNum());
		//board.getPass() : 입력된 비밀번호
		//dbBoard.getPass() : db에 등록된 비밀번호
		if(!board.getPass().equals(dbBoard.getPass())) {
			throw new ShopException("비밀번호가 틀립니다.",  "update?num="+board.getNum());			
		}
		try {
			//1. db의 내용을 등록된 내용으로 변경 : writer, title, content, file1
			//2. file 업로드.
			service.boardUpdate(board,request);
			return "redirect:list?boardid="+board.getBoardid();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ShopException("게시글 수정에 실패 했습니다.", "update?num="+board.getNum());			
		}
	}	
	/*
    1. 비밀번호가 일치하면 num 해당하는 게시물 삭제.
       비밀번호 오류시 globalError 방식으로 처리하기
    2. BoardService.boardDelete 
       boardDao.delete 메서드 명으로 처리하기   
	 */
	@PostMapping("delete")
	public String delete(Board board, BindingResult bresult,Model model) {
		Board dbBoard = service.getBoard(board.getNum());
		//입력값 검증
		if(board.getPass() == null || board.getPass().trim().equals("")) {
			bresult.reject("error.required.password");
			return "board/delete";
		}
		//비밀번호 검증
		if(!board.getPass().equals(dbBoard.getPass())) {
			bresult.reject("error.check.password");
			return null;
		}
		//게시물 삭제
		try {
			service.boardDelete(board.getNum());
			return "redirect:list?boardid="+dbBoard.getBoardid();
		} catch (Exception e) {
			bresult.reject("error.board.delete");
			return null;
		}
	}
	@RequestMapping("comment")  //댓글 등록
	public String comment(@Valid Comment comm,BindingResult bresult,Model model) {
		//model : 뷰에 전달할 데이터 정보들
		//bresult : @Valid에 의해서 검증된 결과 저장
		String view = "board/detail"; //board,comment+오류정보,commlist 정보필요
		if(bresult.hasErrors()) {
			detail(comm.getNum(),false,model); //board, commlist 정보, 
			model.addAllAttributes(bresult.getModel()); //getModel() : comment+오류정보
			return view;   //detail.jsp로 페이지만 이동. board 데이터 없음. 
		}
		int seq = service.commmaxseq(comm.getNum());  //댓글번호 최대값
		comm.setSeq(++seq); //댓글 등록전에 seq 설정.
		service.comminsert(comm);  //댓글 등록
		return "redirect:detail?num="+comm.getNum()+"#comment";
	}
	/*
	 * 1. 파라미터 : num,seq,pass
	 * 2. pass값과 db에 등록된 비밀번호 검증
	 *    일치 : 게시물 삭제. detail로 페이지 이동
	 *    불일치 : 비밀번호 오류. detail로 페이지 이동
	 */
	@PostMapping("commdel")
	public String commdel(Comment comm) {
		Comment dbComm = service.getComment(comm.getNum(),comm.getSeq());
		if(comm.getPass().equals(dbComm.getPass())) {
			service.commendDel(comm.getNum(),comm.getSeq());
			return "redirect:detail?num="+comm.getNum() + "#comment";
		} else {
			throw new ShopException
			("비밀번호가 틀립니다. 비밀번호를 확인하세요","detail?num="+comm.getNum() + "#comment");
		}
	}
	
}
