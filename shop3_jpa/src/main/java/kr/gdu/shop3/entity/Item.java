package kr.gdu.shop3.entity;


import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import kr.gdu.shop3.dto.ItemDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="item")
@Data
@NoArgsConstructor
public class Item {
	@Id   //기본키 설정 필수
	private int id;
	private String name;
	private int price;
	private String description;
	private String pictureUrl;
	@Transient   //컬럼이 아님. 컬럼과 상관없는 프로퍼티 
	private MultipartFile picture;
	public Item(ItemDto item) {
		this.id = item.getId();
		this.name = item.getName();
		this.price = item.getPrice();
		this.description = item.getDescription();
		this.pictureUrl = item.getPictureUrl();
	}
}
