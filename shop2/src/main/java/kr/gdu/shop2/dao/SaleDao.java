package kr.gdu.shop2.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.gdu.shop2.dao.mapper.SaleMapper;
import kr.gdu.shop2.dto.Sale;

@Repository
public class SaleDao {
	@Autowired
	private SqlSessionTemplate template;
	private Class<SaleMapper> cls = SaleMapper.class;

	public int getMaxSaleId() {
		return template.getMapper(cls).maxid();
	}

	public void insert(Sale sale) {
		template.getMapper(cls).insert(sale);
	}
	
	public List<Sale> list(String userid) {
		return template.getMapper(cls).selectList(userid);
	}

	public List<Sale> listAll(String userid) {
		return template.getMapper(cls).selectListAll(userid);
	}
}
