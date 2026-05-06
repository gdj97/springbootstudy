package kr.gdu.shop2.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.gdu.shop2.dao.mapper.BoardMapper;
import kr.gdu.shop2.dto.Board;

@Repository
public class BoardDao {
	@Autowired
	private SqlSessionTemplate template;
	private Class<BoardMapper> cls = BoardMapper.class;
	private Map<String,Object> param = new HashMap<>();
	
	public int maxNum() { //num 컬럼의 최대값 리턴
		return template.getMapper(cls).maxNum();
	}
	public void insert(Board board) {  //board 내용을 board 테이블에 저장
		template.getMapper(cls).insert(board);
	}
	public int count(String boardid,String searchtype, String searchcontent) {
		param.clear();
		param.put("boardid", boardid);
		if(searchtype != null) {
			String[] cols = searchtype.split(",");
			switch(cols.length) {
				case 2 : param.put("col2", cols[1]);
				case 1 : param.put("col1", cols[0]);
			}
		}
		param.put("searchcontent", searchcontent);
		return template.getMapper(cls).count(param);
	}
	public int count2(String boardid,String searchtype, String searchcontent) {
		param.clear();
		param.put("boardid", boardid);
		if(searchtype != null) {
			String[] cols = searchtype.split(",");
			param.put("cols", cols);
		}
		param.put("searchcontent", searchcontent);
		return template.getMapper(cls).count2(param);
	}
	public List<Board> list(Integer pageNum, int limit, String boardid,String searchtype, String searchcontent) {
		param.clear();
		param.put("startrow", (pageNum - 1) * limit); //10
		param.put("limit",  limit);		              //10
		param.put("boardid",  boardid);	
		if(searchtype != null) {
			String[] cols = searchtype.split(",");
			switch(cols.length) {
				case 2 : param.put("col2", cols[1]);
				case 1 : param.put("col1", cols[0]);
			}
		}
		param.put("searchcontent", searchcontent);
		return template.getMapper(cls).selectList(param);
	}
	public List<Board> list2 (Integer pageNum, int limit, String boardid,String searchtype, String searchcontent) {
		param.clear();
		param.put("startrow", (pageNum - 1) * limit); //10
		param.put("limit",  limit);		              //10
		param.put("boardid",  boardid);	
		if(searchtype != null) {
			String[] cols = searchtype.split(",");
			param.put("cols", cols);
		}
		param.put("searchcontent", searchcontent);
		return template.getMapper(cls).selectList2(param);
	}
	public Board selectOne(Integer num) {
		return template.getMapper(cls).selectOne(num);
	}
	public void addReadcnt(Integer num) {
		template.getMapper(cls).addReadcnt(num);
	}
	public void grpStepAdd(Board board) {
		template.getMapper(cls).grpStepAdd(board.getGrp(),board.getGrpstep());
	}
	public void update(Board board) {
		template.getMapper(cls).update(board);		
	}
	public void delete(int num) {
		template.getMapper(cls).delete(num);
	}
	public List<Map<String, Object>> graph1(String id) {
		return template.getMapper(cls).graph1(id);
	}
	public List<Map<String, Object>> graph2(String id) {
		return template.getMapper(cls).graph2(id);
	}
}
