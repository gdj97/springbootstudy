package kr.gdu.shop2.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.gdu.shop2.dao.mapper.CommentMapper;
import kr.gdu.shop2.dto.Comment;


@Repository
public class CommentDao {
	@Autowired
	private SqlSessionTemplate template;
	private Map<String,Object> param = new HashMap<>();
    private Class<CommentMapper> cls = CommentMapper.class;
    
    //num 컬럼명 최대 seq 컬럼의 값 리턴
	public int maxseq(int num) {
		return template.getMapper(cls).maxseq(num);
	}
	//댓글 데이터 등록.
	public void insert(Comment comm) {
		template.getMapper(cls).insert(comm);
	}
	public List<Comment> list(Integer num) {
		return template.getMapper(cls).list(num);
	}
	public Comment selectOne(int num, int seq) {
		return template.getMapper(cls).selectOne(num,seq);
	}
	public void delete(int num, int seq) {
		template.getMapper(cls).delete(num,seq);
	}
	
}
