package kr.gdu.shop2.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import kr.gdu.shop2.dto.SaleItem;
@Mapper
public interface SaleItemMapper {

	@Insert("insert into saleitem (saleid,seq,itemid,quantity)"
		  + " values (#{saleid},#{seq},#{itemid},#{quantity})")
	void insert(SaleItem saleItem);
	
	
	@Select("select * from saleitem where saleid = #{value}")
	List<SaleItem> selectList(int saleid);

}
