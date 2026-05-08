package kr.gdu.shop3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.gdu.shop3.dto.SaleDto;
import kr.gdu.shop3.entity.Sale;

public interface  SaleRepository extends JpaRepository<Sale, Integer>{
	@Query("select coalesce(max(s.saleid),0) from Sale s")
	public int getMaxSaleId();

	/*
	 * findBy컬럼명 : 컬럼명값으로 조회하는 쿼리를 자동 생성
	 * findByUserid : userid 컬럼명값으로 조회 쿼리를 자동 생성함.
	 */
	public List<Sale> findByUserid(String userid);

}
