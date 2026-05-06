package kr.gdu.shop2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor 
public class ItemSet {
	private Item item;
	private Integer quantity;
}
