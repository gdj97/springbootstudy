package kr.gdu.shop2.service;
import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.gdu.shop2.dao.BoardDao;
import kr.gdu.shop2.dao.CommentDao;
import kr.gdu.shop2.dto.Board;
import kr.gdu.shop2.dto.Comment;
@Service
public class BoardService {
	@Autowired
	BoardDao dao;
	@Autowired
	CommentDao commDao;

	public void boardWrite(Board board, HttpServletRequest request) {
		int maxnum = dao.maxNum();  //board 테이블의 최대 num 컬럼의 값을 리턴
		board.setNum(maxnum+1);
		board.setGrp(maxnum+1); //원글의 경우 grp 컬럼의 값은 num 컬럼의 값과 같음
		if(board.getFile1() != null && !board.getFile1().isEmpty()) { //업로드된 파일이 존재.
			String path = request.getServletContext().getRealPath("/") + "board/file/"; //업로드되는 폴더설정
			uploadFileCreate(board.getFile1(),path);  //파일 업로드
			board.setFileurl(board.getFile1().getOriginalFilename()); //파일이름 설정
		}
		dao.insert(board); //board 테이블에 게시글 추가.
	}

	private void uploadFileCreate(MultipartFile file1, String path) {
		String orgFile = file1.getOriginalFilename();
		File f = new File(path);
		if(!f.exists()) f.mkdirs();
		try {
			file1.transferTo(new File(path+orgFile));
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}	
	public int boardcount(String boardid, String searchtype, String searchcontent) {
//		return dao.count(boardid,searchtype,searchcontent);
		return dao.count2(boardid,searchtype,searchcontent);
	}
	public List<Board> boardlist
	   (Integer pageNum, int limit, String boardid,String searchtype, String searchcontent) {
//		return dao.list(pageNum,limit,boardid,searchtype,searchcontent);
		return dao.list2(pageNum,limit,boardid,searchtype,searchcontent);
	}

	public Board getBoard(Integer num) {
		return dao.selectOne(num); 
	}
	public void addReadcnt(Integer num) {
		dao.addReadcnt(num); 
	}

	public void boardReply(Board board) {
		dao.grpStepAdd(board); //grp 내의 기존의 원글보다 큰 값을 가진 grpstep의 값을 1 증가시킴. 
		//답글의 내용을 db에 등록
		int max = dao.maxNum(); //board 테이블에서 num값의 최대값
		board.setNum(max+1);    //최대값+1로 추가될 게시물의 num값을 설정
		board.setGrplevel(board.getGrplevel() + 1); //원글 + 1
		board.setGrpstep(board.getGrpstep() + 1);   //원글의 바로다음에 출력되도록 설정
		//원글의 grp, boardid 값은 그대로 유지
		dao.insert(board);
	}

	public void boardUpdate(Board board, HttpServletRequest request) {
		//첨부파일 업로드
		if(board.getFile1() != null && !board.getFile1().isEmpty()) { //첨부파일이 수정된경우. 
			String path = request.getServletContext().getRealPath("/") + "board/file/";
			uploadFileCreate(board.getFile1(), path);  
			//board.getFileurl() : 수정전 첨부파일명 
			board.setFileurl(board.getFile1().getOriginalFilename()); //첨부된 파일이름 fileUrl 프로퍼티값 변경
		}
		//db 수정
		dao.update(board);		
	}

	public void boardDelete(int num) {
		dao.delete(num);
	}
	public int commmaxseq(int num) {
		return commDao.maxseq(num);
	}
	public void comminsert(Comment comm) {
		commDao.insert(comm);
	}
	public List<Comment> commentList(Integer num) {
		return commDao.list(num);
	}

	public Comment getComment(int num, int seq) {
		return commDao.selectOne(num,seq);
	}

	public void commendDel(int num, int seq) {
		commDao.delete(num,seq);
	}	
	public Map<String, Integer> graph1(String id) {  //게시판 종류별, 글작성자별 등록 건수
		List<Map<String,Object>> list = dao.graph1(id);
		/*
		 * list :
		 * [
		 *    {"writer":"홍길동","cnt":3},
		 *    {"writer":"111","cnt":2},
		 *    ...
		 * ]
		 *  => 글작성자 : 게시물등록건수
		 *  {
		 *     "홍길동":3,
		 *     "111" : 2,
		 *     ...
		 *  }
		 */
		Map<String, Integer> map = new HashMap<>();//{홍길동:3,111:2,...}
		for(Map<String,Object> m : list) {
			//m : {"writer":"홍길동","cnt":3}
		    String writer =(String)m.get("writer"); //홍길동
		    long cnt = (Long) m.get("cnt");         // 갯수. count(*) 로 조회된 컬럼은 long으로 리턴
		    map.put(writer,(int)cnt);
		}		
		return map;
	}

	public Map<String, Integer> graph2(String id) {
			List<Map<String,Object>> list = dao.graph2(id);
			//TreeMap : 키순으로 정렬. Comparator.reverseOrder() : 기본정렬방식의 역순정렬
			Map<String,Integer> map = new TreeMap<>(Comparator.reverseOrder()); //날짜의 역순으로 정렬. 최근날짜부터
			for(Map<String,Object> m : list) { 
				String day =(String)m.get("day");
				long cnt = (long)m.get("cnt"); 
				map.put(day,(int)cnt);
			}
			return map;
		}
}
