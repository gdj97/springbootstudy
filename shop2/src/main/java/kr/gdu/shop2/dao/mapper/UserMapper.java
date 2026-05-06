package kr.gdu.shop2.dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import kr.gdu.shop2.dto.User;
@Mapper
public interface UserMapper {

	@Insert("insert into useraccount "
	+ "(userid, username,password,phoneno,postcode,address,email,birthday,channel)"
	+ " values(#{userid}, #{username},#{password},#{phoneno},#{postcode},#{address},"
	+ " #{email},#{birthday},#{channel})")
	void insert(User user);

	@Select("select * from useraccount where userid=#{value}")
	User selectOne(String userid);

	@Update("update useraccount set username=#{username},phoneno=#{phoneno},postcode=#{postcode},"
		  + " address=#{address},email=#{email},birthday=#{birthday} where userid=#{userid}")
	void update(User user);

	@Delete("delete from useraccount where userid=#{value}")
	void delete(String userid);

	@Update("update useraccount set password=#{chgpass} where userid=#{userid}")
	void chgPass(@Param("userid") String userid, @Param("chgpass") String chgpass);

	@Select({"<script>",
		     "select ${col} from useraccount where email=#{email} and phoneno=#{phoneno} ",
		     "<if test='userid != null'>and userid=#{userid}</if>",
			 "</script>"})
	String search(Map<String, Object> param);
//userids = [test1] 아이디 조회
//select * from useraccount where userid in ('test1')
	@Select({"<script>",
		  "select * from useraccount ",
		  "<if test='userids != null'> where userid in "
        + "<foreach collection='userids' item='id' separator=',' "
        + " open='(' close=')'>#{id}</foreach></if>",
		  "</script>"})
	List<User> selectList(Map<String, Object> param);
}
