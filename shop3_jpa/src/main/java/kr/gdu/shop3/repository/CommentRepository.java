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

	/*
	 * findByXxx 이름의 함수 : xxx 컬럼의 이름을 가지고 db에서 조회.
	 * findByNum(num) : Comment 테이블에서 num 조건으로 데이터 조회. List 객체로 리턴하도록
	 *                  JPA에서 sql 구문을 생성하여 실행해줌
	 */
	public List<Comment> findByNum(Integer num);
	
}
