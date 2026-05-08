package kr.gdu.shop3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.gdu.shop3.entity.User;

public interface UserRepository extends JpaRepository<User, String>{
	@Modifying
	@Query("update User u set u.password=:chgpass where u.userid = :userid")
	public void chgPass(@Param("userid") String userid, @Param("chgpass") String chgpass);
	
	@Query("select u.userid from User u where u.email=:email and u.phoneno = :phoneno")
	String searchByUserid(@Param("email") String email, @Param("phoneno") String phoneno );
	
	@Query("select u.password from User u where u.userid=:userid and u.email=:email and u.phoneno = :phoneno")
	String searchByPassword(@Param("userid") String userid,
			@Param("email") String email, @Param("phoneno") String phoneno );

	List<User> findByUseridIn(List<String> list);
}
