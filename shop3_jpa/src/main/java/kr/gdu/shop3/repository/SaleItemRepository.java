package kr.gdu.shop3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.gdu.shop3.dto.SaleItemDto;
import kr.gdu.shop3.entity.SaleItem;
import kr.gdu.shop3.entity.SaleItemId;

public interface  SaleItemRepository extends JpaRepository<SaleItem, SaleItemId>{

	List<SaleItem> findBySaleid(int saleid);

}
