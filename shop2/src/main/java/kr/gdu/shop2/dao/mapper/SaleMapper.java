package kr.gdu.shop2.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;

import kr.gdu.shop2.dto.Sale;
@Mapper
public interface SaleMapper {

	@Select("select ifnull(max(saleid),0) from sale")
	int maxid();

	@Insert("insert into sale (saleid, userid, saledate) "
		+	" values (#{saleid},#{userid}, now())")
	void insert(Sale sale);

	@Select("select * from sale where userid = #{value}")
	List<Sale> selectList(String userid);

	@Select("SELECT s.saleid, s.userid, s.saledate, " +
	        "si.seq, si.itemid, si.quantity, " +
	        "i.name, i.price " +
	        "FROM sale s " +
	        "LEFT OUTER JOIN saleitem si ON s.saleid = si.saleid " +
	        "LEFT OUTER JOIN item i ON si.itemid = i.id " +
	        "WHERE s.userid = #{userid}")
	@Results(id = "SaleMap", value = {
	    @Result(property = "saleid", column = "saleid", id = true),
	    @Result(property = "userid", column = "userid"),
	    @Result(property = "saledate", column = "saledate"),
	    // collection 매핑 (SaleItem 목록)
	    @Result(property = "itemList", column = "saleid", 
	            many = @Many(select = "getSaleItemsBySaleId")) 
	})
	List<Sale> selectListAll(String userid);

}
