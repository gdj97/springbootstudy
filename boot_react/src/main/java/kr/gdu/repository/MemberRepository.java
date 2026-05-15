package kr.gdu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.gdu.entity.CommentEntity;
import kr.gdu.entity.CommentId;
import kr.gdu.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, String>{

}
