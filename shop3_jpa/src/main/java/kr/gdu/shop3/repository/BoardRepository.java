package kr.gdu.shop3.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import kr.gdu.shop3.entity.Board;
/*
 * JPQL(Java Persistence Query Language) : JPA에서 사용되는 쿼리
 *  - Board 가 테이블의 이름이 아니고, 엔티티 객체임. 대소문자 구분함
 *  - 엔티티 이름은 반드시 별명을 설정해야 함                           
 *  - Database의 종류에 상관없이 동일한 쿼리문을 작성 가능
 *  - 예약어 (select, from, ...)는 대소문자 구분 안함
 */
@Transactional
public interface BoardRepository extends JpaRepository<Board,Integer>,JpaSpecificationExecutor<Board> {

	@Query("select coalesce(MAX(b.num),0) from Board b")
	public int maxNum(); 
	
	@Modifying //db 수정
	@Query("Update Board b set b.readcnt = b.readcnt+1 where b.num = :num")
	public void addReadcnt(Integer num); 
	
	@Modifying
	@Query("Update Board b SET b.grpstep = b.grpstep + 1 where b.grp  = :grp AND b.grpstep > :grpstep")
	public void grpStepAdd(@Param("grp") int grp,@Param("grpstep") int grpstep);
	
	@Query("SELECT b.writer AS writer, COUNT(b) AS cnt "
			+ " FROM Board b WHERE b.boardid = :boardid "
			+ " GROUP BY b.writer ORDER BY cnt DESC")
	public List<Map<String, Object>> graph1(@Param("boardid") String id);
	
	//nativeQuery = true : JPQL이 아니고 원래 데이터베이스 sql로 사용
	@Query(value = "SELECT DATE_FORMAT(regdate, '%Y-%m-%d') AS day, COUNT(*) AS cnt "
            + "FROM board WHERE boardid = :boardid "
            + "GROUP BY DATE_FORMAT(regdate, '%Y-%m-%d') "
            + "ORDER BY day DESC LIMIT 7", nativeQuery = true)	
	public List<Map<String, Object>> graph2(@Param("boardid") String id);
}
