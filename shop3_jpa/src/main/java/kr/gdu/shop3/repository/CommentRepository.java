package kr.gdu.shop3.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.gdu.shop3.dto.CommentDto;
import kr.gdu.shop3.entity.Comment;
import kr.gdu.shop3.entity.CommentId;

//JpaRepository<엔티티의자료형,기본키의자료형>
public interface CommentRepository extends JpaRepository<Comment,CommentId>{
    
    //num 컬럼명 최대 seq 컬럼의 값 리턴
	@Query("select COALESCE(max(c.seq),0) from Comment c where c.num = :num")
	public int maxseq(@Param("num") int num) ;

	public List<CommentDto> findByNum(Integer num);
	
}
