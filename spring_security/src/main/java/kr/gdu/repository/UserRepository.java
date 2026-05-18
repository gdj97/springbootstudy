package kr.gdu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.gdu.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer>{
	UserEntity findByUsername(String username);
}
