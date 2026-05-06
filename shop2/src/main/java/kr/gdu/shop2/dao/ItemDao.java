package kr.gdu.shop2.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.gdu.shop2.dao.mapper.ItemMapper;
import kr.gdu.shop2.dto.Item;
//Repository : 영속성 객체. DB와 연결된 객체. 
@Repository  //@Component + Repository 기능
public class ItemDao {
	@Autowired
	private SqlSessionTemplate template; //DBConfig 클래스에서 @Bean으로 객체화
	private Map<String,Object> param = new HashMap<>();
	private final Class<ItemMapper> cls = ItemMapper.class;
	
	public List<Item> list() {
		return template.getMapper(cls).selectList();
	}

	public Item selectOne(Integer id) {
		return template.getMapper(cls).selectOne(id);
	}

	public void delete(Integer id) {
		template.getMapper(cls).delete(id);
	}
	public int maxId() {
		return template.getMapper(cls).maxId();
	}
	public void insert(Item item) {
		template.getMapper(cls).insert(item);
	}

	public void update(Item item) {
		template.getMapper(cls).update(item);
	}
}
