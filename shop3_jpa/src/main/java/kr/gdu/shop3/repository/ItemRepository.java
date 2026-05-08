package kr.gdu.shop3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.gdu.shop3.entity.Item;
public interface ItemRepository extends JpaRepository<Item, Integer>{

	@Query("select coalesce(max(i.id),0) from Item i")
	public int maxId();
}
