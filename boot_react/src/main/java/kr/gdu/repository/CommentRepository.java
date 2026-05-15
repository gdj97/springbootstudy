package kr.gdu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.gdu.entity.CommentEntity;
import kr.gdu.entity.CommentId;

public interface CommentRepository 
    extends JpaRepository<CommentEntity, CommentId>{
	@Query("select COALESCE(max(c.seq),0) "
			+ " from CommentEntity c where c.num = :num")
	public int maxseq(@Param("num") int num) ;

	public List<CommentEntity> findByNum(Integer num);
}
