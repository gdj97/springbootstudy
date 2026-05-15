package kr.gdu.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import kr.gdu.entity.BoardEntity;
import kr.gdu.entity.CommentEntity;
import kr.gdu.entity.CommentId;
import kr.gdu.repository.BoardRepository;
import kr.gdu.repository.CommentRepository;

@Service
public class BoardService {
	@Autowired
	BoardRepository repository;
	@Autowired
	CommentRepository commRepository;

	public int boardCount(String boardid) {
		//where 조건문 
		Specification<BoardEntity> spec =(root,query,cri) ->cri.equal(root.get("boardid"),boardid);
		return (int)repository.count(spec); //long count() : 레코드 건수 조회
	}

	public Page<BoardEntity> boardList(Integer pageInt, int limit, String boardid) {
		Specification<BoardEntity> spec =(root,query,cri) ->cri.equal(root.get("boardid"),boardid);
		//PageRequest.of(페이지번호(0부터시작),페이지당보여질레코드갯수,정렬방식)
		Pageable pageable = PageRequest.of(pageInt-1, limit,Sort.by(Sort.Order.desc("num")));		
		return repository.findAll(spec,pageable);
	}

	public BoardEntity insertBoard(BoardEntity boardEntity) {
		return repository.save(boardEntity); //저장된 entity 리턴
	}

	public BoardEntity getBoard(int num) {
		return repository.findById(num).orElseGet(null);
	}

	public void addReadcnt(int num) {
		repository.addReadCnt(num);
	}

	public void boardUpdate(BoardEntity boardEntity) {
		 repository.save(boardEntity);
    }

	public void boardDelete(Integer num) {
		repository.deleteById(num);		
	}

	public int commmaxseq(int num) {
		return commRepository.maxseq(num);
	}

	public CommentEntity commInsert(CommentEntity commentEntity) {
		return commRepository.save(commentEntity);
	}

	public List<CommentEntity> commentList(int num) {
		return commRepository.findByNum(num);
	}
	
	public CommentEntity getComment(int num, int seq) {
		CommentId id = new CommentId(num,seq);
		//findById(키값) :  key해당하는 엔티티를 객체 한개 리턴
		return commRepository.findById(id).orElseGet(()->null);
	}

	public void commentDel(int num, int seq) {
		CommentId id = new CommentId(num,seq);
		commRepository.deleteById(id);
	}	

}
