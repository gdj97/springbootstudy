package kr.gdu.shop3.service;
import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.gdu.shop3.dto.BoardDto;
import kr.gdu.shop3.dto.CommentDto;
import kr.gdu.shop3.entity.Board;
import kr.gdu.shop3.entity.Comment;
import kr.gdu.shop3.entity.CommentId;
import kr.gdu.shop3.repository.BoardRepository;
import kr.gdu.shop3.repository.CommentRepository;
@Service
public class BoardService {
	@Autowired
	BoardRepository dao;
	@Autowired
	CommentRepository commDao;
	
	@Value("${board.upload.dir}")
	private String BOARD_UPLOAD_DIR;
	
	//게시판 등록
	public void boardWrite(BoardDto board, HttpServletRequest request) {
		int maxnum = dao.maxNum();  //board 테이블의 최대 num 컬럼의 값을 리턴
		board.setNum(maxnum+1);
		board.setGrp(maxnum+1); //원글의 경우 grp 컬럼의 값은 num 컬럼의 값과 같음
		if(board.getFile1() != null && !board.getFile1().isEmpty()) { //업로드된 파일이 존재.
			uploadFileCreate(board.getFile1(),BOARD_UPLOAD_DIR + "file/");  //파일 업로드
			board.setFileurl(board.getFile1().getOriginalFilename()); //파일이름 설정
		}
		//save : 추가,변경. JPA 자체에서 제공되는 함수
		dao.save(new Board(board)); //board 테이블에 게시글 추가.
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
	//게시판 종류별 게시글 등록 건수 리턴 함수
	public int boardcount(String boardid, String searchtype, String searchcontent) {
		//조건문
		/*
		 * Specification : Functional interface
		 * 
		 * root : entity 객체. Board 객체
		 * query : 쿼리 구조. 
		 * cri : 조건문.CriteriaBuilder
		 *       boardid가 입력된 boardid 파라미터값과 같은 조건
		 */
		Specification<Board> spec = (root,query,cri) -> cri.equal(root.get("boardid"), boardid);

		if(searchtype != null) { //검색부분
		  if(searchtype.equals("title")) {
			spec = spec.and((root, query, cr)-> cr.like(root.get("title"),"%"+searchcontent+"%"));
		  } else if (searchtype.equals("writer")) {
			spec = spec.and((root, query, cr)-> cr.like(root.get("writer"),"%"+searchcontent+"%"));
		  } else if (searchtype.equals("content")) {
			spec = spec.and((root, query, cr)->	cr.like(root.get("content"),"%"+searchcontent+"%"));
		  }
		}
		//spec에 맞는 레코드의 건수 리턴.
		return (int)dao.count(spec); //리턴값이 long 임
	}
	public List<BoardDto> boardlist
	                (Integer pageNum, int limit, String boardid,String searchtype, String searchcontent) {
		Specification<Board> spec =	(root,query,cri) -> cri.equal(root.get("boardid"), boardid);
					
		if(searchtype != null) { //검색부분
		  if(searchtype.equals("title")) {
			spec = spec.and((root, query, cr)-> cr.like(root.get("title"),"%"+searchcontent+"%"));
		  } else if (searchtype.equals("writer")) {
			spec = spec.and((root, query, cr)-> cr.like(root.get("writer"),"%"+searchcontent+"%"));
		  } else if (searchtype.equals("content")) {
			spec = spec.and((root, query, cr)->	cr.like(root.get("content"),"%"+searchcontent+"%"));
		  }
		}
		//페이징 조건
		//PageRequest.of(페이지번호(0부터시작),조회되는 레코드건수,정렬방식...)
		Pageable pageable = PageRequest.of
				(pageNum-1, limit, Sort.by(Sort.Order.desc("grp"),Sort.Order.asc("grpstep")));
		//findAll : 모든레코드조회
		//Page<Board> findAll(where조건,조회레코드갯수) 
		return dao.findAll(spec, pageable)
	              .map(board -> new BoardDto(board)) // Board 엔티티를 BoardDto로 변환 (생성자 필요)
	              .getContent();
	}

	public BoardDto getBoard(Integer num) {
		/*
		 * findById : 키값으로 레코드 조회. => Optional<Board>로 리턴
		 *           orElseGet : 조회된 레코드 없으면 처리 기능 설정
		 *           ()->null : 조회레코드 없으면 null 리턴
		 */
		return new BoardDto(dao.findById(num).orElseGet(()->null)); 
	}
	public void addReadcnt(Integer num) {
		dao.addReadcnt(num); 
	}

	public void boardReply(BoardDto board) {
		dao.grpStepAdd(board.getGrp(),board.getGrpstep()); //grp 내의 기존의 원글보다 큰 값을 가진 grpstep의 값을 1 증가시킴. 
		//답글의 내용을 db에 등록
		int max = dao.maxNum(); //board 테이블에서 num값의 최대값
		board.setNum(max+1);    //최대값+1로 추가될 게시물의 num값을 설정
		board.setGrplevel(board.getGrplevel() + 1); //원글 + 1
		board.setGrpstep(board.getGrpstep() + 1);   //원글의 바로다음에 출력되도록 설정
		//원글의 grp, boardid 값은 그대로 유지
		dao.save(new Board(board));
	}

	public void boardUpdate(BoardDto board, HttpServletRequest request) {
		//첨부파일 업로드
		if(board.getFile1() != null && !board.getFile1().isEmpty()) { //첨부파일이 수정된경우. 
			uploadFileCreate(board.getFile1(), BOARD_UPLOAD_DIR+ "file/");  
			//board.getFileurl() : 수정전 첨부파일명 
			board.setFileurl(board.getFile1().getOriginalFilename()); //첨부된 파일이름 fileUrl 프로퍼티값 변경
		}
		//db 수정
		dao.save(new Board(board));		
	}

	public void boardDelete(int num) {
		//deleteById : 키에 해당하는 레코드 삭제
		dao.deleteById(num);
	}
	public int commmaxseq(int num) {
		return commDao.maxseq(num);
	}
	public void comminsert(CommentDto comm) {
		commDao.save(new Comment(comm));
	}
	public List<CommentDto> commentList(Integer num) {
		return commDao.findByNum(num).stream().map(comm->new CommentDto(comm)).toList();
	}

	public CommentDto getComment(int num, int seq) {
		//CommentId : Comment 테이블의 키값 클래스
		CommentId id = new CommentId(num,seq);
		//findById(키값) : 키값으로 레코드 한개만 리턴
		return new CommentDto(commDao.findById(id).orElseGet(()->null));
	}

	public void commentDel(int num, int seq) {
		CommentId id = new CommentId(num,seq);
		//deleteById : 키값에 해당하는 레코드 삭제
		commDao.deleteById(id);
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
