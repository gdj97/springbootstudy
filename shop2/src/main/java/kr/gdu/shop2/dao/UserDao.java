package kr.gdu.shop2.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.gdu.shop2.dao.mapper.UserMapper;
import kr.gdu.shop2.dto.User;

@Repository
public class UserDao {
	@Autowired
	private SqlSessionTemplate template;
	private Map<String,Object> param = new HashMap<>();
	private Class<UserMapper> cls = UserMapper.class;
	
	public void insert(User user) {
		template.getMapper(cls).insert(user);
	}

	public User selectOne(String userid) {
		return template.getMapper(cls).selectOne(userid);
	}

	public void update(User user) {
		template.getMapper(cls).update(user);
	}

	public void delete(String userid) {
		template.getMapper(cls).delete(userid);
	}

	public void chgPass(String userid, String chgpass) {
		template.getMapper(cls).chgPass(userid,chgpass);
	}

	public String search(User user, String url) {
		String col = url.equals("pw")?"password":"userid";
		param.clear();
		param.put("col", col); //컬럼명
		param.put("userid", user.getUserid());
		param.put("email", user.getEmail());
		param.put("phoneno", user.getPhoneno());
		return template.getMapper(cls).search(param);
	}

	public List<User> list() {
		return template.getMapper(cls).selectList(null); //모든 사용자데이터 리턴
	}

	public List<User> list(String[] idchks) {
		param.clear();
		param.put("userids", idchks);
		return template.getMapper(cls).selectList(param); //idchks 값에 저장된 userid값만 목록으로 리턴
	}
}
