package kr.gdu.shop2.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import kr.gdu.shop2.dto.Item;
@Mapper
public interface ItemMapper {

	@Select("select * from item")
	List<Item> selectList();

	@Select("select * from item where id=#{value}")	
	Item selectOne(Integer id);

	@Delete("delete from item where id=#{value}")
	void delete(Integer id);
	
	@Select("select ifnull(max(id),0) from item")
	int maxId();

	@Insert("insert into item (id, name, price, description, pictureUrl)"
	 + " values (#{id},#{name},#{price},#{description},#{pictureUrl})")
	void insert(Item item);

	@Update("update item set name=#{name},price=#{price},description=#{description},"
			+ "pictureUrl=#{pictureUrl} where id=#{id}")
	void update(Item item);

}
