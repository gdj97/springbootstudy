package kr.gdu.shop2.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.gdu.shop2.dao.mapper.SaleItemMapper;
import kr.gdu.shop2.dto.SaleItem;


@Repository
public class SaleItemDao {
	@Autowired
	private SqlSessionTemplate template;
	private Class<SaleItemMapper> cls = SaleItemMapper.class;

	public void insert(SaleItem saleItem) {
		template.getMapper(cls).insert(saleItem);
	}

	public List<SaleItem> list(int saleid) {
		return template.getMapper(cls).selectList(saleid);
	}

}
